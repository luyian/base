import request from '@/utils/request'

/**
 * 分页查询导出配置
 */
export function pageExportConfig(params) {
  return request({
    url: '/system/export/config/page',
    method: 'get',
    params
  })
}

/**
 * 获取配置详情
 */
export function getExportConfig(id) {
  return request({
    url: `/system/export/config/${id}`,
    method: 'get'
  })
}

/**
 * 新增配置
 */
export function addExportConfig(data) {
  return request({
    url: '/system/export/config',
    method: 'post',
    data
  })
}

/**
 * 更新配置
 */
export function updateExportConfig(data) {
  return request({
    url: '/system/export/config',
    method: 'put',
    data
  })
}

/**
 * 删除配置
 */
export function deleteExportConfig(id) {
  return request({
    url: `/system/export/config/${id}`,
    method: 'delete'
  })
}

/**
 * 获取字段配置列表
 */
export function getExportFields(configId) {
  return request({
    url: `/system/export/config/${configId}/fields`,
    method: 'get'
  })
}

/**
 * 保存字段配置
 */
export function saveExportFields(configId, data) {
  return request({
    url: `/system/export/config/${configId}/fields`,
    method: 'post',
    data
  })
}

/**
 * 获取所有启用的配置列表
 */
export function listEnabledConfigs() {
  return request({
    url: '/system/export/config/list',
    method: 'get'
  })
}
