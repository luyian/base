<template>
  <div class="doc-detail" v-loading="loading">
    <!-- 顶部 -->
    <div class="detail-header">
      <div class="header-left">
        <el-button :icon="ArrowLeft" circle @click="goBack" />
        <h2>{{ doc?.title || '文档详情' }}</h2>
      </div>
      <div class="header-right">
        <el-button :icon="Download" @click="downloadMd">下载 MD</el-button>
        <div class="toc-toggle" v-if="tocList.length > 0">
          <el-icon><List /></el-icon>
          <span class="toc-label">目录</span>
          <el-switch v-model="showToc" size="small" />
        </div>
        <span class="kb-name" v-if="doc?.knowledgeBaseName">
          <el-icon><Collection /></el-icon> {{ doc.knowledgeBaseName }}
        </span>
      </div>
    </div>

    <div class="detail-main" v-if="doc">
      <!-- 目录大纲侧边栏 -->
      <div class="toc-sidebar" :class="{ collapsed: !showToc }">
        <div class="toc-title">目录大纲</div>
        <div class="toc-list">
          <div
            v-for="item in tocList"
            :key="item.id"
            class="toc-item"
            :class="{ active: activeHeading === item.id }"
            :style="{ paddingLeft: (item.level - 1) * 16 + 12 + 'px' }"
            @click="scrollToHeading(item.id)"
          >
            {{ item.text }}
          </div>
        </div>
      </div>

      <div class="detail-body" ref="detailBodyRef">
      <!-- 标签 -->
      <div class="doc-tags" v-if="doc.tags && doc.tags.filter(Boolean).length > 0">
        <el-tag v-for="tag in doc.tags.filter(Boolean)" :key="tag" size="small" :disable-transitions="true">
          {{ tag }}
        </el-tag>
      </div>

      <!-- 正文 -->
      <div class="doc-content">
        <MdViewer :content="doc.content" @toc="handleTocUpdate" />
      </div>

      <!-- 附件 -->
      <div class="section">
        <div class="section-header">
          <span class="section-title">附件（{{ attachments.length }}）</span>
          <el-button size="small" :icon="Upload" :loading="uploading" @click="triggerUpload">
            上传附件
          </el-button>
          <input ref="fileInputRef" type="file" style="display: none" @change="handleUpload" />
        </div>
        <el-empty v-if="attachments.length === 0" description="暂无附件" :image-size="50" />
        <div class="attach-list" v-else>
          <div class="attach-item" v-for="att in attachments" :key="att.id">
            <el-icon><Document /></el-icon>
            <span class="attach-name">{{ att.fileName }}</span>
            <span class="attach-size">{{ formatSize(att.fileSize) }}</span>
            <el-button link type="primary" size="small" @click="downloadAttach(att)">下载</el-button>
            <el-button link type="danger" size="small" @click="removeAttach(att)">删除</el-button>
          </div>
        </div>
      </div>

      <!-- 评论 -->
      <div class="section">
        <div class="section-header">
          <span class="section-title">评论（{{ commentCount }}）</span>
        </div>
        <!-- 发表评论 -->
        <div class="comment-editor">
          <el-input v-model="newComment" type="textarea" :rows="3" placeholder="写下你的评论..." />
          <el-button type="primary" :disabled="!newComment.trim()" @click="submitComment(0)">
            发表评论
          </el-button>
        </div>
        <!-- 评论列表 -->
        <el-empty v-if="comments.length === 0" description="暂无评论" :image-size="50" />
        <div class="comment-list">
          <div class="comment-item" v-for="c in comments" :key="c.id">
            <el-avatar :size="36" :src="c.commenterAvatar">
              {{ (c.commenterName || '?').charAt(0) }}
            </el-avatar>
            <div class="comment-main">
              <div class="comment-meta">
                <span class="commenter">{{ c.commenterName }}</span>
                <span class="time">{{ c.createTime }}</span>
              </div>
              <div class="comment-text">{{ c.content }}</div>
              <div class="comment-actions">
                <el-button link size="small" @click="toggleReply(c.id)">回复</el-button>
                <el-button v-if="isMine(c)" link size="small" type="danger" @click="removeComment(c)">
                  删除
                </el-button>
              </div>
              <!-- 回复输入框 -->
              <div class="reply-editor" v-if="replyTo === c.id">
                <el-input v-model="replyContent" type="textarea" :rows="2" placeholder="回复..." />
                <div class="reply-btns">
                  <el-button size="small" @click="replyTo = null">取消</el-button>
                  <el-button size="small" type="primary" :disabled="!replyContent.trim()" @click="submitComment(c.id)">
                    回复
                  </el-button>
                </div>
              </div>
              <!-- 一级回复列表 -->
              <div class="reply-list" v-if="c.children && c.children.length > 0">
                <div class="reply-item" v-for="r in c.children" :key="r.id">
                  <el-avatar :size="28" :src="r.commenterAvatar">
                    {{ (r.commenterName || '?').charAt(0) }}
                  </el-avatar>
                  <div class="reply-main">
                    <div class="comment-meta">
                      <span class="commenter">{{ r.commenterName }}</span>
                      <span class="time">{{ r.createTime }}</span>
                    </div>
                    <div class="comment-text">{{ r.content }}</div>
                    <div class="comment-actions" v-if="isMine(r)">
                      <el-button link size="small" type="danger" @click="removeComment(r)">删除</el-button>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft, Document, Upload, Collection, List, Download } from '@element-plus/icons-vue'
