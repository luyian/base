<template>
  <div class="branch-page">
    <!-- 顶部状态栏 -->
    <div class="status-bar">
      <div class="prod-indicator">
        <span class="indicator-dot"></span>
        <span class="indicator-label">PRODUCTION</span>
        <span class="indicator-value">{{ currentProdBranch || '—' }}</span>
        <button
          class="btn-update"
          @click="handleEditProdBranch"
        >
          更新
        </button>
      </div>
      <div class="toolbar">
        <button class="btn-theme" @click="toggleTheme">
          <el-icon v-if="isDark"><Sunny /></el-icon>
          <el-icon v-else><Moon /></el-icon>
        </button>
        <button class="btn-status" :class="{ active: showCompleted }" @click="toggleStatus">
          {{ showCompleted ? '已完成' : '进行中' }}
        </button>
        <el-input
          v-model="queryForm.keyword"
          placeholder="搜索标题或编号..."
          clearable
          class="search-input"
          @clear="handleQuery"
          @keyup.enter="handleQuery"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <button
          class="btn-add"
          @click="handleAdd"
        >
          <el-icon><Plus /></el-icon>
          新增记录
        </button>
      </div>
    </div>

    <!-- 统计面板 -->
    <div class="stats-panel">
      <div class="stat-item">
        <span class="stat-value">{{ stats.total || 0 }}</span>
        <span class="stat-label">总记录</span>
      </div>
      <div class="stat-item stat-pending">
        <span class="stat-value">{{ stats.pendingCount || 0 }}</span>
        <span class="stat-label">待上线</span>
      </div>
      <div class="stat-item stat-week">
        <span class="stat-value">{{ stats.thisWeekCount || 0 }}</span>
        <span class="stat-label">本周上线</span>
      </div>
      <div class="stat-item stat-urgent">
        <span class="stat-value">{{ stats.urgentCount || 0 }}</span>
        <span class="stat-label">紧急</span>
      </div>
      <div class="stat-item stat-critical">
        <span class="stat-value">{{ stats.criticalCount || 0 }}</span>
        <span class="stat-label">特急</span>
      </div>
    </div>

    <!-- 卡片网格 -->
    <div class="card-grid" v-loading="loading">
      <div
        class="branch-card"
        v-for="item in branchList"
        :key="item.id"
        :class="{ 'is-overdue': isOverdue(item.onlineTime), 'is-today': isToday(item.onlineTime) }"
      >
        <span class="card-code">{{ item.code }}</span>
        <span class="priority-tag" :class="getPriorityClass(item.priority)">
          {{ getPriorityText(item.priority) }}
        </span>
        <a
          v-if="item.prdLink"
          class="card-title prd-link"
          :href="item.prdLink"
          target="_blank"
          @click.stop
        >
          {{ item.title }}
        </a>
        <span v-else class="card-title">{{ item.title }}</span>
        <code class="dev-branch" @click="handleCopyBranch(item.devBranch)">
          {{ item.devBranch }}
          <span class="copy-hint">复制</span>
        </code>
        <code class="prod-branch">{{ item.prodBranch }}</code>
        <span class="online-date">{{ item.onlineTime }}</span>
        <span class="card-status" :class="getStatusClass(item.onlineTime)">
          {{ getStatusText(item.onlineTime) }}
        </span>
        <span v-if="item.status === 1" class="completed-tag">已完成</span>
        <el-dropdown trigger="click">
          <span class="card-more">⋯</span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item v-if="item.status !== 1" @click="handleComplete(item)">完成</el-dropdown-item>
              <el-dropdown-item @click="handleEdit(item)">编辑</el-dropdown-item>
              <el-dropdown-item @click="handleDelete(item)" divided>删除</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-if="!loading && branchList.length === 0">
      <p>暂无分支记录</p>
    </div>

    <!-- 加载更多提示 -->
    <div class="load-more" v-if="branchList.length > 0">
      <span v-if="loadingMore">加载中...</span>
      <span v-else-if="noMore" class="no-more">没有更多了</span>
    </div>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑分支记录' : '新增分支记录'"
      width="480px"
      destroy-on-close
      class="branch-dialog"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="编号" prop="code">
          <el-input v-model="form.code" placeholder="如 FEAT-001" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="需求简述" />
        </el-form-item>
        <el-form-item label="PRD链接" prop="prdLink">
          <el-input v-model="form.prdLink" placeholder="https://..." />
        </el-form-item>
        <el-form-item label="上线时间" prop="onlineTime">
          <el-date-picker
            v-model="form.onlineTime"
            type="date"
            placeholder="选择上线日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="紧急程度" prop="priority">
          <el-radio-group v-model="form.priority">
            <el-radio :label="0">普通</el-radio>
            <el-radio :label="1">紧急</el-radio>
            <el-radio :label="2">特急</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="开发分支" prop="devBranch">
          <el-input v-model="form.devBranch" placeholder="留空则自动生成" />
          <div class="form-hint" v-if="!isEdit && !form.devBranch">
            默认：{{ previewDevBranch || 'dev_from_{prod}_{code}' }}
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 更新生产分支弹窗 -->
    <el-dialog v-model="prodDialogVisible" title="更新生产分支" width="400px" class="branch-dialog">
      <el-form label-width="90px">
        <el-form-item label="分支名称">
          <el-input v-model="newProdBranch" placeholder="输入新的生产分支名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="prodDialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="prodUpdating" @click="handleSubmitProdBranch">确定</el-button>
      </template>
    </el-dialog>

    <!-- 访问码验证弹窗 -->
    <el-dialog
      v-model="accessDialogVisible"
      title="访问验证"
      width="380px"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="false"
      class="branch-dialog"
    >
      <div class="access-hint">请输入访问码以使用分支管理功能</div>
      <el-input
        v-model="accessCode"
        placeholder="输入访问码"
        show-password
        @keyup.enter="handleVerifyAccess"
      />
      <template #footer>
        <el-button type="primary" :loading="accessVerifying" @click="handleVerifyAccess">验证</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Calendar, Sunny, Moon } from '@element-plus/icons-vue'
