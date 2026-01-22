import request from '@/utils/request'

/**
 * 查询权限树
 */
export function treePermissions(params) {
  return request({
    url: '/system/permission/tree',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取权限详情
 */
export function getPermissionById(id) {
  return request({
    url: `/system/permission/${id}`,
    method: 'get'
  })
}

/**
 * 新增权限
 */
export function addPermission(data) {
  return request({
    url: '/system/permission',
    method: 'post',
    data
  })
}

/**
 * 编辑权限
 */
export function updatePermission(data) {
  return request({
    url: '/system/permission',
    method: 'put',
    data
  })
}

/**
 * 删除权限
 */
export function deletePermission(id) {
  return request({
    url: `/system/permission/${id}`,
    method: 'delete'
  })
}

/**
 * 获取当前用户的菜单树
 */
export function getCurrentUserMenuTree() {
  return request({
    url: '/system/permission/menu/tree',
    method: 'get'
  })
}

/**
 * 获取所有权限树（用于角色分配权限）
 */
export function getAllPermissionTree() {
  return request({
    url: '/system/permission/all/tree',
    method: 'get'
  })
}
