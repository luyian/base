<template>
  <div class="image-generator-page">
    <header class="generator-header">
      <div class="header-left">
        <el-icon class="header-icon"><Picture /></el-icon>
        <div>
          <div class="header-title">AI 作图</div>
          <div class="header-subtitle">多图生成工作台</div>
        </div>
      </div>
      <div class="header-actions">
        <el-button type="primary" :icon="EditPen" @click="openPolishDrawer">AI 润色</el-button>
        <el-tooltip content="AI 助手" placement="bottom">
          <el-button :icon="Service" circle @click="goChat" />
        </el-tooltip>
        <el-tooltip content="返回首页" placement="bottom">
          <el-button :icon="HomeFilled" circle @click="goHome" />
        </el-tooltip>
      </div>
    </header>

    <main class="generator-main">
      <aside class="control-panel">
        <el-form label-position="top" class="generator-form">
          <el-form-item label="提示词">
            <el-input
              v-model="form.prompt"
              type="textarea"
              :autosize="{ minRows: 5, maxRows: 8 }"
              maxlength="4000"
              show-word-limit
              placeholder="描述你要生成的画面、主体、构图、光线和细节"
            />
          </el-form-item>

          <el-form-item label="负向提示词">
            <el-input
              v-model="form.negativePrompt"
              type="textarea"
              :autosize="{ minRows: 2, maxRows: 4 }"
              maxlength="2000"
              show-word-limit
              placeholder="不希望出现的内容，例如低清晰度、畸形、文字水印"
            />
          </el-form-item>

          <div class="form-grid">
            <el-form-item label="生成数量">
              <div class="count-control">
                <el-slider v-model="form.count" :min="1" :max="8" :step="1" show-stops />
                <el-input-number v-model="form.count" :min="1" :max="8" controls-position="right" />
              </div>
            </el-form-item>

            <el-form-item label="图片尺寸">
              <el-select v-model="form.size" class="full-width">
                <el-option
                  v-for="item in sizeOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="适配器">
              <el-select v-model="form.adapter" class="full-width">
                <el-option
                  v-for="item in adapterOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="模型">
              <el-input v-model="form.model" clearable placeholder="留空使用当前图片模型配置" />
            </el-form-item>

            <el-form-item label="风格">
              <el-select v-model="form.style" class="full-width" filterable allow-create clearable>
                <el-option
                  v-for="item in styleOptions"
                  :key="item"
                  :label="item"
                  :value="item"
                />
              </el-select>
            </el-form-item>

            <el-form-item label="Seed">
              <div class="seed-control">
                <el-input-number
                  v-model="form.seed"
                  :min="0"
                  :max="2147483647"
                  controls-position="right"
                  placeholder="随机"
                />
                <el-tooltip content="随机 seed" placement="top">
                  <el-button :icon="Refresh" circle @click="randomSeed" />
                </el-tooltip>
              </div>
            </el-form-item>
          </div>

          <div class="form-actions">
            <el-button :icon="Delete" @click="resetForm">重置</el-button>
            <el-button
              type="primary"
              :icon="MagicStick"
              :loading="loading"
              :disabled="!form.prompt.trim()"
              @click="handleGenerate"
            >
              生成图片
            </el-button>
          </div>
        </el-form>
      </aside>

      <section class="result-panel">
        <div class="result-toolbar">
          <div>
            <h2>生成结果</h2>
            <p>
              {{ resultSummary }}
            </p>
          </div>
          <el-tag v-if="responseMeta.adapter" effect="plain">
            {{ responseMeta.adapter }} · {{ responseMeta.model || '默认模型' }}
          </el-tag>
        </div>

        <div v-if="loading" class="image-grid">
          <div v-for="item in form.count" :key="item" class="image-card loading-card">
            <el-skeleton animated>
              <template #template>
                <el-skeleton-item
                  variant="image"
                  class="skeleton-image"
                  :style="{ aspectRatio: currentAspectRatio }"
                />
                <el-skeleton-item variant="text" class="skeleton-text" />
              </template>
            </el-skeleton>
          </div>
        </div>

        <el-empty
          v-else-if="images.length === 0"
          description="暂无图片"
          :image-size="120"
          class="empty-result"
        />

        <div v-else class="image-grid">
          <article
            v-for="item in images"
            :key="item.key"
            class="image-card"
          >
            <div class="image-box" :style="{ aspectRatio: item.aspectRatio }">
              <img
                v-if="item.src && !item.error"
                :src="item.src"
                alt="AI 生成图片"
                @click="previewImage(item)"
                @error="handleImageError(item)"
              >
              <div v-else class="image-error">
                <el-icon><WarningFilled /></el-icon>
                <span>图片加载失败</span>
              </div>
            </div>
            <div class="image-meta">
              <span>#{{ item.index }}</span>
              <span>{{ item.mimeType || 'image/png' }}</span>
            </div>
            <div class="image-actions">
              <el-tooltip content="预览" placement="top">
                <el-button :icon="View" circle size="small" @click="previewImage(item)" />
              </el-tooltip>
              <el-tooltip content="打开原图" placement="top">
                <el-button :icon="Link" circle size="small" :disabled="!item.url" @click="openImage(item)" />
              </el-tooltip>
              <el-tooltip content="复制链接" placement="top">
                <el-button :icon="DocumentCopy" circle size="small" @click="copyImage(item)" />
              </el-tooltip>
              <el-tooltip content="下载" placement="top">
                <el-button :icon="Download" circle size="small" @click="downloadImage(item)" />
              </el-tooltip>
            </div>
          </article>
        </div>
      </section>
    </main>

    <el-image-viewer
      v-if="previewVisible"
      :url-list="previewList"
      :initial-index="previewIndex"
      @close="previewVisible = false"
    />

    <el-drawer
      v-model="polishVisible"
      title="AI 润色"
      direction="rtl"
      size="420px"
      class="polish-drawer"
    >
      <div class="polish-panel">
        <el-form label-position="top">
          <el-form-item label="原始描述">
            <el-input
              v-model="polishForm.prompt"
              type="textarea"
              :autosize="{ minRows: 7, maxRows: 11 }"
              maxlength="4000"
              show-word-limit
              placeholder="输入要润色的图片描述"
            />
          </el-form-item>

          <div class="polish-actions">
            <el-button @click="syncCurrentPrompt">同步当前提示词</el-button>
            <el-button
              type="primary"
              :icon="MagicStick"
              :loading="polishLoading"
              :disabled="!polishForm.prompt.trim()"
              @click="handlePolish"
            >
              开始润色
            </el-button>
          </div>

          <el-form-item label="润色结果">
            <el-input
              v-model="polishForm.result"
              type="textarea"
              :autosize="{ minRows: 10, maxRows: 16 }"
              maxlength="4000"
              show-word-limit
              placeholder="AI 润色后的提示词会显示在这里"
            />
          </el-form-item>

          <div class="polish-footer">
            <el-button
              :icon="DocumentCopy"
              :disabled="!polishForm.result.trim()"
              @click="copyPolishedPrompt"
            >
              复制
            </el-button>
            <el-button
              type="primary"
              :disabled="!polishForm.result.trim()"
              @click="applyPolishedPrompt"
            >
              应用到提示词
            </el-button>
          </div>
        </el-form>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { computed, reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  Delete, DocumentCopy, Download, EditPen, HomeFilled, Link, MagicStick, Picture,
  Refresh, Service, View, WarningFilled
} from '@element-plus/icons-vue'
import { generateAiImages, polishAiImagePrompt } from '@/api/aiImage'

