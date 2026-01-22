import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserMenuTree } from '@/api/menu'
import { generateRoutes, filterHiddenMenus } from '@/utils/route'

export const useUserStore = defineStore('user', () => {
  // 用户信息
  const userInfo = ref(null)
  // Token
  const token = ref(localStorage.getItem('token') || '')
  // 用户菜单
  const menus = ref([])
  // 动态路由
  const routes = ref([])
  // 路由是否已加载
  const routesLoaded = ref(false)

  // 设置 Token
  const setToken = (newToken) => {
    token.value = newToken
    localStorage.setItem('token', newToken)
  }

  // 设置用户信息
  const setUserInfo = (info) => {
    userInfo.value = info
  }

  // 设置菜单
  const setMenus = (menuList) => {
    menus.value = menuList
  }

  // 设置路由
  const setRoutes = (routeList) => {
    routes.value = routeList
  }

  // 加载用户菜单和动态路由
  const loadMenus = async () => {
    try {
      // 如果已经加载过，直接返回
      if (routesLoaded.value) {
        return routes.value
      }

      // 从后端获取菜单数据
      const res = await getUserMenuTree()

      if (res.code === 200 && res.data) {
        // 保存原始菜单数据（用于侧边栏显示）
        const menuData = res.data || []
        setMenus(filterHiddenMenus(menuData))

        // 生成路由配置
        const dynamicRoutes = generateRoutes(menuData)
        setRoutes(dynamicRoutes)

        // 标记路由已加载
        routesLoaded.value = true

        return dynamicRoutes
      } else {
        console.error('获取菜单失败:', res.message)
        return []
      }
    } catch (error) {
      console.error('加载菜单失败:', error)
      return []
    }
  }

  // 重置路由加载状态
  const resetRoutesLoaded = () => {
    routesLoaded.value = false
    menus.value = []
    routes.value = []
  }

  // 登出
  const logout = () => {
    token.value = ''
    userInfo.value = null
    menus.value = []
    routes.value = []
    routesLoaded.value = false
    localStorage.removeItem('token')
  }

  return {
    userInfo,
    token,
    menus,
    routes,
    routesLoaded,
    setToken,
    setUserInfo,
    setMenus,
    setRoutes,
    loadMenus,
    resetRoutesLoaded,
    logout
  }
})
