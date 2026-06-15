# Front-end (panel + admin) — improvements & follow-ups

This documents the front-end hardening/quality pass applied to the two Vite +
React 18 + react-router + zustand + Tailwind SPAs:

- `panel/panel/` — user-facing member panel
- `admin/admin/` — admin/operator dashboard

## What changed in this pass

**Security**
- Added strict security headers to both `vercel.json` and the `docker/nginx-*.conf`
  configs: `Content-Security-Policy` (script-src 'self' — the key XSS/token-theft
  mitigation), `X-Frame-Options: DENY`, `X-Content-Type-Options: nosniff`,
  `Referrer-Policy: no-referrer`, `Permissions-Policy`, HSTS, and `X-Robots-Tag:
  noindex` on the admin app.
- The API client is now cookie-auth-ready: set `VITE_AUTH_MODE=cookie` to send
  `credentials: 'include'` on every request (default stays `same-origin`, no CORS
  change). See the token-storage follow-up below.

**Robustness / DX**
- `apiFetch` now aborts hung requests after 15s (AbortController) and surfaces a
  "Request timed out" error instead of hanging forever.
- A missing `VITE_API_URL` in a production build now logs a loud console error
  rather than silently falling back.
- The admin client gained the `account_banned` 403 → force-logout handling that
  had drifted out of sync with the panel client.
- Top-level `ErrorBoundary` in both apps — a render throw shows a branded reload
  card instead of a blank white screen.
- Route-level code splitting (`React.lazy` + `Suspense`) shrinks the initial
  bundle; the login screen no longer ships every page.
- Shared `useAsync` hook removes the repeated `loading`/`error`/`useEffect`
  boilerplate and ignores stale responses (race-safe). Migrated: admin Users,
  Audit (activity + audit panels), panel Dashboard.

**Accessibility**
- New accessible `Modal` primitive (focus trap, Esc-to-close, backdrop click,
  body scroll lock, `role="dialog"` + `aria-modal`, focus restoration). All three
  admin modals (ban / add-time / upgrade) and the Users ban modal use it.
- Icon-only buttons got `aria-label`s; decorative SVGs got `aria-hidden`; error
  banners got `role="alert"`; the user-search input got an `aria-label`.
- `@media (prefers-reduced-motion: reduce)` disables the perpetual background-orb
  float and collapses long transitions.

**Tooling**
- Flat ESLint config (typescript-eslint + react-hooks + react-refresh) and
  Prettier, with `lint` / `format` / `format:check` scripts in both `package.json`.
- CSS design tokens are mapped into the Tailwind theme (`bg-accent`, `text-muted`,
  `border-2`, etc.) so new components can drop the inline `style={{}}` color
  strings.

## Follow-ups that need backend / cross-repo coordination

### 1. Move the auth token off `localStorage` (needs Go server change)
Today both apps persist the bearer token via `zustand/persist` in `localStorage`.
Any XSS can read it — and the admin token can ban users / mint keys. The CSP
added here is the main mitigation, but the real fix is **httpOnly, Secure,
SameSite cookies** issued by the Go server:

1. Server: on login, set the session token as an `HttpOnly; Secure; SameSite=Lax`
   cookie instead of (or in addition to) the JSON `token` field. Add a `/logout`
   endpoint that clears it. Ensure CORS sends `Access-Control-Allow-Credentials:
   true` with an explicit (non-wildcard) origin.
2. Client: set `VITE_AUTH_MODE=cookie` (already wired — sends `credentials:
   'include'`), then drop the token from the zustand `persist` store (keep only
   non-sensitive `user` profile, or nothing).
3. Tighten the CSP `connect-src` from `'self' https:` to the exact API origin.

### 2. De-duplicate panel + admin into a shared workspace
`client.ts`, `auth.ts`, `BackgroundOrbs`, `GlassCard`, `Spinner`, `ErrorBoundary`,
`useAsync`, and the entire `index.css` token system are copy-pasted across both
apps and have already drifted once (the `account_banned` handler). Convert to an
npm/pnpm workspace with a shared `packages/ui` so a fix lands once. Deferred here
because it touches both Vercel deploy configs and is best done with a live deploy
to verify.

### 3. Optional: adopt TanStack Query
The `useAsync` hook covers the immediate boilerplate concern. If the dashboards
grow (caching, background refetch, pagination, optimistic updates), TanStack Query
is the natural upgrade — drop-in over the same API layer.
