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
  },

  // 新的统一导出接口
  generateUnifiedReport(params) {
    return api.post('/vulnerabilities/report', params, {
      responseType: 'blob'
    })
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

export const vulnerabilityTemplateService = {
  getAll(params) {
    return api.get('/vulnerability-templates', { params })
  },
  getAllAvailable() {
    return api.get('/vulnerability-templates/all')
  },
  getById(id) {
    return api.get(`/vulnerability-templates/${id}`)
  },
  create(template) {
    return api.post('/vulnerability-templates', template)
  },
  update(id, template) {
    return api.put(`/vulnerability-templates/${id}`, template)
  },
  delete(id) {
    return api.delete(`/vulnerability-templates/${id}`)
  },
  setDefault(id) {
    return api.put(`/vulnerability-templates/${id}/default`)
  },
  getDefault() {
    return api.get('/vulnerability-templates/default')
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

export const projectService = {
  getAll(params) {
    return api.get('/projects', { params })
  },
  getById(id) {
    return api.get(`/projects/${id}`)
  },
  create(project) {
    return api.post('/projects', project)
  },
  update(id, project) {
    return api.put(`/projects/${id}`, project)
  },
  delete(id) {
    return api.delete(`/projects/${id}`)
  },
  getAllProjects() {
    return api.get('/projects/all')
  },
  getProjectsByUserId(userId) {
    return api.get(`/projects/user/${userId}`)
  }
}

export const systemService = {
  getSystemInfo() {
    return api.get('/system/info')
  }
}

export const taskService = {
  getAll(params) {
    return api.get('/tasks', { params })
  },
  getById(id) {
    return api.get(`/tasks/${id}`)
  },
  create(task) {
    return api.post('/tasks', task)
  },
  update(id, task) {
    return api.put(`/tasks/${id}`, task)
  },
  delete(id) {
    return api.delete(`/tasks/${id}`)
  },
  getAllTasks() {
    return api.get('/tasks/all')
  },
  getTasksByProjectId(projectId) {
    return api.get(`/tasks/project/${projectId}`)
  },
  getTasksByUserId(userId) {
    return api.get(`/tasks/user/${userId}`)
  },
  updateStatus(id, status) {
    return api.put(`/tasks/${id}/status`, null, { params: { status } })
  }
}

export const assetService = {
  getAll(params) {
    return api.get('/assets', { params })
  },
  getById(id) {
    return api.get(`/assets/${id}`)
  },
  create(asset) {
    return api.post('/assets', asset)
  },
  update(id, asset) {
    return api.put(`/assets/${id}`, asset)
  },
  delete(id) {
    return api.delete(`/assets/${id}`)
  },
  getAllAssets() {
    return api.get('/assets/all')
  },
  getAssetsByProjectId(projectId) {
    return api.get(`/assets/project/${projectId}`)
  },
  getAssetsByUserId(userId) {
    return api.get(`/assets/user/${userId}`)
  },
  updateStatus(id, status) {
    return api.put(`/assets/${id}/status`, null, { params: { status } })
  }
}

export default api