import {
  pageBranches,
  addBranch,
  updateBranch,
  deleteBranch,
  getCurrentProdBranch,
  updateCurrentProdBranch,
  getBranchStats,
  completeBranch,
  onDevTokenExpired
} from '@/api/branch'

// 访问码验证
const accessDialogVisible = ref(false)
const accessCode = ref('')
const accessVerifying = ref(false)
// 已登录用户（有 JWT）或已输入过访问码的用户，均视为已验证
const accessVerified = ref(!!localStorage.getItem('token') || !!localStorage.getItem('dev_access_token'))

// 监听访问码过期事件，重新弹出验证框
onDevTokenExpired(() => {
  accessVerified.value = false
  accessCode.value = ''
  accessDialogVisible.value = true
  ElMessage.warning('访问码已失效，请重新输入')
})

async function handleVerifyAccess() {
  if (!accessCode.value.trim()) {
    ElMessage.warning('请输入访问码')
    return
  }
  accessVerifying.value = true
  try {
    // 将访问码存入 localStorage，尝试请求验证
    localStorage.setItem('dev_access_token', accessCode.value.trim())
    await getBranchStats()
    // 请求成功说明验证通过
    accessVerified.value = true
    accessDialogVisible.value = false
    ElMessage.success('验证通过')
    initPageData()
  } catch (e) {
    // 验证失败，清除存储
    localStorage.removeItem('dev_access_token')
    if (e.response && e.response.status === 403) {
      ElMessage.error('访问码无效')
    } else {
      ElMessage.error('验证失败，请稍后重试')
    }
  } finally {
    accessVerifying.value = false
  }
}

// 当前生产分支
const currentProdBranch = ref('')
const loading = ref(false)
const loadingMore = ref(false)
const noMore = ref(false)
const total = ref(0)
const branchList = ref([])
const stats = ref({})
const isDark = ref(sessionStorage.getItem('branch-theme') === 'dark' || (!sessionStorage.getItem('branch-theme') && document.documentElement.getAttribute('data-theme') === 'dark'))
const showCompleted = ref(false)

if (isDark.value) {
  document.documentElement.setAttribute('data-theme', 'dark')
}

