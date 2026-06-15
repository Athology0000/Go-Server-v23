import { describe, it, expect, vi, beforeEach } from 'vitest'
import { render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { MemoryRouter } from 'react-router-dom'
import Login from './Login'
import { useAuth } from '../store/auth'

const authResponse = {
  token: 'tok',
  user: { id: '1', username: 'neo', email: 'neo@example.com', role: 'user', plan: null, planExpiry: null },
}

describe('Login', () => {
  beforeEach(() => {
    useAuth.getState().logout()
    // Stub the network so the real (zod-validated) auth flow runs end to end.
    vi.stubGlobal(
      'fetch',
      vi.fn(
        async () =>
          new Response(JSON.stringify(authResponse), {
            status: 200,
            headers: { 'content-type': 'application/json' },
          }),
      ),
    )
  })

  it('signs in and stores the auth token', async () => {
    render(
      <MemoryRouter>
        <Login />
      </MemoryRouter>,
    )
    await userEvent.type(screen.getByPlaceholderText('you@example.com'), 'neo@example.com')
    await userEvent.type(screen.getByPlaceholderText('••••••••'), 'secret123')
    await userEvent.click(screen.getByRole('button', { name: /sign in/i }))

    await waitFor(() => expect(useAuth.getState().token).toBe('tok'))
    expect(useAuth.getState().user?.username).toBe('neo')
  })
})
