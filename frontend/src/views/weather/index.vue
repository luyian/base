<template>
  <div class="weather-container">
    <!-- 顶部工具栏 -->
    <div class="weather-toolbar">
      <div class="toolbar-left">
        <h3>全国实时天气地图</h3>
        <el-tag v-if="weatherData.updateTime" type="info" size="small">
          更新时间：{{ weatherData.updateTime }}
        </el-tag>
      </div>
      <div class="toolbar-right">
        <el-radio-group v-model="displayMode" size="small" @change="updateChart">
          <el-radio-button value="temperature">气温</el-radio-button>
          <el-radio-button value="humidity">湿度</el-radio-button>
        </el-radio-group>
        <el-button type="primary" size="small" :loading="loading" @click="fetchWeather">
          <el-icon><Refresh /></el-icon>刷新
        </el-button>
      </div>
    </div>

    <!-- 地图区域 -->
    <div class="weather-content">
      <div ref="chartRef" class="weather-chart"></div>

      <!-- 城市详情面板 -->
      <transition name="slide">
        <div v-if="selectedCity" class="city-detail-panel">
          <div class="panel-header">
            <span class="city-name">{{ selectedCity.city }}</span>
            <el-icon class="close-btn" @click="selectedCity = null"><Close /></el-icon>
          </div>
          <div class="panel-body">
            <div class="detail-item">
              <span class="label">省份</span>
              <span class="value">{{ selectedCity.province }}</span>
            </div>
            <div class="detail-item">
              <span class="label">温度</span>
              <span class="value temp">{{ selectedCity.temperature }}℃</span>
            </div>
            <div class="detail-item">
              <span class="label">天气</span>
              <span class="value">{{ selectedCity.weather }}</span>
            </div>
            <div class="detail-item">
              <span class="label">湿度</span>
              <span class="value">{{ selectedCity.humidity }}%</span>
            </div>
            <div class="detail-item">
              <span class="label">风向</span>
              <span class="value">{{ selectedCity.windDirection }}</span>
            </div>
            <div class="detail-item">
              <span class="label">风力</span>
              <span class="value">{{ selectedCity.windPower }}级</span>
            </div>
            <div class="detail-item">
              <span class="label">发布时间</span>
              <span class="value">{{ selectedCity.reportTime }}</span>
            </div>
          </div>
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { Refresh, Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getRealtimeWeather } from '@/api/weather'

const chartRef = ref(null)
const loading = ref(false)
const displayMode = ref('temperature')
const selectedCity = ref(null)
const weatherData = ref({ cities: [], updateTime: '', minTemp: -10, maxTemp: 40 })

let chartInstance = null
let chinaGeoLoaded = false
// GeoJSON 中省份名到后端 province 字段的映射
const provinceNameMap = {
  '北京市': '北京', '天津市': '天津', '上海市': '上海', '重庆市': '重庆',
  '河北省': '河北', '山西省': '山西', '辽宁省': '辽宁', '吉林省': '吉林',
  '黑龙江省': '黑龙江', '江苏省': '江苏', '浙江省': '浙江', '安徽省': '安徽',
  '福建省': '福建', '江西省': '江西', '山东省': '山东', '河南省': '河南',
  '湖北省': '湖北', '湖南省': '湖南', '广东省': '广东', '海南省': '海南',
  '四川省': '四川', '贵州省': '贵州', '云南省': '云南', '陕西省': '陕西',
  '甘肃省': '甘肃', '青海省': '青海', '台湾省': '台湾',
  '内蒙古自治区': '内蒙古', '广西壮族自治区': '广西',
  '西藏自治区': '西藏', '宁夏回族自治区': '宁夏',
  '新疆维吾尔自治区': '新疆',
  '香港特别行政区': '香港', '澳门特别行政区': '澳门'
}

/**
 * 加载中国地图 GeoJSON 数据
 */
async function loadChinaGeo() {
  if (chinaGeoLoaded) {
    return
  }
  try {
    const response = await fetch('/geo/china.json')
    const geoJson = await response.json()
    echarts.registerMap('china', geoJson)
    chinaGeoLoaded = true
  } catch (e) {
    ElMessage.error('加载地图数据失败，请刷新重试')
    console.error('加载中国地图 GeoJSON 失败:', e)
  }
}

/**
 * 获取天气数据
 */
async function fetchWeather() {
  loading.value = true
  try {
    const res = await getRealtimeWeather()
    weatherData.value = res.data
    updateChart()
  } catch (e) {
    ElMessage.error('获取天气数据失败')
    console.error(e)
  } finally {
    loading.value = false
  }
}

/**
 * 按省份聚合天气数据（取该省所有城市的平均值）
 */
function aggregateByProvince() {
  const cities = weatherData.value.cities
  const provinceMap = {}

  cities.forEach(city => {
    if (city.temperature === null) {
      return
    }
    const prov = city.province
    if (!provinceMap[prov]) {
      provinceMap[prov] = { sum: 0, humSum: 0, count: 0, cities: [] }
    }
    provinceMap[prov].sum += city.temperature
    provinceMap[prov].humSum += parseInt(city.humidity || '0')
    provinceMap[prov].count++
    provinceMap[prov].cities.push(city)
  })

  return provinceMap
}

/**
 * 获取色温对应的颜色范围
 */
