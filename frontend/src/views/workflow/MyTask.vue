<template>
  <div class="my-task-container">
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
        <el-table-column prop="nodeName" label="当前节点" width="150" />
        <el-table-column prop="createTime" label="到达时间" width="180" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleApprove(row)">审批</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="任务审批" width="500px">
      <el-form :model="approveForm" label-width="80px">
        <el-form-item label="审批意见">
          <el-input v-model="approveForm.comment" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="danger" @click="handleReject">拒绝</el-button>
        <el-button type="success" @click="handlePass">通过</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getMyTasks, approveTask } from '@/api/workflow'

const loading = ref(false)
const queryForm = reactive({
  keyword: ''
})
const tableData = ref([])
const dialogVisible = ref(false)
const approveForm = reactive({
  taskId: '',
  approveResult: '',
  comment: ''
})

onMounted(() => {
  getList()
})

const getList = async () => {
  loading.value = true
  try {
    const res = await getMyTasks()
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

const handleApprove = (row) => {
  approveForm.taskId = row.id
  approveForm.comment = ''
  approveForm.approveResult = ''
  dialogVisible.value = true
}

const handlePass = async () => {
  try {
    await approveTask({
      taskId: approveForm.taskId,
      approveResult: 'APPROVE',
      comment: approveForm.comment
    })
    ElMessage.success('审批通过')
    dialogVisible.value = false
    getList()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const handleReject = async () => {
  try {
    await approveTask({
      taskId: approveForm.taskId,
      approveResult: 'REJECT',
      comment: approveForm.comment
    })
    ElMessage.success('已拒绝')
    dialogVisible.value = false
    getList()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}
</script>

<style scoped>
.my-task-container {
  padding: 16px;
}

.search-card {
  margin-bottom: 16px;
}

.table-card {
  min-height: calc(100vh - 180px);
}
</style>
