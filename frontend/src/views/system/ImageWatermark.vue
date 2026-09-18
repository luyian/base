<template>
  <div class="image-watermark-container">
    <el-card>
      <template #header>
        <span>图片去水印</span>
      </template>

      <el-tabs v-model="mode" type="border-card">
        <!-- ============ 自动识别 ============ -->
        <el-tab-pane label="自动识别" name="auto">
          <div class="convert-content">
            <div class="upload-section">
              <el-upload
                class="upload-box"
                drag
                :auto-upload="false"
                :show-file-list="false"
                accept=".jpg,.jpeg,.png,.bmp,.webp"
                :on-change="handleFileChange"
              >
                <div v-if="selectedFile" class="file-info">
                  <el-icon size="40" color="#409eff"><Picture /></el-icon>
                  <div class="file-name">{{ selectedFile.name }}</div>
                  <div class="file-size">{{ formatSize(selectedFile.size) }}</div>
                  <el-button v-if="selectedFile" class="overlay-clear" size="small" @click.stop="clearFile">
                    清除文件
                  </el-button>
                </div>
                <div v-else class="upload-placeholder">
                  <el-icon size="48"><UploadFilled /></el-icon>
                  <div class="upload-text">将图片拖到此处，或<em>点击上传</em></div>
                  <div class="upload-tip">支持 JPG / PNG / BMP / WebP，最大 50MB</div>
                </div>
              </el-upload>

              <div class="mode-desc">
                自动识别豆包 / 即梦 / 千问 / 可灵 / 百度 / 元宝等 AI 生成图右下角水印并去除。
              </div>

              <div class="action-bar">
                <el-button
                  type="primary"
                  size="large"
                  :loading="processing"
                  :disabled="!selectedFile"
                  @click="doProcess('auto')"
                >
                  {{ processing ? '处理中...' : '一键去除水印' }}
                </el-button>
              </div>
            </div>

            <!-- 自动识别结果 -->
            <div v-if="resultUrl" class="result-section">
              <el-divider content-position="left">处理结果</el-divider>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="源文件">
                  {{ selectedFile?.name }}
                </el-descriptions-item>
                <el-descriptions-item label="文件大小">
                  {{ formatSize(sourceSize) }}
                </el-descriptions-item>
                <el-descriptions-item label="处理后文件" :span="2">
                  <el-image
                    :src="resultUrl"
                    fit="contain"
                    :preview-src-list="[resultUrl]"
                    class="result-image"
                  />
                </el-descriptions-item>
              </el-descriptions>
              <div class="download-bar">
                <el-button type="success" @click="downloadResult">
                  <el-icon><Download /></el-icon> 下载处理后的图片
                </el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- ============ 手动框选 ============ -->
        <el-tab-pane label="手动框选" name="manual">
          <div class="convert-content">
            <div class="upload-section">
              <template v-if="!selectedFile">
                <el-upload
                  class="upload-box"
                  drag
                  :auto-upload="false"
                  :show-file-list="false"
                  accept=".jpg,.jpeg,.png,.bmp,.webp"
                  :on-change="handleFileChange"
                >
                  <div class="upload-placeholder">
                    <el-icon size="48"><UploadFilled /></el-icon>
                    <div class="upload-text">将图片拖到此处，或<em>点击上传</em></div>
                    <div class="upload-tip">支持 JPG / PNG / BMP / WebP，最大 50MB</div>
                  </div>
                </el-upload>
              </template>

              <!-- 手动画布：在图片上拖出矩形选区 -->
              <div v-else class="canvas-wrap">
                <div class="canvas-head">
                  <span class="file-name">{{ selectedFile.name }}</span>
                  <div class="canvas-tip">
                    {{ hasBox ? '已框选区域，可拖拽移动 / 拖角调整' : '按住并拖动鼠标，框出要去除的水印区域' }}
                  </div>
                  <el-button size="small" @click="resetBox">重置</el-button>
                </div>

                <div
                  class="canvas-box"
                  @mouseenter="canvasHover = true"
                  @mouseleave="canvasHover = false"
                >
                  <div
                    ref="stageRef"
                    class="stage-relative"
                    @mousedown="onStageDown"
                    @mousemove="onStageMove"
                    @mouseup="onStageUp"
                    @mouseleave="onStageUp"
                  >
                    <img
                      :src="displayUrl"
                      alt="原图"
                      class="stage-img"
                      draggable="false"
                      @load="onImgLoad"
                    />

                    <!-- 选区矩形 -->
                    <div
                      v-if="hasBox"
                      class="select-box"
                      :style="boxStyle"
                      @mousedown.stop="onBoxDown"
                    >
                      <!-- 拉伸角（简化：右下角单点） -->
                      <div
                        class="corner corner-se"
                        @mousedown.stop="onCornerDown('se')"
                      ></div>
                      <div
                        class="corner corner-nw"
                        @mousedown.stop="onCornerDown('nw')"
                      ></div>
                    </div>
                  </div>
                </div>

                <div class="action-bar">
                  <el-button
                    type="primary"
                    size="large"
                    :loading="processing"
                    :disabled="!hasBox"
                    @click="doProcess('manual')"
                  >
                    {{ processing ? '处理中...' : '擦除框选区域' }}
                  </el-button>
                  <el-button v-if="hasBox" @click="resetBox">重置框选</el-button>
                  <el-button @click="clearFile">重新选图</el-button>
                </div>
              </div>
            </div>

            <!-- 手动框选结果 -->
            <div v-if="resultUrl" class="result-section">
              <el-divider content-position="left">处理结果</el-divider>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="源文件">
                  {{ selectedFile?.name }}
                </el-descriptions-item>
                <el-descriptions-item label="文件大小">
                  {{ formatSize(sourceSize) }}
                </el-descriptions-item>
                <el-descriptions-item label="处理后文件" :span="2">
                  <el-image
                    :src="resultUrl"
                    fit="contain"
                    :preview-src-list="[resultUrl]"
                    class="result-image"
                  />
                </el-descriptions-item>
              </el-descriptions>
              <div class="download-bar">
                <el-button type="success" @click="downloadResult">
                  <el-icon><Download /></el-icon> 下载处理后的图片
                </el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onBeforeUnmount } from 'vue'
