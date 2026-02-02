<template>
  <div ref="chartRef" class="kline-chart"></div>
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
  }
})

const chartRef = ref(null)
let chartInstance = null

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
  renderChart()
}

// 渲染图表
const renderChart = () => {
  if (!chartInstance || !props.data || props.data.length === 0) {
    return
  }

  // 处理数据
  const categoryData = []
  const values = []
  const volumes = []

  props.data.forEach(item => {
    categoryData.push(item.tradeDate)
    // K线数据格式：[开盘价, 收盘价, 最低价, 最高价]
    values.push([item.openPrice, item.closePrice, item.lowPrice, item.highPrice])
    volumes.push([categoryData.length - 1, item.volume, item.openPrice > item.closePrice ? 1 : -1])
  })

  const option = {
    title: {
      text: props.stockName ? `${props.stockName} K线图` : 'K线图',
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
        boundaryGap: false,
        axisLine: { onZero: false },
        splitLine: { show: false },
        min: 'dataMin',
        max: 'dataMax'
      },
      {
        type: 'category',
        gridIndex: 1,
        data: categoryData,
        boundaryGap: false,
        axisLine: { onZero: false },
        axisTick: { show: false },
        splitLine: { show: false },
        axisLabel: { show: false },
        min: 'dataMin',
        max: 'dataMax'
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
  // 数据变化后延迟调用 resize，确保图表正确显示
  nextTick(() => {
    setTimeout(() => {
      if (chartInstance) {
        chartInstance.resize()
      }
    }, 100)
  })
}, { deep: true })

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
.kline-chart {
  width: 100%;
  height: 500px;
}
</style>
