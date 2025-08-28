package com.example.backend.loan;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/loans")
public class LoansController {

    private final LoanService loanService;
    private final LoanApplicationRepository repo;

    public LoansController(LoanService loanService, LoanApplicationRepository repo) {
        this.loanService = loanService;
        this.repo = repo;
    }

    @PostMapping("/apply")
    public Map<String, Object> apply(@Valid @RequestBody LoanApplicationRequest payload) {
        LoanApplicationEntity saved = loanService.apply(payload);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("applicationId", saved.getId());
        out.put("decision", saved.getDecision());
        out.put("risk_score", saved.getRiskScore());
        out.put("top_factors", saved.getShapValues());
        return out;
    }

    @GetMapping("/recent")
    public List<LoanApplicationEntity> recent() {
        return repo.findTop10ByOrderByCreatedAtDesc();
    }
}
