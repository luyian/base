import request from '@/utils/request'

/**
 * 身份证识别
 * @param {File} file 图片文件
 * @param {string} side front-正面 back-背面
 * @param {string} provider 指定供应商（可选）
 */
export function recognizeIdCard(file, side = 'front', provider = '') {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('side', side)
  if (provider) {
    formData.append('provider', provider)
  }
  return request({
    url: '/ocr/id-card',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 发票识别
 * @param {File} file 图片文件
 * @param {string} provider 指定供应商（可选）
 */
export function recognizeInvoice(file, provider = '') {
  const formData = new FormData()
  formData.append('file', file)
  if (provider) {
    formData.append('provider', provider)
  }
  return request({
    url: '/ocr/invoice',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 银行卡识别
 * @param {File} file 图片文件
 * @param {string} provider 指定供应商（可选）
 */
export function recognizeBankCard(file, provider = '') {
  const formData = new FormData()
  formData.append('file', file)
  if (provider) {
    formData.append('provider', provider)
  }
  return request({
    url: '/ocr/bank-card',
    method: 'post',
    data: formData,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

/**
 * 查询可用OCR供应商列表
 */
export function getOcrProviders() {
  return request({
    url: '/ocr/providers',
    method: 'get'
  })
}
