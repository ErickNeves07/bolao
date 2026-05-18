import { format } from 'date-fns'
import { ptBR } from 'date-fns/locale'
import type { Match } from '../types'
import styles from './MatchCard.module.css'

interface Props {
  match: Match
  userBet?: { home: number; away: number }
  showBet?: boolean
  compact?: boolean
}

const statusLabel = (m: Match) => {
  if (m.status === 'FINISHED') return <span className="badge badge-done">Encerrado</span>
  if (m.status === 'LIVE') return (
    <span className="badge badge-live">
      <span className="live-dot" />
      {m.elapsedMinutes ? `${m.elapsedMinutes}'` : 'Ao Vivo'}
    </span>
  )
  return <span className="badge badge-soon">
    {format(new Date(m.matchDate), "dd/MM HH:mm", { locale: ptBR })}
  </span>
}

export default function MatchCard({ match: m, userBet, showBet, compact }: Props) {
  return (
    <div className={`${styles.card} card ${m.status === 'LIVE' ? styles.live : ''}`}>
      <div className={styles.header}>
        <span className={styles.group}>Grupo {m.groupName}</span>
        {statusLabel(m)}
      </div>

      <div className={styles.teams}>
        {/* Casa */}
        <div className={styles.team}>
          <img src={m.homeTeam.flagUrl} alt={m.homeTeam.name} className={styles.flag} />
          <span className={styles.teamName}>{m.homeTeam.name}</span>
        </div>

        {/* Placar */}
        <div className={styles.score}>
          {m.status !== 'SCHEDULED'
            ? <>
                <span>{m.homeScore ?? '–'}</span>
                <span className={styles.sep}>:</span>
                <span>{m.awayScore ?? '–'}</span>
              </>
            : <span style={{ color: 'var(--text-muted)', fontSize: '1rem' }}>vs</span>
          }
        </div>

        {/* Visitante */}
        <div className={`${styles.team} ${styles.right}`}>
          <img src={m.awayTeam.flagUrl} alt={m.awayTeam.name} className={styles.flag} />
          <span className={styles.teamName}>{m.awayTeam.name}</span>
        </div>
      </div>

      {/* Última atualização (live) */}
      {m.status === 'LIVE' && m.lastUpdated && (
        <div className={styles.updated}>
          Atualizado às {format(new Date(m.lastUpdated), 'HH:mm:ss')}
        </div>
      )}

      {/* Aposta do usuário */}
      {showBet && userBet && (
        <div className={styles.betRow}>
          <span>🎯 Sua aposta:</span>
          <strong>{userBet.home} × {userBet.away}</strong>
        </div>
      )}
    </div>
  )
}
