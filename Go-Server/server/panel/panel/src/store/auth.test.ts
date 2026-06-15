import { describe, it, expect, beforeEach } from 'vitest'
import { useAuth, type AuthUser } from './auth'

const user: AuthUser = {
  id: '1',
  username: 'neo',
  email: 'neo@example.com',
  role: 'user',
  plan: null,
  planExpiry: null,
}

describe('panel auth store', () => {
  beforeEach(() => useAuth.getState().logout())

  it('sets and clears auth', () => {
    useAuth.getState().setAuth('tok', user)
    expect(useAuth.getState().token).toBe('tok')
    expect(useAuth.getState().user?.username).toBe('neo')

    useAuth.getState().logout()
    expect(useAuth.getState().token).toBeNull()
    expect(useAuth.getState().user).toBeNull()
  })
})
