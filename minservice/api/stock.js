// api/stock.js - Stock API
const request = require('../utils/request');

module.exports = {
  // Get stock list (paginated)
  getStockList: (data) => request.post('/stock/list', data),
  
  // Get stock detail by code
  getStockDetail: (stockCode) => request.get(`/stock/${stockCode}`),
  
  // Get stock K-line data
  getStockKline: (stockCode, startDate, endDate) => request.get(`/stock/${stockCode}/kline`, { startDate, endDate }),
  
  // Get industry options
  getIndustryOptions: () => request.get('/stock/industry/options')
};