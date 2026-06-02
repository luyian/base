import request from '@/utils/request'

/**
 * 发起审批
 */
export function submitApproval(data) {
    return request({ url: '/approval/submit', method: 'post', data })
}

/**
 * 撤销审批
 */
export function cancelApproval(id) {
    return request({ url: `/approval/${id}/cancel`, method: 'post' })
}

/**
 * 分页查询审批实例
 */
export function listApprovals(params) {
    return request({ url: '/approval/list', method: 'get', params })
}

/**
 * 审批详情
 */
export function getApprovalDetail(id) {
    return request({ url: `/approval/${id}`, method: 'get' })
}

/**
 * 按业务键查询
 */
export function getApprovalByBusinessKey(businessKey) {
    return request({ url: `/approval/business/${businessKey}`, method: 'get' })
}

/**
 * 手动同步审批状态
 */
export function syncApprovalStatus(id) {
    return request({ url: `/approval/${id}/sync`, method: 'post' })
}
