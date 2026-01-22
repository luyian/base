import request from '@/utils/request'

/**
 * 分页查询枚举列表
 */
export function pageEnums(params) {
  return request({
    url: '/system/enum/page',
    method: 'get',
    params
  })
}

/**
 * 根据ID获取枚举详情
 */
export function getEnumById(id) {
  return request({
    url: `/system/enum/${id}`,
    method: 'get'
  })
}

/**
 * 新增枚举
 */
export function addEnum(data) {
  return request({
    url: '/system/enum',
    method: 'post',
    data
  })
}

/**
 * 编辑枚举
 */
export function updateEnum(data) {
  return request({
    url: '/system/enum',
    method: 'put',
    data
  })
}

/**
 * 删除枚举
 */
export function deleteEnum(id) {
  return request({
    url: `/system/enum/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除枚举
 */
export function batchDeleteEnums(ids) {
  return request({
    url: '/system/enum/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 根据枚举类型查询枚举列表
 */
export function listByType(enumType) {
  return request({
    url: `/system/enum/type/${enumType}`,
    method: 'get'
  })
}

/**
 * 刷新枚举缓存
 */
export function refreshCache() {
  return request({
    url: '/system/enum/refresh-cache',
    method: 'post'
  })
}
