<template>
  <div class="setup-container">
    <el-card class="setup-card">
      <template #header>
        <div class="card-header">
          <h2>系统初始化设置</h2>
          <p class="subtitle">欢迎使用漏洞管理系统，请设置初始管理员账号</p>
        </div>
      </template>
      <el-form :model="setupForm" :rules="rules" ref="setupFormRef" label-position="top">
        <el-form-item label="管理员用户名" prop="username">
          <el-input
            v-model="setupForm.username"
            placeholder="请输入管理员用户名"
            @keyup.enter="handleSetup"
          />
        </el-form-item>
        <el-form-item label="管理员邮箱" prop="email">
          <el-input
            v-model="setupForm.email"
            type="email"
            placeholder="请输入管理员邮箱"
            @keyup.enter="handleSetup"
          />
        </el-form-item>
        <el-form-item label="管理员姓名" prop="fullName">
          <el-input
            v-model="setupForm.fullName"
            placeholder="请输入管理员姓名"
            @keyup.enter="handleSetup"
          />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input 
            v-model="setupForm.password" 
            type="password" 
            placeholder="请输入密码（至少8位）" 
            show-password 
            @keyup.enter="handleSetup"
          />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input 
            v-model="setupForm.confirmPassword" 
            type="password" 
            placeholder="请再次输入密码" 
            show-password 
            @keyup.enter="handleSetup"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="loading" @click="handleSetup" style="width: 100%">
            创建管理员账号
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { setupService } from '@/services/api'

export default {
  name: 'SetupView',
  setup() {
    const router = useRouter()
    const setupFormRef = ref(null)
    const loading = ref(false)
    
    const setupForm = reactive({
      username: '',
      email: '',
      fullName: '',
      password: '',
      confirmPassword: ''
    })
    
    const validateConfirmPassword = (rule, value, callback) => {
      if (value !== setupForm.password) {
        callback(new Error('两次输入的密码不一致'))
      } else {
        callback()
      }
    }
    
    const rules = {
      username: [
        { required: true, message: '请输入管理员用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度应在3-20个字符之间', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入管理员邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
      ],
      fullName: [
        { required: true, message: '请输入管理员姓名', trigger: 'blur' },
        { min: 2, max: 50, message: '姓名长度应在2-50个字符之间', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 8, message: '密码长度至少8位', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        { validator: validateConfirmPassword, trigger: 'blur' }
      ]
    }
    
    const handleSetup = () => {
      setupFormRef.value.validate(async (valid) => {
        if (valid) {
          loading.value = true
          try {
            await setupService.createAdmin({
              username: setupForm.username,
              email: setupForm.email,
              fullName: setupForm.fullName,
              password: setupForm.password
            })
            ElMessage.success('管理员账号创建成功，即将跳转到登录页面')
            setTimeout(() => {
              router.push({ name: 'Login' })
            }, 2000)
          } catch (error) {
            console.error('创建管理员账号失败:', error)
            ElMessage.error(error.response?.data?.message || '创建管理员账号失败，请重试')
          } finally {
            loading.value = false
          }
        }
      })
    }
    
    return {
      setupFormRef,
      setupForm,
      rules,
      loading,
      handleSetup
    }
  }
}
</script>

<style scoped>
.setup-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
}

.setup-card {
  width: 500px;
  max-width: 100%;
}

.card-header {
  text-align: center;
}

.card-header h2 {
  margin: 0 0 10px 0;
  color: #303133;
}

.subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}
</style>