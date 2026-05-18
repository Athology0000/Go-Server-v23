const ENV_BASE = (import.meta.env.VITE_API_URL as string | undefined)?.trim()
const BASE = ENV_BASE && ENV_BASE.length > 0
  ? ENV_BASE.replace(/\/+$/, '')
  : 'https://valiant-cooperation-production.up.railway.app'
  
export class ApiError extends Error {
  constructor(public status: number, message: string) {
    super(message)
    this.name = 'ApiError'
  }
}

let _onUnauthorized: (() => void) | null = null
export function setUnauthorizedHandler(fn: () => void) { _onUnauthorized = fn }

export async function apiFetch<T>(
  path: string,
  options: RequestInit = {},
  token?: string | null
): Promise<T> {
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    ...(options.headers as Record<string, string> | undefined ?? {}),
  }
  if (token) headers['Authorization'] = `Bearer ${token}`

  let res: Response
  try {
    res = await fetch(`${BASE}${path}`, { ...options, headers })
  } catch (err: unknown) {
    const msg = err instanceof Error ? err.message : 'Network error'
    throw new Error(msg)
  }

    if (res.status === 403) {
      const clone = res.clone()
      const body = await clone.json().catch(() => ({})) as { error?: string }
      if (body.error === 'account_banned') {
        _onUnauthorized?.()
        throw new ApiError(403, 'Your account has been banned')
      }
    }

    const contentType = res.headers.get('content-type') ?? ''

    if (contentType.includes('application/json')) {
      const body = await res.json().catch(() => ({})) as { message?: string; error?: string }
      throw new ApiError(res.status, body.message ?? body.error ?? `HTTP ${res.status}`)
    }

    const text = await res.text().catch(() => '')
    const trimmed = text.trim()
    throw new ApiError(res.status, trimmed ? trimmed.slice(0, 200) : `HTTP ${res.status}`)
  }

  if (res.status === 204) return undefined as T
  return res.json() as Promise<T>
}
