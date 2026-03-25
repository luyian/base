// api/fund.js - Fund valuation API
const request = require('../utils/request');

module.exports = {
  // Get fund list
  getFundList: () => request.get('/stock/fund/list'),
  
  // Get fund detail with holdings
  getFundDetail: (fundId) => request.get(`/stock/fund/${fundId}`),
  
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