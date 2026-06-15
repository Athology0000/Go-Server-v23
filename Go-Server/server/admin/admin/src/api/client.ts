const ENV_BASE = (import.meta.env.VITE_API_URL as string | undefined)?.trim()

// Dev convenience fallback. In production a missing VITE_API_URL is almost
// always a misconfigured deploy, so we surface it loudly rather than failing
// silently — but we keep serving the known default so a live build still works.
const DEV_FALLBACK = 'https://valiant-cooperation-production.up.railway.app'

function resolveBase(): string {
  if (ENV_BASE && ENV_BASE.length > 0) return ENV_BASE.replace(/\/+$/, '')
  if (import.meta.env.PROD) {
    console.error(
      '[api] VITE_API_URL is not set for this production build — falling back to ' +
        `${DEV_FALLBACK}. Set VITE_API_URL in the deploy environment.`,
    )
  }
  return DEV_FALLBACK
}

const BASE = resolveBase()

// Send cookies when the backend opts into httpOnly-cookie auth (VITE_AUTH_MODE=cookie).
// Defaults to 'same-origin' (current bearer-token behaviour, no CORS change).
const CREDENTIALS: RequestCredentials =
  import.meta.env.VITE_AUTH_MODE === 'cookie' ? 'include' : 'same-origin'

const DEFAULT_TIMEOUT_MS = 15_000

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

  // Abort the request if it hangs, unless the caller supplied their own signal.
  const controller = options.signal ? null : new AbortController()
  const timeout = controller
    ? setTimeout(() => controller.abort(), DEFAULT_TIMEOUT_MS)
    : null

  let res: Response
  try {
    res = await fetch(`${BASE}${path}`, {
      credentials: CREDENTIALS,
      ...options,
      headers,
      signal: options.signal ?? controller?.signal,
    })
  } catch (err: unknown) {
    if (err instanceof DOMException && err.name === 'AbortError') {
      throw new Error('Request timed out')
    }
    const msg = err instanceof Error ? err.message : 'Network error'
    throw new Error(msg)
  } finally {
    if (timeout) clearTimeout(timeout)
  }

  if (!res.ok) {
    if (res.status === 401 && token) {
      _onUnauthorized?.()
    }

    // Mirror the panel client: force a logout if the account was banned.
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
