package com.example.backend.loan;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/loans")
public class LoansController {

    private static final Logger logger = LoggerFactory.getLogger(LoansController.class);
    
    private final LoanService loanService;
    private final LoanApplicationRepository repo;

    public LoansController(LoanService loanService, LoanApplicationRepository repo) {
        this.loanService = loanService;
        this.repo = repo;
    }

    @PostMapping("/apply")
    public ResponseEntity<?> apply(@Valid @RequestBody LoanApplicationRequest payload) {
        try {
            logger.info("Processing loan application: {}", payload);
            LoanApplicationEntity saved = loanService.apply(payload);

            Map<String, Object> out = new LinkedHashMap<>();
            out.put("applicationId", saved.getId());
            out.put("decision", saved.getDecision());
            out.put("risk_score", saved.getRiskScore());
            out.put("top_factors", saved.getShapValues());
            
            logger.info("Loan application processed successfully. ID: {}, Decision: {}", saved.getId(), saved.getDecision());
            return ResponseEntity.ok(out);
        } catch (Exception e) {
            logger.error("Error processing loan application: {}", e.getMessage(), e);
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Failed to process loan application: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/recent")
    public ResponseEntity<?> recent() {
        try {
            List<LoanApplicationEntity> recent = repo.findTop10ByOrderByCreatedAtDesc();
            
            // Transform to consistent response format
            List<Map<String, Object>> response = recent.stream()
                .map(entity -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("applicationId", entity.getId());
                    item.put("decision", entity.getDecision());
                    item.put("risk_score", entity.getRiskScore());
                    item.put("top_factors", entity.getShapValues());
                    item.put("created_at", entity.getCreatedAt());
                    return item;
                })
                .toList();
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error fetching recent applications: {}", e.getMessage(), e);
            Map<String, String> error = new LinkedHashMap<>();
            error.put("error", "Failed to fetch recent applications: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
