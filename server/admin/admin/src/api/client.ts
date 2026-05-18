const BASE = 'https://valiant-cooperation-production.up.railway.app'
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

  if (!res.ok) {
    if (res.status === 401 && token) {
      _onUnauthorized?.()
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