import { ElMessage } from 'element-plus'
import { Picture, UploadFilled, Download } from '@element-plus/icons-vue'
import { removeWatermark } from '@/api/imageWatermark'

const mode = ref('auto')
const selectedFile = ref(null)
const displayUrl = ref('')
const sourceSize = ref(0)
const processing = ref(false)
const resultUrl = ref('')

// ==================== 选图 ====================
function handleFileChange(uploadFile) {
  const file = uploadFile.raw
  const ok = /\.(jpe?g|png|bmp|webp)$/i.test(file.name)
  if (!ok) {
    ElMessage.error('请选择 JPG/PNG/BMP/WebP 图片')
    return
  }
  if (file.size > 50 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 50MB')
    return
  }
  selectedFile.value = file
  sourceSize.value = file.size
  if (displayUrl.value) URL.revokeObjectURL(displayUrl.value)
  displayUrl.value = URL.createObjectURL(file)
  resultUrl.value = ''
  resetBox()
}

function clearFile() {
  if (displayUrl.value) URL.revokeObjectURL(displayUrl.value)
  selectedFile.value = null
  displayUrl.value = ''
  resultUrl.value = ''
  resetBox()
}

// ==================== 手动框选（显示坐标） ====================
// 选区用 stage 内的显示坐标（displayW/displayH 基准），换算真实像素时乘 scale
const stageRef = ref(null)
const displayW = ref(0)
const displayH = ref(0)
const realW = ref(0)
const realH = ref(0)
// 选区：{x, y, w, h} 显示坐标
const box = ref(null)
const drawStart = ref(null)
const moving = ref(null) // 移动选区时：{ startX, startY, bx, by }
const cornerDrag = ref(null) // 拖角：{ type, bx, by, bw, bh }
const canvasHover = ref(false)
const resizeObserver = ref(null)

