import { render, screen } from '@testing-library/react'
import { describe, it, expect } from 'vitest'
import App from './App'

describe('App', () => {
  it('renders the Phantom wordmark', () => {
    render(<App />)
    expect(screen.getAllByText(/phantom/i).length).toBeGreaterThan(0)
  })
})
