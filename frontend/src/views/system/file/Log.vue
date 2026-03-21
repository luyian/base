<template>
  <div class="file-log-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="文件名">
          <el-input v-model="queryForm.fileName" placeholder="请输入文件名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="操作类型">
          <el-select v-model="queryForm.operationType" placeholder="请选择操作类型" clearable style="width: 120px">
            <el-option v-for="item in operationTypes" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="queryForm.operatorName" placeholder="请输入操作人" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 100px">
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="table-card">
      <div class="toolbar">
        <div>
          <el-button v-permission="'file:log:delete'" type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">批量删除</el-button>
          <el-button v-permission="'file:log:delete'" type="danger" @click="handleClear">清空日志</el-button>
        </div>
      </div>

      <!-- 表格 -->
      <el-table
        :data="tableData"
        style="width: 100%; margin-top: 20px"
        @selection-change="handleSelectionChange"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" />

        <el-table-column prop="fileName" label="文件名" min-width="200" show-overflow-tooltip />
        <el-table-column prop="operationTypeName" label="操作类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getOperationTypeTag(row.operationType)">
              {{ row.operationTypeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="文件大小" width="120">
          <template #default="{ row }">
            {{ formatSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="operatorName" label="操作人" width="120" />
        <el-table-column prop="ip" label="IP地址" width="150" />
        <el-table-column prop="location" label="操作地点" width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">成功</el-tag>
            <el-tag v-else type="danger">失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="executeTime" label="耗时(ms)" width="100" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button v-permission="'file:log:delete'" link type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="日志详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="日志ID">{{ currentLog.id }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">
          <el-tag :type="getOperationTypeTag(currentLog.operationType)">
            {{ currentLog.operationTypeName }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="文件名" :span="2">{{ currentLog.fileName }}</el-descriptions-item>
        <el-descriptions-item label="文件路径" :span="2">{{ currentLog.filePath }}</el-descriptions-item>
        <el-descriptions-item label="文件大小">{{ formatSize(currentLog.fileSize) }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ currentLog.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="操作人ID">{{ currentLog.operatorId }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ currentLog.ip }}</el-descriptions-item>
        <el-descriptions-item label="操作地点" :span="2">{{ currentLog.location }}</el-descriptions-item>
        <el-descriptions-item label="UserAgent" :span="2" show-overflow-tooltip>{{ currentLog.userAgent }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="currentLog.status === 1" type="success">成功</el-tag>
          <el-tag v-else type="danger">失败</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="执行耗时">{{ currentLog.executeTime }}ms</el-descriptions-item>
        <el-descriptions-item label="错误信息" :span="2">
          <span style="color: red">{{ currentLog.errorMsg || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="操作时间" :span="2">{{ currentLog.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentLog.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageFileLogs, deleteFileLog, batchDeleteFileLogs, clearFileLogs, getOperationTypes } from '@/api/file'

const queryForm = ref({
  pageNum: 1,
  pageSize: 10,
  fileName: '',
  operationType: null,
  operatorName: '',
  status: null
})

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref([])
const operationTypes = ref([
  { value: 1, label: '上传' },
  { value: 2, label: '下载' },
  { value: 3, label: '删除' },
  { value: 4, label: '预览' }
])

// 详情相关
const detailVisible = ref(false)
const currentLog = ref({})

onMounted(() => {
  loadOperationTypes()
  handleQuery()
})

async function loadOperationTypes() {
  try {
    const res = await getOperationTypes()
    if (res.data) {
      operationTypes.value = res.data
    }
  } catch (e) {
    // 使用默认值
  }
}

async function handleQuery() {
  loading.value = true
  try {
    const res = await pageFileLogs(queryForm.value)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    ElMessage.error(e.message || '查询失败')
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryForm.value = {
    pageNum: 1,
    pageSize: 10,
    fileName: '',
    operationType: null,
    operatorName: '',
    status: null
  }
  handleQuery()
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.id)
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定要删除该日志吗？', '提示', { type: 'warning' })
    await deleteFileLog(id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

async function handleBatchDelete() {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 条日志吗？`, '提示', { type: 'warning' })
    await batchDeleteFileLogs(selectedIds.value)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

async function handleClear() {
  try {
    await ElMessageBox.confirm('确定要清空所有文件操作日志吗？此操作不可恢复！', '提示', { type: 'warning' })
    await clearFileLogs()
    ElMessage.success('清空成功')
    handleQuery()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '清空失败')
    }
  }
}

function handleView(row) {
  currentLog.value = row
  detailVisible.value = true
}

function formatSize(size) {
  if (!size) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let i = 0
  while (size >= 1024 && i < units.length - 1) {
    size /= 1024
    i++
  }
  return size.toFixed(2) + ' ' + units[i]
}

function getOperationTypeTag(type) {
  switch (type) {
    case 1: return 'success'  // 上传
    case 2: return 'primary'  // 下载
    case 3: return 'danger'   // 删除
    case 4: return 'info'     // 预览
    default: return ''
  }
}
</script>

<style scoped>
.file-log-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>