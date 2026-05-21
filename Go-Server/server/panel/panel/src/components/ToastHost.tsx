import { useToast } from '../store/toast'

const KIND_STYLES: Record<string, { border: string; glow: string; icon: string }> = {
  success: { border: 'rgba(0,255,163,0.28)', glow: 'rgba(0,255,163,0.20)', icon: 'OK' },
  error: { border: 'rgba(255,56,96,0.28)', glow: 'rgba(255,56,96,0.18)', icon: '!' },
  info: { border: 'rgba(77,200,255,0.28)', glow: 'rgba(77,200,255,0.18)', icon: 'i' },
}

export default function ToastHost() {
  const toast = useToast(s => s.toast)
  const hide = useToast(s => s.hide)

  if (!toast) return null

  const style = KIND_STYLES[toast.kind] ?? KIND_STYLES.info

  return (
    <div className="fixed top-4 left-1/2 -translate-x-1/2 z-50 px-4 w-full flex justify-center pointer-events-none">
      <button
        type="button"
        onClick={hide}
        className="pointer-events-auto glass rounded-2xl px-4 py-3 flex items-center gap-3 max-w-[560px] w-full sm:w-auto text-left"
        style={{
          borderColor: style.border,
          boxShadow: `0 0 28px ${style.glow}`,
          animation: 'toastIn 220ms ease-out both',
        }}
      >
        <div
          className="w-8 h-8 rounded-xl flex items-center justify-center font-mono text-sm text-white flex-shrink-0"
          style={{ background: 'var(--surface-2)', border: `1px solid ${style.border}` }}
        >
          {style.icon}
        </div>
        <div className="text-[13px] text-[color:var(--text)] leading-snug">{toast.message}</div>
        <div className="ml-auto text-[11px] text-[color:var(--text-dim)] font-medium">click to dismiss</div>
      </button>
    </div>
  )
}