const router = useRouter()

const DEFAULT_FORM = {
  prompt: '',
  negativePrompt: '',
  size: '1024x1024',
  count: 4,
  model: '',
  adapter: '',
  style: '',
  seed: null
}

const form = reactive({ ...DEFAULT_FORM })
const loading = ref(false)
const images = ref([])
const responseMeta = reactive({
  model: '',
  adapter: '',
  size: '',
  count: 0
})
const previewVisible = ref(false)
const previewList = ref([])
const previewIndex = ref(0)
const polishVisible = ref(false)
const polishLoading = ref(false)
const polishForm = reactive({
  prompt: '',
  result: ''
})

const sizeOptions = [
  { label: '方图 1024x1024', value: '1024x1024' },
  { label: '横图 1536x1024', value: '1536x1024' },
  { label: '竖图 1024x1536', value: '1024x1536' },
  { label: '高清方图 2048x2048', value: '2048x2048' },
  { label: '宽幅横图 2752x1536', value: '2752x1536' },
  { label: '海报竖图 1536x2752', value: '1536x2752' }
]

const adapterOptions = [
  { label: '自动选择', value: '' },
  { label: 'OpenAI Images', value: 'openai-images' },
  { label: 'Chat Completions', value: 'chat-completions' },
  { label: 'SenseNova', value: 'sensenova' }
]

