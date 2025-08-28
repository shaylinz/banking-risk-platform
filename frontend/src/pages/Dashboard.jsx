import { useEffect, useState } from "react";
import "tailwindcss";

export default function Dashboard() {
  const [rows, setRows] = useState([]);

  useEffect(() => {
    (async () => {
      try {
        const r = await fetch("/api/loans/recent");
        if (!r.ok) throw new Error(`HTTP ${r.status}`);
        setRows(await r.json());
      } catch (e) {
        console.error(e);
      }
    })();
  }, []);

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

      <div className="rounded-xl border bg-white shadow-sm">
        <div className="border-b px-5 py-3 font-medium">Recent Loan Decisions</div>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 text-slate-600">
              <tr>
                <th className="px-5 py-3">Created</th>
                <th className="px-5 py-3">Decision</th>
                <th className="px-5 py-3">Risk Score</th>
                <th className="px-5 py-3">Application ID</th>
              </tr>
            </thead>
            <tbody>
              {rows.map((r) => (
                <tr key={r.applicationId} className="border-t">
                  <td className="px-5 py-3">{new Date(r.createdAt).toLocaleString()}</td>
                  <td className="px-5 py-3">
                    <span
                      className={
                        r.decision === "APPROVE"
                          ? "rounded bg-green-100 px-2 py-1 text-green-700"
                          : "rounded bg-red-100 px-2 py-1 text-red-700"
                      }
                    >
                      {r.decision}
                    </span>
                  </td>
                  <td className="px-5 py-3">{Number(r.riskScore ?? r.risk_score).toFixed(4)}</td>
                  <td className="px-5 py-3 font-mono text-xs text-slate-600">{r.applicationId}</td>
                </tr>
              ))}
              {rows.length === 0 && (
                <tr>
                  <td className="px-5 py-8 text-slate-500" colSpan={4}>
                    No applications yet.
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
