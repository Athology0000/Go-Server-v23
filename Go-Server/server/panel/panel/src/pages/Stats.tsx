import { getStats } from '../api/user'
import { useAuth } from '../store/auth'
import { useAsync } from '../hooks/useAsync'
import GlassCard from '../components/GlassCard'
import Spinner from '../components/Spinner'

function fmtRuntime(seconds: number) {
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  if (h > 0) return `${h}h ${m}m`
  return `${m}m`
}

function fmtNum(n: number) {
  if (n >= 1_000_000) return `${(n / 1_000_000).toFixed(1)}M`
  if (n >= 1_000) return `${(n / 1_000).toFixed(1)}K`
  return String(n)
}

interface StatCardProps {
  label: string
  value: string
  sub?: string
  icon: React.ReactNode
  accent?: boolean
}

function StatCard({ label, value, sub, icon, accent }: StatCardProps) {
  return (
    <GlassCard padding="p-5">
      <div className="flex items-start justify-between mb-3">
        <div
          className="w-10 h-10 rounded-xl flex items-center justify-center"
          style={{ background: accent ? 'var(--red-light)' : 'var(--surface-2)' }}
        >
          {icon}
        </div>
      </div>
      <div className="stat-value" style={{ color: accent ? 'var(--red)' : 'var(--text)' }}>
        {value}
      </div>
      <div className="text-xs text-[color:var(--text-muted)] uppercase tracking-widest mt-1">{label}</div>
      {sub && <div className="text-xs text-[color:var(--text-muted)] mt-0.5">{sub}</div>}
    </GlassCard>
  )
}

export default function Stats() {
  const token = useAuth(s => s.token)!
  const { data: stats, loading, error } = useAsync(() => getStats(token), [token])

  if (loading) return (
    <div className="flex items-center justify-center h-64"><Spinner size={32} /></div>
  )

  return (
    <div>
      <div className="mb-8">
        <div className="page-title mb-1">MY STATS</div>
        <p className="text-[color:var(--text-muted)] text-sm">
          {stats?.lastSeen
            ? `Last session: ${new Date(stats.lastSeen).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })}`
            : 'No sessions recorded yet'}
        </p>
      </div>

      {error && (
        <div className="mb-6 text-sm alert-error rounded-xl px-4 py-3">{error}</div>
      )}

      <div className="grid grid-cols-2 md:grid-cols-3 gap-4">
        <StatCard
          label="Total Sessions"
          value={fmtNum(stats?.totalSessions ?? 0)}
          accent
          icon={
            <svg width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="var(--red)" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
            </svg>
          }
        />
        <StatCard
          label="Total Runtime"
          value={fmtRuntime(stats?.totalRuntimeSeconds ?? 0)}
          icon={
            <svg width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="var(--text-muted)" strokeWidth="2">
              <circle cx="12" cy="12" r="10"/><path strokeLinecap="round" d="M12 6v6l4 2"/>
            </svg>
          }
        />
        <StatCard
          label="Coins Earned"
          value={fmtNum(stats?.totalCoins ?? 0)}
          icon={
            <svg width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="var(--warning)" strokeWidth="2">
              <circle cx="12" cy="12" r="10"/><path strokeLinecap="round" strokeLinejoin="round" d="M12 8v8m-3-4h6"/>
            </svg>
          }
        />
        <StatCard
          label="Fish Caught"
          value={fmtNum(stats?.totalFish ?? 0)}
          icon={
            <svg width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="var(--capture)" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M21 12c0 4.418-4.03 8-9 8a9.863 9.863 0 01-4.255-.949L3 20l1.395-3.72C3.512 15.042 3 13.574 3 12c0-4.418 4.03-8 9-8s9 3.582 9 8z"/>
            </svg>
          }
        />
        <StatCard
          label="Drops Received"
          value={fmtNum(stats?.totalDrops ?? 0)}
          icon={
            <svg width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="var(--accent2)" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M5 3l14 9-14 9V3z"/>
            </svg>
          }
        />
      </div>
    </div>
  )
}
