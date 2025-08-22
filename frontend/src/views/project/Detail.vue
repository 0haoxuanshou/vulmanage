<template>
  <div class="page-container">
    <div v-loading="loading">
      <!-- 页面头部 -->
      <div class="page-header">
        <div>
          <el-button @click="goBack" type="text" icon="ArrowLeft">返回</el-button>
          <h1>{{ project.name || '项目详情' }}</h1>
        </div>

      </div>
      
      <div class="content-layout">
      <div class="main-content">
        <!-- 项目基本信息和统计 -->
        <div class="info-stats-container">
          <el-card v-loading="loading" class="info-card">
            <template #header>
              <div class="card-header">
                <span>基本信息</span>
              </div>
            </template>
            
            <el-descriptions :column="2" border>
              <el-descriptions-item label="项目ID">{{ project.id || '-' }}</el-descriptions-item>
              <el-descriptions-item label="项目名称">{{ project.name || '-' }}</el-descriptions-item>
              <el-descriptions-item label="创建人">{{ project.createdUserName || '-' }}</el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ formatDateTime(project.createdAt) }}</el-descriptions-item>
              <el-descriptions-item label="更新时间" :span="2">{{ formatDateTime(project.updatedAt) }}</el-descriptions-item>
            </el-descriptions>
          </el-card>
          
          <el-card class="stats-card">
            <template #header>
              <div class="card-header">
                <span>项目统计</span>
              </div>
            </template>
            
            <div class="stats-content">
              <div class="stat-item">
                <div class="stat-label">项目创建</div>
                <div class="stat-value">{{ getProjectAge() }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">任务总数</div>
                <div class="stat-value">{{ taskStats.total }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">资产总数</div>
                <div class="stat-value">{{ assetStats.total }}</div>
              </div>
              <div class="stat-item">
                <div class="stat-label">完成率</div>
                <div class="stat-value">{{ getCompletionRate() }}%</div>
              </div>
            </div>
          </el-card>
        </div>
        
        <!-- 标签页内容 -->
        <el-card class="tabs-card">
          <el-tabs v-model="activeTab" @tab-click="handleTabClick">
            <!-- 任务标签页 -->
            <el-tab-pane label="任务管理" name="tasks">
              <div class="tab-content">
                <div class="tab-header">
                  <div class="tab-title">
                    <h3>项目任务</h3>
                    <span class="count-badge">{{ taskStats.total }} 个任务</span>
                  </div>
                  <div class="tab-actions">
                    <el-button type="primary" size="small" @click="handleAddTask">
                      <el-icon><Plus /></el-icon>
                      新增任务
                    </el-button>
                  </div>
                </div>
                
                <div class="stats-row">
                  <div class="stat-item">
                    <div class="stat-value">{{ taskStats.pending }}</div>
                    <div class="stat-label">待处理</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-value">{{ taskStats.inProgress }}</div>
                    <div class="stat-label">进行中</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-value">{{ taskStats.completed }}</div>
                    <div class="stat-label">已完成</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-value">{{ taskStats.cancelled }}</div>
                    <div class="stat-label">已取消</div>
                  </div>
                </div>
                
                <el-table
                  v-loading="taskLoading"
                  :data="taskList"
                  stripe
                  style="width: 100%"
                  class="task-table">
                  
                  <el-table-column prop="name" label="任务名称" min-width="150">
                    <template #default="{ row }">
                      <el-link type="primary" @click="handleViewTask(row.id)">{{ row.name }}</el-link>
                    </template>
                  </el-table-column>
                  
                  <el-table-column prop="type" label="任务类型" width="120">
                    <template #default="{ row }">
                      <el-tag :type="getTaskTypeTagType(row.type)">{{ getTaskTypeLabel(row.type) }}</el-tag>
                    </template>
                  </el-table-column>
                  
                  <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                      <el-tag :type="getTaskStatusTagType(row.status)">{{ getTaskStatusLabel(row.status) }}</el-tag>
                    </template>
                  </el-table-column>
                  
                  <el-table-column prop="createdBy" label="创建人" width="100" />
                  
                  <el-table-column prop="createdAt" label="创建时间" width="160">
                    <template #default="{ row }">
                      {{ formatDateTime(row.createdAt) }}
                    </template>
                  </el-table-column>
                  
                  <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{ row }">
                      <el-button size="small" @click="handleViewTask(row.id)">查看</el-button>
                      <el-button size="small" type="primary" @click="handleEditTask(row.id)">编辑</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                
                <div class="table-footer" v-if="taskList.length > 0">
                  <el-link type="primary" @click="viewAllTasks">查看全部任务 →</el-link>
                </div>
              </div>
            </el-tab-pane>
            
            <!-- 资产标签页 -->
            <el-tab-pane label="资产管理" name="assets">
              <div class="tab-content">
                <div class="tab-header">
                  <div class="tab-title">
                    <h3>项目资产</h3>
                    <span class="count-badge">{{ assetStats.total }} 个资产</span>
                  </div>
                  <div class="tab-actions">
                    <el-button type="primary" size="small" @click="handleAddAsset">
                      <el-icon><Plus /></el-icon>
                      新增资产
                    </el-button>
                  </div>
                </div>
                
                <div class="stats-row">
                  <div class="stat-item">
                    <div class="stat-value">{{ assetStats.active }}</div>
                    <div class="stat-label">活跃</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-value">{{ assetStats.inactive }}</div>
                    <div class="stat-label">停用</div>
                  </div>
                  <div class="stat-item">
                    <div class="stat-value">{{ assetStats.maintenance }}</div>
                    <div class="stat-label">维护中</div>
                  </div>
                </div>
                
                <el-table
                  v-loading="assetLoading"
                  :data="assetList"
                  stripe
                  style="width: 100%"
                  class="asset-table">
                  
                  <el-table-column prop="name" label="资产名称" min-width="150">
                    <template #default="{ row }">
                      <el-link type="primary" @click="handleViewAsset(row.id)">{{ row.name }}</el-link>
                    </template>
                  </el-table-column>
                  
                  <el-table-column prop="type" label="资产类型" width="120">
                    <template #default="{ row }">
                      <el-tag :type="getAssetTypeTagType(row.type)">{{ getAssetTypeLabel(row.type) }}</el-tag>
                    </template>
                  </el-table-column>
                  
                  <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                      <el-tag :type="getAssetStatusTagType(row.status)">{{ getAssetStatusLabel(row.status) }}</el-tag>
                    </template>
                  </el-table-column>
                  
                  <el-table-column prop="value" label="资产值" width="130" />
                  
                  <el-table-column prop="createdBy" label="创建人" width="100" />
                  
                  <el-table-column prop="createdAt" label="创建时间" width="160">
                    <template #default="{ row }">
                      {{ formatDateTime(row.createdAt) }}
                    </template>
                  </el-table-column>
                  
                  <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{ row }">
                      <el-button size="small" @click="handleViewAsset(row.id)">查看</el-button>
                      <el-button size="small" type="primary" @click="handleEditAsset(row.id)">编辑</el-button>
                    </template>
                  </el-table-column>
                </el-table>
                
                <div class="table-footer" v-if="assetList.length > 0">
                  <el-link type="primary" @click="viewAllAssets">查看全部资产 →</el-link>
                </div>
              </div>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { projectService, taskService, assetService } from '@/services/api'
import { Plus } from '@element-plus/icons-vue'

export default {
  name: 'ProjectDetail',
  components: {
    Plus
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const loading = ref(false)
    const taskLoading = ref(false)
    const assetLoading = ref(false)
    const activeTab = ref('tasks')
    
    const project = reactive({
      id: null,
      name: '',
      createdUserName: '',
      createdAt: null,
      updatedAt: null
    })
    
    const taskList = ref([])
    const assetList = ref([])
    
    const taskStats = reactive({
      total: 0,
      pending: 0,
      inProgress: 0,
      completed: 0,
      cancelled: 0
    })
    
    const assetStats = reactive({
      total: 0,
      active: 0,
      inactive: 0,
      maintenance: 0
    })
    
    const taskTypeOptions = [
      { value: 'PENETRATION_TEST', label: '渗透测试' },
      { value: 'CODE_REVIEW', label: '代码审计' },
      { value: 'VULNERABILITY_SCAN', label: '漏洞扫描' },
      { value: 'SECURITY_ASSESSMENT', label: '安全评估' },
      { value: 'COMPLIANCE_CHECK', label: '合规检查' },
      { value: 'INCIDENT_RESPONSE', label: '应急响应' },
      { value: 'SECURITY_TRAINING', label: '安全培训' },
      { value: 'OTHER', label: '其他' }
    ]
    
    const taskStatusOptions = [
      { value: 'PENDING', label: '待处理' },
      { value: 'IN_PROGRESS', label: '进行中' },
      { value: 'COMPLETED', label: '已完成' },
      { value: 'CANCELLED', label: '已取消' }
    ]
    
    const assetTypeOptions = [
      { value: 'WEB', label: 'Web应用' },
      { value: 'MOBILE', label: '移动应用' },
      { value: 'NETWORK', label: '网络设备' },
      { value: 'SERVER', label: '服务器' },
      { value: 'DATABASE', label: '数据库' },
      { value: 'API', label: 'API接口' },
      { value: 'IOT', label: '物联网设备' },
      { value: 'OTHER', label: '其他' }
    ]
    
    const assetStatusOptions = [
      { value: 'ACTIVE', label: '活跃' },
      { value: 'INACTIVE', label: '停用' },
      { value: 'MAINTENANCE', label: '维护中' }
    ]
    
    const formatDateTime = (dateTime) => {
      if (!dateTime) return '-'
      return new Date(dateTime).toLocaleString('zh-CN')
    }
    
    const getTaskTypeLabel = (type) => {
      const option = taskTypeOptions.find(item => item.value === type)
      return option ? option.label : type
    }
    
    const getTaskTypeTagType = (type) => {
      const typeMap = {
        'PENETRATION_TEST': 'danger',
        'CODE_REVIEW': 'warning',
        'VULNERABILITY_SCAN': 'primary',
        'SECURITY_ASSESSMENT': 'success',
        'COMPLIANCE_CHECK': 'info',
        'INCIDENT_RESPONSE': 'danger',
        'SECURITY_TRAINING': 'success',
        'OTHER': ''
      }
      return typeMap[type] || ''
    }
    
    const getTaskStatusLabel = (status) => {
      const option = taskStatusOptions.find(item => item.value === status)
      return option ? option.label : status
    }
    
    const getTaskStatusTagType = (status) => {
      const statusMap = {
        'PENDING': 'info',
        'IN_PROGRESS': 'warning',
        'COMPLETED': 'success',
        'CANCELLED': 'danger'
      }
      return statusMap[status] || ''
    }
    
    const getAssetTypeLabel = (type) => {
      const option = assetTypeOptions.find(item => item.value === type)
      return option ? option.label : type
    }
    
    const getAssetTypeTagType = (type) => {
      const typeMap = {
        'WEB': 'primary',
        'MOBILE': 'success',
        'NETWORK': 'warning',
        'SERVER': 'danger',
        'DATABASE': 'info',
        'API': 'primary',
        'IOT': 'warning',
        'OTHER': ''
      }
      return typeMap[type] || ''
    }
    
    const getAssetStatusLabel = (status) => {
      const option = assetStatusOptions.find(item => item.value === status)
      return option ? option.label : status
    }
    
    const getAssetStatusTagType = (status) => {
      const statusMap = {
        'ACTIVE': 'success',
        'INACTIVE': 'danger',
        'MAINTENANCE': 'warning'
      }
      return statusMap[status] || ''
    }
    
    const getProjectAge = () => {
      if (!project.createdAt) return '-'
      
      const now = new Date()
      const created = new Date(project.createdAt)
      const diffMs = now - created
      const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))
      
      if (diffDays === 0) {
        return '今天创建'
      } else if (diffDays === 1) {
        return '1天前'
      } else {
        return `${diffDays}天前`
      }
    }
    
    const getCompletionRate = () => {
      if (taskStats.total === 0) return 0
      return Math.round((taskStats.completed / taskStats.total) * 100)
    }
    
    const fetchProject = async () => {
      loading.value = true
      try {
        const response = await projectService.getById(route.params.id)
        const projectData = response.data || {}
        Object.assign(project, projectData)
      } catch (error) {
        console.error('获取项目详情失败:', error)
        ElMessage.error('获取项目详情失败')
      } finally {
        loading.value = false
      }
    }
    
    const fetchTasks = async () => {
      taskLoading.value = true
      try {
        const response = await taskService.getTasksByProjectId(route.params.id)
        const tasks = response.data || []
        
        // 只显示前5个任务
        taskList.value = tasks.slice(0, 5)
        
        // 计算统计数据
        taskStats.total = tasks.length
        taskStats.pending = tasks.filter(t => t.status === 'PENDING').length
        taskStats.inProgress = tasks.filter(t => t.status === 'IN_PROGRESS').length
        taskStats.completed = tasks.filter(t => t.status === 'COMPLETED').length
        taskStats.cancelled = tasks.filter(t => t.status === 'CANCELLED').length
      } catch (error) {
        console.error('获取任务列表失败:', error)
        ElMessage.error('获取任务列表失败')
      } finally {
        taskLoading.value = false
      }
    }
    
    const fetchAssets = async () => {
      assetLoading.value = true
      try {
        const response = await assetService.getAssetsByProjectId(route.params.id)
        const assets = response.data || []
        
        // 只显示前5个资产
        assetList.value = assets.slice(0, 5)
        
        // 计算统计数据
        assetStats.total = assets.length
        assetStats.active = assets.filter(a => a.status === 'ACTIVE').length
        assetStats.inactive = assets.filter(a => a.status === 'INACTIVE').length
        assetStats.maintenance = assets.filter(a => a.status === 'MAINTENANCE').length
      } catch (error) {
        console.error('获取资产列表失败:', error)
        ElMessage.error('获取资产列表失败')
      } finally {
        assetLoading.value = false
      }
    }
    
    const handleTabClick = (tab) => {
      if (tab.name === 'tasks' && taskList.value.length === 0) {
        fetchTasks()
      } else if (tab.name === 'assets' && assetList.value.length === 0) {
        fetchAssets()
      }
    }
    

    
    const handleAddTask = () => {
      router.push(`/dashboard/tasks/add?projectId=${route.params.id}`)
    }
    
    const handleViewTask = (taskId) => {
      router.push(`/dashboard/tasks/${taskId}`)
    }
    
    const handleEditTask = (taskId) => {
      router.push(`/dashboard/tasks/${taskId}/edit`)
    }
    
    const handleAddAsset = () => {
      router.push(`/dashboard/assets/add?projectId=${route.params.id}`)
    }
    
    const handleViewAsset = (assetId) => {
      router.push(`/dashboard/assets/${assetId}`)
    }
    
    const handleEditAsset = (assetId) => {
      router.push(`/dashboard/assets/${assetId}/edit`)
    }
    
    const viewAllTasks = () => {
      router.push(`/dashboard/tasks?projectId=${route.params.id}`)
    }
    
    const viewAllAssets = () => {
      router.push(`/dashboard/assets?projectId=${route.params.id}`)
    }
    
    const goBack = () => {
      router.go(-1)
    }
    
    onMounted(() => {
      fetchProject()
      fetchTasks() // 默认加载任务数据
      fetchAssets() // 默认加载资产数据
    })
    
    return {
      loading,
      taskLoading,
      assetLoading,
      activeTab,
      project,
      taskList,
      assetList,
      taskStats,
      assetStats,
      formatDateTime,
      getTaskTypeLabel,
      getTaskTypeTagType,
      getTaskStatusLabel,
      getTaskStatusTagType,
      getAssetTypeLabel,
      getAssetTypeTagType,
      getAssetStatusLabel,
      getAssetStatusTagType,
      getProjectAge,
      getCompletionRate,
      handleTabClick,
      handleAddTask,
      handleViewTask,
      handleEditTask,
      handleAddAsset,
      handleViewAsset,
      handleEditAsset,
      viewAllTasks,
      viewAllAssets,
      goBack
    }
  }
}
</script>

