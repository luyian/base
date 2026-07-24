// pages/checkin/checkin.js
const checkinApi = require('../../api/checkin');
const app = getApp();

// 预设卡片颜色
const COLOR_OPTIONS = ['#3B82F6', '#EF4444', '#F59E0B', '#10B981', '#8B5CF6', '#EC4899', '#06B6D4', '#64748B'];

// 补零
function pad(n) {
  return n < 10 ? '0' + n : '' + n;
}

// 格式化日期为 yyyy-MM-dd
function formatDate(d) {
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

Page({
  data: {
    themeClass: '',
    loading: true,
    planList: [],
    today: '',
    selectedDate: '',  // 当前选中日期，空表示今日
    weekDays: ['日', '一', '二', '三', '四', '五', '六'],
    currentYear: 2026,
    currentMonth: 1,
    monthLabel: '',
    calendarCells: [],
    colorOptions: COLOR_OPTIONS,
    // 新增/编辑弹窗
    showModal: false,
    isEdit: false,
    form: { id: null, title: '', icon: '', color: COLOR_OPTIONS[0], remark: '', planType: 0, targetDate: '' }
  },

  onLoad() {
    const now = new Date();
    this.setData({
      today: formatDate(now),
      currentYear: now.getFullYear(),
      currentMonth: now.getMonth() + 1
    });
    this.loadData();
  },

  onShow() {
    // 从其他页返回时刷新打卡状态
    if (this.data.today) {
      this.loadData();
    }
  },

  // 加载计划列表 + 当月日历
  loadData() {
    const token = wx.getStorageSync('token');
    if (!token) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      wx.navigateTo({ url: '/pages/login/login' });
      return;
    }
    this.setData({ loading: true });
    Promise.all([this.loadPlans(this.data.selectedDate), this.loadCalendar()])
      .finally(() => this.setData({ loading: false }));
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadData();
    setTimeout(() => wx.stopPullDownRefresh(), 600);
  },

  loadPlans(date) {
    const req = date ? checkinApi.getPlanListByDate(date) : checkinApi.getPlanList();
    return req
      .then(res => this.setData({ planList: res.data || [] }))
      .catch(() => {});
  },

  loadCalendar() {
    const { currentYear, currentMonth } = this.data;
    const month = `${currentYear}-${pad(currentMonth)}`;
    return checkinApi.getCalendar(month)
      .then(res => {
        this.buildCalendar(currentYear, currentMonth, res.data || []);
      })
      .catch(() => this.buildCalendar(currentYear, currentMonth, []));
  },

  // 构建月历格子（含前后月补位）
  buildCalendar(year, month, dayData) {
    const map = {};
    dayData.forEach(d => { map[d.date] = d; });

    const firstDay = new Date(year, month - 1, 1).getDay(); // 当月1号星期几
    const daysInMonth = new Date(year, month, 0).getDate();
    const cells = [];

    // 前月补位（空白）
    for (let i = 0; i < firstDay; i++) {
      cells.push({ key: 'prev' + i, inMonth: false, day: '' });
    }
    // 当月
    for (let d = 1; d <= daysInMonth; d++) {
      const dateStr = `${year}-${pad(month)}-${pad(d)}`;
      const info = map[dateStr] || { total: 0, completed: 0 };
      cells.push({
        key: dateStr,
        inMonth: true,
        day: d,
        date: dateStr,
        total: info.total,
        completed: info.completed,
        isToday: dateStr === this.data.today,
        // 完成度 0~1，用于颜色深浅
        ratio: info.total > 0 ? info.completed / info.total : 0
      });
    }
    // 后月补位凑满整行
    const remain = (7 - (cells.length % 7)) % 7;
    for (let i = 0; i < remain; i++) {
      cells.push({ key: 'next' + i, inMonth: false, day: '' });
    }

    this.setData({
      calendarCells: cells,
      monthLabel: `${year}年${month}月`
    });
  },

  // 上/下月切换
  onPrevMonth() {
    let { currentYear, currentMonth } = this.data;
    currentMonth -= 1;
    if (currentMonth < 1) { currentMonth = 12; currentYear -= 1; }
    this.setData({ currentYear, currentMonth });
    this.loadCalendar();
  },

  onNextMonth() {
    let { currentYear, currentMonth } = this.data;
    currentMonth += 1;
    if (currentMonth > 12) { currentMonth = 1; currentYear += 1; }
    this.setData({ currentYear, currentMonth });
    this.loadCalendar();
  },

  // 点击卡片：打卡/取消
  onTogglePlan(e) {
    const id = e.currentTarget.dataset.id;
    const date = this.data.selectedDate || this.data.today;
    // 只能对今天及未来的日期打卡
    if (date < this.data.today) {
      wx.showToast({ title: '不能对过去的日期打卡', icon: 'none' });
      return;
    }
    this.togglingPlanIds = this.togglingPlanIds || {};
    if (this.togglingPlanIds[id]) {
      return;
    }
    this.togglingPlanIds[id] = true;
    checkinApi.toggleRecord(id, date)
      .then(res => {
        const checked = res.data && res.data.checked;
        const planList = this.data.planList.map(p =>
          p.id === id ? Object.assign({}, p, { todayChecked: checked }) : p);
        this.setData({ planList });
        wx.showToast({ title: checked ? '打卡成功' : '已取消', icon: 'none' });
        this.loadCalendar();
      })
      .catch(() => {})
      .finally(() => {
        delete this.togglingPlanIds[id];
      });
  },

  // 长按卡片：编辑/删除
  onLongPressPlan(e) {
    const id = e.currentTarget.dataset.id;
    const plan = this.data.planList.find(p => p.id === id);
    if (!plan) { return; }
    wx.showActionSheet({
      itemList: ['编辑', '删除'],
      success: (res) => {
        if (res.tapIndex === 0) {
          this.openEditModal(plan);
        } else if (res.tapIndex === 1) {
          this.confirmDelete(plan);
        }
      }
    });
  },

  // 点击日历日期：查看当天计划
  onTapDay(e) {
    const date = e.currentTarget.dataset.date;
    if (!date) { return; }
    // 如果点击的是已选中的日期，返回今日
    if (date === this.data.selectedDate) {
      this.setData({ selectedDate: '' });
      this.loadPlans();
    } else {
      this.setData({ selectedDate: date, loading: true });
      this.loadPlans(date).finally(() => this.setData({ loading: false }));
    }
  },

  // 长按日历日期：添加单日事件
  onLongPressDay(e) {
    const date = e.currentTarget.dataset.date;
    if (!date) { return; }
    // 只能添加今天及未来的日期
    if (date < this.data.today) {
      wx.showToast({ title: '不能添加过去的事件', icon: 'none' });
      return;
    }
    wx.showActionSheet({
      itemList: ['添加单日事件'],
      success: () => {
        this.setData({
          showModal: true,
          isEdit: false,
          form: {
            id: null,
            title: '',
            icon: '',
            color: COLOR_OPTIONS[0],
            remark: '',
            planType: 1,
            targetDate: date
          }
        });
      }
    });
  },

  // 点击标题返回今日
  onTapTitle() {
    if (this.data.selectedDate) {
      this.setData({ selectedDate: '' });
      this.loadPlans();
    }
  },

  // 打开新增弹窗（长期计划）
  onAddPlan() {
    this.setData({
      showModal: true,
      isEdit: false,
      form: { id: null, title: '', icon: '', color: COLOR_OPTIONS[0], remark: '', planType: 0, targetDate: '' }
    });
  },

  // 打开编辑弹窗
  openEditModal(plan) {
    this.setData({
      showModal: true,
      isEdit: true,
      form: {
        id: plan.id,
        title: plan.title,
        icon: plan.icon || '',
        color: plan.color || COLOR_OPTIONS[0],
        remark: plan.remark || '',
        planType: plan.planType || 0,
        targetDate: plan.targetDate || ''
      }
    });
  },

  confirmDelete(plan) {
    wx.showModal({
      title: '删除计划',
      content: `确定删除「${plan.title}」吗？`,
      confirmColor: '#EF4444',
      success: (res) => {
        if (res.confirm) {
          checkinApi.deletePlan(plan.id)
            .then(() => {
              wx.showToast({ title: '已删除', icon: 'none' });
              this.loadData();
            })
            .catch(() => {});
        }
      }
    });
  },

  // 阻止弹窗内容点击冒泡
  preventBubble() {},

  // 表单输入
  onInputTitle(e) { this.setData({ 'form.title': e.detail.value }); },
  onInputIcon(e) { this.setData({ 'form.icon': e.detail.value }); },
  onInputRemark(e) { this.setData({ 'form.remark': e.detail.value }); },
  onChooseColor(e) { this.setData({ 'form.color': e.currentTarget.dataset.color }); },

  onCloseModal() {
    this.setData({ showModal: false });
  },

  // 提交新增/编辑
  onSubmitPlan() {
    const { isEdit, form } = this.data;
    if (!form.title || !form.title.trim()) {
      wx.showToast({ title: '请输入计划名称', icon: 'none' });
      return;
    }
    const data = {
      title: form.title.trim(),
      icon: form.icon,
      color: form.color,
      remark: form.remark,
      planType: form.planType || 0,
      targetDate: form.targetDate || null
    };
    const req = isEdit
      ? checkinApi.updatePlan(form.id, data)
      : checkinApi.createPlan(data);
    req.then(() => {
        wx.showToast({ title: isEdit ? '已保存' : '已添加', icon: 'none' });
        this.setData({ showModal: false });
        this.loadData();
      })
      .catch(() => {});
  }
});
