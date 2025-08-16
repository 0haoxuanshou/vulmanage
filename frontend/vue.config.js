const { defineConfig } = require('@vue/cli-service')

// 根据环境变量确定后端服务地址
const getBackendUrl = () => {
  // 开发环境使用 localhost，生产环境使用 backend 服务名
  if (process.env.NODE_ENV === 'development') {
    return 'http://localhost:8080'
  }
  return 'http://backend:8080'
}

module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    proxy: {
      '/vulmanage': {
        target: getBackendUrl(),
        changeOrigin: true,
        pathRewrite: {
          '^/vulmanage': '/vulmanage'
        }
      },
      '/api': {
        target: getBackendUrl(),
        changeOrigin: true,
        pathRewrite: {
          '^/api': '/api'
        }
      }
    }
  }
})