function toggleTheme() {
  isDark.value = !isDark.value
  const theme = isDark.value ? 'dark' : 'light'
  document.documentElement.setAttribute('data-theme', theme)
  sessionStorage.setItem('branch-theme', theme)
}

function toggleStatus() {
  showCompleted.value = !showCompleted.value
  resetList()
}

// 查询表单
const queryForm = reactive({
  current: 1,
  size: 20,
  keyword: '',
  status: 0
})

// 弹窗相关
const dialogVisible = ref(false)
const isEdit = ref(false)
const submitting = ref(false)
const formRef = ref(null)
const form = reactive({
  id: null,
  code: '',
  title: '',
  prdLink: '',
  onlineTime: '',
  priority: 0,
  devBranch: ''
})

const rules = {
  code: [{ required: true, message: '请输入编号', trigger: 'blur' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  onlineTime: [{ required: true, message: '请选择上线时间', trigger: 'change' }]
}

// 生产分支更新弹窗
const prodDialogVisible = ref(false)
const newProdBranch = ref('')
const prodUpdating = ref(false)

// 预览开发分支名
const previewDevBranch = computed(() => {
  if (!form.code) return ''
  return `dev_from_${currentProdBranch.value || 'master'}_${form.code}`
})

// 判断上线状态
function isOverdue(onlineTime) {
  if (!onlineTime) return false
  return new Date(onlineTime) < new Date(new Date().toDateString())
}

function isToday(onlineTime) {
  if (!onlineTime) return false
  return new Date(onlineTime).toDateString() === new Date().toDateString()
}

function getStatusClass(onlineTime) {
  if (isToday(onlineTime)) return 'status-today'
  if (isOverdue(onlineTime)) return 'status-done'
  return 'status-pending'
}

function getStatusText(onlineTime) {
  if (isToday(onlineTime)) return '今日上线'
  if (isOverdue(onlineTime)) return '已上线'
  return '待上线'
}

// 紧急程度
function getPriorityClass(priority) {
  const map = { 0: 'priority-normal', 1: 'priority-urgent', 2: 'priority-critical' }
  return map[priority] || 'priority-normal'
}

function getPriorityText(priority) {
  const map = { 0: '普通', 1: '紧急', 2: '特急' }
  return map[priority] || '普通'
}

// 数据加载
async function fetchList() {
  loading.value = true
  try {
    const params = { ...queryForm, status: showCompleted.value ? 1 : 0 }
    const res = await pageBranches(params)
    branchList.value = res.data.records || []
    total.value = res.data.total || 0
    noMore.value = branchList.value.length >= total.value
  } finally {
    loading.value = false
  }
}

async function loadMore() {
  if (loadingMore.value || noMore.value) return
  loadingMore.value = true
  try {
    queryForm.current++
    const params = { ...queryForm, status: showCompleted.value ? 1 : 0 }
    const res = await pageBranches(params)
    const records = res.data.records || []
    branchList.value.push(...records)
    noMore.value = branchList.value.length >= (res.data.total || 0)
  } finally {
    loadingMore.value = false
  }
}

function resetList() {
  queryForm.current = 1
  branchList.value = []
  noMore.value = false
  fetchList()
}

async function fetchProdBranch() {
  const res = await getCurrentProdBranch()
  currentProdBranch.value = res.data || ''
}

async function fetchStats() {
  try {
    const res = await getBranchStats()
    stats.value = res.data || {}
  } catch (e) {
    // 统计接口失败不影响主流程
  }
}

function handleQuery() {
  resetList()
}

// 新增
function handleAdd() {
  isEdit.value = false
  Object.assign(form, { id: null, code: '', title: '', prdLink: '', onlineTime: '', priority: 0, devBranch: '' })
  dialogVisible.value = true
}

// 编辑
function handleEdit(item) {
  isEdit.value = true
  Object.assign(form, {
    id: item.id,
    code: item.code,
    title: item.title,
    prdLink: item.prdLink,
    onlineTime: item.onlineTime,
    priority: item.priority || 0,
    devBranch: item.devBranch || ''
  })
  dialogVisible.value = true
}

// 提交
async function handleSubmit() {
  await formRef.value.validate()
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateBranch(form)
      ElMessage.success('更新成功')
    } else {
      await addBranch(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    fetchList()
    fetchStats()
  } finally {
    submitting.value = false
  }
}

// 删除
function handleDelete(item) {
  ElMessageBox.confirm(`确定删除「${item.title}」？`, '提示', { type: 'warning' })
    .then(async () => {
      await deleteBranch(item.id)
      ElMessage.success('删除成功')
      fetchList()
      fetchStats()
    })
    .catch(() => {})
}

// 复制分支名
function handleCopyBranch(text) {
  navigator.clipboard.writeText(text).then(() => {
    ElMessage.success('已复制到剪贴板')
  })
}

// 完成
async function handleComplete(item) {
  await completeBranch(item.id)
  ElMessage.success('已完成')
  resetList()
  fetchStats()
}

// 更新生产分支
function handleEditProdBranch() {
  newProdBranch.value = currentProdBranch.value
  prodDialogVisible.value = true
}

async function handleSubmitProdBranch() {
  if (!newProdBranch.value.trim()) {
    ElMessage.warning('请输入分支名称')
    return
  }
  prodUpdating.value = true
  try {
    await updateCurrentProdBranch(newProdBranch.value.trim())
    ElMessage.success('生产分支已更新')
    currentProdBranch.value = newProdBranch.value.trim()
    prodDialogVisible.value = false
  } finally {
    prodUpdating.value = false
  }
}

onMounted(() => {
  if (accessVerified.value) {
    initPageData()
  } else {
    accessDialogVisible.value = true
  }
  window.addEventListener('scroll', handleScroll)
})

function initPageData() {
  fetchProdBranch()
  fetchList()
  fetchStats()
}

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})

