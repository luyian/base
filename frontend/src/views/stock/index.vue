<template>
  <div class="stock-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="市场">
          <el-select v-model="queryForm.market" placeholder="请选择市场" clearable style="width: 120px">
            <el-option label="港股" value="HK" />
            <el-option label="沪市" value="SH" />
            <el-option label="深市" value="SZ" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="股票代码/名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
          <el-button type="warning" :icon="Download" @click="handleOpenSyncDialog">同步股票</el-button>
          <el-button type="success" :icon="Download" @click="handleOpenSyncAllDialog">拉取K线</el-button>
          <el-button type="danger" :icon="Warning" @click="handleOpenFailureDialog">失败记录</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="tableData" border stripe>
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
        <el-table-column prop="exchange" label="交易所" width="120" align="center" />
        <el-table-column prop="currency" label="货币" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success" size="small">正常</el-tag>
            <el-tag v-else type="danger" size="small">退市</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">查看K线</el-button>
            <el-button
              :type="isInWatchlist(row.stockCode) ? 'warning' : 'success'"
              link
              :icon="isInWatchlist(row.stockCode) ? StarFilled : Star"
              @click="handleToggleWatchlist(row)"
            >
              {{ isInWatchlist(row.stockCode) ? '取消自选' : '加自选' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 同步股票对话框 -->
    <el-dialog v-model="syncDialogVisible" title="同步股票数据" width="400px">
      <el-form :model="syncForm" label-width="80px">
        <el-form-item label="市场">
          <el-select v-model="syncForm.market" placeholder="请选择市场" style="width: 100%">
            <el-option label="港股" value="HK" />
            <el-option label="沪市" value="SH" />
            <el-option label="深市" value="SZ" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="syncDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="syncLoading" @click="handleSyncStockList">开始同步</el-button>
      </template>
    </el-dialog>

    <!-- 拉取全部K线对话框 -->
    <el-dialog v-model="syncAllDialogVisible" title="拉取K线数据" width="500px">
      <el-form :model="syncAllForm" label-width="100px">
        <el-form-item label="市场">
          <el-select v-model="syncAllForm.market" placeholder="全部市场" clearable style="width: 100%">
            <el-option label="全部市场" value="" />
            <el-option label="港股" value="HK" />
            <el-option label="沪市" value="SH" />
            <el-option label="深市" value="SZ" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker
            v-model="syncAllForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="同步模式">
          <el-radio-group v-model="syncAllForm.concurrent">
            <el-radio :label="false">普通模式</el-radio>
            <el-radio :label="true">并发模式（推荐）</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-alert
            :title="syncAllForm.concurrent ? '并发模式：多线程同时拉取，速度更快，失败会自动记录' : '普通模式：单线程顺序拉取，速度较慢'"
            :type="syncAllForm.concurrent ? 'success' : 'warning'"
            :closable="false"
            show-icon
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="syncAllDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="syncAllLoading" @click="handleSyncAllKline">开始拉取</el-button>
      </template>
    </el-dialog>

    <!-- 失败记录对话框 -->
    <el-dialog v-model="failureDialogVisible" title="同步失败记录" width="900px">
      <div class="failure-toolbar">
        <el-form :inline="true" :model="failureQuery">
          <el-form-item label="股票代码">
            <el-input v-model="failureQuery.stockCode" placeholder="请输入" clearable style="width: 120px" />
          </el-form-item>
          <el-form-item label="状态">
            <el-select v-model="failureQuery.status" placeholder="全部" clearable style="width: 100px">
              <el-option label="待重试" :value="0" />
              <el-option label="已成功" :value="1" />
              <el-option label="已放弃" :value="2" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="loadFailures">查询</el-button>
            <el-button type="success" :loading="retryLoading" @click="handleRetryFailed">补拉失败数据</el-button>
          </el-form-item>
        </el-form>
      </div>
      <el-table v-loading="failureLoading" :data="failureList" border stripe max-height="400px">
        <el-table-column prop="stockCode" label="股票代码" width="100" align="center" />
        <el-table-column prop="startDate" label="开始日期" width="110" align="center" />
        <el-table-column prop="endDate" label="结束日期" width="110" align="center" />
        <el-table-column prop="failureReason" label="失败原因" min-width="200" show-overflow-tooltip />
        <el-table-column prop="retryCount" label="重试次数" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 0" type="warning" size="small">待重试</el-tag>
            <el-tag v-else-if="row.status === 1" type="success" size="small">已成功</el-tag>
            <el-tag v-else type="info" size="small">已放弃</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" align="center" />
      </el-table>
      <el-pagination
        v-model:current-page="failureQuery.page"
        v-model:page-size="failureQuery.size"
        :total="failureTotal"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadFailures"
        @current-change="loadFailures"
        style="margin-top: 15px; justify-content: flex-end"
      />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { Search, Refresh, View, Star, StarFilled, Download, Warning } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { listStocks, addWatchlist, deleteWatchlist, listWatchlist, syncStockList, batchSyncAllKline, batchSyncAllKlineConcurrent, listSyncFailures, retryFailedSync } from '@/api/stock'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

// 自选股票映射：stockCode -> watchlistId
const watchlistMap = ref(new Map())

const queryForm = ref({
  page: 1,
  size: 20,
  market: '',
  keyword: ''
})

// 同步股票相关
const syncDialogVisible = ref(false)
const syncLoading = ref(false)
const syncForm = ref({
  market: 'HK'
})

// 拉取全部K线相关
const syncAllDialogVisible = ref(false)
const syncAllLoading = ref(false)
const syncAllForm = ref({
  market: '',
  dateRange: [],
  concurrent: true
})

// 失败记录相关
const failureDialogVisible = ref(false)
const failureLoading = ref(false)
const failureList = ref([])
const failureTotal = ref(0)
const retryLoading = ref(false)
const failureQuery = ref({
  stockCode: '',
  status: null,
  page: 1,
  size: 20
})

const handleOpenSyncDialog = () => {
  syncDialogVisible.value = true
}

const handleOpenSyncAllDialog = () => {
  syncAllForm.value = {
    market: '',
    dateRange: [],
    concurrent: true
  }
  syncAllDialogVisible.value = true
}

const handleOpenFailureDialog = () => {
  failureQuery.value = {
    stockCode: '',
    status: null,
    page: 1,
    size: 20
  }
  failureDialogVisible.value = true
  loadFailures()
}

const loadFailures = async () => {
  failureLoading.value = true
  try {
    const res = await listSyncFailures(failureQuery.value)
    failureList.value = res.data.records || []
    failureTotal.value = res.data.total || 0
  } catch (error) {
    console.error('查询失败记录失败', error)
  } finally {
    failureLoading.value = false
  }
}

const handleRetryFailed = async () => {
  retryLoading.value = true
  try {
    const res = await retryFailedSync(failureQuery.value.stockCode || null, 3)
    ElMessage.success(`补拉完成，成功 ${res.data} 条`)
    loadFailures()
  } catch (error) {
    ElMessage.error(error.message || '补拉失败')
  } finally {
    retryLoading.value = false
  }
}

const handleSyncStockList = async () => {
  syncLoading.value = true
  try {
    const res = await syncStockList(syncForm.value.market)
    ElMessage.success(`同步成功，共同步 ${res.data} 条股票数据`)
    syncDialogVisible.value = false
    handleQuery()
  } catch (error) {
    ElMessage.error(error.message || '同步失败')
  } finally {
    syncLoading.value = false
  }
}

const handleSyncAllKline = async () => {
  syncAllLoading.value = true
  try {
    const startDate = syncAllForm.value.dateRange?.[0] || null
    const endDate = syncAllForm.value.dateRange?.[1] || null
    let res
    if (syncAllForm.value.concurrent) {
      res = await batchSyncAllKlineConcurrent(syncAllForm.value.market, startDate, endDate)
    } else {
      res = await batchSyncAllKline(syncAllForm.value.market, startDate, endDate)
    }
    ElMessage.success(`拉取成功，共同步 ${res.data} 条K线数据`)
    syncAllDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '拉取失败')
  } finally {
    syncAllLoading.value = false
  }
}

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await listStocks(queryForm.value)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('查询失败', error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.value = {
    page: 1,
    size: 20,
    market: '',
    keyword: ''
  }
  handleQuery()
}

const handleView = (row) => {
  router.push(`/stock/detail/${row.stockCode}`)
}

// 判断是否在自选中
const isInWatchlist = (stockCode) => {
  return watchlistMap.value.has(stockCode)
}

// 加载自选列表
const loadWatchlist = async () => {
  try {
    const res = await listWatchlist()
    const map = new Map()
    ;(res.data || []).forEach(item => {
      map.set(item.stockCode, item.id)
    })
    watchlistMap.value = map
  } catch (error) {
    console.error('获取自选列表失败', error)
  }
}

// 切换自选状态
const handleToggleWatchlist = async (row) => {
  const stockCode = row.stockCode
  if (isInWatchlist(stockCode)) {
    // 取消自选
    try {
      const watchlistId = watchlistMap.value.get(stockCode)
      await deleteWatchlist(watchlistId)
      watchlistMap.value.delete(stockCode)
      ElMessage.success('取消自选成功')
    } catch (error) {
      ElMessage.error(error.message || '取消自选失败')
    }
  } else {
    // 添加自选
    try {
      const res = await addWatchlist(stockCode, '')
      watchlistMap.value.set(stockCode, res.data)
      ElMessage.success('添加自选成功')
    } catch (error) {
      ElMessage.error(error.message || '添加自选失败')
    }
  }
}

onMounted(() => {
  handleQuery()
  loadWatchlist()
})
</script>

<style scoped>
.stock-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 15px;
}
.table-card {
  margin-bottom: 15px;
}
.failure-toolbar {
  margin-bottom: 15px;
}
</style>
