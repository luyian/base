<template>
  <div class="layout-container" :class="{ 'layout-mobile': isMobile, 'sidebar-open': isMobile && sidebarOpen }">
    <!-- 移动端遮罩 -->
    <div
      v-if="isMobile && sidebarOpen"
      class="sidebar-mask"
      aria-hidden="true"
      @click="sidebarOpen = false"
    />
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse && !isMobile ? '64px' : '200px'" class="layout-aside">
      <div class="logo" :class="{ 'logo-collapsed': isCollapse && !isMobile }">
        <div class="logo-icon-wrap">
          <img src="/logo.svg" alt="Logo" class="logo-img" />
        </div>
        <span v-if="!isCollapse || isMobile" class="logo-text">后台管理系统</span>
      </div>
      <div class="sidebar-nav-wrap">
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse && !isMobile"
        :unique-opened="true"
        @select="handleMenuSelect"
      >
        <!-- 首页菜单（固定） -->
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>首页</template>
        </el-menu-item>

        <!-- 动态菜单 -->
        <template v-for="menu in menus" :key="menu.id">
          <!-- 一级菜单（有子菜单） -->
          <el-sub-menu v-if="menu.children && menu.children.length > 0" :index="String(menu.id)">
            <template #title>
              <el-icon v-if="menu.icon">
                <component :is="menu.icon" />
              </el-icon>
              <span>{{ menu.permissionName }}</span>
            </template>
            <!-- 二级菜单 -->
            <template v-for="subMenu in menu.children" :key="subMenu.id">
              <!-- 二级菜单（有子菜单） -->
              <el-sub-menu v-if="subMenu.children && subMenu.children.length > 0" :index="String(subMenu.id)">
                <template #title>
                  <el-icon v-if="subMenu.icon">
                    <component :is="subMenu.icon" />
                  </el-icon>
                  <span>{{ subMenu.permissionName }}</span>
                </template>
                <!-- 三级菜单 -->
                <el-menu-item
                  v-for="thirdMenu in subMenu.children"
                  :key="thirdMenu.id"
                  :index="thirdMenu.path"
                >
                  <el-icon v-if="thirdMenu.icon">
                    <component :is="thirdMenu.icon" />
                  </el-icon>
                  <span>{{ thirdMenu.permissionName }}</span>
                </el-menu-item>
              </el-sub-menu>
              <!-- 二级菜单（无子菜单） -->
              <el-menu-item v-else :index="subMenu.path">
                <el-icon v-if="subMenu.icon">
                  <component :is="subMenu.icon" />
                </el-icon>
                <span>{{ subMenu.permissionName }}</span>
              </el-menu-item>
            </template>
          </el-sub-menu>
          <!-- 一级菜单（无子菜单） -->
          <el-menu-item v-else :index="menu.path">
            <el-icon v-if="menu.icon">
              <component :is="menu.icon" />
            </el-icon>
            <template #title>{{ menu.permissionName }}</template>
          </el-menu-item>
        </template>
      </el-menu>
      </div>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="layout-main">
      <!-- 顶部栏 -->
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon v-if="isMobile" class="collapse-icon" @click="sidebarOpen = !sidebarOpen">
            <Menu />
          </el-icon>
          <el-icon v-else class="collapse-icon" @click="toggleCollapse">
            <Expand v-if="isCollapse" />
            <Fold v-else />
          </el-icon>
        </div>
        <div class="header-right">
          <!-- 主题切换按钮 -->
          <el-tooltip :content="isDark ? '切换亮色' : '切换暗色'" placement="bottom" :hide-after="1500">
            <button class="theme-toggle-btn" @click="toggleTheme" :aria-label="isDark ? '切换亮色' : '切换暗色'">
              <Transition name="theme-icon" mode="out-in">
                <el-icon :key="isDark ? 'moon' : 'sun'">
                  <Sunny v-if="isDark" />
                  <Moon v-else />
                </el-icon>
              </Transition>
            </button>
          </el-tooltip>
          <NoticeDropdown ref="noticeBadgeRef" />
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              <span class="user-name">{{ isMobile ? '管理' : (userInfo?.nickname || userInfo?.username || '管理员') }}</span>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 内容区 -->
      <el-main class="layout-content">
        <router-view />
      </el-main>
    </el-container>

    <div class="feishu-float">
      <el-button
        class="feishu-float-btn"
        type="primary"
        circle
        @click="toggleFeishuPanel"
        aria-label="发送飞书消息"
      >
        <el-icon><ChatDotRound /></el-icon>
      </el-button>

      <div v-show="feishuPanelVisible" class="feishu-panel">
        <div class="feishu-panel-header">
          <span>发送消息</span>
          <el-button text @click="feishuPanelVisible = false">
            <el-icon><Close /></el-icon>
          </el-button>
        </div>
        <div
          class="feishu-panel-body"
          :class="{ 'is-dragging': isDraggingFile }"
          @dragenter.prevent="handleDragEnter"
          @dragover.prevent="handleDragOver"
          @dragleave.prevent="handleDragLeave"
          @drop.prevent="handleDrop"
          @paste="handlePaste"
        >
          <el-input
            v-model="feishuMessage"
            type="textarea"
            :rows="4"
            placeholder="输入消息内容，支持拖拽或粘贴文件..."
          />
          <div class="feishu-upload">
            <el-tag v-if="selectedFile" type="success" size="small">
              {{ selectedFileType === 'image' ? '图片' : '文件' }}
            </el-tag>
            <el-tag v-else type="info" size="small">文本</el-tag>
            <span class="feishu-tip">支持拖拽/粘贴附件</span>
          </div>
        </div>
        <div class="feishu-panel-footer">
          <el-button size="small" @click="resetFeishuForm">清空</el-button>
          <el-button size="small" type="primary" :loading="feishuSending" @click="sendFeishu">
            发送
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { HomeFilled, Setting, User, Expand, Fold, Menu, Document, Bell, ChatDotRound, Close, Sunny, Moon } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import NoticeDropdown from '@/components/NoticeDropdown.vue'
import { sendFeishuMessageToUser, uploadFeishuImage, uploadFeishuFile } from '@/api/feishu'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// ---- 主题切换 ----
const isDark = ref(false)