function handleScroll() {
  const scrollTop = document.documentElement.scrollTop || document.body.scrollTop
  const scrollHeight = document.documentElement.scrollHeight
  const clientHeight = document.documentElement.clientHeight
  if (scrollTop + clientHeight >= scrollHeight - 100) {
    loadMore()
  }
}
</script>

<style>
/* ---- 亮色主题变量（默认） ---- */
.branch-page {
  --bp-bg: #f5f7fa;
  --bp-text: #2c3e50;
  --bp-text-secondary: #6b7280;
  --bp-text-muted: #9ca3af;
  --bp-card-bg: #ffffff;
  --bp-card-border: #e5e7eb;
  --bp-card-hover-border: #409eff;
  --bp-card-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  --bp-bar-bg: #ffffff;
  --bp-bar-border: #e5e7eb;
  --bp-code-bg: #f8f9fc;
  --bp-code-border: #eef0f4;
  --bp-green: #22c55e;
  --bp-green-bg: rgba(34, 197, 94, 0.08);
  --bp-green-border: rgba(34, 197, 94, 0.25);
  --bp-blue: #409eff;
  --bp-blue-bg: rgba(64, 158, 255, 0.08);
  --bp-yellow: #e5a00d;
  --bp-yellow-bg: rgba(229, 160, 13, 0.08);
  --bp-red: #ef4444;
  --bp-red-bg: rgba(239, 68, 68, 0.08);
  --bp-btn-add-bg: #409eff;
  --bp-btn-add-hover: #337ecc;
  --bp-branch-dev: #1f2937;
  --bp-branch-dev-hover: #374151;
  --bp-input-bg: #ffffff;
  --bp-input-border: #dcdfe6;
  --bp-stat-value: #1f2937;
  --bp-dot-glow: rgba(34, 197, 94, 0.4);
  --bp-card-left-bar: #e5e7eb;
}

