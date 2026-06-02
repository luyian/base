<template>
  <div class="approval-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :inline="true" :model="queryParams">
        <el-form-item label="审批状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 140px">
            <el-option label="审批中" value="PENDING" />
            <el-option label="已通过" value="APPROVED" />
            <el-option label="已拒绝" value="REJECTED" />
            <el-option label="已撤回" value="CANCELED" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务类型">
          <el-input v-model="queryParams.businessType" placeholder="请输入" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="showSubmitDialog = true">发起审批</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" style="margin-top: 12px">
      <el-table v-loading="loading" :data="tableData" stripe>
        <el-table-column prop="title" label="审批标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="templateName" label="模板" width="140" />
        <el-table-column prop="businessType" label="业务类型" width="120" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicantName" label="发起人" width="100" />
        <el-table-column prop="submittedAt" label="发起时间" width="170" />
        <el-table-column prop="completedAt" label="完成时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="goDetail(row.id)">详情</el-button>
            <el-button link type="warning" @click="handleSync(row.id)" :disabled="row.status !== 'PENDING'">同步</el-button>
            <el-button link type="danger" @click="handleCancel(row.id)" :disabled="row.status !== 'PENDING'">撤销</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        style="margin-top: 16px; justify-content: flex-end"
        @size-change="fetchList"
        @current-change="fetchList"
      />
    </el-card>

    <!-- 发起审批弹窗 -->
    <el-dialog v-model="showSubmitDialog" title="发起审批" width="500px">
      <el-form :model="submitForm" label-width="90px">
        <el-form-item label="模板编码" required>
          <el-input v-model="submitForm.templateCode" placeholder="如 leave_apply" />
        </el-form-item>
        <el-form-item label="业务主键" required>
          <el-input v-model="submitForm.businessKey" placeholder="业务记录唯一标识" />
        </el-form-item>
        <el-form-item label="业务类型">
          <el-input v-model="submitForm.businessType" placeholder="如 leave" />
        </el-form-item>
        <el-form-item label="审批标题">
          <el-input v-model="submitForm.title" placeholder="审批标题" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showSubmitDialog = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listApprovals, submitApproval, cancelApproval, syncApprovalStatus } from '@/api/approval'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const showSubmitDialog = ref(false)
const submitLoading = ref(false)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  status: '',
  businessType: ''
})

const submitForm = reactive({
  templateCode: '',
  businessKey: '',
  businessType: '',
  title: ''
})

const statusMap = {
  PENDING: { label: '审批中', type: 'warning' },
  APPROVED: { label: '已通过', type: 'success' },
  REJECTED: { label: '已拒绝', type: 'danger' },
  CANCELED: { label: '已撤回', type: 'info' }
}

const statusLabel = (s) => statusMap[s]?.label || s
const statusTagType = (s) => statusMap[s]?.type || ''

async function fetchList() {
  loading.value = true
  try {
    const res = await listApprovals(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  queryParams.pageNum = 1
  fetchList()
}

function resetQuery() {
  queryParams.status = ''
  queryParams.businessType = ''
  handleQuery()
}

function goDetail(id) {
  router.push(`/approval/detail/${id}`)
}

async function handleSubmit() {
  if (!submitForm.templateCode || !submitForm.businessKey) {
    ElMessage.warning('请填写模板编码和业务主键')
    return
  }
  submitLoading.value = true
  try {
    await submitApproval(submitForm)
    ElMessage.success('审批发起成功')
    showSubmitDialog.value = false
    submitForm.templateCode = ''
    submitForm.businessKey = ''
    submitForm.businessType = ''
    submitForm.title = ''
    fetchList()
  } catch (e) {
    ElMessage.error(e.message || '发起失败')
  } finally {
    submitLoading.value = false
  }
}

async function handleCancel(id) {
  await ElMessageBox.confirm('确定撤销该审批？', '提示', { type: 'warning' })
  await cancelApproval(id)
  ElMessage.success('撤销成功')
  fetchList()
}

async function handleSync(id) {
  await syncApprovalStatus(id)
  ElMessage.success('同步完成')
  fetchList()
}

onMounted(() => fetchList())
</script>

<style scoped>
.approval-container {
  padding: 16px;
}
.search-card :deep(.el-card__body) {
  padding-bottom: 0;
}
</style>
