<template>
  <el-dialog
    v-model="visible"
    :title="`${stockName || stockCode} - 趋势`"
    width="90%"
    top="5vh"
    destroy-on-close
    @close="handleClose"
  >
    <template v-if="$slots.header" #header>
      <slot name="header"></slot>
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
          :stock-name="stockName"
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
          :stock-name="stockName"
          :k-type="klineType"
          :has-more="hasMoreKline"
          @load-more="loadMoreKline"
        />
      </el-tab-pane>
    </el-tabs>
  </el-dialog>
</template>

<script setup>
import { ref, watch, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import { getKlineData, getMinuteKline } from '@/api/stock'
import KlineChart from './KlineChart.vue'
import MinuteKlineChart from './MinuteKlineChart.vue'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  stockCode: {
    type: String,
    default: ''
  },
  stockName: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue'])

const visible = ref(false)
const activeTab = ref('day')

// 图表 ref
const dayChartRef = ref(null)
const minuteChartRef = ref(null)

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

// 监听 modelValue 变化
watch(() => props.modelValue, (val) => {
  visible.value = val
  if (val && props.stockCode) {
    resetData()
    fetchDayKlineData()
  }
})

// 监听 visible 变化，同步到父组件
watch(visible, (val) => {
  emit('update:modelValue', val)
})

/**
 * 重置数据
 */
const resetData = () => {
  activeTab.value = 'day'
  dayKlineData.value = []
  klineData.value = []
  hasMoreKline.value = false
  earliestTimestamp.value = null
  dayKlineDateRange.value = 'month'
  klineType.value = 1
}

/**
 * 关闭弹窗
 */
const handleClose = () => {
  visible.value = false
}

/**
 * 日期格式化
 */
const formatDateStr = (date) => {
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  return `${year}-${month}-${day}`
}

/**
 * 获取日K线日期范围
 */
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
  return { startDate: formatDateStr(startDate), endDate: formatDateStr(endDate) }
}

/**
 * 获取日K线数据
 */
const fetchDayKlineData = async () => {
  if (!props.stockCode) return
  dayKlineLoading.value = true
  try {
    const { startDate, endDate } = getDayKlineDateRange()
    const res = await getKlineData(props.stockCode, startDate, endDate)
    dayKlineData.value = res.data || []
  } catch (error) {
    ElMessage.error(error.message || '获取日K线数据失败')
  } finally {
    dayKlineLoading.value = false
  }
}

/**
 * 日K线日期范围切换
 */
const handleDayKlineDateChange = () => {
  fetchDayKlineData()
}

/**
 * 刷新日K线
 */
const refreshDayKline = () => {
  fetchDayKlineData()
}

/**
 * Tab 切换处理
 */
const handleTabChange = (tabName) => {
  if (tabName === 'day' && dayKlineData.value.length === 0) {
    fetchDayKlineData()
  } else if (tabName === 'minute' && klineData.value.length === 0) {
    fetchKlineDataMinute()
  }
}

/**
 * 获取分钟K线数据
 */
const fetchKlineDataMinute = async (et = null) => {
  if (!props.stockCode) return
  klineLoading.value = true
  try {
    const res = await getMinuteKline(props.stockCode, klineType.value, et, 100)
    if (res.data) {
      if (et) {
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

/**
 * K线类型切换
 */
const handleKlineTypeChange = () => {
  klineData.value = []
  hasMoreKline.value = false
  earliestTimestamp.value = null
  fetchKlineDataMinute()
}

/**
 * 刷新分钟K线
 */
const refreshKline = () => {
  klineData.value = []
  hasMoreKline.value = false
  earliestTimestamp.value = null
  fetchKlineDataMinute()
}

/**
 * 加载更多历史数据
 */
const loadMoreKline = () => {
  if (loadingMore.value || !hasMoreKline.value || !earliestTimestamp.value) {
    return
  }
  loadingMore.value = true
  fetchKlineDataMinute(earliestTimestamp.value)
}

/**
 * 等待图表渲染
 */
const waitForChartResize = (ms = 400) => new Promise(resolve => setTimeout(resolve, ms))

/**
 * 获取两张图表的 Base64 图片（供导出 PDF 使用）
 */
const getBothChartImages = async () => {
  let dayImg = ''
  let minuteImg = ''

  // 切到日K线 Tab 并等待
  activeTab.value = 'day'
  await nextTick()
  await waitForChartResize()
  if (dayChartRef.value && typeof dayChartRef.value.getDataURL === 'function') {
    dayImg = dayChartRef.value.getDataURL({ type: 'png', pixelRatio: 2 }) || ''
  }

  // 确保分钟 K 线已加载
  if (klineData.value.length === 0) {
    await fetchKlineDataMinute()
  }
  // 切到分钟K线 Tab 并等待
  activeTab.value = 'minute'
  await nextTick()
  await waitForChartResize()
  if (minuteChartRef.value && typeof minuteChartRef.value.getDataURL === 'function') {
    minuteImg = minuteChartRef.value.getDataURL({ type: 'png', pixelRatio: 2 }) || ''
  }

  return { dayImg, minuteImg }
}

// 暴露方法给父组件
defineExpose({
  getBothChartImages,
  resetData
})
</script>

<style scoped>
.kline-toolbar {
  margin-bottom: 16px;
  display: flex;
  align-items: center;
}
</style>
