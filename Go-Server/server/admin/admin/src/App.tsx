import { useEffect } from 'react'
import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom'
import { useAuth } from './store/auth'
import { setUnauthorizedHandler } from './api/client'
import BackgroundOrbs from './components/BackgroundOrbs'
import Layout from './components/Layout'
import Login from './pages/Login'
import Users from './pages/Users'
import UserDetail from './pages/UserDetail'
import Keys from './pages/Keys'
import Audit from './pages/Audit'

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
      <div className="relative z-10">
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
      </div>
    </BrowserRouter>
  )
}
