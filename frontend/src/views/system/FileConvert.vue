<template>
  <div class="file-convert-container">
    <el-card>
      <template #header>
        <span>文件转换</span>
      </template>

      <!-- 转换类型 Tab -->
      <el-tabs v-model="activeType" type="border-card">
        <!-- PDF 转 Word -->
        <el-tab-pane label="PDF → Word" name="pdfToWord">
          <div class="convert-content">
            <div class="upload-section">
              <el-upload
                class="upload-box"
                drag
                :auto-upload="false"
                :show-file-list="false"
                accept=".pdf"
                :on-change="handleFileChange"
              >
                <div v-if="selectedFile" class="file-info">
                  <el-icon size="40" color="#409eff"><Document /></el-icon>
                  <div class="file-name">{{ selectedFile.name }}</div>
                  <div class="file-size">{{ formatSize(selectedFile.size) }}</div>
                </div>
                <div v-else class="upload-placeholder">
                  <el-icon size="48"><UploadFilled /></el-icon>
                  <div class="upload-text">将 PDF 文件拖到此处，或<em>点击上传</em></div>
                  <div class="upload-tip">支持 .pdf 格式，最大 50MB</div>
                </div>
              </el-upload>

              <div class="action-bar">
                <el-button
                  type="primary"
                  size="large"
                  :loading="converting"
                  :disabled="!selectedFile"
                  @click="doConvert"
                >
                  {{ converting ? '转换中...' : '开始转换' }}
                </el-button>
                <el-button v-if="selectedFile" @click="clearFile">清除文件</el-button>
              </div>
            </div>

            <!-- 转换结果 -->
            <div v-if="convertResult" class="result-section">
              <el-divider content-position="left">转换结果</el-divider>
              <el-descriptions :column="2" border>
                <el-descriptions-item label="源文件">
                  {{ convertResult.sourceFile.fileName }}
                </el-descriptions-item>
                <el-descriptions-item label="源文件大小">
                  {{ formatSize(convertResult.sourceFile.fileSize) }}
                </el-descriptions-item>
                <el-descriptions-item label="转换后文件">
                  {{ convertResult.targetFile.fileName }}
                </el-descriptions-item>
                <el-descriptions-item label="转换后大小">
                  {{ formatSize(convertResult.targetFile.fileSize) }}
                </el-descriptions-item>
              </el-descriptions>
              <div class="download-bar">
                <el-button type="success" @click="downloadResult">
                  <el-icon><Download /></el-icon> 下载 Word 文件
                </el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- 预留：后续扩展其他转换类型 -->
        <el-tab-pane label="更多格式" name="more" disabled>
          <div class="coming-soon">更多转换格式即将上线...</div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Document, UploadFilled, Download } from '@element-plus/icons-vue'
import { pdfToWord } from '@/api/fileConvert'

const activeType = ref('pdfToWord')
const selectedFile = ref(null)
const converting = ref(false)
const convertResult = ref(null)

function handleFileChange(uploadFile) {
  const file = uploadFile.raw
  if (!file.name.toLowerCase().endsWith('.pdf')) {
    ElMessage.error('请选择 PDF 文件')
    return
  }
  if (file.size > 50 * 1024 * 1024) {
    ElMessage.error('文件大小不能超过 50MB')
    return
  }
  selectedFile.value = file
  convertResult.value = null
}

function clearFile() {
  selectedFile.value = null
  convertResult.value = null
}

async function doConvert() {
  if (!selectedFile.value) return
  converting.value = true
  convertResult.value = null
  try {
    const res = await pdfToWord(selectedFile.value)
    convertResult.value = res.data
    ElMessage.success('转换成功')
  } catch (e) {
    ElMessage.error(e.message || '转换失败')
  } finally {
    converting.value = false
  }
}

function downloadResult() {
  if (!convertResult.value?.targetFile?.fileUrl) {
    ElMessage.error('文件链接不可用')
    return
  }
  window.open(convertResult.value.targetFile.fileUrl, '_blank')
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
.file-convert-container {
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
}

.file-name {
  margin-top: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  word-break: break-all;
}

.file-size {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.action-bar {
  margin-top: 20px;
  display: flex;
  gap: 12px;
}

.result-section {
  margin-top: 30px;
}

.download-bar {
  margin-top: 16px;
  text-align: center;
}

.coming-soon {
  text-align: center;
  padding: 60px 0;
  color: #909399;
  font-size: 14px;
}
</style>
