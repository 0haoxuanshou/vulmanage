<template>
  <div class="dashboard-home">
    <div class="page-header">
      <h2>系统概览</h2>
    </div>
    
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon critical">
              <el-icon><warning /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.critical }}</div>
              <div class="stats-label">严重漏洞</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon high">
              <el-icon><warning-filled /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.high }}</div>
              <div class="stats-label">高危漏洞</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon medium">
              <el-icon><info-filled /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.medium }}</div>
              <div class="stats-label">中危漏洞</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card">
          <div class="stats-content">
            <div class="stats-icon total">
              <el-icon><document /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ stats.total }}</div>
              <div class="stats-label">总漏洞数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="charts-row">
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>漏洞类型分布</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <div v-for="item in typeDistribution" :key="item.type" class="type-item">
              <span class="type-label">{{ item.label }}</span>
              <span class="type-count">{{ item.count }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="12">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>漏洞状态分布</span>
            </div>
          </template>
          <div class="chart-placeholder">
            <div v-for="item in statusDistribution" :key="item.status" class="status-item">
              <span class="status-label">{{ item.label }}</span>
              <span class="status-count">{{ item.count }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-row :gutter="20" class="recent-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>最近发现的漏洞</span>
              <el-button type="text" @click="viewAllVulnerabilities">查看全部</el-button>
            </div>
          </template>
          <el-table :data="recentVulnerabilities" style="width: 100%">
            <el-table-column prop="vulnerabilityNumber" label="漏洞编号" width="150" />
            <el-table-column prop="name" label="漏洞名称" />
            <el-table-column prop="type" label="类型" width="120">
              <template #default="scope">
                {{ getTypeLabel(scope.row.type) }}
              </template>
            </el-table-column>
            <el-table-column prop="severity" label="严重程度" width="100">
              <template #default="scope">
                <el-tag :type="getSeverityType(scope.row.severity)">
                  {{ getSeverityLabel(scope.row.severity) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="discoveryDate" label="发现日期" width="120" />
            <el-table-column label="操作" width="100">
              <template #default="scope">
                <el-button type="text" @click="viewVulnerability(scope.row.id)">查看</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Warning, WarningFilled, InfoFilled, Document } from '@element-plus/icons-vue'
import { vulnerabilityService } from '@/services/api'

export default {
  name: 'DashboardHome',
  components: {
    Warning,
    WarningFilled,
    InfoFilled,
    Document
  },
  setup() {
    const router = useRouter()
    const loading = ref(false)
    
    const stats = ref({
      critical: 0,
      high: 0,
      medium: 0,
      total: 0
    })
    
    const typeDistribution = ref([])
    const statusDistribution = ref([])
    const recentVulnerabilities = ref([])
    
    const typeOptions = [
      { value: 'SQL_INJECTION', label: 'SQL注入' },
      { value: 'XSS', label: '跨站脚本' },
      { value: 'CSRF', label: '跨站请求伪造' },
      { value: 'FILE_UPLOAD', label: '文件上传' },
      { value: 'COMMAND_INJECTION', label: '命令注入' },
      { value: 'INFORMATION_DISCLOSURE', label: '信息泄露' },
      { value: 'AUTHENTICATION_BYPASS', label: '认证绕过' },
      { value: 'AUTHORIZATION_BYPASS', label: '授权绕过' },
      { value: 'BUSINESS_LOGIC', label: '业务逻辑' },
      { value: 'OTHER', label: '其他' }
    ]
    
    const severityOptions = [
      { value: 'CRITICAL', label: '严重' },
      { value: 'HIGH', label: '高危' },
      { value: 'MEDIUM', label: '中危' },
      { value: 'LOW', label: '低危' },
      { value: 'INFO', label: '信息' }
    ]
    
    const statusOptions = [
      { value: 'OPEN', label: '未修复' },
      { value: 'IN_PROGRESS', label: '修复中' },
      { value: 'FIXED', label: '已修复' },
      { value: 'WONT_FIX', label: '不修复' },
      { value: 'FALSE_POSITIVE', label: '误报' }
    ]
    
    const getTypeLabel = (type) => {
      const option = typeOptions.find(item => item.value === type)
      return option ? option.label : type
    }
    
    const getSeverityLabel = (severity) => {
      const option = severityOptions.find(item => item.value === severity)
      return option ? option.label : severity
    }
    
    const getSeverityType = (severity) => {
      const typeMap = {
        'CRITICAL': 'danger',
        'HIGH': 'danger',
        'MEDIUM': 'warning',
        'LOW': 'info',
        'INFO': 'info'
      }
      return typeMap[severity] || 'info'
    }
    
    const fetchDashboardData = async () => {
      loading.value = true
      try {
        // 获取漏洞列表数据用于统计
        const response = await vulnerabilityService.getAll({ page: 1, size: 1000 })
        const vulnerabilities = response.data.vulnerabilities || []
        
        // 统计严重程度
        stats.value.critical = vulnerabilities.filter(v => v.severity === 'CRITICAL').length
        stats.value.high = vulnerabilities.filter(v => v.severity === 'HIGH').length
        stats.value.medium = vulnerabilities.filter(v => v.severity === 'MEDIUM').length
        stats.value.total = vulnerabilities.length
        
        // 统计类型分布
        const typeCount = {}
        vulnerabilities.forEach(v => {
          typeCount[v.type] = (typeCount[v.type] || 0) + 1
        })
        typeDistribution.value = Object.entries(typeCount).map(([type, count]) => ({
          type,
          label: getTypeLabel(type),
          count
        }))
        
        // 统计状态分布
        const statusCount = {}
        vulnerabilities.forEach(v => {
          statusCount[v.status] = (statusCount[v.status] || 0) + 1
        })
        statusDistribution.value = Object.entries(statusCount).map(([status, count]) => ({
          status,
          label: statusOptions.find(s => s.value === status)?.label || status,
          count
        }))
        
        // 最近发现的漏洞（按发现日期排序，取前5个）
        recentVulnerabilities.value = vulnerabilities
          .sort((a, b) => new Date(b.discoveryDate) - new Date(a.discoveryDate))
          .slice(0, 5)
        
      } catch (error) {
        console.error('获取dashboard数据失败:', error)
        ElMessage.error('获取dashboard数据失败')
      } finally {
        loading.value = false
      }
    }
    
    const viewAllVulnerabilities = () => {
      router.push({ name: 'VulnerabilityList' })
    }
    
    const viewVulnerability = (id) => {
      router.push({ name: 'VulnerabilityDetail', params: { id } })
    }
    
    onMounted(() => {
      fetchDashboardData()
    })
    
    return {
      loading,
      stats,
      typeDistribution,
      statusDistribution,
      recentVulnerabilities,
      getTypeLabel,
      getSeverityLabel,
      getSeverityType,
      viewAllVulnerabilities,
      viewVulnerability
    }
  }
}
</script>

<style scoped>
.dashboard-home {
  padding: 0;
}

.page-header {
  margin-bottom: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  height: 100px;
}

.stats-content {
  display: flex;
  align-items: center;
  height: 100%;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
  font-size: 24px;
  color: white;
}

.stats-icon.critical {
  background-color: #f56c6c;
}

.stats-icon.high {
  background-color: #e6a23c;
}

.stats-icon.medium {
  background-color: #409eff;
}

.stats-icon.total {
  background-color: #67c23a;
}

.stats-info {
  flex: 1;
}

.stats-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  line-height: 1;
}

.stats-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.charts-row {
  margin-bottom: 20px;
}

.chart-placeholder {
  min-height: 200px;
  padding: 20px 0;
}

.type-item, .status-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.type-item:last-child, .status-item:last-child {
  border-bottom: none;
}

.type-label, .status-label {
  font-size: 14px;
  color: #606266;
}

.type-count, .status-count {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.recent-row {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>