import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getUser, banUser, unbanUser, addTime, upgradePlan, type UserRecord } from '../api/admin'
import { useAuth } from '../store/auth'
import GlassCard from '../components/GlassCard'
import Badge from '../components/Badge'
import Spinner from '../components/Spinner'
import Modal from '../components/Modal'

const PLANS = ['starter', 'pro', 'lifetime'] as const
const DURATIONS = [
  { label: '7 days', value: 7 },
  { label: '30 days', value: 30 },
  { label: '90 days', value: 90 },
  { label: '180 days', value: 180 },
  { label: '365 days', value: 365 },
]

function Row({ label, value, mono, sensitive }: { label: string; value: string; mono?: boolean; sensitive?: boolean }) {
  return (
    <div className="flex items-center justify-between py-3 border-b border-[color:var(--border)] last:border-0 gap-4">
      <span className="text-xs text-[color:var(--text-dim)] uppercase tracking-widest">{label}</span>
      <span className={`text-sm font-medium text-[color:var(--text)] text-right max-w-xs break-all ${mono ? 'font-mono text-xs' : ''} ${sensitive ? 'blur-sensitive' : ''}`}>
        {value}
      </span>
    </div>
  )
}

function getStatus(u: UserRecord): 'active' | 'banned' | 'expired' {
  if (u.banned) return 'banned'
  if (!u.plan || !u.planExpiry || new Date(u.planExpiry) < new Date()) return 'expired'
  return 'active'
}

function fmtDate(d: string | null) {
  if (!d) return '-'
  return new Date(d).toLocaleString('en-US', {
    month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit',
  })
}

