import { useEffect, useState, useRef, type FormEvent } from 'react'
import { generateKey, getKeys, type GeneratedKey } from '../api/admin'
import { useAuth } from '../store/auth'
import { useToast } from '../store/toast'
import GlassCard from '../components/GlassCard'
import Badge from '../components/Badge'
import Spinner from '../components/Spinner'

const KEY_CACHE_SS = 'cobalt-raw-keys'

function loadKeyCache(): Record<string, string> {
  try { return JSON.parse(sessionStorage.getItem(KEY_CACHE_SS) ?? '{}') } catch { return {} }
}

function saveKeyCache(cache: Record<string, string>) {
  try { sessionStorage.setItem(KEY_CACHE_SS, JSON.stringify(cache)) } catch { /* ignore */ }
}

const PLANS = ['starter', 'pro', 'lifetime'] as const
const DURATIONS = [
  { label: '7 Days', value: 7 },
  { label: '30 Days', value: 30 },
  { label: '90 Days', value: 90 },
  { label: '180 Days', value: 180 },
  { label: '365 Days', value: 365 },
]

function fmtDate(d: string) {
  return new Date(d).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' })
}

function CopyChip({
  value,
  label,
  tone = 'accent',
  onCopy,
}: {
  value: string
  label: string
  tone?: 'accent' | 'alt'
  onCopy?: () => void
}) {
  const [copied, setCopied] = useState(false)

  const copy = async () => {
    try {
      await navigator.clipboard.writeText(value)
      setCopied(true)
      onCopy?.()
      setTimeout(() => setCopied(false), 1400)
    } catch {
      // ignore
    }
  }

  const style =
    tone === 'alt'
      ? { borderColor: 'rgba(192,132,252,0.28)', color: 'var(--accent2)' }
      : { borderColor: 'rgba(155,89,255,0.28)', color: 'var(--text)' }

  return (
    <button
      type="button"
      onClick={copy}
      className="flex-shrink-0 px-3 py-2 rounded-xl border text-xs font-semibold transition-all hover:bg-[var(--surface-2)]"
      style={style}
      title="Copy"
    >
      {copied ? 'Copied' : label}
    </button>
  )
}

