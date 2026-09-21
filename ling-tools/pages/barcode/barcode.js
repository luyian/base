// pages/barcode/barcode.js - 商品条码扫码 + 编辑（元灵工具箱）
// 职责：仅负责扫码命中商品 + 编辑表单保存，不生成条码。
const productApi = require('../../api/product');
const app = getApp();

// 补零
function pad(n) {
  return n < 10 ? '0' + n : '' + n;
}

Page({
  data: {
    themeClass: '',
    // 扫码结果态
    scanned: false,       // 是否已完成一次扫码
    found: false,         // 是否命中已有商品
    code: '',             // 当前扫码内容
    productId: null,      // 命中商品ID（未命中为 null）
    // 商品表单
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
    saving: false,
    showResult: false
  },

  onLoad() {
    this.applyTheme();
  },

  onShow() {
    this.applyTheme();
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

  // 扫码
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
        await that.lookupCode(code);
      },
      fail: () => {
        // 用户取消扫码，不提示
      }
    });
  },

  // 根据编码查商品：命中 → 展示编辑；未命中 → 新建表单带 code
  async lookupCode(code) {
    wx.showLoading({ title: '查询中...' });
    try {
      const res = await productApi.scanProduct(code);
      const product = res.data || res;
      if (product) {
        this.setData({
          scanned: true,
          found: true,
          code,
          productId: product.id,
          form: {
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
          showResult: true
        });
      } else {
        // 未命中：新建表单，绑定该码
        this.setData({
          scanned: true,
          found: false,
          code,
          productId: null,
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
          showResult: true
        });
      }
    } catch (e) {
      // 查询异常由 request 统一提示
    } finally {
      wx.hideLoading();
    }
  },

  // 输入绑定
  onInput(e) {
    const field = e.currentTarget.dataset.field;
    this.setData({
      [`form.${field}`]: e.detail.value
    });
  },

  // 保存（新增或编辑）
  async onSave() {
    const { form, found, productId, code } = this.data;
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
      if (found && productId) {
        await productApi.updateProduct(productId, payload);
        wx.showToast({ title: '保存成功', icon: 'success' });
      } else {
        const res = await productApi.createProduct(payload);
        const newId = res;
        // 绑定这次扫码的条码到新商品
        try {
          await productApi.bindCode(newId, { code, type: 1, source: 1 });
        } catch (e) {
          // 绑定失败不阻断保存（同码已存在等场景）
        }
        wx.showToast({ title: '新增成功', icon: 'success' });
      }
      // 保存成功后复位，等待下一次扫码
      this.setData({
        showResult: false,
        scanned: false,
        found: false,
        code: '',
        productId: null,
        form: {
          name: '', category: '', spec: '', unit: '',
          salePrice: '', costPrice: '', stock: 0, supplier: '',
          productionDate: '', remark: ''
        }
      });
    } catch (e) {
      // 错误由 request 统一提示
    } finally {
      this.setData({ saving: false });
    }
  },

  // 取消/关闭结果
  onClose() {
    this.setData({
      showResult: false,
      scanned: false,
      found: false,
      code: '',
      productId: null
    });
  }
});