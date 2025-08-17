<template>
  <div class="dashboard-container">
    <el-container>
      <el-header>
        <div class="header-logo">漏洞管理系统</div>
        <div class="header-menu">
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              管理员
              <el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <el-container>
        <el-aside width="200px">
          <el-menu
            router
            :default-active="activeMenu"
            class="el-menu-vertical"
            background-color="#304156"
            text-color="#bfcbd9"
            active-text-color="#409EFF">
            <el-menu-item index="/dashboard/home">
              <el-icon><odometer /></el-icon>
              <span>Dashboard</span>
            </el-menu-item>
            <el-menu-item index="/dashboard/vulnerabilities">
              <el-icon><document /></el-icon>
              <span>漏洞管理</span>
            </el-menu-item>
            <el-menu-item index="/dashboard/templates">
              <el-icon><files /></el-icon>
              <span>模板管理</span>
            </el-menu-item>
          </el-menu>
        </el-aside>
        
        <el-main>
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessageBox } from 'element-plus'
import { ArrowDown, Document, Odometer, Files } from '@element-plus/icons-vue'
import { authService } from '@/services/api'

export default {
  name: 'DashboardView',
  components: {
    ArrowDown,
    Document,
    Odometer,
    Files
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    
    const activeMenu = computed(() => {
      return route.path
    })
    
    const handleCommand = async (command) => {
      if (command === 'logout') {
        try {
          await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          })
          
          await authService.logout()
          router.push({ name: 'Login' })
        } catch (error) {
          // 用户取消操作
          if (error !== 'cancel') {
            console.error('退出登录失败:', error)
          }
        }
      }
    }
    
    return {
      activeMenu,
      handleCommand
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
}

.dashboard-container .el-container {
  height: 100%;
}

.dashboard-container .el-container:last-child {
  flex: 1;
}

.el-header {
  background-color: #304156;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-logo {
  font-size: 18px;
  font-weight: bold;
}

.el-dropdown-link {
  color: #fff;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.el-aside {
  background-color: #304156;
  color: #bfcbd9;
}

.el-menu-vertical {
  border-right: none;
}

.el-main {
  background-color: #f0f2f5;
  padding: 20px;
}
</style>