<template>
  <div class="dashboard-container">
    <el-row class="dashboard-row" :gutter="20">
      <el-col :span="24">
        <el-card class="ai-card">
          <template #header>
            <span class="ai-card-title">
              <span class="ai-card-icon"><el-icon><ChatDotRound /></el-icon></span>
              AI 助手
            </span>
          </template>
          <div class="ai-body">
            <div class="ai-messages" ref="aiMessagesRef">
              <template v-if="aiMessages.length === 0 && !aiLoading && !aiError">
                <div class="ai-empty">
                  <el-icon class="ai-empty-icon"><ChatDotRound /></el-icon>
                  <p>输入问题后，AI 将在此回复</p>
                  <span class="ai-empty-hint-pc">Enter 发送 · Shift+Enter 换行</span>
                  <span class="ai-empty-hint-mobile">点击下方发送按钮</span>
                </div>
              </template>
              <template v-else>
                <div
                  v-for="(item, index) in aiMessages"
                  :key="index"
                  :class="['ai-bubble', item.role === 'user' ? 'ai-bubble-user' : 'ai-bubble-assistant']"
                >
                  <div class="ai-bubble-avatar" v-if="item.role === 'assistant'">
                    <el-icon><ChatDotRound /></el-icon>
                  </div>
                  <div class="ai-bubble-inner">
                    <div class="ai-bubble-content">{{ item.content }}</div>
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
                <div v-if="aiLoading" class="ai-bubble ai-bubble-assistant ai-bubble-loading">
                  <div class="ai-bubble-avatar">
                    <el-icon><ChatDotRound /></el-icon>
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

    <el-row class="dashboard-row" :gutter="20">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">1,234</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon role">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">56</div>
              <div class="stat-label">角色总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon permission">
              <el-icon><Lock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">89</div>
              <div class="stat-label">权限总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon dept">
              <el-icon><OfficeBuilding /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">23</div>
              <div class="stat-label">部门总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row class="dashboard-row dashboard-row-2" :gutter="20">
      <el-col :xs="24" :sm="24" :md="12">
        <el-card class="table-card">
          <template #header>
            <span>最近登录</span>
          </template>
          <div class="table-wrap">
            <el-table :data="recentLogins" style="width: 100%">
              <el-table-column prop="username" label="用户名" min-width="80" />
              <el-table-column prop="ip" label="IP地址" min-width="100" />
              <el-table-column prop="time" label="登录时间" min-width="140" />
            </el-table>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="24" :md="12">
        <el-card>
          <template #header>
            <span>系统通知</span>
          </template>
          <el-timeline>
            <el-timeline-item
              v-for="notice in notices"
              :key="notice.id"
              :timestamp="notice.time"
            >
              {{ notice.content }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, nextTick } from 'vue'
import { User, UserFilled, Lock, OfficeBuilding, ChatDotRound, DocumentCopy, WarningFilled, Promotion } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { chat } from '@/api/ai'

// 最近登录数据
const recentLogins = ref([
  { username: 'admin', ip: '192.168.1.100', time: '2026-01-12 10:30:00' },
  { username: 'test', ip: '192.168.1.101', time: '2026-01-12 09:15:00' },
  { username: 'user1', ip: '192.168.1.102', time: '2026-01-12 08:45:00' }
])

// 系统通知数据
const notices = ref([
  { id: 1, content: '系统将于今晚22:00进行维护', time: '2026-01-12 10:00' },
  { id: 2, content: '新版本功能已上线', time: '2026-01-11 15:30' },
  { id: 3, content: '欢迎使用后台管理系统', time: '2026-01-10 09:00' }
])

// AI 助手
const aiQuestion = ref('')
const aiMessages = ref([]) // { role: 'user'|'assistant', content: string }
const aiLoading = ref(false)
const aiError = ref('')
const aiMessagesRef = ref(null)

const contextText = computed(() => {
  const logins = recentLogins.value.map(l => `${l.username} ${l.time}`).join('；')
  const noticeText = notices.value.map(n => n.content).join('；')
  return `最近登录：${logins}。系统通知：${noticeText}。`
})

function scrollToBottom() {
  nextTick(() => {
    const el = aiMessagesRef.value
    if (el) el.scrollTop = el.scrollHeight
  })
}

