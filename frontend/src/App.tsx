import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import HomePage from './pages/HomePage'
import BetsPage from './pages/BetsPage'
import RankingPage from './pages/RankingPage'
import AdminPage from './pages/AdminPage'
import { UserProvider } from './hooks/useUser'

export default function App() {
  return (
    <UserProvider>
      <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column' }}>
        <Navbar />
        <main style={{ flex: 1, maxWidth: 1100, width: '100%', margin: '0 auto', padding: '1.5rem 1rem' }}>
          <Routes>
            <Route path="/"        element={<HomePage />} />
            <Route path="/apostar" element={<BetsPage />} />
            <Route path="/ranking" element={<RankingPage />} />
            <Route path="/admin"   element={<AdminPage />} />
          </Routes>
        </main>
        <footer style={{ textAlign: 'center', padding: '.8rem', color: 'var(--text-muted)', fontSize: '.8rem' }}>
          🏆 Bolão Copa do Mundo 2026
        </footer>
      </div>
    </UserProvider>
  )
}