const styleOptions = [
  '写实摄影',
  '电影感',
  '商业海报',
  '国风插画',
  '像素艺术',
  '水彩',
  '赛博朋克',
  '极简平面'
]

const resultSummary = computed(() => {
  if (loading.value) {
    return `正在生成 ${form.count} 张图片`
  }
  if (images.value.length === 0) {
    return '生成后会在这里展示图片网格'
  }
  return `已返回 ${images.value.length} 张图片，目标数量 ${responseMeta.count || form.count} 张`
})

const currentAspectRatio = computed(() => resolveAspectRatio(responseMeta.size || form.size))

function goHome() {
  router.push('/')
}

function goChat() {
  router.push('/ai/chat')
}

function resetForm() {
  Object.assign(form, DEFAULT_FORM)
  images.value = []
  responseMeta.model = ''
  responseMeta.adapter = ''
  responseMeta.size = ''
  responseMeta.count = 0
}

function randomSeed() {
  form.seed = Math.floor(Math.random() * 2147483647)
}

function openPolishDrawer() {
  polishForm.prompt = form.prompt
  polishForm.result = ''
  polishVisible.value = true
}

function syncCurrentPrompt() {
  polishForm.prompt = form.prompt
  ElMessage.success('已同步当前提示词')
}

async function handlePolish() {
  if (!polishForm.prompt.trim()) {
    ElMessage.warning('请输入要润色的图片描述')
    return
  }
  polishLoading.value = true
  try {
    const res = await polishAiImagePrompt(buildPolishPayload())
    const data = res.data || {}
    polishForm.result = data.polishedPrompt || ''
    if (!polishForm.result) {
      ElMessage.warning('AI 未返回润色结果')
      return
    }
    ElMessage.success('润色完成')
  } catch (error) {
    console.error('AI 润色失败:', error)
  } finally {
    polishLoading.value = false
  }
}

function buildPolishPayload() {
  const payload = {
    prompt: polishForm.prompt.trim(),
    style: form.style.trim() || undefined,
    size: form.size || undefined
  }
  Object.keys(payload).forEach((key) => {
    if (payload[key] === undefined || payload[key] === '') {
      delete payload[key]
    }
  })
  return payload
}

function applyPolishedPrompt() {
  if (!polishForm.result.trim()) {
    ElMessage.warning('暂无可应用的润色结果')
    return
  }
  form.prompt = polishForm.result.trim()
  polishVisible.value = false
  ElMessage.success('已应用到提示词')
}

async function copyPolishedPrompt() {
  if (!polishForm.result.trim()) {
    ElMessage.warning('暂无可复制的润色结果')
    return
  }
  try {
    await navigator.clipboard.writeText(polishForm.result.trim())
    ElMessage.success('已复制')
  } catch (error) {
    console.error('润色结果复制失败:', error)
    ElMessage.error('复制失败')
  }
}

