<template>
  <div class="dashboard-container">
    <!-- 第一行：AI 助手 -->
    <el-row :gutter="20" class="dashboard-row">
      <el-col :span="24">
        <el-card class="ai-card" shadow="hover">
          <template #header>
            <div class="ai-card-header">
              <span class="ai-card-title">
                <el-icon class="ai-card-icon"><ChatDotRound /></el-icon>
                AI 智能助手
              </span>
              <el-tag type="success" size="small" effect="dark">在线</el-tag>
            </div>
          </template>
          <div class="ai-body">
            <div class="ai-messages" ref="aiMessagesRef">
              <template v-if="aiMessages.length === 0 && !aiLoading && !aiError">
                <div class="ai-empty">
                  <div class="ai-empty-illustration">
                    <el-icon class="ai-robot"><Service /></el-icon>
                  </div>
                  <p>你好！我是 AI 助手，有什么可以帮你的吗？</p>
                  <div class="ai-quick-questions">
                    <el-tag 
                      v-for="q in quickQuestions" 
                      :key="q"
                      class="quick-tag"
                      effect="plain"
                      @click="handleQuickQuestion(q)"
                    >
                      {{ q }}
                    </el-tag>
                  </div>
                </div>
              </template>
              <template v-else>
                <TransitionGroup name="message" tag="div">
                  <div
                    v-for="(item, index) in aiMessages"
                    :key="index"
                    :class="['ai-bubble', item.role === 'user' ? 'ai-bubble-user' : 'ai-bubble-assistant']"
                  >
                    <div class="ai-bubble-avatar" v-if="item.role === 'assistant'">
                      <el-icon><Service /></el-icon>
                    </div>
                    <div class="ai-bubble-inner">
                      <div class="ai-bubble-content" v-html="formatContent(item.content)"></div>
                      <div v-if="item.role === 'assistant'" class="ai-bubble-actions">
                        <el-button type="primary" link size="small" @click="copyContent(item.content)">
                          <el-icon><DocumentCopy /></el-icon>
                          复制
                        </el-button>
                      </div>
                    </div>
                    <div class="ai-bubble-avatar user" v-if="item.role === 'user'">
                      <el-icon><User /></el-icon>
                    </div>
                  </div>
                </TransitionGroup>
                <div v-if="aiLoading" class="ai-bubble ai-bubble-assistant ai-bubble-loading">
                  <div class="ai-bubble-avatar">
                    <el-icon><Service /></el-icon>
                  </div>
                  <div class="ai-bubble-inner">
                    <div class="ai-typing">
                      <span></span><span></span><span></span>
                    </div>
                  </div>
                </div>
              </template>
            </div>
            <div v-if="aiError" class="ai-error-bar">
              <el-icon><WarningFilled /></el-icon>
              {{ aiError }}
            </div>
            <div class="ai-input-row">
              <el-input
                v-model="aiQuestion"
                type="textarea"
                :rows="2"
                :autosize="{ minRows: 2, maxRows: 5 }"
                placeholder="输入问题，Enter 发送，Shift+Enter 换行"
                :disabled="aiLoading"
                @keydown.enter.exact.prevent="handleAiSend"
              />
              <el-button
                class="ai-send-btn"
                type="primary"
                :loading="aiLoading"
                :disabled="!aiQuestion.trim()"
                @click="handleAiSend"
              >
                <el-icon><Promotion /></el-icon>
                发送
              </el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 第二行：服务器状态卡片 -->
    <el-row :gutter="20" class="dashboard-row">
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
      <el-col :xs="24" :lg="12">
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
import { ref, onMounted, onUnmounted, nextTick, markRaw } from 'vue'
import { 
  User, UserFilled, Lock, OfficeBuilding, ChatDotRound, DocumentCopy, 
  WarningFilled, Promotion, Service, TrendCharts, PieChart, Clock, 
  Refresh, Bell, InfoFilled, SuccessFilled, Cpu, Odometer, Monitor, FolderOpened,
  DataBoard, Folder
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import { chat } from '@/api/ai'
import { getServerInfo } from '@/api/monitor'
import { pageLoginLogs } from '@/api/loginLog'
import { getLatestNotices, getUnreadCount } from '@/api/notice'

// AI 助手
const quickQuestions = [
  '查看服务器状态',
  '今日登录统计',
  '系统有什么新功能',
  '帮我分析数据'
]

const aiQuestion = ref('')
const aiMessages = ref([])
const aiLoading = ref(false)
const aiError = ref('')
const aiMessagesRef = ref(null)

function formatContent(content) {
  if (!content) return ''
  return content.replace(/\n/g, '<br>')
}

function scrollToBottom() {
  nextTick(() => {
    const el = aiMessagesRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

function handleQuickQuestion(q) {
  aiQuestion.value = q
  handleAiSend()
}

function handleAiSend() {
  const msg = aiQuestion.value?.trim()
  if (!msg || aiLoading.value) return
  aiError.value = ''
  aiMessages.value.push({ role: 'user', content: msg })
  aiQuestion.value = ''
  aiLoading.value = true
  scrollToBottom()
  
  const contextInfo = `
    服务器状态: CPU使用率${serverStats.value[0]?.value || 'N/A'}, 
    内存使用率${serverStats.value[1]?.value || 'N/A'}, 
    磁盘使用率${serverStats.value[2]?.value || 'N/A'}
  `
  
  chat({ message: msg, context: contextInfo })
    .then(res => {
      const answer = (res.data && res.data.answer) ? res.data.answer : '暂无回复'
      aiMessages.value.push({ role: 'assistant', content: answer })
      scrollToBottom()
    })
    .catch(err => {
      const errMsg = err.response?.data?.message || err.message || 'AI 服务暂时不可用'
      aiError.value = errMsg
    })
    .finally(() => {
      aiLoading.value = false
    })
}

function copyContent(text) {
  if (!text) return
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

// 服务器状态
const serverStats = ref([
  { label: 'CPU 使用率', value: '0%', percentage: 0, color: '#67c23a', bgColor: 'rgba(103, 194, 58, 0.1)', icon: markRaw(Cpu) },
  { label: '内存使用率', value: '0%', percentage: 0, color: '#409eff', bgColor: 'rgba(64, 158, 255, 0.1)', icon: markRaw(DataBoard) },
  { label: '磁盘使用率', value: '0%', percentage: 0, color: '#e6a23c', bgColor: 'rgba(230, 162, 60, 0.1)', icon: markRaw(Folder) },
  { label: '运行时间', value: '0天', percentage: 0, color: '#909399', bgColor: 'rgba(144, 147, 153, 0.1)', icon: markRaw(Monitor) }
])

async function loadServerStats() {
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
    console.error('获取服务器信息失败', e)
  }
}

function getProgressColor(percentage) {
  const p = parseFloat(percentage)
  if (p < 60) return '#67c23a'
  if (p < 80) return '#e6a23c'
  return '#f56c6c'
}

// 登录日志
const recentLogins = ref([])
const loginLoading = ref(false)

async function loadLoginLogs() {
  loginLoading.value = true
  try {
    const res = await pageLoginLogs({ page: 1, pageSize: 10 })
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
  
  refreshTimer = setInterval(() => {
    loadServerStats()
  }, 30000)
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

/* AI 助手样式 */
.ai-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ai-card-title {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 16px;
}

.ai-card-icon {
  font-size: 20px;
  color: var(--el-color-primary);
}

.ai-body {
  display: flex;
  flex-direction: column;
  min-height: 300px;
}

.ai-messages {
  flex: 1;
  min-height: 200px;
  max-height: 350px;
  overflow-y: auto;
  padding: 16px;
  background: linear-gradient(180deg, #f5f7fa 0%, #fff 100%);
  border-radius: 8px;
  margin-bottom: 12px;
}

.ai-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  padding: 20px;
}

.ai-empty-illustration {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.ai-robot {
  font-size: 40px;
  color: white;
}

.ai-empty p {
  margin: 0 0 16px 0;
  color: var(--el-text-color-secondary);
  font-size: 15px;
}

.ai-quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.quick-tag {
  cursor: pointer;
  transition: all 0.3s;
}

.quick-tag:hover {
  transform: scale(1.05);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.ai-bubble {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-bottom: 16px;
  max-width: 90%;
}

.ai-bubble-user {
  flex-direction: row-reverse;
  margin-left: auto;
}

.ai-bubble-assistant {
  flex-direction: row;
}

.ai-bubble-avatar {
  flex-shrink: 0;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.ai-bubble-avatar.user {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
}

.ai-bubble-inner {
  flex: 1;
  min-width: 0;
}

.ai-bubble-content {
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.ai-bubble-user .ai-bubble-content {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.ai-bubble-assistant .ai-bubble-content {
  background: var(--el-bg-color);
  color: var(--el-text-color-primary);
  border: 1px solid var(--el-border-color-lighter);
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.ai-bubble-actions {
  margin-top: 8px;
  padding-left: 4px;
}

.ai-typing {
  display: flex;
  gap: 5px;
  align-items: center;
  padding: 8px 0;
}

.ai-typing span {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: var(--el-color-primary);
  animation: ai-typing 1.4s ease-in-out infinite both;
}

.ai-typing span:nth-child(2) { animation-delay: 0.2s; }
.ai-typing span:nth-child(3) { animation-delay: 0.4s; }

@keyframes ai-typing {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

.ai-error-bar {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 14px;
  margin-bottom: 12px;
  background: var(--el-color-danger-light-9);
  color: var(--el-color-danger);
  border-radius: 8px;
  font-size: 13px;
}

.ai-input-row {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.ai-send-btn {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.ai-send-btn:hover {
  opacity: 0.9;
}

/* 消息动画 */
.message-enter-active {
  transition: all 0.3s ease-out;
}

.message-enter-from {
  opacity: 0;
  transform: translateY(20px);
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

  .ai-body {
    min-height: 260px;
  }

  .ai-messages {
    max-height: 280px;
  }

  .ai-empty-illustration {
    width: 60px;
    height: 60px;
  }

  .ai-robot {
    font-size: 30px;
  }

  .chart-container {
    height: 220px;
  }
}
</style>