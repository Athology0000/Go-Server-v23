import { create } from 'zustand'

export type ToastKind = 'success' | 'error' | 'info'

export interface Toast {
  id: number
  kind: ToastKind
  message: string
}

interface ToastState {
  toast: Toast | null
  show: (kind: ToastKind, message: string, opts?: { durationMs?: number }) => void
  hide: () => void
}

let hideTimer: number | null = null
let counter = 0

export const useToast = create<ToastState>((set, get) => ({
  toast: null,
  show: (kind, message, opts) => {
    const id = ++counter
    if (hideTimer != null) window.clearTimeout(hideTimer)
    set({ toast: { id, kind, message } })

    const durationMs = opts?.durationMs ?? 2800
    hideTimer = window.setTimeout(() => {
      const current = get().toast
      if (current?.id === id) set({ toast: null })
    }, durationMs)
  },
  hide: () => {
    if (hideTimer != null) window.clearTimeout(hideTimer)
    hideTimer = null
    set({ toast: null })
  },
}))
