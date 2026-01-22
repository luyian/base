import request from '@/utils/request'

/**
 * 分页查询用户列表
 */
export function pageUsers(params) {
  return request({
    url: '/system/user/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取用户详情
 */
export function getUserById(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'get'
  })
}

/**
 * 新增用户
 */
export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

/**
 * 编辑用户
 */
export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}

/**
 * 删除用户
 */
export function deleteUser(id) {
  return request({
    url: `/system/user/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除用户
 */
export function batchDeleteUsers(ids) {
  return request({
    url: '/system/user/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 切换用户状态
 */
export function toggleUserStatus(id, status) {
  return request({
    url: `/system/user/${id}/status/${status}`,
    method: 'put'
  })
}

/**
 * 重置密码
 */
export function resetPassword(data) {
  return request({
    url: '/system/user/reset-password',
    method: 'put',
    data
  })
}

/**
 * 分配角色
 */
export function assignRoles(data) {
  return request({
    url: '/system/user/assign-roles',
    method: 'put',
    data
  })
}

/**
 * 获取用户的角色ID列表
 */
export function getUserRoleIds(userId) {
  return request({
    url: `/system/user/${userId}/roles`,
    method: 'get'
  })
}
