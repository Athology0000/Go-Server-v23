import { describe, it, expect, beforeEach } from 'vitest'
import { useAuth, type AdminUser } from './auth'

const user: AdminUser = {
  id: '1',
  username: 'root',
  email: 'root@example.com',
  role: 'admin',
}

describe('admin auth store', () => {
  beforeEach(() => useAuth.getState().logout())

  it('sets and clears auth', () => {
    useAuth.getState().setAuth('tok', user)
    expect(useAuth.getState().token).toBe('tok')
    expect(useAuth.getState().user?.role).toBe('admin')

    useAuth.getState().logout()
    expect(useAuth.getState().token).toBeNull()
    expect(useAuth.getState().user).toBeNull()
  })
})
