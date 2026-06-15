import { z } from 'zod'
import { apiFetch } from './client'

const adminUserSchema = z.object({
  id: z.string(),
  username: z.string(),
  email: z.string().nullable(),
  role: z.enum(['admin', 'superadmin']),
})

export const authResponseSchema = z.object({
  token: z.string(),
  user: adminUserSchema,
})
export type AuthResponse = z.infer<typeof authResponseSchema>

export const userRecordSchema = z.object({
  id: z.string(),
  username: z.string(),
  email: z.string().nullable(),
  plan: z.string().nullable(),
  planExpiry: z.string().nullable(),
  hwid: z.string().nullable(),
  hwidBound: z.boolean(),
  banned: z.boolean(),
  bannedReason: z.string().nullable(),
  createdAt: z.string(),
  lastSeen: z.string().nullable(),
  minecraftUsername: z.string().nullable(),
})
export type UserRecord = z.infer<typeof userRecordSchema>

export const generatedKeySchema = z.object({
  id: z.string(),
  key: z.string(),
  keyHash: z.string().optional(),
  keyHashPrefix: z.string().optional(),
  plan: z.string(),
  durationDays: z.number(),
  createdAt: z.string(),
  usedBy: z.string().nullable(),
  usedAt: z.string().nullable(),
})
export type GeneratedKey = z.infer<typeof generatedKeySchema>

export const auditRecordSchema = z.object({
  id: z.string(),
  event_type: z.string(),
  account_id: z.string().nullable(),
  device_id: z.string().nullable(),
  admin_name: z.string().nullable(),
  ip: z.string().nullable(),
  details: z.record(z.unknown()).nullable(),
  created_at: z.string(),
})
export type AuditRecord = z.infer<typeof auditRecordSchema>

const addTimeResponseSchema = z.object({ status: z.string(), new_expiry: z.string() })
const upgradeResponseSchema = z.object({ status: z.string(), plan: z.string() })

export const serverLogResponseSchema = z.object({
  lines: z.array(z.string()),
  seq: z.number(),
})
export type ServerLogResponse = z.infer<typeof serverLogResponseSchema>

export const adminLogin = (username: string, password: string) =>
  apiFetch('/admin/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  }, undefined, authResponseSchema)

export const getUsers = (search: string, token: string) =>
  apiFetch(`/admin/users${search ? `?q=${encodeURIComponent(search)}` : ''}`, {}, token, z.array(userRecordSchema))

export const getUser = (id: string, token: string) =>
  apiFetch(`/admin/users/${id}`, {}, token, userRecordSchema)

export const banUser = (id: string, reason: string, token: string) =>
  apiFetch<void>(`/admin/users/${id}/ban`, {
    method: 'POST',
    body: JSON.stringify({ reason }),
  }, token)

export const unbanUser = (id: string, token: string) =>
  apiFetch<void>(`/admin/users/${id}/unban`, { method: 'POST' }, token)

export const addTime = (id: string, days: number, plan: string, token: string) =>
  apiFetch(`/admin/users/${id}/add-time`, {
    method: 'POST',
    body: JSON.stringify({ days, plan }),
  }, token, addTimeResponseSchema)

export const upgradePlan = (id: string, plan: string, token: string) =>
  apiFetch(`/admin/users/${id}/upgrade`, {
    method: 'POST',
    body: JSON.stringify({ plan }),
  }, token, upgradeResponseSchema)

export const generateKey = (plan: string, durationDays: number, token: string) =>
  apiFetch('/admin/keys/generate', {
    method: 'POST',
    body: JSON.stringify({ plan, durationDays }),
  }, token, generatedKeySchema)

export const getKeys = (token: string) =>
  apiFetch('/admin/keys', {}, token, z.array(generatedKeySchema))

export const getAuditLog = (token: string, limit = 120, offset = 0) =>
  apiFetch(`/admin/audit?limit=${limit}&offset=${offset}`, {}, token, z.array(auditRecordSchema))

export const getActivityLog = (token: string, limit = 120, offset = 0) =>
  apiFetch(`/admin/activity?limit=${limit}&offset=${offset}`, {}, token, z.array(auditRecordSchema))

export const getServerLogs = (token: string, after = 0) =>
  apiFetch(`/admin/server-logs?after=${after}`, {}, token, serverLogResponseSchema)
