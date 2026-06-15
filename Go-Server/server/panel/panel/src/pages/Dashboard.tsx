import { useState } from 'react'
import { Link } from 'react-router-dom'
import { getMe, type UserProfile } from '../api/user'
import { useAuth } from '../store/auth'
import { useAsync } from '../hooks/useAsync'
import GlassCard from '../components/GlassCard'
import Spinner from '../components/Spinner'

function CopyButton({ value, label }: { value: string; label: string }) {
  const [copied, setCopied] = useState(false)
  const copy = () => {
    navigator.clipboard.writeText(value).then(() => {
      setCopied(true)
      setTimeout(() => setCopied(false), 1500)
    })
  }
  return (
    <button
      onClick={copy}
      aria-label={copied ? 'Copied to clipboard' : `Copy ${label}`}
      className="flex-shrink-0 flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-xs font-semibold border transition-all"
      style={{ borderColor: 'rgba(155,89,255,0.3)', color: copied ? 'var(--good)' : 'var(--text-muted)', background: 'transparent' }}
    >
      {copied ? (
        <>
          <svg width="11" height="11" aria-hidden="true" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="3"><path d="M5 13l4 4L19 7"/></svg>
          Copied
        </>
      ) : (
        <>
          <svg width="11" height="11" aria-hidden="true" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2.5"><rect x="9" y="9" width="13" height="13" rx="2"/><path d="M5 15H4a2 2 0 01-2-2V4a2 2 0 012-2h9a2 2 0 012 2v1"/></svg>
          {label}
        </>
      )}
    </button>
  )
}

const KEY_SS = 'cobalt-setup-key'

function BootstrapSetupCard({ profile }: { profile: UserProfile }) {
  const [licenseKey, setLicenseKey] = useState(() => {
    try { return sessionStorage.getItem(KEY_SS) ?? '' } catch { return '' }
  })
  const [editing, setEditing] = useState(false)
  const [draft, setDraft] = useState('')

  const saveKey = () => {
    const trimmed = draft.trim()
    setLicenseKey(trimmed)
    try { sessionStorage.setItem(KEY_SS, trimmed) } catch { /* ignore */ }
    setEditing(false)
  }

  const rows: { step: number; label: string; value?: string; placeholder?: string; isKey?: boolean; isPassword?: boolean }[] = [
    { step: 1, label: 'Account ID', value: profile.id },
    { step: 2, label: 'License Key', value: licenseKey || undefined, placeholder: 'Paste your key here…', isKey: true },
    { step: 3, label: 'Username', value: profile.username },
    { step: 4, label: 'Password', isPassword: true },
  ]

  return (
    <GlassCard padding="p-0" className="overflow-hidden">
      <div className="px-5 py-4 border-b border-[color:var(--border)] flex items-center gap-3">
        <div className="w-7 h-7 rounded-lg flex items-center justify-center flex-shrink-0" style={{ background: 'rgba(155,89,255,0.12)' }}>
          <svg width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="var(--accent)" strokeWidth="2">
            <path strokeLinecap="round" strokeLinejoin="round" d="M9 3H5a2 2 0 00-2 2v4m6-6h10a2 2 0 012 2v4M9 3v18m0 0h10a2 2 0 002-2V9M9 21H5a2 2 0 01-2-2V9m0 0h18"/>
          </svg>
        </div>
        <div>
          <div className="label">Bootstrapper Setup</div>
          <div className="text-[11px] text-[color:var(--text-muted)]">Copy each field into the launcher when prompted</div>
        </div>
      </div>
      <div className="divide-y divide-[color:var(--border)]">
        {rows.map(row => (
          <div key={row.step} className="px-5 py-3.5 flex items-center gap-3">
            <span className="w-5 h-5 rounded-full text-[10px] font-bold flex items-center justify-center flex-shrink-0 text-white" style={{ background: row.isPassword ? 'rgba(255,255,255,0.12)' : 'var(--red)' }}>
              {row.step}
            </span>
            <div className="flex-1 min-w-0">
              <div className="text-[10px] text-[color:var(--text-muted)] uppercase tracking-widest mb-0.5">{row.label}</div>
              {row.isPassword ? (
                <div className="text-xs text-[color:var(--text-dim)] italic">use your account password</div>
              ) : row.isKey && editing ? (
                <div className="flex items-center gap-2 mt-1">
                  <input
                    autoFocus
                    value={draft}
                    onChange={e => setDraft(e.target.value)}
                    onKeyDown={e => { if (e.key === 'Enter') saveKey(); if (e.key === 'Escape') setEditing(false) }}
                    placeholder="COBALT-XXXX-XXXX-…"
                    className="input-field text-xs font-mono px-3 py-1.5 rounded-lg flex-1 min-w-0"
                  />
                  <button onClick={saveKey} className="text-xs px-3 py-1.5 rounded-lg font-semibold" style={{ background: 'var(--red)', color: '#fff' }}>Save</button>
                  <button onClick={() => setEditing(false)} className="text-xs px-2 py-1.5 rounded-lg" style={{ color: 'var(--text-muted)' }}>✕</button>
                </div>
              ) : (
                <code className={`text-xs font-mono ${row.value ? 'text-[color:var(--text)]' : 'text-[color:var(--text-dim)] italic'} break-all`}>
                  {row.value ?? row.placeholder}
                </code>
              )}
            </div>
            {!row.isPassword && !row.isKey && row.value && (
              <CopyButton value={row.value} label="Copy" />
            )}
            {row.isKey && !editing && (
              row.value
                ? <CopyButton value={row.value} label="Copy" />
                : (
                  <button
                    onClick={() => { setDraft(''); setEditing(true) }}
                    className="flex-shrink-0 text-xs px-3 py-1.5 rounded-lg border font-semibold transition-all"
                    style={{ borderColor: 'rgba(155,89,255,0.3)', color: 'var(--accent)' }}
                  >
                    Paste
                  </button>
                )
            )}
            {row.isKey && !editing && row.value && (
              <button
                onClick={() => { setDraft(row.value!); setEditing(true) }}
                className="flex-shrink-0 text-xs text-[color:var(--text-dim)] hover:text-[color:var(--text)] transition-colors"
                title="Edit"
                aria-label="Edit license key"
              >
                <svg width="12" height="12" aria-hidden="true" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2"><path d="M11 5H6a2 2 0 00-2 2v11a2 2 0 002 2h11a2 2 0 002-2v-5m-1.414-9.414a2 2 0 112.828 2.828L11.828 15H9v-2.828l8.586-8.586z"/></svg>
              </button>
            )}
          </div>
        ))}
      </div>
    </GlassCard>
  )
}

