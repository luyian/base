import request from '@/utils/request'

/**
 * 分页查询我的商品
 * @param {Object} params { name, page, size }
 */
export function listProducts(params) {
  return request({
    url: '/prod/list',
    method: 'get',
    params
  })
}

/**
 * 新增商品
 * @param {Object} data 商品信息
 */
export function createProduct(data) {
  return request({
    url: '/prod',
    method: 'post',
    data
  })
}

/**
 * 编辑商品
 * @param {number} id 商品ID
 * @param {Object} data 商品信息
 */
export function updateProduct(id, data) {
  return request({
    url: `/prod/${id}`,
    method: 'put',
    data
  })
}

/**
 * 删除商品
 * @param {number} id 商品ID
 */
export function deleteProduct(id) {
  return request({
    url: `/prod/${id}`,
    method: 'delete'
  })
}

/**
 * 查询商品详情
 * @param {number} id 商品ID
 */
export function getProduct(id) {
  return request({
    url: `/prod/${id}`,
    method: 'get'
  })
}

/**
 * 查询商品关联条码列表
 * @param {number} id 商品ID
 */
export function listProductCodes(id) {
  return request({
    url: `/prod/codes/${id}`,
    method: 'get'
  })
}

/**
 * 生成新的业务唯一编码
 */
export function generateCode() {
  return request({
    url: '/prod/generate-code',
    method: 'get'
  })
}

/**
 * 绑定条码到商品
 * @param {number} id 商品ID
 * @param {Object} params { code, type, source }
 */
export function bindCode(id, params) {
  return request({
    url: `/prod/${id}/bind`,
    method: 'post',
    params
  })
}

/**
 * 解绑商品条码
 * @param {number} id 商品ID
 * @param {number} codeId 条码记录ID
 */
export function unbindCode(id, codeId) {
  return request({
    url: `/prod/${id}/bind/${codeId}`,
    method: 'delete'
  })
}