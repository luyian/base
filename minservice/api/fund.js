// api/fund.js - Fund valuation API
const request = require('../utils/request');

module.exports = {
  // Get fund list
  getFundList: () => request.get('/stock/fund/list'),
  
  // Get fund detail (config only)
  getFundById: (fundId) => request.get(`/stock/fund/${fundId}`),
  
  // Get fund detail with holdings (include valuation with quotes)
  getFundDetail: (fundId) => request.get(`/stock/fund/${fundId}/valuation`),
  
  // Create fund (admin)
  createFund: (data) => request.post('/stock/fund', data),
  
  // Update fund (admin)
  updateFund: (fundId, data) => request.put(`/stock/fund/${fundId}`, data),
  
  // Delete fund (admin)
  deleteFund: (fundId) => request.delete(`/stock/fund/${fundId}`),
  
  // Get latest valuation
  getLatestValuation: (fundId, tradeDate) => request.get(`/stock/fund/valuation/${fundId}`, { tradeDate }),
  
  // Refresh all fund valuations
  refreshAllValuations: () => request.post('/stock/fund/refresh-all'),
  
  // Refresh single fund valuation
  refreshValuation: (fundId) => request.post(`/stock/fund/refresh/${fundId}`),
  
  // ========== Fund Watchlist APIs ==========
  
  // Get my watchlist
  getMyWatchlist: () => request.get('/stock/fund/watchlist/list'),
  
  // Get watchlist valuations
  getWatchlistValuation: () => request.get('/stock/fund/watchlist/valuation'),
  
  // Add fund to watchlist
  addToWatchlist: (fundId) => request.post(`/stock/fund/watchlist/${fundId}`),
  
  // Remove fund from watchlist
  removeFromWatchlist: (fundId) => request.delete(`/stock/fund/watchlist/${fundId}`)
};