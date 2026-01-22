import request from '@/utils/request'

/**
 * 分页查询角色列表
 */
export function pageRoles(params) {
  return request({
    url: '/system/role/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取角色详情
 */
export function getRoleById(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'get'
  })
}

/**
 * 新增角色
 */
export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

/**
 * 编辑角色
 */
export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data
  })
}

/**
 * 删除角色
 */
export function deleteRole(id) {
  return request({
    url: `/system/role/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除角色
 */
export function batchDeleteRoles(ids) {
  return request({
    url: '/system/role/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 切换角色状态
 */
export function toggleRoleStatus(id, status) {
  return request({
    url: `/system/role/${id}/status/${status}`,
    method: 'put'
  })
}

/**
 * 分配权限
 */
export function assignPermissions(data) {
  return request({
    url: '/system/role/assign-permissions',
    method: 'put',
    data
  })
}

/**
 * 获取角色的权限ID列表
 */
export function getRolePermissionIds(roleId) {
  return request({
    url: `/system/role/${roleId}/permissions`,
    method: 'get'
  })
}

/**
 * 获取所有角色列表（不分页）
 */
export function listAllRoles() {
  return request({
    url: '/system/role/list',
    method: 'get'
  })
}
