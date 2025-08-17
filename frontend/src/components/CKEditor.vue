<template>
  <div class="ck-editor-wrapper">
    <ckeditor
      :editor="editor"
      v-model="editorData"
      :config="editorConfig"
      @ready="onReady"
      @focus="onFocus"
      @blur="onBlur"
    ></ckeditor>
  </div>
</template>

<script>
import ClassicEditor from '@ckeditor/ckeditor5-build-classic'
import { Ckeditor } from '@ckeditor/ckeditor5-vue'
import { fileService } from '@/services/api'

export default {
  name: 'CKEditor',
  components: {
    ckeditor: Ckeditor
  },
  props: {
    value: {
      type: String,
      default: ''
    },
    modelValue: {
      type: String,
      default: ''
    },
    placeholder: {
      type: String,
      default: '请输入内容...'
    },
    height: {
      type: String,
      default: '300px'
    }
  },
  data() {
    return {
      editor: ClassicEditor,
      editorData: this.modelValue || this.value,
      editorConfig: {
        placeholder: this.placeholder,
        toolbar: {
          items: [
            'heading',
            '|',
            'bold',
            'italic',
            'link',
            'bulletedList',
            'numberedList',
            '|',
            'outdent',
            'indent',
            '|',
            'imageUpload',
            'blockQuote',
            'insertTable',
            'mediaEmbed',
            'undo',
            'redo'
          ]
        },
        language: 'zh-cn',
        image: {
          toolbar: [
            'imageTextAlternative',
            'imageStyle:inline',
            'imageStyle:block',
            'imageStyle:side'
          ]
        },
        table: {
          contentToolbar: [
            'tableColumn',
            'tableRow',
            'mergeTableCells'
          ]
        },
        // 自定义图片上传适配器
        simpleUpload: {
          uploadUrl: '/api/files/upload/image'
        },
        // 剪贴板配置，处理图片粘贴
        clipboard: {
          // 允许图片粘贴，但会通过上传适配器处理
          allowedContent: true
        }
      }
    }
  },
  watch: {
    value(newVal) {
      if (newVal !== this.editorData) {
        this.editorData = newVal
      }
    },
    modelValue(newVal) {
      if (newVal !== this.editorData) {
        this.editorData = newVal
      }
    },
    editorData(newVal) {
      // Vue 3 v-model 支持
      this.$emit('update:modelValue', newVal)
      // Vue 2 兼容
      this.$emit('input', newVal)
    }
  },
  mounted() {
    // 设置编辑器高度
    this.$nextTick(() => {
      const setEditorHeight = () => {
        const editorElement = this.$el.querySelector('.ck-editor__editable')
        if (editorElement) {
          editorElement.style.minHeight = this.height
          editorElement.style.height = 'auto'
          // 确保编辑器不会塌陷
          if (editorElement.offsetHeight < parseInt(this.height)) {
            editorElement.style.height = this.height
          }
        }
      }
      
      // 立即设置一次
      setEditorHeight()
      
      // 延迟再设置一次，确保CKEditor完全初始化后生效
      setTimeout(setEditorHeight, 100)
    })
  },
  methods: {
    onReady(editor) {
      // 自定义图片上传适配器
      editor.plugins.get('FileRepository').createUploadAdapter = (loader) => {
        return new CustomUploadAdapter(loader)
      }
      
      // 配置图片粘贴处理
      // 让CKEditor自然处理图片粘贴，通过上传适配器自动上传
      
      // 设置编辑器高度
      const editorElement = editor.ui.getEditableElement()
      if (editorElement) {
        editorElement.style.minHeight = this.height
      }
      
      this.$emit('ready', editor)
    },
    onFocus(event, editor) {
      this.$emit('focus', event, editor)
    },
    onBlur(event, editor) {
      this.$emit('blur', event, editor)
    },
    // 获取编辑器内容
    getContent() {
      return this.editorData
    },
    // 设置编辑器内容
    setContent(content) {
      this.editorData = content
    },
    // 清空编辑器内容
    clear() {
      this.editorData = ''
    }
  }
}

// 自定义图片上传适配器
class CustomUploadAdapter {
  constructor(loader) {
    this.loader = loader
  }

  upload() {
    return this.loader.file.then(file => {
      return new Promise((resolve, reject) => {
        // 验证文件类型
        if (!file.type.startsWith('image/')) {
          reject('请选择图片文件')
          return
        }
        
        // 验证文件大小（限制为5MB）
        const maxSize = 5 * 1024 * 1024
        if (file.size > maxSize) {
          reject('图片大小不能超过5MB')
          return
        }
        
        fileService.uploadImage(file)
          .then(response => {
            if (response.data && response.data.url) {
              let imageUrl = response.data.url
              
              // 图片上传成功
              
              resolve({
                default: imageUrl
              })
            } else {
              reject('上传失败：服务器未返回图片URL')
            }
          })
          .catch(error => {
            console.error('图片上传失败:', error)
            const errorMsg = error.response?.data?.message || error.message || '图片上传失败'
            reject(errorMsg)
          })
      })
    })
  }

  abort() {
    // 取消上传的逻辑
    if (this.xhr) {
      this.xhr.abort()
    }
  }
}
</script>

<style scoped>
.ck-editor-wrapper {
  width: 100%;
  position: relative;
}

/* CKEditor 样式自定义 */
.ck-editor-wrapper >>> .ck-editor {
  border-radius: 4px;
  overflow: hidden;
}

.ck-editor-wrapper >>> .ck-editor__editable {
  border-radius: 0 0 4px 4px;
  border: 1px solid #dcdfe6;
  border-top: none;
  transition: border-color 0.2s cubic-bezier(0.645, 0.045, 0.355, 1);
  min-height: 150px !important;
  padding: 12px 16px;
  box-sizing: border-box;
}

.ck-editor-wrapper >>> .ck-editor__editable:focus {
  border-color: #409eff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.ck-editor-wrapper >>> .ck-toolbar {
  border-radius: 4px 4px 0 0;
  border: 1px solid #dcdfe6;
  border-bottom: none;
  background: #fafafa;
  padding: 8px 12px;
}

.ck-editor-wrapper >>> .ck-toolbar:focus {
  border-color: #409eff;
}

.ck-editor-wrapper >>> .ck-content {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Fira Sans', 'Droid Sans', 'Helvetica Neue', sans-serif;
  line-height: 1.6;
  font-size: 14px;
  color: #606266;
}

.ck-editor-wrapper >>> .ck-content img {
  max-width: 100%;
  height: auto;
  border-radius: 4px;
  margin: 8px 0;
}

.ck-editor-wrapper >>> .ck-content p {
  margin: 0 0 8px 0;
}

.ck-editor-wrapper >>> .ck-content p:last-child {
  margin-bottom: 0;
}

/* 防止编辑器高度塌陷 */
.ck-editor-wrapper >>> .ck-editor__editable_inline {
  min-height: 150px !important;
  max-height: 400px;
  overflow-y: auto;
}

/* 工具栏按钮样式优化 */
.ck-editor-wrapper >>> .ck-button {
  border-radius: 3px;
}

.ck-editor-wrapper >>> .ck-button:hover {
  background: #e6f7ff;
}

.ck-editor-wrapper >>> .ck-button.ck-on {
  background: #409eff;
  color: white;
}
</style>