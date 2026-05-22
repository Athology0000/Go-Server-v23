type Status = 'active' | 'banned' | 'expired' | 'unused' | 'used'

const cfg: Record<Status, { bg: string; color: string; label: string }> = {
  active:  { bg: 'rgba(0,255,163,0.10)', color: 'var(--good)', label: 'Active' },
  banned:  { bg: 'rgba(255,56,96,0.10)', color: 'var(--mistake)', label: 'Banned' },
  expired: { bg: 'rgba(255,170,0,0.10)', color: 'var(--warning)', label: 'Expired' },
  unused:  { bg: 'rgba(255,255,255,0.05)', color: 'var(--text-muted)', label: 'Unused' },
  used:    { bg: 'rgba(192,132,252,0.12)', color: 'var(--accent2)', label: 'Used' },
}

export default function Badge({ status }: { status: Status }) {
  const { bg, color, label } = cfg[status]
  return (
    <span
      className="inline-block text-[11px] font-bold px-2.5 py-0.5 rounded-full tracking-wide"
      style={{ background: bg, color }}
    >
      {label}
    </span>
  )
}
