package com.example.backend.loan;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class LoanService {

    private final WebClient mlClient;
    private final LoanApplicationRepository repo;

    public LoanService(WebClient mlWebClient, LoanApplicationRepository repo) {
        this.mlClient = mlWebClient;
        this.repo = repo;
    }

    public LoanApplicationEntity apply(LoanApplicationRequest r) {
        Map<String, Object> payload = toPayload(r);

        MlPredictResponse ml = mlClient.post()
                .uri("/predict")
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(MlPredictResponse.class)
                .onErrorResume(e -> Mono.error(new RuntimeException("ML call failed: " + e.getMessage(), e)))
                .block();

        LoanApplicationEntity e = new LoanApplicationEntity();
        e.setPayload(payload);
        e.setRiskScore(ml.risk_score);
        e.setDecision(ml.decision);
        e.setShapValues(ml.top_factors);
        return repo.save(e);
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
