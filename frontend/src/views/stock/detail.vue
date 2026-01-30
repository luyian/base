<template>
  <div class="stock-detail-container">
    <!-- 股票基本信息 -->
    <el-card class="info-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>股票信息</span>
          <div>
            <el-button type="primary" :icon="Refresh" @click="handleSyncKline" :loading="syncing">
              同步K线
            </el-button>
            <el-button :icon="Back" @click="handleBack">返回</el-button>
          </div>
        </div>
      </template>
      <el-descriptions :column="4" border v-loading="infoLoading">
        <el-descriptions-item label="股票代码">{{ stockInfo.stockCode }}</el-descriptions-item>
        <el-descriptions-item label="股票名称">{{ stockInfo.stockName }}</el-descriptions-item>
        <el-descriptions-item label="市场">
          <el-tag v-if="stockInfo.market === 'HK'" type="danger" size="small">港股</el-tag>
          <el-tag v-else-if="stockInfo.market === 'SH'" type="primary" size="small">沪市</el-tag>
          <el-tag v-else-if="stockInfo.market === 'SZ'" type="success" size="small">深市</el-tag>
          <el-tag v-else type="info" size="small">{{ stockInfo.market }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="交易所">{{ stockInfo.exchange }}</el-descriptions-item>
        <el-descriptions-item label="货币">{{ stockInfo.currency }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="stockInfo.status === 1" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">退市</el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-card>

    <!-- K线图 -->
    <el-card class="chart-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>K线图</span>
          <el-radio-group v-model="dateRange" @change="handleDateRangeChange">
            <el-radio-button label="week">近1周</el-radio-button>
            <el-radio-button label="month">近1月</el-radio-button>
            <el-radio-button label="quarter">近3月</el-radio-button>
          </el-radio-group>
        </div>
      </template>
      <div v-loading="klineLoading">
        <KlineChart :data="klineData" :stock-name="stockInfo.stockName" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Refresh, Back } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getStockByCode, getKlineData, syncKline } from '@/api/stock'
import KlineChart from './components/KlineChart.vue'

const route = useRoute()
const router = useRouter()

const stockCode = computed(() => route.params.code)

const infoLoading = ref(false)
const klineLoading = ref(false)
const syncing = ref(false)

const stockInfo = ref({})
const klineData = ref([])
const dateRange = ref('month')

// 获取股票信息
const fetchStockInfo = async () => {
  infoLoading.value = true
  try {
    const res = await getStockByCode(stockCode.value)
    stockInfo.value = res.data || {}
  } catch (error) {
    console.error('获取股票信息失败', error)
  } finally {
    infoLoading.value = false
  }
}

// 计算日期范围
const getDateRange = () => {
  const endDate = new Date()
  const startDate = new Date()

  switch (dateRange.value) {
    case 'week':
      startDate.setDate(startDate.getDate() - 7)
      break
    case 'month':
      startDate.setMonth(startDate.getMonth() - 1)
      break
    case 'quarter':
      startDate.setMonth(startDate.getMonth() - 3)
      break
    default:
      startDate.setMonth(startDate.getMonth() - 1)
  }

  const formatDate = (date) => {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    return `${year}-${month}-${day}`
  }

  return {
    startDate: formatDate(startDate),
    endDate: formatDate(endDate)
  }
}

// 获取K线数据
const fetchKlineData = async () => {
  klineLoading.value = true
  try {
    const { startDate, endDate } = getDateRange()
    const res = await getKlineData(stockCode.value, startDate, endDate)
    klineData.value = res.data || []
  } catch (error) {
    console.error('获取K线数据失败', error)
  } finally {
    klineLoading.value = false
  }
}

// 日期范围变化
const handleDateRangeChange = () => {
  fetchKlineData()
}

// 同步K线数据
const handleSyncKline = async () => {
  syncing.value = true
  try {
    const { startDate, endDate } = getDateRange()
    await syncKline(stockCode.value, startDate, endDate)
    ElMessage.success('同步成功')
    fetchKlineData()
  } catch (error) {
    ElMessage.error(error.message || '同步失败')
  } finally {
    syncing.value = false
  }
}

// 返回
const handleBack = () => {
  router.back()
}

onMounted(() => {
  fetchStockInfo()
  fetchKlineData()
})
</script>

<style scoped>
.stock-detail-container {
  padding: 20px;
}
.info-card {
  margin-bottom: 15px;
}
.chart-card {
  margin-bottom: 15px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
