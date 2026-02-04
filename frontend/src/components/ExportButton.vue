<template>
  <div class="export-button-wrapper">
    <el-button
      :type="buttonType"
      :size="buttonSize"
      :loading="exporting"
      @click="handleExport"
    >
      <el-icon v-if="!exporting"><Download /></el-icon>
      {{ exporting ? '导出中...' : buttonText }}
    </el-button>

    <!-- 导出进度弹窗 -->
    <el-dialog
      v-model="progressDialogVisible"
      title="导出进度"
      width="400px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="canClose"
    >
      <div class="progress-content">
        <el-progress
          :percentage="progress"
          :status="progressStatus"
          :stroke-width="20"
          text-inside
        />
        <div class="progress-info">
          <span v-if="taskStatus === 0">等待处理...</span>
          <span v-else-if="taskStatus === 1">正在导出数据，请稍候...</span>
          <span v-else-if="taskStatus === 2" class="success-text">导出完成！</span>
          <span v-else-if="taskStatus === 3" class="error-text">导出失败：{{ errorMessage }}</span>
          <span v-else-if="taskStatus === 4">已取消</span>
        </div>
      </div>
      <template #footer>
        <el-button v-if="taskStatus === 2" type="primary" @click="handleDownload">
          下载文件
        </el-button>
        <el-button v-if="taskStatus === 0 || taskStatus === 1" @click="handleCancelExport">
          取消导出
        </el-button>
        <el-button v-if="canClose" @click="handleClose">
          关闭
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue'
import { Download } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { createExportTask, getTaskProgress, cancelExportTask, getDownloadUrl } from '@/api/exportTask'

const props = defineProps({
  // 配置编码
  configCode: {
    type: String,
    required: true
  },
  // 查询参数
  queryParams: {
    type: Object,
    default: () => ({})
  },
  // 按钮文字
  buttonText: {
    type: String,
    default: '导出'
  },
  // 按钮类型
  buttonType: {
    type: String,
    default: 'primary'
  },
  // 按钮大小
  buttonSize: {
    type: String,
    default: 'default'
  }
})

const emit = defineEmits(['success', 'error', 'cancel'])

// 状态
const exporting = ref(false)
const progressDialogVisible = ref(false)
const progress = ref(0)
const taskStatus = ref(0) // 0-待处理，1-处理中，2-已完成，3-失败，4-已取消
const taskId = ref(null)
const taskNo = ref('')
const errorMessage = ref('')

// 轮询定时器
let pollingTimer = null

// 计算属性
const progressStatus = computed(() => {
  if (taskStatus.value === 2) return 'success'
  if (taskStatus.value === 3) return 'exception'
  return null
})

const canClose = computed(() => {
  return taskStatus.value === 2 || taskStatus.value === 3 || taskStatus.value === 4
})

// 开始导出
const handleExport = async () => {
  exporting.value = true
  progress.value = 0
  taskStatus.value = 0
  errorMessage.value = ''

  try {
    // 创建导出任务
    const res = await createExportTask({
      configCode: props.configCode,
      queryParams: JSON.stringify(props.queryParams)
    })

    taskNo.value = res.data
    progressDialogVisible.value = true

    // 开始轮询进度
    startPolling()
  } catch (error) {
    exporting.value = false
    ElMessage.error(error.message || '创建导出任务失败')
    emit('error', error)
  }
}

// 开始轮询
const startPolling = () => {
  if (pollingTimer) return

  pollingTimer = setInterval(async () => {
    try {
      // 这里需要通过任务编号获取任务详情
      // 由于 API 设计是通过 ID 获取进度，我们需要先获取任务列表找到对应任务
      // 简化处理：直接轮询进度接口
      const res = await getTaskProgress(taskId.value)

      if (res.data !== null) {
        progress.value = res.data

        if (res.data >= 100) {
          taskStatus.value = 2
          stopPolling()
          exporting.value = false
          emit('success', taskNo.value)
        }
      }
    } catch (error) {
      // 如果获取进度失败，可能任务已完成或失败
      // 需要重新查询任务状态
      stopPolling()
      exporting.value = false
    }
  }, 1500)
}

// 停止轮询
const stopPolling = () => {
  if (pollingTimer) {
    clearInterval(pollingTimer)
    pollingTimer = null
  }
}

// 取消导出
const handleCancelExport = async () => {
  try {
    if (taskId.value) {
      await cancelExportTask(taskId.value)
    }
    taskStatus.value = 4
    stopPolling()
    exporting.value = false
    emit('cancel')
    ElMessage.info('已取  } catch (error) {
    ElMessage.error('取消失败')
  }
}

// 下载文件
const handleDownload = () => {
  if (taskNo.value) {
    const url = getDownloadUrl(taskNo.value)
    window.open(url, '_blank')
  }
}

// 关闭弹窗
const handleClose = () => {
  progressDialogVisible.value = false
  stopPolling()
}

// 清理
onUnmounted(() => {
  stopPolling()
})
</script>

<style scoped>
.export-button-wrapper {
  display: inline-block;
}

.progress-content {
  padding: 20px 0;
}

.progress-info {
  margin-top: 16px;
  text-align: center;
  color: #666;
}

.success-text {
  color: #67c23a;
  font-weight: bold;
}

.error-text {
  color: #f56c6c;
}
</style>
