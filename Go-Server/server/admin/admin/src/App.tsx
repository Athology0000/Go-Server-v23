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
const Users = lazy(() => import('./pages/Users'))
const UserDetail = lazy(() => import('./pages/UserDetail'))
const Keys = lazy(() => import('./pages/Keys'))
const Audit = lazy(() => import('./pages/Audit'))

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
              <Route element={<Guard><Layout /></Guard>}>
                <Route index element={<Navigate to="/users" replace />} />
                <Route path="users" element={<Users />} />
                <Route path="users/:id" element={<UserDetail />} />
                <Route path="keys" element={<Keys />} />
                <Route path="audit" element={<Audit />} />
              </Route>
              <Route path="*" element={<Navigate to="/users" replace />} />
            </Routes>
          </Suspense>
        </ErrorBoundary>
      </div>
    </BrowserRouter>
  )
}
