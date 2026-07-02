import request from '@/utils/request'

/**
 * 分页查询院校录取分数线
 */
export function pageCollegeScores(data) {
  return request({
    url: '/gaokao/college-scores/page',
    method: 'post',
    data
  })
}

/**
 * 分页查询专业录取分数线
 */
export function pageMajorScores(data) {
  return request({
    url: '/gaokao/major-scores/page',
    method: 'post',
    data
  })
}

/**
 * 分页查询专业招生计划
 */
export function pageAdmissionPlans(data) {
  return request({
    url: '/gaokao/admission-plans/page',
    method: 'post',
    data
  })
}
