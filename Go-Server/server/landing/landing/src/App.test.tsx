import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import App from './App'
import { features, tiers, faqs } from './content'

describe('App', () => {
  it('renders the hero headline', () => {
    render(<App />)
    expect(screen.getByRole('heading', { name: /dominate skyblock/i })).toBeInTheDocument()
  })

  it('renders every feature title', () => {
    render(<App />)
    for (const f of features) {
      expect(screen.getByRole('heading', { name: f.title })).toBeInTheDocument()
    }
  })

  it('renders every pricing tier and the FAQ questions', () => {
    render(<App />)
    for (const t of tiers) {
      expect(screen.getByRole('heading', { name: t.name })).toBeInTheDocument()
    }
    for (const f of faqs) {
      expect(screen.getByRole('button', { name: f.q })).toBeInTheDocument()
    }
  })

  it('exposes nav and main landmarks', () => {
    render(<App />)
    expect(screen.getByRole('banner')).toBeInTheDocument()
    expect(screen.getByRole('main')).toBeInTheDocument()
    expect(screen.getByRole('contentinfo')).toBeInTheDocument()
  })
})
