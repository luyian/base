/**
 * 路由工具类
 * 用于处理动态路由生成
 */

// 导入所有可能的视图组件（使用懒加载）
const modules = import.meta.glob('../views/**/*.vue', { eager: false })

/**
 * 将后端返回的菜单数据转换为Vue Router路由配置
 * @param {Array} menus - 后端返回的菜单树
 * @returns {Array} 路由配置数组
 */
export function generateRoutes(menus) {
  const routes = []

  menus.forEach(menu => {
    // 只处理目录和菜单类型（type = 1 目录, type = 2 菜单）
    if (menu.type !== 1 && menu.type !== 2) {
      return
    }

    // 如果是菜单类型（type = 2）且有组件路径，生成路由
    if (menu.type === 2 && menu.component) {
      const route = {
        path: menu.path,
        name: menu.permissionCode || `menu_${menu.id}`,
        component: loadComponent(menu.component),
        meta: {
          title: menu.permissionName,
          icon: menu.icon,
          hidden: menu.visible === 0
        }
      }
      routes.push(route)
    }

    // 递归处理子菜单
    if (menu.children && menu.children.length > 0) {
      const childRoutes = generateRoutes(menu.children)
      routes.push(...childRoutes)
    }
  })

  return routes
}

/**
 * 动态加载组件
 * @param {String} componentPath - 组件路径
 * @returns {Function} 组件加载函数
 */
function loadComponent(componentPath) {
  // 处理组件路径，确保格式正确
  let path = componentPath

  // 如果路径不以 / 开头，添加 /
  if (!path.startsWith('/')) {
    path = '/' + path
  }

  // 如果路径不以 .vue 结尾，添加 .vue
  if (!path.endsWith('.vue')) {
    path = path + '.vue'
  }

  // 构建完整的组件路径
  const fullPath = `../views${path}`

  // 从 modules 中查找对应的组件
  if (modules[fullPath]) {
    return modules[fullPath]
  }

  // 尝试不区分大小写匹配（处理 Windows 路径问题）
  const lowerFullPath = fullPath.toLowerCase()
  for (const [key, value] of Object.entries(modules)) {
    if (key.toLowerCase() === lowerFullPath) {
      return value
    }
  }

  // 如果找不到组件，返回一个默认的空组件
  return () => import('../views/Dashboard.vue')
}

/**
 * 过滤隐藏的菜单
 * @param {Array} menus - 菜单树
 * @returns {Array} 过滤后的菜单树
 */
export function filterHiddenMenus(menus) {
  return menus.filter(menu => {
    // 过滤掉隐藏的菜单（visible = 0）
    if (menu.visible === 0) {
      return false
    }

    // 递归过滤子菜单
    if (menu.children && menu.children.length > 0) {
      menu.children = filterHiddenMenus(menu.children)
    }

    return true
  })
}

/**
 * 将菜单树转换为扁平数组
 * @param {Array} menus - 菜单树
 * @returns {Array} 扁平化的菜单数组
 */
export function flattenMenus(menus) {
  const result = []

  function flatten(items) {
    items.forEach(item => {
      result.push(item)
      if (item.children && item.children.length > 0) {
        flatten(item.children)
      }
    })
  }

  flatten(menus)
  return result
}

/**
 * 根据权限编码查找菜单
 * @param {Array} menus - 菜单树
 * @param {String} permissionCode - 权限编码
 * @returns {Object|null} 找到的菜单对象
 */
export function findMenuByCode(menus, permissionCode) {
  for (const menu of menus) {
    if (menu.permissionCode === permissionCode || menu.code === permissionCode) {
      return menu
    }

    if (menu.children && menu.children.length > 0) {
      const found = findMenuByCode(menu.children, permissionCode)
      if (found) {
        return found
      }
    }
  }

  return null
}
