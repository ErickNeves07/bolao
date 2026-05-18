import { Link, useLocation } from 'react-router-dom'
import { useUser } from '../hooks/useUser'
import styles from './Navbar.module.css'

export default function Navbar() {
  const { currentUser, logout } = useUser()
  const location = useLocation()

  const links = [
    { to: '/',        label: '⚽ Jogos' },
    { to: '/apostar', label: '🎯 Apostar' },
    { to: '/ranking', label: '🏆 Ranking' },
  ]

  return (
    <nav className={styles.nav}>
      <div className={styles.inner}>
        <Link to="/" className={styles.brand}>
          🌍 <span>Bolão 2026</span>
        </Link>

        <div className={styles.links}>
          {links.map(l => (
            <Link
              key={l.to}
              to={l.to}
              className={`${styles.link} ${location.pathname === l.to ? styles.active : ''}`}
            >
              {l.label}
            </Link>
          ))}
        </div>

        <div className={styles.user}>
          {currentUser ? (
            <>
              <span className={styles.avatar}>
                {currentUser.avatarUrl
                  ? <img src={currentUser.avatarUrl} alt="" />
                  : currentUser.name.charAt(0).toUpperCase()}
              </span>
              <span className={styles.name}>{currentUser.name}</span>
              <button className="btn btn-ghost" style={{ fontSize: '.8rem', padding: '.3rem .7rem' }} onClick={logout}>
                Sair
              </button>
            </>
          ) : (
            <Link to="/apostar" className="btn btn-primary" style={{ fontSize: '.85rem' }}>
              Entrar
            </Link>
          )}
          <Link to="/admin" className={styles.lock} title="Admin">🔒</Link>
        </div>
      </div>
    </nav>
  )
}
