import { useState } from 'react'
import { getLeaderboard } from '../api/user'
import { useAuth } from '../store/auth'
import { useAsync } from '../hooks/useAsync'
import GlassCard from '../components/GlassCard'
import Spinner from '../components/Spinner'

const TABS: { key: string; label: string }[] = [
  { key: 'coins', label: 'Coins' },
  { key: 'fish', label: 'Fish' },
  { key: 'drops', label: 'Drops' },
  { key: 'runtime', label: 'Runtime' },
]

function fmtVal(val: number, stat: string) {
  if (stat === 'runtime') {
    const h = Math.floor(val / 3600)
    return `${h}h`
  }
  if (val >= 1_000_000) return `${(val / 1_000_000).toFixed(2)}M`
  if (val >= 1_000) return `${(val / 1_000).toFixed(1)}K`
  return String(val)
}

const medalColors: Record<number, string> = {
  1: '#f59e0b',
  2: '#9ca3af',
  3: '#b45309',
}

export default function Leaderboard() {
  const token = useAuth(s => s.token)!
  const authUser = useAuth(s => s.user)
  const [tab, setTab] = useState('coins')
  const { data: rows, loading, error } = useAsync(() => getLeaderboard(tab, token), [tab, token])
  const data = rows ?? []

  return (
    <div>
      <div className="mb-8">
        <div className="page-title mb-1">LEADERBOARD</div>
        <p className="text-[color:var(--text-muted)] text-sm">Top performers across all categories</p>
      </div>

      {/* Tabs */}
      <div className="flex gap-1.5 mb-5 p-1 glass rounded-xl w-fit">
        {TABS.map(t => (
          <button
            key={t.key}
            onClick={() => setTab(t.key)}
            className={`px-4 py-2 rounded-lg text-sm font-medium transition-all ${
              tab === t.key
                ? 'text-white shadow-sm'
                : 'text-[color:var(--text-muted)] hover:text-[color:var(--text)]'
            }`}
            style={tab === t.key ? { background: 'var(--red)' } : {}}
          >
            {t.label}
          </button>
        ))}
      </div>

      <GlassCard padding="p-0">
        {loading ? (
          <div className="flex items-center justify-center h-48"><Spinner size={28} /></div>
        ) : error ? (
          <div className="p-6 text-sm alert-error">{error}</div>
        ) : data.length === 0 ? (
          <div className="p-12 text-center text-[color:var(--text-muted)] text-sm">No data yet</div>
        ) : (
          <table className="w-full">
            <thead>
              <tr className="border-b border-[color:var(--border)]">
                <th scope="col" className="text-left py-3.5 px-5 text-[10px] font-semibold text-[color:var(--text-muted)] uppercase tracking-widest w-12">#</th>
                <th scope="col" className="text-left py-3.5 px-4 text-[10px] font-semibold text-[color:var(--text-muted)] uppercase tracking-widest">Player</th>
                <th scope="col" className="text-right py-3.5 px-5 text-[10px] font-semibold text-[color:var(--text-muted)] uppercase tracking-widest">{TABS.find(t => t.key === tab)?.label}</th>
              </tr>
            </thead>
            <tbody>
              {data.map((entry) => {
                const isMe = authUser?.username === entry.username
                return (
                  <tr
                    key={entry.rank}
                    className={`border-b border-[color:var(--border)] last:border-0 transition-colors ${isMe ? '' : 'hover:bg-[rgba(255,255,255,0.03)]'}`}
                    style={isMe ? { background: 'var(--red-light)' } : {}}
                  >
                    <td className="py-3.5 px-5">
                      {entry.rank <= 3 ? (
                        <span className="text-lg" style={{ color: medalColors[entry.rank] }}>
                          {entry.rank === 1 ? '🥇' : entry.rank === 2 ? '🥈' : '🥉'}
                        </span>
                      ) : (
                        <span className="text-sm font-mono text-[color:var(--text-muted)]">{entry.rank}</span>
                      )}
                    </td>
                    <td className="py-3.5 px-4">
                      <div className="flex items-center gap-2.5">
                        <div className="w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold text-white flex-shrink-0"
                          style={{ background: isMe ? 'var(--red)' : 'rgba(255,255,255,0.10)' }}
                        >
                          {entry.username[0].toUpperCase()}
                        </div>
                        <span className={`text-sm font-medium ${isMe ? 'text-[color:var(--text)]' : 'text-[color:var(--text)]'}`}>
                          {entry.username}
                          {isMe && <span className="ml-1.5 text-[10px] font-bold text-white px-1.5 py-0.5 rounded" style={{ background: 'var(--red)' }}>YOU</span>}
                        </span>
                      </div>
                    </td>
                    <td className="py-3.5 px-5 text-right">
                      <span className="font-display text-lg tracking-wider text-[color:var(--text)]">{fmtVal(entry.value, tab)}</span>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        )}
      </GlassCard>
    </div>
  )
}