const applyTheme = (dark) => {
  document.documentElement.setAttribute('data-theme', dark ? 'dark' : 'light')
  localStorage.setItem('theme', dark ? 'dark' : 'light')
}

const toggleTheme = () => {
  isDark.value = !isDark.value
  applyTheme(isDark.value)
}

// 侧边栏折叠状态（桌面端）
const isCollapse = ref(false)

// 移动端侧栏展开状态
const sidebarOpen = ref(false)

// 是否移动端（≤768px）
const isMobile = ref(false)
const mobileQuery = typeof window !== 'undefined' ? window.matchMedia('(max-width: 768px)') : null

function updateMobile() {
  isMobile.value = mobileQuery ? mobileQuery.matches : false
  if (isMobile.value) {
    sidebarOpen.value = false
  }
}

onMounted(() => {
  if (mobileQuery) {
    updateMobile()
    mobileQuery.addEventListener('change', updateMobile)
  }
  // 恢复本地存储的主题
  const saved = localStorage.getItem('theme')
  if (saved === 'dark') {
    isDark.value = true
    document.documentElement.setAttribute('data-theme', 'dark')
  }
})

onUnmounted(() => {
  if (mobileQuery) {
    mobileQuery.removeEventListener('change', updateMobile)
  }
})

watch(() => route.path, () => {
  if (isMobile.value) {
    sidebarOpen.value = false
  }
})

// 用户信息
const userInfo = computed(() => userStore.userInfo)

// 动态菜单
const menus = computed(() => userStore.menus)

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 通知徽章引用
const noticeBadgeRef = ref(null)

const feishuPanelVisible = ref(false)
const feishuMessage = ref('')
const feishuSending = ref(false)
const selectedFile = ref(null)
const selectedFileType = ref('text')
const fileList = ref([])
const isDraggingFile = ref(false)

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 处理菜单选择
const handleMenuSelect = (index) => {
  if (index && index.startsWith('/')) {
    router.push(index)
    if (isMobile.value) {
      sidebarOpen.value = false
    }
  }
}

// 处理下拉菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.logout()
      router.push('/login')
      ElMessage.success('退出成功')
    }).catch(() => {})
  } else if (command === 'profile') {
    router.push('/profile')
  }
}

const toggleFeishuPanel = () => {
  feishuPanelVisible.value = !feishuPanelVisible.value
}

const resetFeishuForm = () => {
  feishuMessage.value = ''
  selectedFile.value = null
  selectedFileType.value = 'text'
  fileList.value = []
  isDraggingFile.value = false
}

const handleFileChange = (file, files) => {
  const latest = files && files.length ? files[files.length - 1] : null
  if (!latest) {
    selectedFile.value = null
    selectedFileType.value = 'text'
    fileList.value = []
    return
  }
  selectedFile.value = latest.raw
  selectedFileType.value = latest.raw && latest.raw.type && latest.raw.type.startsWith('image/') ? 'image' : 'file'
  fileList.value = [latest]
}

const handleFileRemove = () => {
  selectedFile.value = null
  selectedFileType.value = 'text'
  fileList.value = []
}

const applySelectedFile = (file) => {
  if (!file) {
    return
  }
  selectedFile.value = file
  selectedFileType.value = file.type && file.type.startsWith('image/') ? 'image' : 'file'
  fileList.value = [{ name: file.name, url: '', status: 'ready', raw: file }]
}

