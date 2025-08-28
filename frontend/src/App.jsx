import { Routes, Route, Link, NavLink } from "react-router-dom";
import Dashboard from "./pages/Dashboard";
import Loan from "./pages/Loan";

export default function App() {
  return (
    <div className="min-h-screen">
      {/* Top nav */}
      <header className="border-b bg-white">
        <div className="mx-auto max-w-6xl px-4 py-3 flex items-center justify-between">
          <Link to="/dashboard" className="text-xl font-semibold tracking-tight">
            Banking Platform
          </Link>
          <nav className="flex gap-6 text-sm">
            <NavLink
              to="/dashboard"
              className={({ isActive }) =>
                `hover:text-indigo-700 ${isActive ? "text-indigo-700 font-medium" : "text-slate-600"}`
              }
            >
              Dashboard
            </NavLink>
            <NavLink
              to="/loan"
              className={({ isActive }) =>
                `hover:text-indigo-700 ${isActive ? "text-indigo-700 font-medium" : "text-slate-600"}`
              }
            >
              Loan Application
            </NavLink>
          </nav>
        </div>
      </header>

      {/* Page content */}
      <main className="mx-auto max-w-6xl px-4 py-8">
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/loan" element={<Loan />} />
        </Routes>
      </main>
    </div>
  );
}
