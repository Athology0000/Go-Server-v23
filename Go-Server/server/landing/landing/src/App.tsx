import BackgroundOrbs from './components/BackgroundOrbs'
import Nav from './components/Nav'
import Hero from './components/Hero'
import Features from './components/Features'
import Pricing from './components/Pricing'
import FAQ from './components/FAQ'
import Footer from './components/Footer'

export default function App() {
  return (
    <>
      <a href="#main" className="skip-link">Skip to content</a>
      <BackgroundOrbs />
      <div className="relative z-10">
        <Nav />
        <main id="main">
          <Hero />
          <Features />
          <Pricing />
          <FAQ />
        </main>
        <Footer />
      </div>
    </>
  )
}
