import { features } from '../content'
import GlassCard from './GlassCard'

export default function Features() {
  return (
    <section id="features" aria-labelledby="features-title" className="mx-auto max-w-6xl px-4 py-20">
      <div className="mb-12 text-center">
        <h2 id="features-title" className="page-title text-3xl md:text-4xl">
          Everything in one client
        </h2>
        <p className="mt-3 text-[color:var(--text-muted)]">
          Six pillars, one secure package.
        </p>
      </div>

      <div className="grid gap-5 sm:grid-cols-2 lg:grid-cols-3">
        {features.map(f => (
          <GlassCard key={f.title} className="transition-transform hover:-translate-y-1">
            <div
              className="mb-4 inline-flex h-11 w-11 items-center justify-center rounded-xl"
              style={{ background: 'var(--accent-soft)' }}
            >
              <svg
                width="22"
                height="22"
                viewBox="0 0 24 24"
                fill="none"
                stroke="white"
                strokeWidth="1.8"
                strokeLinecap="round"
                strokeLinejoin="round"
                aria-hidden="true"
              >
                <path d={f.iconPath} />
              </svg>
            </div>
            <h3 className="mb-2 font-display text-lg text-[color:var(--text)]">{f.title}</h3>
            <p className="text-sm leading-relaxed text-[color:var(--text-muted)]">{f.blurb}</p>
          </GlassCard>
        ))}
      </div>
    </section>
  )
}
