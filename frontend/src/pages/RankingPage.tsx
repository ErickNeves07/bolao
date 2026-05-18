import { useEffect, useState } from 'react'
import { getRanking, getUserBets } from '../services/bolaoApi'
import type { RankingEntry, Bet } from '../types'
import RankingTable from '../components/RankingTable'
import { useUser } from '../hooks/useUser'
import styles from './RankingPage.module.css'

export default function RankingPage() {
  const { currentUser } = useUser()
  const [ranking, setRanking] = useState<RankingEntry[]>([])
  const [selectedUser, setSelectedUser] = useState<RankingEntry | null>(null)
  const [userBets, setUserBets] = useState<Bet[]>([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const load = async () => {
      setLoading(true)
      try { setRanking(await getRanking()) }
      finally { setLoading(false) }
    }
    load()
    const t = setInterval(load, 30_000)
    return () => clearInterval(t)
  }, [])

  const handleSelectUser = async (entry: RankingEntry) => {
    if (selectedUser?.userId === entry.userId) {
      setSelectedUser(null)
      setUserBets([])
      return
    }
    setSelectedUser(entry)
    const bets = await getUserBets(entry.userId)
    setUserBets(bets)
  }

  if (loading) return <div className={styles.loading}>Carregando ranking...</div>

  return (
    <div>
      <div className={styles.header}>
        <h1>🏆 Ranking Geral</h1>
        <p>Clique num participante para ver as apostas</p>
      </div>

      {/* Pódio top 3 */}
      {ranking.length >= 3 && (
        <div className={styles.podium}>
          {/* 2º */}
          <div className={`${styles.podiumItem} ${styles.second}`} onClick={() => handleSelectUser(ranking[1])}>
            <span className={styles.podiumEmoji}>🥈</span>
            <div className={styles.podiumAvatar}>{ranking[1].userName.charAt(0)}</div>
            <span className={styles.podiumName}>{ranking[1].userName}</span>
            <span className={styles.podiumPts}>{ranking[1].totalPoints} pts</span>
          </div>
          {/* 1º */}
          <div className={`${styles.podiumItem} ${styles.first}`} onClick={() => handleSelectUser(ranking[0])}>
            <span className={styles.podiumEmoji}>🥇</span>
            <div className={styles.podiumAvatar}>{ranking[0].userName.charAt(0)}</div>
            <span className={styles.podiumName}>{ranking[0].userName}</span>
            <span className={styles.podiumPts}>{ranking[0].totalPoints} pts</span>
          </div>
          {/* 3º */}
          <div className={`${styles.podiumItem} ${styles.third}`} onClick={() => handleSelectUser(ranking[2])}>
            <span className={styles.podiumEmoji}>🥉</span>
            <div className={styles.podiumAvatar}>{ranking[2].userName.charAt(0)}</div>
            <span className={styles.podiumName}>{ranking[2].userName}</span>
            <span className={styles.podiumPts}>{ranking[2].totalPoints} pts</span>
          </div>
        </div>
      )}

      <div className={styles.legend}>
        <span>⭐ Placar exato (5pts)</span>
        <span>🤝 Empate certo (3pts)</span>
        <span>✅ Vencedor certo (2pts)</span>
      </div>

      <RankingTable entries={ranking} highlightUserId={currentUser?.id} />

      {/* Painel de apostas do usuário selecionado */}
      {selectedUser && (
        <div className={styles.betPanel}>
          <h3>Apostas de <span style={{ color: 'var(--primary)' }}>{selectedUser.userName}</span></h3>
          <div className={styles.betList}>
            {userBets.map(b => (
              <div key={b.id} className={`card ${styles.betItem}`}>
                <span className={styles.teams}>{b.homeTeamName} vs {b.awayTeamName}</span>
                <span className={styles.betScore}>{b.homeScoreBet} × {b.awayScoreBet}</span>
                {b.pointsCalculated && (
                  <span className={styles.pts} data-pts={b.points}>
                    {b.points}pts
                  </span>
                )}
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  )
}
