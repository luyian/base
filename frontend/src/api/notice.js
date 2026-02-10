import request from '@/utils/request'

/**
 * 分页查询通知公告列表
 */
export function pageNotices(data) {
  return request({
    url: '/system/notice/page',
    method: 'post',
    data
  })
}

/**
 * 根据ID获取通知公告详情
 */
export function getNoticeById(id) {
  return request({
    url: `/system/notice/${id}`,
    method: 'get'
  })
}

/**
 * 新增通知公告
 */
export function addNotice(data) {
  return request({
    url: '/system/notice',
    method: 'post',
    data
  })
}

/**
 * 编辑通知公告
 */
export function updateNotice(data) {
  return request({
    url: '/system/notice',
    method: 'put',
    data
  })
}

/**
 * 删除通知公告
 */
export function deleteNotice(id) {
  return request({
    url: `/system/notice/${id}`,
    method: 'delete'
  })
}

/**
 * 批量删除通知公告
 */
export function batchDeleteNotices(ids) {
  return request({
    url: '/system/notice/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 发布通知公告
 */
export function publishNotice(id) {
  return request({
    url: `/system/notice/${id}/publish`,
    method: 'put'
  })
}

/**
 * 获取最新通知列表
 */
export function getLatestNotices(limit = 5) {
  return request({
    url: '/system/notice/latest',
    method: 'get',
    params: { limit }
  })
}

/**
 * 标记通知为已读
 */
export function markAsRead(id) {
  return request({
    url: `/system/notice/${id}/read`,
    method: 'post'
  })
}

/**
 * 获取未读通知数量
 */
export function getUnreadCount() {
  return request({
    url: '/system/notice/unread/count',
    method: 'get'
  })
}

/**
 * 获取我的通知列表
 */
export function getMyNotices(data) {
  return request({
    url: '/system/notice/my',
    method: 'post',
    data
  })
}
