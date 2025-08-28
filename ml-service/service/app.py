from fastapi import FastAPI
from pydantic import BaseModel, Field
import os, json
import numpy as np
import pandas as pd
import joblib
import shap

BASE = os.path.dirname(__file__)
ART  = os.path.join(BASE, '..', 'artifacts')

model   = joblib.load(os.path.join(ART, 'model.joblib'))
imputer = joblib.load(os.path.join(ART, 'imputer.joblib'))
with open(os.path.join(ART, 'features.json'), 'r') as f:
    FEATURES = json.load(f)

# SHAP explainer for XGBoost
explainer = shap.TreeExplainer(model)

app = FastAPI(title="Credit Risk API")

class LoanInput(BaseModel):
    RevolvingUtilizationOfUnsecuredLines: float = Field(..., ge=0)
    age: int = Field(..., ge=18)
    NumberOfTime30_59DaysPastDueNotWorse: int = Field(..., ge=0)
    DebtRatio: float = Field(..., ge=0)
    MonthlyIncome: float | None = None
    NumberOfOpenCreditLinesAndLoans: int = Field(..., ge=0)
    NumberOfTimes90DaysLate: int = Field(..., ge=0)
    NumberRealEstateLoansOrLines: int = Field(..., ge=0)
    NumberOfTime60_89DaysPastDueNotWorse: int = Field(..., ge=0)
    NumberOfDependents: int | None = None

    def to_dataframe(self) -> pd.DataFrame:
        row = {
            'RevolvingUtilizationOfUnsecuredLines': self.RevolvingUtilizationOfUnsecuredLines,
            'age': self.age,
            'NumberOfTime30-59DaysPastDueNotWorse': self.NumberOfTime30_59DaysPastDueNotWorse,
            'DebtRatio': self.DebtRatio,
            'MonthlyIncome': self.MonthlyIncome,
            'NumberOfOpenCreditLinesAndLoans': self.NumberOfOpenCreditLinesAndLoans,
            'NumberOfTimes90DaysLate': self.NumberOfTimes90DaysLate,
            'NumberRealEstateLoansOrLines': self.NumberRealEstateLoansOrLines,
            'NumberOfTime60-89DaysPastDueNotWorse': self.NumberOfTime60_89DaysPastDueNotWorse,
            'NumberOfDependents': self.NumberOfDependents,
        }
        df = pd.DataFrame([row])
        for c in FEATURES:
            if c not in df.columns:
                df[c] = 0
        return df[FEATURES]

@app.get("/health")
def health():
    return {"status": "ok"}

@app.post("/predict")
def predict(payload: LoanInput):
    X = payload.to_dataframe()
    X_imp = imputer.transform(X)

    # Probability of default (class 1)
    proba = float(model.predict_proba(X_imp)[:, 1][0])

    # SHAP values (handle list/array shapes)
    X_imp_df = pd.DataFrame(X_imp, columns=FEATURES)
    shap_vals = explainer.shap_values(X_imp_df)
    if isinstance(shap_vals, list):
        shap_vals = shap_vals[0]
    vals = shap_vals[0]
    top_idx = np.argsort(np.abs(vals))[::-1][:3]
    top_factors = [(FEATURES[i], float(vals[i])) for i in top_idx]

    decision = "DENY" if proba > 0.35 else "APPROVE"
    return {"risk_score": proba, "decision": decision, "top_factors": top_factors}
