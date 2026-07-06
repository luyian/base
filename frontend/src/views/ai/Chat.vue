<template>
  <div class="ai-chat-page">
    <!-- 顶部导航栏 -->
    <header class="chat-header">
      <div class="header-left">
        <el-icon class="header-icon"><ChatDotRound /></el-icon>
        <span class="header-title">AI 智能助手</span>
        <el-tag type="success" effect="plain" size="small" class="status-tag">
          <span class="status-dot"></span>
          在线
        </el-tag>
      </div>
      <div class="header-right">
        <el-tooltip content="新对话" placement="bottom">
          <el-button :icon="Plus" circle @click="startNewChat" />
        </el-tooltip>
        <el-tooltip content="清空历史" placement="bottom">
          <el-button :icon="Delete" circle @click="clearChat" />
        </el-tooltip>
        <el-tooltip content="返回首页" placement="bottom">
          <el-button :icon="HomeFilled" circle @click="goHome" />
        </el-tooltip>
      </div>
    </header>

    <!-- 主体区域 -->
    <main class="chat-main">
      <!-- 对话列表 -->
      <div class="chat-messages" ref="messagesRef">
        <!-- 空状态 -->
        <div v-if="messages.length === 0 && !loading" class="empty-state">
          <div class="empty-logo">
            <el-icon :size="48"><Service /></el-icon>
          </div>
          <h2 class="empty-title">AI 智能助手</h2>
          <p class="empty-desc">有什么我可以帮助你的吗？</p>
          <div class="quick-actions">
            <div
              v-for="item in quickActions"
              :key="item.text"
              class="quick-action-card"
              @click="sendMessage(item.text)"
            >
              <el-icon class="quick-action-icon" :style="{ color: item.color }">
                <component :is="item.icon" />
              </el-icon>
              <span class="quick-action-text">{{ item.text }}</span>
            </div>
          </div>
        </div>

        <!-- 消息列表 -->
        <template v-else>
          <div
            v-for="(msg, index) in messages"
            :key="index"
            :class="['message-item', msg.role]"
          >
            <!-- AI 头像 -->
            <div v-if="msg.role === 'assistant'" class="message-avatar ai">
              <el-icon :size="20"><Service /></el-icon>
            </div>

            <!-- 消息内容 -->
            <div class="message-content">
              <div v-if="msg.role === 'user'" class="message-bubble user-bubble">
                <div v-html="formatUserMessage(msg.content)"></div>
              </div>
              <div v-else class="message-bubble ai-bubble">
                <MdViewer :content="getDisplayContent(msg)" class="ai-md-content" />
                <div v-if="getImageContext(msg)" class="ai-image-card">
                  <div class="ai-image-card__header">
                    <span class="ai-image-card__title">
                      <el-icon><Picture /></el-icon>
                      AI 生成图片
                    </span>
                    <el-button
                      type="primary"
                      link
                      size="small"
                      @click="reloadImage(msg)"
                    >
                      重新加载
                    </el-button>
                  </div>
                  <div class="ai-image-preview">
                    <div v-if="msg.imageContext.imageLoading" class="ai-image-state">
                      图片加载中...
                    </div>
                    <img
                      v-else-if="msg.imageContext.proxyImageUrl"
                      :src="msg.imageContext.proxyImageUrl"
                      alt="AI 生成图片"
                      @error="handlePreviewImageError(msg)"
                      @click="previewImage(msg.imageContext.proxyImageUrl)"
                    >
                    <div v-else-if="msg.imageContext.imageError" class="ai-image-state error">
                      {{ msg.imageContext.imageError }}
                    </div>
                    <div v-else class="ai-image-state">
                      图片待加载
                    </div>
                  </div>
                  <div class="ai-image-actions">
                    <el-button
                      type="primary"
                      link
                      size="small"
                      :disabled="!msg.imageContext.proxyImageUrl && !msg.imageContext.referenceImageUrl"
                      @click="previewImage(msg.imageContext.proxyImageUrl || msg.imageContext.referenceImageUrl)"
                    >
                      <el-icon><View /></el-icon>
                      预览
                    </el-button>
                    <el-button
                      type="primary"
                      link
                      size="small"
                      @click="openOriginalImage(msg.imageContext.referenceImageUrl)"
                    >
                      <el-icon><Link /></el-icon>
                      打开原图
                    </el-button>
                  </div>
                </div>
              </div>
              <div v-if="msg.role === 'assistant'" class="message-actions">
                <el-button type="primary" link size="small" @click="copyContent(msg.content)">
                  <el-icon><DocumentCopy /></el-icon>
                  复制
                </el-button>
                <el-button type="primary" link size="small" @click="regenerate(index)">
                  <el-icon><RefreshRight /></el-icon>
                  重新生成
                </el-button>
                <el-button
                  v-if="getImageContext(msg)"
                  type="primary"
                  link
                  size="small"
                  @click="adjustImage(msg)"
                >
                  <el-icon><EditPen /></el-icon>
                  调整图片
                </el-button>
              </div>
            </div>

            <!-- 用户头像 -->
            <div v-if="msg.role === 'user'" class="message-avatar user">
              <el-icon :size="20"><User /></el-icon>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-if="loading" class="message-item assistant">
            <div class="message-avatar ai">
              <el-icon :size="20"><Service /></el-icon>
            </div>
            <div class="message-content">
              <div class="message-bubble ai-bubble">
                <div class="typing-indicator">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
              </div>
            </div>
          </div>
        </template>
      </div>
    </main>

    <!-- 底部输入区域 -->
    <footer class="chat-footer">
      <div class="footer-tools">
        <div class="image-size-control">
          <el-icon><Picture /></el-icon>
          <span>图片尺寸</span>
          <el-select v-model="imageSize" size="small" class="image-size-select">
            <el-option
              v-for="item in imageSizeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </div>
      </div>
      <div class="input-container">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="1"
          :autosize="{ minRows: 1, maxRows: 4 }"
          placeholder="输入你的问题... (Enter 发送, Shift+Enter 换行)"
          :disabled="loading"
          @keydown="handleKeydown"
          ref="inputRef"
          class="message-input"
        />
        <div class="input-actions">
          <el-tooltip content="发送消息" placement="top">
            <el-button
              type="primary"
              :icon="Promotion"
              circle
              :loading="loading"
              :disabled="!inputMessage.trim()"
              @click="sendMessage()"
              class="send-btn"
            />
          </el-tooltip>
        </div>
      </div>
      <div class="footer-tips">
        <span>AI 可能会犯错，请核实重要信息</span>
      </div>
    </footer>
    <el-image-viewer
      v-if="imagePreviewVisible"
      :url-list="[imagePreviewUrl]"
      @close="imagePreviewVisible = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ChatDotRound, Service, User, DocumentCopy, RefreshRight,
  Promotion, Plus, Delete, HomeFilled, TrendCharts,
  DataAnalysis, PieChart, Histogram, EditPen, Picture, View, Link
} from '@element-plus/icons-vue'
import { chat } from '@/api/ai'
import MdViewer from '@/components/MdViewer.vue'

