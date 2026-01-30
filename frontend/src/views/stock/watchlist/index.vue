<template>
  <div class="watchlist-container">
    <!-- 操作栏 -->
    <el-card class="action-card" shadow="never">
      <el-button type="primary" :icon="Refresh" @click="handleBatchSync" :loading="syncing">
        批量同步K线
      </el-button>
      <el-button type="danger" :icon="Delete" @click="handleBatchDelete" :disabled="selectedIds.length === 0">
        批量删除
      </el-button>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="stockCode" label="股票代码" width="120" align="center" />
        <el-table-column prop="stockName" label="股票名称" width="150" align="center" />
        <el-table-column prop="market" label="市场" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.market === 'HK'" type="danger" size="small">港股</el-tag>
            <el-tag v-else-if="row.market === 'SH'" type="primary" size="small">沪市</el-tag>
            <el-tag v-else-if="row.market === 'SZ'" type="success" size="small">深市</el-tag>
            <el-tag v-else type="info" size="small">{{ row.market }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" align="center" />
        <el-table-column prop="createTime" label="添加时间" width="180" align="center" />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">查看K线</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 同步日期选择对话框 -->
    <el-dialog v-model="syncDialogVisible" title="批量同步K线" width="400px">
      <el-form :model="syncForm" label-width="100px">
        <el-form-item label="开始日期">
          <el-date-picker
            v-model="syncForm.startDate"
            type="date"
            placeholder="选择开始日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束日期">
          <el-date-picker
            v-model="syncForm.endDate"
            type="date"
            placeholder="选择结束日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="syncDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmBatchSync" :loading="syncing">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Refresh, Delete, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listWatchlist, deleteWatchlist, batchDeleteWatchlist, batchSyncKline } from '@/api/stock'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const syncing = ref(false)
const tableData = ref([])
const selectedIds = ref([])

const syncDialogVisible = ref(false)
const syncForm = ref({
  startDate: '',
  endDate: ''
})

// 获取自选列表
const fetchWatchlist = async () => {
  loading.value = true
  try {
    const res = await listWatchlist()
    tableData.value = res.data || []
  } catch (error) {
    console.error('获取自选列表失败', error)
  } finally {
    loading.value = false
  }
}

// 选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 查看K线
const handleView = (row) => {
  router.push(`/stock/detail/${row.stockCode}`)
}

// 删除单个
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要删除自选股票 ${row.stockName} 吗？`, '提示', {
      type: 'warning'
    })
    await deleteWatchlist(row.id)
    ElMessage.success('删除成功')
    fetchWatchlist()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 只股票吗？`, '提示', {
      type: 'warning'
    })
    await batchDeleteWatchlist(selectedIds.value)
    ElMessage.success('删除成功')
    fetchWatchlist()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 打开批量同步对话框
const handleBatchSync = () => {
  // 默认同步近1个月
  const endDate = new Date()
  const startDate = new Date()
  startDate.setMonth(startDate.getMonth() - 1)

  const formatDate = (date) => {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  }

  syncForm.value = {
    startDate: formatDate(startDate),
    endDate: formatDate(endDate)
  }
  syncDialogVisible.value = true
}

// 确认批量同步
const confirmBatchSync = async () => {
  syncing.value = true
  try {
    await batchSyncKline(syncForm.value.startDate, syncForm.value.endDate)
    ElMessage.success('同步成功')
    syncDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '同步失败')
  } finally {
    syncing.value = false
  }
}

onMounted(() => {
  fetchWatchlist()
})
</script>

<style scoped>
.watchlist-container {
  padding: 20px;
}
.action-card {
  margin-bottom: 15px;
}
.table-card {
  margin-bottom: 15px;
}
</style>
