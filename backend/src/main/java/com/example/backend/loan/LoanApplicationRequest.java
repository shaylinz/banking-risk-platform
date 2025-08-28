package com.example.backend.loan;

import jakarta.validation.constraints.*;

// Field names match your ML model exactly
public record LoanApplicationRequest(
        @PositiveOrZero double RevolvingUtilizationOfUnsecuredLines,
        @Min(18) int age,
        @PositiveOrZero int NumberOfTime30_59DaysPastDueNotWorse,
        @PositiveOrZero double DebtRatio,
        @PositiveOrZero double MonthlyIncome,
        @PositiveOrZero int NumberOfOpenCreditLinesAndLoans,
        @PositiveOrZero int NumberOfTimes90DaysLate,
        @PositiveOrZero int NumberRealEstateLoansOrLines,
        @PositiveOrZero int NumberOfTime60_89DaysPastDueNotWorse,
        @PositiveOrZero int NumberOfDependents) {
}
