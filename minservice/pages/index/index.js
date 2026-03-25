// pages/index/index.js
const fundApi = require('../../api/fund');

Page({
  data: {
    watchlist: [],
    funds: [],
    loading: true,
    refreshing: false,
    lastUpdate: ''
  },

  onLoad() {
    this.loadData();
  },

  onShow() {
    // Refresh data when page shows
    if (getApp().isLoggedIn()) {
      this.loadData();
    }
  },

  // Load all data: watchlist first, then all funds
  loadData() {
    this.setData({ loading: true });
    
    // Load watchlist and all funds in parallel
    Promise.all([
      fundApi.getWatchlistValuation(),
      fundApi.getFundList()
    ])
      .then(([watchlistRes, fundsRes]) => {
        const watchlist = (watchlistRes.data || []).map(fund => ({
          ...fund,
          estimatedChangePercent: parseFloat(fund.estimatedChangePercent || 0).toFixed(2)
        }));
        
        const funds = (fundsRes.data || []).map(fund => ({
          ...fund,
          estimatedChangePercent: parseFloat(fund.estimatedChangePercent || 0).toFixed(2),
          inWatchlist: !!fund.inWatchlist
        }));
        
        // Sort by change percent
        funds.sort((a, b) => {
          const aChange = parseFloat(a.estimatedChangePercent) || 0;
          const bChange = parseFloat(b.estimatedChangePercent) || 0;
          return bChange - aChange;
        });
        
        this.setData({
          watchlist,
          funds,
          loading: false,
          lastUpdate: new Date().toLocaleString()
        });
      })
      .catch(() => {
        this.setData({ loading: false });
      });
  },

  // Pull down to refresh
  onPullDownRefresh() {
    this.refreshData();
  },

  // Refresh data
  refreshData() {
    this.setData({ refreshing: true });
    
    fundApi.refreshAllValuations()
      .then(() => {
        return this.loadData();
      })
      .finally(() => {
        this.setData({ refreshing: false });
        wx.stopPullDownRefresh();
      });
  },

  // Go to fund detail
  goToDetail(e) {
    const fundId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/fund/detail?id=${fundId}`
    });
  },

  // Go to fund list page
  goToFundList() {
    wx.switchTab({
      url: '/pages/fund/fund'
    });
  },

  // Go to watchlist page
  goToWatchlist() {
    wx.switchTab({
      url: '/pages/watchlist/watchlist'
    });
  },

  // Format percentage display
  formatPercent(value) {
    if (!value && value !== 0) return '--';
    const num = parseFloat(value);
    if (isNaN(num)) return '--';
    return (num > 0 ? '+' : '') + num.toFixed(2) + '%';
  },

  // Get color class based on change
  getChangeClass(value) {
    if (!value && value !== 0) return '';
    const num = parseFloat(value);
    if (isNaN(num)) return '';
    if (num > 0) return 'positive';
    if (num < 0) return 'negative';
    return '';
  }
});