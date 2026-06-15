import { useEffect, useRef, useState, useCallback } from 'react'
import GlassCard from '../components/GlassCard'
import Spinner from '../components/Spinner'
import { getActivityLog, getAuditLog, getServerLogs } from '../api/admin'
import { useAuth } from '../store/auth'
import { useAsync } from '../hooks/useAsync'

function fmtTs(value: string) {
  return new Date(value).toLocaleString()
}

function renderDetails(details: Record<string, unknown> | null) {
  if (!details || Object.keys(details).length === 0) return ''
  try { return JSON.stringify(details) } catch { return '' }
}

function eventColor(type: string): string {
  if (type.includes('.success')) return 'good'
  if (type.includes('.fail') || type.includes('suspended')) return 'mistake'
  if (type.includes('ban') || type.includes('revoke')) return 'warning'
  return ''
}

function eventLabel(type: string): string {
  const map: Record<string, string> = {
    'panel.login.success': 'Panel Login',
    'panel.login.fail': 'Panel Login Failed',
    'auth.start.success': 'Auth Started',
    'auth.start.fail': 'Auth Start Failed',
    'auth.finish.success': 'Authenticated',
    'auth.finish.fail': 'Auth Failed',
    'auth.device.suspended': 'Device Suspended',
    'panel.key.redeem.success': 'Key Redeemed',
  }
  return map[type] ?? type
}

// ── Activity Log (regular admins) ─────────────────────────────────────────────

function ActivityLog() {
  const token = useAuth(s => s.token)!
  const bodyRef = useRef<HTMLDivElement>(null)
  const [limit, setLimit] = useState(120)
  const { data, loading, error } = useAsync(() => getActivityLog(token, limit), [token, limit])
  const rows = data ?? []

  useEffect(() => {
    if (bodyRef.current) bodyRef.current.scrollTop = bodyRef.current.scrollHeight
  }, [rows, loading])

  return (
    <>
      {error && <div className="mb-4 text-sm alert-error rounded-xl px-4 py-3">{error}</div>}
      <GlassCard padding="p-0" className="overflow-hidden">
        <div className="terminal-wrap rounded-2xl border-0">
          <div className="terminal-header">
            <span className="terminal-dot red" />
            <span className="terminal-dot yellow" />
            <span className="terminal-dot green" />
            <span className="terminal-title">user activity — logins · auth · key redeems</span>
          </div>
          <div ref={bodyRef} className="terminal-body" style={{ maxHeight: '68vh' }}>
            {loading ? (
              <div className="flex items-center justify-center py-10"><Spinner size={24} /></div>
            ) : rows.length === 0 ? (
              <div className="terminal-line t-dim">[empty] no activity yet</div>
            ) : (
              rows.map(row => (
                <div key={row.id} className={`terminal-line ${eventColor(row.event_type)}`}>
                  <span className="t-dim">[{fmtTs(row.created_at)}]</span>{' '}
                  <span className="t-accent">{eventLabel(row.event_type)}</span>{' '}
                  {row.ip && <span className="t-dim">from {row.ip} </span>}
                  {row.account_id && <span className="t-dim">uid={row.account_id} </span>}
                  <span>{renderDetails(row.details)}</span>
                </div>
              ))
            )}
            {!loading && <div className="terminal-line terminal-cursor">$ tail -f activity.log</div>}
          </div>
        </div>
      </GlassCard>
      {!loading && rows.length >= limit && (
        <div className="mt-3 flex justify-center">
          <button onClick={() => setLimit(l => l + 120)} className="btn-ghost px-4 py-2 rounded-xl text-xs">
            Load more
          </button>
        </div>
      )}
    </>
  )
}

// ── Full Audit Log (all admin actions) ────────────────────────────────────────

function FullAuditLog() {
  const token = useAuth(s => s.token)!
  const bodyRef = useRef<HTMLDivElement>(null)
  const [limit, setLimit] = useState(120)
  const { data, loading, error } = useAsync(() => getAuditLog(token, limit), [token, limit])
  const rows = data ?? []

  useEffect(() => {
    if (bodyRef.current) bodyRef.current.scrollTop = bodyRef.current.scrollHeight
  }, [rows, loading])

  return (
    <>
      {error && <div className="mb-4 text-sm alert-error rounded-xl px-4 py-3">{error}</div>}
      <GlassCard padding="p-0" className="overflow-hidden">
        <div className="terminal-wrap rounded-2xl border-0">
          <div className="terminal-header">
            <span className="terminal-dot red" />
            <span className="terminal-dot yellow" />
            <span className="terminal-dot green" />
            <span className="terminal-title">/var/log/cobalt/admin-audit.log</span>
          </div>
          <div ref={bodyRef} className="terminal-body" style={{ maxHeight: '68vh' }}>
            {loading ? (
              <div className="flex items-center justify-center py-10"><Spinner size={24} /></div>
            ) : rows.length === 0 ? (
              <div className="terminal-line t-dim">[empty] no audit records</div>
            ) : (
              rows.map(row => (
                <div key={row.id} className="terminal-line">
                  <span className="t-dim">[{fmtTs(row.created_at)}]</span>{' '}
                  <span className="t-accent">{row.event_type}</span>{' '}
                  <span className="t-good">{row.admin_name ?? 'system'}</span>{' '}
                  <span className="t-dim">account={row.account_id ?? '-'} ip={row.ip ?? '-'}</span>{' '}
                  <span>{renderDetails(row.details)}</span>
                </div>
              ))
            )}
            {!loading && <div className="terminal-line terminal-cursor">$ tail -f admin-audit.log</div>}
          </div>
        </div>
      </GlassCard>
      {!loading && rows.length >= limit && (
        <div className="mt-3 flex justify-center">
          <button onClick={() => setLimit(l => l + 120)} className="btn-ghost px-4 py-2 rounded-xl text-xs">
            Load more
          </button>
        </div>
      )}
    </>
  )
}