const router = useRouter()

// 消息列表
const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messagesRef = ref(null)
const inputRef = ref(null)
const imageSize = ref('2048x2048')
const imagePreviewVisible = ref(false)
const imagePreviewUrl = ref('')

const imageSizeOptions = [
  { label: '方图 2048x2048', value: '2048x2048' },
  { label: '横图 2752x1536', value: '2752x1536' },
  { label: '竖图 1536x2752', value: '1536x2752' }
]

// 快捷操作
const quickActions = [
  { text: '今日A股行情如何？', icon: TrendCharts, color: '#f56c6c' },
  { text: '分析贵州茅台最新走势', icon: DataAnalysis, color: '#409eff' },
  { text: '查看北向资金流向', icon: PieChart, color: '#67c23a' },
  { text: '推荐几只优质基金', icon: Histogram, color: '#e6a23c' }
]

// 格式化用户消息
function formatUserMessage(content) {
  return content.replace(/\n/g, '<br>')
}

// 滚动到底部
function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

function isImageAdjustMessage(message) {
  if (!message) return false
  const adjustWords = [
    '调整', '修改', '改成', '换成', '优化', '重画', '重新生成',
    '基于上一张', '基于上图', '上一张', '上图', '这张图'
  ]
  return adjustWords.some((word) => message.includes(word))
}

function isImageGenerateMessage(message) {
  if (!message) return false
  const generateWords = [
    '生成图片', '生成一张', '画一张', '画个', '画一个', '绘制',
    '出图', '文生图', 'AI作图', 'AI绘图', '图片'
  ]
  return generateWords.some((word) => message.includes(word))
}

function extractLineValue(content, label) {
  const regexp = new RegExp(`^-\\s*${label}[:：]\\s*(.+)$`, 'm')
  const match = content.match(regexp)
  return match ? match[1].trim() : ''
}

