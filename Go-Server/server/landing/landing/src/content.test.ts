import { describe, it, expect } from 'vitest'
import { features, tiers, faqs, links, nav } from './content'

describe('content', () => {
  it('exposes a non-empty feature list with required fields', () => {
    expect(features.length).toBeGreaterThan(0)
    for (const f of features) {
      expect(f.title).toBeTruthy()
      expect(f.blurb).toBeTruthy()
      expect(f.iconPath).toBeTruthy()
    }
  })

  it('exposes exactly three pricing tiers with exactly one highlighted', () => {
    expect(tiers).toHaveLength(3)
    expect(tiers.filter(t => t.highlight)).toHaveLength(1)
    for (const t of tiers) {
      expect(t.perks.length).toBeGreaterThan(0)
    }
  })

  it('exposes non-empty FAQ and nav lists', () => {
    expect(faqs.length).toBeGreaterThan(0)
    expect(nav.length).toBeGreaterThan(0)
  })

  it('builds panel CTA links', () => {
    expect(links.signIn).toMatch(/login$/)
    expect(links.getAccess).toMatch(/register$/)
  })
})
