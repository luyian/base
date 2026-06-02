import request from '@/utils/request'

/**
 * 模板列表
 */
export function listTemplates(params) {
    return request({ url: '/approval/template/list', method: 'get', params })
}

/**
 * 模板详情
 */
export function getTemplate(id) {
    return request({ url: `/approval/template/${id}`, method: 'get' })
}

/**
 * 新增模板
 */
export function saveTemplate(data) {
    return request({ url: '/approval/template', method: 'post', data })
}

/**
 * 更新模板
 */
export function updateTemplate(id, data) {
    return request({ url: `/approval/template/${id}`, method: 'put', data })
}

/**
 * 删除模板
 */
export function deleteTemplate(id) {
    return request({ url: `/approval/template/${id}`, method: 'delete' })
}

/**
 * 事件日志分页
 */
export function listEventLogs(params) {
    return request({ url: '/approval/event-log/list', method: 'get', params })
}

/**
 * WebSocket 连接状态
 */
export function getWsStatus() {
    return request({ url: '/approval/event-log/ws-status', method: 'get' })
}

/**
 * 手动重试事件
 */
export function retryEvent(id) {
    return request({ url: `/approval/event-log/retry/${id}`, method: 'post' })
}
