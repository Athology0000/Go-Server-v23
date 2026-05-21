import { useEffect, useState } from 'react'
import { getMe, type UserProfile } from '../api/user'
import { useAuth } from '../store/auth'
import GlassCard from '../components/GlassCard'
import Spinner from '../components/Spinner'

function InfoRow({ label, value, mono }: { label: string; value: string; mono?: boolean }) {
  return (
    <div className="flex items-center justify-between py-3 border-b border-[color:var(--border)] last:border-0">
      <span className="text-xs text-[color:var(--text-muted)] uppercase tracking-widest">{label}</span>
      <span className={`text-sm font-medium text-[color:var(--text)] ${mono ? 'font-mono tracking-wider' : ''}`}>
        {value}
      </span>
    </div>
  )
}

function CopySecret({ label, value, hint }: { label: string; value: string; hint?: string }) {
  const [copied, setCopied] = useState(false)
  const copy = () => {
    navigator.clipboard.writeText(value).then(() => {
      setCopied(true)
      setTimeout(() => setCopied(false), 1500)
    })
  }
  return (
    <div className="rounded-xl border border-[color:var(--border)] overflow-hidden">
      <div className="px-4 py-2.5 flex items-center justify-between border-b border-[color:var(--border)]" style={{ background: 'rgba(155,89,255,0.06)' }}>
        <span className="text-xs font-semibold text-[color:var(--accent)] uppercase tracking-widest">{label}</span>
        <button
          onClick={copy}
          className="flex items-center gap-1.5 px-3 py-1 rounded-lg text-xs font-semibold transition-all"
          style={{ background: copied ? 'rgba(22,163,74,0.12)' : 'rgba(155,89,255,0.12)', color: copied ? 'var(--good)' : 'var(--accent)' }}
        >
          {copied ? '✓ Copied' : 'Copy'}
        </button>
      </div>
      <div className="px-4 py-3">
        <code className="text-sm font-mono text-[color:var(--text)] break-all select-all">{value}</code>
        {hint && <p className="text-[11px] text-[color:var(--text-muted)] mt-2 leading-relaxed">{hint}</p>}
      </div>
    </div>
  )
}

export default function Device() {
  const token = useAuth(s => s.token)!
  const [profile, setProfile] = useState<UserProfile | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    getMe(token)
      .then(setProfile)
      .catch(e => setError(e instanceof Error ? e.message : 'Failed to load'))
      .finally(() => setLoading(false))
  }, [token])

  if (loading) return (
    <div className="flex items-center justify-center h-64"><Spinner size={32} /></div>
  )

  return (
    <div>
      <div className="mb-8">
        <div className="page-title mb-1">DEVICE INFO</div>
        <p className="text-[color:var(--text-muted)] text-sm">Your binding and hardware information</p>
      </div>

      {error && (
        <div className="mb-6 text-sm alert-error rounded-xl px-4 py-3">{error}</div>
      )}

      <div className="max-w-lg space-y-4">
        <GlassCard padding="p-6">
          <div className="label mb-4">Binding Status</div>
          <div className="flex items-center gap-4 mb-5">
            <div
              className="w-12 h-12 rounded-2xl flex items-center justify-center flex-shrink-0"
              style={{ background: profile?.hwidBound ? 'rgba(22,163,74,0.1)' : 'rgba(255,56,96,0.08)' }}
            >
              {profile?.hwidBound ? (
                <svg width="22" height="22" fill="none" viewBox="0 0 24 24" stroke="#16a34a" strokeWidth="2">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z"/>
                </svg>
              ) : (
                <svg width="22" height="22" fill="none" viewBox="0 0 24 24" stroke="var(--red)" strokeWidth="2">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M12 15v2m-6 4h12a2 2 0 002-2v-6a2 2 0 00-2-2H6a2 2 0 00-2 2v6a2 2 0 002 2zm10-10V7a4 4 0 00-8 0v4h8z"/>
                </svg>
              )}
            </div>
            <div>
              <div className="font-semibold text-[color:var(--text)]">
                {profile?.hwidBound ? 'Device Bound' : 'No Device Bound'}
              </div>
              <div className="text-xs text-[color:var(--text-muted)] mt-0.5">
                {profile?.hwidBound
                  ? 'Your license is locked to this hardware'
                  : 'Will bind on first authenticated launch'}
              </div>
            </div>
          </div>

          <div>
            <InfoRow
              label="Hardware ID"
              value={profile?.hwid ? `${profile.hwid.substring(0, 8)}••••••••` : 'Not bound'}
              mono
            />
            <InfoRow
              label="Minecraft Username"
              value={profile?.minecraftUsername ?? 'Not set'}
            />
          </div>
        </GlassCard>

        {!profile?.hwidBound && profile?.id && (
          <GlassCard padding="p-5">
            <div className="label mb-1">First-Time Setup</div>
            <p className="text-xs text-[color:var(--text-muted)] mb-4 leading-relaxed">
              Enter this Account ID into the Cobalt Bootstrapper when prompted during first-time setup.
            </p>
            <CopySecret
              label="Account ID"
              value={profile.id}
              hint="Paste this into the bootstrapper when it asks for your Cobalt account ID."
            />
          </GlassCard>
        )}

        <GlassCard padding="p-5">
          <div className="flex items-start gap-3">
            <svg width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="var(--warning)" strokeWidth="2" className="mt-0.5 flex-shrink-0">
              <path strokeLinecap="round" strokeLinejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
            <div>
              <div className="text-sm font-medium text-[color:var(--text)] mb-1">Need to reset your binding?</div>
              <p className="text-xs text-[color:var(--text-muted)] leading-relaxed">
                Hardware resets are handled manually. Open a support ticket in our Discord and a staff member will assist you.
              </p>
            </div>
          </div>
        </GlassCard>
      </div>
    </div>
  )
}
