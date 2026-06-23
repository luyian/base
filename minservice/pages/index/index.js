// pages/index/index.js
const fundApi = require('../../api/fund');
const app = getApp();

Page({
  data: {
    watchlist: [],
    funds: [],
    loading: true,
    refreshing: false,
    lastUpdate: '',
    themeClass: '',
    topActiveId: null
  },

  onLoad() {
    const theme = app.getTheme();
    this.applyTheme();
    this.loadData();
  },

  onShow() {
    this.applyTheme();
    // Refresh data when page shows
    if (getApp().isLoggedIn()) {
      this.loadData();
    }
  },

  // 应用主题
  applyTheme() {
    const theme = app.getTheme();
    this.setData({
      themeClass: theme === 'dark' ? 'dark-theme' : 'light-theme'
    });
  },

  // 页面样式设置（供 app.js 调用）
  setTheme(theme) {
    this.applyTheme();
  },

  // Load all data: watchlist first, then all funds
  loadData() {
    this.setData({ loading: true });
    
    return Promise.all([
      fundApi.getWatchlistValuation(),
      fundApi.getFundList()
    ])
      .then(([watchlistRes, fundsRes]) => {
        const watchlist = (watchlistRes.data || []).map(fund => ({
          ...fund,
          id: fund.fundId || fund.id,
          estimatedChangePercent: parseFloat(fund.estimatedChangePercent || 0).toFixed(2)
        }));
        
        const funds = (fundsRes.data || []).map(fund => ({
          ...fund,
          id: fund.fundId || fund.id, // 确保 id 字段存在
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

  // 长按自选卡片，显示置顶操作浮层
  onWatchlistLongPress(e) {
    const fundId = e.currentTarget.dataset.id;
    this.setData({ topActiveId: fundId });
  },

  // 关闭置顶操作浮层
  closeTopMenu() {
    if (this.data.topActiveId !== null) {
      this.setData({ topActiveId: null });
    }
  },

  // 置顶指定自选基金（前端乐观更新，接口后台异步执行）
  onTopFund(e) {
    const fundId = e.currentTarget.dataset.id;
    // 前端立即将目标基金移到列表最前
    const watchlist = this.data.watchlist.slice();
    const index = watchlist.findIndex(item => item.fundId === fundId);
    if (index > 0) {
      const [target] = watchlist.splice(index, 1);
      watchlist.unshift(target);
    }
    this.setData({ watchlist, topActiveId: null });
    wx.showToast({ title: '已置顶', icon: 'success' });
    // 后台静默持久化，失败不回滚也不提示（下次进入页面按后端真实顺序刷新）
    fundApi.topWatchlist(fundId).catch(() => {});
  },

  // 取消自选（删除）指定基金（前端乐观更新，接口后台异步执行）
  onRemoveFund(e) {
    const fundId = e.currentTarget.dataset.id;
    const fundName = e.currentTarget.dataset.name || '该基金';
    this.setData({ topActiveId: null });
    wx.showModal({
      title: '取消自选',
      content: `确定将「${fundName}」移出自选？`,
      success: (res) => {
        if (!res.confirm) {
          return;
        }
        // 前端立即移除该基金
        const watchlist = this.data.watchlist.filter(item => item.fundId !== fundId);
        this.setData({ watchlist });
        wx.showToast({ title: '已取消自选', icon: 'success' });
        // 后台静默持久化，失败不回滚也不提示（下次进入页面按后端真实顺序刷新）
        fundApi.removeFromWatchlist(fundId).catch(() => {});
      }
    });
  },

  // Go to fund detail
  goToDetail(e) {
    // 置顶浮层激活时，点击仅关闭浮层，不跳转详情
    if (this.data.topActiveId !== null) {
      this.setData({ topActiveId: null });
      return;
    }
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
  },

  // 跳转 AI 助手
  goToAi() {
    wx.navigateTo({
      url: '/pages/ai/ai'
    });
  }
});