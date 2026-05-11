import { useEffect } from 'react'
import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from './store/auth'
import { setUnauthorizedHandler } from './api/client'
import BackgroundOrbs from './components/BackgroundOrbs'
import ToastHost from './components/ToastHost'
import Layout from './components/Layout'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import Redeem from './pages/Redeem'
import Device from './pages/Device'
import Download from './pages/Download'
import Stats from './pages/Stats'
import Leaderboard from './pages/Leaderboard'

function Guard({ children }: { children: React.ReactNode }) {
  const token = useAuth(s => s.token)
  return token ? <>{children}</> : <Navigate to="/login" replace />
}

function AuthWatcher() {
  const logout = useAuth(s => s.logout)
  const navigate = useNavigate()
  useEffect(() => {
    setUnauthorizedHandler(() => {
      logout()
      navigate('/login', { replace: true })
    })
  }, [logout, navigate])
  return null
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthWatcher />
      <BackgroundOrbs />
      <ToastHost />
      <div className="relative z-10">
        <Routes>
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route element={<Guard><Layout /></Guard>}>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<Dashboard />} />
            <Route path="redeem" element={<Redeem />} />
            <Route path="device" element={<Device />} />
            <Route path="download" element={<Download />} />
            <Route path="stats" element={<Stats />} />
            <Route path="leaderboard" element={<Leaderboard />} />
          </Route>
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </div>
    </BrowserRouter>
  )
}
