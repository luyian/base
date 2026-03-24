<template>
  <div class="knowledge-base-list">
    <div class="page-header">
      <h2>知识库</h2>
      <el-button type="primary" :icon="Plus" @click="handleCreate">
        创建知识库
      </el-button>
    </div>

    <!-- 知识库卡片列表 -->
    <div class="card-list" v-loading="loading">
      <el-empty v-if="!loading && list.length === 0" description="暂无知识库，点击创建按钮开始" />

      <el-card
        v-for="item in list"
        :key="item.id"
        class="kb-card"
        shadow="hover"
        @click="handleDetail(item)"
      >
        <div class="kb-card-header">
          <el-icon class="kb-icon" :size="32"><Collection /></el-icon>
          <div class="kb-info">
            <h3 class="kb-title">{{ item.name }}</h3>
            <p class="kb-desc">{{ item.description || '暂无描述' }}</p>
          </div>
        </div>
        <div class="kb-card-footer">
          <span class="kb-time">更新于 {{ formatDate(item.updatedAt) }}</span>
          <div class="kb-actions" @click.stop>
            <el-button type="primary" link :icon="Edit" @click="handleEdit(item)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(item)">删除</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑知识库' : '创建知识库'"
      width="500px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="80px"
      >
        <el-form-item label="名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入知识库名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="form.description"
            type="textarea"
            :rows="3"
            placeholder="请输入知识库描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete, Collection } from '@element-plus/icons-vue'
import dayjs from 'dayjs'
import {
  getKnowledgeBaseList,
  createKnowledgeBase,
  updateKnowledgeBase,
  deleteKnowledgeBase
} from '@/api/knowledge'

const router = useRouter()

// 数据
const loading = ref(false)
const submitLoading = ref(false)
const list = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  description: ''
})

const rules = {
  name: [
    { required: true, message: '请输入知识库名称', trigger: 'blur' }
  ]
}

// 格式化日期
const formatDate = (date) => {
  return dayjs(date).format('YYYY-MM-DD HH:mm')
}

// 加载列表
const loadList = async () => {
  loading.value = true
  try {
    const res = await getKnowledgeBaseList()
    list.value = res.data || []
  } catch (error) {
    console.error('获取知识库列表失败:', error)
    ElMessage.error('获取知识库列表失败')
  } finally {
    loading.value = false
  }
}

// 创建知识库
const handleCreate = () => {
  isEdit.value = false
  form.id = null
  form.name = ''
  form.description = ''
  dialogVisible.value = true
}

// 编辑知识库
const handleEdit = (row) => {
  isEdit.value = true
  form.id = row.id
  form.name = row.name
  form.description = row.description
  dialogVisible.value = true
}

// 查看详情
const handleDetail = (row) => {
  router.push(`/knowledge-base/${row.id}`)
}

// 删除知识库
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除知识库 "${row.name}" 吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    try {
      await deleteKnowledgeBase(row.id)
      ElMessage.success('删除成功')
      loadList()
    } catch (error) {
      console.error('删除知识库失败:', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateKnowledgeBase(form)
      ElMessage.success('更新成功')
    } else {
      await createKnowledgeBase(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadList()
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error(isEdit.value ? '更新失败' : '创建失败')
  } finally {
    submitLoading.value = false
  }
}

// 关闭对话框
const handleDialogClose = () => {
  formRef.value?.resetFields()
}

onMounted(() => {
  loadList()
})
</script>

<style scoped>
.knowledge-base-list {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
}

.card-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 16px;
}

.kb-card {
  cursor: pointer;
  transition: transform 0.2s;
}

.kb-card:hover {
  transform: translateY(-4px);
}

.kb-card-header {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 16px;
}

.kb-icon {
  color: #409eff;
  flex-shrink: 0;
}

.kb-info {
  flex: 1;
  min-width: 0;
}

.kb-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.kb-desc {
  margin: 0;
  font-size: 14px;
  color: #909399;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.kb-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #ebeef5;
}

.kb-time {
  font-size: 12px;
  color: #909399;
}

.kb-actions {
  display: flex;
  gap: 8px;
}
</style>