import { useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { register } from '../api/auth'
import { useAuth } from '../store/auth'
import Spinner from '../components/Spinner'

export default function Register() {
  const [username, setUsername] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [confirm, setConfirm] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const setAuth = useAuth(s => s.setAuth)
  const navigate = useNavigate()

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    if (password !== confirm) { setError('Passwords do not match'); return }
    setError('')
    setLoading(true)
    try {
      const res = await register(username, email, password)
      setAuth(res.token, res.user)
      navigate('/dashboard')
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Registration failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <div className="relative w-full max-w-sm">
        <div className="text-center mb-8">
          <div
            className="inline-flex items-center justify-center w-16 h-16 rounded-2xl mb-5 shadow-xl"
            style={{ background: 'var(--red)' }}
          >
            <span className="font-display text-white text-4xl leading-none">C</span>
          </div>
          <h1 className="font-display text-5xl tracking-[0.2em] text-[color:var(--text)]">COBALT</h1>
          <p className="text-[color:var(--text-muted)] text-xs tracking-widest mt-1 uppercase">Create Account</p>
        </div>

        <div className="glass rounded-2xl p-8">
          <h2 className="font-display text-2xl tracking-[0.12em] text-[color:var(--text)] mb-6">REGISTER</h2>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="label">Username</label>
              <input
                type="text"
                value={username}
                onChange={e => setUsername(e.target.value)}
                className="input-field px-4 py-3 rounded-xl"
                placeholder="YourUsername"
                required
                autoFocus
              />
            </div>
            <div>
              <label className="label">Email</label>
              <input
                type="email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                className="input-field px-4 py-3 rounded-xl"
                placeholder="you@example.com"
                required
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
                minLength={8}
              />
            </div>
            <div>
              <label className="label">Confirm Password</label>
              <input
                type="password"
                value={confirm}
                onChange={e => setConfirm(e.target.value)}
                className="input-field px-4 py-3 rounded-xl"
                placeholder="••••••••"
                required
              />
            </div>

            {error && (
              <div className="flex items-center gap-2 text-sm alert-error rounded-xl px-4 py-3">
                <svg width="14" height="14" fill="currentColor" viewBox="0 0 20 20"><path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11a1 1 0 10-2 0v4a1 1 0 102 0V7zm0 6a1 1 0 10-2 0 1 1 0 002 0z" clipRule="evenodd"/></svg>
                {error}
              </div>
            )}

            <button
              type="submit"
              disabled={loading}
              className="btn-red w-full py-3 rounded-xl flex items-center justify-center gap-2 mt-2"
            >
              {loading ? <Spinner size={18} color="white" /> : 'Create Account'}
            </button>
          </form>

          <p className="text-center text-sm text-[color:var(--text-muted)] mt-6">
            Already have an account?{' '}
            <Link to="/login" className="font-semibold transition-colors" style={{ color: 'var(--red)' }}>
              Sign In
            </Link>
          </p>
        </div>
      </div>
    </div>
  )
}
