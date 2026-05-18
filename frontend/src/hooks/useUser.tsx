import { createContext, useContext, useState, useEffect, type ReactNode } from 'react'
import type { User } from '../types'
import { getUsers, createUser } from '../services/bolaoApi'

interface UserCtx {
  currentUser: User | null
  users: User[]
  loading: boolean
  selectUser: (u: User) => void
  addUser: (name: string) => Promise<User>
  logout: () => void
  refresh: () => void
}

const Ctx = createContext<UserCtx>({} as UserCtx)
export const useUser = () => useContext(Ctx)

export function UserProvider({ children }: { children: ReactNode }) {
  const [currentUser, setCurrentUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('bolao_user')
    return saved ? JSON.parse(saved) : null
  })
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(false)

  const fetchUsers = async () => {
    setLoading(true)
    try {
      const data = await getUsers()
      setUsers(data)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { fetchUsers() }, [])

  const selectUser = (u: User) => {
    setCurrentUser(u)
    localStorage.setItem('bolao_user', JSON.stringify(u))
  }

  const addUser = async (name: string) => {
    const u = await createUser(name)
    await fetchUsers()
    selectUser(u)
    return u
  }

  const logout = () => {
    setCurrentUser(null)
    localStorage.removeItem('bolao_user')
  }

  return (
    <Ctx.Provider value={{ currentUser, users, loading, selectUser, addUser, logout, refresh: fetchUsers }}>
      {children}
    </Ctx.Provider>
  )
}
