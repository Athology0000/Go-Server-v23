import { describe, it, expect, vi } from 'vitest'
import { renderHook, waitFor, act } from '@testing-library/react'
import { useAsync } from './useAsync'

describe('useAsync', () => {
  it('loads data and clears loading', async () => {
    const fn = vi.fn().mockResolvedValue({ ok: true })
    const { result } = renderHook(() => useAsync(fn, []))
    expect(result.current.loading).toBe(true)
    await waitFor(() => expect(result.current.loading).toBe(false))
    expect(result.current.data).toEqual({ ok: true })
    expect(result.current.error).toBe('')
  })

  it('captures errors', async () => {
    const fn = vi.fn().mockRejectedValue(new Error('boom'))
    const { result } = renderHook(() => useAsync(fn, []))
    await waitFor(() => expect(result.current.loading).toBe(false))
    expect(result.current.error).toBe('boom')
    expect(result.current.data).toBeNull()
  })

  it('reload re-runs the function', async () => {
    const fn = vi.fn().mockResolvedValue(1)
    const { result } = renderHook(() => useAsync(fn, []))
    await waitFor(() => expect(result.current.loading).toBe(false))
    expect(fn).toHaveBeenCalledTimes(1)
    act(() => result.current.reload())
    await waitFor(() => expect(fn).toHaveBeenCalledTimes(2))
  })

  it('ignores a stale resolve when deps change (race-safe)', async () => {
    let resolveFirst: (v: string) => void = () => {}
    const fn = vi
      .fn()
      .mockImplementationOnce(() => new Promise<string>(r => { resolveFirst = r }))
      .mockImplementationOnce(() => Promise.resolve('second'))

    const { result, rerender } = renderHook(({ d }) => useAsync(fn, [d]), {
      initialProps: { d: 1 },
    })
    rerender({ d: 2 })
    await waitFor(() => expect(result.current.data).toBe('second'))

    // The slow first request resolving late must NOT clobber the newer result.
    act(() => resolveFirst('first'))
    await waitFor(() => expect(result.current.data).toBe('second'))
  })
})
