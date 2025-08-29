package com.example.backend.loan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    
    private final JdbcTemplate snowflakeJdbcTemplate;

    public AnalyticsController(@Qualifier("snowflakeJdbcTemplate") JdbcTemplate snowflakeJdbcTemplate) {
        this.snowflakeJdbcTemplate = snowflakeJdbcTemplate;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            // Test basic connectivity
            Integer count = snowflakeJdbcTemplate.queryForObject("SELECT COUNT(*) FROM BKRISK_DB.CORE.LOAN_APPLICATIONS", Integer.class);
            response.put("status", "healthy");
            response.put("total_applications", count);
            response.put("message", "Snowflake connection successful");
        } catch (Exception e) {
            logger.error("Snowflake health check failed: {}", e.getMessage(), e);
            response.put("status", "unhealthy");
            response.put("error", e.getMessage());
        }
        return response;
    }

    @GetMapping("/risk-distribution")
    public List<Map<String, Object>> getRiskDistribution() {
        String sql = """
            SELECT 
                RISK_SCORE_BUCKET,
                APPLICATION_COUNT,
                APPROVED_COUNT,
                DENIED_COUNT,
                APPROVAL_RATE_PERCENT
            FROM BKRISK_DB.CORE.V_RISK_SCORE_DISTRIBUTION
            ORDER BY RISK_SCORE_BUCKET
            """;
        
        return snowflakeJdbcTemplate.queryForList(sql);
    }

    @GetMapping("/approval-rates")
    public List<Map<String, Object>> getApprovalRates() {
        String sql = """
            SELECT 
                APPLICATION_DATE,
                TOTAL_APPLICATIONS,
                APPROVED_APPLICATIONS,
                DENIED_APPLICATIONS,
                APPROVAL_RATE_PERCENT,
                AVG_RISK_SCORE
            FROM BKRISK_DB.CORE.V_DAILY_APPROVAL_RATES
            ORDER BY APPLICATION_DATE DESC
            LIMIT 30
            """;
        
        return snowflakeJdbcTemplate.queryForList(sql);
    }

    @GetMapping("/top-factors")
    public List<Map<String, Object>> getTopFactors() {
        String sql = """
            SELECT 
                FACTOR_NAME,
                AVG_IMPACT,
                APPLICATION_COUNT
            FROM BKRISK_DB.CORE.V_GLOBAL_RISK_FACTORS
            ORDER BY AVG_IMPACT DESC
            LIMIT 10
            """;
        
        return snowflakeJdbcTemplate.queryForList(sql);
    }

    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();
        
        try {
            // Total applications
            Integer totalApps = snowflakeJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM BKRISK_DB.CORE.V_LOAN_APPLICATIONS", Integer.class);
            summary.put("total_applications", totalApps);
            
            // Approval rate
            Double approvalRate = snowflakeJdbcTemplate.queryForObject(
                "SELECT ROUND(AVG(IS_APPROVED) * 100, 2) FROM BKRISK_DB.CORE.V_LOAN_APPLICATIONS", Double.class);
            summary.put("overall_approval_rate_percent", approvalRate);
            
            // Average risk score
            Double avgRiskScore = snowflakeJdbcTemplate.queryForObject(
                "SELECT ROUND(AVG(RISK_SCORE), 4) FROM BKRISK_DB.CORE.V_LOAN_APPLICATIONS", Double.class);
            summary.put("average_risk_score", avgRiskScore);
            
            // Recent activity (last 7 days)
            Integer recentApps = snowflakeJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM BKRISK_DB.CORE.V_LOAN_APPLICATIONS WHERE CREATED_AT >= DATEADD(day, -7, CURRENT_DATE())", Integer.class);
            summary.put("applications_last_7_days", recentApps);
            
            summary.put("status", "success");
            
        } catch (Exception e) {
            logger.error("Failed to get analytics summary: {}", e.getMessage(), e);
            summary.put("status", "error");
            summary.put("error", e.getMessage());
        }
        
        return summary;
    }
}
