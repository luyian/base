<template>
  <div class="dashboard-container">
    <!-- 第一行：AI 助手入口 -->
    <el-row :gutter="20" class="dashboard-row">
      <el-col :span="24">
        <el-card class="ai-entry-card" shadow="hover" @click="openAiChat">
          <div class="ai-entry-content">
            <div class="ai-entry-left">
              <div class="ai-entry-icon">
                <el-icon :size="32"><Service /></el-icon>
              </div>
              <div class="ai-entry-info">
                <h3 class="ai-entry-title">AI 智能助手</h3>
                <p class="ai-entry-desc">智能问答、数据分析、股票查询，随时为您服务</p>
              </div>
            </div>
            <div class="ai-entry-right">
              <div class="ai-entry-features">
                <el-tag effect="plain" size="small">A股数据</el-tag>
                <el-tag effect="plain" size="small">智能问答</el-tag>
                <el-tag effect="plain" size="small">数据分析</el-tag>
              </div>
              <el-button type="primary" class="ai-entry-btn">
                开始对话
                <el-icon class="el-icon--right"><ArrowRight /></el-icon>
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行：服务器状态卡片（需要 monitor:server:view 权限） -->
    <el-row :gutter="20" class="dashboard-row" v-if="hasMonitorPermission">
      <el-col :xs="12" :sm="6" v-for="stat in serverStats" :key="stat.label">
        <el-card class="stat-card" shadow="hover" :body-style="{ padding: '0' }">
          <div class="stat-card-inner">
            <div class="stat-icon" :style="{ background: stat.bgColor }">
              <el-icon :style="{ color: stat.color }"><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
            <div class="stat-progress">
              <el-progress 
                :percentage="stat.percentage" 
                :color="stat.color"
                :show-text="false"
                :stroke-width="6"
              />
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第三行：图表区域 -->
    <el-row :gutter="20" class="dashboard-row">
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>
                <el-icon><TrendCharts /></el-icon>
                登录趋势
              </span>
              <el-radio-group v-model="loginChartRange" size="small" @change="loadLoginTrend">
                <el-radio-button label="7">近7天</el-radio-button>
                <el-radio-button label="30">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="loginChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card class="chart-card" shadow="hover">
          <template #header>
            <div class="chart-header">
              <span>
                <el-icon><PieChart /></el-icon>
                操作类型分布
              </span>
            </div>
          </template>
          <div ref="operationChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第四行：日志和通知 -->
    <el-row :gutter="20" class="dashboard-row">
      <el-col :xs="24" :lg="12" v-if="hasLoginLogPermission">
        <el-card class="log-card" shadow="hover">
          <template #header>
            <div class="table-header">
              <span>
                <el-icon><Clock /></el-icon>
                最近登录
              </span>
              <el-button type="primary" link @click="loadLoginLogs">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          <el-table 
            :data="recentLogins" 
            style="width: 100%"
            v-loading="loginLoading"
            size="small"
          >
            <el-table-column prop="username" label="用户" min-width="80">
              <template #default="{ row }">
                <el-icon><User /></el-icon>
                {{ row.username }}
              </template>
            </el-table-column>
            <el-table-column prop="ip" label="IP地址" min-width="100" />
            <el-table-column prop="location" label="位置" min-width="100" />
            <el-table-column prop="loginTime" label="登录时间" min-width="140">
              <template #default="{ row }">
                <span class="time-text">{{ row.loginTime }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card class="notice-card" shadow="hover">
          <template #header>
            <div class="table-header">
              <span>
                <el-icon><Bell /></el-icon>
                系统通知
                <el-badge :value="unreadNoticeCount" :hidden="unreadNoticeCount === 0" class="notice-badge" />
              </span>
              <el-button type="primary" link @click="loadNotices">
                <el-icon><Refresh /></el-icon>
                刷新
              </el-button>
            </div>
          </template>
          <div class="notice-list" v-loading="noticeLoading">
            <TransitionGroup name="notice-item">
              <div 
                v-for="notice in notices" 
                :key="notice.id" 
                class="notice-item"
                :class="{ 'notice-unread': !notice.read }"
              >
                <div class="notice-icon" :class="notice.type">
                  <el-icon>
                    <WarningFilled v-if="notice.type === 'warning'" />
                    <InfoFilled v-else-if="notice.type === 'info'" />
                    <SuccessFilled v-else />
                  </el-icon>
                </div>
                <div class="notice-content">
                  <div class="notice-title">{{ notice.title }}</div>
                  <div class="notice-desc">{{ notice.content }}</div>
                  <div class="notice-time">{{ notice.createTime }}</div>
                </div>
              </div>
            </TransitionGroup>
            <el-empty v-if="notices.length === 0 && !noticeLoading" description="暂无通知" :image-size="60" />
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick, markRaw, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import {
  User, UserFilled, Lock, OfficeBuilding, ChatDotRound, DocumentCopy,
  WarningFilled, Promotion, Service, TrendCharts, PieChart, Clock,
  Refresh, Bell, InfoFilled, SuccessFilled, Cpu, Odometer, Monitor, FolderOpened,
  DataBoard, Folder, ArrowRight
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { getServerInfo } from '@/api/monitor'
import { pageLoginLogs } from '@/api/loginLog'
import { getLatestNotices, getUnreadCount } from '@/api/notice'

const router = useRouter()

// 打开 AI 对话页面
function openAiChat() {
  router.push('/ai/chat')
}

// 服务器状态
const serverStats = ref([
  { label: 'CPU 使用率', value: '0%', percentage: 0, color: '#67c23a', bgColor: 'rgba(103, 194, 58, 0.1)', icon: markRaw(Cpu) },
  { label: '内存使用率', value: '0%', percentage: 0, color: '#409eff', bgColor: 'rgba(64, 158, 255, 0.1)', icon: markRaw(DataBoard) },
  { label: '磁盘使用率', value: '0%', percentage: 0, color: '#e6a23c', bgColor: 'rgba(230, 162, 60, 0.1)', icon: markRaw(Folder) },
  { label: '运行时间', value: '0天', percentage: 0, color: '#909399', bgColor: 'rgba(144, 147, 153, 0.1)', icon: markRaw(Monitor) }
])

async function loadServerStats() {
  if (!hasMonitorPermission.value) return
  try {
    const res = await getServerInfo()
    if (res.code === 200 && res.data) {
      const data = res.data
      if (data.cpu) {
        serverStats.value[0].value = data.cpu.usedPercent + '%'
        serverStats.value[0].percentage = parseFloat(data.cpu.usedPercent)
        serverStats.value[0].color = getProgressColor(data.cpu.usedPercent)
      }
      if (data.memory) {
        serverStats.value[1].value = data.memory.usedPercent + '%'
        serverStats.value[1].percentage = parseFloat(data.memory.usedPercent)
        serverStats.value[1].color = getProgressColor(data.memory.usedPercent)
      }
      if (data.disks && data.disks.length > 0) {
        const mainDisk = data.disks[0]
        serverStats.value[2].value = mainDisk.usedPercent + '%'
        serverStats.value[2].percentage = parseFloat(mainDisk.usedPercent)
        serverStats.value[2].color = getProgressColor(mainDisk.usedPercent)
      }
      if (data.jvm) {
        serverStats.value[3].value = data.jvm.runTime || '0秒'
        serverStats.value[3].percentage = Math.min(parseFloat(data.jvm.usedPercent) || 0, 100)
      }
    }
  } catch (e) {
    // 权限不足时静默处理，不显示错误
    if (e?.code === 403 || e?.message?.includes('Forbidden')) {
      return
    }
    console.error('获取服务器信息失败', e)
  }
}

function getProgressColor(percentage) {
  const p = parseFloat(percentage)
  if (p < 60) return '#67c23a'
  if (p < 80) return '#e6a23c'
  return '#f56c6c'
}

// 权限判断
const userStore = useUserStore()
const hasMonitorPermission = computed(() => userStore.hasPermission('monitor:server:view'))

// 登录日志
const recentLogins = ref([])
const loginLoading = ref(false)
// 根据权限控制显示登录记录（log:login:list）
const hasLoginLogPermission = computed(() => userStore.hasPermission('log:login:list'))

async function loadLoginLogs() {
  if (!hasLoginLogPermission.value) return
  loginLoading.value = true
  try {
    const res = await pageLoginLogs({ page: 1, pageSize: 3 })
    if (res.code === 200) {
      recentLogins.value = (res.data?.records || []).map(item => ({
        username: item.username || item.createBy || '-',
        ip: item.ip || '-',
        location: item.location || '-',
        loginTime: item.createTime || '-'
      }))
    }
  } catch (e) {
    console.error('获取登录日志失败', e)
  } finally {
    loginLoading.value = false
  }
}

// 通知
const notices = ref([])
const noticeLoading = ref(false)
const unreadNoticeCount = ref(0)

async function loadNotices() {
  noticeLoading.value = true
  try {
    const [noticesRes, countRes] = await Promise.all([
      getLatestNotices(10),
      getUnreadCount()
    ])
    if (noticesRes.code === 200) {
      notices.value = (noticesRes.data || []).map(item => ({
        id: item.id,
        title: item.title || '通知',
        content: item.content || '',
        type: item.type || 'info',
        read: item.readStatus === 1,
        createTime: item.createTime || '-'
      }))
    }
    if (countRes.code === 200) {
      unreadNoticeCount.value = countRes.data || 0
    }
  } catch (e) {
    console.error('获取通知失败', e)
  } finally {
    noticeLoading.value = false
  }
}

// 图表
const loginChartRef = ref(null)
const operationChartRef = ref(null)
const loginChartRange = ref('7')
let loginChart = null
let operationChart = null

const loginTrendData = ref({
  dates: [],
  counts: []
})

const operationTypeData = ref([
  { name: '查询', value: 0 },
  { name: '新增', value: 0 },
  { name: '修改', value: 0 },
  { name: '删除', value: 0 }
])

async function loadLoginTrend() {
  const days = parseInt(loginChartRange.value)
  const dates = []
  const counts = []
  const now = new Date()
  
  for (let i = days - 1; i >= 0; i--) {
    const date = new Date(now)
    date.setDate(date.getDate() - i)
    const dateStr = date.toISOString().slice(0, 10)
    dates.push(dateStr.slice(5))
    counts.push(Math.floor(Math.random() * 50) + 10)
  }
  
  loginTrendData.value = { dates, counts }
  updateLoginChart()
}

function updateLoginChart() {
  if (!loginChart) return
  loginChart.setOption({
    tooltip: { trigger: 'axis' },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { 
      type: 'category', 
      boundaryGap: false,
      data: loginTrendData.value.dates,
      axisLine: { lineStyle: { color: '#e4e7ed' } },
      axisLabel: { color: '#909399' }
    },
    yAxis: { 
      type: 'value',
      axisLine: { lineStyle: { color: '#e4e7ed' } },
      axisLabel: { color: '#909399' },
      splitLine: { lineStyle: { color: '#f0f0f0' } }
    },
    series: [{
      name: '登录次数',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      lineStyle: { width: 3, color: '#409eff' },
      itemStyle: { color: '#409eff' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.05)' }
        ])
      },
      data: loginTrendData.value.counts
    }]
  })
}

