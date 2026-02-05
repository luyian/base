<template>
  <div class="export-task-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="任务编号">
          <el-input v-model="queryForm.taskNo" placeholder="请输入任务编号" clearable />
        </el-form-item>
        <el-form-item label="配置名称">
          <el-input v-model="queryForm.configName" placeholder="请输入配置名称" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="失败" :value="3" />
            <el-option label="已取消" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="taskNo" label="任务编号" min-width="180" />
        <el-table-column prop="configName" label="配置名称" min-width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ row.statusDesc }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="progress" label="进度" width="150">
          <template #default="{ row }">
            <el-progress
              :percentage="row.progress || 0"
              :status="getProgressStatus(row.status)"
              :stroke-width="16"
              text-inside
            />
          </template>
        </el-table-column>
        <el-table-column prop="totalCount" label="总记录数" width="100" />
        <el-table-column prop="processedCount" label="已处理" width="100" />
        <el-table-column prop="fileSizeDesc" label="文件大小" width="100" />
        <el-table-column prop="downloadCount" label="下载次数" width="90" />
        <el-table-column prop="startTime" label="开始时间" width="170" />
        <el-table-column prop="endTime" label="结束时间" width="170" />
        <el-table-column prop="createBy" label="创建人" width="100" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="row.status === 2"
              type="primary"
              link
              @click="handleDownload(row)"
            >
              下载
            </el-button>
            <el-button
              v-if="row.status === 0 || row.status === 1"
              type="warning"
              link
              @click="handleCancel(row)"
            >
              取消
            </el-button>
            <el-button
              v-if="row.status === 3"
              type="info"
              link
              @click="handleViewError(row)"
            >
              查看错误
            </el-button>
            <el-button
              v-if="row.status !== 1"
              type="danger"
              link
              @click="handleDelete(row)"
            >
              删除
            </el-button>
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
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 16px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 错误信息弹窗 -->
    <el-dialog
      v-model="errorDialogVisible"
      title="错误信息"
      width="600px"
    >
      <el-alert type="error" :closable="false">
        <template #title>
          <pre style="white-space: pre-wrap; word-break: break-all;">{{ currentError }}</pre>
        </template>
      </el-alert>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  pageExportTask,
  cancelExportTask,
  deleteExportTask,
  getTaskProgress,
  getDownloadUrl
} from '@/api/exportTask'

// 查询表单
const queryForm = reactive({
  taskNo: '',
  configName: '',
  status: null,
  startTimeBegin: '',
  startTimeEnd: '',
  pageNum: 1,
  pageSize: 10
})

// 日期范围
const dateRange = ref([])

// 表格数据
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// 错误弹窗
const errorDialogVisible = ref(false)
const currentError = ref('')

// 轮询定时器
let pollingTimer = null

// 获取状态类型
const getStatusType = (status) => {
  const types = {
    0: 'info',
    1: 'warning',
    2: 'success',
    3: 'danger',
    4: 'info'
  }
  return types[status] || 'info'
}

// 获取进度条状态
const getProgressStatus = (status) => {
  if (status === 2) return 'success'
  if (status === 3) return 'exception'
  return null
}

// 查询列表
const handleQuery = async () => {
  // 处理日期范围
  if (dateRange.value && dateRange.value.length === 2) {
    queryForm.startTimeBegin = dateRange.value[0]
    queryForm.startTimeEnd = dateRange.value[1]
  } else {
    queryForm.startTimeBegin = ''
    queryForm.startTimeEnd = ''
  }

  loading.value = true
  try {
    const res = await pageExportTask(queryForm)
    tableData.value = res.data.records
    total.value = res.data.total

    // 如果有处理中的任务，启动轮询
    const hasProcessing = tableData.value.some(item => item.status === 0 || item.status === 1)
    if (hasProcessing) {
      startPolling()
    } else {
      stopPolling()
    }
  } finally {
    loading.value = false
  }
}

// 重置查询
const handleReset = () => {
  queryForm.taskNo = ''
  queryForm.configName = ''
  queryForm.status = null
  queryForm.startTimeBegin = ''
  queryForm.startTimeEnd = ''
  queryForm.pageNum = 1
  dateRange.value = []
  handleQuery()
}

// 下载文件
const handleDownload = (row) => {
  const url = getDownloadUrl(row.taskNo)
  window.open(url, '_blank')
}

// 取消任务
const handleCancel = (row) => {
  ElMessageBox.confirm('确定要取消该任务吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await cancelExportTask(row.id)
    ElMessage.success('取消成功')
    handleQuery()
  }).catch(() => {})
}

// 删除任务
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该任务吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteExportTask(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  }).catch(() => {})
}

// 查看错误
const handleViewError = (row) => {
  currentError.value = row.errorMessage || '未知错误'
  errorDialogVisible.value = true
}

// 启动轮询
const startPolling = () => {
  if (pollingTimer) return
  pollingTimer = setInterval(async () => {
    // 更新处理中任务的进度
    for (const item of tableData.value) {
      if (item.status === 0 || item.status === 1) {
        try {
          const res = await getTaskProgress(item.id)
          if (res.data !== null) {
            item.progress = res.data
          }
        } catch (e) {
          // 忽略错误
        }
      }
    }

    // 检查是否还有处理中的任务
    const hasProcessing = tableData.value.some(item => item.status === 0 || item.status === 1)
    if (!hasProcessing) {
      stopPolling()
      handleQuery() // 刷新列表获取最新状态
    }
  }, 2000)
}

// 停止轮询
const stopPolling = () => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
}

// 初始化
onMounted(() => {
  handleQuery()
})

// 清理
onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped>
.export-task-container {
  padding: 16px;
}
.search-card {
  margin-bottom: 16px;
}
</style>
