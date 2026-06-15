import { useCallback, useEffect, useRef, useState } from 'react'

interface AsyncState<T> {
  data: T | null
  loading: boolean
  error: string
  /** Re-run the async function. */
  reload: () => void
}

/**
 * Runs an async function on mount and whenever `deps` change, tracking
 * loading/error/data and exposing a `reload()`. Ignores results from a stale
 * run if the component re-fetched or unmounted, preventing race conditions and
 * setState-after-unmount warnings.
 */
export function useAsync<T>(fn: () => Promise<T>, deps: unknown[]): AsyncState<T> {
  const [data, setData] = useState<T | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  // Keep the latest fn without forcing it into the dep array.
  const fnRef = useRef(fn)
  fnRef.current = fn

  const runIdRef = useRef(0)

  const run = useCallback(() => {
    const runId = ++runIdRef.current
    setLoading(true)
    setError('')
    fnRef.current()
      .then(res => {
        if (runId === runIdRef.current) setData(res)
      })
      .catch(e => {
        if (runId === runIdRef.current) {
          setError(e instanceof Error ? e.message : 'Failed to load')
        }
      })
      .finally(() => {
        if (runId === runIdRef.current) setLoading(false)
      })
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps)

  useEffect(() => {
    run()
    return () => {
      // Invalidate any in-flight run on unmount/re-run.
      runIdRef.current++
    }
  }, [run])

  return { data, loading, error, reload: run }
}
