import { nav, links } from '../content'

export default function Nav() {
  return (
    <header className="sticky top-0 z-20 border-b border-[color:var(--border)] bg-[color:var(--bg)]/80 backdrop-blur">
      <div className="mx-auto flex max-w-6xl items-center justify-between px-4 py-3">
        <a href="#main" className="flex items-center gap-2" aria-label="Phantom home">
          <span
            className="inline-flex h-8 w-8 items-center justify-center rounded-lg text-white"
            style={{ background: 'var(--accent)' }}
          >
            <span className="font-display text-lg leading-none">P</span>
          </span>
          <span className="font-display text-lg tracking-[0.18em] text-[color:var(--text)]">PHANTOM</span>
        </a>

        <nav className="hidden items-center gap-6 md:flex" aria-label="Primary">
          {nav.map(item => (
            <a
              key={item.href}
              href={item.href}
              className="text-sm text-[color:var(--text-muted)] transition-colors hover:text-[color:var(--text)]"
            >
              {item.label}
            </a>
          ))}
        </nav>

        <div className="flex items-center gap-2">
          <a href={links.signIn} className="btn-ghost rounded-lg px-4 py-2 text-sm">
            Sign in
          </a>
          <a href={links.getAccess} className="btn-red rounded-lg px-4 py-2 text-sm">
            Get access
          </a>
        </div>
      </div>
    </header>
  )
}
