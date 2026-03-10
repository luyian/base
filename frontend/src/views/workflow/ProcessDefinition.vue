<template>
  <div class="process-def-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="流程名称">
          <el-input
            v-model="queryForm.keyword"
            placeholder="请输入流程名称"
            clearable
            style="width: 200px"
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="流程分类">
          <el-select v-model="queryForm.category" placeholder="请选择分类" clearable style="width: 150px">
            <el-option label="工单管理" value="工单管理" />
            <el-option label="审批流程" value="审批流程" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 150px">
            <el-option label="草稿" :value="0" />
            <el-option label="已发布" :value="1" />
            <el-option label="禁用" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <div class="toolbar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增流程</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="processKey" label="流程标识" width="150" />
        <el-table-column prop="processName" label="流程名称" min-width="200" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="120" />
        <el-table-column prop="version" label="版本" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" type="success" link @click="handlePublish(row)">发布</el-button>
            <el-button v-if="row.status === 1" type="warning" link @click="handleDisable(row)">禁用</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listProcessDefinitions, deleteProcessDefinition, publishProcessDefinition, disableProcessDefinition } from '@/api/workflow'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const queryForm = reactive({
  keyword: '',
  category: '',
  status: ''
})
const tableData = ref([])
const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

onMounted(() => {
  getList()
})

const getList = async () => {
  loading.value = true
  try {
    const res = await listProcessDefinitions(queryForm)
    tableData.value = res.data || []
    pagination.total = tableData.value.length
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  pagination.pageNum = 1
  getList()
}

const handleReset = () => {
  queryForm.keyword = ''
  queryForm.category = ''
  queryForm.status = ''
  handleQuery()
}

const handleAdd = () => {
  router.push('/workflow/design')
}

const handleEdit = (row) => {
  router.push(`/workflow/design?id=${row.id}`)
}

const handleView = (row) => {
  router.push(`/workflow/design?id=${row.id}&mode=view`)
}

const handlePublish = async (row) => {
  try {
    await ElMessageBox.confirm('确定要发布此流程吗？', '提示', { type: 'warning' })
    await publishProcessDefinition(row.id)
    ElMessage.success('发布成功')
    getList()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('发布失败')
    }
  }
}

const handleDisable = async (row) => {
  try {
    await ElMessageBox.confirm('确定要禁用此流程吗？', '提示', { type: 'warning' })
    await disableProcessDefinition(row.id)
    ElMessage.success('禁用成功')
    getList()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('禁用失败')
    }
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除此流程吗？', '提示', { type: 'warning' })
    await deleteProcessDefinition(row.id)
    ElMessage.success('删除成功')
    getList()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const getStatusText = (status) => {
  const map = { 0: '草稿', 1: '已发布', 2: '禁用' }
  return map[status] || '未知'
}

const getStatusTagType = (status) => {
  const map = { 0: 'info', 1: 'success', 2: 'danger' }
  return map[status] || 'info'
}
</script>

<style scoped>
.process-def-container {
  padding: 16px;
}

.search-card {
  margin-bottom: 16px;
}

.table-card {
  min-height: calc(100vh - 180px);
}

.toolbar {
  margin-bottom: 16px;
}
</style>
