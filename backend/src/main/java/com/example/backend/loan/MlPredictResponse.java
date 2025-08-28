package com.example.backend.loan;

import java.util.List;

public class MlPredictResponse {
    public double risk_score;
    public String decision;
    // ML returns [["feature", value], ...]
    public List<List<Object>> top_factors;
}
