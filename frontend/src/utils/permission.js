import { useUserStore } from '@/store/user'

/**
 * 检查单个权限
 * @param {string} permission - 权限编码
 * @returns {boolean}
 */
export function hasPermission(permission) {
  const userStore = useUserStore()
  return userStore.hasPermission(permission)
}

/**
 * 检查是否有任意一个权限（OR 逻辑）
 * @param {Array<string>} permissions - 权限编码数组
 * @returns {boolean}
 */
export function hasAnyPermission(permissions) {
  const userStore = useUserStore()
  return userStore.hasAnyPermission(permissions)
}

/**
 * 检查是否有所有权限（AND 逻辑）
 * @param {Array<string>} permissions - 权限编码数组
 * @returns {boolean}
 */
export function hasAllPermissions(permissions) {
  const userStore = useUserStore()
  return userStore.hasAllPermissions(permissions)
}
