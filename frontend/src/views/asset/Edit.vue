<template>
  <div class="page-container">
    <div class="page-header">
      <h2>{{ isEdit ? '编辑资产' : '新增资产' }}</h2>
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
          class="asset-form">
          
          <el-row :gutter="24">
            <el-col :span="12">
              <el-form-item label="资产名称" prop="name">
                <el-input
                  v-model="form.name"
                  placeholder="请输入资产名称"
                  maxlength="100"
                  show-word-limit
                />
              </el-form-item>
            </el-col>
            
            <el-col :span="12">
              <el-form-item label="资产类型" prop="type">
                <el-select v-model="form.type" placeholder="请选择资产类型" style="width: 100%">
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
              <el-form-item label="资产状态" prop="status">
                <el-select v-model="form.status" placeholder="请选择资产状态" style="width: 100%">
                  <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          
          <el-row :gutter="24">
            <el-col :span="24">
              <el-form-item label="资产值" prop="value">
                <el-input
                  v-model="form.value"
                  placeholder="请输入IP地址或域名"
                  maxlength="255"
                />
              </el-form-item>
            </el-col>
          </el-row>
          

          
          <el-form-item label="资产描述" prop="description">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="6"
              placeholder="请输入资产描述"
              maxlength="2000"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="handleSubmit" :loading="submitting">
              {{ isEdit ? '更新资产' : '创建资产' }}
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
import { assetService, projectService } from '@/services/api'
import { ArrowLeft } from '@element-plus/icons-vue'

export default {
  name: 'AssetEdit',
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
      value: '',
      status: 'ACTIVE',
      description: '',
      projectId: null
    })
    
    const rules = {
      name: [
        { required: true, message: '请输入资产名称', trigger: 'blur' },
        { min: 2, max: 255, message: '资产名称长度在 2 到 255 个字符', trigger: 'blur' }
      ],
      type: [
        { required: true, message: '请选择资产类型', trigger: 'change' }
      ],
      value: [
        { required: true, message: '请输入资产值', trigger: 'blur' },
        { max: 255, message: '资产值长度不能超过 255 个字符', trigger: 'blur' }
      ],
      projectId: [
        { required: true, message: '请选择所属项目', trigger: 'change' }
      ],
      status: [
        { required: true, message: '请选择资产状态', trigger: 'change' }
      ]
    }
    
    const typeOptions = [
      { value: 'IP', label: 'IP地址' },
      { value: 'DOMAIN', label: '域名' }
    ]
    
    const statusOptions = [
      { value: 'ACTIVE', label: '活跃' },
      { value: 'INACTIVE', label: '停用' }
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
    
    const fetchAsset = async () => {
      if (!isEdit.value) return
      
      try {
        const response = await assetService.getById(route.params.id)
        const asset = response.data || {}
        
        Object.assign(form, {
          name: asset.name || '',
          type: asset.type || '',
          value: asset.value || '',
          status: asset.status || 'ACTIVE',
          description: asset.description || '',
          projectId: asset.projectId || null
        })
      } catch (error) {
        console.error('获取资产详情失败:', error)
        ElMessage.error('获取资产详情失败')
      }
    }
    
    const handleSubmit = async () => {
      if (!formRef.value) return
      
      try {
        await formRef.value.validate()
        submitting.value = true
        
        // 清理空字符串字段
        const submitData = { ...form }
        Object.keys(submitData).forEach(key => {
          if (submitData[key] === '') {
            submitData[key] = null
          }
        })
        
        if (isEdit.value) {
          await assetService.update(route.params.id, submitData)
          ElMessage.success('资产更新成功')
        } else {
          await assetService.create(submitData)
          ElMessage.success('资产创建成功')
        }
        
        // 检查是否从项目详情页跳转过来
        const projectId = route.query.projectId
        if (projectId) {
          router.push(`/dashboard/projects/${projectId}`)
        } else {
          router.push('/dashboard/assets')
        }
      } catch (error) {
        if (error.errors) {
          // 表单验证失败
          return
        }
        console.error('保存资产失败:', error)
        ElMessage.error('保存资产失败')
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
      fetchAsset()
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

.asset-form {
  padding: var(--spacing-xl);
}

.el-form-item {
  margin-bottom: var(--spacing-lg);
}

.el-textarea {
  resize: vertical;
}
</style>