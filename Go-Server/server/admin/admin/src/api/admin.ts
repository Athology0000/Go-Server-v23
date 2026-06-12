import { apiFetch } from './client'
import type { AdminUser as AuthUser } from '../store/auth'

export interface AuthResponse {
  token: string
  user: AuthUser
}

export interface UserRecord {
  id: string
  username: string
  email: string | null
  plan: string | null
  planExpiry: string | null
  hwid: string | null
  hwidBound: boolean
  banned: boolean
  bannedReason: string | null
  createdAt: string
  lastSeen: string | null
  minecraftUsername: string | null
}

export interface GeneratedKey {
  id: string
  key: string
  keyHash?: string
  keyHashPrefix?: string
  plan: string
  durationDays: number
  createdAt: string
  usedBy: string | null
  usedAt: string | null
}

export interface AuditRecord {
  id: string
  event_type: string
  account_id: string | null
  device_id: string | null
  admin_name: string | null
  ip: string | null
  details: Record<string, unknown> | null
  created_at: string
}

export const adminLogin = (username: string, password: string) =>
  apiFetch<AuthResponse>('/admin/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  })

export const getUsers = (search: string, token: string) =>
  apiFetch<UserRecord[]>(`/admin/users${search ? `?q=${encodeURIComponent(search)}` : ''}`, {}, token)

export const getUser = (id: string, token: string) =>
  apiFetch<UserRecord>(`/admin/users/${id}`, {}, token)

export const banUser = (id: string, reason: string, token: string) =>
  apiFetch<void>(`/admin/users/${id}/ban`, {
    method: 'POST',
    body: JSON.stringify({ reason }),
  }, token)

export const unbanUser = (id: string, token: string) =>
  apiFetch<void>(`/admin/users/${id}/unban`, { method: 'POST' }, token)

export const addTime = (id: string, days: number, plan: string, token: string) =>
  apiFetch<{ status: string; new_expiry: string }>(`/admin/users/${id}/add-time`, {
    method: 'POST',
    body: JSON.stringify({ days, plan }),
  }, token)

export const upgradePlan = (id: string, plan: string, token: string) =>
  apiFetch<{ status: string; plan: string }>(`/admin/users/${id}/upgrade`, {
    method: 'POST',
    body: JSON.stringify({ plan }),
  }, token)

export const generateKey = (plan: string, durationDays: number, token: string) =>
  apiFetch<GeneratedKey>('/admin/keys/generate', {
    method: 'POST',
    body: JSON.stringify({ plan, durationDays }),
  }, token)

export const getKeys = (token: string) =>
  apiFetch<GeneratedKey[]>('/admin/keys', {}, token)

export const getAuditLog = (token: string, limit = 120, offset = 0) =>
  apiFetch<AuditRecord[]>(`/admin/audit?limit=${limit}&offset=${offset}`, {}, token)

export const getActivityLog = (token: string, limit = 120, offset = 0) =>
  apiFetch<AuditRecord[]>(`/admin/activity?limit=${limit}&offset=${offset}`, {}, token)

export interface ServerLogResponse {
  lines: string[]
  seq: number
}

export const getServerLogs = (token: string, after = 0) =>
  apiFetch<ServerLogResponse>(`/admin/server-logs?after=${after}`, {}, token)
