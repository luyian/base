import request from '@/utils/request'

/**
 * AI 对话（一次性非流式）
 * @param {Object} data - { message: string, context?: string }
 * @returns {Promise<{ answer: string }>}
 */
export function chat(data) {
  return request({
    url: '/ai/chat',
    method: 'post',
    data,
    timeout: 60000
  })
}

/**
 * 大模型配置分页
 */
export function pageAiConfig(params) {
  return request({
    url: '/system/ai-config/page',
    method: 'get',
    params
  })
}

/**
 * 大模型配置列表（启用中的，用于下拉等）
 */
export function listAiConfig() {
  return request({
    url: '/system/ai-config/list',
    method: 'get'
  })
}

/**
 * 获取大模型配置详情（含 API Key 原文，编辑用）
 */
export function getAiConfigById(id) {
  return request({
    url: `/system/ai-config/${id}`,
    method: 'get'
  })
}

/**
 * 新增大模型配置
 */
export function addAiConfig(data) {
  return request({
    url: '/system/ai-config',
    method: 'post',
    data
  })
}

/**
 * 更新大模型配置
 */
export function updateAiConfig(data) {
  return request({
    url: '/system/ai-config',
    method: 'put',
    data
  })
}

/**
 * 删除大模型配置
 */
export function deleteAiConfig(id) {
  return request({
    url: `/system/ai-config/${id}`,
    method: 'delete'
  })
}

/**
 * 设为生效配置
 */
export function setActiveAiConfig(id) {
  return request({
    url: `/system/ai-config/${id}/active`,
    method: 'post'
  })
}
