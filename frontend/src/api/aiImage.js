import request from '@/utils/request'

/**
 * AI 作图专用多图生成
 * @param {Object} data 生成参数
 * @returns {Promise<Object>} 生成结果
 */
export function generateAiImages(data) {
  return request({
    url: '/ai/image/generate',
    method: 'post',
    data,
    timeout: 180000
  })
}

/**
 * AI 作图专用提示词润色
 * @param {Object} data 润色参数
 * @returns {Promise<Object>} 润色结果
 */
export function polishAiImagePrompt(data) {
  return request({
    url: '/ai/image/polish',
    method: 'post',
    data,
    timeout: 180000
  })
}
