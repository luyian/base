import request from '@/utils/request'

/**
 * 获取个人信息
 */
export function getProfile() {
  return request({
    url: '/system/profile',
    method: 'get'
  })
}

/**
 * 更新个人信息
 */
export function updateProfile(data) {
  return request({
    url: '/system/profile',
    method: 'put',
    data
  })
}

/**
 * 修改密码
 */
export function updatePassword(data) {
  return request({
    url: '/system/profile/password',
    method: 'put',
    data
  })
}

/**
 * 更新头像
 */
export function updateAvatar(avatarUrl) {
  return request({
    url: '/system/profile/avatar',
    method: 'put',
    params: { avatarUrl }
  })
}
