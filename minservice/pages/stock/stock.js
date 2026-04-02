// pages/stock/stock.js
const stockApi = require('../../api/stock');
const app = getApp();

Page({
  data: {
    stocks: [],
    loading: true,
    page: 1,
    size: 20,
    hasMore: true,
    keyword: '',
    industry: '',
    industries: [],
    themeClass: '',
    isAdmin: false,
    showAddModal: false,
    submitting: false,
    newStock: {
      stockCode: '',
      stockName: '',
      market: '',
      industry: ''
    },
    markets: ['SH', 'SZ', 'HK'],
    marketIndex: 0
  },

  onLoad() {
    this.applyTheme();
    this.setData({ isAdmin: app.isAdmin() });
    this.loadStocks();
    this.loadIndustryOptions();
  },

  onShow() {
    this.applyTheme();
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

  loadIndustryOptions() {
    stockApi.getIndustryOptions()
      .then(res => {
        this.setData({ industries: res.data || [] });
      })
      .catch(() => {});
  },

  loadStocks(loadMore = false) {
    if (!loadMore) {
      this.setData({ loading: true, page: 1 });
    }
    
    const data = {
      page: this.data.page,
      size: this.data.size
    };
    
    if (this.data.keyword) {
      data.keyword = this.data.keyword;
    }
    if (this.data.industry) {
      data.industry = this.data.industry;
    }
    
    return stockApi.getStockList(data)
      .then(res => {
        const list = res.data?.records || [];
        this.setData({
          stocks: loadMore ? [...this.data.stocks, ...list] : list,
          loading: false,
          hasMore: list.length >= this.data.size,
          page: this.data.page + 1
        });
      })
      .catch(() => {
        this.setData({ loading: false });
      });
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadStocks(true);
    }
  },

  onPullDownRefresh() {
    this.setData({ page: 1, hasMore: true });
    this.loadStocks().finally(() => {
      wx.stopPullDownRefresh();
    });
  },

  onSearch(e) {
    this.setData({ keyword: e.detail.value, page: 1 });
    this.loadStocks();
  },

  onIndustryChange(e) {
    this.setData({ industry: e.detail.value, page: 1 });
    this.loadStocks();
  },

  goToDetail(e) {
    const stockCode = e.currentTarget.dataset.code;
    wx.navigateTo({ url: `/pages/stock/detail?code=${stockCode}` });
  },

  // 新增股票
  showAddModal() {
    this.setData({
      showAddModal: true,
      newStock: { stockCode: '', stockName: '', market: 'SH', industry: '' }
    });
  },

  hideAddModal() {
    this.setData({ showAddModal: false });
  },

  onStockCodeChange(e) {
    this.setData({ 'newStock.stockCode': e.detail.value });
  },

  onStockNameChange(e) {
    this.setData({ 'newStock.stockName': e.detail.value });
  },

  onMarketChange(e) {
    this.setData({
      marketIndex: e.detail.value,
      'newStock.market': this.data.markets[e.detail.value]
    });
  },

  onIndustryChangeInput(e) {
    this.setData({ 'newStock.industry': e.detail.value });
  },

  submitAddStock() {
    const { stockCode, stockName, market, industry } = this.data.newStock;
    if (!stockCode || !stockName) {
      wx.showToast({ title: '请填写股票代码和名称', icon: 'none' });
      return;
    }
    this.setData({ submitting: true });
    stockApi.createStock(this.data.newStock)
      .then(() => {
        wx.showToast({ title: '添加成功', icon: 'success' });
        this.setData({ showAddModal: false });
        this.loadStocks();
      })
      .catch(err => {
        wx.showToast({ title: err.message || '添加失败', icon: 'none' });
      })
      .finally(() => {
        this.setData({ submitting: false });
      });
  }
});