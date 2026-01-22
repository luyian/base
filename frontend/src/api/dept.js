import request from '@/utils/request'

/**
 * 获取部门树
 */
export function getDeptTree() {
  return request({
    url: '/system/dept/tree',
    method: 'get'
  })
}

/**
 * 根据父部门ID获取子部门列表
 */
export function getChildrenByParentId(parentId) {
  return request({
    url: `/system/dept/children/${parentId}`,
    method: 'get'
  })
}

/**
 * 根据ID获取部门详情
 */
export function getDeptById(id) {
  return request({
    url: `/system/dept/${id}`,
    method: 'get'
  })
}

/**
 * 创建部门
 */
export function createDept(data) {
  return request({
    url: '/system/dept',
    method: 'post',
    data
  })
}

/**
 * 更新部门
 */
export function updateDept(data) {
  return request({
    url: '/system/dept',
    method: 'put',
    data
  })
}

/**
 * 删除部门
 */
export function deleteDept(id) {
  return request({
    url: `/system/dept/${id}`,
    method: 'delete'
  })
}
