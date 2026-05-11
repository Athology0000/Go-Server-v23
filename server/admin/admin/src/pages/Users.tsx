import { useEffect, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { getUsers, banUser, unbanUser, type UserRecord } from '../api/admin'
import { useAuth } from '../store/auth'
import GlassCard from '../components/GlassCard'
import Badge from '../components/Badge'
import Spinner from '../components/Spinner'

function getUserStatus(u: UserRecord): 'active' | 'banned' | 'expired' {
  if (u.banned) return 'banned'
  if (!u.plan || !u.planExpiry || new Date(u.planExpiry) < new Date()) return 'expired'
  return 'active'
}

function fmtDate(d: string | null) {
  if (!d) return '-'
  return new Date(d).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

export default function Users() {
  const token = useAuth(s => s.token)!
  const navigate = useNavigate()

  const [users, setUsers] = useState<UserRecord[]>([])
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const [banModal, setBanModal] = useState<{ user: UserRecord } | null>(null)
  const [banReason, setBanReason] = useState('')
  const [actionLoading, setActionLoading] = useState<string | null>(null)

  const load = useCallback(() => {
    setLoading(true)
    setError('')
    getUsers(search, token)
      .then(setUsers)
      .catch(e => setError(e instanceof Error ? e.message : 'Failed to load'))
      .finally(() => setLoading(false))
  }, [search, token])

  useEffect(() => {
    const t = setTimeout(load, 300)
    return () => clearTimeout(t)
  }, [load])

  const handleBan = async () => {
    if (!banModal) return
    setActionLoading(banModal.user.id)
    try {
      await banUser(banModal.user.id, banReason, token)
      setBanModal(null)
      setBanReason('')
      load()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Action failed')
    } finally {
      setActionLoading(null)
    }
  }

  const handleUnban = async (user: UserRecord) => {
    setActionLoading(user.id)
    try {
      await unbanUser(user.id, token)
      load()
    } catch (e) {
      setError(e instanceof Error ? e.message : 'Action failed')
    } finally {
      setActionLoading(null)
    }
  }

  return (
    <div>
      <div className="flex items-end justify-between mb-6 gap-4 flex-wrap">
        <div>
          <div className="page-title mb-1">USERS</div>
          <p className="text-[color:var(--text-muted)] text-sm">
            {users.length} member{users.length !== 1 ? 's' : ''} found
          </p>
        </div>

        <div className="relative">
          <svg
            width="16"
            height="16"
            className="absolute left-3.5 top-1/2 -translate-y-1/2 text-[color:var(--text-muted)]"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
            strokeWidth="2"
          >
            <circle cx="11" cy="11" r="8" />
            <path d="M21 21l-4.35-4.35" />
          </svg>
          <input
            type="text"
            value={search}
            onChange={e => setSearch(e.target.value)}
            placeholder="Search username or email..."
            className="input-field pl-10 pr-4 py-2.5 rounded-xl w-72"
          />
        </div>
      </div>

      {error && <div className="mb-4 text-sm alert-error rounded-xl px-4 py-3">{error}</div>}

      <GlassCard padding="p-0">
        {loading ? (
          <div className="flex items-center justify-center h-48">
            <Spinner size={28} />
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-[color:var(--border)]">
                  {['User', 'Email', 'Plan', 'Expiry', 'Last Seen', 'Status', 'Actions'].map(h => (
                    <th
                      key={h}
                      className="text-left py-3.5 px-5 text-[10px] font-semibold text-[color:var(--text-dim)] uppercase tracking-widest first:rounded-tl-2xl"
                    >
                      {h}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {users.length === 0 ? (
                  <tr>
                    <td colSpan={7} className="py-12 text-center text-[color:var(--text-muted)] text-sm">
                      No users found
                    </td>
                  </tr>
                ) : (
                  users.map(u => (
                    <tr
                      key={u.id}
                      className="border-b border-[color:var(--border)] last:border-0 hover:bg-[var(--surface-2)] transition-colors"
                    >
                      <td className="py-3.5 px-5">
                        <button onClick={() => navigate(`/users/${u.id}`)} className="flex items-center gap-2.5 group">
                          <div
                            className="w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold text-white flex-shrink-0"
                            style={{ background: u.banned ? '#999' : 'var(--red)' }}
                          >
                            {u.username[0].toUpperCase()}
                          </div>
                          <span className="text-sm font-medium text-[color:var(--text)] group-hover:text-white">
                            {u.username}
                          </span>
                        </button>
                      </td>

                      <td className="py-3.5 px-5 text-sm text-[color:var(--text-muted)]"><span className="blur-sensitive">{u.email ?? '-'}</span></td>

                      <td className="py-3.5 px-5">
                        {u.plan ? (
                          <span className="text-xs font-bold px-2 py-0.5 rounded text-white" style={{ background: 'var(--red)' }}>
                            {u.plan.toUpperCase()}
                          </span>
                        ) : (
                          <span className="text-[color:var(--text-dim)] text-sm">-</span>
                        )}
                      </td>

                      <td className="py-3.5 px-5 text-sm text-[color:var(--text-muted)]">{fmtDate(u.planExpiry)}</td>
                      <td className="py-3.5 px-5 text-sm text-[color:var(--text-muted)]">{fmtDate(u.lastSeen)}</td>

                      <td className="py-3.5 px-5">
                        <Badge status={getUserStatus(u)} />
                      </td>

                      <td className="py-3.5 px-5">
                        {actionLoading === u.id ? (
                          <Spinner size={16} />
                        ) : u.banned ? (
                          <button
                            onClick={() => handleUnban(u)}
                            className="text-xs font-semibold px-3 py-1.5 rounded-lg transition-all hover:bg-[rgba(0,255,163,0.08)]"
                            style={{ color: 'var(--good)' }}
                          >
                            Unban
                          </button>
                        ) : (
                          <button
                            onClick={() => {
                              setBanModal({ user: u })
                              setBanReason('')
                            }}
                            className="text-xs font-semibold px-3 py-1.5 rounded-lg transition-all"
                            style={{ color: 'var(--red)' }}
                            onMouseEnter={e => (e.currentTarget.style.background = 'var(--red-light)')}
                            onMouseLeave={e => (e.currentTarget.style.background = 'transparent')}
                          >
                            Ban
                          </button>
                        )}
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>
        )}
      </GlassCard>

      {banModal && (
        <div
          className="fixed inset-0 flex items-center justify-center z-50 p-4"
          style={{ background: 'rgba(0,0,0,0.3)', backdropFilter: 'blur(4px)' }}
        >
          <div className="glass rounded-2xl p-7 w-full max-w-md shadow-2xl">
            <h3 className="font-display text-xl tracking-wider text-[color:var(--text)] mb-1">BAN USER</h3>
            <p className="text-sm text-[color:var(--text-muted)] mb-5">
              Banning <span className="font-semibold text-[color:var(--text)]">{banModal.user.username}</span> will immediately revoke their access.
            </p>

            <div className="mb-5">
              <label className="label">Reason</label>
              <input
                type="text"
                value={banReason}
                onChange={e => setBanReason(e.target.value)}
                className="input-field px-4 py-3 rounded-xl"
                placeholder="e.g. Terms of service violation"
                autoFocus
              />
            </div>

            <div className="flex gap-3">
              <button onClick={() => setBanModal(null)} className="btn-ghost flex-1 py-2.5 rounded-xl text-sm">
                Cancel
              </button>
              <button
                onClick={handleBan}
                disabled={!banReason.trim() || !!actionLoading}
                className="btn-red flex-1 py-2.5 rounded-xl text-sm flex items-center justify-center gap-2"
              >
                {actionLoading ? <Spinner size={16} color="white" /> : 'Confirm Ban'}
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