const handlePaste = (event) => {
  const files = event.clipboardData ? event.clipboardData.files : null
  if (files && files.length > 0) {
    applySelectedFile(files[0])
  }
}

const handleDragEnter = () => {
  isDraggingFile.value = true
}

const handleDragOver = () => {
  isDraggingFile.value = true
}

const handleDragLeave = () => {
  isDraggingFile.value = false
}

const handleDrop = (event) => {
  const files = event.dataTransfer ? event.dataTransfer.files : null
  if (files && files.length > 0) {
    applySelectedFile(files[0])
  }
  isDraggingFile.value = false
}

const getCurrentUserId = () => {
  return userInfo.value?.id || userInfo.value?.userId || null
}

const sendFeishu = async () => {
  const text = feishuMessage.value.trim()
  if (!selectedFile.value && !text) {
    ElMessage.warning('请输入消息内容或选择附件')
    return
  }
  const userId = getCurrentUserId()
  if (!userId) {
    ElMessage.error('未获取到当前用户信息')
    return
  }
  feishuSending.value = true
  try {
    if (selectedFile.value) {
      if (selectedFileType.value === 'image') {
        const res = await uploadFeishuImage(selectedFile.value)
        const imageKey = res?.data?.imageKey
        if (!imageKey) {
          throw new Error('图片上传失败')
        }
        await sendFeishuMessageToUser(userId, {
          msgType: 'image',
          content: JSON.stringify({ image_key: imageKey })
        })
      } else {
        const res = await uploadFeishuFile(selectedFile.value)
        const fileKey = res?.data?.fileKey
        if (!fileKey) {
          throw new Error('文件上传失败')
        }
        await sendFeishuMessageToUser(userId, {
          msgType: 'file',
          content: JSON.stringify({ file_key: fileKey })
        })
      }
    } else {
      await sendFeishuMessageToUser(userId, {
        msgType: 'text',
        content: JSON.stringify({ text })
      })
    }
    ElMessage.success('发送成功')
    feishuPanelVisible.value = false
    resetFeishuForm()
  } catch (e) {
    ElMessage.error(e.message || '发送失败')
  } finally {
    feishuSending.value = false
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  width: 100%;
  height: 100%;
}

.layout-aside {
  background-color: var(--sidebar-bg);
  border-right: 1px solid var(--sidebar-border);
  transition: width 0.28s cubic-bezier(0.4, 0, 0.2, 1), var(--theme-transition);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* ---- Logo 区域 ---- */
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 18px;
  background-color: var(--logo-bg);
  border-bottom: 1px solid var(--sidebar-border);
  flex-shrink: 0;
  overflow: hidden;
  transition: padding 0.28s, var(--theme-transition);
}

.logo-collapsed {
  padding: 0 14px;
  justify-content: center;
}

.logo-icon-wrap {
  width: 30px;
  height: 30px;
  border-radius: 7px;
  background: var(--logo-icon-bg);
  border: 1px solid var(--logo-icon-border);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  transition: var(--theme-transition);
}

.logo-img {
  width: 20px;
  height: 20px;
  object-fit: contain;
}

.logo-text {
  font-size: 15px;
  font-weight: 700;
  color: var(--logo-text-color);
  white-space: nowrap;
  letter-spacing: 0.02em;
  transition: var(--theme-transition);
}

/* ---- 导航滚动区 ---- */
.sidebar-nav-wrap {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  scrollbar-width: thin;
  scrollbar-color: var(--scrollbar-thumb) transparent;
}

.sidebar-nav-wrap::-webkit-scrollbar {
  width: 4px;
}

.sidebar-nav-wrap::-webkit-scrollbar-thumb {
  background: var(--scrollbar-thumb);
  border-radius: 2px;
  transition: var(--theme-transition);
}

.sidebar-nav-wrap::-webkit-scrollbar-thumb:hover {
  background: var(--scrollbar-thumb-hov);
}

.sidebar-nav-wrap::-webkit-scrollbar-track {
  background: transparent;
}

/* ---- 菜单整体 ---- */
.el-menu {
  border-right: none;
  background-color: var(--menu-bg) !important;
  transition: var(--theme-transition);
}

/* ---- 菜单项 & 子菜单标题 ---- */
:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  color: var(--menu-text);
  transition: background-color 0.18s ease, color 0.18s ease, border-color 0.18s ease;
  border-left: 3px solid transparent;
}

/* ---- 子菜单背景 ---- */
:deep(.el-sub-menu .el-menu) {
  background-color: var(--menu-sub-bg) !important;
}

:deep(.el-sub-menu .el-menu-item) {
  background-color: var(--menu-sub-bg);
  color: var(--menu-sub-text);
  font-size: 13px;
  border-left: 3px solid transparent;
  transition: background-color 0.18s ease, color 0.18s ease, border-color 0.18s ease;
}

