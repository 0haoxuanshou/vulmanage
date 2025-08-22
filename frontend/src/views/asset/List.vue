<template>
  <div class="page-container">
    <div class="page-header">
      <h2>资产管理</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增资产
        </el-button>
      </div>
    </div>
    
    <div class="search-card">
      <el-card>
        <el-form :model="searchForm" inline class="search-form">
          <el-form-item label="资产名称">
            <el-input
              v-model="searchForm.name"
              placeholder="请输入资产名称"
              clearable
              style="width: 200px"
            />
          </el-form-item>
          
          <el-form-item label="资产类型">
            <el-select v-model="searchForm.type" placeholder="请选择资产类型" clearable style="width: 150px">
              <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="资产状态">
            <el-select v-model="searchForm.status" placeholder="请选择资产状态" clearable style="width: 150px">
              <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="所属项目">
            <el-select v-model="searchForm.projectId" placeholder="请选择项目" clearable style="width: 200px">
              <el-option v-for="project in projects" :key="project.id" :label="project.name" :value="project.id" />
            </el-select>
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>
              搜索
            </el-button>
            <el-button @click="handleReset">
              <el-icon><Refresh /></el-icon>
              重置
            </el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
    
    <div class="table-container">
      <el-card>
        <el-table
          v-loading="loading"
          :data="assetData"
          stripe
          style="width: 100%"
          @selection-change="handleSelectionChange">
          
          <el-table-column type="selection" width="55" />
          
          <el-table-column prop="name" label="资产名称" min-width="150">
            <template #default="{ row }">
              <el-link type="primary" @click="handleView(row.id)">{{ row.name }}</el-link>
            </template>
          </el-table-column>
          
          <el-table-column prop="type" label="资产类型" width="120">
            <template #default="{ row }">
              <el-tag :type="getTypeTagType(row.type)">{{ getTypeLabel(row.type) }}</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="status" label="资产状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusTagType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="value" label="资产值" min-width="150" />
          
          <el-table-column prop="projectName" label="所属项目" min-width="150" />
          
          <el-table-column prop="createdUserName" label="创建人" width="100" />
          
          <el-table-column prop="createdAt" label="创建时间" width="160">
            <template #default="{ row }">
              {{ formatDateTime(row.createdAt) }}
            </template>
          </el-table-column>
          
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="handleView(row.id)">查看</el-button>
              <el-button size="small" type="primary" @click="handleEdit(row.id)">编辑</el-button>
              <el-dropdown @command="(command) => handleStatusChange(row.id, command)">
                <el-button size="small" type="warning">
                  状态<el-icon class="el-icon--right"><arrow-down /></el-icon>
                </el-button>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item command="ACTIVE" :disabled="row.status === 'ACTIVE'">激活</el-dropdown-item>
                    <el-dropdown-item command="INACTIVE" :disabled="row.status === 'INACTIVE'">停用</el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <el-button size="small" type="danger" @click="handleDelete(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
        
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          class="pagination"
        />
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { assetService, projectService } from '@/services/api'
import { Plus, Search, Refresh, ArrowDown } from '@element-plus/icons-vue'

export default {
  name: 'AssetList',
  components: {
    Plus,
    Search,
    Refresh,
    ArrowDown
  },
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const assetData = ref([])
    const projects = ref([])
    const selectedAssets = ref([])
    
    const searchForm = reactive({
      name: '',
      type: '',
      status: '',
      projectId: null
    })
    
    const pagination = reactive({
      page: 1,
      size: 20,
      total: 0
    })
    
    const typeOptions = [
      { value: 'IP', label: 'IP地址' },
      { value: 'DOMAIN', label: '域名' }
    ]
    
    const statusOptions = [
      { value: 'ACTIVE', label: '活跃' },
      { value: 'INACTIVE', label: '停用' }
    ]
    
    const getTypeLabel = (type) => {
      const option = typeOptions.find(item => item.value === type)
      return option ? option.label : type
    }
    
    const getTypeTagType = (type) => {
      const typeMap = {
        'WEB_APPLICATION': 'primary',
        'MOBILE_APPLICATION': 'success',
        'NETWORK_DEVICE': 'warning',
        'SERVER': 'danger',
        'DATABASE': 'info',
        'API': 'primary',
        'IOT_DEVICE': 'warning',
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
    
    const fetchAssets = async () => {
      loading.value = true
      try {
        const params = {
          page: pagination.page,
          size: pagination.size,
          ...searchForm
        }
        
        // 过滤空值
        Object.keys(params).forEach(key => {
          if (params[key] === '' || params[key] === null) {
            delete params[key]
          }
        })
        
        const response = await assetService.getAll(params)
        const result = response.data || {}
        
        assetData.value = result.data || []
        pagination.total = result.total || 0
      } catch (error) {
        console.error('获取资产列表失败:', error)
        ElMessage.error('获取资产列表失败')
      } finally {
        loading.value = false
      }
    }
    
    const fetchProjects = async () => {
      try {
        const response = await projectService.getAllProjects()
        projects.value = response.data || []
      } catch (error) {
        console.error('获取项目列表失败:', error)
        ElMessage.error('获取项目列表失败')
      }
    }
    
    const handleSearch = () => {
      pagination.page = 1
      fetchAssets()
    }
    
    const handleReset = () => {
      Object.assign(searchForm, {
        name: '',
        type: '',
        status: '',
        projectId: null
      })
      pagination.page = 1
      fetchAssets()
    }
    
    const handleAdd = () => {
      router.push('/dashboard/assets/add')
    }
    
    const handleView = (id) => {
      router.push(`/dashboard/assets/${id}`)
    }
    
    const handleEdit = (id) => {
      router.push(`/dashboard/assets/${id}/edit`)
    }
    
    const handleStatusChange = async (id, status) => {
      try {
        await assetService.updateStatus(id, status)
        ElMessage.success('资产状态更新成功')
        fetchAssets()
      } catch (error) {
        console.error('更新资产状态失败:', error)
        ElMessage.error('更新资产状态失败')
      }
    }
    
    const handleDelete = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这个资产吗？', '确认删除', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await assetService.delete(id)
        ElMessage.success('资产删除成功')
        fetchAssets()
      } catch (error) {
        if (error === 'cancel') {
          return
        }
        console.error('删除资产失败:', error)
        ElMessage.error('删除资产失败')
      }
    }
    
    const handleSelectionChange = (selection) => {
      selectedAssets.value = selection
    }
    
    const handleSizeChange = (size) => {
      pagination.size = size
      pagination.page = 1
      fetchAssets()
    }
    
    const handleCurrentChange = (page) => {
      pagination.page = page
      fetchAssets()
    }
    
    onMounted(() => {
      fetchProjects()
      fetchAssets()
    })
    
    return {
      loading,
      assetData,
      projects,
      selectedAssets,
      searchForm,
      pagination,
      typeOptions,
      statusOptions,
      getTypeLabel,
      getTypeTagType,
      getStatusLabel,
      getStatusTagType,
      formatDateTime,
      handleSearch,
      handleReset,
      handleAdd,
      handleView,
      handleEdit,
      handleStatusChange,
      handleDelete,
      handleSelectionChange,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
@import '@/assets/styles/common.css';
</style>