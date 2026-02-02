<template>
  <div class="watchlist-container">
    <!-- 操作栏 -->
    <el-card class="action-card" shadow="never">
      <el-button type="default" :icon="Refresh" @click="fetchWatchlist">
        刷新列表
      </el-button>
      <el-button type="primary" :icon="Download" @click="handleBatchSync" :loading="syncing">
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
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="TrendCharts" @click="handleViewTrend(row)">趋势</el-button>
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

    <!-- 趋势弹窗（日K线 + 分钟K线） -->
    <el-dialog
      v-model="trendDialogVisible"
      :title="`${currentStock.stockName || currentStock.stockCode} - 趋势`"
      width="90%"
      top="5vh"
      destroy-on-close
    >
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="日K线" name="day">
          <div class="kline-toolbar">
            <el-radio-group v-model="dayKlineDateRange" @change="handleDayKlineDateChange">
              <el-radio-button label="week">近1周</el-radio-button>
              <el-radio-button label="month">近1月</el-radio-button>
              <el-radio-button label="quarter">近3月</el-radio-button>
            </el-radio-group>
            <el-button :icon="Refresh" @click="refreshDayKline" :loading="dayKlineLoading" style="margin-left: 16px">
              刷新
            </el-button>
          </div>
          <KlineChart
            v-loading="dayKlineLoading"
            :data="dayKlineData"
            :stock-name="currentStock.stockName"
          />
        </el-tab-pane>
        <el-tab-pane label="分钟K线" name="minute">
          <div class="kline-toolbar">
            <el-radio-group v-model="klineType" @change="handleKlineTypeChange">
              <el-radio-button :label="1">1分钟</el-radio-button>
              <el-radio-button :label="5">5分钟</el-radio-button>
            </el-radio-group>
            <el-button :icon="Refresh" @click="refreshKline" :loading="klineLoading" style="margin-left: 16px">
              刷新
            </el-button>
          </div>
          <MinuteKlineChart
            v-loading="klineLoading"
            :data="klineData"
            :stock-name="currentStock.stockName"
            :k-type="klineType"
            :has-more="hasMoreKline"
            @load-more="loadMoreKline"
          />
        </el-tab-pane>
      </el-tabs>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { Refresh, Delete, Download, TrendCharts } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listWatchlist, deleteWatchlist, batchDeleteWatchlist, batchSyncKline, getMinuteKline, getKlineData } from '@/api/stock'
import MinuteKlineChart from '../components/MinuteKlineChart.vue'
import KlineChart from '../components/KlineChart.vue'

const loading = ref(false)
const syncing = ref(false)
const tableData = ref([])
const selectedIds = ref([])

const syncDialogVisible = ref(false)
const syncForm = ref({
  startDate: '',
  endDate: ''
})

// 趋势弹窗相关状态
const trendDialogVisible = ref(false)
const activeTab = ref('day')
const currentStock = ref({})

// 日K线相关状态
const dayKlineLoading = ref(false)
const dayKlineData = ref([])
const dayKlineDateRange = ref('month')

// 分钟K线相关状态
const klineLoading = ref(false)
const klineType = ref(1)
const klineData = ref([])
const hasMoreKline = ref(false)
const earliestTimestamp = ref(null)
const loadingMore = ref(false)

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

// 日期格式化
const formatDate = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

// 获取日K线日期范围
const getDayKlineDateRange = () => {
  const endDate = new Date()
  const startDate = new Date()
  if (dayKlineDateRange.value === 'week') {
    startDate.setDate(startDate.getDate() - 7)
  } else if (dayKlineDateRange.value === 'month') {
    startDate.setMonth(startDate.getMonth() - 1)
  } else {
    startDate.setMonth(startDate.getMonth() - 3)
  }
  return { startDate: formatDate(startDate), endDate: formatDate(endDate) }
}

// 获取日K线数据
const fetchDayKlineData = async () => {
  dayKlineLoading.value = true
  try {
    const { startDate, endDate } = getDayKlineDateRange()
    const res = await getKlineData(currentStock.value.stockCode, startDate, endDate)
    dayKlineData.value = res.data || []
  } catch (error) {
    ElMessage.error(error.message || '获取日K线数据失败')
  } finally {
    dayKlineLoading.value = false
  }
}

// 日K线日期范围切换
const handleDayKlineDateChange = () => {
  fetchDayKlineData()
}

// 刷新日K线
const refreshDayKline = () => {
  fetchDayKlineData()
}

// 查看趋势（打开弹窗）
const handleViewTrend = (row) => {
  currentStock.value = row
  activeTab.value = 'day'
  // 重置数据
  dayKlineData.value = []
  klineData.value = []
  hasMoreKline.value = false
  earliestTimestamp.value = null
  trendDialogVisible.value = true
  // 加载日K线数据
  fetchDayKlineData()
}

// Tab 切换处理
const handleTabChange = (tabName) => {
  if (tabName === 'day' && dayKlineData.value.length === 0) {
    fetchDayKlineData()
  } else if (tabName === 'minute' && klineData.value.length === 0) {
    fetchKlineData()
  }
}

// 获取分钟K线数据
const fetchKlineData = async (et = null) => {
  klineLoading.value = true
  try {
    const res = await getMinuteKline(currentStock.value.stockCode, klineType.value, et, 100)
    if (res.data) {
      if (et) {
        // 加载更多历史数据，插入到前面
        klineData.value = [...res.data.klineList, ...klineData.value]
      } else {
        klineData.value = res.data.klineList || []
      }
      hasMoreKline.value = res.data.hasMore
      earliestTimestamp.value = res.data.earliestTimestamp
    }
  } catch (error) {
    ElMessage.error(error.message || '获取K线数据失败')
  } finally {
    klineLoading.value = false
    loadingMore.value = false
  }
}

// K线类型切换
const handleKlineTypeChange = () => {
  klineData.value = []
  hasMoreKline.value = false
  earliestTimestamp.value = null
  fetchKlineData()
}

// 刷新K线
const refreshKline = () => {
  klineData.value = []
  hasMoreKline.value = false
  earliestTimestamp.value = null
  fetchKlineData()
}

// 加载更多历史数据
const loadMoreKline = () => {
  if (loadingMore.value || !hasMoreKline.value || !earliestTimestamp.value) {
    return
  }
  loadingMore.value = true
  fetchKlineData(earliestTimestamp.value)
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
.kline-toolbar {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
}
</style>
