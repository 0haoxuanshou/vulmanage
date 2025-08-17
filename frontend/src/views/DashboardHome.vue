<template>
  <div class="dashboard-home">
    <div class="page-header">
      <h2>系统概览</h2>
    </div>
    

    
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
import { Refresh, Cpu, MemoryCard, HardDrive, Connection } from '@element-plus/icons-vue'
import { systemService } from '@/services/api'

export default {
  name: 'DashboardHome',
  components: {
    Refresh,
    Cpu,
    MemoryCard,
    HardDrive,
    Connection
  },
  setup() {
    const systemInfoLoading = ref(false)
    
    const systemInfo = ref({})
    

    
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
    })
    
    return {
      systemInfoLoading,
      systemInfo,
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