function extractImageContext(content) {
  if (!content) return null
  const markdownImageMatch = content.match(/!\[[^\]]*]\(([^)]+)\)/)
  const openImageMatch = content.match(/\[打开原图]\(([^)]+)\)/)
  const imageUrl = markdownImageMatch?.[1] || openImageMatch?.[1] || ''
  if (!imageUrl) return null
  return normalizeImageContext({
    referenceImageUrl: imageUrl.trim(),
    referenceImagePrompt: extractLineValue(content, '提示词'),
    referenceImageRevisedPrompt: extractLineValue(content, '优化提示词'),
    proxyImageUrl: imageUrl.trim(),
    imageLoading: false,
    imageError: ''
  })
}

function normalizeImageContext(context) {
  if (!context) return null
  if (!context.referenceImageUrl && context.proxyImageUrl) {
    context.referenceImageUrl = context.proxyImageUrl
  }
  if (!context.proxyImageUrl && context.referenceImageUrl) {
    context.proxyImageUrl = context.referenceImageUrl
  }
  context.imageLoading = false
  return context
}

function getImageContext(message) {
  if (!message || message.role !== 'assistant') return null
  if (!message.imageContext) {
    message.imageContext = extractImageContext(message.content)
  }
  return normalizeImageContext(message.imageContext)
}

function buildDisplayContent(content) {
  if (!content) return ''
  return content
    .replace(/!\[[^\]]*]\([^)]+\)/g, '')
    .replace(/\n*\[打开原图]\([^)]+\)/g, '')
    .trim()
}

function getDisplayContent(message) {
  return message?.displayContent || buildDisplayContent(message?.content)
}

function createAssistantMessage(answer) {
  const assistantMessage = {
    role: 'assistant',
    content: answer,
    displayContent: buildDisplayContent(answer),
    imageContext: extractImageContext(answer)
  }
  messages.value.push(assistantMessage)
}

function findLastImageContext(beforeIndex = messages.value.length) {
  for (let i = beforeIndex - 1; i >= 0; i--) {
    const context = getImageContext(messages.value[i])
    if (context) {
      return context
    }
  }
  return null
}

function buildChatPayload(message, beforeIndex = messages.value.length) {
  const payload = { message, enableSkills: true }
  if (isImageGenerateMessage(message) || isImageAdjustMessage(message)) {
    payload.imageSize = imageSize.value
  }
  if (!isImageAdjustMessage(message)) {
    return payload
  }
  const imageContext = findLastImageContext(beforeIndex)
  if (!imageContext) {
    return payload
  }
  return {
    ...payload,
    referenceImageUrl: imageContext.referenceImageUrl,
    referenceImagePrompt: imageContext.referenceImagePrompt,
    referenceImageRevisedPrompt: imageContext.referenceImageRevisedPrompt
  }
}

// 发送消息
async function sendMessage(text) {
  const message = text || inputMessage.value.trim()
  if (!message || loading.value) return

  // 添加用户消息
  messages.value.push({ role: 'user', content: message })
  inputMessage.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const res = await chat(buildChatPayload(message))
    const answer = res.data?.answer || '抱歉，我暂时无法回答这个问题。'
    createAssistantMessage(answer)
    scrollToBottom()
  } catch (err) {
    const errMsg = err.response?.data?.message || err.message || 'AI 服务暂时不可用'
    messages.value.push({
      role: 'assistant',
      content: `❌ **请求失败**\n\n${errMsg}\n\n请稍后重试，或检查网络连接。`
    })
    scrollToBottom()
  } finally {
    loading.value = false
    nextTick(() => {
      inputRef.value?.focus()
    })
  }
}

// 处理键盘事件
function handleKeydown(event) {
  if (event.key === 'Enter' && !event.shiftKey) {
    event.preventDefault()
    sendMessage()
  }
}

// 复制内容
function copyContent(text) {
  if (!text) return
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('已复制到剪贴板')
  }).catch(() => {
    ElMessage.error('复制失败')
  })
}

// 重新生成
async function regenerate(index) {
  if (loading.value) return

  // 找到对应的用户消息
  let userMessageIndex = -1
  for (let i = index - 1; i >= 0; i--) {
    if (messages.value[i].role === 'user') {
      userMessageIndex = i
      break
    }
  }

  if (userMessageIndex === -1) return

  const userMessage = messages.value[userMessageIndex].content

  // 删除当前 AI 回复
  messages.value.splice(index, 1)
  loading.value = true
  scrollToBottom()

  try {
    const res = await chat(buildChatPayload(userMessage, userMessageIndex))
    const answer = res.data?.answer || '抱歉，我暂时无法回答这个问题。'
    createAssistantMessage(answer)
    scrollToBottom()
  } catch (err) {
    const errMsg = err.response?.data?.message || err.message || 'AI 服务暂时不可用'
    messages.value.push({
      role: 'assistant',
      content: `❌ **请求失败**\n\n${errMsg}\n\n请稍后重试。`
    })
    scrollToBottom()
  } finally {
    loading.value = false
  }
}

