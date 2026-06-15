import { Component, type ErrorInfo, type ReactNode } from 'react'

interface Props {
  children: ReactNode
}

interface State {
  error: Error | null
}

/**
 * Top-level error boundary. A throw anywhere in the tree renders a branded
 * fallback instead of a blank white screen, and offers a one-click reload.
 */
export default class ErrorBoundary extends Component<Props, State> {
  state: State = { error: null }

  static getDerivedStateFromError(error: Error): State {
    return { error }
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    // Surface in the console for support; wire to a reporter (Sentry, etc.) here.
    console.error('Unhandled UI error:', error, info.componentStack)
  }

  render() {
    if (!this.state.error) return this.props.children

    return (
      <div className="min-h-screen flex items-center justify-center p-4">
        <div className="glass rounded-2xl p-8 max-w-md w-full text-center">
          <div className="font-display text-2xl tracking-[0.12em] text-[color:var(--text)] mb-2">
            SOMETHING BROKE
          </div>
          <p className="text-sm text-[color:var(--text-muted)] mb-6">
            An unexpected error occurred. Reloading usually clears it.
          </p>
          <pre className="text-left text-[11px] font-mono text-[color:var(--text-dim)] bg-black/30 rounded-xl p-3 mb-6 overflow-auto max-h-32">
            {this.state.error.message}
          </pre>
          <button
            onClick={() => window.location.reload()}
            className="btn-red w-full py-3 rounded-xl text-sm"
          >
            Reload
          </button>
        </div>
      </div>
    )
  }
}