async function handleGenerate() {
  if (!form.prompt.trim()) {
    ElMessage.warning('请输入图片提示词')
    return
  }
  loading.value = true
  images.value = []
  try {
    const res = await generateAiImages(buildPayload())
    const data = res.data || {}
    responseMeta.model = data.model || ''
    responseMeta.adapter = data.adapter || ''
    responseMeta.size = data.size || ''
    responseMeta.count = data.count || form.count
    images.value = (data.images || []).map(normalizeImage)
    if (images.value.length === 0) {
      ElMessage.warning('供应商未返回可展示图片')
      return
    }
    ElMessage.success(`已生成 ${images.value.length} 张图片`)
  } catch (error) {
    console.error('AI 作图失败:', error)
  } finally {
    loading.value = false
  }
}

function buildPayload() {
  const payload = {
    prompt: form.prompt.trim(),
    negativePrompt: form.negativePrompt.trim() || undefined,
    size: form.size,
    count: form.count,
    model: form.model.trim() || undefined,
    adapter: form.adapter || undefined,
    style: form.style.trim() || undefined,
    seed: form.seed === null || form.seed === undefined ? undefined : form.seed
  }
  Object.keys(payload).forEach((key) => {
    if (payload[key] === undefined || payload[key] === '') {
      delete payload[key]
    }
  })
  return payload
}

function normalizeImage(item, index) {
  const mimeType = item.mimeType || 'image/png'
  const base64 = item.base64 || ''
  const src = item.url || (base64 ? `data:${mimeType};base64,${stripDataPrefix(base64)}` : '')
  const size = responseMeta.size || form.size
  return {
    key: `${item.index || index + 1}-${Date.now()}-${index}`,
    index: item.index || index + 1,
    url: item.url || '',
    base64,
    mimeType,
    revisedPrompt: item.revisedPrompt || '',
    src,
    aspectRatio: resolveAspectRatio(size),
    error: false
  }
}

function resolveAspectRatio(size) {
  if (!size) {
    return '1 / 1'
  }
  const match = String(size).match(/^(\d{3,4})x(\d{3,4})$/)
  if (!match) {
    return '1 / 1'
  }
  return `${Number(match[1])} / ${Number(match[2])}`
}

function stripDataPrefix(value) {
  const index = value.indexOf(',')
  if (value.startsWith('data:image/') && index > -1) {
    return value.substring(index + 1)
  }
  return value
}

function handleImageError(item) {
  item.error = true
}

function previewImage(item) {
  if (!item.src || item.error) {
    ElMessage.warning('当前图片不可预览')
    return
  }
  previewList.value = images.value.filter((image) => image.src && !image.error).map((image) => image.src)
  previewIndex.value = Math.max(previewList.value.indexOf(item.src), 0)
  previewVisible.value = true
}

function openImage(item) {
  if (!item.url) {
    ElMessage.warning('当前图片没有原图链接')
    return
  }
  window.open(item.url, '_blank', 'noopener,noreferrer')
}

async function copyImage(item) {
  const value = item.url || item.src
  if (!value) {
    ElMessage.warning('当前图片没有可复制内容')
    return
  }
  try {
    await navigator.clipboard.writeText(value)
    ElMessage.success('已复制')
  } catch (error) {
    console.error('复制失败:', error)
    ElMessage.error('复制失败')
  }
}

async function downloadImage(item) {
  if (!item.src) {
    ElMessage.warning('当前图片不可下载')
    return
  }
  const fileName = `ai-image-${item.index}.${resolveExt(item.mimeType)}`
  try {
    if (item.base64) {
      triggerDownload(item.src, fileName)
      return
    }
    const response = await fetch(item.url)
    const blob = await response.blob()
    const blobUrl = URL.createObjectURL(blob)
    triggerDownload(blobUrl, fileName)
    URL.revokeObjectURL(blobUrl)
  } catch (error) {
    console.error('图片下载失败:', error)
    window.open(item.url || item.src, '_blank', 'noopener,noreferrer')
  }
}