function initOperationChart() {
  if (!operationChartRef.value) return
  operationChart = echarts.init(operationChartRef.value)
  operationChart.setOption({
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', right: 10, top: 'center', textStyle: { color: '#606266' } },
    series: [{
      name: '操作类型',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['35%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: {
        label: { show: true, fontSize: 14, fontWeight: 'bold' }
      },
      labelLine: { show: false },
      data: operationTypeData.value.map((item, index) => ({
        ...item,
        itemStyle: { color: ['#409eff', '#67c23a', '#e6a23c', '#f56c6c'][index] }
      }))
    }]
  })
}

let refreshTimer = null

onMounted(() => {
  loadServerStats()
  loadLoginLogs()
  loadNotices()
  loadLoginTrend()
  
  nextTick(() => {
    if (loginChartRef.value) {
      loginChart = echarts.init(loginChartRef.value)
      updateLoginChart()
      window.addEventListener('resize', () => loginChart?.resize())
    }
    initOperationChart()
    window.addEventListener('resize', () => operationChart?.resize())
  })
  
  // 仅有监控权限时才定时刷新服务器状态
  if (hasMonitorPermission.value) {
    refreshTimer = setInterval(() => {
      loadServerStats()
    }, 30000)
  }
})

onUnmounted(() => {
  if (refreshTimer) clearInterval(refreshTimer)
  loginChart?.dispose()
  operationChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.dashboard-row {
  margin-bottom: 20px;
}

.dashboard-row:last-child {
  margin-bottom: 0;
}

/* AI 助手入口卡片样式 */
.ai-entry-card {
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.ai-entry-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(102, 126, 234, 0.4);
}

.ai-entry-card :deep(.el-card__body) {
  padding: 24px;
}

.ai-entry-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.ai-entry-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.ai-entry-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  backdrop-filter: blur(10px);
}

.ai-entry-info {
  color: white;
}

.ai-entry-title {
  font-size: 20px;
  font-weight: 600;
  margin: 0 0 4px;
  color: white;
}

.ai-entry-desc {
  font-size: 14px;
  margin: 0;
  opacity: 0.9;
}

.ai-entry-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

.ai-entry-features {
  display: flex;
  gap: 8px;
}

.ai-entry-features .el-tag {
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  color: white;
}

.ai-entry-btn {
  background: white;
  border: none;
  color: #667eea;
  font-weight: 600;
}

.ai-entry-btn:hover {
  background: rgba(255, 255, 255, 0.9);
  color: #764ba2;
}

/* 统计卡片 */
.stat-card {
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.12);
}