function adjustImage() {
  inputMessage.value = '请基于上一张图片调整：'
  nextTick(() => {
    inputRef.value?.focus()
  })
}

function reloadImage(message) {
  const context = getImageContext(message)
  if (!context) return
  context.imageError = ''
  context.imageLoading = false
  context.proxyImageUrl = ''
  nextTick(() => {
    context.proxyImageUrl = context.referenceImageUrl
  })
}

function previewImage(url) {
  if (!url) return
  imagePreviewUrl.value = url
  imagePreviewVisible.value = true
}

function handlePreviewImageError(message) {
  const context = getImageContext(message)
  if (!context) return
  context.imageLoading = false
  context.imageError = '图片加载失败，请打开原图查看'
}

function openOriginalImage(url) {
  if (!url) return
  const openedWindow = window.open(url, '_blank')
  if (openedWindow) {
    openedWindow.opener = null
  }
}

// 新对话
function startNewChat() {
  if (messages.value.length === 0) return
  ElMessageBox.confirm('确定要开始新对话吗？当前对话将被清空。', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    messages.value = []
    nextTick(() => {
      inputRef.value?.focus()
    })
  }).catch(() => {})
}

// 清空聊天
function clearChat() {
  if (messages.value.length === 0) return
  ElMessageBox.confirm('确定要清空所有对话记录吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    messages.value = []
    ElMessage.success('对话已清空')
  }).catch(() => {})
}

// 返回首页
async function goHome() {
  try {
    await router.push('/dashboard')
  } catch (error) {
    console.warn('返回首页路由跳转失败，改用整页跳转', error)
    window.location.assign('/dashboard')
  }
}

// 自动聚焦输入框
onMounted(() => {
  nextTick(() => {
    inputRef.value?.focus()
  })
})

</script>

<style scoped>
.ai-chat-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--el-bg-color);
}

