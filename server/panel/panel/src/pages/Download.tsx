import GlassCard from '../components/GlassCard'

const BOOTSTRAPPER_VERSION = '1.0.0'
const BOOTSTRAPPER_URL = '#' // Replace with actual download URL

const steps = [
  'Download and run the bootstrapper',
  'Sign in with your Cobalt panel credentials',
  'The bootstrapper will verify your license and launch the mod',
  'Open Minecraft 1.21.1 with Fabric installed',
]

export default function Download() {
  return (
    <div>
      <div className="mb-8">
        <div className="page-title mb-1">DOWNLOAD</div>
        <p className="text-[color:var(--text-muted)] text-sm">Get started with Cobalt</p>
      </div>

      <div className="max-w-2xl space-y-4">
        {/* Main download */}
        <GlassCard padding="p-6">
          <div className="flex items-center justify-between flex-wrap gap-4">
            <div className="flex items-center gap-4">
              <div
                className="w-14 h-14 rounded-2xl flex items-center justify-center flex-shrink-0"
                style={{ background: 'var(--red-light)' }}
              >
                <svg width="26" height="26" fill="none" viewBox="0 0 24 24" stroke="var(--red)" strokeWidth="1.8">
                  <path strokeLinecap="round" strokeLinejoin="round" d="M9 3H5a2 2 0 00-2 2v4m6-6h10a2 2 0 012 2v4M9 3v4m0 0H5m4 0h10M5 7v10a2 2 0 002 2h10a2 2 0 002-2V7"/>
                </svg>
              </div>
              <div>
                <div className="font-display text-xl tracking-wider text-[color:var(--text)]">COBALT BOOTSTRAPPER</div>
                <div className="flex items-center gap-2 mt-1">
                  <span className="text-xs text-[color:var(--text-muted)]">v{BOOTSTRAPPER_VERSION}</span>
                  <span className="text-xs text-[color:var(--text-dim)]">·</span>
                  <span className="text-xs text-[color:var(--text-muted)]">Windows x64</span>
                  <span className="text-xs text-[color:var(--text-dim)]">·</span>
                  <span className="text-xs font-semibold" style={{ color: 'var(--good)' }}>Stable</span>
                </div>
              </div>
            </div>
            <a
              href={BOOTSTRAPPER_URL}
              className="btn-red flex items-center gap-2 px-5 py-3 rounded-xl text-sm"
            >
              <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2.5">
                <path strokeLinecap="round" strokeLinejoin="round" d="M12 3v12m0 0l-4-4m4 4l4-4M3 17v2a2 2 0 002 2h14a2 2 0 002-2v-2"/>
              </svg>
              Download .exe
            </a>
          </div>
        </GlassCard>

        {/* Requirements */}
        <GlassCard padding="p-6">
          <div className="label mb-4">System Requirements</div>
          <div className="grid grid-cols-2 gap-3">
            {[
              { label: 'OS', value: 'Windows 10/11 (64-bit)' },
              { label: 'Minecraft', value: '1.21.1 with Fabric' },
              { label: 'Java', value: 'Java 21+' },
              { label: 'RAM', value: '4 GB minimum' },
            ].map(({ label, value }) => (
              <div key={label} className="glass-dark rounded-xl px-4 py-3">
                <div className="text-[10px] text-[color:var(--text-muted)] uppercase tracking-widest mb-0.5">{label}</div>
                <div className="text-sm font-medium text-[color:var(--text)]">{value}</div>
              </div>
            ))}
          </div>
        </GlassCard>

        {/* Setup steps */}
        <GlassCard padding="p-6">
          <div className="label mb-4">Installation Guide</div>
          <ol className="space-y-3">
            {steps.map((step, i) => (
              <li key={i} className="flex items-start gap-3.5">
                <span
                  className="flex-shrink-0 w-6 h-6 rounded-full text-white text-xs font-bold flex items-center justify-center mt-0.5"
                  style={{ background: 'var(--red)' }}
                >
                  {i + 1}
                </span>
                <span className="text-sm text-[color:var(--text-muted)]">{step}</span>
              </li>
            ))}
          </ol>
        </GlassCard>

        {/* Fabric link */}
        <GlassCard padding="p-4">
          <div className="flex items-center gap-3">
            <svg width="18" height="18" fill="none" viewBox="0 0 24 24" stroke="var(--capture)" strokeWidth="2" className="flex-shrink-0">
              <path strokeLinecap="round" strokeLinejoin="round" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
            <p className="text-xs text-[color:var(--text-muted)]">
              Don't have Fabric?{' '}
              <a href="https://fabricmc.net/use" target="_blank" rel="noopener noreferrer" className="font-medium hover:underline" style={{ color: 'var(--capture)' }}>
                Download the Fabric installer
              </a>
              {' '}and install it for Minecraft 1.21.1.
            </p>
          </div>
        </GlassCard>
      </div>
    </div>
  )
}
