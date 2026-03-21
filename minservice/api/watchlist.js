// api/watchlist.js - Stock watchlist API
const request = require('../utils/request');

module.exports = {
  // Get user's watchlist
  getWatchlist: () => request.get('/stock/watchlist'),
  
  // Add stock to watchlist
  addToWatchlist: (stockCode, stockName, market) => 
    request.post('/stock/watchlist', { stockCode, stockName, market }),
  
  // Remove stock from watchlist
  removeFromWatchlist: (id) => request.delete(`/stock/watchlist/${id}`),
  
  // Get real-time quote for watchlist
  getQuotes: (codes) => request.post('/stock/quotes', { codes })
};