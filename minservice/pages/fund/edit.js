// pages/fund/edit.js - Fund edit/Create page
const fundApi = require('../../api/fund');
const stockApi = require('../../api/stock');

Page({
  data: {
    isEdit: false,
    fundId: null,
    form: {
      fundName: '',
      fundCode: '',
      description: '',
      holdings: []
    },
    loading: false,
    submitting: false,
    stockOptions: [],
    stockSearchLoading: false
  },

  onLoad(options) {
    this.applyTheme();
    if (options.id) {
      this.setData({ isEdit: true, fundId: parseInt(options.id) });
      this.loadFundDetail(options.id);
    }
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

  loadFundDetail(fundId) {
    this.setData({ loading: true });
    fundApi.getFundById(fundId)
      .then(res => {
        const fund = res.data;
        this.setData({
          form: {
            fundName: fund.fundName || '',
            fundCode: fund.fundCode || '',
            description: fund.description || '',
            holdings: (fund.holdings || []).map(h => ({
              stockCode: h.stockCode,
              stockName: h.stockName,
              weight: h.weight
            }))
          },
          loading: false
        });
      })
      .catch(() => {
        this.setData({ loading: false });
        wx.showToast({ title: '获取失败', icon: 'none' });
      });
  },

  onNameChange(e) {
    this.setData({ 'form.fundName': e.detail.value });
  },

  onCodeChange(e) {
    this.setData({ 'form.fundCode': e.detail.value });
  },

  onDescChange(e) {
    this.setData({ 'form.description': e.detail.value });
  },

  onStockSearch(e) {
    const keyword = e.detail.value;
    if (!keyword || keyword.length < 1) {
      this.setData({ stockOptions: [] });
      return;
    }
    this.setData({ stockSearchLoading: true });
    stockApi.getStockList({ keyword, page: 1, size: 20 })
      .then(res => {
        this.setData({
          stockOptions: res.data?.records || [],
          stockSearchLoading: false
        });
      })
      .catch(() => {
        this.setData({ stockSearchLoading: false });
      });
  },

  onStockSelect(e) {
    const index = e.currentTarget.dataset.index;
    const stock = this.data.stockOptions[index];
    const holdings = this.data.form.holdings;
    holdings[index] = {
      stockCode: stock.stockCode,
      stockName: stock.stockName,
      weight: holdings[index]?.weight || null
    };
    this.setData({ 'form.holdings': holdings, stockOptions: [] });
  },

  onWeightChange(e) {
    const index = e.currentTarget.dataset.index;
    const value = parseFloat(e.detail.value);
    const holdings = [...this.data.form.holdings];
    if (holdings[index]) {
      holdings[index].weight = value;
    }
    this.setData({ 'form.holdings': holdings });
  },

  addHolding() {
    const holdings = [...this.data.form.holdings, { stockCode: '', stockName: '', weight: null }];
    this.setData({ 'form.holdings': holdings });
  },

  removeHolding(e) {
    const index = e.currentTarget.dataset.index;
    if (this.data.form.holdings.length <= 1) {
      wx.showToast({ title: '至少保留一个持仓', icon: 'none' });
      return;
    }
    const holdings = [...this.data.form.holdings];
    holdings.splice(index, 1);
    this.setData({ 'form.holdings': holdings });
  },

  submit() {
    const { form, isEdit, fundId } = this.data;
    
    if (!form.fundName) {
      wx.showToast({ title: '请输入基金名称', icon: 'none' });
      return;
    }
    
    const validHoldings = form.holdings.filter(h => h.stockCode && h.weight);
    if (validHoldings.length === 0) {
      wx.showToast({ title: '请添加至少一个持仓', icon: 'none' });
      return;
    }

    this.setData({ submitting: true });
    
    const data = {
      fundName: form.fundName,
      fundCode: form.fundCode,
      description: form.description,
      holdings: validHoldings
    };
    
    const api = isEdit 
      ? fundApi.updateFund(fundId, data)
      : fundApi.createFund(data);
    
    api
      .then(() => {
        wx.showToast({ title: isEdit ? '更新成功' : '创建成功', icon: 'success' });
        setTimeout(() => wx.navigateBack(), 1500);
      })
      .catch(err => {
        wx.showToast({ title: err.message || '操作失败', icon: 'none' });
      })
      .finally(() => {
        this.setData({ submitting: false });
      });
  }
});