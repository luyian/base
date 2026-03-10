<template>
  <div class="my-initiated-container">
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
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="tableData" style="width: 100%" v-loading="loading">
        <el-table-column prop="title" label="流程标题" min-width="200" show-overflow-tooltip />
        <el-table-column prop="processName" label="流程名称" width="150" />
        <el-table-column prop="currentNodeName" label="当前节点" width="150" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="180" />
        <el-table-column prop="endTime" label="结束时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 'RUNNING'" type="danger" link @click="handleTerminate(row)">终止</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="detailVisible" title="流程详情" width="800px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="流程标题">{{ currentRow?.title }}</el-descriptions-item>
        <el-descriptions-item label="流程名称">{{ currentRow?.processName }}</el-descriptions-item>
        <el-descriptions-item label="当前节点">{{ currentRow?.currentNodeName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(currentRow?.status)">
            {{ getStatusText(currentRow?.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="开始时间">{{ currentRow?.startTime }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ currentRow?.endTime || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>审批历史</el-divider>
      <el-table :data="historyData" style="width: 100%">
        <el-table-column prop="nodeName" label="节点" width="150" />
        <el-table-column prop="operatorName" label="操作人" width="120" />
        <el-table-column prop="action" label="操作" width="100">
          <template #default="{ row }">
            {{ getActionText(row.action) }}
          </template>
        </el-table-column>
        <el-table-column prop="comment" label="意见" min-width="200" />
        <el-table-column prop="createTime" label="时间" width="180" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyInitiated, terminateProcess, getProcessHistory } from '@/api/workflow'

const loading = ref(false)
const queryForm = reactive({
  keyword: ''
})
const tableData = ref([])
const detailVisible = ref(false)
const currentRow = ref(null)
const historyData = ref([])

onMounted(() => {
  getList()
})

const getList = async () => {
  loading.value = true
  try {
    const res = await getMyInitiated()
    tableData.value = res.data || []
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  getList()
}

const handleReset = () => {
  queryForm.keyword = ''
  handleQuery()
}

const handleView = async (row) => {
  currentRow.value = row
  try {
    const res = await getProcessHistory(row.id)
    historyData.value = res.data || []
  } catch (e) {
    historyData.value = []
  }
  detailVisible.value = true
}

const handleTerminate = async (row) => {
  try {
    await ElMessageBox.confirm('确定要终止此流程吗？', '提示', { type: 'warning' })
    await terminateProcess(row.id)
    ElMessage.success('终止成功')
    getList()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error('终止失败')
    }
  }
}

const getStatusText = (status) => {
  const map = { RUNNING: '进行中', COMPLETED: '已完成', TERMINATED: '已终止', ROLLED_BACK: '已回退' }
  return map[status] || '未知'
}

const getStatusTagType = (status) => {
  const map = { RUNNING: 'primary', COMPLETED: 'success', TERMINATED: 'danger', ROLLED_BACK: 'warning' }
  return map[status] || 'info'
}

const getActionText = (action) => {
  const map = { START: '发起', APPROVE: '通过', REJECT: '拒绝', ROLLBACK: '回退', DELEGATE: '转办', TERMINATE: '终止', COMPLETE: '完成' }
  return map[action] || action
}
</script>

<style scoped>
.my-initiated-container {
  padding: 16px;
}

.search-card {
  margin-bottom: 16px;
}

.table-card {
  min-height: calc(100vh - 180px);
}
</style>
