import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useAuth } from '../store/auth'

const NAV = [
  {
    to: '/users', label: 'Users', icon: (
      <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75"/></svg>
    )
  },
  {
    to: '/keys', label: 'Key Generator', icon: (
      <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2"><path d="M15 5l-10 10 4 4 10-10-4-4z"/><circle cx="18" cy="6" r="2.5"/></svg>
    )
  },
  {
    to: '/audit', label: 'Audit Log', icon: (
      <svg width="16" height="16" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="2"><path d="M4 5h16M4 12h16M4 19h10"/></svg>
    )
  },
]

export default function Layout() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

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
              <div className="text-[10px] text-[color:var(--text-muted)] tracking-widest mt-0.5">ADMIN</div>
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
              <span className="text-[10px] font-semibold px-2 py-0.5 rounded-full text-white" style={{ background: 'var(--red)' }}>
                {user?.role?.toUpperCase() ?? 'ADMIN'}
              </span>
            </div>
          </div>
        </div>

        {/* Nav */}
        <nav className="flex-1 px-3 space-y-0.5 overflow-y-auto">
          {NAV.map(({ to, label, icon }) => (
            <NavLink
              key={to}
              to={to}
              className={({ isActive }) =>
                `flex items-center gap-3 px-3 py-2.5 rounded-xl text-[13px] font-medium transition-all duration-150 ${
                  isActive
                    ? 'text-[color:var(--text)]'
                    : 'text-[color:var(--text-muted)] hover:text-[color:var(--text)] hover:bg-[var(--surface-2)]'
                }`
              }
              style={({ isActive }) => isActive ? { background: 'var(--red-light)' } : {}}
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