/* 顶部导航 */
.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 24px;
  background: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-lighter);
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-icon {
  font-size: 24px;
  color: var(--el-color-primary);
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.status-tag {
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.status-dot {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--el-color-success);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.header-right {
  display: flex;
  gap: 8px;
}

/* 主体区域 */
.chat-main {
  flex: 1;
  overflow: hidden;
  display: flex;
  justify-content: center;
}

.chat-messages {
  width: 100%;
  max-width: 800px;
  padding: 24px;
  overflow-y: auto;
  scroll-behavior: smooth;
}

/* 空状态 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  text-align: center;
}

.empty-logo {
  width: 80px;
  height: 80px;
  border-radius: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  margin-bottom: 24px;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.4);
}

.empty-title {
  font-size: 28px;
  font-weight: 700;
  color: var(--el-text-color-primary);
  margin: 0 0 8px;
}

.empty-desc {
  font-size: 16px;
  color: var(--el-text-color-secondary);
  margin: 0 0 32px;
}

.quick-actions {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  max-width: 480px;
  width: 100%;
}

.quick-action-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: var(--el-fill-color-light);
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.quick-action-card:hover {
  background: var(--el-color-primary-light-9);
  border-color: var(--el-color-primary-light-5);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.quick-action-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.quick-action-text {
  font-size: 14px;
  color: var(--el-text-color-primary);
  line-height: 1.4;
}

/* 消息项 */
.message-item {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.message-item.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.message-avatar.ai {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.message-avatar.user {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.message-content {
  max-width: 80%;
  min-width: 0;
}

.message-item.user .message-content {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.message-bubble {
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.user-bubble {
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  color: white;
  border-bottom-right-radius: 4px;
}

.ai-bubble {
  background: var(--el-fill-color-light);
  color: var(--el-text-color-primary);
  border: 1px solid var(--el-border-color-lighter);
  border-bottom-left-radius: 4px;
}

/* AI Markdown 内容样式 */
.ai-md-content {
  white-space: normal;
}

.ai-md-content :deep(.md-viewer__body) {
  font-size: 14px;
  line-height: 1.7;
}

.ai-md-content :deep(.md-viewer__body p) {
  margin: 0 0 8px;
}

.ai-md-content :deep(.md-viewer__body p:last-child) {
  margin-bottom: 0;
}

.ai-md-content :deep(.md-viewer__body table) {
  font-size: 13px;
  margin: 8px 0;
  border-collapse: collapse;
  min-width: 100%;
}

.ai-md-content :deep(.md-viewer__body table th),
.ai-md-content :deep(.md-viewer__body table td) {
  padding: 8px 12px;
  border: 1px solid var(--el-border-color-lighter);
}

.ai-md-content :deep(.md-viewer__body table th) {
  background: var(--el-fill-color-lighter);
  font-weight: 600;
}

.ai-md-content :deep(.md-viewer__body ul),
.ai-md-content :deep(.md-viewer__body ol) {
  padding-left: 20px;
  margin: 8px 0;
}

.ai-md-content :deep(.md-viewer__body li) {
  margin-bottom: 4px;
}

.ai-md-content :deep(.md-viewer__body code) {
  font-size: 13px;
  padding: 2px 6px;
  background: var(--el-fill-color-lighter);
  border-radius: 4px;
}

.ai-md-content :deep(.md-code-block) {
  margin: 8px 0;
  border-radius: 8px;
  overflow: hidden;
}

.ai-md-content :deep(.md-viewer__body blockquote) {
  margin: 8px 0;
  padding: 8px 12px;
  border-left: 3px solid var(--el-color-primary);
  background: var(--el-fill-color-lighter);
  border-radius: 0 6px 6px 0;
}

.ai-image-card {
  margin-top: 12px;
  border: 1px solid var(--el-border-color-lighter);
  border-radius: 8px;
  overflow: hidden;
  background: var(--el-bg-color);
}

.ai-image-card__header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 8px 12px;
  border-bottom: 1px solid var(--el-border-color-lighter);
}

.ai-image-card__title {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  min-width: 0;
  font-size: 13px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.ai-image-preview {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 180px;
  max-height: 520px;
  background: var(--el-fill-color-blank);
}

.ai-image-preview img {
  display: block;
  max-width: 100%;
  max-height: 520px;
  object-fit: contain;
  cursor: zoom-in;
}

.ai-image-state {
  padding: 48px 24px;
  font-size: 13px;
  color: var(--el-text-color-secondary);
  text-align: center;
}

.ai-image-state.error {
  color: var(--el-color-danger);
}

.ai-image-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-top: 1px solid var(--el-border-color-lighter);
}

.message-actions {
  margin-top: 8px;
  padding-left: 4px;
  display: flex;
  gap: 8px;
}

/* 打字动画 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 4px 0;
}

.typing-indicator span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--el-color-primary);
  animation: typing 1.4s ease-in-out infinite;
}

.typing-indicator span:nth-child(2) { animation-delay: 0.2s; }
.typing-indicator span:nth-child(3) { animation-delay: 0.4s; }

@keyframes typing {
  0%, 80%, 100% { transform: scale(0.6); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

/* 底部输入区域 */
.chat-footer {
  padding: 16px 24px 24px;
  background: var(--el-bg-color);
  border-top: 1px solid var(--el-border-color-lighter);
}

.footer-tools {
  max-width: 800px;
  margin: 0 auto 10px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
}

.image-size-control {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.image-size-select {
  width: 160px;
}

.input-container {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.message-input {
  flex: 1;
}

.message-input :deep(.el-textarea__inner) {
  border-radius: 12px;
  padding: 12px 16px;
  resize: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.message-input :deep(.el-textarea__inner:focus) {
  box-shadow: 0 2px 12px rgba(64, 158, 255, 0.2);
}

.input-actions {
  flex-shrink: 0;
}

.send-btn {
  width: 44px;
  height: 44px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
}

.send-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.5);
}

.send-btn:active {
  transform: scale(0.95);
}

.footer-tips {
  text-align: center;
  margin-top: 8px;
  font-size: 12px;
  color: var(--el-text-color-placeholder);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .chat-header {
    padding: 10px 16px;
  }

  .header-title {
    font-size: 16px;
  }

  .chat-messages {
    padding: 16px;
  }

  .footer-tools {
    justify-content: flex-start;
  }

  .image-size-select {
    width: 150px;
  }

  .quick-actions {
    grid-template-columns: 1fr;
  }

  .message-content {
    max-width: 90%;
  }

  .chat-footer {
    padding: 12px 16px 20px;
  }
}
</style>
