<template>
  <div class="dashboard-home">
    <div class="page-header">
      <h2>系统概览</h2>
    </div>
    
    <!-- 数据统计区域 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <el-card class="stats-card vulnerability-stats">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon><document /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ statsData.vulnerabilityCount }}</div>
              <div class="stats-label">漏洞数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card project-stats">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon><folder /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ statsData.projectCount }}</div>
              <div class="stats-label">项目数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card task-stats">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon><list /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ statsData.taskCount }}</div>
              <div class="stats-label">任务数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stats-card asset-stats">
          <div class="stats-content">
            <div class="stats-icon">
              <el-icon><monitor /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-number">{{ statsData.assetCount }}</div>
              <div class="stats-label">资产数量</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 系统信息展示区域 -->
    <el-row :gutter="20" class="system-info-row">
      <el-col :span="24">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>服务器配置信息</span>
              <el-button type="text" @click="refreshSystemInfo" :loading="systemInfoLoading">
                <el-icon><refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          
          <el-row :gutter="20">
            <!-- CPU信息 -->
            <el-col :span="6">
              <div class="system-info-card">
                <div class="info-header">
                  <el-icon class="info-icon cpu"><cpu /></el-icon>
                  <span class="info-title">CPU</span>
                </div>
                <div class="info-content">
                  <div class="info-item">
                    <span class="info-label">核心数:</span>
                    <span class="info-value">{{ systemInfo.cpu?.processors || 'N/A' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">架构:</span>
                    <span class="info-value">{{ systemInfo.cpu?.architecture || 'N/A' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">使用率:</span>
                    <span class="info-value">{{ systemInfo.cpu?.systemCpuUsage || 'N/A' }}</span>
                  </div>
                </div>
              </div>
            </el-col>
            
            <!-- 内存信息 -->
            <el-col :span="6">
              <div class="system-info-card">
                <div class="info-header">
                  <el-icon class="info-icon memory"><memory-card /></el-icon>
                  <span class="info-title">内存</span>
                </div>
                <div class="info-content">
                  <div class="info-item">
                    <span class="info-label">总计:</span>
                    <span class="info-value">{{ systemInfo.memory?.system?.total || 'N/A' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">已用:</span>
                    <span class="info-value">{{ systemInfo.memory?.system?.used || 'N/A' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">使用率:</span>
                    <span class="info-value">{{ systemInfo.memory?.system?.usagePercent || 'N/A' }}</span>
                  </div>
                </div>
              </div>
            </el-col>
            
            <!-- 磁盘信息 -->
            <el-col :span="6">
              <div class="system-info-card">
                <div class="info-header">
                  <el-icon class="info-icon disk"><hard-drive /></el-icon>
                  <span class="info-title">磁盘</span>
                </div>
                <div class="info-content">
                  <div class="info-item">
                    <span class="info-label">总计:</span>
                    <span class="info-value">{{ systemInfo.disk?.summary?.total || 'N/A' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">已用:</span>
                    <span class="info-value">{{ systemInfo.disk?.summary?.used || 'N/A' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">使用率:</span>
                    <span class="info-value">{{ systemInfo.disk?.summary?.usagePercent || 'N/A' }}</span>
                  </div>
                </div>
              </div>
            </el-col>
            
            <!-- 网络信息 -->
            <el-col :span="6">
              <div class="system-info-card">
                <div class="info-header">
                  <el-icon class="info-icon network"><connection /></el-icon>
                  <span class="info-title">网络</span>
                </div>
                <div class="info-content">
                  <div class="info-item">
                    <span class="info-label">主机名:</span>
                    <span class="info-value">{{ systemInfo.network?.hostname || 'N/A' }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">网卡数:</span>
                    <span class="info-value">{{ systemInfo.network?.interfaces?.length || 0 }}</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">系统:</span>
                    <span class="info-value">{{ systemInfo.os?.name || 'N/A' }}</span>
                  </div>
                </div>
              </div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
    

    

  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Cpu, MemoryCard, HardDrive, Connection, Document, Folder, List, Monitor } from '@element-plus/icons-vue'
import { systemService, vulnerabilityService, projectService, taskService, assetService } from '@/services/api'

export default {
  name: 'DashboardHome',
  components: {
    Refresh,
    Cpu,
    MemoryCard,
    HardDrive,
    Connection,
    Document,
    Folder,
    List,
    Monitor
  },
  setup() {
    const systemInfoLoading = ref(false)
    const statsLoading = ref(false)
    
    const systemInfo = ref({})
    const statsData = ref({
      vulnerabilityCount: 0,
      projectCount: 0,
      taskCount: 0,
      assetCount: 0
    })
    
    const fetchStats = async () => {
      statsLoading.value = true
      try {
        const [vulnerabilities, projects, tasks, assets] = await Promise.all([
          vulnerabilityService.getAll({ page: 1, size: 1 }),
          projectService.getAll({ page: 1, size: 1 }),
          taskService.getAll({ page: 1, size: 1 }),
          assetService.getAll({ page: 1, size: 1 })
        ])
        
        statsData.value = {
          vulnerabilityCount: vulnerabilities.data?.total || 0,
          projectCount: projects.data?.total || 0,
          taskCount: tasks.data?.total || 0,
          assetCount: assets.data?.total || 0
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
        ElMessage.error('获取统计数据失败')
      } finally {
        statsLoading.value = false
      }
    }
    
    const fetchSystemInfo = async () => {
      systemInfoLoading.value = true
      try {
        const response = await systemService.getSystemInfo()
        systemInfo.value = response.data || {}
      } catch (error) {
        console.error('获取系统信息失败:', error)
        ElMessage.error('获取系统信息失败')
      } finally {
        systemInfoLoading.value = false
      }
    }
    
    const refreshSystemInfo = () => {
      fetchSystemInfo()
    }
    
    onMounted(() => {
      fetchSystemInfo()
      fetchStats()
    })
    
    return {
      systemInfoLoading,
      statsLoading,
      systemInfo,
      statsData,
      refreshSystemInfo
    }
  }
}
</script>

<style scoped>
.dashboard-home {
  padding: 0;
}

/* 页面特定样式 */

/* 统计卡片样式 */
.stats-row {
  margin-bottom: 20px;
}

.stats-card {
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
}

.stats-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stats-content {
  display: flex;
  align-items: center;
  padding: 20px;
}

.stats-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 24px;
  color: white;
}

.vulnerability-stats .stats-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.project-stats .stats-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.task-stats .stats-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.asset-stats .stats-icon {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stats-info {
  flex: 1;
}

.stats-number {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.stats-label {
  font-size: 14px;
  color: #909399;
  font-weight: 500;
}

/* 系统信息样式 */
.system-info-row {
  margin-bottom: 20px;
}

.system-info-card {
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background-color: #fff;
  height: 140px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s ease;
}

.system-info-card:hover {
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.15);
}

.info-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.info-icon {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  font-size: 16px;
  color: white;
}

.info-icon.cpu {
  background-color: #409eff;
}

.info-icon.memory {
  background-color: #67c23a;
}

.info-icon.disk {
  background-color: #e6a23c;
}

.info-icon.network {
  background-color: #f56c6c;
}

.info-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.info-content {
  flex: 1;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.info-item:last-child {
  margin-bottom: 0;
}

.info-label {
  font-size: 13px;
  color: #909399;
}

.info-value {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
}
</style>