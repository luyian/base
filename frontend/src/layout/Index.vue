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
      <div class="logo">
        <span v-if="!isCollapse">后台管理系统</span>
        <span v-else>后台</span>
      </div>
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
import { HomeFilled, Setting, User, Expand, Fold, Menu, Document, Bell, ChatDotRound, Close } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import NoticeDropdown from '@/components/NoticeDropdown.vue'
import { sendFeishuMessageToUser, uploadFeishuImage, uploadFeishuFile } from '@/api/feishu'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

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
  background-color: #304156;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  background-color: #2b3a4b;
}

.el-menu {
  border-right: none;
  background-color: #304156;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  color: #bfcbd9;
}

:deep(.el-sub-menu .el-menu) {
  background-color: #1f2d3d;
}

:deep(.el-sub-menu .el-menu-item) {
  background-color: #1f2d3d;
  color: #bfcbd9;
}

:deep(.el-menu-item:hover),
:deep(.el-sub-menu__title:hover) {
  background-color: #263445 !important;
  color: #fff;
}

:deep(.el-sub-menu .el-menu-item:hover) {
  background-color: #263445 !important;
  color: #fff;
}

:deep(.el-menu-item.is-active) {
  background-color: #409eff !important;
  color: #fff;
}

.layout-main {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.layout-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-icon {
  font-size: 20px;
  cursor: pointer;
  transition: color 0.3s;
}

.collapse-icon:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  padding: 0 10px;
}

.user-info .el-icon {
  margin-right: 5px;
}

.layout-content {
  background-color: #f0f2f5;
  padding: 20px;
}

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
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 12px 28px rgba(0, 0, 0, 0.16);
  overflow: hidden;
}

.feishu-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 12px;
  background: #f7f8fa;
  border-bottom: 1px solid #eceef2;
  font-weight: 600;
}

.feishu-panel-body {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.feishu-panel-body.is-dragging {
  border: 1px dashed #409eff;
  background: #f0f7ff;
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
  border-top: 1px solid #eceef2;
}

.feishu-tip {
  margin-left: 8px;
  color: #909399;
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
