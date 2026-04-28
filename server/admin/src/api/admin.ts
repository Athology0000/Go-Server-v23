import { apiFetch } from './client'
import type { AdminUser as AuthUser } from '../store/auth'

export interface AuthResponse {
  token: string
  user: AuthUser
}

export interface UserRecord {
  id: string
  username: string
  email: string
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
  plan: string
  durationDays: number
  createdAt: string
  usedBy: string | null
  usedAt: string | null
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

export const generateKey = (plan: string, durationDays: number, token: string) =>
  apiFetch<GeneratedKey>('/admin/keys/generate', {
    method: 'POST',
    body: JSON.stringify({ plan, durationDays }),
  }, token)

export const getKeys = (token: string) =>
  apiFetch<GeneratedKey[]>('/admin/keys', {}, token)
