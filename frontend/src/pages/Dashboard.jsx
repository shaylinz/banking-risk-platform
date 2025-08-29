import { useEffect, useState } from "react";
import "tailwindcss";

export default function Dashboard() {
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const fetchRecentLoans = async () => {
    try {
      setLoading(true);
      setError("");
      const r = await fetch("/api/loans/recent");
      if (!r.ok) throw new Error(`HTTP ${r.status}: ${await r.text()}`);
      const data = await r.json();
      setRows(data);
    } catch (e) {
      console.error("Failed to fetch recent loans:", e);
      setError("Failed to load recent applications");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRecentLoans();
  }, []);

  // Listen for storage events to refresh when a new loan is submitted
  useEffect(() => {
    const handleStorageChange = (e) => {
      if (e.key === 'loanSubmitted') {
        fetchRecentLoans();
      }
    };

    window.addEventListener('storage', handleStorageChange);
    return () => window.removeEventListener('storage', handleStorageChange);
  }, []);

  const formatTopFactors = (topFactors) => {
    if (!topFactors || !Array.isArray(topFactors)) return [];
    
    return topFactors.map(factor => {
      if (Array.isArray(factor) && factor.length >= 2) {
        const [feature, value] = factor;
        return {
          feature: String(feature),
          value: Number(value).toFixed(4),
          impact: Number(value) > 0 ? 'positive' : 'negative'
        };
      }
      return null;
    }).filter(Boolean);
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

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-4xl font-bold tracking-tight">Dashboard</h1>
        <a
          href="/loan"
          className="inline-flex items-center rounded-lg bg-indigo-600 px-4 py-2 text-white hover:bg-indigo-700"
        >
          New Loan Application
        </a>
      </div>

      {error && (
        <div className="mb-6 p-4 border border-red-300 bg-red-50 rounded text-red-800">
          {error}
        </div>
      )}

      <div className="rounded-xl border bg-white shadow-sm">
        <div className="border-b px-5 py-3 font-medium flex items-center justify-between">
          <span>Recent Loan Decisions</span>
          <button 
            onClick={fetchRecentLoans}
            disabled={loading}
            className="text-sm text-indigo-600 hover:text-indigo-800 disabled:opacity-50"
          >
            {loading ? "Loading..." : "Refresh"}
          </button>
        </div>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 text-slate-600">
              <tr>
                <th className="px-5 py-3">Created</th>
                <th className="px-5 py-3">Decision</th>
                <th className="px-5 py-3">Risk Score</th>
                <th className="px-5 py-3">Top Factors</th>
                <th className="px-5 py-3">Application ID</th>
              </tr>
            </thead>
            <tbody>
              {loading && (
                <tr>
                  <td className="px-5 py-8 text-slate-500" colSpan={5}>
                    Loading recent applications...
                  </td>
                </tr>
              )}
              {!loading && rows.map((r) => (
                <tr key={r.applicationId} className="border-t hover:bg-slate-50">
                  <td className="px-5 py-3">
                    {new Date(r.created_at).toLocaleString()}
                  </td>
                  <td className="px-5 py-3">
                    <span
                      className={
                        r.decision === "APPROVE"
                          ? "rounded bg-green-100 px-2 py-1 text-green-700 font-medium"
                          : "rounded bg-red-100 px-2 py-1 text-red-700 font-medium"
                      }
                    >
                      {r.decision}
                    </span>
                  </td>
                  <td className="px-5 py-3">
                    <span className="font-mono">
                      {Number(r.risk_score).toFixed(4)}
                    </span>
                  </td>
                  <td className="px-5 py-3">
                    <div className="space-y-1">
                      {formatTopFactors(r.top_factors).slice(0, 3).map((factor, idx) => (
                        <div key={idx} className="text-xs">
                          <span className={`font-medium ${
                            factor.impact === 'positive' ? 'text-green-600' : 'text-red-600'
                          }`}>
                            {getFeatureDisplayName(factor.feature)}: {factor.value}
                          </span>
                        </div>
                      ))}
                    </div>
                  </td>
                  <td className="px-5 py-3 font-mono text-xs text-slate-600">
                    {r.applicationId}
                  </td>
                </tr>
              ))}
              {!loading && rows.length === 0 && (
                <tr>
                  <td className="px-5 py-8 text-slate-500 text-center" colSpan={5}>
                    No applications yet. 
                    <br />
                    <a href="/loan" className="text-indigo-600 hover:text-indigo-800">
                      Submit your first loan application →
                    </a>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
