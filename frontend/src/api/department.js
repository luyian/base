import request from '@/utils/request'

/**
 * 查询部门树
 */
export function treeDepartments(params) {
  return request({
    url: '/system/department/tree',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取部门详情
 */
export function getDepartmentById(id) {
  return request({
    url: `/system/department/${id}`,
    method: 'get'
  })
}

/**
 * 新增部门
 */
export function addDepartment(data) {
  return request({
    url: '/system/department',
    method: 'post',
    data
  })
}

/**
 * 编辑部门
 */
export function updateDepartment(data) {
  return request({
    url: '/system/department',
    method: 'put',
    data
  })
}

/**
 * 删除部门
 */
export function deleteDepartment(id) {
  return request({
    url: `/system/department/${id}`,
    method: 'delete'
  })
}

/**
 * 获取所有部门树（用于选择上级部门）
 */
export function getAllDepartmentTree() {
  return request({
    url: '/system/department/all/tree',
    method: 'get'
  })
}
