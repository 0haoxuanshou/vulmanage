<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑任务' : '新增任务' }}</h2>
      <div class="header-actions">
        <el-button @click="goBack">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </el-button>
      </div>
    </div>
    
    <div class="form-container">
      <el-card>
        <el-form
          ref="formRef"
          :model="form"
          :rules="rules"
          label-width="120px"
          class="task-form">
          
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="任务名称" prop="name">
                <el-input
                  v-model="form.name"
                  placeholder="请输入任务名称"
                  maxlength="100"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item label="任务类型" prop="type">
                <el-select v-model="form.type" placeholder="请选择任务类型" style="width: 100%">
                  <el-option v-for="item in typeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="所属项目" prop="projectId">
                <el-select v-model="form.projectId" placeholder="请选择项目" style="width: 100%">
                  <el-option v-for="project in projects" :key="project.id" :label="project.name" :value="project.id" />
                </el-select>
              </el-form-item>
            </el-col>
            
            <el-col :span="12" v-if="isEdit">
              <el-form-item label="任务状态" prop="status">
                <el-select v-model="form.status" placeholder="请选择任务状态" style="width: 100%">
                  <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-form-item label="任务描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="6"
              placeholder="请输入任务描述"
              maxlength="2000"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">
              {{ isEdit ? '更新任务' : '创建任务' }}
            </el-button>
            <el-button @click="handleReset">重置</el-button>
            <el-button @click="goBack">取消</el-button>
          </el-form-item>
        </el-form>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { taskService, projectService } from '@/services/api'
import { ArrowLeft } from '@element-plus/icons-vue'

export default {
  name: 'TaskEdit',
  components: {
    ArrowLeft
  },
  setup() {
    const router = useRouter()
    const route = useRoute()
    const formRef = ref()
    const submitting = ref(false)
    const projects = ref([])
    
    const isEdit = computed(() => !!route.params.id)
    
    const form = reactive({
      name: '',
      type: '',
      status: 'PENDING',
      description: '',
      projectId: null
    })
    
    const rules = {
      name: [
        { required: true, message: '请输入任务名称', trigger: 'blur' },
        { min: 2, max: 100, message: '任务名称长度在 2 到 100 个字符', trigger: 'blur' }
      ],
      type: [
        { required: true, message: '请选择任务类型', trigger: 'change' }
      ],
      projectId: [
        { required: true, message: '请选择所属项目', trigger: 'change' }
      ],
      status: [
        { required: true, message: '请选择任务状态', trigger: 'change' }
      ]
    }
    
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
    
    const fetchProjects = async () => {
      try {
        const response = await projectService.getAllProjects()
        projects.value = response.data || []
      } catch (error) {
        console.error('获取项目列表失败:', error)
        ElMessage.error('获取项目列表失败')
      }
    }
    
    const fetchTask = async () => {
      if (!isEdit.value) return
      
      try {
        const response = await taskService.getById(route.params.id)
        const task = response.data || {}
        
        Object.assign(form, {
          name: task.name || '',
          type: task.type || '',
          status: task.status || 'PENDING',
          description: task.description || '',
          projectId: task.projectId || null
        })
      } catch (error) {
        console.error('获取任务详情失败:', error)
        ElMessage.error('获取任务详情失败')
      }
    }
    
    const handleSubmit = async () => {
      if (!formRef.value) return
      
      try {
        await formRef.value.validate()
        submitting.value = true
        
        if (isEdit.value) {
          await taskService.update(route.params.id, form)
          ElMessage.success('任务更新成功')
        } else {
          await taskService.create(form)
          ElMessage.success('任务创建成功')
        }
        
        // 检查是否从项目详情页跳转过来
        const projectId = route.query.projectId
        if (projectId) {
          router.push(`/dashboard/projects/${projectId}`)
        } else {
          router.push('/dashboard/tasks')
        }
      } catch (error) {
        if (error.errors) {
          // 表单验证失败
          return
        }
        console.error('保存任务失败:', error)
        ElMessage.error('保存任务失败')
      } finally {
        submitting.value = false
      }
    }
    
    const handleReset = () => {
      if (!formRef.value) return
      formRef.value.resetFields()
    }
    
    const goBack = () => {
      router.go(-1)
    }
    
    onMounted(() => {
      fetchProjects()
      fetchTask()
    })
    
    return {
      formRef,
      submitting,
      projects,
      isEdit,
      form,
      rules,
      typeOptions,
      statusOptions,
      handleSubmit,
      handleReset,
      goBack
    }
  }
}
</script>

<style scoped>
@import '@/assets/styles/common.css';

.form-container {
  margin-top: var(--spacing-lg);
  max-width: 800px;
}

.task-form {
  padding: var(--spacing-xl);
}

.el-form-item {
  margin-bottom: var(--spacing-lg);
}

.el-textarea {
  resize: vertical;
}
</style>