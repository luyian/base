import { useUserStore } from '@/store/user'

/**
 * 权限指令
 * 用法：
 * - 单个权限：v-permission="'system:user:add'"
 * - 多个权限（OR）：v-permission="['system:user:add', 'system:user:edit']"
 * - 多个权限（AND）：v-permission:all="['system:user:add', 'system:user:edit']"
 */
export default {
  mounted(el, binding) {
    const { value, arg } = binding
    const userStore = useUserStore()

    // 如果没有传入权限值，默认显示
    if (!value) return

    let hasPermission = false

    if (Array.isArray(value)) {
      // 数组形式：多个权限
      if (arg === 'all') {
        // AND 逻辑：需要拥有所有权限
        hasPermission = userStore.hasAllPermissions(value)
      } else {
        // OR 逻辑：拥有任意一个权限即可
        hasPermission = userStore.hasAnyPermission(value)
      }
    } else {
      // 字符串形式：单个权限
      hasPermission = userStore.hasPermission(value)
    }

    // 如果没有权限，移除 DOM 元素
    if (!hasPermission) {
      el.parentNode?.removeChild(el)
    }
  }
}