// ── Server Logs (superadmin only) ─────────────────────────────────────────────

function ServerLogs() {
  const token = useAuth(s => s.token)!
  const [lines, setLines] = useState<string[]>([])
  const [seq, setSeq] = useState(0)
  const [error, setError] = useState('')
  const bodyRef = useRef<HTMLDivElement>(null)
  const seqRef = useRef(0)

  const poll = useCallback(() => {
    getServerLogs(token, seqRef.current)
      .then(res => {
        if (res.lines.length > 0) {
          setLines(prev => [...prev, ...res.lines].slice(-500))
        }
        seqRef.current = res.seq
        setSeq(res.seq)
      })
      .catch(e => setError(e instanceof Error ? e.message : 'Failed'))
  }, [token])

  useEffect(() => {
    poll()
    const id = setInterval(poll, 2500)
    return () => clearInterval(id)
  }, [poll])

  useEffect(() => {
    if (bodyRef.current) bodyRef.current.scrollTop = bodyRef.current.scrollHeight
  }, [lines])

  function logColor(line: string): string {
    const l = line.toLowerCase()
    if (l.includes('error') || l.includes('fatal')) return 'mistake'
    if (l.includes('warn')) return 'warning'
    if (l.includes('start') || l.includes('listen')) return 'good'
    return ''
  }

  return (
    <>
      {error && <div className="mb-4 text-sm alert-error rounded-xl px-4 py-3">{error}</div>}
      <GlassCard padding="p-0" className="overflow-hidden">
        <div className="terminal-wrap rounded-2xl border-0">
          <div className="terminal-header">
            <span className="terminal-dot red" />
            <span className="terminal-dot yellow" />
            <span className="terminal-dot green" />
            <span className="terminal-title">cobalt-server · live · {seq} lines captured</span>
            <span className="text-[10px] text-[color:var(--good)] font-mono">● LIVE</span>
          </div>
          <div ref={bodyRef} className="terminal-body" style={{ maxHeight: '68vh' }}>
            {lines.length === 0 ? (
              <div className="terminal-line t-dim">waiting for output...</div>
            ) : (
              lines.map((line, i) => (
                <div key={i} className={`terminal-line ${logColor(line)}`}>{line}</div>
              ))
            )}
            <div className="terminal-line terminal-cursor">$</div>
          </div>
        </div>
      </GlassCard>
    </>
  )
}

// ── Page shell ────────────────────────────────────────────────────────────────

type Tab = 'activity' | 'audit' | 'server'

export default function Audit() {
  const user = useAuth(s => s.user)
  const isSuperAdmin = user?.role === 'superadmin'

  const [tab, setTab] = useState<Tab>('activity')

  const tabs: { id: Tab; label: string; superOnly?: boolean }[] = [
    { id: 'activity', label: 'Activity' },
    { id: 'audit', label: 'Admin Audit' },
    { id: 'server', label: 'Server Logs', superOnly: true },
  ]

  return (
    <div>
      <div className="mb-6">
        <div className="page-title mb-1">AUDIT LOG</div>
        <p className="text-[color:var(--text-muted)] text-sm">Event stream and server output</p>
      </div>

      {/* Tabs */}
      <div className="flex gap-1 mb-5 glass-dark rounded-xl p-1 w-fit">
        {tabs.filter(t => !t.superOnly || isSuperAdmin).map(t => (
          <button
            key={t.id}
            onClick={() => setTab(t.id)}
            className="px-4 py-2 rounded-lg text-sm font-medium transition-all"
            style={tab === t.id
              ? { background: 'var(--red)', color: '#fff' }
              : { color: 'var(--text-muted)' }}
          >
            {t.label}
          </button>
        ))}
      </div>

      {tab === 'activity' && <ActivityLog />}
      {tab === 'audit' && <FullAuditLog />}
      {tab === 'server' && isSuperAdmin && <ServerLogs />}
    </div>
  )
}