export default function UserDetail() {
  const { id } = useParams<{ id: string }>()
  const token = useAuth(s => s.token)!
  const navigate = useNavigate()

  const [user, setUser] = useState<UserRecord | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')
  const [actionLoading, setActionLoading] = useState(false)

  const [banModal, setBanModal] = useState(false)
  const [banReason, setBanReason] = useState('')

  const [addTimeModal, setAddTimeModal] = useState(false)
  const [selectedDays, setSelectedDays] = useState(30)
  const [addTimePlan, setAddTimePlan] = useState<(typeof PLANS)[number]>('pro')

  const [upgradeModal, setUpgradeModal] = useState(false)
  const [upgradeTo, setUpgradeTo] = useState<(typeof PLANS)[number]>('pro')

  const load = () => {
    setLoading(true)
    getUser(id!, token)
      .then(u => { setUser(u); setAddTimePlan((u.plan as typeof PLANS[number]) ?? 'pro') })
      .catch(e => setError(e instanceof Error ? e.message : 'Not found'))
      .finally(() => setLoading(false))
  }

  // eslint-disable-next-line react-hooks/exhaustive-deps
  useEffect(() => { load() }, [id])

  const handleBan = async () => {
    if (!user) return
    setActionLoading(true)
    try {
      await banUser(user.id, banReason, token)
      setBanModal(false)
      setBanReason('')
      load()
    } catch (e) { setError(e instanceof Error ? e.message : 'Failed') }
    finally { setActionLoading(false) }
  }

  const handleUnban = async () => {
    if (!user) return
    setActionLoading(true)
    try { await unbanUser(user.id, token); load() }
    catch (e) { setError(e instanceof Error ? e.message : 'Failed') }
    finally { setActionLoading(false) }
  }

  const handleAddTime = async () => {
    if (!user) return
    setActionLoading(true)
    try {
      await addTime(user.id, selectedDays, addTimePlan, token)
      setAddTimeModal(false)
      load()
    } catch (e) { setError(e instanceof Error ? e.message : 'Failed') }
    finally { setActionLoading(false) }
  }

  const handleUpgrade = async () => {
    if (!user) return
    setActionLoading(true)
    try {
      await upgradePlan(user.id, upgradeTo, token)
      setUpgradeModal(false)
      load()
    } catch (e) { setError(e instanceof Error ? e.message : 'Failed') }
    finally { setActionLoading(false) }
  }

  if (loading) return <div className="flex items-center justify-center h-64"><Spinner size={32} /></div>
  if (!user) return <div className="alert-error rounded-xl px-4 py-3" role="alert">{error || 'User not found'}</div>

  return (
    <div>
      <button
        onClick={() => navigate('/users')}
        className="flex items-center gap-2 text-sm text-[color:var(--text-muted)] hover:text-[color:var(--text)] mb-6 transition-colors"
      >
        <svg width="16" height="16" aria-hidden="true" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
          <path d="M19 12H5M12 5l-7 7 7 7" />
        </svg>
        Back to Users
      </button>

      <div className="flex items-start justify-between mb-6 flex-wrap gap-4">
        <div className="flex items-center gap-4">
          <div
            className="w-14 h-14 rounded-2xl flex items-center justify-center text-2xl font-bold text-white shadow-lg flex-shrink-0"
            style={{ background: user.banned ? '#999' : 'var(--red)' }}
            aria-hidden="true"
          >
            {user.username[0].toUpperCase()}
          </div>
          <div>
            <div className="page-title leading-none">{user.username}</div>
            <div className="text-[color:var(--text-muted)] text-sm mt-1 blur-sensitive">{user.email ?? '-'}</div>
            <div className="mt-2"><Badge status={getStatus(user)} /></div>
          </div>
        </div>

        <div className="flex gap-2 flex-wrap">
          <button
            onClick={() => setAddTimeModal(true)}
            className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold border transition-all"
            style={{ color: 'var(--capture)', borderColor: 'rgba(77,200,255,0.22)' }}
            onMouseEnter={e => (e.currentTarget.style.background = 'rgba(77,200,255,0.07)')}
            onMouseLeave={e => (e.currentTarget.style.background = 'transparent')}
          >
            <svg width="14" height="14" aria-hidden="true" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2.5">
              <circle cx="12" cy="12" r="10"/><path d="M12 6v6l4 2"/>
            </svg>
            Add Time
          </button>

          <button
            onClick={() => { setUpgradeTo((user.plan as typeof PLANS[number]) ?? 'pro'); setUpgradeModal(true) }}
            className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold border transition-all"
            style={{ color: 'var(--accent2)', borderColor: 'rgba(192,132,252,0.22)' }}
            onMouseEnter={e => (e.currentTarget.style.background = 'rgba(192,132,252,0.07)')}
            onMouseLeave={e => (e.currentTarget.style.background = 'transparent')}
          >
            <svg width="14" height="14" aria-hidden="true" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2.5">
              <path d="M5 15l7-7 7 7"/>
            </svg>
            Upgrade Plan
          </button>

          {user.banned ? (
            <button
              onClick={handleUnban}
              disabled={actionLoading}
              className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm font-semibold border transition-all"
              style={{ color: 'var(--good)', borderColor: 'rgba(0,255,163,0.22)' }}
              onMouseEnter={e => (e.currentTarget.style.background = 'rgba(0,255,163,0.07)')}
              onMouseLeave={e => (e.currentTarget.style.background = 'transparent')}
            >
              {actionLoading ? <Spinner size={14} color="var(--good)" /> : null}
              Unban User
            </button>
          ) : (
            <button
              onClick={() => { setBanModal(true); setBanReason('') }}
              className="btn-danger flex items-center gap-2 px-4 py-2.5 rounded-xl text-sm"
            >
              Ban User
            </button>
          )}
        </div>
      </div>

      {error && <div className="mb-4 text-sm alert-error rounded-xl px-4 py-3" role="alert">{error}</div>}

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
        <GlassCard padding="p-5">
          <div className="label mb-3">Account Details</div>
          <Row label="User ID" value={user.id} mono />
          <Row label="Username" value={user.username} />
          <Row label="Email" value={user.email ?? '-'} sensitive />
          <Row label="Joined" value={fmtDate(user.createdAt)} />
          <Row label="Last Seen" value={fmtDate(user.lastSeen)} />
        </GlassCard>

        <GlassCard padding="p-5">
          <div className="label mb-3">Subscription</div>
          <Row label="Plan" value={user.plan ?? 'None'} />
          <Row label="Expiry" value={fmtDate(user.planExpiry)} />
          <Row label="Status" value={getStatus(user).charAt(0).toUpperCase() + getStatus(user).slice(1)} />
        </GlassCard>

        <GlassCard padding="p-5">
          <div className="label mb-3">Device Binding</div>
          <Row label="HWID" value={user.hwid ?? 'Not bound'} mono />
          <Row label="Bound" value={user.hwidBound ? 'Yes' : 'No'} />
          <Row label="MC Username" value={user.minecraftUsername ?? '-'} sensitive />
        </GlassCard>

        {user.banned && (
          <GlassCard padding="p-5">
            <div className="label mb-3">Ban Info</div>
            <Row label="Status" value="Banned" />
            <Row label="Reason" value={user.bannedReason ?? 'No reason provided'} />
          </GlassCard>
        )}
      </div>

      {/* Add Time Modal */}
      <Modal open={addTimeModal} onClose={() => setAddTimeModal(false)} title="ADD TIME">
        <p className="text-sm text-[color:var(--text-muted)] mb-5">
          Extend subscription for <strong className="text-[color:var(--text)]">{user.username}</strong>
        </p>

        <div className="mb-4">
          <label className="label">Duration</label>
          <div className="flex flex-wrap gap-2">
            {DURATIONS.map(d => (
              <button
                key={d.value}
                type="button"
                onClick={() => setSelectedDays(d.value)}
                className="px-3 py-1.5 rounded-lg text-xs font-semibold transition-all border"
                style={selectedDays === d.value
                  ? { background: 'var(--capture)', color: '#000', borderColor: 'transparent' }
                  : { background: 'var(--surface)', color: 'var(--text-muted)', borderColor: 'var(--border)' }}
              >
                {d.label}
              </button>
            ))}
          </div>
        </div>

        <div className="mb-5">
          <label className="label">Plan (if no active plan)</label>
          <div className="flex gap-2">
            {PLANS.map(p => (
              <button
                key={p}
                type="button"
                onClick={() => setAddTimePlan(p)}
                className="px-3 py-1.5 rounded-lg text-xs font-semibold transition-all capitalize border"
                style={addTimePlan === p
                  ? { background: 'var(--red)', color: '#fff', borderColor: 'transparent' }
                  : { background: 'var(--surface)', color: 'var(--text-muted)', borderColor: 'var(--border)' }}
              >
                {p}
              </button>
            ))}
          </div>
        </div>

        <div className="flex gap-3">
          <button onClick={() => setAddTimeModal(false)} className="btn-ghost flex-1 py-2.5 rounded-xl text-sm">
            Cancel
          </button>
          <button
            onClick={handleAddTime}
            disabled={actionLoading}
            className="flex-1 py-2.5 rounded-xl text-sm font-semibold flex items-center justify-center gap-2 transition-all"
            style={{ background: 'var(--capture)', color: '#000' }}
          >
            {actionLoading ? <Spinner size={16} color="#000" /> : `Add ${selectedDays} Days`}
          </button>
        </div>
      </Modal>

      {/* Upgrade Plan Modal */}
      <Modal open={upgradeModal} onClose={() => setUpgradeModal(false)} title="UPGRADE PLAN">
        <p className="text-sm text-[color:var(--text-muted)] mb-5">
          Change plan for <strong className="text-[color:var(--text)]">{user.username}</strong>
        </p>

        <div className="mb-5">
          <label className="label">Select Plan</label>
          <div className="flex gap-2">
            {PLANS.map(p => (
              <button
                key={p}
                type="button"
                onClick={() => setUpgradeTo(p)}
                className="flex-1 py-3 rounded-xl text-sm font-semibold transition-all capitalize border"
                style={upgradeTo === p
                  ? { background: 'var(--accent2)', color: '#fff', borderColor: 'transparent' }
                  : { background: 'var(--surface)', color: 'var(--text-muted)', borderColor: 'var(--border)' }}
              >
                {p}
              </button>
            ))}
          </div>
        </div>

        <div className="flex gap-3">
          <button onClick={() => setUpgradeModal(false)} className="btn-ghost flex-1 py-2.5 rounded-xl text-sm">
            Cancel
          </button>
          <button
            onClick={handleUpgrade}
            disabled={actionLoading}
            className="flex-1 py-2.5 rounded-xl text-sm font-semibold flex items-center justify-center gap-2 transition-all"
            style={{ background: 'var(--accent2)', color: '#fff' }}
          >
            {actionLoading ? <Spinner size={16} color="white" /> : `Set to ${upgradeTo}`}
          </button>
        </div>
      </Modal>

      {/* Ban Modal */}
      <Modal open={banModal} onClose={() => setBanModal(false)} title="BAN USER">
        <p className="text-sm text-[color:var(--text-muted)] mb-5">
          Banning <strong className="text-[color:var(--text)]">{user.username}</strong> will revoke all access immediately.
        </p>
        <div className="mb-5">
          <label className="label" htmlFor="detail-ban-reason">Reason</label>
          <input
            id="detail-ban-reason"
            type="text"
            value={banReason}
            onChange={e => setBanReason(e.target.value)}
            onKeyDown={e => { if (e.key === 'Enter' && banReason.trim() && !actionLoading) handleBan() }}
            className="input-field px-4 py-3 rounded-xl"
            placeholder="Reason for ban"
            autoFocus
          />
        </div>
        <div className="flex gap-3">
          <button onClick={() => setBanModal(false)} className="btn-ghost flex-1 py-2.5 rounded-xl text-sm">Cancel</button>
          <button
            onClick={handleBan}
            disabled={!banReason.trim() || actionLoading}
            className="btn-red flex-1 py-2.5 rounded-xl text-sm flex items-center justify-center gap-2"
          >
            {actionLoading ? <Spinner size={16} color="white" /> : 'Confirm Ban'}
          </button>
        </div>
      </Modal>
    </div>
  )
}
