<template>
  <div class="page-container">
    <div class="page-header">
      <h2>资产详情</h2>
      <div class="header-actions">
        <el-button @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
        <el-button type="primary" @click="handleEdit">
          <el-icon><Edit /></el-icon>
          编辑
        </el-button>
        <el-dropdown @command="handleStatusChange">
          <el-button type="warning">
            更改状态<el-icon class="el-icon--right"><arrow-down /></el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="ACTIVE" :disabled="asset.status === 'ACTIVE'">激活</el-dropdown-item>
              <el-dropdown-item command="INACTIVE" :disabled="asset.status === 'INACTIVE'">停用</el-dropdown-item>
              <el-dropdown-item command="MAINTENANCE" :disabled="asset.status === 'MAINTENANCE'">维护中</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    
    <div class="content-layout">
      <div class="main-content">
        <el-card v-loading="loading">
          <template #header>
            <div class="card-header">
              <span>基本信息</span>
            </div>
          </template>
          
          <el-descriptions :column="2" border>
            <el-descriptions-item label="资产名称">{{ asset.name || '-' }}</el-descriptions-item>
            <el-descriptions-item label="资产类型">
              <el-tag :type="getTypeTagType(asset.type)">{{ getTypeLabel(asset.type) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="资产状态">
              <el-tag :type="getStatusTagType(asset.status)">{{ getStatusLabel(asset.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="所属项目">{{ asset.projectName || '-' }}</el-descriptions-item>
            <el-descriptions-item label="资产值">{{ asset.value || '-' }}</el-descriptions-item>
            <el-descriptions-item label="创建人">{{ asset.createdBy || '-' }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDateTime(asset.createdAt) }}</el-descriptions-item>
            <el-descriptions-item label="更新时间">{{ formatDateTime(asset.updatedAt) }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
        
        <el-card class="description-card">
          <template #header>
            <div class="card-header">
              <span>资产描述</span>
            </div>
          </template>
          
          <div class="description-content">
            <p v-if="asset.description">{{ asset.description }}</p>
            <p v-else class="no-description">暂无描述</p>
          </div>
        </el-card>
      </div>
      
      <div class="sidebar">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>资产统计</span>
            </div>
          </template>
          
          <div class="stats-content">
            <div class="stat-item">
              <div class="stat-label">运行时长</div>
              <div class="stat-value">{{ calculateUptime() }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">最后更新</div>
              <div class="stat-value">{{ getLastUpdateTime() }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">风险等级</div>
              <div class="stat-value">
                <el-tag :type="getRiskTagType()">{{ getRiskLevel() }}</el-tag>
              </div>
            </div>
          </div>
        </el-card>
        
        <el-card class="timeline-card">
          <template #header>
            <div class="card-header">
              <span>操作记录</span>
            </div>
          </template>
          
          <el-timeline>
            <el-timeline-item
              v-for="(event, index) in assetEvents"
              :key="index"
              :timestamp="formatDateTime(event.timestamp)"
              :type="getEventType(event.type)">
              {{ event.description }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { assetService } from '@/services/api'
import { ArrowLeft, Edit, ArrowDown } from '@element-plus/icons-vue'

export default {
  name: 'AssetDetail',
  components: {
    ArrowLeft,
    Edit,
    ArrowDown
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const loading = ref(false)
    
    const asset = reactive({
      id: null,
      name: '',
      type: '',
      status: '',
      description: '',
      projectId: null,
      projectName: '',
      ipAddress: '',
      port: '',
      url: '',
      operatingSystem: '',
      serviceVersion: '',
      createdBy: '',
      createdAt: null,
      updatedAt: null
    })
    
    const assetEvents = ref([
      {
        type: 'create',
        description: '资产创建',
        timestamp: new Date()
      }
    ])
    
    const typeOptions = [
      { value: 'WEB', label: 'Web应用' },
      { value: 'MOBILE', label: '移动应用' },
      { value: 'NETWORK', label: '网络设备' },
      { value: 'SERVER', label: '服务器' },
      { value: 'DATABASE', label: '数据库' },
      { value: 'API', label: 'API接口' },
      { value: 'IOT', label: '物联网设备' },
      { value: 'OTHER', label: '其他' }
    ]
    
    const statusOptions = [
      { value: 'ACTIVE', label: '活跃' },
      { value: 'INACTIVE', label: '停用' },
      { value: 'MAINTENANCE', label: '维护中' }
    ]
    
    const getTypeLabel = (type) => {
      const option = typeOptions.find(item => item.value === type)
      return option ? option.label : type
    }
    
    const getTypeTagType = (type) => {
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
    
    const getStatusLabel = (status) => {
      const option = statusOptions.find(item => item.value === status)
      return option ? option.label : status
    }
    
    const getStatusTagType = (status) => {
      const statusMap = {
        'ACTIVE': 'success',
        'INACTIVE': 'danger',
        'MAINTENANCE': 'warning'
      }
      return statusMap[status] || ''
    }
    
    const formatDateTime = (dateTime) => {
      if (!dateTime) return '-'
      return new Date(dateTime).toLocaleString('zh-CN')
    }
    
    const calculateUptime = () => {
      if (!asset.createdAt) return '-'
      
      const now = new Date()
      const created = new Date(asset.createdAt)
      const diffMs = now - created
      const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24))
      
      if (diffDays === 0) {
        const diffHours = Math.floor(diffMs / (1000 * 60 * 60))
        return `${diffHours} 小时`
      }
      
      return `${diffDays} 天`
    }
    
    const getLastUpdateTime = () => {
      if (!asset.updatedAt) return '-'
      
      const now = new Date()
      const updated = new Date(asset.updatedAt)
      const diffMs = now - updated
      const diffMinutes = Math.floor(diffMs / (1000 * 60))
      
      if (diffMinutes < 60) {
        return `${diffMinutes} 分钟前`
      }
      
      const diffHours = Math.floor(diffMinutes / 60)
      if (diffHours < 24) {
        return `${diffHours} 小时前`
      }
      
      const diffDays = Math.floor(diffHours / 24)
      return `${diffDays} 天前`
    }
    
    const getRiskLevel = () => {
      // 根据资产类型和状态计算风险等级
      if (asset.status === 'INACTIVE') return '低风险'
      if (asset.status === 'MAINTENANCE') return '中风险'
      
      const highRiskTypes = ['WEB_APPLICATION', 'DATABASE', 'API']
      if (highRiskTypes.includes(asset.type)) {
        return '高风险'
      }
      
      return '中风险'
    }
    
    const getRiskTagType = () => {
      const riskLevel = getRiskLevel()
      const riskMap = {
        '低风险': 'success',
        '中风险': 'warning',
        '高风险': 'danger'
      }
      return riskMap[riskLevel] || 'info'
    }
    
    const getEventType = (type) => {
      const eventTypeMap = {
        'create': 'success',
        'update': 'primary',
        'status_change': 'warning',
        'delete': 'danger'
      }
      return eventTypeMap[type] || 'info'
    }
    
    const fetchAsset = async () => {
      loading.value = true
      try {
        const response = await assetService.getById(route.params.id)
        const assetData = response.data || {}
        
        Object.assign(asset, assetData)
        
        // 模拟事件数据
        assetEvents.value = [
          {
            type: 'create',
            description: '资产创建',
            timestamp: assetData.createdAt
          },
          {
            type: 'update',
            description: '资产信息更新',
            timestamp: assetData.updatedAt
          }
        ].filter(event => event.timestamp)
      } catch (error) {
        console.error('获取资产详情失败:', error)
        ElMessage.error('获取资产详情失败')
      } finally {
        loading.value = false
      }
    }
    
    const handleEdit = () => {
      router.push(`/dashboard/assets/${route.params.id}/edit`)
    }
    
    const handleStatusChange = async (status) => {
      try {
        await assetService.updateStatus(route.params.id, status)
        ElMessage.success('资产状态更新成功')
        
        // 更新本地状态
        asset.status = status
        asset.updatedAt = new Date().toISOString()
        
        // 添加事件记录
        assetEvents.value.unshift({
          type: 'status_change',
          description: `状态变更为：${getStatusLabel(status)}`,
          timestamp: new Date().toISOString()
        })
      } catch (error) {
        console.error('更新资产状态失败:', error)
        ElMessage.error('更新资产状态失败')
      }
    }
    
    const goBack = () => {
      router.go(-1)
    }
    
    onMounted(() => {
      fetchAsset()
    })
    
    return {
      loading,
      asset,
      assetEvents,
      getTypeLabel,
      getTypeTagType,
      getStatusLabel,
      getStatusTagType,
      formatDateTime,
      calculateUptime,
      getLastUpdateTime,
      getRiskLevel,
      getRiskTagType,
      getEventType,
      handleEdit,
      handleStatusChange,
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
  margin-top: var(--spacing-lg);
}

.main-content {
  flex: 1;
}

.sidebar {
  width: 300px;
  flex-shrink: 0;
}

.description-card {
  margin-top: var(--spacing-lg);
}

.description-content {
  min-height: 100px;
  line-height: 1.6;
}

.no-description {
  color: var(--color-text-secondary);
  font-style: italic;
}

.stats-content {
  padding: var(--spacing-md) 0;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-sm) 0;
  border-bottom: 1px solid var(--border-color-light);
}

.stat-item:last-child {
  border-bottom: none;
}

.stat-label {
  color: var(--color-text-secondary);
  font-size: var(--font-size-sm);
}

.stat-value {
  font-weight: 500;
  color: var(--color-text-primary);
}

.timeline-card {
  margin-top: var(--spacing-lg);
}

.el-timeline {
  padding-left: 0;
}
</style>