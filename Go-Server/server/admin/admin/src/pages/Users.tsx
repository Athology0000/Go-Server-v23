import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getUsers, banUser, unbanUser, type UserRecord } from '../api/admin'
import { useAuth } from '../store/auth'
import { useToast } from '../store/toast'
import { useAsync } from '../hooks/useAsync'
import GlassCard from '../components/GlassCard'
import Badge from '../components/Badge'
import Spinner from '../components/Spinner'
import Modal from '../components/Modal'

function getUserStatus(u: UserRecord): 'active' | 'banned' | 'expired' {
  if (u.banned) return 'banned'
  if (!u.plan || !u.planExpiry || new Date(u.planExpiry) < new Date()) return 'expired'
  return 'active'
}

function fmtDate(d: string | null) {
  if (!d) return '-'
  return new Date(d).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

const PAGE_SIZE = 25

export default function Users() {
  const token = useAuth(s => s.token)!
  const navigate = useNavigate()

  const [search, setSearch] = useState('')
  const [debounced, setDebounced] = useState('')
  const [page, setPage] = useState(0)

  const [banModal, setBanModal] = useState<{ user: UserRecord } | null>(null)
  const [banReason, setBanReason] = useState('')
  const [actionLoading, setActionLoading] = useState<string | null>(null)
  const [actionError, setActionError] = useState('')
  const showToast = useToast(s => s.show)

  // Debounce the search box so we don't refetch on every keystroke.
  useEffect(() => {
    const t = setTimeout(() => setDebounced(search), 300)
    return () => clearTimeout(t)
  }, [search])

  // Reset to the first page whenever the search query changes.
  useEffect(() => { setPage(0) }, [debounced])

  const { data, loading, error, reload } = useAsync(
    () => getUsers(debounced, token),
    [debounced, token],
  )
  const users = data ?? []
  const totalPages = Math.max(1, Math.ceil(users.length / PAGE_SIZE))
  const safePage = Math.min(page, totalPages - 1)
  const pageUsers = users.slice(safePage * PAGE_SIZE, safePage * PAGE_SIZE + PAGE_SIZE)

  const handleBan = async () => {
    if (!banModal) return
    setActionLoading(banModal.user.id)
    setActionError('')
    try {
      await banUser(banModal.user.id, banReason, token)
      showToast('success', `${banModal.user.username} banned`)
      setBanModal(null)
      setBanReason('')
      reload()
    } catch (e) {
      setActionError(e instanceof Error ? e.message : 'Action failed')
    } finally {
      setActionLoading(null)
    }
  }

  const handleUnban = async (user: UserRecord) => {
    setActionLoading(user.id)
    setActionError('')
    try {
      await unbanUser(user.id, token)
      showToast('success', `${user.username} unbanned`)
      reload()
    } catch (e) {
      setActionError(e instanceof Error ? e.message : 'Action failed')
    } finally {
      setActionLoading(null)
    }
  }

  const shownError = error || actionError

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
            aria-hidden="true"
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
            aria-label="Search users by username or email"
            className="input-field pl-10 pr-4 py-2.5 rounded-xl w-72"
          />
        </div>
      </div>

      {shownError && <div className="mb-4 text-sm alert-error rounded-xl px-4 py-3" role="alert">{shownError}</div>}

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
                      scope="col"
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
                  pageUsers.map(u => (
                    <tr
                      key={u.id}
                      className="border-b border-[color:var(--border)] last:border-0 hover:bg-[var(--surface-2)] transition-colors"
                    >
                      <td className="py-3.5 px-5">
                        <button
                          onClick={() => navigate(`/users/${u.id}`)}
                          className="flex items-center gap-2.5 group"
                          aria-label={`View details for ${u.username}`}
                        >
                          <div
                            className="w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold text-white flex-shrink-0"
                            style={{ background: u.banned ? '#999' : 'var(--red)' }}
                            aria-hidden="true"
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
                            aria-label={`Unban ${u.username}`}
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
                            aria-label={`Ban ${u.username}`}
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

      {!loading && users.length > PAGE_SIZE && (
        <div className="flex items-center justify-between mt-4 text-sm text-[color:var(--text-muted)]">
          <span>
            Showing {safePage * PAGE_SIZE + 1}–{Math.min(users.length, safePage * PAGE_SIZE + PAGE_SIZE)} of {users.length}
          </span>
          <div className="flex gap-2">
            <button
              disabled={safePage === 0}
              onClick={() => setPage(p => Math.max(0, p - 1))}
              className="btn-ghost px-3 py-1.5 rounded-lg text-xs disabled:opacity-40"
            >
              Prev
            </button>
            <button
              disabled={safePage >= totalPages - 1}
              onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
              className="btn-ghost px-3 py-1.5 rounded-lg text-xs disabled:opacity-40"
            >
              Next
            </button>
          </div>
        </div>
      )}

      <Modal open={!!banModal} onClose={() => setBanModal(null)} title="BAN USER">
        {banModal && (
          <>
            <p className="text-sm text-[color:var(--text-muted)] mb-5">
              Banning <span className="font-semibold text-[color:var(--text)]">{banModal.user.username}</span> will immediately revoke their access.
            </p>

            <div className="mb-5">
              <label className="label" htmlFor="ban-reason">Reason</label>
              <input
                id="ban-reason"
                type="text"
                value={banReason}
                onChange={e => setBanReason(e.target.value)}
                onKeyDown={e => { if (e.key === 'Enter' && banReason.trim() && !actionLoading) handleBan() }}
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
          </>
        )}
      </Modal>
    </div>
  )
}
