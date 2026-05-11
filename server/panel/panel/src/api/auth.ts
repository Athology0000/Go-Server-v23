import { apiFetch } from './client'
import type { AuthUser } from '../store/auth'

export interface AuthResponse {
  token: string
  user: AuthUser
}

export const login = (email: string, password: string) =>
  apiFetch<AuthResponse>('/auth/login', {
    method: 'POST',
    body: JSON.stringify({ email, password }),
  })

export const register = (username: string, email: string, password: string) =>
  apiFetch<AuthResponse>('/auth/register', {
    method: 'POST',
    body: JSON.stringify({ username, email, password }),
  })
