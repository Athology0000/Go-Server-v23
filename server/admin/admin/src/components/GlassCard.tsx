import type { ReactNode } from 'react'

export default function GlassCard({ children, className = '', padding = 'p-6' }: { children: ReactNode; className?: string; padding?: string }) {
  return <div className={`glass rounded-2xl ${padding} ${className}`}>{children}</div>
}
