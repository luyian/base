// pages/barcode/barcode.js - 商品管理（元灵工具箱）
// 职责：商品列表管理（浏览/添加/编辑/删除）+ 扫码编辑 + 行内扫码绑定（先选商品再扫码）。
const productApi = require('../../api/product');
const app = getApp();

// 条码类型（与后端一致：1条码 2二维码）
const TYPE_CODE = 1;
const TYPE_QR = 2;

Page({
  data: {
    themeClass: '',
    // 商品列表
    list: [],
    page: 1,
    size: 10,
    hasMore: true,
    loading: false,
    // 表单弹层态
    formVisible: false,   // 是否显示添加/编辑表单弹层
    formTitle: '',        // 弹层标题
    editingId: null,      // 编辑中商品ID（null 表示新增）
    scanCodeTitle: '',    // 顶部扫码命中时的 code 徽章（可空）
    form: {
      name: '',
      category: '',
      spec: '',
      unit: '',
      salePrice: '',
      costPrice: '',
      stock: 0,
      supplier: '',
      productionDate: '',
      remark: ''
    },
    saving: false
  },

  onLoad() {
    this.applyTheme();
    this.loadList(true);
  },

  onShow() {
    this.applyTheme();
  },

  onReachBottom() {
    this.loadList(false);
  },

  applyTheme() {
    const theme = app.getTheme();
    this.setData({
      themeClass: theme === 'dark' ? 'dark-theme' : 'light-theme'
    });
  },

  // 页面样式设置（供 app.js 调用）
  setTheme() {
    this.applyTheme();
  },

  // 加载商品列表（reset=true 时从第一页重新加载）
  async loadList(reset) {
    if (this.loading) {
      return;
    }
    this.loading = true;
    const page = reset ? 1 : this.data.page;
    const size = this.data.size;
    if (reset) {
      this.setData({ list: [], hasMore: true });
    }
    this.setData({ loading: true });
    try {
      const res = await productApi.listProducts({ page, size });
      const pageData = res.data || res || {};
      const records = pageData.records || [];
      const total = pageData.total || 0;
      const list = reset ? records : this.data.list.concat(records);
      this.setData({
        list,
        page: page + 1,
        hasMore: list.length < total,
        loading: false
      });
    } catch (e) {
      this.setData({ loading: false });
      // 错误由 request 统一提示
    } finally {
      this.loading = false;
    }
  },

  // 顶部大扫码：命中的商品进入编辑，否则提示
  onScan() {
    const that = this;
    wx.scanCode({
      onlyFromCamera: false,
      scanType: ['barCode', 'qrCode'],
      success: async (res) => {
        const code = (res.result || '').trim();
        if (!code) {
          wx.showToast({ title: '未识别到条码内容', icon: 'none' });
          return;
        }
        await that.lookupProduct(code);
      },
      fail: () => {
        // 用户取消扫码，不提示
      }
    });
  },

  // 扫码命中：命中 → 打开编辑表单；未命中 → 提示
  async lookupProduct(code) {
    wx.showLoading({ title: '查询中...' });
    try {
      const res = await productApi.scanProduct(code);
      const product = res.data || res;
      if (product) {
        this.fillForm(product, product.id, code);
      } else {
        wx.showToast({ title: '未命中商品，可用列表「扫码绑定」', icon: 'none' });
      }
    } catch (e) {
      // 异常由 request 统一提示
    } finally {
      wx.hideLoading();
    }
  },

  // 打开新增表单
  onAdd() {
    this.setData({
      formTitle: '添加商品',
      editingId: null,
      scanCodeTitle: '',
      form: this.emptyForm(),
      formVisible: true
    });
  },

  // 打开编辑表单（列表行 or 扫码命中共用）
  openEdit(product) {
    this.fillForm(product, product.id, '');
  },

  // 列表行「编辑」入口
  onRowEdit(e) {
    const product = e.currentTarget.dataset.product || {};
    this.fillForm(product, product.id, '');
  },

  // 空操作（拦截弹层背景点击冒泡）
  noop() {},

  fillForm(product, id, codeTitle) {
    this.setData({
      formTitle: id ? '编辑商品' : '添加商品',
      editingId: id,
      scanCodeTitle: codeTitle,
      form: {
        code: product.code || '',
        name: product.name || '',
        category: product.category || '',
        spec: product.spec || '',
        unit: product.unit || '',
        salePrice: product.salePrice != null ? String(product.salePrice) : '',
        costPrice: product.costPrice != null ? String(product.costPrice) : '',
        stock: product.stock || 0,
        supplier: product.supplier || '',
        productionDate: product.productionDate || '',
        remark: product.remark || ''
      },
      formVisible: true
    });
  },

  emptyForm() {
    return {
      name: '', category: '', spec: '', unit: '',
      salePrice: '', costPrice: '', stock: 0, supplier: '',
      productionDate: '', remark: ''
    };
  },

  // 关闭表单弹层
  onCloseForm() {
    this.setData({ formVisible: false, scanCodeTitle: '' });
  },

  // 输入绑定（表单复用 onInput；picker date 以 detail.value 直接给到 target.dataset.field）
  onInput(e) {
    const field = e.currentTarget.dataset.field;
    const value = e.detail.value;
    this.setData({ [`form.${field}`]: value });
  },

  // 保存（新增或编辑）
  async onSave() {
    const { form, editingId, scanCodeTitle } = this.data;
    if (!form.name || !form.name.trim()) {
      wx.showToast({ title: '请填写商品名称', icon: 'none' });
      return;
    }
    const payload = {
      name: form.name.trim(),
      category: form.category,
      spec: form.spec,
      unit: form.unit,
      salePrice: form.salePrice || null,
      costPrice: form.costPrice || null,
      stock: Number(form.stock) || 0,
      supplier: form.supplier,
      productionDate: form.productionDate,
      remark: form.remark
    };

    this.setData({ saving: true });
    try {
      if (editingId) {
        await productApi.updateProduct(editingId, payload);
        wx.showToast({ title: '保存成功', icon: 'success' });
      } else {
        await productApi.createProduct(payload);
        wx.showToast({ title: '新增成功', icon: 'success' });
      }
      this.setData({ formVisible: false, scanCodeTitle: '' });
      this.loadList(true);
    } catch (e) {
      // 错误由 request 统一提示
    } finally {
      this.setData({ saving: false });
    }
  },

  // 行内扫码绑定（先选商品再扫码，顺序已确认）
  onBindScan(e) {
    const { id } = e.currentTarget.dataset;
    const that = this;
    wx.scanCode({
      onlyFromCamera: false,
      scanType: ['barCode', 'qrCode'],
      success: async (res) => {
        const code = (res.result || '').trim();
        if (!code) {
          wx.showToast({ title: '未识别到条码内容', icon: 'none' });
          return;
        }
        const type = (res.scanType === 'qrCode') ? TYPE_QR : TYPE_CODE;
        await that.bindCode(id, code, type);
      },
      fail: () => {
        // 用户取消扫码，不提示
      }
    });
  },

  // 绑定条码到指定商品
  async bindCode(id, code, type) {
    wx.showLoading({ title: '绑定中...' });
    try {
      await productApi.bindCode(id, { code, type, source: 1 });
      wx.showToast({ title: '绑定成功', icon: 'success' });
      this.loadList(false);
    } catch (e) {
      // 错误由 request 统一提示
    } finally {
      wx.hideLoading();
    }
  },

  // 删除商品
  onDelete(e) {
    const { id, name } = e.currentTarget.dataset;
    const that = this;
    wx.showModal({
      title: '删除商品',
      content: `确定删除「${name || ''}」吗？删除后不可恢复。`,
      confirmColor: '#C6402E',
      success: async (r) => {
        if (!r.confirm) {
          return;
        }
        try {
          await productApi.deleteProduct(id);
          wx.showToast({ title: '删除成功', icon: 'success' });
          that.loadList(true);
        } catch (e) {
          // 错误由 request 统一提示
        }
      }
    });
  }
});