/* ---- 暗色主题变量 ---- */
[data-theme="dark"] .branch-page {
  --bp-bg: #0f1419;
  --bp-text: #c9d1d9;
  --bp-text-secondary: #8b949e;
  --bp-text-muted: #484f58;
  --bp-card-bg: #161b22;
  --bp-card-border: #30363d;
  --bp-card-hover-border: #58a6ff;
  --bp-card-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  --bp-bar-bg: #161b22;
  --bp-bar-border: #30363d;
  --bp-code-bg: #0d1117;
  --bp-code-border: #21262d;
  --bp-green: #3fb950;
  --bp-green-bg: rgba(63, 185, 80, 0.1);
  --bp-green-border: rgba(63, 185, 80, 0.3);
  --bp-blue: #58a6ff;
  --bp-blue-bg: rgba(56, 139, 253, 0.15);
  --bp-yellow: #d29922;
  --bp-yellow-bg: rgba(210, 153, 34, 0.15);
  --bp-red: #f85149;
  --bp-red-bg: rgba(248, 81, 73, 0.15);
  --bp-btn-add-bg: #238636;
  --bp-btn-add-hover: #2ea043;
  --bp-branch-dev: #c9d1d9;
  --bp-branch-dev-hover: #f0f6fc;
  --bp-input-bg: #0d1117;
  --bp-input-border: #30363d;
  --bp-stat-value: #f0f6fc;
  --bp-dot-glow: rgba(63, 185, 80, 0.6);
  --bp-card-left-bar: #30363d;
}

/* ---- 基础样式 ---- */
.branch-page {
  padding: 20px;
  min-height: 100vh;
  background: var(--bp-bg);
  color: var(--bp-text);
  font-family: 'JetBrains Mono', 'Fira Code', 'SF Mono', monospace;
  transition: var(--theme-transition);
}

/* 顶部状态栏 */
.status-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  margin-bottom: 24px;
  background: var(--bp-bar-bg);
  border: 1px solid var(--bp-bar-border);
  border-radius: 8px;
  transition: var(--theme-transition);
}

.prod-indicator {
  display: flex;
  align-items: center;
  gap: 10px;
}

.indicator-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--bp-green);
  box-shadow: 0 0 8px var(--bp-dot-glow);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.indicator-label {
  font-size: 11px;
  font-weight: 600;
  letter-spacing: 1.5px;
  color: var(--bp-green);
  text-transform: uppercase;
}

.indicator-value {
  font-size: 14px;
  color: var(--bp-stat-value);
  font-weight: 500;
  padding: 2px 10px;
  background: var(--bp-green-bg);
  border: 1px solid var(--bp-green-border);
  border-radius: 4px;
}

