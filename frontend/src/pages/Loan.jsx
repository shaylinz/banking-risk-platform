import React, { useState } from "react";
import "tailwindcss";

const initialForm = {
  RevolvingUtilizationOfUnsecuredLines: 0.45,
  age: 35,
  NumberOfTime30_59DaysPastDueNotWorse: 0,
  DebtRatio: 0.25,
  MonthlyIncome: 5000,
  NumberOfOpenCreditLinesAndLoans: 6,
  NumberOfTimes90DaysLate: 0,
  NumberRealEstateLoansOrLines: 1,
  NumberOfTime60_89DaysPastDueNotWorse: 0,
  NumberOfDependents: 1,
};

export default function Loan() {
  const [form, setForm] = useState(initialForm);
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [error, setError] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((f) => ({ ...f, [name]: value === "" ? "" : Number(value) }));
  };

  const score = async () => {
    setLoading(true);
    setError("");
    setResult(null);
    try {
      const res = await fetch("/api/loans/apply", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(form),
      });
      if (!res.ok) throw new Error(`HTTP ${res.status}: ${await res.text()}`);
      const result = await res.json();
      setResult(result);
      
      // Trigger dashboard refresh by setting a storage event
      localStorage.setItem('loanSubmitted', Date.now().toString());
      
      // Also trigger the event for the current window
      window.dispatchEvent(new StorageEvent('storage', {
        key: 'loanSubmitted',
        newValue: Date.now().toString()
      }));
      
    } catch (e) {
      setError(String(e));
    } finally {
      setLoading(false);
    }
  };

  const checkBackend = async () => {
    try {
      const r = await fetch("/api/health");
      alert(await r.text());
    } catch {
      alert("Backend not reachable");
    }
  };
  const checkML = async () => {
    try {
      const r = await fetch("/ml/health");
      alert(await r.text());
    } catch {
      alert("ML not reachable");
    }
  };

  const getFeatureDisplayName = (feature) => {
    const featureMap = {
      'RevolvingUtilizationOfUnsecuredLines': 'Revolving Utilization',
      'NumberOfTime30_59DaysPastDueNotWorse': '30-59 Days Past Due',
      'NumberOfTime60_89DaysPastDueNotWorse': '60-89 Days Past Due',
      'NumberOfTimes90DaysLate': '90+ Days Late',
      'NumberOfOpenCreditLinesAndLoans': 'Open Credit Lines',
      'NumberRealEstateLoansOrLines': 'Real Estate Loans',
      'NumberOfDependents': 'Dependents',
      'age': 'Age',
      'DebtRatio': 'Debt Ratio',
      'MonthlyIncome': 'Monthly Income'
    };
    return featureMap[feature] || feature;
  };

  const Field = ({ name, label, step = "any", min, max }) => (
    <div>
      <label className="block text-sm font-medium text-slate-600 mb-1">{label}</label>
      <input
        type="number"
        step={step}
        min={min}
        max={max}
        name={name}
        value={form[name]}
        onChange={handleChange}
        className="w-full rounded-lg border-slate-300 focus:border-indigo-500 focus:ring-indigo-500"
      />
    </div>
  );

  return (
    <div>
      <h1 className="text-4xl font-bold tracking-tight mb-6">Loan Application</h1>

      <div className="flex gap-3 mb-6">
        <button onClick={checkML} className="px-4 py-2 rounded-lg border">
          Check ML /health
        </button>
        <button onClick={checkBackend} className="px-4 py-2 rounded-lg border">
          Check Backend /health
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-5 bg-white border rounded-xl p-5 shadow-sm">
        <Field name="RevolvingUtilizationOfUnsecuredLines" label="Revolving Utilization of Unsecured Lines" step="0.0001" min="0" />
        <Field name="age" label="Age" step="1" min="18" />
        <Field name="NumberOfTime30_59DaysPastDueNotWorse" label="Times 30–59 Days Past Due" step="1" min="0" />
        <Field name="DebtRatio" label="Debt Ratio" step="0.0001" min="0" />
        <Field name="MonthlyIncome" label="Monthly Income" step="1" min="0" />
        <Field name="NumberOfOpenCreditLinesAndLoans" label="Open Credit Lines & Loans" step="1" min="0" />
        <Field name="NumberOfTimes90DaysLate" label="Times 90+ Days Late" step="1" min="0" />
        <Field name="NumberRealEstateLoansOrLines" label="Real Estate Loans/Lines" step="1" min="0" />
        <Field name="NumberOfTime60_89DaysPastDueNotWorse" label="Times 60–89 Days Past Due" step="1" min="0" />
        <Field name="NumberOfDependents" label="Dependents" step="1" min="0" />
      </div>

      <div className="mt-5 flex gap-3">
        <button
          onClick={score}
          disabled={loading}
          className="px-5 py-2 rounded-lg bg-indigo-600 text-white hover:bg-indigo-700 disabled:opacity-50"
        >
          {loading ? "Scoring..." : "Score Application"}
        </button>
        <button
          onClick={() => {
            setForm(initialForm);
            setResult(null);
            setError("");
          }}
          className="px-4 py-2 rounded-lg border"
        >
          Reset
        </button>
      </div>

      {error && <div className="mt-6 p-4 border border-red-300 bg-red-50 rounded text-red-800">{error}</div>}

      {result && (
        <div className="mt-6 p-5 rounded-xl border bg-white shadow-sm">
          <div className="text-sm text-slate-600 mb-2">
            Application ID: <code className="font-mono">{result.applicationId}</code>
          </div>
          <div className="text-xl font-semibold">
            Decision:{" "}
            <span className={result.decision === "APPROVE" ? "text-green-700" : "text-red-700"}>{result.decision}</span>
          </div>
          <div className="mt-1">
            Risk Score: <strong>{Number(result.risk_score).toFixed(4)}</strong>
          </div>

          {Array.isArray(result.top_factors) && result.top_factors.length > 0 && (
            <div className="mt-3">
              <div className="font-medium mb-2">Top Factors</div>
              <div className="space-y-1">
                {result.top_factors.map((factor, i) => {
                  if (Array.isArray(factor) && factor.length >= 2) {
                    const [feature, value] = factor;
                    const impact = Number(value) > 0 ? 'positive' : 'negative';
                    const displayName = getFeatureDisplayName(feature);
                    return (
                      <div key={i} className="flex items-center justify-between text-sm">
                        <span className="text-slate-600">{displayName}</span>
                        <span className={`font-mono font-medium ${
                          impact === 'positive' ? 'text-green-600' : 'text-red-600'
                        }`}>
                          {Number(value).toFixed(4)}
                        </span>
                      </div>
                    );
                  }
                  return (
                    <div key={i} className="text-sm text-slate-600">
                      {String(factor)}
                    </div>
                  );
                })}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
