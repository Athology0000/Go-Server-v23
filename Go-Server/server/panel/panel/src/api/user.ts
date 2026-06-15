import { z } from 'zod'
import { apiFetch } from './client'

export const userProfileSchema = z.object({
  id: z.string(),
  username: z.string(),
  email: z.string(),
  plan: z.string().nullable(),
  planExpiry: z.string().nullable(),
  hwid: z.string().nullable(),
  hwidBound: z.boolean(),
  minecraftUsername: z.string().nullable(),
  createdAt: z.string(),
  licenseKeyPrefix: z.string().nullable(),
})
export type UserProfile = z.infer<typeof userProfileSchema>

export const redeemResponseSchema = z.object({
  success: z.boolean(),
  plan: z.string(),
  expiry: z.string().nullable(),
})
export type RedeemResponse = z.infer<typeof redeemResponseSchema>

export const userStatsSchema = z.object({
  totalSessions: z.number(),
  totalRuntimeSeconds: z.number(),
  totalCoins: z.number(),
  totalFish: z.number(),
  totalDrops: z.number(),
  lastSeen: z.string().nullable(),
})
export type UserStats = z.infer<typeof userStatsSchema>

export const leaderboardEntrySchema = z.object({
  rank: z.number(),
  username: z.string(),
  value: z.number(),
})
export type LeaderboardEntry = z.infer<typeof leaderboardEntrySchema>

export const getMe = (token: string) =>
  apiFetch('/user/me', {}, token, userProfileSchema)

export const redeemKey = (key: string, token: string) =>
  apiFetch('/license/redeem', {
    method: 'POST',
    body: JSON.stringify({ key }),
  }, token, redeemResponseSchema)

export const getStats = (token: string) =>
  apiFetch('/user/stats', {}, token, userStatsSchema)

export const getLeaderboard = (stat: string, token: string) =>
  apiFetch(`/leaderboard?stat=${encodeURIComponent(stat)}`, {}, token, z.array(leaderboardEntrySchema))