import MdViewer from '@/components/MdViewer.vue'
import { useUserStore } from '@/store/user'
import { uploadFile } from '@/api/file'
import {
  getDocumentDetail,
  getAttachmentList,
  bindAttachment,
  deleteAttachment,
  getCommentList,
  addComment,
  deleteComment
} from '@/api/knowledge'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const docId = computed(() => Number(route.params.docId))

const doc = ref(null)
const loading = ref(false)
const attachments = ref([])
const comments = ref([])
const newComment = ref('')
const replyTo = ref(null)
const replyContent = ref('')
const uploading = ref(false)
const fileInputRef = ref(null)

const detailBodyRef = ref(null)
const showToc = ref(true)
const activeHeading = ref(null)
let headingObserver = null

// 目录列表由 MdViewer 组件通过 toc 事件提供
const tocList = ref([])
const handleTocUpdate = (list) => {
  tocList.value = list
}

// 点击 TOC 项滚动到对应标题
const scrollToHeading = (id) => {
  const el = document.getElementById(id)
  if (el) {
    el.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
}

// 监听滚动，高亮当前可视标题
const setupObserver = () => {
  if (headingObserver) {
    headingObserver.disconnect()
  }
  const container = detailBodyRef.value
  if (!container) {
    return
  }
  headingObserver = new IntersectionObserver(
    (entries) => {
      for (const entry of entries) {
        if (entry.isIntersecting) {
          activeHeading.value = entry.target.id
        }
      }
    },
    { root: container, rootMargin: '0px 0px -80% 0px', threshold: 0 }
  )
  tocList.value.forEach((item) => {
    const el = document.getElementById(item.id)
    if (el) {
      headingObserver.observe(el)
    }
  })
}

watch(tocList, () => {
  nextTick(() => setupObserver())
})

// 评论总数（顶级 + 回复）
const commentCount = computed(() =>
  comments.value.reduce((sum, c) => sum + 1 + (c.children ? c.children.length : 0), 0)
)

// 是否本人评论（用于显示删除按钮，后端同样会校验）
const isMine = (c) => {
  const uid = userStore.userInfo?.id
  return uid != null && c.commenterId === uid
}

const formatSize = (size) => {
  if (!size) {
    return ''
  }
  if (size < 1024) {
    return size + ' B'
  }
  if (size < 1024 * 1024) {
    return (size / 1024).toFixed(1) + ' KB'
  }
  return (size / 1024 / 1024).toFixed(1) + ' MB'
}

const goBack = () => {
  router.push('/document-square')
}

// 下载 MD 文件
const downloadMd = () => {
  if (!doc.value || !doc.value.content) {
    ElMessage.warning('文档内容为空，无法下载')
    return
  }
  const blob = new Blob([doc.value.content], { type: 'text/markdown;charset=utf-8' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = (doc.value.title || '文档') + '.md'
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  URL.revokeObjectURL(url)
}

const loadDoc = async () => {
  loading.value = true
  try {
    const res = await getDocumentDetail(docId.value)
    doc.value = res.data
  } catch (error) {
    console.error('获取文档失败:', error)
    ElMessage.error('获取文档失败')
  } finally {
    loading.value = false
  }
}

const loadAttachments = async () => {
  try {
    const res = await getAttachmentList(docId.value)
    attachments.value = res.data || []
  } catch (error) {
    console.error('获取附件失败:', error)
  }
}

const loadComments = async () => {
  try {
    const res = await getCommentList(docId.value)
    comments.value = res.data || []
  } catch (error) {
    console.error('获取评论失败:', error)
  }
}

// 上传附件：先上传到文件服务，再绑定到文档
const triggerUpload = () => {
  fileInputRef.value?.click()
}
const handleUpload = async (event) => {
  const file = event.target.files?.[0]
  if (!file) {
    return
  }
  uploading.value = true
  try {
    const up = await uploadFile(file, 'knowledge')
    const fileId = up.data?.id
    await bindAttachment({ documentId: docId.value, fileId })
    ElMessage.success('上传成功')
    loadAttachments()
  } catch (error) {
    console.error('上传附件失败:', error)
    ElMessage.error('上传失败')
  } finally {
    uploading.value = false
    event.target.value = ''
  }
}

const downloadAttach = (att) => {
  if (att.fileUrl) {
    window.open(att.fileUrl, '_blank')
  } else {
    ElMessage.warning('文件地址不可用')
  }
}

const removeAttach = (att) => {
  ElMessageBox.confirm(`确定删除附件「${att.fileName}」？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteAttachment(att.id)
      ElMessage.success('已删除')
      loadAttachments()
    })
    .catch(() => {})
}

const toggleReply = (id) => {
  replyTo.value = replyTo.value === id ? null : id
  replyContent.value = ''
}

// parentId 为 0 表示顶级评论
const submitComment = async (parentId) => {
  const content = parentId ? replyContent.value : newComment.value
  if (!content.trim()) {
    return
  }
  try {
    await addComment({
      knowledgeBaseId: doc.value.knowledgeBaseId,
      documentId: docId.value,
      parentId: parentId || 0,
      content
    })
    ElMessage.success('评论成功')
    if (parentId) {
      replyTo.value = null
      replyContent.value = ''
    } else {
      newComment.value = ''
    }
    loadComments()
  } catch (error) {
    console.error('评论失败:', error)
    ElMessage.error('评论失败')
  }
}

const removeComment = (c) => {
  ElMessageBox.confirm('确定删除该评论？', '提示', { type: 'warning' })
    .then(async () => {
      try {
        await deleteComment(c.id)
        ElMessage.success('已删除')
        loadComments()
      } catch (error) {
        ElMessage.error(error?.message || '删除失败')
      }
    })
    .catch(() => {})
}

onMounted(async () => {
  await loadDoc()
  await Promise.all([loadAttachments(), loadComments()])
})

onBeforeUnmount(() => {
  if (headingObserver) {
    headingObserver.disconnect()
  }
})
</script>

<style scoped>
.doc-detail {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #f5f7fa;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.header-left h2 {
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.toc-toggle {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #606266;
}

.toc-label {
  white-space: nowrap;
}

.kb-name {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #909399;
}

.detail-main {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.toc-sidebar {
  width: 240px;
  min-width: 240px;
  background: #fff;
  border-right: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  transition: width 0.25s ease, min-width 0.25s ease;
}

.toc-sidebar.collapsed {
  width: 0;
  min-width: 0;
  border-right: none;
}

.toc-title {
  padding: 14px 16px 10px;
  font-size: 13px;
  font-weight: 600;
  color: #909399;
}

.toc-list {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 16px;
}

.toc-item {
  padding: 6px 12px;
  font-size: 13px;
  color: #606266;
  cursor: pointer;
  line-height: 1.5;
  border-left: 2px solid transparent;
  transition: all 0.15s;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.toc-item:hover {
  color: #409eff;
  background: #f5f7fa;
}

.toc-item.active {
  color: #409eff;
  border-left-color: #409eff;
  background: #ecf5ff;
}

.detail-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  max-width: 1200px;
  width: 100%;
  margin: 0 auto;
}

.doc-tags {
  display: flex;
  gap: 6px;
  flex-wrap: wrap;
  margin-bottom: 16px;
}

.doc-content {
  background: #fff;
  border-radius: 6px;
  padding: 24px;
  margin-bottom: 20px;
}

.section {
  background: #fff;
  border-radius: 6px;
  padding: 16px 24px;
  margin-bottom: 20px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
}

.attach-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.attach-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f5f7fa;
  border-radius: 4px;
}

.attach-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.attach-size {
  font-size: 12px;
  color: #909399;
}

.comment-editor {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 8px;
  margin-bottom: 20px;
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.comment-item {
  display: flex;
  gap: 12px;
}

.comment-main {
  flex: 1;
}

.comment-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 4px;
}

.commenter {
  font-weight: 600;
  font-size: 14px;
}

.time {
  font-size: 12px;
  color: #c0c4cc;
}

.comment-text {
  font-size: 14px;
  color: #303133;
  line-height: 1.6;
  white-space: pre-wrap;
}

.comment-actions {
  margin-top: 4px;
}

.reply-editor {
  margin-top: 8px;
}

.reply-btns {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  margin-top: 6px;
}

.reply-list {
  margin-top: 12px;
  padding-left: 16px;
  border-left: 2px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.reply-item {
  display: flex;
  gap: 10px;
}

.reply-main {
  flex: 1;
}

/* ==================== 暗色主题 ==================== */
[data-theme="dark"] .doc-detail {
  background: var(--dk-bg-1);
}

[data-theme="dark"] .detail-header {
  background: var(--dk-bg-2);
  border-bottom-color: var(--dk-border);
}

[data-theme="dark"] .header-left h2 {
  color: var(--dk-text-1);
}

[data-theme="dark"] .toc-toggle {
  color: var(--dk-text-2);
}

[data-theme="dark"] .kb-name {
  color: var(--dk-text-3);
}

[data-theme="dark"] .toc-sidebar {
  background: var(--dk-bg-2);
  border-right-color: var(--dk-border);
}

[data-theme="dark"] .toc-title {
  color: var(--dk-text-3);
}

[data-theme="dark"] .toc-item {
  color: var(--dk-text-2);
}

[data-theme="dark"] .toc-item:hover {
  color: #62b2ff;
  background: var(--dk-bg-3);
}

[data-theme="dark"] .toc-item.active {
  color: #62b2ff;
  border-left-color: #409eff;
  background: rgba(64, 158, 255, 0.12);
}

[data-theme="dark"] .doc-content {
  background: var(--dk-bg-2);
}

[data-theme="dark"] .section {
  background: var(--dk-bg-2);
}

[data-theme="dark"] .section-title {
  color: var(--dk-text-1);
}

[data-theme="dark"] .attach-item {
  background: var(--dk-bg-3);
}

[data-theme="dark"] .attach-name {
  color: var(--dk-text-1);
}

[data-theme="dark"] .attach-size {
  color: var(--dk-text-3);
}

[data-theme="dark"] .comment-text {
  color: var(--dk-text-1);
}

[data-theme="dark"] .commenter {
  color: var(--dk-text-1);
}

[data-theme="dark"] .time {
  color: var(--dk-text-3);
}

[data-theme="dark"] .reply-list {
  border-left-color: var(--dk-border);
}
</style>
