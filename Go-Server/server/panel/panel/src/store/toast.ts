import { create } from 'zustand'

export type ToastKind = 'success' | 'error' | 'info'

export interface Toast {
  id: number
  kind: ToastKind
  message: string
}

let audioCtx: AudioContext | null = null

function getAudioCtx(): AudioContext | null {
  try {
    const AnyWindow = window as unknown as { webkitAudioContext?: typeof AudioContext }
    const Ctx = window.AudioContext ?? AnyWindow.webkitAudioContext
    if (!Ctx) return null
    if (!audioCtx || audioCtx.state === 'closed') audioCtx = new Ctx()
    return audioCtx
  } catch {
    return null
  }
}

// Browsers often require the AudioContext to be resumed in the same call stack as a user gesture.
// We call this right at the start of click/submit handlers (before any awaits) to "unlock" audio.
function primeAudio() {
  const ctx = getAudioCtx()
  if (!ctx) return
  try {
    // Safari quirks: touching the graph helps it transition state more reliably.
    ctx.createGain()
  } catch {
    // ignore
  }
  try {
    if (ctx.state !== 'running') void ctx.resume?.()
  } catch {
    // ignore
  }
}

function playDing() {
  try {
    const ctx = getAudioCtx()
    if (!ctx) return

    const now = ctx.currentTime
    const osc = ctx.createOscillator()
    const gain = ctx.createGain()

    osc.type = 'sine'
    osc.frequency.setValueAtTime(880, now)
    osc.frequency.exponentialRampToValueAtTime(1320, now + 0.08)

    gain.gain.setValueAtTime(0.0001, now)
    gain.gain.exponentialRampToValueAtTime(0.35, now + 0.01)
    gain.gain.exponentialRampToValueAtTime(0.0001, now + 0.25)

    osc.connect(gain)
    gain.connect(ctx.destination)

    osc.start(now)
    osc.stop(now + 0.26)

    osc.onended = () => {
      try {
        osc.disconnect()
        gain.disconnect()
      } catch {
        // ignore
      }
    }
  } catch {
    // ignore
  }
}

interface ToastState {
  toast: Toast | null
  primeAudio: () => void
  show: (kind: ToastKind, message: string, opts?: { ding?: boolean; durationMs?: number }) => void
  hide: () => void
}

let hideTimer: number | null = null

export const useToast = create<ToastState>((set, get) => ({
  toast: null,
  primeAudio: () => primeAudio(),
  show: (kind, message, opts) => {
    const id = Date.now()
    if (hideTimer != null) window.clearTimeout(hideTimer)
    set({ toast: { id, kind, message } })

    if (opts?.ding) playDing()

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
