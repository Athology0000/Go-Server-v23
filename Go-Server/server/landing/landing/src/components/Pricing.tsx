import { tiers, links } from '../content'
import GlassCard from './GlassCard'

export default function Pricing() {
  return (
    <section id="pricing" aria-labelledby="pricing-title" className="mx-auto max-w-6xl px-4 py-20">
      <div className="mb-12 text-center">
        <h2 id="pricing-title" className="page-title text-3xl md:text-4xl">
          Simple pricing
        </h2>
        <p className="mt-3 text-[color:var(--text-muted)]">
          One license, one device. Pick a term.
        </p>
      </div>

      <div className="grid gap-5 md:grid-cols-3">
        {tiers.map(t => (
          <GlassCard
            key={t.name}
            padding="p-8"
            className={
              t.highlight
                ? 'relative border-2 !border-[color:var(--accent)]'
                : 'relative'
            }
          >
            {t.highlight && (
              <span
                className="absolute -top-3 left-1/2 -translate-x-1/2 rounded-full px-3 py-1 text-xs font-semibold text-white"
                style={{ background: 'var(--accent)' }}
              >
                Most popular
              </span>
            )}
            <h3 className="font-display text-xl text-[color:var(--text)]">{t.name}</h3>
            <div className="mt-4 flex items-baseline gap-1">
              <span className="stat-value">{t.price}</span>
              <span className="text-sm text-[color:var(--text-muted)]">{t.period}</span>
            </div>
            <ul className="mt-6 space-y-3">
              {t.perks.map(p => (
                <li key={p} className="flex items-start gap-2 text-sm text-[color:var(--text-muted)]">
                  <svg width="16" height="16" viewBox="0 0 20 20" fill="var(--good)" aria-hidden="true" className="mt-0.5 shrink-0">
                    <path fillRule="evenodd" d="M16.7 5.3a1 1 0 010 1.4l-7.5 7.5a1 1 0 01-1.4 0L3.3 9.7a1 1 0 011.4-1.4l3.1 3.1 6.8-6.8a1 1 0 011.4 0z" clipRule="evenodd" />
                  </svg>
                  {p}
                </li>
              ))}
            </ul>
            <a
              href={links.getAccess}
              className={`mt-8 block rounded-xl py-3 text-center text-sm ${t.highlight ? 'btn-red' : 'btn-ghost'}`}
            >
              {t.cta}
            </a>
          </GlassCard>
        ))}
      </div>
      <p className="mt-6 text-center text-xs text-[color:var(--text-dim)]">
        Prices shown are placeholders.
      </p>
    </section>
  )
}
