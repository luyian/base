<template>
  <div class="scan-convert-container">
    <!-- 尚未开始整理：创建工作区 -->
    <div v-if="!doc" class="init-section">
      <el-form inline :model="initForm" @submit.prevent>
        <el-form-item label="文档命名">
          <el-input
            v-model="initForm.docName"
            placeholder="如：报销单合集"
            maxlength="50"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="压缩质量">
          <el-select v-model="levelKey" style="width: 140px">
            <el-option v-for="lv in levels" :key="lv.key" :label="lv.label" :value="lv.key" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :loading="creating" @click="startCreate">开始整理</el-button>
          <el-button type="success" @click="startCameraCapture">
            <el-icon><Camera /></el-icon> 拍照整理
          </el-button>
        </el-form-item>
      </el-form>
      <p class="init-tip">
        上传手机拍摄的文档扫描图片，系统会自动「灰度 + 降采样 + JPEG 压缩」后合并成一份 PDF。<br />
        手机端点「📷 拍照整理」可直接唤起相机逐张拍摄。
      </p>
    </div>

    <!-- 已开始整理：工作区 -->
    <div v-else>
      <!-- 工作区信息 -->
      <div class="doc-info">
        <el-tag type="primary" effect="plain">唯一编号：{{ doc.docNo }}</el-tag>
        <span class="doc-name">{{ doc.docName }}</span>
        <span class="doc-count">{{ images.length }} 张图片</span>
      </div>

      <!-- 上传追加区 -->
      <div class="upload-section">
        <div class="camera-bar">
          <el-button type="success" size="large" @click="startCameraCapture">
            <el-icon><Camera /></el-icon> 拍照整理
          </el-button>
          <span class="camera-tip">手机点此唤起相机逐张拍摄、依序追加；PC 上退化为选择图片</span>
        </div>
        <el-upload
          class="upload-box"
          drag
          multiple
          :auto-upload="false"
          :show-file-list="false"
          accept=".jpg,.jpeg,.png,.bmp,.webp"
          :on-change="handleFileChange"
        >
          <el-icon size="40" color="#409eff"><UploadFilled /></el-icon>
          <div class="upload-text">拖拽或点击选择图片（可多选）</div>
          <div class="upload-tip">
            支持 JPG / PNG / BMP / WebP，单张 ≤ 50MB；已整理完成后仍可继续追加
          </div>
        </el-upload>
        <div v-if="pendingCount > 0" class="pending-bar">
          <span>已选择 {{ pendingCount }} 张待上传</span>
          <el-button type="primary" size="small" :loading="uploading" @click="doUpload">
            上传图片
          </el-button>
          <el-button size="small" @click="clearPending">清空待上传</el-button>
        </div>
      </div>

      <!-- 图片列表（可排序） -->
      <div v-if="images.length > 0" class="image-grid">
        <div v-for="(item, index) in images" :key="item.key" class="image-card">
          <el-image :src="item.url" :preview-src-list="[item.url]" fit="cover" class="image-thumb" />
          <span class="image-index">{{ index + 1 }}</span>
          <div class="image-actions">
            <el-tooltip content="前移">
              <el-button text :disabled="index === 0" @click="moveUp(index)">
                <el-icon><ArrowUp /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="后移">
              <el-button text :disabled="index === images.length - 1" @click="moveDown(index)">
                <el-icon><ArrowDown /></el-icon>
              </el-button>
            </el-tooltip>
            <el-tooltip content="删除">
              <el-button text type="danger" @click="removeImage(item, index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </el-tooltip>
          </div>
          <div class="image-name">{{ item.fileName }}</div>
        </div>
      </div>

      <!-- 操作栏 -->
      <div class="action-bar">
        <el-button type="primary" :loading="finalizing" :disabled="images.length === 0" @click="doFinalize">
          {{ finalizing ? '生成中...' : '完成并生成 PDF' }}
        </el-button>
        <el-button @click="confirmAbort">放弃工作区</el-button>
      </div>

      <!-- 完成结果 -->
      <div v-if="result" class="result-section">
        <el-divider content-position="left">整理完成</el-divider>
        <el-descriptions :column="2" border>
          <el-descriptions-item label="PDF 文件">{{ result.fileName }}</el-descriptions-item>
          <el-descriptions-item label="大小">{{ formatSize(result.fileSize) }}</el-descriptions-item>
          <el-descriptions-item label="图片数">{{ result.count }} 张</el-descriptions-item>
          <el-descriptions-item label="状态">已生成，源图已清理</el-descriptions-item>
        </el-descriptions>
        <div class="download-bar">
          <el-button type="success" @click="downloadPdf">
            <el-icon><Download /></el-icon> 下载 PDF
          </el-button>
          <el-button @click="resetAll">再整理一份</el-button>
        </div>
      </div>
    </div>

    <!-- 隐藏的摄像头文件输入：capture=environment 在手机端唤起后置相机，PC 端忽略退化为选图 -->
    <input
      ref="cameraInput"
      type="file"
      accept="image/*"
      capture="environment"
      style="display: none"
      @change="onCameraCapture"
    />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowDown, ArrowUp, Camera, Delete, Download, UploadFilled } from '@element-plus/icons-vue'