/* ---- Hover 效果 ---- */
:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: var(--menu-hover-bg) !important;
  color: var(--menu-hover-text) !important;
  border-left-color: var(--menu-hover-border) !important;
}

:deep(.el-sub-menu .el-menu-item:hover) {
  background-color: var(--menu-hover-bg) !important;
  color: var(--menu-hover-text) !important;
  border-left-color: var(--menu-hover-border) !important;
}

/* ---- 激活状态 ---- */
:deep(.el-menu-item.is-active) {
  background-color: var(--menu-active-bg) !important;
  color: var(--menu-active-text) !important;
  border-left-color: var(--menu-active-border) !important;
  font-weight: 600;
}

:deep(.el-menu--collapse .el-menu-item.is-active) {
  border-left-color: transparent !important;
  background-color: var(--menu-active-bg) !important;
}

/* 父菜单展开时标题颜色 */
:deep(.el-sub-menu.is-opened > .el-sub-menu__title) {
  color: var(--menu-active-text);
}

/* 子菜单箭头图标 */
:deep(.el-sub-menu__icon-arrow) {
  color: var(--menu-arrow-color);
  transition: transform 0.25s ease, color 0.18s ease;
}

:deep(.el-sub-menu.is-opened > .el-sub-menu__title .el-sub-menu__icon-arrow) {
  color: var(--menu-arrow-open);
}

/* ---- 主体 ---- */
.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--header-bg);
  border-bottom: 1px solid var(--header-border);
  padding: 0 20px;
  transition: var(--theme-transition);
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  color: var(--header-icon-color);
  transition: color 0.2s ease;
}

.collapse-icon:hover {
  color: var(--header-icon-hover);
}

.header-right {
  display: flex;
  align-items: center;
  gap: 4px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 10px;
  color: var(--user-info-color);
  transition: var(--theme-transition);
}

.user-info .el-icon {
  margin-right: 5px;
}

.layout-content {
  background-color: var(--content-bg);
  padding: 20px;
  transition: var(--theme-transition);
}

/* ---- 主题切换按钮 ---- */
.theme-toggle-btn {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: var(--theme-toggle-bg);
  color: var(--theme-toggle-color);
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  transition: background 0.2s ease, color 0.2s ease;
  outline: none;
}

.theme-toggle-btn:hover {
  background: var(--theme-toggle-hover);
  color: var(--header-icon-hover);
}

/* 主题图标切换动画 */
.theme-icon-enter-active,
.theme-icon-leave-active {
  transition: opacity 0.15s ease, transform 0.15s ease;
}

.theme-icon-enter-from {
  opacity: 0;
  transform: rotate(-30deg) scale(0.8);
}

.theme-icon-leave-to {
  opacity: 0;
  transform: rotate(30deg) scale(0.8);
}

/* ---- 飞书浮窗 ---- */
.feishu-float {
  position: fixed;
  right: 24px;
  bottom: 24px;
  z-index: 1200;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 10px;
}

.feishu-float-btn {
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.2);
}

.feishu-panel {
  width: 320px;
  background: var(--feishu-panel-bg);
  border-radius: 12px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.18);
  overflow: hidden;
  border: 1px solid var(--feishu-panel-border);
  transition: var(--theme-transition);
}

.feishu-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  background: var(--feishu-panel-head-bg);
  border-bottom: 1px solid var(--feishu-panel-border);
  font-weight: 600;
  transition: var(--theme-transition);
}

.feishu-panel-body {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.feishu-panel-body.is-dragging {
  border: 1px dashed #409eff;
  background: rgba(64, 158, 255, 0.05);
  border-radius: 8px;
}

.feishu-upload {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.feishu-panel-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 10px 12px 12px;
  border-top: 1px solid var(--feishu-panel-border);
}

.feishu-tip {
  margin-left: 8px;
  color: var(--feishu-tip-color);
  font-size: 12px;
}

/* 移动端：侧栏抽屉 + 遮罩 */
.sidebar-mask {
  position: fixed;
  inset: 0;
  z-index: 1000;
  background: rgba(0, 0, 0, 0.5);
  transition: opacity 0.3s;
}

.layout-mobile .layout-aside {
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  z-index: 1001;
  width: 260px !important;
  max-width: 80vw;
  transform: translateX(-100%);
  transition: transform 0.3s;
}

.layout-mobile.sidebar-open .layout-aside {
  transform: translateX(0);
}

@media (max-width: 768px) {
  .layout-header {
    padding: 0 12px;
  }

  .feishu-float {
    right: 16px;
    bottom: 16px;
  }

  .feishu-panel {
    width: 90vw;
  }
}
</style>
