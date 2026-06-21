import { useState } from 'react'
import { faqs } from '../content'

export default function FAQ() {
  const [open, setOpen] = useState<number | null>(null)

  return (
    <section id="faq" aria-labelledby="faq-title" className="mx-auto max-w-3xl px-4 py-20">
      <div className="mb-12 text-center">
        <h2 id="faq-title" className="page-title text-3xl md:text-4xl">
          Frequently asked
        </h2>
      </div>

      <div className="space-y-3">
        {faqs.map((f, i) => {
          const isOpen = open === i
          const panelId = `faq-panel-${i}`
          const btnId = `faq-button-${i}`
          return (
            <div key={f.q} className="glass rounded-xl">
              <h3>
                <button
                  id={btnId}
                  type="button"
                  aria-expanded={isOpen}
                  aria-controls={panelId}
                  onClick={() => setOpen(isOpen ? null : i)}
                  className="flex w-full items-center justify-between gap-4 px-5 py-4 text-left"
                >
                  <span className="font-medium text-[color:var(--text)]">{f.q}</span>
                  <svg
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    aria-hidden="true"
                    className={`shrink-0 text-[color:var(--text-muted)] transition-transform ${isOpen ? 'rotate-180' : ''}`}
                  >
                    <path d="M6 9l6 6 6-6" />
                  </svg>
                </button>
              </h3>
              <div
                id={panelId}
                role="region"
                aria-labelledby={btnId}
                hidden={!isOpen}
                className="px-5 pb-5 text-sm leading-relaxed text-[color:var(--text-muted)]"
              >
                {f.a}
              </div>
            </div>
          )
        })}
      </div>
    </section>
  )
}
