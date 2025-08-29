package com.example.backend.loan;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component
public class SnowflakeWriter {

    private static final Logger logger = LoggerFactory.getLogger(SnowflakeWriter.class);

    private final JdbcTemplate snowflakeJdbcTemplate;
    private final ObjectMapper objectMapper;

    public SnowflakeWriter(@Qualifier("snowflakeJdbcTemplate") JdbcTemplate snowflakeJdbcTemplate) {
        this.snowflakeJdbcTemplate = snowflakeJdbcTemplate;
        this.objectMapper = new ObjectMapper();
    }

    public void writeLoanApplication(LoanApplicationEntity loanApplication) {
        try {
            String sql = """
                    INSERT INTO BKRISK_DB.CORE.LOAN_APPLICATIONS (
                        APPLICATION_ID,
                        USER_ID,
                        REVOLVING_UTILIZATION_OF_UNSECURED_LINES,
                        AGE,
                        NUMBER_OF_TIME_30_59_DAYS_PAST_DUE_NOT_WORSE,
                        DEBT_RATIO,
                        MONTHLY_INCOME,
                        NUMBER_OF_OPEN_CREDIT_LINES_AND_LOANS,
                        NUMBER_OF_TIMES_90_DAYS_LATE,
                        NUMBER_REAL_ESTATE_LOANS_OR_LINES,
                        NUMBER_OF_TIME_60_89_DAYS_PAST_DUE_NOT_WORSE,
                        NUMBER_OF_DEPENDENTS,
                        RISK_SCORE,
                        DECISION,
                        SHAP_VALUES,
                        CREATED_AT
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """;

            Map<String, Object> payload = loanApplication.getPayload();

            snowflakeJdbcTemplate.update(sql,
                    loanApplication.getId().toString(),
                    loanApplication.getUserId() != null ? loanApplication.getUserId().toString() : null,
                    getDoubleValue(payload, "RevolvingUtilizationOfUnsecuredLines"),
                    getIntValue(payload, "age"),
                    getIntValue(payload, "NumberOfTime30_59DaysPastDueNotWorse"),
                    getDoubleValue(payload, "DebtRatio"),
                    getDoubleValue(payload, "MonthlyIncome"),
                    getIntValue(payload, "NumberOfOpenCreditLinesAndLoans"),
                    getIntValue(payload, "NumberOfTimes90DaysLate"),
                    getIntValue(payload, "NumberRealEstateLoansOrLines"),
                    getIntValue(payload, "NumberOfTime60_89DaysPastDueNotWorse"),
                    getIntValue(payload, "NumberOfDependents"),
                    loanApplication.getRiskScore(),
                    loanApplication.getDecision(),
                    objectMapper.writeValueAsString(loanApplication.getShapValues()),
                    loanApplication.getCreatedAt());

            logger.info("Successfully wrote loan application {} to Snowflake", loanApplication.getId());

        } catch (Exception e) {
            logger.error("Failed to write loan application {} to Snowflake: {}", loanApplication.getId(),
                    e.getMessage(), e);
            // Don't throw the exception to avoid breaking the main flow
            // In production, you might want to implement retry logic or dead letter queue
        }
    }

    private Double getDoubleValue(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null)
            return null;
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            logger.warn("Could not parse double value for key {}: {}", key, value);
            return null;
        }
    }

    private Integer getIntValue(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        if (value == null)
            return null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            logger.warn("Could not parse int value for key {}: {}", key, value);
            return null;
        }
    }
}
