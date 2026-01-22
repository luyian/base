import request from '@/utils/request'

/**
 * 登录
 */
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

/**
 * 获取验证码
 */
export function getCaptcha() {
  return request({
    url: '/auth/captcha',
    method: 'get'
  })
}

/**
 * 登出
 */
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

/**
 * 刷新 Token
 */
export function refreshToken() {
  return request({
    url: '/auth/refresh',
    method: 'post'
  })
}

/**
 * 获取当前用户信息
 */
export function getUserInfo() {
  return request({
    url: '/auth/info',
    method: 'get'
  })
}