import {
  createScanDoc,
  pushScanImages,
  reorderScanImages,
  deleteScanImage,
  deleteScanDoc,
  finalizeScanDoc
} from '@/api/fileConvert'

// 压缩档位：颜色（灰度）+ 降采样最长边 + JPEG 质量
const levels = [
  { key: 'standard', label: '常规 (推荐)', maxSide: 2000, quality: 70 },
  { key: 'high', label: '清晰', maxSide: 2400, quality: 80 },
  { key: 'compact', label: '压缩', maxSide: 1200, quality: 50 }
]

const initForm = ref({ docName: '' })
const levelKey = ref('standard')
const level = computed(() => levels.find((lv) => lv.key === levelKey.value))

const doc = ref(null)
const images = ref([]) // {key, id, fileName, size, url, pending}
let keySeq = 0

const creating = ref(false)
const uploading = ref(false)
const finalizing = ref(false)
const result = ref(null)

// 隐藏的摄像头文件输入（手机端唤起相机）
const cameraInput = ref(null)

const pendingCount = computed(() => images.value.filter((i) => i.pending).length)

function handleFileChange(uploadFile) {
  const file = uploadFile.raw
  if (!file) return
  if (!/\.(jpg|jpeg|png|bmp|webp)$/i.test(file.name)) {
    ElMessage.error(`「${file.name}」不是支持的图片格式`)
    return
  }
  if (file.size > 50 * 1024 * 1024) {
    ElMessage.error(`「${file.name}」超过 50MB`)
    return
  }
  if (result.value) {
    ElMessage.warning('已完成归档，如需追加请重新开始整理')
    return
  }
  images.value.push({
    key: ++keySeq,
    id: null,
    fileName: file.name,
    size: file.size,
    url: URL.createObjectURL(file),
    pending: true,
    file
  })
}

async function startCreate() {
  creating.value = true
  try {
    const res = await createScanDoc(initForm.value.docName)
    doc.value = res.data
    result.value = null
    images.value = []
    ElMessage.success(`工作区已创建，编号 ${doc.value.docNo}`)
  } catch (e) {
    ElMessage.error(e.message || '创建失败')
  } finally {
    creating.value = false
  }
}

/**
 * 点击「拍照整理」：若无工作区先创建一个（命名缺省「拍摄文档」），随后唤起相机。
 */
async function startCameraCapture() {
  if (!doc.value) {
    if (!initForm.value.docName.trim()) {
      initForm.value.docName = '拍摄文档'
    }
    await startCreate()
  }
  cameraInput.value && cameraInput.value.click()
}

/**
 * 拍照/选图回调：校验并追加为待上传项，重置 input 以支持连拍，随后自动上传到工作区末尾。
 */
function onCameraCapture(e) {
  const input = e.target
  const file = input && input.files && input.files[0]
  // 立即重置，下次点击仍能唤起相机/选择器（连拍）
  if (input) {
    input.value = ''
  }
  if (!file) {
    return
  }
  if (result.value) {
    ElMessage.warning('已完成归档，如需追加请重新开始整理')
    return
  }
  if (!/\.(jpg|jpeg|png|bmp|webp)$/i.test(file.name)) {
    ElMessage.error(`「${file.name}」不是支持的图片格式，请重拍或改用相册选图`)
    return
  }
  if (file.size > 50 * 1024 * 1024) {
    ElMessage.error(`「${file.name}」超过 50MB`)
    return
  }
  images.value.push({
    key: ++keySeq,
    id: null,
    fileName: file.name,
    size: file.size,
    url: URL.createObjectURL(file),
    pending: true,
    file
  })
  // 拍照即上传，依序追加到工作区末尾
  doUpload()
}

async function doUpload() {
  const pending = images.value.filter((i) => i.pending)
  if (pending.length === 0) return
  uploading.value = true
  try {
    const res = await pushScanImages(doc.value.docId, pending.map((i) => i.file))
    syncByIdentity(res.data.images)
    ElMessage.success(`已上传 ${pending.length} 张`)
  } catch (e) {
    ElMessage.error(e.message || '上传失败')
  } finally {
    uploading.value = false
  }
}

