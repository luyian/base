// api/stock.js - Stock API
const request = require('../utils/request');

module.exports = {
  // Search stocks (public, no auth required)
  searchStocks: (keyword) => request.get('/stock/search', { keyword }),
  
  // Get stock list (paginated, requires auth)
  getStockList: (data) => request.post('/stock/list', data),
  
  // Get stock detail by code
  getStockDetail: (stockCode) => request.get(`/stock/${stockCode}`),
  
  // Get stock K-line data
  getStockKline: (stockCode, startDate, endDate) => request.get(`/stock/${stockCode}/kline`, { startDate, endDate }),
  
  // Get industry options
  getIndustryOptions: () => request.get('/stock/industry/options'),
  
  // Create stock (admin)
  createStock: (data) => request.post('/stock', data)
};