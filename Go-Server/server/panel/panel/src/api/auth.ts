import { z } from 'zod'
import { apiFetch } from './client'

const authUserSchema = z.object({
  id: z.string(),
  username: z.string(),
  email: z.string(),
  role: z.enum(['user', 'admin']),
  plan: z.string().nullable(),
  planExpiry: z.string().nullable(),
})

export const authResponseSchema = z.object({
  token: z.string(),
  user: authUserSchema,
})
export type AuthResponse = z.infer<typeof authResponseSchema>

export const login = (email: string, password: string) =>
  apiFetch('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  }, undefined, authResponseSchema)

export const register = (username: string, email: string, password: string) =>
  apiFetch('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username, email, password }),
  }, undefined, authResponseSchema)
