// 环境配置
const config = {
  development: {
    // 开发环境配置
    baseURL: 'http://localhost:8080',
    apiPrefix: '/api'
  },
  production: {
    // 生产环境配置
    baseURL: '', // 生产环境使用相对路径，通过nginx代理
    apiPrefix: '/api'
  }
}

// 获取当前环境配置
const getConfig = () => {
  const env = process.env.NODE_ENV || 'development'
  return config[env] || config.development
}

// 导出配置
export default getConfig()

// 导出API基础URL
export const API_BASE_URL = getConfig().baseURL + getConfig().apiPrefix