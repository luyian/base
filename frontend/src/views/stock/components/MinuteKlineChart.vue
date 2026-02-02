<template>
  <div ref="chartRef" class="minute-kline-chart"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  stockName: {
    type: String,
    default: ''
  },
  kType: {
    type: Number,
    default: 1
  },
  hasMore: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['load-more'])

const chartRef = ref(null)
let chartInstance = null

// 记录当前 dataZoom 范围
let currentZoomStart = 50
let currentZoomEnd = 100

// 计算均线数据
const calculateMA = (data, dayCount) => {
  const result = []
  for (let i = 0; i < data.length; i++) {
    if (i < dayCount - 1) {
      result.push('-')
      continue
    }
    let sum = 0
    for (let j = 0; j < dayCount; j++) {
      sum += data[i - j][1]
    }
    result.push((sum / dayCount).toFixed(2))
  }
  return result
}

// 初始化图表
const initChart = () => {
  if (!chartRef.value) {
    return
  }

  chartInstance = echarts.init(chartRef.value)
  bindEvents()
  renderChart()
}

// 绑定事件
const bindEvents = () => {
  if (!chartInstance) {
    return
  }

  // 监听 dataZoom 事件，当滑动到最左边时触发加载更多
  chartInstance.on('datazoom', (params) => {
    // 更新当前缩放范围
    if (params.batch) {
      currentZoomStart = params.batch[0].start
      currentZoomEnd = params.batch[0].end
    } else if (params.start !== undefined) {
      currentZoomStart = params.start
      currentZoomEnd = params.end
    }

    if (!props.hasMore) {
      return
    }

    // 当滑动到最左边（start <= 5%）时触发加载更多
    if (currentZoomStart <= 5) {
      emit('load-more')
    }
  })
}

// 渲染图表
const renderChart = () => {
  if (!chartInstance || !props.data || props.data.length === 0) {
    if (chartInstance) {
      chartInstance.clear()
    }
    return
  }

  // 重置缩放范围为初始值
  currentZoomStart = 50
  currentZoomEnd = 100

  // 处理数据
  const categoryData = []
  const values = []
  const volumes = []

  props.data.forEach(item => {
    categoryData.push(item.tradeTime)
    // K线数据格式：[开盘价, 收盘价, 最低价, 最高价]
    values.push([item.openPrice, item.closePrice, item.lowPrice, item.highPrice])
    volumes.push([categoryData.length - 1, item.volume, item.openPrice > item.closePrice ? 1 : -1])
  })

  const kTypeText = props.kType === 1 ? '1分钟' : '5分钟'
  const title = props.stockName ? `${props.stockName} ${kTypeText}K线图` : `${kTypeText}K线图`

  const option = {
    title: {
      text: title,
      left: 'center'
    },
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross'
      },
      formatter: (params) => {
        const kline = params.find(p => p.seriesName === 'K线')
        if (!kline) {
          return ''
        }
        const data = kline.data
        return `
          <div style="font-weight: bold">${kline.axisValue}</div>
          <div>开盘: ${data[1]}</div>
          <div>收盘: ${data[2]}</div>
          <div>最低: ${data[3]}</div>
          <div>最高: ${data[4]}</div>
        `
      }
    },
    legend: {
      data: ['K线', 'MA5', 'MA10', 'MA20'],
      top: 30
    },
    grid: [
      {
        left: '10%',
        right: '10%',
        top: 80,
        height: '50%'
      },
      {
        left: '10%',
        right: '10%',
        top: '70%',
        height: '15%'
      }
    ],
    xAxis: [
      {
        type: 'category',
        data: categoryData,
        axisLine: { onZero: false },
        splitLine: { show: false },
        axisLabel: {
          rotate: 45,
          fontSize: 10,
          interval: (index) => {
            // 计算当前可视范围内的数据点数量
            const totalPoints = categoryData.length
            const visiblePercent = (currentZoomEnd - currentZoomStart) / 100
            const visiblePoints = Math.floor(totalPoints * visiblePercent)

            // 目标显示约 10 个标签
            const targetLabels = 10
            const step = Math.max(1, Math.floor(visiblePoints / targetLabels))

            // 计算当前索引在可视范围内的相对位置
            const startIndex = Math.floor(totalPoints * currentZoomStart / 100)
            const relativeIndex = index - startIndex

            // 每隔 step 个数据点显示一个标签
            return relativeIndex >= 0 && relativeIndex % step === 0
          },
          formatter: (value, index) => {
            // value 格式: "2026-02-02 10:30"
            const parts = value.split(' ')
            if (parts.length !== 2) {
              return value
            }
            const [date, time] = parts
            // 第一个显示的标签，显示日期和时间
            if (index === 0) {
              return `${date.substring(5)}\n${time}`
            }
            // 检查与前一个数据的日期是否相同
            const prevValue = categoryData[index - 1] || ''
            const prevDate = prevValue.split(' ')[0]
            if (date !== prevDate) {
              // 日期变化，显示日期和时间
              return `${date.substring(5)}\n${time}`
            }
            // 日期相同，只显示时间
            return time
          }
        }
      },
      {
        type: 'category',
        gridIndex: 1,
        data: categoryData,
        axisLine: { onZero: false },
        axisTick: { show: false },
        splitLine: { show: false },
        axisLabel: { show: false }
      }
    ],
    yAxis: [
      {
        scale: true,
        splitArea: {
          show: true
        }
      },
      {
        scale: true,
        gridIndex: 1,
        splitNumber: 2,
        axisLabel: { show: false },
        axisLine: { show: false },
        axisTick: { show: false },
        splitLine: { show: false }
      }
    ],
    dataZoom: [
      {
        type: 'inside',
        xAxisIndex: [0, 1],
        start: 50,
        end: 100
      },
      {
        show: true,
        xAxisIndex: [0, 1],
        type: 'slider',
        top: '90%',
        start: 50,
        end: 100
      }
    ],
    series: [
      {
        name: 'K线',
        type: 'candlestick',
        data: values,
        itemStyle: {
          color: '#ef5350',
          color0: '#26a69a',
          borderColor: '#ef5350',
          borderColor0: '#26a69a'
        }
      },
      {
        name: 'MA5',
        type: 'line',
        data: calculateMA(values, 5),
        smooth: true,
        lineStyle: {
          opacity: 0.5,
          width: 1
        }
      },
      {
        name: 'MA10',
        type: 'line',
        data: calculateMA(values, 10),
        smooth: true,
        lineStyle: {
          opacity: 0.5,
          width: 1
        }
      },
      {
        name: 'MA20',
        type: 'line',
        data: calculateMA(values, 20),
        smooth: true,
        lineStyle: {
          opacity: 0.5,
          width: 1
        }
      },
      {
        name: '成交量',
        type: 'bar',
        xAxisIndex: 1,
        yAxisIndex: 1,
        data: volumes,
        itemStyle: {
          color: (params) => {
            return params.data[2] === 1 ? '#ef5350' : '#26a69a'
          }
        }
      }
    ]
  }

  chartInstance.setOption(option)
}

// 监听数据变化
watch(() => props.data, () => {
  renderChart()
}, { deep: true })

// 监听 kType 变化
watch(() => props.kType, () => {
  renderChart()
})

// 监听窗口大小变化
const handleResize = () => {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
  // 延迟调用 resize，确保弹窗容器尺寸已确定
  nextTick(() => {
    setTimeout(() => {
      if (chartInstance) {
        chartInstance.resize()
      }
    }, 100)
  })
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.minute-kline-chart {
  width: 100%;
  height: 500px;
}
</style>