function syncByIdentity(serverImages) {
  // 后端返回全量图片列表（按 sort 排序 = 旧图原序 + 本次追加序）。
  // 本地始终保留上传文件的 objectURL；已上传的按 id 匹配，新增的按待上传顺序取本地缩略图。
  const byId = new Map(images.value.filter((i) => i.id).map((i) => [i.id, i]))
  const pendingPool = images.value.filter((i) => !i.id)
  const usedIds = new Set()
  let pIndex = 0

  images.value = serverImages.map((si) => {
    let src = byId.get(si.id)
    if (!src || usedIds.has(si.id)) {
      while (pIndex < pendingPool.length && usedIds.has(pendingPool[pIndex].id || '')) pIndex++
      src = pendingPool[pIndex++]
    }
    usedIds.add(si.id)
    return {
      key: ++keySeq,
      id: si.id,
      fileName: si.fileName || (src && src.fileName) || '',
      size: si.size,
      url: src ? src.url : '',
      pending: false,
      file: null
    }
  })
  // 去掉未匹配到缩略图的占位项
  images.value = images.value.filter((i) => i.url)
}

function clearPending() {
  images.value = images.value.filter((i) => !i.pending)
}

function moveUp(index) {
  if (index <= 0) return
  const arr = [...images.value]
  ;[arr[index - 1], arr[index]] = [arr[index], arr[index - 1]]
  images.value = arr
  persistOrder()
}

function moveDown(index) {
  if (index >= images.value.length - 1) return
  const arr = [...images.value]
  ;[arr[index + 1], arr[index]] = [arr[index], arr[index + 1]]
  images.value = arr
  persistOrder()
}

async function persistOrder() {
  const ids = images.value.map((i) => i.id).filter((id) => id)
  if (ids.length === 0) return
  try {
    await reorderScanImages(doc.value.docId, ids)
  } catch (e) {
    ElMessage.error(e.message || '顺序保存失败')
  }
}

async function removeImage(item, index) {
  try {
    await ElMessageBox.confirm('确定删除这张图片吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    if (item.id) {
      await deleteScanImage(doc.value.docId, item.id)
    }
    images.value.splice(index, 1)
    ElMessage.success('已删除')
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}

async function doFinalize() {
  finalizing.value = true
  try {
    const res = await finalizeScanDoc(doc.value.docId, level.value.maxSide, level.value.quality)
    const t = res.data.targetFile
    result.value = {
      fileName: t.fileName,
      fileSize: t.fileSize,
      fileUrl: t.fileUrl,
      count: images.value.length
    }
    images.value = []
    ElMessage.success('PDF 生成成功，源图片已清理')
  } catch (e) {
    ElMessage.error(e.message || '生成失败')
  } finally {
    finalizing.value = false
  }
}

function downloadPdf() {
  if (!result.value?.fileUrl) return
  window.open(result.value.fileUrl, '_blank')
}

async function confirmAbort() {
  try {
    await ElMessageBox.confirm('将放弃当前整理并清理所有已上传图片，确定吗？', '提示', { type: 'warning' })
  } catch {
    return
  }
  try {
    await deleteScanDoc(doc.value.docId)
    ElMessage.success('已放弃')
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  } finally {
    resetAll()
  }
}

function resetAll() {
  doc.value = null
  images.value = []
  result.value = null
  initForm.value.docName = ''
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
.scan-convert-container {
  padding: 20px 0;
}

.init-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.init-tip {
  margin-top: 8px;
  font-size: 13px;
  color: #909399;
}

.doc-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.doc-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.doc-count {
  font-size: 13px;
  color: #909399;
}

.upload-section {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.camera-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.camera-tip {
  font-size: 12px;
  color: #909399;
}

.upload-box {
  width: 100%;
}

.upload-box :deep(.el-upload-dragger) {
  padding: 30px 16px;
}

.upload-text {
  margin-top: 8px;
  font-size: 14px;
  color: #606266;
}

.upload-tip {
  margin-top: 4px;
  font-size: 12px;
  color: #c0c4cc;
}

.pending-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 13px;
  color: #606266;
}

.image-grid {
  margin-top: 20px;
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: 16px;
}

.image-card {
  position: relative;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  overflow: hidden;
  padding: 6px;
}

.image-thumb {
  width: 100%;
  height: 140px;
  border-radius: 4px;
  display: block;
}

.image-index {
  position: absolute;
  top: 10px;
  left: 10px;
  background: rgba(48, 49, 51, 0.7);
  color: #fff;
  font-size: 12px;
  border-radius: 4px;
  padding: 1px 6px;
}

.image-actions {
  display: flex;
  justify-content: center;
  margin-top: 4px;
}

.image-name {
  font-size: 12px;
  color: #606266;
  text-align: center;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.action-bar {
  margin-top: 24px;
  display: flex;
  gap: 12px;
}

.result-section {
  margin-top: 24px;
}

.download-bar {
  margin-top: 16px;
  text-align: center;
}
</style>