function daysUntil(dateStr: string) {
  const diff = new Date(dateStr).getTime() - Date.now()
  return Math.max(0, Math.ceil(diff / 86_400_000))
}

function formatDate(dateStr: string) {
  return new Date(dateStr).toLocaleDateString('en-US', { year: 'numeric', month: 'short', day: 'numeric' })
}

export default function Dashboard() {
  const token = useAuth(s => s.token)
  const authUser = useAuth(s => s.user)
  const { data: profile, loading, error } = useAsync<UserProfile | null>(
    () => (token ? getMe(token) : Promise.resolve(null)),
    [token],
  )

  const hasPlan = Boolean(profile?.plan)
  const planActive = hasPlan && (profile?.planExpiry ? new Date(profile.planExpiry) > new Date() : true)
  const days = profile?.planExpiry ? daysUntil(profile.planExpiry) : 0
  const canSeeDownload = Boolean(token && planActive)

  if (loading) return (
    <div className="flex items-center justify-center h-64">
      <Spinner size={32} />
    </div>
  )

  return (
    <div>
      {/* Header */}
      <div className="mb-8">
        <div className="page-title mb-1">DASHBOARD</div>
        <p className="text-[color:var(--text-muted)] text-sm">
          Welcome back, <span className="text-[color:var(--text)] font-medium">{authUser?.username}</span>
        </p>
      </div>

      {error && (
        <div className="mb-6 text-sm alert-error rounded-xl px-4 py-3">
          {error}
        </div>
      )}

      {/* Top row */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-4">
        {/* Plan status */}
        <GlassCard className="md:col-span-2" padding="p-6">
          <div className="flex items-start justify-between">
            <div>
              <div className="label mb-3">Subscription</div>
              {profile?.plan ? (
                <>
                  <div className="font-display text-4xl tracking-wider text-[color:var(--text)] mb-1">
                    {profile.plan.toUpperCase()}
                  </div>
                  <div className="flex items-center gap-3 mt-3">
                    <span
                      className="text-xs font-semibold px-3 py-1 rounded-full text-white"
                      style={{ background: planActive ? 'var(--red)' : '#999' }}
                    >
                      {planActive ? 'ACTIVE' : 'EXPIRED'}
                    </span>
                    {profile.planExpiry && (
                      <span className="text-sm text-[color:var(--text-muted)]">
                        {planActive ? `${days} days remaining` : `Expired ${formatDate(profile.planExpiry)}`}
                      </span>
                    )}
                  </div>
                  {profile.planExpiry && planActive && (
                    <div className="mt-4">
                      <div className="flex justify-between text-xs text-[color:var(--text-muted)] mb-1.5">
                        <span>Expires {formatDate(profile.planExpiry)}</span>
                        <span>{days}d left</span>
                      </div>
                      <div className="w-full h-1.5 rounded-full overflow-hidden" style={{ background: 'rgba(255,255,255,0.08)' }}>
                        <div
                          className="h-full rounded-full transition-all"
                          style={{
                            width: `${Math.min(100, (days / 30) * 100)}%`,
                            background: days < 7 ? 'var(--warning)' : 'var(--red)',
                          }}
                        />
                      </div>
                    </div>
                  )}
                  {profile.licenseKeyPrefix && (
                    <div className="mt-4 flex items-center gap-3 pt-4 border-t border-[color:var(--border)]">
                      <div className="flex-1 min-w-0">
                        <div className="text-[11px] text-[color:var(--text-muted)] uppercase tracking-widest mb-1">License Key</div>
                        <code className="text-xs font-mono text-[color:var(--text-dim)]">{profile.licenseKeyPrefix}••••••••</code>
                      </div>
                      <CopyButton value={profile.licenseKeyPrefix} label="Copy Key" />
                    </div>
                  )}
                </>
              ) : (
                <div>
                  <div className="text-[color:var(--text-muted)] text-sm mb-4">No active plan</div>
                  <Link
                    to="/redeem"
                    className="btn-red inline-flex items-center gap-2 px-5 py-2.5 rounded-xl text-sm"
                  >
                    Redeem a Key
                    <svg width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2.5"><path d="M5 12h14M12 5l7 7-7 7"/></svg>
                  </Link>
                </div>
              )}
            </div>
            <div
              className="w-12 h-12 rounded-2xl flex items-center justify-center flex-shrink-0 opacity-80"
              style={{ background: 'var(--red-light)' }}
            >
              <svg width="22" height="22" fill="none" viewBox="0 0 24 24" stroke="var(--red)" strokeWidth="1.8">
                <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
              </svg>
            </div>
          </div>
        </GlassCard>

        {/* Account info */}
        <GlassCard padding="p-6">
          <div className="label mb-4">Account</div>
          <div className="space-y-3">
            <div>
              <div className="text-[11px] text-[color:var(--text-muted)] uppercase tracking-widest">Username</div>
              <div className="font-medium text-[color:var(--text)] text-sm mt-0.5">{profile?.username ?? '-'}</div>
            </div>
            <div>
              <div className="text-[11px] text-[color:var(--text-muted)] uppercase tracking-widest">Email</div>
              <div className="font-medium text-[color:var(--text)] text-sm mt-0.5 truncate blur-sensitive">{profile?.email ?? '-'}</div>
            </div>
            <div>
              <div className="text-[11px] text-[color:var(--text-muted)] uppercase tracking-widest">Member since</div>
              <div className="font-medium text-[color:var(--text)] text-sm mt-0.5">
                {profile?.createdAt ? formatDate(profile.createdAt) : '-'}
              </div>
            </div>
          </div>
        </GlassCard>
      </div>

      {/* Quick actions */}
      <GlassCard padding="p-5" className="mb-4">
        <div className="label mb-4">Quick Actions</div>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
          {[
            { to: '/redeem', label: 'Redeem Key', icon: '◈' },
            ...(canSeeDownload ? [{ to: '/download', label: 'Download', icon: '↓' }] : []),
            { to: '/stats', label: 'View Stats', icon: '▣' },
            { to: '/device', label: 'Device Info', icon: '◧' },
          ].map(({ to, label, icon }) => (
            <Link
              key={to}
              to={to}
              className="glass-dark rounded-xl px-4 py-3.5 flex items-center gap-3 transition-all group hover:border-[color:var(--border-2)]"
            >
              <span className="text-lg text-[color:var(--text-muted)] group-hover:text-[color:var(--accent)] transition-colors">{icon}</span>
              <span className="text-sm font-medium text-[color:var(--text-muted)] group-hover:text-[color:var(--text)]">{label}</span>
            </Link>
          ))}
        </div>
      </GlassCard>

      {profile && <BootstrapSetupCard profile={profile} />}
    </div>
  )
}
