import { useState, useEffect } from 'react'
import { getApiStatus, forceUpdate, updateAvatar } from '../services/bolaoApi'
import { useUser } from '../hooks/useUser'
import type { ApiStatus, ForceUpdateResponse } from '../types'
import styles from './AdminPage.module.css'

export default function AdminPage() {
  const { users, refresh } = useUser()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [loggedIn, setLoggedIn] = useState(false)
  const [error, setError] = useState('')

  const [apiStatus, setApiStatus] = useState<ApiStatus | null>(null)
  const [forceResult, setForceResult] = useState<ForceUpdateResponse | null>(null)
  const [loadingForce, setLoadingForce] = useState(false)

  const [avatarUserId, setAvatarUserId] = useState<number | null>(null)
  const [avatarUrl, setAvatarUrl] = useState('')

  const handleLogin = async () => {
    try {
      const status = await getApiStatus(username, password)
      setApiStatus(status)
      setLoggedIn(true)
      setError('')
    } catch {
      setError('Credenciais inválidas.')
    }
  }

  const handleForceUpdate = async () => {
    setLoadingForce(true)
    try {
      const result = await forceUpdate(username, password)
      setForceResult(result)
      const status = await getApiStatus(username, password)
      setApiStatus(status)
    } catch {
      alert('Erro ao forçar atualização.')
    } finally {
      setLoadingForce(false)
    }
  }

  const handleUpdateAvatar = async () => {
    if (!avatarUserId || !avatarUrl) return
    try {
      await updateAvatar(avatarUserId, avatarUrl, username, password)
      refresh()
      setAvatarUserId(null)
      setAvatarUrl('')
      alert('Avatar atualizado!')
    } catch {
      alert('Erro ao atualizar avatar.')
    }
  }

  if (!loggedIn) {
    return (
      <div className={styles.loginWrap}>
        <div className={styles.loginCard}>
          <h2>🔒 Painel Admin</h2>
          <p>Acesso restrito ao administrador do bolão.</p>

          {error && <div className={styles.error}>{error}</div>}

          <input type="text" placeholder="Usuário" value={username} onChange={e => setUsername(e.target.value)} style={{ marginBottom: '.5rem' }} />
          <input type="password" placeholder="Senha" value={password} onChange={e => setPassword(e.target.value)}
            onKeyDown={e => e.key === 'Enter' && handleLogin()} style={{ marginBottom: '1rem' }} />
          <button className="btn btn-primary" onClick={handleLogin} style={{ width: '100%' }}>
            Entrar
          </button>
        </div>
      </div>
    )
  }

  const usagePct = apiStatus ? (apiStatus.requestsToday / apiStatus.dailyLimit) * 100 : 0

  return (
    <div className={styles.wrap}>
      <div className={styles.header}>
        <h1>🔒 Painel Admin</h1>
        <button className="btn btn-ghost" style={{ fontSize: '.85rem' }} onClick={() => setLoggedIn(false)}>
          Sair
        </button>
      </div>

      <div className={styles.grid}>

        {/* API Status */}
        <div className="card">
          <h3>📡 Uso da API</h3>
          {apiStatus && <>
            <div className={styles.apiNumbers}>
              <div>
                <span>{apiStatus.requestsToday}</span>
                <small>usadas hoje</small>
              </div>
              <div>
                <span>{apiStatus.remaining}</span>
                <small>restantes</small>
              </div>
              <div>
                <span>{apiStatus.dailyLimit}</span>
                <small>limite diário</small>
              </div>
            </div>
            <div className={styles.progressBar}>
              <div style={{ width: `${Math.min(usagePct, 100)}%`, background: usagePct > 80 ? 'var(--red)' : 'var(--primary)' }} />
            </div>
            {apiStatus.lastRequestAt && (
              <p className={styles.lastReq}>
                Última requisição: {new Date(apiStatus.lastRequestAt).toLocaleTimeString('pt-BR')}
              </p>
            )}
          </>}
        </div>

        {/* Forçar atualização */}
        <div className="card">
          <h3>🔄 Forçar Atualização</h3>
          <p className={styles.desc}>
            Busca placares atualizados, recalcula pontuações e rebuilda o ranking imediatamente.
          </p>
          <button
            className="btn btn-primary"
            onClick={handleForceUpdate}
            disabled={loadingForce}
            style={{ marginTop: '.8rem', width: '100%' }}
          >
            {loadingForce ? '⏳ Atualizando...' : '🔄 Atualizar Agora'}
          </button>
          {forceResult && (
            <div className={styles.result}>
              ✅ {forceResult.matchesUpdated} jogos atualizados, {forceResult.betsRecalculated} apostas recalculadas
            </div>
          )}
        </div>

        {/* Avatar de usuários */}
        <div className="card">
          <h3>🖼️ Foto de Perfil</h3>
          <select
            value={avatarUserId ?? ''}
            onChange={e => setAvatarUserId(Number(e.target.value))}
            className={styles.select}
          >
            <option value="">Selecione usuário...</option>
            {users.map(u => <option key={u.id} value={u.id}>{u.name}</option>)}
          </select>
          <input
            type="text"
            placeholder="URL da imagem..."
            value={avatarUrl}
            onChange={e => setAvatarUrl(e.target.value)}
            style={{ marginTop: '.5rem', marginBottom: '.8rem' }}
          />
          {avatarUrl && (
            <img src={avatarUrl} alt="preview" className={styles.avatarPreview} />
          )}
          <button
            className="btn btn-primary"
            onClick={handleUpdateAvatar}
            disabled={!avatarUserId || !avatarUrl}
            style={{ width: '100%' }}
          >
            Salvar Foto
          </button>
        </div>

        {/* Lista de participantes */}
        <div className="card">
          <h3>👥 Participantes ({users.length})</h3>
          <div className={styles.userList}>
            {users.map(u => (
              <div key={u.id} className={styles.userItem}>
                <div className={styles.userAv}>
                  {u.avatarUrl ? <img src={u.avatarUrl} alt="" /> : u.name.charAt(0)}
                </div>
                <span>{u.name}</span>
                <span className={styles.betCount}>{u.totalBets} apostas</span>
              </div>
            ))}
          </div>
        </div>

      </div>
    </div>
  )
}
