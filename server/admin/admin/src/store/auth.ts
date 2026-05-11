import { create } from 'zustand'
import { persist } from 'zustand/middleware'

export interface AdminUser {
  id: string
  username: string
  email: string | null
  role: 'admin' | 'superadmin'
}

interface AuthState {
  token: string | null
  user: AdminUser | null
  setAuth: (token: string, user: AdminUser) => void
  logout: () => void
}

export const useAuth = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      user: null,
      setAuth: (token, user) => set({ token, user }),
      logout: () => set({ token: null, user: null }),
    }),
    { name: 'cobalt-admin-auth' }
  )
)
