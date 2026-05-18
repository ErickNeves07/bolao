import { useEffect, useState, useCallback } from 'react'
import { useUser } from '../hooks/useUser'
import { getMatchGroups, getUserBets, saveBulkBets } from '../services/bolaoApi'
import type { GroupMatches, Bet, Match } from '../types'
import styles from './BetsPage.module.css'

const DEADLINE = new Date('2026-06-11T16:00:00-03:00')

export default function BetsPage() {
  const { currentUser, users, selectUser, addUser } = useUser()
  const [groups, setGroups] = useState<GroupMatches[]>([])
  const [bets, setBets] = useState<Record<number, { home: number; away: number }>>({})
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const [saved, setSaved] = useState(false)
  const [newName, setNewName] = useState('')
  const [activeGroup, setActiveGroup] = useState<string | null>(null)

  const isPastDeadline = new Date() > DEADLINE

  const loadData = useCallback(async (uid: number) => {
    setLoading(true)
    try {
      const [g, b] = await Promise.all([getMatchGroups(), getUserBets(uid)])
      setGroups(g)
      if (g.length > 0) setActiveGroup(g[0].groupName)
      const map: Record<number, { home: number; away: number }> = {}
      b.forEach(bet => { map[bet.matchId] = { home: bet.homeScoreBet, away: bet.awayScoreBet } })
      setBets(map)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
    if (currentUser) loadData(currentUser.id)
    else {
      getMatchGroups().then(g => { setGroups(g); if (g[0]) setActiveGroup(g[0].groupName) })
        .finally(() => setLoading(false))
    }
  }, [currentUser, loadData])

  const handleBet = (matchId: number, side: 'home' | 'away', val: string) => {
    const num = Math.max(0, parseInt(val) || 0)
    setBets(prev => ({
      ...prev,
      [matchId]: { ...(prev[matchId] || { home: 0, away: 0 }), [side]: num }
    }))
  }

  const handleSave = async () => {
    if (!currentUser) return
    setSaving(true)
    try {
      const payload = Object.entries(bets).map(([matchId, b]) => ({
        matchId: parseInt(matchId),
        homeScoreBet: b.home,
        awayScoreBet: b.away
      }))
      await saveBulkBets(currentUser.id, payload)
      setSaved(true)
      setTimeout(() => setSaved(false), 3000)
    } catch (e) {
      alert('Erro ao salvar apostas. Tente novamente.')
    } finally {
      setSaving(false)
    }
  }

  const handleLogin = async () => {
    if (!newName.trim()) return
    try { await addUser(newName.trim()) }
    catch { alert('Nome já cadastrado! Selecione abaixo.') }
  }

  // ---- Não logado ----
  if (!currentUser) {
    return (
      <div className={styles.loginWrap}>
        <div className={styles.loginCard}>
          <h2>🎯 Fazer Apostas</h2>
          <p>Selecione seu nome ou cadastre-se para apostar:</p>

          <div className={styles.userGrid}>
            {users.map(u => (
              <button key={u.id} className={styles.userBtn} onClick={() => selectUser(u)}>
                <span className={styles.userAv}>
                  {u.avatarUrl ? <img src={u.avatarUrl} alt="" /> : u.name.charAt(0).toUpperCase()}
                </span>
                <span>{u.name}</span>
              </button>
            ))}
          </div>

          <div className={styles.divider}><span>ou crie seu perfil</span></div>

          <div className={styles.newUser}>
            <input
              type="text"
              placeholder="Seu nome..."
              value={newName}
              onChange={e => setNewName(e.target.value)}
              onKeyDown={e => e.key === 'Enter' && handleLogin()}
              style={{ flex: 1 }}
            />
            <button className="btn btn-primary" onClick={handleLogin}>Entrar</button>
          </div>
        </div>
      </div>
    )
  }

  if (loading) return <div className={styles.loading}>Carregando...</div>

  const allMatches = groups.flatMap(g => g.matches)
  const scheduledMatches = allMatches.filter(m => m.status === 'SCHEDULED')
  const betCount = Object.keys(bets).length

  return (
    <div>
      <div className={styles.header}>
        <div>
          <h1>🎯 Suas Apostas</h1>
          <p className={styles.sub}>
            Olá, <strong>{currentUser.name}</strong>!
            {isPastDeadline
              ? ' ⛔ Prazo encerrado — apostas bloqueadas.'
              : ` Prazo: 11/06/2026 às 16:00`}
          </p>
        </div>
        <div className={styles.progress}>
          <span>{betCount} / {scheduledMatches.length}</span>
          <small>apostas</small>
        </div>
      </div>

      {/* Tabs grupos */}
      <div className={styles.tabs}>
        {groups.map(g => (
          <button
            key={g.groupName}
            className={`${styles.tab} ${activeGroup === g.groupName ? styles.active : ''}`}
            onClick={() => setActiveGroup(g.groupName)}
          >
            Grupo {g.groupName}
          </button>
        ))}
      </div>

      {/* Jogos */}
      {groups.filter(g => g.groupName === activeGroup).map(group => (
        <div key={group.groupName} className={styles.betGrid}>
          {group.matches.map(m => (
            <BetRow
              key={m.id}
              match={m}
              bet={bets[m.id] || { home: 0, away: 0 }}
              onChange={handleBet}
              disabled={isPastDeadline || m.status !== 'SCHEDULED'}
            />
          ))}
        </div>
      ))}

      {/* Salvar */}
      {!isPastDeadline && (
        <div className={styles.saveBar}>
          <button
            className="btn btn-primary"
            onClick={handleSave}
            disabled={saving}
            style={{ padding: '.7rem 2rem', fontSize: '1rem' }}
          >
            {saving ? '⏳ Salvando...' : saved ? '✅ Salvo!' : '💾 Salvar Apostas'}
          </button>
        </div>
      )}
    </div>
  )
}

// ----- BetRow -----
interface BetRowProps {
  match: Match
  bet: { home: number; away: number }
  onChange: (matchId: number, side: 'home' | 'away', val: string) => void
  disabled: boolean
}

function BetRow({ match: m, bet, onChange, disabled }: BetRowProps) {
  return (
    <div className={`${styles.betRow} card ${m.status !== 'SCHEDULED' ? styles.pastGame : ''}`}>
      <div className={styles.betTeam}>
        <img src={m.homeTeam.flagUrl} alt="" className={styles.betFlag} />
        <span>{m.homeTeam.name}</span>
      </div>

      <div className={styles.betInputs}>
        <input
          type="number" min={0} max={30}
          value={bet.home}
          onChange={e => onChange(m.id, 'home', e.target.value)}
          disabled={disabled}
        />
        <span className="vs">x</span>
        <input
          type="number" min={0} max={30}
          value={bet.away}
          onChange={e => onChange(m.id, 'away', e.target.value)}
          disabled={disabled}
        />
      </div>

      <div className={`${styles.betTeam} ${styles.right}`}>
        <img src={m.awayTeam.flagUrl} alt="" className={styles.betFlag} />
        <span>{m.awayTeam.name}</span>
      </div>

      {m.status !== 'SCHEDULED' && (
        <div className={styles.realScore}>
          Resultado: {m.homeScore ?? '–'} × {m.awayScore ?? '–'}
        </div>
      )}
    </div>
  )
}
