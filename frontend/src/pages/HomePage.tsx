import { useEffect, useState } from 'react'
import { getMatchGroups } from '../services/bolaoApi'
import { getUserBets } from '../services/bolaoApi'
import type { GroupMatches, Bet } from '../types'
import MatchCard from '../components/MatchCard'
import { useUser } from '../hooks/useUser'
import styles from './HomePage.module.css'

export default function HomePage() {
  const { currentUser } = useUser()
  const [groups, setGroups] = useState<GroupMatches[]>([])
  const [bets, setBets] = useState<Bet[]>([])
  const [loading, setLoading] = useState(true)
  const [activeGroup, setActiveGroup] = useState<string | null>(null)

  useEffect(() => {
    const load = async () => {
      setLoading(true)
      try {
        const g = await getMatchGroups()
        setGroups(g)
        if (g.length > 0) setActiveGroup(g[0].groupName)

        if (currentUser) {
          const b = await getUserBets(currentUser.id)
          setBets(b)
        }
      } finally {
        setLoading(false)
      }
    }
    load()

    // Polling: refresh a cada 30s se há jogos ao vivo
    const interval = setInterval(load, 30_000)
    return () => clearInterval(interval)
  }, [currentUser])

  const betMap = Object.fromEntries(
    bets.map(b => [b.matchId, { home: b.homeScoreBet, away: b.awayScoreBet }])
  )

  if (loading) return <div className={styles.loading}>Carregando jogos...</div>

  const liveMatches = groups.flatMap(g => g.matches).filter(m => m.status === 'LIVE')

  return (
    <div>
      <div className={styles.hero}>
        <h1>🏆 Copa do Mundo 2026</h1>
        <p>Fase de grupos — Acompanhe os jogos em tempo real</p>
        {liveMatches.length > 0 && (
          <div className={styles.liveAlert}>
            <span className="live-dot" />
            {liveMatches.length} jogo(s) ao vivo agora!
          </div>
        )}
      </div>

      {/* Tabs de grupos */}
      <div className={styles.tabs}>
        {groups.map(g => (
          <button
            key={g.groupName}
            className={`${styles.tab} ${activeGroup === g.groupName ? styles.tabActive : ''}`}
            onClick={() => setActiveGroup(g.groupName)}
          >
            Grupo {g.groupName}
          </button>
        ))}
      </div>

      {/* Jogos do grupo */}
      {groups.filter(g => g.groupName === activeGroup).map(group => (
        <div key={group.groupName} className={styles.groupSection}>
          <h2 className={styles.groupTitle}>Grupo {group.groupName}</h2>
          <div className={styles.matchGrid}>
            {group.matches.map(m => (
              <MatchCard
                key={m.id}
                match={m}
                userBet={betMap[m.id]}
                showBet={!!currentUser && !!betMap[m.id]}
              />
            ))}
          </div>
        </div>
      ))}
    </div>
  )
}
