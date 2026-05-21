import { apiFetch } from './client'

export interface UserProfile {
  id: string
  username: string
  email: string
  plan: string | null
  planExpiry: string | null
  hwid: string | null
  hwidBound: boolean
  minecraftUsername: string | null
  createdAt: string
  licenseKeyPrefix: string | null
}

export interface RedeemResponse {
  success: boolean
  plan: string
  expiry: string | null
}

export interface UserStats {
  totalSessions: number
  totalRuntimeSeconds: number
  totalCoins: number
  totalFish: number
  totalDrops: number
  lastSeen: string | null
}

export interface LeaderboardEntry {
  rank: number
  username: string
  value: number
}

export const getMe = (token: string) =>
  apiFetch<UserProfile>('/user/me', {}, token)

export const redeemKey = (key: string, token: string) =>
  apiFetch<RedeemResponse>('/license/redeem', {
    method: 'POST',
    body: JSON.stringify({ key }),
  }, token)

export const getStats = (token: string) =>
  apiFetch<UserStats>('/user/stats', {}, token)

export const getLeaderboard = (stat: string, token: string) =>
  apiFetch<LeaderboardEntry[]>(`/leaderboard?stat=${encodeURIComponent(stat)}`, {}, token)
