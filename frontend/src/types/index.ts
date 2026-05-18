// Tipos alinhados com o backend
export type MatchStatus = 'SCHEDULED' | 'LIVE' | 'FINISHED' | 'POSTPONED'

export interface Team {
  id: number
  name: string
  flagUrl: string
  groupName: string
}

export interface Match {
  id: number
  apiMatchId: number
  homeTeam: Team
  awayTeam: Team
  groupName: string
  matchDate: string   // ISO-8601
  venue: string
  status: MatchStatus
  homeScore: number | null
  awayScore: number | null
  elapsedMinutes: number | null
  lastUpdated: string | null
}

export interface GroupMatches {
  groupName: string
  matches: Match[]
}

export interface User {
  id: number
  name: string
  avatarUrl: string | null
  totalBets: number
}

export interface Bet {
  id: number
  matchId: number
  homeTeamName: string
  awayTeamName: string
  homeScoreBet: number
  awayScoreBet: number
  points: number
  pointsCalculated: boolean
  matchStatus: MatchStatus
  matchHomeScore: number | null
  matchAwayScore: number | null
}

export interface RankingEntry {
  userId: number
  userName: string
  avatarUrl: string | null
  totalPoints: number
  position: number
  previousPosition: number | null
  positionChange: number
  exactScores: number
  correctDraws: number
  correctWinners: number
  totalBets: number
}

export interface ApiStatus {
  date: string
  requestsToday: number
  dailyLimit: number
  remaining: number
  lastRequestAt: string | null
}

export interface ForceUpdateResponse {
  message: string
  matchesUpdated: number
  betsRecalculated: number
  updatedAt: string
}
