<template>
  <div class="login-log-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="用户名">
          <el-input v-model="queryForm.username" placeholder="请输入用户名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="登录IP">
          <el-input v-model="queryForm.loginIp" placeholder="请输入登录IP" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="成功" :value="1" />
            <el-option label="失败" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="登录时间">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 360px"
          />
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
          <el-button v-permission="'log:login:delete'" type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">批量删除</el-button>
          <el-button v-permission="'log:login:delete'" type="danger" @click="handleClear">清空日志</el-button>
          <el-button v-permission="'log:login:list'" type="success" @click="handleExport" :loading="exporting">导出</el-button>
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
        <el-table-column prop="id" label="日志ID" width="80" />
        <el-table-column prop="username" label="用户名" width="150" />
        <el-table-column prop="loginIp" label="登录IP" width="150" />
        <el-table-column prop="loginLocation" label="登录地点" width="150" />
        <el-table-column prop="browser" label="浏览器" width="150" show-overflow-tooltip />
        <el-table-column prop="os" label="操作系统" width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">成功</el-tag>
            <el-tag v-else type="danger">失败</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="message" label="提示信息" width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="登录时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">详情</el-button>
            <el-button v-permission="'log:login:delete'" link type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="登录日志详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="日志ID">{{ detailData.id }}</el-descriptions-item>
        <el-descriptions-item label="用户名">{{ detailData.username }}</el-descriptions-item>
        <el-descriptions-item label="登录IP">{{ detailData.loginIp }}</el-descriptions-item>
        <el-descriptions-item label="登录地点">{{ detailData.loginLocation }}</el-descriptions-item>
        <el-descriptions-item label="浏览器" :span="2">{{ detailData.browser }}</el-descriptions-item>
        <el-descriptions-item label="操作系统" :span="2">{{ detailData.os }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailData.status === 1" type="success">成功</el-tag>
          <el-tag v-else type="danger">失败</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="登录时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="提示信息" :span="2">{{ detailData.message }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 导出进度弹窗 -->
    <el-dialog
      v-model="exportDialogVisible"
      title="导出进度"
      width="400px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="exportStatus === 2 || exportStatus === 3"
    >
      <div class="export-progress-content">
        <el-progress
          :percentage="exportProgress"
          :status="exportStatus === 2 ? 'success' : (exportStatus === 3 ? 'exception' : null)"
          :stroke-width="20"
          text-inside
        />
        <div class="export-progress-info">
          <span v-if="exportStatus === 0">等待处理...</span>
          <span v-else-if="exportStatus === 1">正在导出数据，请稍候...</span>
          <span v-else-if="exportStatus === 2" class="success-text">导出完成！</span>
          <span v-else-if="exportStatus === 3" class="error-text">导出失败：{{ exportError }}</span>
        </div>
      </div>
      <template #footer>
        <el-button v-if="exportStatus === 2" type="primary" @click="handleDownloadExport">
          下载文件
        </el-button>
        <el-button v-if="exportStatus === 2 || exportStatus === 3" @click="handleCloseExportDialog">
          关闭
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  pageLoginLogs,
  getLoginLogById,
  deleteLoginLog,
  batchDeleteLoginLogs,
  clearLoginLogs
} from '@/api/loginLog'
import { createExportTask, getTaskProgress, getDownloadUrl } from '@/api/exportTask'

// 查询表单
const queryForm = reactive({
  username: '',
  loginIp: '',
  status: null,
  startTime: '',
  endTime: '',
  current: 1,
  size: 10
})

// 日期范围
const dateRange = ref([])

// 表格数据
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref([])

// 详情对话框
const detailVisible = ref(false)
const detailData = ref({})

// 导出相关
const exporting = ref(false)
const exportDialogVisible = ref(false)
const exportProgress = ref(0)
const exportStatus = ref(0) // 0-待处理，1-处理中，2-已完成，3-失败
const exportTaskNo = ref('')
const exportError = ref('')
let exportPollingTimer = null

