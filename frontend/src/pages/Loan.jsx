import { useState } from 'react'

export default function Loan() {
  const [mlStatus, setMlStatus] = useState(null)
  const [apiStatus, setApiStatus] = useState(null)

  const checkMl = async () => {
    try {
      const res = await fetch('/ml/health')
      const json = await res.json()
      setMlStatus(JSON.stringify(json))
    } catch (e) {
      setMlStatus('Error reaching ML service (is it running on port 8000?)')
    }
  }

  const checkApi = async () => {
    try {
      const res = await fetch('/api/health')
      const text = await res.text()
      setApiStatus(text)
    } catch (e) {
      setApiStatus('Error reaching backend API (will work after Step 3 when DB is set up and the app runs).')
    }
  }

  return (
    <div style={{ padding: 24 }}>
      <h2>Loan Application (skeleton)</h2>
      <p>Form fields and model scoring UI will be added in Step 6.</p>
      <div style={{ marginTop: 16 }}>
        <button onClick={checkMl}>Check ML /health</button>
        <span style={{ marginLeft: 12 }}>{mlStatus}</span>
      </div>
      <div style={{ marginTop: 12 }}>
        <button onClick={checkApi}>Check Backend /health</button>
        <span style={{ marginLeft: 12 }}>{apiStatus}</span>
      </div>
    </div>
  )
}
