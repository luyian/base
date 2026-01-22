<template>
  <div class="my-notice-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="标题">
          <el-input v-model="queryForm.title" placeholder="请输入标题" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="queryForm.type" placeholder="请选择类型" clearable style="width: 150px">
            <el-option label="通知" :value="1" />
            <el-option label="公告" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="级别">
          <el-select v-model="queryForm.level" placeholder="请选择级别" clearable style="width: 150px">
            <el-option label="普通" :value="1" />
            <el-option label="重要" :value="2" />
            <el-option label="紧急" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 通知列表 -->
    <el-card class="list-card">
      <el-empty v-if="noticeList.length === 0 && !loading" description="暂无通知公告" />

      <div v-loading="loading" class="notice-list">
        <div
          v-for="notice in noticeList"
          :key="notice.id"
          :class="['notice-item', { 'notice-unread': !notice.isRead }]"
          @click="handleView(notice)"
        >
          <div class="notice-header">
            <div class="notice-title">
              <el-badge v-if="!notice.isRead" is-dot class="unread-badge" />
              <el-tag
                :type="notice.level === 3 ? 'danger' : notice.level === 2 ? 'warning' : 'info'"
                size="small"
                style="margin-right: 10px"
              >
                {{ notice.levelName }}
              </el-tag>
              <el-tag
                :type="notice.type === 1 ? 'primary' : 'success'"
                size="small"
                style="margin-right: 10px"
              >
                {{ notice.typeName }}
              </el-tag>
              <span :class="['title-text', { 'title-unread': !notice.isRead }]">{{ notice.title }}</span>
            </div>
            <div class="notice-time">{{ notice.publishTime }}</div>
          </div>
          <div class="notice-content">{{ getContentPreview(notice.content) }}</div>
        </div>
      </div>

      <el-pagination
        v-if="noticeList.length > 0"
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 查看详情对话框 -->
    <el-dialog
      v-model="viewDialogVisible"
      title="通知公告详情"
      width="800px"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="标题" :span="2">{{ viewData.title }}</el-descriptions-item>
        <el-descriptions-item label="类型">
          <el-tag :type="viewData.type === 1 ? 'primary' : 'success'">{{ viewData.typeName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="级别">
          <el-tag :type="getLevelType(viewData.level)">{{ viewData.levelName }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布时间" :span="2">{{ viewData.publishTime }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">
          <div style="white-space: pre-wrap; max-height: 400px; overflow-y: auto; padding: 10px; background-color: #f5f7fa; border-radius: 4px;">{{ viewData.content }}</div>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyNotices, getNoticeById, markAsRead } from '@/api/notice'

// 查询表单
const queryForm = reactive({
  title: '',
  type: null,
  level: null,
  current: 1,
  size: 10
})

// 通知列表
const noticeList = ref([])
const total = ref(0)
const loading = ref(false)

// 查看详情
const viewDialogVisible = ref(false)
const viewData = ref({})

// 获取级别标签类型
const getLevelType = (level) => {
  const typeMap = {
    1: 'info',
    2: 'warning',
    3: 'danger'
  }
  return typeMap[level] || 'info'
}

// 获取内容预览
const getContentPreview = (content) => {
  if (!content) return ''
  return content.length > 100 ? content.substring(0, 100) + '...' : content
}

// 查询
const handleQuery = async () => {
  loading.value = true
  try {
    const res = await getMyNotices(queryForm)
    noticeList.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 重置
const handleReset = () => {
  queryForm.title = ''
  queryForm.type = null
  queryForm.level = null
  queryForm.current = 1
  handleQuery()
}

// 查看详情
const handleView = async (notice) => {
  try {
    // 如果未读，先标记为已读
    if (!notice.isRead) {
      await markAsRead(notice.id)
      // 更新列表中的已读状态
      notice.isRead = true
    }

    const res = await getNoticeById(notice.id)
    viewData.value = res.data
    viewDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取详情失败')
  }
}

// 初始化
onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.my-notice-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.list-card {
  margin-bottom: 20px;
}

.notice-list {
  min-height: 400px;
}

.notice-item {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background-color 0.3s;
}

.notice-item:hover {
  background-color: #f5f7fa;
}

.notice-item:last-child {
  border-bottom: none;
}

/* 未读通知样式 */
.notice-unread {
  background-color: #f0f9ff;
}

.notice-unread:hover {
  background-color: #e6f4ff;
}

.notice-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.notice-title {
  display: flex;
  align-items: center;
  flex: 1;
  position: relative;
}

.unread-badge {
  margin-right: 8px;
}

.unread-badge :deep(.el-badge__content) {
  background-color: #f56c6c;
}

.title-text {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

/* 未读通知标题加粗 */
.title-unread {
  font-weight: 600;
  color: #1890ff;
}

.notice-time {
  font-size: 14px;
  color: #909399;
}

.notice-content {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}
</style>
