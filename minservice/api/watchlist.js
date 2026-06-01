// api/watchlist.js - Stock watchlist API
const request = require('../utils/request');

module.exports = {
    // 查询自选列表
    getWatchlist: () => request.get('/stock/watchlist'),

    // 添加自选（后端接收 @RequestParam）
    addToWatchlist: (stockCode) =>
        request.post('/stock/watchlist?stockCode=' + encodeURIComponent(stockCode)),

    // 删除自选
    removeFromWatchlist: (id) => request.delete('/stock/watchlist/' + id),

    // 批量获取实时行情（复用基金估值的 QuoteProvider）
    getQuotes: (codes) => request.post('/stock/watchlist/quotes', codes)
};
