import { links } from '../content'

export default function Footer() {
  const year = new Date().getFullYear()
  return (
    <footer className="border-t border-[color:var(--border)] py-10">
      <div className="mx-auto flex max-w-6xl flex-col items-center justify-between gap-4 px-4 sm:flex-row">
        <div className="flex items-center gap-2">
          <span
            className="inline-flex h-6 w-6 items-center justify-center rounded-md text-white"
            style={{ background: 'var(--accent)' }}
          >
            <span className="font-display text-sm leading-none">P</span>
          </span>
          <span className="font-display tracking-[0.18em] text-[color:var(--text)]">PHANTOM</span>
        </div>

        <nav className="flex items-center gap-6 text-sm text-[color:var(--text-muted)]" aria-label="Footer">
          <a href={links.discord} className="transition-colors hover:text-[color:var(--text)]">
            Discord
          </a>
          <a href="#features" className="transition-colors hover:text-[color:var(--text)]">
            Features
          </a>
          <a href="#pricing" className="transition-colors hover:text-[color:var(--text)]">
            Pricing
          </a>
        </nav>

        <p className="text-xs text-[color:var(--text-dim)]">Phantom © {year}</p>
      </div>
      <p className="mx-auto mt-6 max-w-6xl px-4 text-center text-[11px] leading-relaxed text-[color:var(--text-dim)]">
        Phantom is a third-party client. Not affiliated with Hypixel or Mojang. Use is at your own risk.
      </p>
    </footer>
  )
}
