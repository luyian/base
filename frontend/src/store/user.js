import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getUserMenuTree } from '@/api/menu'
import { getUserInfo } from '@/api/auth'
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
  // 用户权限列表
  const permissions = ref([])

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

  // 设置权限
  const setPermissions = (permissionList) => {
    permissions.value = permissionList || []
  }

  // 检查单个权限
  const hasPermission = (permission) => {
    if (!permission) return true
    return permissions.value.includes(permission)
  }

  // 检查是否有任意一个权限（OR 逻辑）
  const hasAnyPermission = (permissionList) => {
    if (!permissionList || permissionList.length === 0) return true
    return permissionList.some(p => permissions.value.includes(p))
  }

  // 检查是否有所有权限（AND 逻辑）
  const hasAllPermissions = (permissionList) => {
    if (!permissionList || permissionList.length === 0) return true
    return permissionList.every(p => permissions.value.includes(p))
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

  // 加载用户信息和权限
  const loadUserInfo = async () => {
    try {
      const res = await getUserInfo()
      if (res.code === 200 && res.data) {
        setUserInfo(res.data)
        setPermissions(res.data.permissions || [])
        return true
      } else {
        console.error('获取用户信息失败:', res.message)
        return false
      }
    } catch (error) {
      console.error('加载用户信息失败:', error)
      return false
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
    permissions.value = []
    routesLoaded.value = false
    localStorage.removeItem('token')
  }

  return {
    userInfo,
    token,
    menus,
    routes,
    routesLoaded,
    permissions,
    setToken,
    setUserInfo,
    setMenus,
    setRoutes,
    setPermissions,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    loadMenus,
    loadUserInfo,
    resetRoutesLoaded,
    logout
  }
})
