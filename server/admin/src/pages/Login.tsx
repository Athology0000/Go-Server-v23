import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { adminLogin } from '../api/admin'
import { useAuth } from '../store/auth'
import Spinner from '../components/Spinner'

export default function Login() {
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const setAuth = useAuth(s => s.setAuth)
  const navigate = useNavigate()

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      const res = await adminLogin(username, password)
      setAuth(res.token, res.user)
      navigate('/users')
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Authentication failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <div
        className="fixed inset-0 pointer-events-none"
        style={{
          backgroundImage: `
            linear-gradient(rgba(218,41,28,0.04) 1px, transparent 1px),
            linear-gradient(90deg, rgba(218,41,28,0.04) 1px, transparent 1px)
          `,
          backgroundSize: '48px 48px',
        }}
      />

      <div className="relative w-full max-w-sm">
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-16 h-16 rounded-2xl mb-5 shadow-xl" style={{ background: 'var(--red)' }}>
            <span className="font-display text-white text-4xl leading-none">C</span>
          </div>
          <h1 className="font-display text-5xl tracking-[0.2em] text-gray-900">COBALT</h1>
          <p className="text-gray-400 text-xs tracking-widest mt-1 uppercase">Staff Panel</p>
        </div>

        <div className="glass rounded-2xl p-8">
          <div className="flex items-center gap-2 mb-6">
            <div className="w-1 h-6 rounded-full" style={{ background: 'var(--red)' }} />
            <h2 className="font-display text-2xl tracking-[0.12em] text-gray-800">ADMIN LOGIN</h2>
          </div>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="label">Username</label>
              <input
                type="text"
                value={username}
                onChange={e => setUsername(e.target.value)}
                className="input-field px-4 py-3 rounded-xl"
                placeholder="admin"
                required
                autoFocus
              />
            </div>
            <div>
              <label className="label">Password</label>
              <input
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                className="input-field px-4 py-3 rounded-xl"
                placeholder="••••••••"
                required
              />
            </div>

            {error && (
              <div className="flex items-center gap-2 text-sm text-red-600 bg-red-50 border border-red-100 rounded-xl px-4 py-3">
                <svg width="14" height="14" fill="currentColor" viewBox="0 0 20 20"><path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11a1 1 0 10-2 0v4a1 1 0 102 0V7zm0 6a1 1 0 10-2 0 1 1 0 002 0z" clipRule="evenodd"/></svg>
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="btn-red w-full py-3 rounded-xl flex items-center justify-center gap-2 mt-2"
            >
              {loading ? <Spinner size={18} color="white" /> : 'Authenticate'}
            </button>
          </form>
        </div>

        <p className="text-center text-[11px] text-gray-300 mt-5 tracking-wider">
          COBALT STAFF ONLY — UNAUTHORIZED ACCESS PROHIBITED
        </p>
      </div>
    </div>
  )
}
