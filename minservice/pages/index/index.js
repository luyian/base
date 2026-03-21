// pages/index/index.js
const fundApi = require('../../api/fund');

Page({
  data: {
    funds: [],
    loading: true,
    refreshing: false,
    lastUpdate: ''
  },

  onLoad() {
    this.loadFunds();
  },

  onShow() {
    // Refresh data when page shows
    if (getApp().isLoggedIn()) {
      this.loadFunds();
    }
  },

  // Load fund list
  loadFunds() {
    this.setData({ loading: true });
    
    fundApi.getFundList()
      .then(res => {
        const funds = res.data || [];
        // Sort by change percent
        funds.sort((a, b) => {
          const aChange = parseFloat(a.estimatedChangePercent) || 0;
          const bChange = parseFloat(b.estimatedChangePercent) || 0;
          return bChange - aChange;
        });
        
        this.setData({
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
        return this.loadFunds();
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