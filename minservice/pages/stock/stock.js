// pages/stock/stock.js
const stockApi = require('../../api/stock');
const watchlistApi = require('../../api/watchlist');
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
    watchlistMap: {},
    newStock: {
      stockCode: '',
      stockName: '',
      market: '',
      industry: ''
    },
    markets: ['SH', 'SZ', 'BJ', 'HK'],
    marketIndex: 0,
    showEditModal: false,
    originalStockCode: '',
    editStock: {
      stockCode: '',
      stockName: '',
      market: '',
      industry: ''
    },
    editMarketIndex: 0
  },

  onLoad() {
    this.applyTheme();
    this.setData({ isAdmin: app.isAdmin() });
    this.loadStocks();
    this.loadIndustryOptions();
    this.loadWatchlistStatus();
  },

  onShow() {
    this.applyTheme();
    this.loadWatchlistStatus();
  },

  applyTheme() {
    const theme = app.getTheme();
    this.setData({
      themeClass: theme === 'dark' ? 'dark-theme' : 'light-theme'
    });
  },

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

  loadWatchlistStatus() {
    const token = wx.getStorageSync('token');
    if (!token) return;

    watchlistApi.getWatchlist()
      .then(res => {
        const list = res.data || [];
        const watchlistMap = {};
        list.forEach(item => {
          watchlistMap[item.stockCode] = item.id;
        });
        this.setData({ watchlistMap });
      })
      .catch(() => {});
  },

  toggleWatchlist(e) {
    const token = wx.getStorageSync('token');
    if (!token) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }

    const { code, name, market } = e.currentTarget.dataset;
    const isInWatchlist = this.data.watchlistMap[code];

    if (isInWatchlist) {
      watchlistApi.removeFromWatchlist(isInWatchlist)
        .then(() => {
          const watchlistMap = { ...this.data.watchlistMap };
          delete watchlistMap[code];
          this.setData({ watchlistMap });
          wx.showToast({ title: '已取消自选', icon: 'success' });
        })
        .catch(err => {
          wx.showToast({ title: err.message || '操作失败', icon: 'none' });
        });
    } else {
      watchlistApi.addToWatchlist(code)
        .then(res => {
          const watchlistMap = { ...this.data.watchlistMap };
          watchlistMap[code] = res.data || true;
          this.setData({ watchlistMap });
          wx.showToast({ title: '已添加自选', icon: 'success' });
        })
        .catch(err => {
          wx.showToast({ title: err.message || '操作失败', icon: 'none' });
        });
    }
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

  stopPropagation() {},

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

  showEditModal(e) {
    const stock = e.currentTarget.dataset.stock;
    const editMarketIndex = this.data.markets.indexOf(stock.market);
    this.setData({
      showEditModal: true,
      originalStockCode: stock.stockCode,
      editStock: {
        stockCode: stock.stockCode,
        stockName: stock.stockName,
        market: stock.market || 'SH',
        industry: stock.industry || ''
      },
      editMarketIndex: editMarketIndex >= 0 ? editMarketIndex : 0
    });
  },

  hideEditModal() {
    this.setData({ showEditModal: false });
  },

  onEditStockCodeChange(e) {
    this.setData({ 'editStock.stockCode': e.detail.value });
  },

  onEditStockNameChange(e) {
    this.setData({ 'editStock.stockName': e.detail.value });
  },

  onEditMarketChange(e) {
    this.setData({
      editMarketIndex: e.detail.value,
      'editStock.market': this.data.markets[e.detail.value]
    });
  },

  onEditIndustryChange(e) {
    this.setData({ 'editStock.industry': e.detail.value });
  },

  submitEditStock() {
    const { stockCode, stockName, market, industry } = this.data.editStock;
    if (!stockCode || !stockName) {
      wx.showToast({ title: '请填写股票代码和名称', icon: 'none' });
      return;
    }
    this.setData({ submitting: true });
    stockApi.updateStock(this.data.originalStockCode, { stockCode, stockName, market, industry })
      .then(() => {
        wx.showToast({ title: '更新成功', icon: 'success' });
        this.setData({ showEditModal: false });
        this.loadStocks();
      })
      .catch(err => {
        wx.showToast({ title: err.message || '更新失败', icon: 'none' });
      })
      .finally(() => {
        this.setData({ submitting: false });
      });
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