const hasBox = computed(() => !!box.value && box.value.w > 0 && box.value.h > 0)

const boxStyle = computed(() => {
  const b = box.value
  if (!b) return {}
  return {
    left: b.x + 'px',
    top: b.y + 'px',
    width: b.w + 'px',
    height: b.h + 'px'
  }
})

function onImgLoad(e) {
  const img = e.target
  realW.value = img.naturalWidth
  realH.value = img.naturalHeight
  // 用 stage 实测宽度 × 原图宽高比 推算渲染尺寸（确定性强，不受加载时序影响）
  measureStage()
  watchStage()
}

// 按 stage 实际宽度等比推算显示尺寸
function measureStage() {
  const w = stageRef.value?.clientWidth || 0
  if (!w || !realW.value || !realH.value) return
  displayW.value = w
  displayH.value = Math.round((w * realH.value) / realW.value)
}

// 监听 stage 尺寸变化（换图、窗口缩放、容器宽度变化时校准）
function watchStage() {
  if (!stageRef.value) return
  if (resizeObserver.value) {
    resizeObserver.value.disconnect()
  }
  resizeObserver.value = new ResizeObserver(measureStage)
  resizeObserver.value.observe(stageRef.value)
}

onBeforeUnmount(() => {
  if (resizeObserver.value) resizeObserver.value.disconnect()
})

// 相对 stage 的坐标
function stagePos(e) {
  const rect = stageRef.value.getBoundingClientRect()
  return {
    x: e.clientX - rect.left,
    y: e.clientY - rect.top
  }
}

function onStageDown(e) {
  if (!realW.value || !realH.value) return
  // 落在选区内 → 整体移动
  if (hasBox.value && pointInBox(e)) {
    onBoxDown(e)
    return
  }
  drawStart.value = stagePos(e)
  moving.value = null
  cornerDrag.value = null
}

function onStageMove(e) {
  const p = stagePos(e)
  // 拖角
  if (cornerDrag.value) {
    resizeBox(p)
    return
  }
  // 整体移动
  if (moving.value) {
    moveBox(p)
    return
  }
  // 绘制新框
  if (drawStart.value) {
    const s = drawStart.value
    const x = Math.min(s.x, p.x)
    const y = Math.min(s.y, p.y)
    const w = Math.max(0, Math.abs(p.x - s.x))
    const h = Math.max(0, Math.abs(p.y - s.y))
    if (w > 2 && h > 2) {
      box.value = { x, y, w, h }
    }
  }
}

function onStageUp() {
  drawStart.value = null
  moving.value = null
  cornerDrag.value = null
}

function pointInBox(e) {
  const p = stagePos(e)
  const b = box.value
  return p.x >= b.x && p.x <= b.x + b.w && p.y >= b.y && p.y <= b.y + b.h
}

// 移动选区
function onBoxDown(e) {
  const p = stagePos(e)
  const b = box.value
  moving.value = {
    startX: p.x,
    startY: p.y,
    bx: b.x,
    by: b.y
  }
}

function moveBox(p) {
  const m = moving.value
  let nx = m.bx + (p.x - m.startX)
  let ny = m.by + (p.y - m.startY)
  nx = Math.max(0, Math.min(nx, displayW.value - box.value.w))
  ny = Math.max(0, Math.min(ny, displayH.value - box.value.h))
  box.value = { ...box.value, x: nx, y: ny }
}

// 拖角缩放
function onCornerDown(type) {
  const b = box.value
  cornerDrag.value = { type, bx: b.x, by: b.y, bw: b.w, bh: b.h }
}

function resizeBox(p) {
  const t = cornerDrag.value
  let x = t.bx
  let y = t.by
  let w = t.bw
  let h = t.bh
  if (t.type === 'se') {
    w = Math.max(4, Math.min(displayW.value - t.bx, p.x - t.bx))
    h = Math.max(4, Math.min(displayH.value - t.by, p.y - t.by))
  } else {
    // nw：左上角
    const nx = Math.max(0, Math.min(p.x, t.bx + t.bw - 4))
    const ny = Math.max(0, Math.min(p.y, t.by + t.bh - 4))
    x = nx
    y = ny
    w = t.bx + t.bw - nx
    h = t.by + t.bh - ny
  }
  box.value = { x, y, w, h }
}

