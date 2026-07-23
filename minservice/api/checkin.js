// api/checkin.js - 打卡 API
const request = require('../utils/request');

module.exports = {
  // 查询我的计划列表（含今日打卡状态）
  getPlanList: () => request.get('/checkin/plan/list'),

  // 新增计划
  createPlan: (data) => request.post('/checkin/plan', data),

  // 编辑计划
  updatePlan: (id, data) => request.put(`/checkin/plan/${id}`, data),

  // 删除计划
  deletePlan: (id) => request.delete(`/checkin/plan/${id}`),

  // 打卡/取消打卡
  toggleRecord: (planId, date) => request.post(`/checkin/record/toggle?planId=${planId}&date=${date}`),

  // 查询某月完成度概览
  getCalendar: (month) => request.get('/checkin/calendar', { month })
};
