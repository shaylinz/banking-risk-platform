export default function App() {
  return (
    <div style={{ padding: 24, fontFamily: 'system-ui' }}>
      <h1>Banking Platform (Skeleton)</h1>
      <p>Welcome! Use the links below to navigate.</p>
      <ul>
        <li><a href='/login'>Login</a></li>
        <li><a href='/dashboard'>Dashboard</a></li>
        <li><a href='/loan'>Loan Application</a></li>
      </ul>
      <p style={{opacity:.7}}>We will connect to the backend and ML APIs in later steps.</p>
    </div>
  )
}