import { useState, type FormEvent } from 'react'
import { redeemKey } from '../api/user'
import { useAuth } from '../store/auth'
import { useToast } from '../store/toast'
import GlassCard from '../components/GlassCard'
import Spinner from '../components/Spinner'

export default function Redeem() {
  const token = useAuth(s => s.token)!
  const authUser = useAuth(s => s.user)
  const setAuth = useAuth(s => s.setAuth)
  const primeAudio = useToast(s => s.primeAudio)
  const showToast = useToast(s => s.show)
  const [key, setKey] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState<{ plan: string; expiry: string | null } | null>(null)

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    primeAudio()
    if (!key.trim()) return
    setError('')
    setSuccess(null)
    setLoading(true)
    try {
      const res = await redeemKey(key.trim(), token)
      setSuccess({ plan: res.plan, expiry: res.expiry })
      setKey('')
      if (authUser) {
        setAuth(token, { ...authUser, plan: res.plan, planExpiry: res.expiry })
      }
      showToast('success', 'Key redeemed. Subscription active.', { ding: true })
    } catch (err: unknown) {
      setError(err instanceof Error ? err.message : 'Redemption failed')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div>
      <div className="mb-8">
        <div className="page-title mb-1">REDEEM KEY</div>
        <p className="text-[color:var(--text-muted)] text-sm">Enter your license key to activate your subscription</p>
      </div>

      <div className="max-w-lg">
        {success ? (
          <GlassCard padding="p-8">
            <div className="text-center">
              <div
                className="inline-flex items-center justify-center w-16 h-16 rounded-2xl mb-5"
                style={{ background: 'rgba(22,163,74,0.1)' }}
              >
                <svg width="28" height="28" fill="none" viewBox="0 0 24 24" stroke="#16a34a" strokeWidth="2">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M5 13l4 4L19 7"/>
                </svg>
              </div>
              <div className="font-display text-3xl tracking-wider text-[color:var(--text)] mb-2">KEY REDEEMED</div>
              <p className="text-[color:var(--text-muted)] text-sm mb-6">Your subscription is now active</p>
              <div className="glass-dark rounded-xl px-6 py-4 text-left space-y-3">
                <div className="flex justify-between">
                  <span className="text-xs text-[color:var(--text-muted)] uppercase tracking-widest">Plan</span>
                  <span
                    className="text-xs font-bold px-2.5 py-0.5 rounded-full text-white"
                    style={{ background: 'var(--red)' }}
                  >
                    {success.plan.toUpperCase()}
                  </span>
                </div>
                <div className="flex justify-between">
                  <span className="text-xs text-[color:var(--text-muted)] uppercase tracking-widest">Expires</span>
                  <span className="text-sm font-medium text-[color:var(--text)]">
                    {success.expiry
                      ? new Date(success.expiry).toLocaleDateString('en-US', { year: 'numeric', month: 'long', day: 'numeric' })
                      : 'Never'}
                  </span>
                </div>
              </div>
              <button
                onClick={() => setSuccess(null)}
                className="btn-ghost mt-5 px-6 py-2.5 rounded-xl text-sm"
              >
                Redeem Another Key
              </button>
            </div>
          </GlassCard>
        ) : (
          <GlassCard padding="p-8">
            <form onSubmit={handleSubmit}>
              <div className="mb-6">
                <label className="label">License Key</label>
                <input
                  type="text"
                  value={key}
                  onChange={e => setKey(e.target.value.toUpperCase())}
                  className="input-field px-4 py-3.5 rounded-xl font-mono text-base tracking-widest"
                  placeholder="XXXX-XXXX-XXXX-XXXX"
                  spellCheck={false}
                  autoComplete="off"
                  required
                  autoFocus
                />
              </div>

              {error && (
                <div className="flex items-center gap-2 text-sm alert-error rounded-xl px-4 py-3 mb-4">
                  <svg width="14" height="14" fill="currentColor" viewBox="0 0 20 20"><path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm1-11a1 1 0 10-2 0v4a1 1 0 102 0V7zm0 6a1 1 0 10-2 0 1 1 0 002 0z" clipRule="evenodd"/></svg>
                  {error}
                </div>
              )}

              <button
                type="submit"
                disabled={loading || !key.trim()}
                className="btn-red w-full py-3.5 rounded-xl flex items-center justify-center gap-2"
              >
                {loading ? <Spinner size={18} color="white" /> : (
                  <>
                    Activate Key
                    <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2.5"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
                  </>
                )}
              </button>
            </form>

            <div className="mt-6 pt-5 border-t border-[color:var(--border)]">
              <p className="text-xs text-[color:var(--text-muted)] leading-relaxed">
                Keys are single-use and bind to your account on first redemption.
                Contact support if your key isn't working.
              </p>
            </div>
          </GlassCard>
        )}
      </div>
    </div>
  )
}
