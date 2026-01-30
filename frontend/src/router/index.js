import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

// 基础路由（不需要权限的路由）
const constantRoutes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/Index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '首页', icon: 'HomeFilled' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Index.vue'),
        meta: { title: '个人中心', icon: 'UserFilled' }
      },
      {
        path: 'system/my-notice',
        name: 'MyNotice',
        component: () => import('@/views/system/MyNotice.vue'),
        meta: { title: '我的通知', icon: 'Bell' }
      },
      // 股票模块路由
      {
        path: 'stock',
        name: 'StockList',
        component: () => import('@/views/stock/index.vue'),
        meta: { title: '股票列表', icon: 'TrendCharts' }
      },
      {
        path: 'stock/detail/:code',
        name: 'StockDetail',
        component: () => import('@/views/stock/detail.vue'),
        meta: { title: '股票详情', icon: 'TrendCharts' }
      },
      {
        path: 'stock/watchlist',
        name: 'StockWatchlist',
        component: () => import('@/views/stock/watchlist/index.vue'),
        meta: { title: '自选股票', icon: 'Star' }
      },
      {
        path: 'stock/token',
        name: 'StockToken',
        component: () => import('@/views/stock/token/index.vue'),
        meta: { title: 'Token管理', icon: 'Key' }
      },
      {
        path: 'stock/mapping',
        name: 'StockMapping',
        component: () => import('@/views/stock/mapping/index.vue'),
        meta: { title: '映射配置', icon: 'Connection' }
      },
      {
        path: 'stock/mapping/edit/:id?',
        name: 'StockMappingEdit',
        component: () => import('@/views/stock/mapping/edit.vue'),
        meta: { title: '编辑映射配置', icon: 'Connection' }
      }
    ]
  }
]

// 404路由（需要在最后添加）
const notFoundRoute = {
  path: '/:pathMatch(.*)*',
  name: 'NotFound',
  redirect: '/404'
}

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 后台管理系统` : '后台管理系统'

  // 获取 token
  const token = localStorage.getItem('token')

  // 如果访问登录页，直接放行
  if (to.path === '/login') {
    if (token) {
      // 已登录，跳转到首页
      next('/')
    } else {
      next()
    }
    return
  }

  // 访问其他页面，检查是否已登录
  if (!token) {
    // 未登录，跳转到登录页
    next('/login')
    return
  }

  // 已登录，检查是否已加载动态路由
  const userStore = useUserStore()

  if (!userStore.routesLoaded) {
    try {
      // 先加载用户信息和权限
      await userStore.loadUserInfo()

      // 加载用户菜单和动态路由
      const dynamicRoutes = await userStore.loadMenus()

      // 将动态路由添加到主布局的children中
      if (dynamicRoutes && dynamicRoutes.length > 0) {
        dynamicRoutes.forEach(route => {
          // 路径已经是完整路径（如 /system/user），需要去掉开头的 / 作为子路由
          const routePath = route.path.startsWith('/') ? route.path.substring(1) : route.path
          router.addRoute({
            path: '/',
            component: () => import('@/layout/Index.vue'),
            children: [{
              ...route,
              path: routePath
            }]
          })
        })
      }

      // 添加404路由（必须在最后）
      router.addRoute(notFoundRoute)

      // 重新导航到目标路由
      next({ ...to, replace: true })
    } catch (error) {
      console.error('加载动态路由失败:', error)
      // 加载失败，清除token并跳转到登录页
      userStore.logout()
      next('/login')
    }
  } else {
    // 路由已加载，但可能权限数据丢失（刷新页面），需要重新加载用户信息
    if (!userStore.permissions || userStore.permissions.length === 0) {
      try {
        await userStore.loadUserInfo()
      } catch (error) {
        console.error('加载用户信息失败:', error)
      }
    }
    // 直接放行
    next()
  }
})

export default router
