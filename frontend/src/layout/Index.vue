<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '200px'" class="layout-aside">
      <div class="logo">
        <span v-if="!isCollapse">后台管理系统</span>
        <span v-else>后台</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
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
          <el-icon class="collapse-icon" @click="toggleCollapse">
            <Expand v-if="isCollapse" />
            <Fold v-else />
          </el-icon>
        </div>
        <div class="header-right">
          <NoticeDropdown ref="noticeBadgeRef" />
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              <span>{{ userInfo?.username || '管理员' }}</span>
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
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { HomeFilled, Setting, User, Expand, Fold, Document, Bell } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/user'
import NoticeDropdown from '@/components/NoticeDropdown.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

// 侧边栏折叠状态
const isCollapse = ref(false)

// 用户信息
const userInfo = computed(() => userStore.userInfo)

// 动态菜单
const menus = computed(() => userStore.menus)

// 当前激活的菜单
const activeMenu = computed(() => route.path)

// 通知徽章引用
const noticeBadgeRef = ref(null)

// 切换侧边栏折叠状态
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 处理菜单选择
const handleMenuSelect = (index) => {
  if (index && index.startsWith('/')) {
    router.push(index)
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
</style>
