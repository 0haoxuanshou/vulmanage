<template>
  <div class="page-container">
    <div class="page-header">
      <h1>项目管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="showAddDialog">
          <el-icon><Plus /></el-icon>
          添加项目
        </el-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="项目名称">
          <el-input
            v-model="searchForm.name"
            placeholder="请输入项目名称"
            clearable
            @clear="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <el-icon><Search /></el-icon>
            搜索
          </el-button>
          <el-button @click="resetSearch">
            <el-icon><Refresh /></el-icon>
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 项目列表 -->
    <div class="table-container">
      <el-table
        :data="projectList"
        v-loading="loading"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="id" label="项目ID" width="80" />
        <el-table-column prop="name" label="项目名称" min-width="200" />
        <el-table-column prop="createdUserName" label="创建用户" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="updatedAt" label="更新时间" width="180">
          <template #default="scope">
            {{ formatDateTime(scope.row.updatedAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="scope">
            <el-button size="small" @click="viewProject(scope.row.id)">查看</el-button>
            <el-button size="small" type="primary" @click="editProject(scope.row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteProject(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :page-sizes="[10, 20, 50, 100]"
          :total="pagination.total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 添加/编辑项目对话框 -->
    <el-dialog
      :title="dialogTitle"
      v-model="dialogVisible"
      width="500px"
      @close="resetForm"
    >
      <el-form
        ref="projectFormRef"
        :model="projectForm"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="项目名称" prop="name">
          <el-input
            v-model="projectForm.name"
            placeholder="请输入项目名称"
            maxlength="255"
            show-word-limit
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 查看项目对话框 -->
    <el-dialog
      title="项目详情"
      v-model="viewDialogVisible"
      width="500px"
    >
      <div class="project-detail">
        <div class="detail-item">
          <label>项目ID：</label>
          <span>{{ viewProject.id }}</span>
        </div>
        <div class="detail-item">
          <label>项目名称：</label>
          <span>{{ viewProject.name }}</span>
        </div>
        <div class="detail-item">
          <label>创建用户：</label>
          <span>{{ viewProject.createdUserName }}</span>
        </div>
        <div class="detail-item">
          <label>创建时间：</label>
          <span>{{ formatDateTime(viewProject.createdAt) }}</span>
        </div>
        <div class="detail-item">
          <label>更新时间：</label>
          <span>{{ formatDateTime(viewProject.updatedAt) }}</span>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="viewDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh } from '@element-plus/icons-vue'
import { projectService } from '@/services/api'

// 响应式数据
const router = useRouter()
const loading = ref(false)
const projectList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const projectFormRef = ref()

// 搜索表单
const searchForm = reactive({
  name: ''
})

// 分页数据
const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

// 项目表单
const projectForm = reactive({
  id: null,
  name: '',
  createdUserId: null
})



// 表单验证规则
const formRules = {
  name: [
    { required: true, message: '请输入项目名称', trigger: 'blur' },
    { min: 1, max: 255, message: '项目名称长度在 1 到 255 个字符', trigger: 'blur' }
  ]
}

// 计算属性
const dialogTitle = computed(() => {
  return isEdit.value ? '编辑项目' : '添加项目'
})

// 方法
const loadProjects = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.current,
      size: pagination.size
    }
    
    if (searchForm.name) {
      params.name = searchForm.name
    }
    
    const response = await projectService.getAll(params)
    // 处理分页响应数据结构
    if (response.data && typeof response.data === 'object') {
      // 如果是分页数据结构
      if ('data' in response.data && 'total' in response.data) {
        projectList.value = response.data.data || []
        pagination.total = response.data.total || 0
      } else if (Array.isArray(response.data)) {
        // 如果直接是数组
        projectList.value = response.data
        pagination.total = response.data.length
      } else {
        projectList.value = []
        pagination.total = 0
      }
    } else {
      projectList.value = []
      pagination.total = 0
    }
  } catch (error) {
    console.error('加载项目列表失败:', error)
    ElMessage.error('加载项目列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadProjects()
}

const resetSearch = () => {
  searchForm.name = ''
  pagination.current = 1
  loadProjects()
}

const handleSizeChange = (val) => {
  pagination.size = val
  pagination.current = 1
  loadProjects()
}

const handleCurrentChange = (val) => {
  pagination.current = val
  loadProjects()
}

const showAddDialog = () => {
  isEdit.value = false
  dialogVisible.value = true
}

const editProject = (project) => {
  isEdit.value = true
  projectForm.id = project.id
  projectForm.name = project.name
  projectForm.createdUserId = project.createdUserId
  dialogVisible.value = true
}

const viewProject = (projectId) => {
  router.push({ name: 'ProjectDetail', params: { id: projectId } })
}

const deleteProject = async (project) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除项目 "${project.name}" 吗？`,
      '确认删除',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )
    
    await projectService.delete(project.id)
    ElMessage.success('删除成功')
    loadProjects()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除项目失败:', error)
      ElMessage.error('删除项目失败')
    }
  }
}

const submitForm = async () => {
  if (!projectFormRef.value) return
  
  try {
    await projectFormRef.value.validate()
    
    if (isEdit.value) {
      // 编辑项目
      await projectService.update(projectForm.id, {
        name: projectForm.name
      })
      ElMessage.success('更新成功')
    } else {
      // 添加项目 - 创建用户ID现在由后端从session中获取
      await projectService.create({
        name: projectForm.name
      })
      ElMessage.success('添加成功')
    }
    
    dialogVisible.value = false
    loadProjects()
  } catch (error) {
    console.error('提交表单失败:', error)
    ElMessage.error('操作失败')
  }
}

const resetForm = () => {
  if (projectFormRef.value) {
    projectFormRef.value.resetFields()
  }
  projectForm.id = null
  projectForm.name = ''
  projectForm.createdUserId = null
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  return new Date(dateTime).toLocaleString('zh-CN')
}

// 生命周期
onMounted(() => {
  loadProjects()
})
</script>

<style scoped>
/* 页面特定样式 */
.dialog-footer {
  text-align: right;
}

.el-form-item {
  margin-bottom: var(--spacing-lg);
}

.el-input,
.el-select {
  width: 100%;
}

.pagination {
  padding: 20px;
  text-align: right;
  background: #fafafa;
  border-top: 1px solid #ebeef5;
}

.project-detail {
  padding: 20px 0;
}

.detail-item {
  display: flex;
  margin-bottom: 15px;
  align-items: center;
}

.detail-item label {
  width: 100px;
  font-weight: 600;
  color: #606266;
  flex-shrink: 0;
}

.detail-item span {
  color: #303133;
  flex: 1;
}

.dialog-footer {
  text-align: right;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;
}

/* 统一按钮样式 */
.el-button {
  border-radius: 4px;
}

.el-button + .el-button {
  margin-left: 8px;
}


</style>