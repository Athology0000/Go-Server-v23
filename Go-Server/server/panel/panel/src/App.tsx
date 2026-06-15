import { Suspense, lazy, useEffect } from 'react'
import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from './store/auth'
import { setUnauthorizedHandler } from './api/client'
import BackgroundOrbs from './components/BackgroundOrbs'
import ToastHost from './components/ToastHost'
import ErrorBoundary from './components/ErrorBoundary'
import Spinner from './components/Spinner'
import Layout from './components/Layout'

const Login = lazy(() => import('./pages/Login'))
const Register = lazy(() => import('./pages/Register'))
const Dashboard = lazy(() => import('./pages/Dashboard'))
const Redeem = lazy(() => import('./pages/Redeem'))
const Device = lazy(() => import('./pages/Device'))
const Download = lazy(() => import('./pages/Download'))
const Stats = lazy(() => import('./pages/Stats'))
const Leaderboard = lazy(() => import('./pages/Leaderboard'))

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

function PageFallback() {
  return (
    <div className="flex items-center justify-center h-screen">
      <Spinner size={32} />
    </div>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthWatcher />
      <BackgroundOrbs />
      <ToastHost />
      <div className="relative z-10">
        <ErrorBoundary>
          <Suspense fallback={<PageFallback />}>
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
          </Suspense>
        </ErrorBoundary>
      </div>
    </BrowserRouter>
  )
}