.stat-card-inner {
  padding: 20px;
  position: relative;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, transparent, var(--stat-color, #409eff), transparent);
  opacity: 0;
  transition: opacity 0.3s;
}

.stat-card:hover::before {
  opacity: 1;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-bottom: 12px;
}

.stat-info {
  margin-bottom: 12px;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 13px;
  color: var(--el-text-color-secondary);
}

.stat-progress {
  margin-top: 8px;
}

/* 图表卡片 */
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-header span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

.chart-container {
  height: 280px;
  width: 100%;
}

/* 表格卡片 */
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.table-header span {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-weight: 600;
}

.time-text {
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

/* 通知卡片 */
.notice-badge {
  margin-left: 8px;
}

.notice-list {
  max-height: 320px;
  overflow-y: auto;
}

.notice-item {
  display: flex;
  gap: 12px;
  padding: 14px;
  border-radius: 8px;
  margin-bottom: 10px;
  background: var(--el-fill-color-light);
  transition: all 0.3s;
}

.notice-item:hover {
  background: var(--el-fill-color-lighter);
  transform: translateX(4px);
}

.notice-unread {
  border-left: 3px solid var(--el-color-primary);
  background: rgba(64, 158, 255, 0.05);
}

.notice-icon {
  flex-shrink: 0;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.notice-icon.warning {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.notice-icon.info {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.notice-icon.success {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.notice-content {
  flex: 1;
  min-width: 0;
}

.notice-title {
  font-weight: 600;
  font-size: 14px;
  color: var(--el-text-color-primary);
  margin-bottom: 4px;
}

.notice-desc {
  font-size: 13px;
  color: var(--el-text-color-secondary);
  margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-time {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
}

.notice-item-enter-active {
  transition: all 0.3s ease-out;
}

.notice-item-enter-from {
  opacity: 0;
  transform: translateX(-20px);
}

/* 手机端适配 */
@media (max-width: 768px) {
  .dashboard-container {
    padding: 12px;
  }

  .dashboard-row {
    margin-left: -8px !important;
    margin-right: -8px !important;
    margin-bottom: 12px;
  }

  .dashboard-row > .el-col {
    padding-left: 8px !important;
    padding-right: 8px !important;
  }

  .stat-card-inner {
    padding: 14px;
  }

  .stat-icon {
    width: 44px;
    height: 44px;
    font-size: 22px;
  }

  .stat-value {
    font-size: 20px;
  }

  .ai-entry-content {
    flex-direction: column;
    align-items: flex-start;
  }

  .ai-entry-right {
    align-items: flex-start;
    width: 100%;
  }

  .ai-entry-features {
    flex-wrap: wrap;
  }

  .chart-container {
    height: 220px;
  }
}
</style>