<style scoped>
@import '@/assets/styles/common.css';

.content-layout {
  display: flex;
  gap: var(--spacing-lg);
}

.main-content {
  flex: 1;
}

.info-stats-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.info-card {
  grid-column: 1;
}

.stats-card {
  grid-column: 2;
}

@media (max-width: 768px) {
  .info-stats-container {
    grid-template-columns: 1fr;
  }
  
  .info-card,
  .stats-card {
    grid-column: 1;
  }
}

.tabs-card {
  margin-top: var(--spacing-lg);
}

.tab-content {
  padding: var(--spacing-md) 0;
}

.tab-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.tab-title {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.tab-title h3 {
  margin: 0;
  font-size: 18px;
  color: var(--text-primary);
}

.count-badge {
  background: #ECF5FF;
  color: var(--primary-color);
  padding: 2px 8px;
  border-radius: var(--border-radius);
  font-size: 12px;
}

.stats-row {
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-md);
  background: var(--background-color);
  border-radius: var(--border-radius);
}

.stat-item {
  text-align: center;
  flex: 1;
}

.stat-value {
  font-size: 20px;
  font-weight: 600;
  color: var(--primary-color);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: var(--text-secondary);
}

.table-footer {
  text-align: center;
  padding: var(--spacing-md);
  border-top: 1px solid var(--border-light);
  margin-top: var(--spacing-md);
}

.stats-content {
  padding: var(--spacing-md) 0;
}

.stats-content .stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-sm) 0;
  border-bottom: 1px solid var(--border-light);
}

.stats-content .stat-item:last-child {
  border-bottom: none;
}

.stats-content .stat-label {
  color: var(--text-secondary);
  font-size: 12px;
}

.stats-content .stat-value {
  font-weight: 500;
  color: var(--text-primary);
  font-size: 14px;
}

.task-table,
.asset-table {
  margin-top: var(--spacing-md);
}
</style>