import request from '@/utils/request'

/**
 * 分页查询全局变量列表
 */
export function pageConfigs(data) {
  return request({
    url: '/system/config/page',
    method: 'post',
    data
  })
}

/**
 * 根据ID获取全局变量详情
 */
export function getConfigById(id) {
  return request({
    url: `/system/config/${id}`,
    method: 'get'
  })
}

/**
 * 新增全局变量
 */
export function addConfig(data) {
  return request({
    url: '/system/config',
    method: 'post',
    data
  })
}

/**
 * 编辑全局变量
 */
export function updateConfig(data) {
  return request({
    url: '/system/config',
    method: 'put',
    data
  })
}

/**
 * 删除全局变量
 */
export function deleteConfig(id) {
  return request({
    url: `/system/config/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除全局变量
 */
export function batchDeleteConfigs(ids) {
  return request({
    url: '/system/config/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 根据配置键获取配置值
 */
export function getConfigValueByKey(configKey) {
  return request({
    url: `/system/config/key/${configKey}`,
    method: 'get'
  })
}

/**
 * 刷新全局变量缓存
 */
export function refreshCache() {
  return request({
    url: '/system/config/refresh-cache',
    method: 'post'
  })
}
