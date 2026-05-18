import type { RankingEntry } from '../types'
import styles from './RankingTable.module.css'

interface Props {
  entries: RankingEntry[]
  highlightUserId?: number
}

const ChangeIndicator = ({ change }: { change: number }) => {
  if (change === 0) return <span className={styles.neutral}>—</span>
  if (change > 0) return <span className={styles.up}>▲{change}</span>
  return <span className={styles.down}>▼{Math.abs(change)}</span>
}

export default function RankingTable({ entries, highlightUserId }: Props) {
  return (
    <div className={styles.wrap}>
      <div className={styles.header}>
        <span className={styles.pos}>#</span>
        <span className={styles.nameCol}>Participante</span>
        <span className={styles.pts}>Pts</span>
        <span className={styles.stat}>⭐</span>
        <span className={styles.stat}>🤝</span>
        <span className={styles.stat}>✅</span>
        <span className={styles.chg}>Variação</span>
      </div>

      {entries.map(e => (
        <div
          key={e.userId}
          className={`${styles.row} ${e.userId === highlightUserId ? styles.highlight : ''} ${e.position <= 3 ? styles['top' + e.position] : ''}`}
        >
          <span className={styles.pos}>
            {e.position === 1 ? '🥇' : e.position === 2 ? '🥈' : e.position === 3 ? '🥉' : e.position}
          </span>

          <div className={styles.nameCol}>
            <div className={styles.avatar}>
              {e.avatarUrl
                ? <img src={e.avatarUrl} alt="" />
                : e.userName.charAt(0).toUpperCase()}
            </div>
            <span className={styles.nameText}>{e.userName}</span>
            <span className={styles.bets}>{e.totalBets} apostas</span>
          </div>

          <span className={styles.pts}>{e.totalPoints}</span>
          <span className={styles.stat}>{e.exactScores}</span>
          <span className={styles.stat}>{e.correctDraws}</span>
          <span className={styles.stat}>{e.correctWinners}</span>
          <span className={styles.chg}>
            <ChangeIndicator change={e.positionChange} />
          </span>
        </div>
      ))}
    </div>
  )
}
