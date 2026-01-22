<template>
  <el-dropdown trigger="click" @command="handleCommand">
    <div class="notice-dropdown">
      <el-badge :value="unreadCount" :hidden="unreadCount === 0" :max="99">
        <el-icon :size="20">
          <Bell />
        </el-icon>
      </el-badge>
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <div class="notice-dropdown-header">
          <span>通知消息</span>
          <el-link v-if="unreadCount > 0" type="primary" underline="never" @click="handleViewAll">
            查看全部
          </el-link>
        </div>
        <el-divider style="margin: 0" />
        <div v-loading="loading" class="notice-dropdown-body">
          <el-empty v-if="noticeList.length === 0" description="暂无通知" :image-size="60" />
          <div v-else class="notice-list">
            <div
              v-for="notice in noticeList"
              :key="notice.id"
              :class="['notice-item', { 'notice-unread': !notice.isRead }]"
              @click="handleNoticeClick(notice)"
            >
              <div class="notice-item-header">
                <el-badge v-if="!notice.isRead" is-dot />
                <el-tag
                  :type="notice.level === 3 ? 'danger' : notice.level === 2 ? 'warning' : 'info'"
                  size="small"
                  style="margin-left: 5px"
                >
                  {{ notice.levelName }}
                </el-tag>
                <span class="notice-item-time">{{ formatTime(notice.publishTime) }}</span>
              </div>
              <div class="notice-item-title">{{ notice.title }}</div>
              <div class="notice-item-content">{{ getContentPreview(notice.content) }}</div>
            </div>
          </div>
        </div>
        <el-divider style="margin: 0" />
        <div class="notice-dropdown-footer">
          <el-link type="primary" underline="never" @click="handleViewAll">
            查看全部通知
          </el-link>
        </div>
      </el-dropdown-menu>
    </template>
  </el-dropdown>

  <!-- 通知详情对话框 -->
  <el-dialog
    v-model="detailVisible"
    title="通知详情"
    width="700px"
    @close="handleDetailClose"
  >
    <el-descriptions :column="2" border>
      <el-descriptions-item label="标题" :span="2">
        {{ detailData.title }}
      </el-descriptions-item>
      <el-descriptions-item label="类型">
        <el-tag :type="detailData.type === 1 ? 'primary' : 'su'">
          {{ detailData.typeName }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="级别">
        <el-tag :type="getLevelType(detailData.level)">
          {{ detailData.levelName }}
        </el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="发布人">
        {{ detailData.publisher }}
      </el-descriptions-item>
      <el-descriptions-item label="发布时间">
        {{ detailData.publishTime }}
      </el-descriptions-item>
      <el-descriptions-item label="内容" :span="2">
        <div class="notice-detail-content">{{ detailData.content }}</div>
      </el-descriptions-item>
      <el-descriptions-item v-if="detailData.remark" label="备注" :span="2">
        {{ detailData.remark }}
      </el-descriptions-item>
    </el-descriptions>
    <template #footer>
      <el-button @click="detailVisible = false">关闭</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Bell } from '@element-plus/icons-vue'
import { getMyNotices, getUnreadCount, markAsRead, getNoticeById } from '@/api/notice'

const router = useRouter()

// 未读数量
const unreadCount = ref(0)

// 通知列表
const noticeList = ref([])
const loading = ref(false)

// 详情对话框
const detailVisible = ref(false)
const detailData = ref({})

// 定时器
let timer = null

// 获取未读数量
const fetchUnreadCount = async () => {
  try {
    const { data } = await getUnreadCount()
    unreadCount.value = data
  } catch (error) {
    console.error('获取未读数量失败', error)
  }
}

// 获取最新通知列表
const fetchNoticeList = async () => {
  loading.value = true
  try {
    const { data } = await getMyNotices({
      current: 1,
      size: 5
    })
    noticeList.value = data.records
  } catch (error) {
    console.error('获取通知列表失败', error)
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const now = new Date()
  const noticeTime = new Date(time)
  const diff = now - noticeTime
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes}分钟前`
  if (hours < 24) return `${hours}小时前`
  if (days < 7) return `${days}天前`
  return time
}

// 获取内容预览
const getContentPreview = (content) => {
  if (!content) return ''
  return content.length > 50 ? content.substring(0, 50) + '...' : content
}

// 获取级别类型
const getLevelType = (level) => {
  const typeMap = {
    1: 'info',
    2: 'warning',
    3: 'danger'
  }
  return typeMap[level] || 'info'
}

// 点击通知
const handleNoticeClick = async (notice) => {
  try {
    // 如果未读，标记为已读
    if (!notice.isRead) {
      await markAsRead(notice.id)
      notice.isRead = true
      await fetchUnreadCount()
    }

    // 获取详情
    const { data } = await getNoticeById(notice.id)
    detailData.value = data
    detailVisible.value = true
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 查看全部
const handleViewAll = () => {
  router.push('/system/my-notice')
}

// 关闭详情对话框
const handleDetailClose = () => {
  detailData.value = {}
  // 刷新列表
  fetchNoticeList()
}

// 下拉菜单命令处理
const handleCommand = (command) => {
  if (command === 'viewAll') {
    handleViewAll()
  }
}

// 监听通知已读事件
const handleNoticeRead = () => {
  fetchUnreadCount()
  fetchNoticeList()
}

// 初始化
onMounted(() => {
  fetchUnreadCount()
  fetchNoticeList()

  // 每30秒刷新一次未读数量
  timer = setInterval(() => {
    fetchUnreadCount()
  }, 30000)

  // 监听通知已读事件
  window.addEventListener('notice-read', handleNoticeRead)
})

// 清理
onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
  window.removeEventListener('notice-read', handleNoticeRead)
})
</script>

<style scoped>
.notice-dropdown {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  cursor: pointer;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.notice-dropdown:hover {
  background-color: rgba(0, 0, 0, 0.05);
}

.notice-dropdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.notice-dropdown-body {
  max-height: 400px;
  overflow-y: auto;
  min-width: 360px;
}

.notice-list {
  padding: 8px 0;
}

.notice-item {
  padding: 12px 16px;
  cursor: pointer;
  transition: background-color 0.3s;
  border-bottom: 1px solid #f0f0f0;
}

.notice-item:last-child {
  border-bottom: none;
}

.notice-item:hover {
  background-color: #f5f7fa;
}

.notice-unread {
  background-color: #f0f9ff;
}

.notice-unread:hover {
  background-color: #e6f4ff;
}

.notice-item-header {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
}

.notice-item-time {
  margin-left: auto;
  font-size: 12px;
  color: #909399;
}

.notice-item-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notice-item-content {
  font-size: 12px;
  color: #606266;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notice-dropdown-footer {
  padding: 12px 16px;
  text-align: center;
}

.notice-detail-content {
  white-space: pre-wrap;
  word-break: break-word;
  line-height: 1.8;
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
</style>
