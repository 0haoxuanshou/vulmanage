<template>
  <div class="page-container">
    <div class="page-header">
      <h2>任务列表</h2>
      <div class="header-actions">
        <el-button type="primary" @click="handleCreate">
          <el-icon><Plus /></el-icon>
          新增任务
        </el-button>
      </div>
    </div>
    
    <el-card class="search-card">
      <el-form :model="searchForm" inline class="search-form">
        <el-form-item label="任务名称">
          <el-input v-model="searchForm.name" placeholder="请输入任务名称" clearable />
        </el-form-item>
        <el-form-item label="任务类型">
          <el-select v-model="searchForm.type" placeholder="请选择任务类型" clearable>
            <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务状态">
          <el-select v-model="searchForm.status" placeholder="请选择任务状态" clearable>
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属项目">
          <el-select v-model="searchForm.projectId" placeholder="请选择项目" clearable>
            <el-option v-for="project in projects" :key="project.id" :label="project.name" :value="project.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="tasks"
        border
        style="width: 100%">
        <el-table-column prop="name" label="任务名称" min-width="200" />
        <el-table-column prop="type" label="任务类型" width="120">
          <template #default="{ row }">
            {{ getTypeLabel(row.type) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="任务状态" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="projectName" label="所属项目" width="150" />
        <el-table-column prop="createdUserName" label="创建人" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="startedAt" label="开始时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.startedAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="completedAt" label="完成时间" width="160">
          <template #default="{ row }">
            {{ formatDateTime(row.completedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" size="small" @click="handleView(row.id)">查看</el-button>
              <el-button type="warning" size="small" @click="handleEdit(row.id)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
      
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { taskService, projectService } from '@/services/api'
import { Plus } from '@element-plus/icons-vue'

export default {
  name: 'TaskList',
  components: {
    Plus
  },
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const tasks = ref([])
    const projects = ref([])
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)
    
    const searchForm = reactive({
      name: '',
      type: '',
      status: '',
      projectId: null
    })
    
    const typeOptions = [
      { value: 'SCAN_TARGET', label: '扫描目标' },
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
    
    const fetchTasks = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value,
          size: pageSize.value,
          ...searchForm
        }
        
        const response = await taskService.getAll(params)
        tasks.value = response.data.data || []
        total.value = response.data.total || 0
      } catch (error) {
        console.error('获取任务列表失败:', error)
        ElMessage.error('获取任务列表失败')
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
      }
    }
    
    const handleSearch = () => {
      currentPage.value = 1
      fetchTasks()
    }
    
    const resetSearch = () => {
      Object.assign(searchForm, {
        name: '',
        type: '',
        status: '',
        projectId: null
      })
      currentPage.value = 1
      fetchTasks()
    }
    
    const handleCreate = () => {
      router.push('/dashboard/tasks/add')
    }
    
    const handleView = (id) => {
      router.push(`/dashboard/tasks/${id}`)
    }
    
    const handleEdit = (id) => {
      router.push(`/dashboard/tasks/${id}/edit`)
    }
    

    
    const handleDelete = async (id) => {
      try {
        await ElMessageBox.confirm('确定要删除这个任务吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await taskService.delete(id)
        ElMessage.success('删除成功')
        fetchTasks()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除任务失败:', error)
          ElMessage.error('删除任务失败')
        }
      }
    }
    
    const handleSizeChange = (size) => {
      pageSize.value = size
      currentPage.value = 1
      fetchTasks()
    }
    
    const handleCurrentChange = (page) => {
      currentPage.value = page
      fetchTasks()
    }
    
    onMounted(() => {
      fetchTasks()
      fetchProjects()
    })
    
    return {
      loading,
      tasks,
      projects,
      total,
      currentPage,
      pageSize,
      searchForm,
      typeOptions,
      statusOptions,
      getTypeLabel,
      getStatusLabel,
      getStatusType,
      formatDateTime,
      handleSearch,
      resetSearch,
      handleCreate,
      handleView,
      handleEdit,
      handleDelete,
      handleSizeChange,
      handleCurrentChange
    }
  }
}
</script>

<style scoped>
@import '@/assets/styles/common.css';

.pagination-container {
  margin-top: var(--spacing-lg);
  display: flex;
  justify-content: center;
  padding: var(--spacing-md);
  background: white;
  border-radius: var(--border-radius);
  box-shadow: var(--box-shadow);
}
</style>