function resetBox() {
  box.value = null
  drawStart.value = null
  moving.value = null
  cornerDrag.value = null
}

// ==================== 处理 ====================
async function doProcess(type) {
  if (!selectedFile.value) return
  processing.value = true
  resultUrl.value = ''
  try {
    let region = ''
    if (type === 'manual') {
      // 显示坐标 → 真实像素坐标（等比）
      const scaleX = realW.value / displayW.value
      const scaleY = realH.value / displayH.value
      const x = Math.round(box.value.x * scaleX)
      const y = Math.round(box.value.y * scaleY)
      const w = Math.round(box.value.w * scaleX)
      const h = Math.round(box.value.h * scaleY)
      region = `${x},${y},${w},${h}`
    }
    const res = await removeWatermark(selectedFile.value, region)
    resultUrl.value = res.data.targetFile.fileUrl
    ElMessage.success('去水印完成')
  } catch (e) {
    ElMessage.error(e.message || '去水印失败')
  } finally {
    processing.value = false
  }
}

function downloadResult() {
  if (!resultUrl.value) {
    ElMessage.error('文件链接不可用')
    return
  }
  window.open(resultUrl.value, '_blank')
}

function formatSize(bytes) {
  if (!bytes) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let i = 0
  let size = bytes
  while (size >= 1024 && i < units.length - 1) {
    size /= 1024
    i++
  }
  return size.toFixed(1) + ' ' + units[i]
}
</script>

<style scoped>
.image-watermark-container {
  padding: 20px;
}

.convert-content {
  padding: 20px 0;
}

.upload-section {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.upload-box {
  width: 100%;
  max-width: 500px;
}

.upload-box :deep(.el-upload-dragger) {
  padding: 40px 20px;
}

.upload-placeholder {
  text-align: center;
  color: #909399;
}

.upload-text {
  margin-top: 12px;
  font-size: 14px;
}

.upload-text em {
  color: #409eff;
  font-style: normal;
}

.upload-tip {
  margin-top: 6px;
  font-size: 12px;
  color: #c0c4cc;
}

.file-info {
  text-align: center;
  position: relative;
}

.overlay-clear {
  margin-top: 8px;
}

.file-name {
  margin-top: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  word-break: break-all;
  max-width: 460px;
}

.file-size {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.mode-desc {
  margin-top: 18px;
  font-size: 13px;
  color: #606266;
}

.action-bar {
  margin-top: 22px;
  display: flex;
  gap: 12px;
}

.result-section {
  margin-top: 30px;
  width: 100%;
  max-width: 760px;
}

.result-image {
  width: 100%;
  max-height: 480px;
}

.download-bar {
  margin-top: 16px;
  text-align: center;
}

/* 手动框选画布 */
.canvas-wrap {
  width: 100%;
  max-width: 760px;
}

.canvas-head {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
}

.canvas-tip {
  flex: 1;
  font-size: 13px;
  color: #909399;
}

.canvas-box {
  position: relative;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  overflow: auto;
  background: #f5f7fa;
  cursor: crosshair;
  max-height: calc(100vh - 280px);
}

.stage-relative {
  position: relative;
}

.stage-img {
  display: block;
  width: 100%;
  user-select: none;
}

.select-box {
  position: absolute;
  box-sizing: border-box;
  background: rgba(38, 132, 255, 0.14);
  border: 1.5px solid #2684ff;
  cursor: move;
}

.corner {
  position: absolute;
  width: 20px;
  height: 20px;
  background: #2684ff;
  border: 2px solid #fff;
  border-radius: 3px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.3);
}

.corner-se {
  right: -10px;
  bottom: -10px;
  cursor: nwse-resize;
}

.corner-nw {
  left: -10px;
  top: -10px;
  cursor: nwse-resize;
}
</style>