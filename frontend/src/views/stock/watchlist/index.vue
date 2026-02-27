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
        <el-table-column prop="industryCn" label="所属行业" min-width="120" align="center" show-overflow-tooltip />
        <el-table-column prop="marketCap" label="总市值" width="130" align="right">
          <template #default="{ row }">
            {{ row.marketCap != null ? formatMarketCap(row.marketCap) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="totalShares" label="总股本" width="130" align="right">
          <template #default="{ row }">
            {{ row.totalShares != null ? formatMarketCap(row.totalShares) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="peRatio" label="市盈率" width="100" align="right">
          <template #default="{ row }">
            {{ row.peRatio != null ? row.peRatio.toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="添加时间" width="180" align="center" />
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'stock:watchlist:score'" type="warning" link :icon="Star" @click="handleScore(row)" :loading="row.scoring">打分</el-button>
            <el-button type="primary" link :icon="TrendCharts" @click="handleViewTrend(row)">趋势</el-button>
            <el-button v-permission="'stock:watchlist:delete'" type="danger" link :icon="Delete" @click="handleDelete(row)">删除</el-button>
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

    <!-- 趋势弹窗（复用公共组件） -->
    <TrendDialog
      ref="trendDialogRef"
      v-model="trendDialogVisible"
      :stock-code="currentStock.stockCode"
      :stock-name="currentStock.stockName"
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
    </TrendDialog>

    <!-- PDF 预览弹窗 -->
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
import { ref, onMounted } from 'vue'
import { Refresh, Delete, Download, TrendCharts, View, Star } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listWatchlist, deleteWatchlist, batchDeleteWatchlist, batchSyncKline, scoreWatchlistStock } from '@/api/stock'
import TrendDialog from '../components/TrendDialog.vue'
import { jsPDF } from 'jspdf'

const loading = ref(false)
const syncing = ref(false)
const tableData = ref([])
const selectedIds = ref([])

// 趋势弹窗
const trendDialogRef = ref(null)
const trendDialogVisible = ref(false)
const currentStock = ref({})

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

// 格式化市值/股本（万/亿）
const formatMarketCap = (value) => {
  if (value >= 100000000) {
    return (value / 100000000).toFixed(2) + '亿'
  } else if (value >= 10000) {
    return (value / 10000).toFixed(2) + '万'
  }
  return value.toFixed(2)
}

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

// 查看趋势（打开弹窗）
const handleViewTrend = (row) => {
  currentStock.value = row
  trendDialogVisible.value = true
}

// 预览 PDF 内容
const handlePreviewPdf = async () => {
  if (!trendDialogRef.value) return
  pdfLoading.value = true
  try {
    const { dayImg, minuteImg } = await trendDialogRef.value.getBothChartImages()
    previewDayImage.value = dayImg
    previewMinuteImage.value = minuteImg
    previewDialogVisible.value = true
  } catch (e) {
    ElMessage.error(e?.message || '预览失败')
  } finally {
    pdfLoading.value = false
  }
}

// 导出 PDF
const handleExportPdf = async () => {
  if (!trendDialogRef.value) return
  pdfLoading.value = true
  try {
    const { dayImg, minuteImg } = await trendDialogRef.value.getBothChartImages()
    const title = `${currentStock.value.stockName || currentStock.value.stockCode} - 趋势`

    const doc = new jsPDF('p', 'mm', 'a4')
    const pageW = doc.internal.pageSize.getWidth()
    const pageH = doc.internal.pageSize.getHeight()
    const margin = 10
    const contentW = pageW - margin * 2
    let y = margin

    // 使用 canvas 渲染中文文字为图片
    const renderTextToImage = (text, fontSize, color = '#000000') => {
      const canvas = document.createElement('canvas')
      const ctx = canvas.getContext('2d')
      ctx.font = `${fontSize}px Microsoft YaHei, SimHei, sans-serif`
      const metrics = ctx.measureText(text)
      const textWidth = metrics.width
      const textHeight = fontSize * 1.2
      canvas.width = textWidth + 4
      canvas.height = textHeight + 4
      ctx.fillStyle = '#ffffff'
      ctx.fillRect(0, 0, canvas.width, canvas.height)
      ctx.font = `${fontSize}px Microsoft YaHei, SimHei, sans-serif`
      ctx.fillStyle = color
      ctx.textBaseline = 'middle'
      ctx.fillText(text, 2, canvas.height / 2)
      return { dataUrl: canvas.toDataURL('image/png'), width: canvas.width, height: canvas.height }
    }

    // 添加标题
    const titleImg = renderTextToImage(title, 28)
    const titleH = 8
    const titleW = (titleImg.width / titleImg.height) * titleH
    doc.addImage(titleImg.dataUrl, 'PNG', margin, y, titleW, titleH)
    y += titleH + 6

    const addImageToPdf = async (imgData, label, yStart) => {
      if (!imgData) return yStart
      let yy = yStart
      const labelImg = renderTextToImage(label, 22, '#505050')
      const labelH = 5
      const labelW = (labelImg.width / labelImg.height) * labelH
      doc.addImage(labelImg.dataUrl, 'PNG', margin, yy, labelW, labelH)
      yy += labelH + 3
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

// 对自选股票打分
const handleScore = async (row) => {
  try {
    row.scoring = true
    const scoreDate = new Date().toISOString().split('T')[0]
    await scoreWatchlistStock(row.stockCode, scoreDate)
    ElMessage.success(`${row.stockName || row.stockCode} 打分成功`)
  } catch (error) {
    ElMessage.error(error.message || '打分失败')
  } finally {
    row.scoring = false
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
