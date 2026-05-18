import api from './api'
import type {
  User, Match, GroupMatches, Bet, RankingEntry,
  ApiStatus, ForceUpdateResponse
} from '../types'

// --- Usuários ---
export const getUsers = () => api.get<User[]>('/api/users').then(r => r.data)
export const createUser = (name: string) =>
  api.post<User>('/api/users', { name }).then(r => r.data)

// --- Jogos ---
export const getMatches = () => api.get<Match[]>('/api/matches').then(r => r.data)
export const getMatchGroups = () =>
  api.get<GroupMatches[]>('/api/matches/groups').then(r => r.data)

// --- Apostas ---
export const getUserBets = (userId: number) =>
  api.get<Bet[]>(`/api/users/${userId}/bets`).then(r => r.data)

export const saveBet = (userId: number, matchId: number, home: number, away: number) =>
  api.put<Bet>(`/api/users/${userId}/bets`, {
    matchId, homeScoreBet: home, awayScoreBet: away
  }).then(r => r.data)

export const saveBulkBets = (userId: number, bets: { matchId: number; homeScoreBet: number; awayScoreBet: number }[]) =>
  api.put<Bet[]>(`/api/users/${userId}/bets/bulk`, { bets }).then(r => r.data)

// --- Ranking ---
export const getRanking = () =>
  api.get<RankingEntry[]>('/api/ranking').then(r => r.data)

export const getMatchBets = (matchId: number) =>
  api.get<Bet[]>(`/api/ranking/matches/${matchId}/bets`).then(r => r.data)

// --- Admin ---
export const getApiStatus = (username: string, password: string) =>
  api.get<ApiStatus>('/api/admin/api-status', {
    auth: { username, password }
  }).then(r => r.data)

export const forceUpdate = (username: string, password: string) =>
  api.post<ForceUpdateResponse>('/api/admin/force-update', null, {
    auth: { username, password }
  }).then(r => r.data)

export const updateAvatar = (userId: number, avatarUrl: string, username: string, password: string) =>
  api.put<User>(`/api/admin/users/${userId}/avatar`, { avatarUrl }, {
    auth: { username, password }
  }).then(r => r.data)