export default function Keys() {
  const token = useAuth(s => s.token)!

  const [plan, setPlan] = useState<(typeof PLANS)[number]>('pro')
  const [duration, setDuration] = useState(30)
  const [generating, setGenerating] = useState(false)
  const [genError, setGenError] = useState('')
  const [newKey, setNewKey] = useState<GeneratedKey | null>(null)

  const [keys, setKeys] = useState<GeneratedKey[]>([])
  const [keysLoading, setKeysLoading] = useState(true)
  const rawKeyCache = useRef<Record<string, string>>(loadKeyCache())
  const showToast = useToast(s => s.show)

  const loadKeys = () => {
    setKeysLoading(true)
    getKeys(token)
      .then(setKeys)
      .catch(() => {})
      .finally(() => setKeysLoading(false))
  }

  useEffect(() => {
    loadKeys()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const handleGenerate = async (e: FormEvent) => {
    e.preventDefault()
    setGenError('')
    setNewKey(null)
    setGenerating(true)
    try {
      const key = await generateKey(plan, duration, token)
      setNewKey(key)
      // cache the raw key by DB id so the list can show a copy button
      const updated = { ...rawKeyCache.current, [key.id]: key.key }
      rawKeyCache.current = updated
      saveKeyCache(updated)
      showToast('success', `${key.plan.toUpperCase()} key generated`)
      loadKeys()
    } catch (err: unknown) {
      setGenError(err instanceof Error ? err.message : 'Generation failed')
    } finally {
      setGenerating(false)
    }
  }

  const hashPrefix = (newKey?.keyHashPrefix ?? newKey?.keyHash?.slice(0, 16) ?? '').trim()

  return (
    <div>
      <div className="mb-8">
        <div className="page-title mb-1">KEY GENERATOR</div>
        <p className="text-[color:var(--text-muted)] text-sm">Generate and manage license keys</p>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-5 mb-6">
        <GlassCard className="lg:col-span-1" padding="p-6">
          <div className="label mb-5">Generate New Key</div>

          <form onSubmit={handleGenerate} className="space-y-4">
            <div>
              <label className="label">Plan</label>
              <div className="flex gap-2 flex-wrap">
                {PLANS.map(p => {
                  const active = plan === p
                  return (
                    <button
                      key={p}
                      type="button"
                      onClick={() => setPlan(p)}
                      className="px-3 py-1.5 rounded-lg text-xs font-semibold transition-all capitalize border"
                      style={
                        active
                          ? { background: 'var(--red)', color: '#fff', borderColor: 'transparent' }
                          : { background: 'var(--surface)', color: 'var(--text-muted)', borderColor: 'var(--border)' }
                      }
                    >
                      {p}
                    </button>
                  )
                })}
              </div>
            </div>

            <div>
              <label className="label">Duration</label>
              <select
                value={plan === 'lifetime' ? 0 : duration}
                onChange={e => setDuration(Number(e.target.value))}
                disabled={plan === 'lifetime'}
                className="input-field px-4 py-2.5 rounded-xl disabled:opacity-60"
              >
                {plan === 'lifetime' ? (
                  <option value={0}>Lifetime</option>
                ) : (
                  DURATIONS.map(d => (
                    <option key={d.value} value={d.value}>
                      {d.label}
                    </option>
                  ))
                )}
              </select>
            </div>

            {genError && <div className="text-xs alert-error rounded-xl px-3 py-2">{genError}</div>}

            <button
              type="submit"
              disabled={generating}
              className="btn-red w-full py-3 rounded-xl flex items-center justify-center gap-2 text-sm"
            >
              {generating ? (
                <Spinner size={16} color="white" />
              ) : (
                <>
                  <svg width="14" height="14" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2.5">
                    <path d="M12 4v16m8-8H4" />
                  </svg>
                  Generate Key
                </>
              )}
            </button>
          </form>

          {newKey && (
            <div className="mt-5 pt-5 border-t border-[color:var(--border)]">
              <div className="label mb-2">Generated</div>

              {hashPrefix && (
                <div className="glass-dark rounded-xl p-3 flex items-center gap-2 mb-2">
                  <code className="flex-1 text-xs font-mono text-[color:var(--text)] break-all leading-relaxed">
                    {hashPrefix}
                  </code>
                  <CopyChip value={hashPrefix} label="Copy ID" tone="alt" />
                </div>
              )}

              <div className="glass-dark rounded-xl p-3 flex items-center gap-2">
                <code className="flex-1 text-xs font-mono text-[color:var(--text)] break-all leading-relaxed">
                  {newKey.key}
                </code>
                <CopyChip value={newKey.key} label="Copy Key" tone="accent" onCopy={() => showToast('success', 'Key copied to clipboard')} />
              </div>

              <p className="text-xs text-[color:var(--text-muted)] mt-2">
                {newKey.plan.toUpperCase()} {newKey.durationDays ? `- ${newKey.durationDays} days` : '- lifetime'}
              </p>
            </div>
          )}
        </GlassCard>

        <GlassCard className="lg:col-span-2" padding="p-0">
          <div className="px-6 py-4 border-b border-[color:var(--border)]">
            <div className="label">Recent Keys</div>
          </div>

          {keysLoading ? (
            <div className="flex items-center justify-center h-32">
              <Spinner size={24} />
            </div>
          ) : keys.length === 0 ? (
            <div className="py-12 text-center text-[color:var(--text-muted)] text-sm">No keys generated yet</div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead>
                  <tr className="border-b border-[color:var(--border)]">
                    {['Key ID', 'Plan', 'Duration', 'Created', 'Status'].map(h => (
                      <th
                        key={h}
                        scope="col"
                        className="text-left py-3 px-5 text-[10px] font-semibold text-[color:var(--text-dim)] uppercase tracking-widest"
                      >
                        {h}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {keys.map(k => {
                    const rawKey = rawKeyCache.current[k.id]
                    const displayId = (k.keyHashPrefix ?? k.key).slice(0, 16)
                    return (
                    <tr
                      key={k.id}
                      className="border-b border-[color:var(--border)] last:border-0 hover:bg-[var(--surface-2)] transition-colors"
                    >
                      <td className="py-3 px-5">
                        <div className="flex items-center gap-2 flex-wrap">
                          <code className="text-xs font-mono text-[color:var(--text)]">{displayId}</code>
                          {rawKey ? (
                            <CopyChip value={rawKey} label="Copy Key" tone="accent" onCopy={() => showToast('success', 'Key copied to clipboard')} />
                          ) : (
                            <CopyChip value={displayId} label="Copy ID" tone="alt" />
                          )}
                        </div>
                      </td>
                      <td className="py-3 px-5">
                        <span className="text-xs font-bold px-2 py-0.5 rounded text-white" style={{ background: 'var(--red)' }}>
                          {k.plan.toUpperCase()}
                        </span>
                      </td>
                      <td className="py-3 px-5 text-sm text-[color:var(--text-muted)]">{k.durationDays ? `${k.durationDays}d` : 'lifetime'}</td>
                      <td className="py-3 px-5 text-sm text-[color:var(--text-muted)]">{fmtDate(k.createdAt)}</td>
                      <td className="py-3 px-5">
                        <Badge status={k.usedBy ? 'used' : 'unused'} />
                        {k.usedBy && <div className="text-[10px] text-[color:var(--text-muted)] mt-0.5">{k.usedBy}</div>}
                      </td>
                    </tr>
                    )
                  })}
                </tbody>
              </table>
            </div>
          )}
        </GlassCard>
      </div>
    </div>
  )
}
