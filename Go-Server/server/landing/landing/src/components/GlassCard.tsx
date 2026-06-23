import type { ReactNode } from 'react'

interface Props {
  children: ReactNode
  className?: string
  padding?: string
}

export default function GlassCard({ children, className = '', padding = 'p-6' }: Props) {
  return (
    <div className={`glass rounded-2xl ${padding} ${className}`}>
      {children}
    </div>
  )
}
