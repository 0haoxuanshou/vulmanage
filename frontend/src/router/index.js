import { createRouter, createWebHistory } from 'vue-router'
import { setupService } from '@/services/api'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/setup',
    name: 'Setup',
    component: () => import('../views/Setup.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('../views/Dashboard.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: { name: 'DashboardHome' }
      },
      {
        path: 'home',
        name: 'DashboardHome',
        component: () => import('../views/DashboardHome.vue')
      },
      {
        path: 'vulnerabilities',
        name: 'VulnerabilityList',
        component: () => import('../views/vulnerability/List.vue')
      },
      {
        path: 'vulnerabilities/create',
        name: 'VulnerabilityCreate',
        component: () => import('../views/vulnerability/Edit.vue')
      },
      {
        path: 'vulnerabilities/:id',
        name: 'VulnerabilityDetail',
        component: () => import('../views/vulnerability/Detail.vue')
      },
      {
        path: 'vulnerabilities/:id/edit',
        name: 'VulnerabilityEdit',
        component: () => import('../views/vulnerability/Edit.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

router.beforeEach(async (to, from, next) => {
  const isAuthenticated = localStorage.getItem('isAuthenticated') === 'true'
  
  // 检查系统是否需要初始化设置
  if (to.name !== 'Setup') {
    try {
      const response = await setupService.getSetupStatus()
      if (response.data && response.data.needsSetup) {
        // 系统需要初始化，跳转到设置页面
        next({ name: 'Setup' })
        return
      }
    } catch (error) {
      // 如果检查失败，继续正常流程
      console.warn('检查系统初始化状态失败:', error)
    }
  }
  
  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!isAuthenticated) {
      next({ name: 'Login' })
    } else {
      next()
    }
  } else {
    if (isAuthenticated && to.name === 'Login') {
      next({ name: 'Dashboard' })
    } else {
      next()
    }
  }
})

export default router