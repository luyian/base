<template>
  <div class="file-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="文件名">
          <el-input v-model="queryForm.fileName" placeholder="请输入文件名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="文件分组">
          <el-select v-model="queryForm.fileGroup" placeholder="请选择文件分组" clearable style="width: 150px">
            <el-option v-for="item in fileGroups" :key="item" :label="item" :value="item" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="table-card">
      <div class="toolbar">
        <div>
          <el-button v-permission="'file:delete'" type="danger" :disabled="selectedIds.length === 0" @click="handleBatchDelete">批量删除</el-button>
        </div>
        <div>
          <el-upload
            v-permission="'file:upload'"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :data="uploadData"
            :show-file-list="false"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            class="upload-btn"
          >
            <el-button type="primary">上传文件</el-button>
          </el-upload>
        </div>
      </div>

      <!-- 表格 -->
      <el-table
        :data="tableData"
        style="width: 100%; margin-top: 20px"
        @selection-change="handleSelectionChange"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="originalName" label="文件名" min-width="200" show-overflow-tooltip />
        <el-table-column prop="fileExt" label="扩展名" width="100" />
        <el-table-column prop="fileSize" label="大小" width="120">
          <template #default="{ row }">
            {{ formatSize(row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="fileGroup" label="分组" width="120" />
        <el-table-column prop="uploadUserName" label="上传人" width="120" />
        <el-table-column prop="createTime" label="上传时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleDownload(row)">下载</el-button>
            <el-button link type="primary" @click="handlePreview(row)">预览</el-button>
            <el-button link type="primary" @click="handleCopyUrl(row)">复制URL</el-button>
            <el-button v-permission="'file:delete'" link type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 20px"
        @size-change="handleQuery"
        @current-change="handleQuery"
      />
    </el-card>

    <!-- 预览对话框 -->
    <el-dialog v-model="previewVisible" title="文件预览" width="60%">
      <div v-if="previewUrl" class="preview-content">
        <img v-if="isImage(previewFile)" :src="previewUrl" style="max-width: 100%" />
        <iframe v-else-if="isPdf(previewFile)" :src="previewUrl" style="width: 100%; height: 500px" />
        <div v-else class="no-preview">该文件类型暂不支持预览</div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageFiles, deleteFile, batchDeleteFiles, getFileGroups, getFileUrl } from '@/api/file'

const queryForm = ref({
  pageNum: 1,
  pageSize: 10,
  fileName: '',
  fileGroup: ''
})

const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref([])
const fileGroups = ref([])

// 上传相关
const uploadUrl = computed(() => import.meta.env.VITE_BASE_URL + '/system/file/upload')
const uploadHeaders = computed(() => ({ Authorization: 'Bearer ' + (localStorage.getItem('token') || '') }))
const uploadData = computed(() => ({ fileGroup: 'default', fileDesc: '' }))

// 预览相关
const previewVisible = ref(false)
const previewUrl = ref('')
const previewFile = ref(null)

onMounted(() => {
  loadFileGroups()
  handleQuery()
})

async function loadFileGroups() {
  try {
    const res = await getFileGroups()
    if (res.data) {
      fileGroups.value = res.data
    }
  } catch (e) {
    fileGroups.value = ['default', 'images', 'documents', 'videos', 'audio', 'others']
  }
}

async function handleQuery() {
  loading.value = true
  try {
    const res = await pageFiles(queryForm.value)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (e) {
    ElMessage.error(e.message || '查询失败')
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryForm.value = {
    pageNum: 1,
    pageSize: 10,
    fileName: '',
    fileGroup: ''
  }
  handleQuery()
}

function handleSelectionChange(selection) {
  selectedIds.value = selection.map(item => item.id)
}

async function handleDelete(id) {
  try {
    await ElMessageBox.confirm('确定要删除该文件吗？', '提示', { type: 'warning' })
    await deleteFile(id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

async function handleBatchDelete() {
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedIds.value.length} 个文件吗？`, '提示', { type: 'warning' })
    await batchDeleteFiles(selectedIds.value)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (e) {
    if (e !== 'cancel') {
      ElMessage.error(e.message || '删除失败')
    }
  }
}

function beforeUpload(file) {
  const isLt100M = file.size / 1024 / 1024 < 100
  if (!isLt100M) {
    ElMessage.error('文件大小不能超过 100MB')
    return false
  }
  return true
}

function handleUploadSuccess(res) {
  if (res.code === 200) {
    ElMessage.success('上传成功')
    handleQuery()
  } else {
    ElMessage.error(res.message || '上传失败')
  }
}

function handleUploadError(e) {
  ElMessage.error(e.message || '上传失败')
}

async function handleDownload(row) {
  try {
    const res = await getFileUrl(row.id)
    if (res.data && res.data.url) {
      window.open(res.data.url, '_blank')
    }
  } catch (e) {
    ElMessage.error('获取下载链接失败')
  }
}

async function handlePreview(row) {
  try {
    const res = await getFileUrl(row.id)
    if (res.data && res.data.url) {
      previewUrl.value = res.data.url
      previewFile.value = row
      previewVisible.value = true
    }
  } catch (e) {
    ElMessage.error('获取预览链接失败')
  }
}

async function handleCopyUrl(row) {
  try {
    const res = await getFileUrl(row.id)
    if (res.data && res.data.url) {
      await navigator.clipboard.writeText(res.data.url)
      ElMessage.success('URL已复制到剪贴板')
    }
  } catch (e) {
    ElMessage.error('复制失败')
  }
}

function formatSize(size) {
  if (!size) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let i = 0
  while (size >= 1024 && i < units.length - 1) {
    size /= 1024
    i++
  }
  return size.toFixed(2) + ' ' + units[i]
}

function isImage(file) {
  if (!file || !file.fileType) return false
  return file.fileType.startsWith('image/')
}

function isPdf(file) {
  if (!file || !file.fileType) return false
  return file.fileType === 'application/pdf'
}
</script>

<style scoped>
.file-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.upload-btn {
  display: inline-block;
}

.preview-content {
  text-align: center;
  min-height: 300px;
}

.no-preview {
  padding: 50px;
  color: #999;
}
</style>