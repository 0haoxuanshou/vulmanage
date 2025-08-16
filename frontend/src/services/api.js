import axios from 'axios'
import { API_BASE_URL } from '../config/env.js'

const API_URL = API_BASE_URL

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  },
  withCredentials: true
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    // 处理统一返回格式
    if (response.data && typeof response.data === 'object' && 'code' in response.data) {
      if (response.data.code === 200) {
        // 成功响应，返回data字段
        return { ...response, data: response.data.data }
      } else {
        // 业务错误，抛出异常
        const error = new Error(response.data.message || '请求失败')
        error.code = response.data.code
        return Promise.reject(error)
      }
    }
    return response
  },
  async error => {
    if (error.response) {
      const status = error.response.status
      
      if (status === 401) {
        // 未授权，清除本地存储并重定向到登录页面
        localStorage.removeItem('isAuthenticated')
        localStorage.removeItem('token')
        window.location.href = '/login'
      } else if (status === 403) {
        // 权限不足，自动登出并清除登录信息
        localStorage.removeItem('isAuthenticated')
        localStorage.removeItem('token')
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export const authService = {
  async login(username, password) {
    const response = await api.post('/auth/login', { username, password })
    if (response.data && response.data.token) {
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('isAuthenticated', 'true')
    }
    return response
  },
  async logout() {
    try {
      const response = await api.post('/auth/logout')
      localStorage.removeItem('token')
      localStorage.removeItem('isAuthenticated')
      return response
    } catch (error) {
      // 即使请求失败也要清除本地token
      localStorage.removeItem('token')
      localStorage.removeItem('isAuthenticated')
      throw error
    }
  },
  
  async getAuthStatus() {
    return api.get('/auth/status')
  }
}

export const vulnerabilityService = {
  getAll(params) {
    return api.get('/vulnerabilities', { params })
  },
  getById(id) {
    return api.get(`/vulnerabilities/${id}`)
  },
  create(vulnerability) {
    return api.post('/vulnerabilities', vulnerability)
  },
  update(id, vulnerability) {
    return api.put(`/vulnerabilities/${id}`, vulnerability)
  },
  delete(id) {
    return api.delete(`/vulnerabilities/${id}`)
  },
  getHistory(id) {
    return api.get(`/vulnerabilities/${id}/history`)
  }
}

export const fileService = {
  uploadImage(file) {
    const formData = new FormData()
    formData.append('file', file)
    return api.post('/files/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  }
}

export const setupService = {
  async getSetupStatus() {
    return api.get('/setup/status')
  },
  async createAdmin(adminData) {
    return api.post('/setup/admin', adminData)
  }
}

export default api