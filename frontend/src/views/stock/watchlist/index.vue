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
      <template #header>
        <div class="trend-dialog-header">
          <span>{{ currentStock.stockName || currentStock.stockCode }} - 趋势</span>
          <div class="trend-dialog-actions">
            <el-button type="primary" :icon="View" @click="handlePreviewPdf" :loading="pdfLoading">
              预览
            </el-button>
            <el-button type="success" :icon="Download" @click="handleExportPdf" :loading="pdfLoading">
              导出PDF
            </el-button>
          </div>
        </div>
      </template>
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
            ref="dayChartRef"
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
            ref="minuteChartRef"
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

    <!-- PDF 预览弹窗：按顺序上下平铺日K线、分钟K线 -->
    <el-dialog
      v-model="previewDialogVisible"
      title="PDF 预览"
      width="700px"
      destroy-on-close
    >
      <div class="pdf-preview-content">
        <div class="pdf-section">
          <div class="pdf-section-title">日K线</div>
          <img v-if="previewDayImage" :src="previewDayImage" alt="日K线" class="pdf-preview-img" />
          <div v-else class="pdf-preview-placeholder">暂无数据</div>
        </div>
        <div class="pdf-section">
          <div class="pdf-section-title">分钟K线</div>
          <img v-if="previewMinuteImage" :src="previewMinuteImage" alt="分钟K线" class="pdf-preview-img" />
          <div v-else class="pdf-preview-placeholder">暂无数据</div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, nextTick } from 'vue'
import { Refresh, Delete, Download, TrendCharts, View } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listWatchlist, deleteWatchlist, batchDeleteWatchlist, batchSyncKline, getMinuteKline, getKlineData } from '@/api/stock'
import MinuteKlineChart from '../components/MinuteKlineChart.vue'
import KlineChart from '../components/KlineChart.vue'
import { jsPDF } from 'jspdf'

const loading = ref(false)
const syncing = ref(false)
const tableData = ref([])
const selectedIds = ref([])

// 图表 ref（用于导出图片）
const dayChartRef = ref(null)
const minuteChartRef = ref(null)

// PDF 预览与导出
const pdfLoading = ref(false)
const previewDialogVisible = ref(false)
const previewDayImage = ref('')
const previewMinuteImage = ref('')

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

// 等待图表渲染/尺寸更新
const waitForChartResize = (ms = 400) => new Promise(resolve => setTimeout(resolve, ms))

/**
 * 按顺序获取日K线、分钟K线两张图表的 Base64 图片（会先切到对应 Tab 并等待渲染）
 */
const getBothChartImages = async () => {
  let dayImg = ''
  let minuteImg = ''

  // 1. 切到日K线 Tab 并等待
  activeTab.value = 'day'
  await nextTick()
  await waitForChartResize()
  if (dayChartRef.value && typeof dayChartRef.value.getDataURL === 'function') {
    dayImg = dayChartRef.value.getDataURL({ type: 'png', pixelRatio: 2 }) || ''
  }

  // 2. 确保分钟 K 线已加载
  if (klineData.value.length === 0) {
    await fetchKlineData()
  }
  // 3. 切到分钟K线 Tab 并等待
  activeTab.value = 'minute'
  await nextTick()
  await waitForChartResize()
  if (minuteChartRef.value && typeof minuteChartRef.value.getDataURL === 'function') {
    minuteImg = minuteChartRef.value.getDataURL({ type: 'png', pixelRatio: 2 }) || ''
  }

  return { dayImg, minuteImg }
}

// 预览 PDF 内容（按顺序上下平铺）
const handlePreviewPdf = async () => {
  pdfLoading.value = true
  try {
    const { dayImg, minuteImg } = await getBothChartImages()
    previewDayImage.value = dayImg
    previewMinuteImage.value = minuteImg
    previewDialogVisible.value = true
    activeTab.value = 'day'
  } catch (e) {
    ElMessage.error(e?.message || '预览失败')
  } finally {
    pdfLoading.value = false
  }
}

// 导出 PDF：将两个 Tab 内容按顺序上下平铺到 PDF
const handleExportPdf = async () => {
  pdfLoading.value = true
  try {
    const { dayImg, minuteImg } = await getBothChartImages()
    const title = `${currentStock.value.stockName || currentStock.value.stockCode} - 趋势`

    const doc = new jsPDF('p', 'mm', 'a4')
    const pageW = doc.internal.pageSize.getWidth()
    const pageH = doc.internal.pageSize.getHeight()
    const margin = 10
    const contentW = pageW - margin * 2
    let y = margin

    // 标题
    doc.setFontSize(14)
    doc.text(title, margin, y)
    y += 10

    const addImageToPdf = async (imgData, label, yStart) => {
      if (!imgData) return yStart
      let yy = yStart
      doc.setFontSize(11)
      doc.setTextColor(80, 80, 80)
      doc.text(label, margin, yy)
      yy += 6
      const img = new Image()
      img.src = imgData
      await new Promise((resolve, reject) => {
        img.onload = () => resolve()
        img.onerror = reject
      })
      const imgW = img.naturalWidth
      const imgH = img.naturalHeight
      const ratio = imgH / imgW
      const drawW = contentW
      const drawH = Math.min(drawW * ratio, 110)
      doc.addImage(imgData, 'PNG', margin, yy, drawW, drawH)
      return yy + drawH + 12
    }

    y = await addImageToPdf(dayImg, '日K线', y)
    if (y > pageH - 30) {
      doc.addPage()
      y = margin
    }
    y = await addImageToPdf(minuteImg, '分钟K线', y)

    doc.save(`${title.replace(/\s*-\s*趋势$/, '')}_趋势.pdf`)
    ElMessage.success('导出成功')
    activeTab.value = 'day'
  } catch (e) {
    ElMessage.error(e?.message || '导出失败')
  } finally {
    pdfLoading.value = false
  }
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

.trend-dialog-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.trend-dialog-actions {
  display: flex;
  gap: 8px;
}

.pdf-preview-content {
  max-height: 75vh;
  overflow-y: auto;
}

.pdf-section {
  margin-bottom: 24px;
}

.pdf-section:last-child {
  margin-bottom: 0;
}

.pdf-section-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.pdf-preview-img {
  width: 100%;
  height: auto;
  display: block;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.pdf-preview-placeholder {
  padding: 40px;
  text-align: center;
  color: #909399;
  background: #f5f7fa;
  border-radius: 4px;
}
</style>