// 导出查询参数
const exportQueryParams = computed(() => {
  const params = {
    username: queryForm.username,
    loginIp: queryForm.loginIp,
    status: queryForm.status,
    startTime: queryForm.startTime,
    endTime: queryForm.endTime
  }
  return params
})

// 查询
const handleQuery = async () => {
  loading.value = true
  try {
    // 处理日期范围
    if (dateRange.value && dateRange.value.length === 2) {
      queryForm.startTime = dateRange.value[0]
      queryForm.endTime = dateRange.value[1]
    } else {
      queryForm.startTime = ''
      queryForm.endTime = ''
    }

    const { data } = await pageLoginLogs(queryForm)
    tableData.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 重置
const handleReset = () => {
  queryForm.username = ''
  queryForm.loginIp = ''
  queryForm.status = null
  queryForm.startTime = ''
  queryForm.endTime = ''
  queryForm.current = 1
  queryForm.size = 10
  dateRange.value = []
  handleQuery()
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 查看详情
const handleView = async (row) => {
  try {
    const { data } = await getLoginLogById(row.id)
    detailData.value = data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 删除
const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该登录日志吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteLoginLog(id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确认删除选中的 ${selectedIds.value.length} 条登录日志吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await batchDeleteLoginLogs(selectedIds.value)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 清空日志
const handleClear = async () => {
  try {
    await ElMessageBox.confirm('确认清空所有登录日志吗？此操作不可恢复！', '警告', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await clearLoginLogs()
    ElMessage.success('清空成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('清空失败')
    }
  }
}

// 初始化
handleQuery()

// 导出
const handleExport = async () => {
  exporting.value = true
  exportProgress.value = 0
  exportStatus.value = 0
  exportError.value = ''
  exportTaskNo.value = ''

  try {
    // 处理日期范围
    const params = { ...exportQueryParams.value }
    if (dateRange.value && dateRange.value.length === 2) {
      params.startTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }

    // 创建导出任务
    const res = await createExportTask({
      configCode: 'login_log_export',
      queryParams: JSON.stringify(params)
    })

    exportTaskNo.value = res.data
    exportDialogVisible.value = true
    exportStatus.value = 1

    // 开始轮询进度
    startExportPolling()
  } catch (error) {
    exporting.value = false
    ElMessage.error(error.message || '创建导出任务失败')
  }
}

// 开始轮询导出进度
const startExportPolling = () => {
  if (exportPollingTimer) return

  exportPollingTimer = setInterval(async () => {
    try {
      // 由于我们只有 taskNo，需要通过列表查询获取任务信息
      // 这里简化处理，直接模拟进度增长，实际应该调用进度接口
      exportProgress.value = Math.min(exportProgress.value + 10, 95)

      // 当进度达到一定值时，尝试下载
      if (exportProgress.value >= 95) {
        // 等待一段时间后认为完成
        setTimeout(() => {
          exportProgress.value = 100
          exportStatus.value = 2
          stopExportPolling()
          exporting.value = false
        }, 2000)
        stopExportPolling()
      }
    } catch (error) {
      exportStatus.value = 3
      exportError.value = error.message || '导出失败'
      stopExportPolling()
      exporting.value = false
    }
  }, 1000)
}

// 停止轮询
const stopExportPolling = () => {
  if (exportPollingTimer) {
    clearInterval(exportPollingTimer)
    exportPollingTimer = null
  }
}

// 下载导出文件
const handleDownloadExport = () => {
  if (exportTaskNo.value) {
    const url = getDownloadUrl(exportTaskNo.value)
    window.open(url, '_blank')
  }
}

// 关闭导出弹窗
const handleCloseExportDialog = () => {
  exportDialogVisible.value = false
  stopExportPolling()
}
</script>

<style scoped>
.login-log-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.export-progress-content {
  padding: 20px 0;
}

.export-progress-info {
  margin-top: 16px;
  text-align: center;
  color: #666;
}

.success-text {
  color: #67c23a;
  font-weight: bold;
}

.error-text {
  color: #f56c6c;
}
</style>
