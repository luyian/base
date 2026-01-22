import request from '@/utils/request'

/**
 * 获取当前用户的菜单树
 */
export function getUserMenuTree() {
  return request({
    url: '/system/permission/menu/tree',
    method: 'get'
  })
}