function triggerDownload(url, fileName) {
  const link = document.createElement('a')
  link.href = url
  link.download = fileName
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
}

function resolveExt(mimeType) {
  if (mimeType === 'image/jpeg') {
    return 'jpg'
  }
  if (mimeType === 'image/webp') {
    return 'webp'
  }
  if (mimeType === 'image/gif') {
    return 'gif'
  }
  return 'png'
}
</script>

<style scoped>
.image-generator-page {
  min-height: 100vh;
  background: #f4f7fb;
  color: #1f2937;
}

.generator-header {
  height: 72px;
  padding: 0 28px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #ffffff;
  border-bottom: 1px solid #e5e7eb;
}

.header-left,
.header-actions {
  display: flex;
  align-items: center;
  gap: 14px;
}

.header-icon {
  width: 42px;
  height: 42px;
  border-radius: 10px;
  color: #ffffff;
  background: linear-gradient(135deg, #0f766e, #2563eb);
  font-size: 22px;
}

.header-title {
  font-size: 18px;
  font-weight: 700;
  line-height: 1.3;
}

.header-subtitle {
  font-size: 12px;
  color: #64748b;
}

.generator-main {
  height: calc(100vh - 72px);
  display: grid;
  grid-template-columns: minmax(320px, 400px) minmax(0, 1fr);
  gap: 20px;
  padding: 20px;
}

.control-panel,
.result-panel {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
}

.control-panel {
  overflow-y: auto;
}

.generator-form {
  padding: 20px;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 4px;
}

.full-width {
  width: 100%;
}

.count-control,
.seed-control {
  display: flex;
  align-items: center;
  gap: 12px;
}

.count-control .el-slider {
  flex: 1;
}

.seed-control .el-input-number {
  flex: 1;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 8px;
}

.polish-panel {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.polish-actions,
.polish-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-bottom: 18px;
}

.polish-footer {
  margin-bottom: 0;
  padding-top: 2px;
}

.result-panel {
  min-width: 0;
  overflow-y: auto;
  padding: 20px;
}

.result-toolbar {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 16px;
  margin-bottom: 18px;
}

.result-toolbar h2 {
  font-size: 18px;
  margin: 0 0 4px;
}

.result-toolbar p {
  margin: 0;
  font-size: 13px;
  color: #64748b;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.image-card {
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  background: #ffffff;
  overflow: hidden;
}

.image-box {
  background: #eef2f7;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.image-box img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  cursor: zoom-in;
}

.image-error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #94a3b8;
  font-size: 13px;
}

.image-meta {
  min-height: 34px;
  padding: 8px 10px 0;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #64748b;
  font-size: 12px;
}

.image-actions {
  padding: 10px;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.loading-card {
  padding: 10px;
}

.skeleton-image {
  width: 100%;
  aspect-ratio: 1 / 1;
}

.skeleton-text {
  margin-top: 12px;
}

.empty-result {
  height: calc(100% - 80px);
  min-height: 360px;
}

@media (max-width: 1180px) {
  .image-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 900px) {
  .generator-main {
    height: auto;
    grid-template-columns: 1fr;
  }

  .control-panel,
  .result-panel {
    overflow: visible;
  }
}

@media (max-width: 640px) {
  .generator-header {
    height: auto;
    padding: 14px 16px;
    align-items: flex-start;
    gap: 12px;
    flex-direction: column;
  }

  .header-actions {
    width: 100%;
    justify-content: flex-end;
    flex-wrap: wrap;
  }

  .generator-main {
    padding: 12px;
    gap: 12px;
  }

  .result-panel,
  .generator-form {
    padding: 14px;
  }

  .image-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 12px;
  }

  .result-toolbar {
    flex-direction: column;
  }
}
</style>
