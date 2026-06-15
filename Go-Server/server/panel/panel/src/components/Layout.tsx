import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../store/auth'

const BASE_NAV = [
  { to: '/dashboard', label: 'Dashboard', requiresPlan: false, icon: (
    <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2"><rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/></svg>
  )},
  { to: '/device', label: 'Device Info', requiresPlan: false, icon: (
    <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2"><rect x="2" y="4" width="20" height="16" rx="2"/><path d="M8 20h8M12 16v4"/></svg>
  )},
  { to: '/download', label: 'Download', requiresPlan: true, icon: (
    <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2"><path d="M12 3v12m0 0l-4-4m4 4l4-4"/><path d="M3 17v2a2 2 0 002 2h14a2 2 0 002-2v-2"/></svg>
  )},
  { to: '/stats', label: 'My Stats', requiresPlan: true, icon: (
    <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2"><path d="M3 17l4-8 4 5 4-3 4 6"/></svg>
  )},
  { to: '/leaderboard', label: 'Leaderboard', requiresPlan: true, icon: (
    <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2"><path d="M8 6v14M12 10v10M16 3v17M4 20h16"/></svg>
  )},
]

export default function Layout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const hasPlan = !!user?.plan
  const planActive = hasPlan && (user?.planExpiry ? new Date(user.planExpiry) > new Date() : true)
  const nav = BASE_NAV.filter(item => !item.requiresPlan || hasPlan)

  return (
    <div className="flex h-full min-h-screen">
      <a href="#main-content" className="skip-link">Skip to content</a>
      {/* Sidebar */}
      <aside className="fixed top-0 left-0 h-full glass rounded-none flex flex-col" style={{ width: 'var(--sidebar-w)', zIndex: 10 }}>
        {/* Brand */}
        <div className="px-5 pt-7 pb-6">
          <div className="flex items-center gap-3">
            <div className="w-9 h-9 rounded-xl flex items-center justify-center shadow-md flex-shrink-0" style={{ background: 'var(--red)' }}>
              <span className="font-display text-white text-xl leading-none">C</span>
            </div>
            <div>
              <div className="font-display text-xl tracking-[0.15em] text-[color:var(--text)] leading-none">COBALT</div>
              <div className="text-[10px] text-[color:var(--text-muted)] tracking-widest mt-0.5">PANEL</div>
            </div>
          </div>
        </div>

        <hr className="divider mx-4" />

        {/* User badge */}
        <div className="px-4 py-4">
          <div className="glass-dark rounded-xl px-3 py-3">
            <div className="text-[10px] text-[color:var(--text-dim)] tracking-widest uppercase mb-1">Signed in as</div>
            <div className="font-medium text-[color:var(--text)] text-sm truncate">{user?.username}</div>
            <div className="mt-1.5 flex items-center gap-1.5">
              {user?.plan ? (
                <span className="text-[10px] font-semibold px-2 py-0.5 rounded-full text-white" style={{ background: planActive ? 'var(--red)' : '#999' }}>
                  {user.plan.toUpperCase()}
                </span>
              ) : (
                <span className="text-[10px] font-semibold px-2 py-0.5 rounded-full bg-[rgba(255,255,255,0.04)] border border-[rgba(155,89,255,0.14)] text-[color:var(--text-muted)]">NO PLAN</span>
              )}
              {planActive && <span className="text-[10px] text-[color:var(--good)] font-medium">● Active</span>}
            </div>
          </div>
        </div>

        {/* Nav */}
        <nav className="flex-1 px-3 space-y-0.5 overflow-y-auto">
          {nav.map(({ to, label, icon }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-xl text-[13px] font-medium transition-all duration-150 ${
                  isActive
                    ? 'text-white shadow-sm'
                    : 'text-[color:var(--text-muted)] hover:text-[color:var(--text)] hover:bg-[var(--surface-2)]'
                }`
              }
              style={({ isActive }) => isActive ? { background: 'var(--red)' } : {}}
            >
              {icon}
              {label}
            </NavLink>
          ))}
        </nav>

        {/* Logout */}
        <div className="p-3 pb-5">
          <button
            onClick={() => { logout(); navigate('/login') }}
            className="w-full flex items-center gap-2.5 px-3 py-2.5 rounded-xl text-[13px] text-[color:var(--text-muted)] hover:text-[color:var(--mistake)] hover:bg-[rgba(255,56,96,0.08)] transition-all"
          >
            <svg width="15" height="15" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2">
              <path d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a2 2 0 01-2 2H6a2 2 0 01-2-2V7a2 2 0 012-2h5a2 2 0 012 2v1"/>
            </svg>
            Sign Out
          </button>
        </div>
      </aside>

      {/* Page content */}
      <main id="main-content" tabIndex={-1} className="flex-1 overflow-auto outline-none" style={{ marginLeft: 'var(--sidebar-w)' }}>
        <div className="p-8 max-w-5xl mx-auto">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