function handleAiSend() {
  const msg = aiQuestion.value?.trim()
  if (!msg || aiLoading.value) return
  aiError.value = ''
  aiMessages.value.push({ role: 'user', content: msg })
  aiQuestion.value = ''
  aiLoading.value = true
  scrollToBottom()
  chat({
    message: msg,
    context: contextText.value
  })
    .then(res => {
      const answer = (res.data && res.data.answer) ? res.data.answer : ''
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
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

.dashboard-row + .dashboard-row {
  margin-top: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  margin-right: 20px;
}

.stat-icon.user {
  background: var(--el-color-primary-light-5);
  color: var(--el-color-primary);
}

.stat-icon.role {
  background: var(--el-color-success-light-5);
  color: var(--el-color-success);
}

.stat-icon.permission {
  background: var(--el-color-warning-light-5);
  color: var(--el-color-warning);
}

.stat-icon.dept {
  background: var(--el-color-info-light-5);
  color: var(--el-color-info);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: var(--el-text-color-primary);
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

.ai-card :deep(.el-card__header) {
  background: var(--el-color-primary-light-5);
  color: var(--el-color-primary);
  padding: 14px 20px;
  border-radius: 4px 4px 0 0;
}

.ai-card-title {
  display: inline-flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
  font-size: 16px;
}

.ai-card-icon {
  display: inline-flex;
  align-items: center;
  font-size: 20px;
  opacity: 0.95;
}

.ai-body {
  display: flex;
  flex-direction: column;
  min-height: 280px;
}

.ai-messages {
  flex: 1;
  min-height: 200px;
  max-height: 400px;
  overflow-y: auto;
  padding: 16px;
  background: var(--el-fill-color-lighter);
  border-radius: 8px;
  margin-bottom: 12px;
}

.ai-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 180px;
  color: var(--el-text-color-placeholder);
  font-size: 14px;
}

.ai-empty-icon {
  font-size: 48px;
  margin-bottom: 12px;
  opacity: 0.5;
}

.ai-empty p {
  margin: 0 0 6px 0;
  color: var(--el-text-color-secondary);
}

.ai-empty span {
  font-size: 12px;
  color: var(--el-text-color-placeholder);
}

.ai-empty-hint-mobile {
  display: none;
}

.ai-empty-hint-pc {
  display: inline;
}

.ai-bubble {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  margin-bottom: 14px;
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
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--el-color-primary-light-5);
  color: var(--el-color-primary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
}

.ai-bubble-avatar.user {
  background: var(--el-color-primary-light-3);
  color: var(--el-color-primary);
}

.ai-bubble-inner {
  flex: 1;
  min-width: 0;
}

.ai-bubble-content {
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  white-space: pre-wrap;
  word-break: break-word;
}

.ai-bubble-user .ai-bubble-content {
  background: var(--el-color-primary-light-5);
  color: var(--el-color-primary);
  border-bottom-right-radius: 4px;
}

.ai-bubble-assistant .ai-bubble-content {
  background: var(--el-bg-color);
  color: var(--el-text-color-primary);
  border: 1px solid var(--el-border-color-lighter);
  border-bottom-left-radius: 4px;
}

.ai-bubble-actions {
  margin-top: 6px;
  padding-left: 4px;
}

.ai-bubble-loading .ai-bubble-content {
  padding: 14px 18px;
}

.ai-typing {
  display: flex;
  gap: 4px;
  align-items: center;
}

.ai-typing span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--el-text-color-placeholder);
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

.ai-error-bar .el-icon {
  flex-shrink: 0;
  font-size: 18px;
}

.ai-input-row {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.ai-input-row .el-input {
  flex: 1;
}

.ai-send-btn {
  flex-shrink: 0;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 10px 18px;
}

/* ========== 手机端适配 ========== */
@media (max-width: 768px) {
  .dashboard-container {
    padding: 12px;
  }

  .dashboard-row {
    margin-left: -8px !important;
    margin-right: -8px !important;
  }

  .dashboard-row > .el-col {
    padding-left: 8px !important;
    padding-right: 8px !important;
  }

  .dashboard-row + .dashboard-row {
    margin-top: 12px !important;
  }

  .stat-card {
    margin-bottom: 0;
  }

  .stat-content {
    padding: 4px 0;
  }

  .stat-icon {
    width: 48px;
    height: 48px;
    font-size: 24px;
    margin-right: 14px;
  }

  .stat-value {
    font-size: 22px;
  }

  .stat-label {
    font-size: 13px;
  }

  .table-card :deep(.el-card__body) {
    padding: 10px;
  }

  .table-wrap {
    overflow-x: auto;
    -webkit-overflow-scrolling: touch;
    margin: 0 -10px;
  }

  .table-wrap .el-table {
    min-width: 320px;
  }

  .ai-card :deep(.el-card__header) {
    padding: 12px 14px;
  }

  .ai-card-title {
    font-size: 15px;
  }

  .ai-card-icon {
    font-size: 18px;
  }

  .ai-body {
    min-height: 240px;
  }

  .ai-messages {
    min-height: 160px;
    max-height: min(400px, 50vh);
    padding: 12px;
    margin-bottom: 10px;
  }

  .ai-empty {
    min-height: 140px;
  }

  .ai-empty-icon {
    font-size: 40px;
  }

  .ai-empty-hint-pc {
    display: none;
  }

  .ai-empty-hint-mobile {
    display: inline;
  }

  .ai-bubble {
    gap: 8px;
    margin-bottom: 12px;
    max-width: 92%;
  }

  .ai-bubble-avatar {
    width: 32px;
    height: 32px;
    font-size: 16px;
  }

  .ai-bubble-content {
    padding: 8px 12px;
    font-size: 13px;
  }

  .ai-bubble-actions .el-button {
    min-height: 36px;
    padding: 4px 8px;
  }

  .ai-error-bar {
    padding: 8px 12px;
    font-size: 12px;
    margin-bottom: 10px;
  }

  .ai-input-row {
    flex-direction: row;
    gap: 10px;
    align-items: flex-end;
  }

  .ai-input-row .el-input {
    min-width: 0;
  }

  .ai-send-btn {
    min-height: 44px;
    padding: 10px 14px;
    flex-shrink: 0;
  }
}

@media (max-width: 480px) {
  .dashboard-container {
    padding: 10px;
  }

  .dashboard-row > .el-col {
    padding-left: 6px !important;
    padding-right: 6px !important;
  }

  .stat-icon {
    width: 44px;
    height: 44px;
    font-size: 22px;
    margin-right: 12px;
  }

  .stat-value {
    font-size: 20px;
  }

  .ai-messages {
    min-height: 140px;
    max-height: min(360px, 45vh);
    padding: 10px;
  }

  .ai-bubble {
    max-width: 95%;
  }

  .ai-bubble-avatar {
    width: 28px;
    height: 28px;
    font-size: 14px;
  }

  .ai-bubble-content {
    padding: 8px 10px;
    font-size: 13px;
  }

  /* 小屏下输入区可改为上下排列，发送按钮更易点 */
  .ai-input-row {
    flex-direction: column;
    align-items: stretch;
  }

  .ai-send-btn {
    width: 100%;
    min-height: 44px;
    justify-content: center;
  }
}
</style>
