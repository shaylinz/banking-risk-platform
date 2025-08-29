package com.example.backend.loan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class LoanService {

    private static final Logger logger = LoggerFactory.getLogger(LoanService.class);

    private final WebClient mlClient;
    private final LoanApplicationRepository repo;
    private final SnowflakeWriter snowflakeWriter;

    public LoanService(WebClient mlWebClient, LoanApplicationRepository repo, SnowflakeWriter snowflakeWriter) {
        this.mlClient = mlWebClient;
        this.repo = repo;
        this.snowflakeWriter = snowflakeWriter;
    }

    public LoanApplicationEntity apply(LoanApplicationRequest r) {
        Map<String, Object> payload = toPayload(r);
        logger.debug("Sending payload to ML service: {}", payload);

        try {
            MlPredictResponse ml = mlClient.post()
                    .uri("/predict")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(MlPredictResponse.class)
                    .onErrorResume(WebClientResponseException.class, e -> {
                        logger.error("ML service returned error: {} - {}", e.getStatusCode(),
                                e.getResponseBodyAsString());
                        return Mono.error(new RuntimeException(
                                "ML service error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString()));
                    })
                    .onErrorResume(Exception.class, e -> {
                        logger.error("Unexpected error calling ML service: {}", e.getMessage(), e);
                        return Mono.error(new RuntimeException("Failed to call ML service: " + e.getMessage()));
                    })
                    .block();

            if (ml == null) {
                throw new RuntimeException("ML service returned null response");
            }

            logger.info("ML service response: decision={}, risk_score={}", ml.decision, ml.risk_score);

            LoanApplicationEntity e = new LoanApplicationEntity();
            e.setPayload(payload);
            e.setRiskScore(ml.risk_score);
            e.setDecision(ml.decision);
            e.setShapValues(ml.top_factors);

            // Save to Postgres
            LoanApplicationEntity saved = repo.save(e);

            // Write to Snowflake
            snowflakeWriter.writeLoanApplication(saved);

            return saved;
        } catch (Exception e) {
            logger.error("Error in loan application processing: {}", e.getMessage(), e);
            throw e;
        }
    }

    private Map<String, Object> toPayload(LoanApplicationRequest r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("RevolvingUtilizationOfUnsecuredLines", r.RevolvingUtilizationOfUnsecuredLines());
        m.put("age", r.age());
        m.put("NumberOfTime30_59DaysPastDueNotWorse", r.NumberOfTime30_59DaysPastDueNotWorse());
        m.put("DebtRatio", r.DebtRatio());
        m.put("MonthlyIncome", r.MonthlyIncome());
        m.put("NumberOfOpenCreditLinesAndLoans", r.NumberOfOpenCreditLinesAndLoans());
        m.put("NumberOfTimes90DaysLate", r.NumberOfTimes90DaysLate());
        m.put("NumberRealEstateLoansOrLines", r.NumberRealEstateLoansOrLines());
        m.put("NumberOfTime60_89DaysPastDueNotWorse", r.NumberOfTime60_89DaysPastDueNotWorse());
        m.put("NumberOfDependents", r.NumberOfDependents());
        return m;
    }
}