.btn-update {
  background: none;
  border: 1px solid var(--bp-bar-border);
  color: var(--bp-text-secondary);
  padding: 4px 12px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.btn-update:hover {
  border-color: var(--bp-blue);
  color: var(--bp-blue);
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-theme {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: 6px;
  border: 1px solid var(--bp-bar-border);
  background: var(--bp-card-bg);
  color: var(--bp-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 16px;
}

.btn-theme:hover {
  border-color: var(--bp-blue);
  color: var(--bp-blue);
}

.btn-status {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 32px;
  padding: 0 14px;
  border-radius: 6px;
  border: 1px solid var(--bp-bar-border);
  background: var(--bp-card-bg);
  color: var(--bp-text-secondary);
  cursor: pointer;
  transition: all 0.2s;
  font-size: 12px;
  white-space: nowrap;
}

.btn-status:hover {
  border-color: var(--bp-blue);
  color: var(--bp-blue);
}

.btn-status.active {
  border-color: var(--bp-green);
  color: var(--bp-green);
  background: var(--bp-green-bg);
}

.load-more {
  text-align: center;
  padding: 16px 0;
  font-size: 13px;
  color: var(--bp-text-muted);
}

.no-more {
  opacity: 0.6;
}

.search-input {
  width: 200px;
}

.search-input :deep(.el-input__wrapper) {
  background: var(--bp-input-bg);
  border: 1px solid var(--bp-input-border);
  box-shadow: none;
}

.search-input :deep(.el-input__inner) {
  color: var(--bp-text);
}

.btn-add {
  display: flex;
  align-items: center;
  gap: 6px;
  background: var(--bp-btn-add-bg);
  border: 1px solid var(--bp-btn-add-hover);
  color: #fff;
  padding: 8px 16px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 13px;
  font-weight: 500;
  transition: background 0.2s;
}

.btn-add:hover {
  background: var(--bp-btn-add-hover);
}

/* 统计面板 */
.stats-panel {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.stat-item {
  flex: 1;
  min-width: 120px;
  background: var(--bp-card-bg);
  border: 1px solid var(--bp-card-border);
  border-radius: 8px;
  padding: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  transition: border-color 0.2s, var(--theme-transition);
}

.stat-item:hover {
  border-color: var(--bp-text-muted);
}

.stat-value {
  font-size: 24px;
  font-weight: 700;
  color: var(--bp-stat-value);
}

.stat-label {
  font-size: 11px;
  color: var(--bp-text-muted);
  text-transform: uppercase;
  letter-spacing: 1px;
}

.stat-pending .stat-value { color: var(--bp-blue); }
.stat-week .stat-value { color: var(--bp-yellow); }
.stat-urgent .stat-value { color: var(--bp-yellow); }
.stat-critical .stat-value { color: var(--bp-red); }

/* 卡片网格 */
.card-grid {
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-height: 200px;
}

.branch-card {
  background: var(--bp-card-bg);
  border: 1px solid var(--bp-card-border);
  border-radius: 8px;
  padding: 14px 20px;
  transition: all 0.2s ease;
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  gap: 16px;
}

.branch-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 3px;
  height: 100%;
  background: var(--bp-card-left-bar);
  transition: background 0.2s;
}

.branch-card:hover {
  border-color: var(--bp-card-hover-border);
  box-shadow: var(--bp-card-shadow);
}

.branch-card:hover::before {
  background: var(--bp-blue);
}

.branch-card.is-today::before {
  background: var(--bp-yellow);
}

.branch-card.is-overdue::before {
  background: var(--bp-green);
}

.card-code {
  font-size: 12px;
  font-weight: 600;
  color: var(--bp-stat-value);
  flex-shrink: 0;
  min-width: 120px;
}

.card-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--bp-stat-value);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
  min-width: 0;
}

.card-title.prd-link {
  color: var(--bp-blue);
  text-decoration: none;
  cursor: pointer;
}

.card-title.prd-link:hover {
  text-decoration: underline;
  color: var(--bp-branch-dev-hover);
}

.dev-branch {
  font-size: 12px;
  color: var(--bp-branch-dev);
  background: var(--bp-code-bg);
  border: 1px solid var(--bp-code-border);
  border-radius: 4px;
  padding: 4px 10px;
  cursor: pointer;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
  min-width: 0;
  transition: color 0.2s;
}

.dev-branch:hover {
  color: var(--bp-branch-dev-hover);
}

.copy-hint {
  font-size: 10px;
  color: var(--bp-text-muted);
  margin-left: 6px;
  opacity: 0;
  transition: opacity 0.2s;
}

.dev-branch:hover .copy-hint {
  opacity: 1;
}

.online-date {
  font-size: 12px;
  color: var(--bp-text-muted);
  flex-shrink: 0;
}

.prod-branch {
  font-size: 12px;
  color: var(--bp-branch-dev);
  background: var(--bp-code-bg);
  border: 1px solid var(--bp-code-border);
  border-radius: 4px;
  padding: 4px 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex-shrink: 0;
}

.card-status {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 500;
  flex-shrink: 0;
}

.card-more {
  cursor: pointer;
  color: var(--bp-text-muted);
  font-size: 12px;
  letter-spacing: 0.5px;
  padding: 2px 6px;
  border-radius: 4px;
  transition: all 0.2s;
  flex-shrink: 0;
}

.card-more:hover {
  color: var(--bp-text);
  background: var(--bp-code-bg);
}

.status-pending {
  background: var(--bp-blue-bg);
  color: var(--bp-blue);
}

.status-today {
  background: var(--bp-yellow-bg);
  color: var(--bp-yellow);
}

.status-done {
  background: var(--bp-green-bg);
  color: var(--bp-green);
}

/* 紧急程度标签 */
.priority-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 500;
}

.priority-normal {
  display: none;
}

.priority-urgent {
  background: var(--bp-yellow-bg);
  color: var(--bp-yellow);
  border: 1px solid rgba(210, 153, 34, 0.3);
}

.priority-critical {
  background: var(--bp-red-bg);
  color: var(--bp-red);
  border: 1px solid rgba(248, 81, 73, 0.3);
  animation: blink 2s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
}

