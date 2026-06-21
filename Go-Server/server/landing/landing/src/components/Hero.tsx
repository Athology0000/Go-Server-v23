import { links } from '../content'

export default function Hero() {
  return (
    <section className="mx-auto grid max-w-6xl items-center gap-10 px-4 py-20 md:grid-cols-2 md:py-28">
      <div>
        <span className="inline-block rounded-full border border-[color:var(--border-2)] px-3 py-1 text-xs uppercase tracking-widest text-[color:var(--text-muted)]">
          Hypixel Skyblock client
        </span>
        <h1 className="mt-5 font-display text-5xl font-bold leading-tight tracking-tight text-[color:var(--text)] md:text-6xl">
          Dominate Skyblock.
          <span className="block text-[color:var(--accent)]">On autopilot.</span>
        </h1>
        <p className="mt-5 max-w-md text-base leading-relaxed text-[color:var(--text-muted)]">
          Phantom bundles mining, combat, Kuudra, and commissions behind a native
          pathfinder and a secure, fail-closed loader. One client. Every grind.
        </p>
        <div className="mt-8 flex flex-wrap items-center gap-3">
          <a href={links.getAccess} className="btn-red rounded-xl px-6 py-3 text-sm">
            Get access
          </a>
          <a href="#features" className="btn-ghost rounded-xl px-6 py-3 text-sm">
            View features
          </a>
        </div>
      </div>

      {/* Stylized mock-client visual reusing the panel's terminal styling */}
      <div className="terminal-wrap" aria-hidden="true">
        <div className="terminal-header">
          <span className="terminal-dot red" />
          <span className="terminal-dot yellow" />
          <span className="terminal-dot green" />
          <span className="terminal-title">phantom — session</span>
        </div>
        <div className="terminal-body">
          <div className="terminal-line good">[auth] manifest signature verified ✓</div>
          <div className="terminal-line">[loader] modules decrypted in memory</div>
          <div className="terminal-line t-accent">[mining] route: Dwarven Mines → mithril</div>
          <div className="terminal-line">[pathfinder] 4128 nodes / 0.7ms</div>
          <div className="terminal-line good">[combat] target locked</div>
          <div className="terminal-line terminal-cursor">[phantom] running</div>
        </div>
      </div>
    </section>
  )
}
