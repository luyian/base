import request from '@/utils/request'

/**
 * 获取全国实时天气
 */
export function getRealtimeWeather() {
  return request({
    url: '/weather/realtime',
    method: 'get',
    timeout: 120000
  })
}

/**
 * 获取城市天气详情
 */
export function getCityWeather(adcode) {
  return request({
    url: `/weather/city/${adcode}`,
    method: 'get'
  })
}
