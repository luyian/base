<template>
  <div class="detail-container" v-loading="loading">
    <el-page-header @back="$router.back()" content="审批详情" style="margin-bottom: 20px" />

    <el-card v-if="detail">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="审批标题">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="statusTagType(detail.status)" size="small">{{ statusLabel(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="模板名称">{{ detail.templateName }}</el-descriptions-item>
        <el-descriptions-item label="模板编码">{{ detail.templateCode }}</el-descriptions-item>
        <el-descriptions-item label="平台">{{ detail.platform }}</el-descriptions-item>
        <el-descriptions-item label="平台实例ID">{{ detail.platformInstanceId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="业务主键">{{ detail.businessKey }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ detail.businessType || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发起人">{{ detail.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="审批结果">{{ detail.resultComment || '-' }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detail.submittedAt }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ detail.completedAt || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 20px">
        <el-button type="warning" @click="handleSync" :disabled="detail.status !== 'PENDING'">同步状态</el-button>
        <el-button type="danger" @click="handleCancel" :disabled="detail.status !== 'PENDING'">撤销审批</el-button>
      </div>
    </el-card>

    <el-card v-if="detail && detail.formData" header="表单数据" style="margin-top: 16px">
      <pre class="form-data-pre">{{ formatJson(detail.formData) }}</pre>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getApprovalDetail, cancelApproval, syncApprovalStatus } from '@/api/approval'

const route = useRoute()
const router = useRouter()
const loading = ref(false)
const detail = ref(null)

const statusMap = {
  PENDING: { label: '审批中', type: 'warning' },
  APPROVED: { label: '已通过', type: 'success' },
  REJECTED: { label: '已拒绝', type: 'danger' },
  CANCELED: { label: '已撤回', type: 'info' }
}
const statusLabel = (s) => statusMap[s]?.label || s
const statusTagType = (s) => statusMap[s]?.type || ''

function formatJson(str) {
  try {
    return JSON.stringify(JSON.parse(str), null, 2)
  } catch {
    return str
  }
}

async function fetchDetail() {
  loading.value = true
  try {
    const res = await getApprovalDetail(route.params.id)
    detail.value = res.data
  } finally {
    loading.value = false
  }
}

async function handleSync() {
  await syncApprovalStatus(route.params.id)
  ElMessage.success('同步完成')
  fetchDetail()
}

async function handleCancel() {
  await ElMessageBox.confirm('确定撤销该审批？', '提示', { type: 'warning' })
  await cancelApproval(route.params.id)
  ElMessage.success('撤销成功')
  fetchDetail()
}

onMounted(() => fetchDetail())
</script>

<style scoped>
.detail-container {
  padding: 16px;
}
.form-data-pre {
  background: #f5f7fa;
  padding: 12px;
  border-radius: 4px;
  font-size: 13px;
  overflow-x: auto;
  margin: 0;
}
</style>
