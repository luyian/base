<template>
  <div class="event-log-container">
    <!-- 连接状态 -->
    <el-card shadow="never" style="margin-bottom: 12px">
      <div style="display: flex; align-items: center; gap: 16px">
        <span>WebSocket 连接状态：</span>
        <span v-for="(connected, platform) in wsStatus" :key="platform" style="display: flex; align-items: center; gap: 4px">
          <span :style="{ width: '8px', height: '8px', borderRadius: '50%', display: 'inline-block',
                          background: connected ? '#67c23a' : '#f56c6c' }" />
          {{ platform === 'feishu' ? '飞书' : platform }}
          {{ connected ? '已连接' : '未连接' }}
        </span>
        <el-button size="small" @click="fetchWsStatus">刷新</el-button>
      </div>
    </el-card>

    <!-- 日志列表 -->
    <el-card shadow="never">
      <el-form :inline="true" :model="queryParams" style="margin-bottom: 12px">
        <el-form-item label="事件类型">
          <el-input v-model="queryParams.eventType" placeholder="如 approval_instance" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="处理状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 130px">
            <el-option label="待处理" value="PENDING" />
            <el-option label="成功" value="SUCCESS" />
            <el-option label="失败" value="FAILED" />
            <el-option label="已跳过" value="SKIPPED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="eventId" label="事件ID" width="200" show-overflow-tooltip />
        <el-table-column prop="eventType" label="事件类型" width="200" />
        <el-table-column prop="platform" label="平台" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small">{{ row.platform === 'feishu' ? '飞书' : row.platform }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="处理状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="eventStatusType(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="retryCount" label="重试次数" width="90" align="center" />
        <el-table-column prop="errorMessage" label="错误信息" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="140" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="showPayload(row)">详情</el-button>
            <el-button link type="warning" @click="handleRetry(row.id)" :disabled="row.status === 'SUCCESS'">重试</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next"
        :page-sizes="[20, 50, 100]"
        style="margin-top: 16px; justify-content: flex-end"
        @size-change="fetchList"
        @current-change="fetchList"
      />
    </el-card>

    <!-- 事件详情弹窗 -->
    <el-dialog v-model="payloadVisible" title="事件详情" width="700px">
      <pre class="payload-pre">{{ payloadContent }}</pre>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { listEventLogs, getWsStatus, retryEvent } from '@/api/approvalTemplate'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const wsStatus = ref({})
const payloadVisible = ref(false)
const payloadContent = ref('')

const queryParams = reactive({ pageNum: 1, pageSize: 20, eventType: '', status: '' })

const eventStatusType = (s) => {
  const map = { SUCCESS: 'success', FAILED: 'danger', PENDING: 'warning', SKIPPED: 'info', PROCESSING: '' }
  return map[s] || ''
}

async function fetchList() {
  loading.value = true
  try {
    const res = await listEventLogs(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function fetchWsStatus() {
  try {
    const res = await getWsStatus()
    wsStatus.value = res.data
  } catch {
    wsStatus.value = {}
  }
}

function handleQuery() {
  queryParams.pageNum = 1
  fetchList()
}

function showPayload(row) {
  try {
    payloadContent.value = JSON.stringify(JSON.parse(row.eventPayload), null, 2)
  } catch {
    payloadContent.value = row.eventPayload
  }
  payloadVisible.value = true
}

async function handleRetry(id) {
  await retryEvent(id)
  ElMessage.success('已重新入队')
  fetchList()
}

onMounted(() => {
  fetchList()
  fetchWsStatus()
})
</script>

<style scoped>
.event-log-container {
  padding: 16px;
}
.payload-pre {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  font-size: 13px;
  max-height: 500px;
  overflow: auto;
  margin: 0;
}
</style>