function getVisualMapConfig() {
  if (displayMode.value === 'temperature') {
    return {
      min: weatherData.value.minTemp || -10,
      max: weatherData.value.maxTemp || 40,
      text: ['高温', '低温'],
      inRange: {
        color: ['#313695', '#4575b4', '#74add1', '#abd9e9', '#e0f3f8',
                '#ffffbf', '#fee08b', '#fdae61', '#f46d43', '#d73027', '#a50026']
      },
      formatter: '{value}℃'
    }
  }
  return {
    min: 0,
    max: 100,
    text: ['湿润', '干燥'],
    inRange: {
      color: ['#f7fbff', '#deebf7', '#c6dbef', '#9ecae1', '#6baed6',
              '#4292c6', '#2171b5', '#08519c', '#08306b']
    },
    formatter: '{value}%'
  }
}

/**
 * 更新图表
 */
function updateChart() {
  if (!chartInstance || !weatherData.value.cities.length) {
    return
  }

  const provinceMap = aggregateByProvince()
  const isTemp = displayMode.value === 'temperature'

  // 构造 map series 数据：每个省一条记录
  const mapData = []
  Object.keys(provinceNameMap).forEach(geoName => {
    const shortName = provinceNameMap[geoName]
    const info = provinceMap[shortName]
    if (info && info.count > 0) {
      const avgValue = isTemp
        ? Math.round(info.sum / info.count)
        : Math.round(info.humSum / info.count)
      mapData.push({
        name: geoName,
        value: avgValue,
        cities: info.cities
      })
    }
  })

  const visualConfig = getVisualMapConfig()

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: (params) => {
        if (!params.data || !params.data.cities) {
          return params.name
        }
        const d = params.data
        const unit = isTemp ? '℃' : '%'
        let html = `<div style="font-weight:bold;margin-bottom:6px">${params.name}  平均${isTemp ? '气温' : '湿度'}：${d.value}${unit}</div>`
        // 列出前5个城市
        const list = d.cities.slice(0, 5)
        list.forEach(c => {
          html += `<div style="display:flex;justify-content:space-between;gap:12px">
            <span>${c.city}</span>
            <span style="font-weight:500">${isTemp ? c.temperature + '℃' : c.humidity + '%'}  ${c.weather}</span>
          </div>`
        })
        if (d.cities.length > 5) {
          html += `<div style="color:#999;margin-top:2px">...等${d.cities.length}个城市</div>`
        }
        return html
      }
    },
    visualMap: {
      type: 'continuous',
      ...visualConfig,
      calculable: true,
      left: 20,
      bottom: 20,
      itemWidth: 15,
      itemHeight: 200,
      textStyle: { color: '#333' }
    },
    series: [
      {
        name: '天气分布',
        type: 'map',
        map: 'china',
        roam: true,
        scaleLimit: { min: 0.8, max: 6 },
        zoom: 1.2,
        center: [104.5, 36],
        label: {
          show: true,
          fontSize: 10,
          color: '#333',
          formatter: (params) => {
            if (params.data && params.data.value !== undefined) {
              return `${params.data.value}°`
            }
            return ''
          }
        },
        itemStyle: {
          borderColor: '#fff',
          borderWidth: 1
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 13,
            fontWeight: 'bold',
            color: '#000'
          },
          itemStyle: {
            areaColor: undefined,
            borderColor: '#333',
            borderWidth: 2
          }
        },
        data: mapData
      }
    ]
  }

  chartInstance.setOption(option, true)
}

/**
 * 初始化图表
 */
async function initChart() {
  await loadChinaGeo()
  if (!chinaGeoLoaded) {
    return
  }

  await nextTick()
  chartInstance = echarts.init(chartRef.value)

  // 点击省份显示城市列表
  chartInstance.on('click', (params) => {
    if (params.data && params.data.cities && params.data.cities.length > 0) {
      // 显示省会城市的详情
      selectedCity.value = params.data.cities[0]
    }
  })

  // 窗口 resize
  window.addEventListener('resize', handleResize)
}

function handleResize() {
  if (chartInstance) {
    chartInstance.resize()
  }
}

onMounted(async () => {
  await initChart()
  await fetchWeather()
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped>
.weather-container {
  height: 100%;
  display: flex;
  flex-direction: column;
  padding: 16px;
  box-sizing: border-box;
}

.weather-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.toolbar-left h3 {
  margin: 0;
  font-size: 16px;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 12px;
}

.weather-content {
  flex: 1;
  position: relative;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  overflow: hidden;
  min-height: calc(100vh - 140px);
}

.weather-chart {
  width: 100%;
  height: 100%;
  min-height: calc(100vh - 140px);
}

.city-detail-panel {
  position: absolute;
  top: 12px;
  right: 12px;
  width: 240px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  z-index: 10;
  overflow: hidden;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: linear-gradient(135deg, #409eff, #67c23a);
  color: #fff;
}

.city-name {
  font-size: 16px;
  font-weight: bold;
}

.close-btn {
  cursor: pointer;
  font-size: 16px;
}

.panel-body {
  padding: 12px 16px;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px dashed #f0f0f0;
}

.detail-item:last-child {
  border-bottom: none;
}

.detail-item .label {
  color: #909399;
  font-size: 13px;
}

.detail-item .value {
  color: #303133;
  font-size: 13px;
  font-weight: 500;
}

.detail-item .value.temp {
  color: #e6a23c;
  font-size: 18px;
  font-weight: bold;
}

/* 滑入动画 */
.slide-enter-active,
.slide-leave-active {
  transition: transform 0.3s ease, opacity 0.3s ease;
}

.slide-enter-from,
.slide-leave-to {
  transform: translateX(20px);
  opacity: 0;
}
</style>
