export default function Spinner({ size = 20, color = 'var(--red)' }: { size?: number; color?: string }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" className="animate-spin" style={{ color }}>
      <circle cx="12" cy="12" r="9" stroke="currentColor" strokeWidth="2.5" strokeDasharray="42" strokeDashoffset="14" strokeLinecap="round" />
    </svg>
  )
}
