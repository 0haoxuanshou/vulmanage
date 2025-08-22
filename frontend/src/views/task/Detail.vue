<template>
  <div class="page-container">
    <div class="page-header">
      <h2>任务详情</h2>
      <div class="header-actions">
        <el-button @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <el-button type="warning" @click="handleEdit">
          <el-icon><Edit /></el-icon>
          编辑
        </el-button>
      </div>
    </div>
    
    <div v-loading="loading" class="detail-container">
      <el-row :gutter="24">
        <el-col :span="16">
          <el-card class="detail-card">
            <template #header>
              <div class="card-header">
                <h3>基本信息</h3>
              </div>
            </template>
            
            <el-descriptions :column="2" border>
              <el-descriptions-item label="任务名称">
                <span class="task-name">{{ task.name }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="任务类型">
                <el-tag>{{ getTypeLabel(task.type) }}</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="任务状态">
                <el-tag :type="getStatusType(task.status)">
                  {{ getStatusLabel(task.status) }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="所属项目">
                <el-link type="primary" @click="viewProject(task.projectId)">
                  {{ task.projectName }}
                </el-link>
              </el-descriptions-item>
              <el-descriptions-item label="创建人">
                {{ task.createdUserName }}
              </el-descriptions-item>
              <el-descriptions-item label="创建时间">
                {{ formatDateTime(task.createdAt) }}
              </el-descriptions-item>
              <el-descriptions-item label="开始时间">
                {{ formatDateTime(task.startedAt) }}
              </el-descriptions-item>
              <el-descriptions-item label="完成时间">
                {{ formatDateTime(task.completedAt) }}
              </el-descriptions-item>
            </el-descriptions>
            
            <div class="description-section" v-if="task.description">
              <h4>任务描述</h4>
              <div class="description-content" v-html="task.description"></div>
            </div>
          </el-card>
        </el-col>
        
        <el-col :span="8">

          

        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { taskService } from '@/services/api'
import { ArrowLeft, Edit } from '@element-plus/icons-vue'

export default {
  name: 'TaskDetail',
  components: {
    ArrowLeft,
    Edit
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const loading = ref(false)
    const task = ref({})
    
    const typeOptions = [
      { value: 'PENETRATION_TEST', label: '渗透测试' },
      { value: 'CODE_REVIEW', label: '代码审计' },
      { value: 'VULNERABILITY_SCAN', label: '漏洞扫描' },
      { value: 'SECURITY_ASSESSMENT', label: '安全评估' },
      { value: 'COMPLIANCE_CHECK', label: '合规检查' },
      { value: 'INCIDENT_RESPONSE', label: '应急响应' },
      { value: 'SECURITY_TRAINING', label: '安全培训' },
      { value: 'OTHER', label: '其他' }
    ]
    
    const statusOptions = [
      { value: 'PENDING', label: '待处理' },
      { value: 'IN_PROGRESS', label: '进行中' },
      { value: 'COMPLETED', label: '已完成' },
      { value: 'CANCELLED', label: '已取消' }
    ]
    
    const getTypeLabel = (type) => {
      const option = typeOptions.find(item => item.value === type)
      return option ? option.label : type
    }
    
    const getStatusLabel = (status) => {
      const option = statusOptions.find(item => item.value === status)
      return option ? option.label : status
    }
    
    const getStatusType = (status) => {
      const map = {
        'PENDING': 'info',
        'IN_PROGRESS': 'warning',
        'COMPLETED': 'success',
        'CANCELLED': 'danger'
      }
      return map[status] || 'info'
    }
    
    const formatDateTime = (dateTime) => {
      if (!dateTime) return '-'
      const date = new Date(dateTime)
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      })
    }
    

    
    const fetchTask = async () => {
      loading.value = true
      try {
        const response = await taskService.getById(route.params.id)
        task.value = response.data || {}
      } catch (error) {
        console.error('获取任务详情失败:', error)
        ElMessage.error('获取任务详情失败')
      } finally {
        loading.value = false
      }
    }
    
    const goBack = () => {
      router.go(-1)
    }
    
    const handleEdit = () => {
      router.push(`/dashboard/tasks/${route.params.id}/edit`)
    }
    

    
    const viewProject = (projectId) => {
      router.push(`/dashboard/projects/${projectId}`)
    }
    
    onMounted(() => {
      fetchTask()
    })
    
    return {
      loading,
      task,
      getTypeLabel,
      getStatusLabel,
      getStatusType,
      formatDateTime,
      goBack,
      handleEdit,
      viewProject
    }
  }
}
</script>

<style scoped>
@import '@/assets/styles/common.css';

.detail-container {
  margin-top: var(--spacing-lg);
}

.detail-card,
.info-card {
  margin-bottom: var(--spacing-lg);
}

.task-name {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
}

.description-section {
  margin-top: var(--spacing-xl);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-light);
}

.description-section h4 {
  margin: 0 0 var(--spacing-md) 0;
  color: var(--text-primary);
  font-size: 16px;
  font-weight: 500;
}

.description-content {
  line-height: 1.6;
  color: var(--text-regular);
  background: var(--background-color);
  padding: var(--spacing-md);
  border-radius: var(--border-radius);
  border: 1px solid var(--border-light);
 border-bottom: 1px solid var(--border-light);
}

.stats-item:last-child {
  border-bottom: none;
}

.stats-label {
  color: var(--text-secondary);
  font-size: 14px;
}

.stats-value {
  color: var(--text-primary);
  font-weight: 500;
}

.el-timeline {
  padding-left: 0;
}
</style>