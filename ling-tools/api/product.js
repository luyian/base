// api/product.js - 商品管理 API（元灵工具箱）
const request = require('../utils/request');

/**
 * 分页查询我的商品
 * @param {Object} data { name, page, size }
 */
function listProducts(data = {}) {
  return request.get('/prod/list', data);
}

/**
 * 新增商品
 * @param {Object} data 商品信息
 */
function createProduct(data) {
  return request.post('/prod', data);
}

/**
 * 编辑商品
 * @param {number} id
 * @param {Object} data
 */
function updateProduct(id, data) {
  return request.put(`/prod/${id}`, data);
}

/**
 * 删除商品
 * @param {number} id
 */
function deleteProduct(id) {
  return request.delete(`/prod/${id}`);
}

/**
 * 查询商品详情
 * @param {number} id
 */
function getProduct(id) {
  return request.get(`/prod/${id}`);
}

/**
 * 查询商品关联条码列表
 * @param {number} id
 */
function listProductCodes(id) {
  return request.get(`/prod/codes/${id}`);
}

/**
 * 扫码命中商品
 * @param {string} code 条码内容
 */
function scanProduct(code) {
  return request.get(`/prod/scan/${code}`);
}

/**
 * 生成业务唯一编码
 */
function generateCode() {
  return request.get('/prod/generate-code');
}

/**
 * 绑定条码到商品
 * @param {number} id
 * @param {Object} data { code, type, source }
 */
function bindCode(id, data) {
  return request.post(`/prod/${id}/bind`, data, { header: { 'Content-Type': 'application/x-www-form-urlencoded' } });
}

/**
 * 解绑商品条码
 * @param {number} id
 * @param {number} codeId
 */
function unbindCode(id, codeId) {
  return request.delete(`/prod/${id}/bind/${codeId}`);
}

module.exports = {
  listProducts,
  createProduct,
  updateProduct,
  deleteProduct,
  getProduct,
  listProductCodes,
  scanProduct,
  generateCode,
  bindCode,
  unbindCode
};