.card-more {
  cursor: pointer;
  color: var(--bp-text-muted);
  font-size: 16px;
  letter-spacing: 1px;
  padding: 4px 8px;
  border-radius: 4px;
  transition: all 0.2s;
}

.card-more:hover {
  color: var(--bp-text);
  background: var(--bp-code-bg);
}

.btn-complete {
  background: none;
  border: 1px solid var(--bp-green-border);
  color: var(--bp-green);
  padding: 2px 10px;
  border-radius: 4px;
  cursor: pointer;
  font-size: 11px;
  font-weight: 500;
  transition: all 0.2s;
  flex-shrink: 0;
}

.btn-complete:hover {
  background: var(--bp-green-bg);
}

.completed-tag {
  font-size: 11px;
  color: var(--bp-green);
  background: var(--bp-green-bg);
  padding: 2px 8px;
  border-radius: 4px;
  flex-shrink: 0;
}

/* 卡片标题 */
.card-title {
  font-size: 14px;
  font-weight: 600;
  color: var(--bp-stat-value);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  flex: 1;
  min-width: 0;
}

/* 分支信息块 */
.branch-block {
  background: var(--bp-code-bg);
  border: 1px solid var(--bp-code-border);
  border-radius: 6px;
  padding: 8px 12px;
}

.branch-row {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 3px 0;
}

.branch-row + .branch-row {
  border-top: 1px solid var(--bp-code-border);
  margin-top: 3px;
  padding-top: 6px;
}

.branch-label {
  font-size: 11px;
  font-weight: 600;
  color: var(--bp-text-muted);
  min-width: 32px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.branch-name {
  font-size: 12px;
  color: var(--bp-text-secondary);
  word-break: break-all;
}

.branch-name.dev {
  color: var(--bp-branch-dev);
  cursor: pointer;
  position: relative;
  transition: color 0.2s;
}

.branch-name.dev:hover {
  color: var(--bp-branch-dev-hover);
}

.copy-hint {
  font-size: 10px;
  color: var(--bp-text-muted);
  margin-left: 6px;
  opacity: 0;
  transition: opacity 0.2s;
}

.branch-name.dev:hover .copy-hint {
  opacity: 1;
}

/* 卡片底部 */
.card-bottom {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.online-date {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: var(--bp-text-muted);
}

.prd-link {
  font-size: 12px;
  color: var(--bp-blue);
  text-decoration: none;
  transition: color 0.2s;
}

.prd-link:hover {
  color: var(--bp-branch-dev-hover);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 0;
  color: var(--bp-text-muted);
}

.empty-state p {
  margin-bottom: 16px;
  font-size: 14px;
}

/* 分页 */
.pagination-bar {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}

.pagination-bar :deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-text-color: var(--bp-text-secondary);
  --el-pagination-button-bg-color: var(--bp-card-bg);
}

/* 弹窗样式 */
.access-hint {
  font-size: 13px;
  color: var(--bp-text-secondary);
  margin-bottom: 16px;
}

.preview-branch {
  display: block;
  padding: 8px 12px;
  background: var(--bp-code-bg);
  border: 1px solid var(--bp-code-border);
  border-radius: 4px;
  font-size: 12px;
  color: var(--bp-branch-dev);
  word-break: break-all;
}

.form-hint {
  font-size: 12px;
  color: var(--bp-text-muted);
  margin-top: 4px;
}

[data-theme="dark"] .branch-dialog .el-input.is-disabled .el-input__wrapper {
  background-color: #1a1f2b;
  box-shadow: 0 0 0 1px #30363d inset;
}

[data-theme="dark"] .branch-dialog .el-input.is-disabled .el-input__inner {
  color: #8b949e;
}

/* 响应式 */
@media (max-width: 768px) {
  .status-bar {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }

  .branch-card {
    flex-wrap: wrap;
    gap: 8px;
  }

  .card-title {
    max-width: unset;
    flex-basis: 100%;
  }

  .dev-branch {
    flex-basis: 100%;
  }

  .stats-panel {
    flex-wrap: wrap;
  }

  .stat-item {
    min-width: 80px;
  }
}
</style>
