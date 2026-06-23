import { render, screen } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import { describe, it, expect } from 'vitest'
import FAQ from './FAQ'
import { faqs } from '../content'

describe('FAQ', () => {
  it('renders every question collapsed by default', () => {
    render(<FAQ />)
    for (const f of faqs) {
      const btn = screen.getByRole('button', { name: f.q })
      expect(btn).toHaveAttribute('aria-expanded', 'false')
    }
  })

  it('expands a question on click', async () => {
    const user = userEvent.setup()
    render(<FAQ />)
    const btn = screen.getByRole('button', { name: faqs[0].q })
    await user.click(btn)
    expect(btn).toHaveAttribute('aria-expanded', 'true')
    expect(screen.getByText(faqs[0].a)).toBeVisible()
  })
})
