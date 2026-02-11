<template>
  <div class="fund-container">
    <!-- 操作栏 -->
    <el-card class="action-card" shadow="never">
      <el-form :inline="true">
        <el-form-item>
          <el-switch v-model="onlyWatchlist" active-text="仅自选" @change="fetchFundList" />
        </el-form-item>
        <el-form-item v-if="hasPermission('stock:fund:add')">
          <el-button type="primary" :icon="Plus" @click="handleAdd">新建基金</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 基金列表 -->
    <el-row :gutter="20" v-loading="loading">
      <el-col :xs="24" :sm="12" :md="8" :lg="6" v-for="fund in fundList" :key="fund.fundId">
        <el-card class="fund-card" shadow="hover" @click="handleViewDetail(fund)">
          <template #header>
            <div class="fund-header">
              <span class="fund-name">{{ fund.fundName }}</span>
              <div class="fund-actions" @click.stop>
                <!-- 自选按钮 -->
                <el-button
                  v-if="fund.inWatchlist"
                  type="warning"
                  size="small"
                  link
                  @click="handleRemoveWatchlist(fund)"
                >
                  <el-icon><StarFilled /></el-icon>
                </el-button>
                <el-button
                  v-else
                  type="info"
                  size="small"
                  link
                  @click="handleAddWatchlist(fund)"
                >
                  <el-icon><Star /></el-icon>
                </el-button>
                <!-- 管理员操作 -->
                <el-dropdown v-if="isAdmin" trigger="click">
                  <el-icon class="more-icon"><MoreFilled /></el-icon>
                  <template #dropdown>
                    <el-dropdown-menu>
                      <el-dropdown-item @click="handleEdit(fund)">
                        <el-icon><Edit /></el-icon>编辑
                      </el-dropdown-item>
                      <el-dropdown-item @click="handleDelete(fund)" divided>
                        <el-icon><Delete /></el-icon>删除
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </div>
            </div>
          </template>
          <div class="fund-content">
            <div class="valuation-row" v-if="fund.cacheTime">
              <span class="label">估算涨跌幅</span>
              <span class="value" :class="getChangeClass(fund.estimatedChangePercent)">
                {{ formatChangePercent(fund.estimatedChangePercent) }}
              </span>
            </div>
            <div class="info-row" v-if="fund.cacheTime">
              <span class="label">持仓数量</span>
              <span class="value">{{ fund.holdingCount || 0 }} 只</span>
            </div>
            <div class="info-row" v-if="fund.fundCode">
              <span class="label">基金代码</span>
              <span class="value">{{ fund.fundCode }}</span>
            </div>
            <div class="info-row" v-if="!fund.cacheTime">
              <span class="label">暂无估值</span>
              <span class="value">点击查看获取</span>
            </div>
          </div>
          <div class="fund-footer">
            <span class="cache-time" v-if="fund.cacheTime">
              {{ formatCacheTime(fund.cacheTime) }}
            </span>
            <el-button type="primary" size="small" link @click.stop="handleViewDetail(fund)">
              查看估值
            </el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 空状态 -->
    <el-empty v-if="!loading && fundList.length === 0" :description="onlyWatchlist ? '暂无自选基金' : '暂无基金配置'">
      <el-button v-if="isAdmin && !onlyWatchlist" type="primary" @click="handleAdd">创建第一个基金</el-button>
    </el-empty>

    <!-- 基金详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="currentFund.fundName + ' - 实时估值'"
      width="80%"
      destroy-on-close
    >
      <div class="detail-header">
        <el-descriptions :column="4" border>
          <el-descriptions-item label="基金名称">{{ currentFund.fundName }}</el-descriptions-item>
          <el-descriptions-item label="基金代码">{{ currentFund.fundCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="估算涨跌幅">
            <span :class="getChangeClass(currentFund.estimatedChangePercent)">
              {{ formatChangePercent(currentFund.estimatedChangePercent) }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="持仓数量">{{ currentFund.holdingCount || 0 }} 只</el-descriptions-item>
        </el-descriptions>
        <div class="detail-actions">
          <el-button type="success" :icon="Refresh" @click="handleRefreshValuation" :loading="valuationLoading">
            刷新估值
          </el-button>
          <el-button v-if="isAdmin" type="primary" :icon="Edit" @click="handleEditFromDetail">
            编辑基金
          </el-button>
        </div>
      </div>

      <el-table :data="currentFund.quotes || []" border stripe style="margin-top: 20px" v-loading="valuationLoading">
        <el-table-column prop="stockCode" label="股票代码" width="120" align="center" />
        <el-table-column prop="stockName" label="股票名称" width="150" align="center" />
        <el-table-column prop="market" label="市场" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.market === 'HK'" type="danger" size="small">港股</el-tag>
            <el-tag v-else-if="row.market === 'SH'" type="primary" size="small">沪市</el-tag>
            <el-tag v-else-if="row.market === 'SZ'" type="success" size="small">深市</el-tag>
            <el-tag v-else type="info" size="small">{{ row.market }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="当前价" width="100" align="right">
          <template #default="{ row }">
            {{ row.price ? row.price.toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="changePercent" label="涨跌幅" width="100" align="right">
          <template #default="{ row }">
            <span :class="getChangeClass(row.changePercent)">
              {{ formatChangePercent(row.changePercent) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="weight" label="权重(%)" width="100" align="right">
          <template #default="{ row }">
            {{ row.weight ? row.weight.toFixed(2) : '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="weightedChangePercent" label="加权涨跌幅" width="120" align="right">
          <template #default="{ row }">
            <span :class="getChangeClass(row.weightedChangePercent)">
              {{ row.weightedChangePercent ? row.weightedChangePercent.toFixed(4) + '%' : '-' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="success" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.success" type="success" size="small">成功</el-tag>
            <el-tooltip v-else :content="row.errorMsg" placement="top">
              <el-tag type="danger" size="small">失败</el-tag>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 编辑弹窗 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="isEdit ? '编辑基金' : '新建基金'"
      width="800px"
      destroy-on-close
    >
      <el-form ref="formRef" :model="formData" :rules="formRules" label-width="100px">
        <el-form-item label="基金名称" prop="fundName">
          <el-input v-model="formData.fundName" placeholder="请输入基金名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="基金代码" prop="fundCode">
          <el-input v-model="formData.fundCode" placeholder="请输入基金代码（可选）" maxlength="50" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input
            v-model="formData.description"
            type="textarea"
            :rows="3"
            placeholder="请输入描述（可选）"
            maxlength="500"
          />
        </el-form-item>
        <el-form-item label="持仓配置" prop="holdings">
          <div class="holdings-container">
            <div class="holdings-header">
              <span>股票代码</span>
              <span>权重(%)</span>
              <span>操作</span>
            </div>
            <div class="holding-item" v-for="(holding, index) in formData.holdings" :key="index">
              <el-select
                v-model="holding.stockCode"
                filterable
                remote
                reserve-keyword
                placeholder="搜索股票"
                :remote-method="searchStock"
                :loading="stockSearchLoading"
                style="width: 300px"
              >
                <el-option
                  v-for="stock in stockOptions"
                  :key="stock.stockCode"
                  :label="`${stock.stockCode} - ${stock.stockName}`"
                  :value="stock.stockCode"
                />
              </el-select>
              <el-input-number
                v-model="holding.weight"
                :min="0"
                :max="100"
                :precision="2"
                :step="1"
                placeholder="权重"
                style="width: 150px"
              />
              <el-button type="danger" :icon="Delete" @click="removeHolding(index)" />
            </div>
            <div class="holdings-footer">
              <el-button type="primary" :icon="Plus" @click="addHolding">添加持仓</el-button>
              <span class="total-weight">
                总权重: {{ totalWeight.toFixed(2) }}%
                <el-tag v-if="totalWeight === 100" type="success" size="small">正常</el-tag>
                <el-tag v-else type="warning" size="small">建议100%</el-tag>
              </span>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Edit, Delete, MoreFilled, Star, StarFilled } from '@element-plus/icons-vue'
import { listFunds, getFundById, createFund, updateFund, deleteFund, getFundValuation, addWatchlist, removeWatchlist } from '@/api/fund'
import { listStocks } from '@/api/stock'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()

// 权限判断
const hasPermission = (perm) => userStore.hasPermission(perm)
const isAdmin = computed(() => {
  return hasPermission('stock:fund:add') || hasPermission('stock:fund:edit') || hasPermission('stock:fund:delete')
})

// 自选筛选
const onlyWatchlist = ref(false)

// 基金列表
const allFundList = ref([])
const fundList = computed(() => {
  if (onlyWatchlist.value) {
    return allFundList.value.filter(f => f.inWatchlist)
  }
  return allFundList.value
})
const loading = ref(false)

// 详情弹窗
const detailDialogVisible = ref(false)
const currentFund = ref({})
const valuationLoading = ref(false)

// 编辑弹窗
const editDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const submitting = ref(false)

// 表单数据
const formData = reactive({
  fundName: '',
  fundCode: '',
  description: '',
  holdings: []
})

// 表单校验规则
const formRules = {
  fundName: [
    { required: true, message: '请输入基金名称', trigger: 'blur' }
  ],
  holdings: [
    {
      validator: (rule, value, callback) => {
        if (!value || value.length === 0) {
          callback(new Error('请至少添加一个持仓'))
        } else {
          const hasEmpty = value.some(h => !h.stockCode || h.weight === null || h.weight === undefined)
          if (hasEmpty) {
            callback(new Error('请完善持仓信息'))
          } else {
            callback()
          }
        }
      },
      trigger: 'change'
    }
  ]
}

// 股票搜索
const stockOptions = ref([])
const stockSearchLoading = ref(false)

// 计算总权重
const totalWeight = computed(() => {
  return formData.holdings.reduce((sum, h) => sum + (h.weight || 0), 0)
})

// 初始化
onMounted(() => {
  fetchFundList()
})

// 获取基金列表
const fetchFundList = async () => {
  loading.value = true
  try {
    const res = await listFunds()
    allFundList.value = res.data || []
  } catch (error) {
    console.error('获取基金列表失败', error)
    ElMessage.error('获取基金列表失败')
  } finally {
    loading.value = false
  }
}

// 加自选
const handleAddWatchlist = async (fund) => {
  try {
    await addWatchlist(fund.fundId)
    fund.inWatchlist = true
    ElMessage.success('已加入自选')
  } catch (error) {
    console.error('加自选失败', error)
    ElMessage.error('加自选失败')
  }
}

// 取消自选
const handleRemoveWatchlist = async (fund) => {
  try {
    await removeWatchlist(fund.fundId)
    fund.inWatchlist = false
    ElMessage.success('已取消自选')
  } catch (error) {
    console.error('取消自选失败', error)
    ElMessage.error('取消自选失败')
  }
}

// 查看详情
const handleViewDetail = async (fund) => {
  currentFund.value = {
    fundId: fund.fundId,
    fundName: fund.fundName,
    fundCode: fund.fundCode,
    description: fund.description,
    estimatedChangePercent: fund.estimatedChangePercent,
    holdingCount: fund.holdingCount,
    quotes: fund.quotes || [],
    cacheTime: fund.cacheTime
  }
  detailDialogVisible.value = true

  if (!fund.cacheTime) {
    await fetchValuation(fund.fundId)
  }
}

// 获取实时估值
const fetchValuation = async (fundId) => {
  valuationLoading.value = true
  try {
    const res = await getFundValuation(fundId)
    const valuation = res.data
    currentFund.value = {
      ...currentFund.value,
      ...valuation
    }
  } catch (error) {
    console.error('获取估值失败', error)
    ElMessage.error('获取估值失败')
  } finally {
    valuationLoading.value = false
  }
}

// 刷新估值
const handleRefreshValuation = async () => {
  if (currentFund.value.fundId) {
    await fetchValuation(currentFund.value.fundId)
    ElMessage.success('估值刷新成功')
  }
}

// 从详情弹窗进入编辑
const handleEditFromDetail = () => {
  detailDialogVisible.value = false
  handleEdit({ fundId: currentFund.value.fundId })
}

// 新建基金
const handleAdd = () => {
  isEdit.value = false
  formData.fundName = ''
  formData.fundCode = ''
  formData.description = ''
  formData.holdings = [{ stockCode: '', weight: null }]
  editDialogVisible.value = true
}

// 编辑基金
const handleEdit = async (fund) => {
  isEdit.value = true
  try {
    const res = await getFundById(fund.fundId)
    const fundDetail = res.data
    formData.id = fundDetail.id
    formData.fundName = fundDetail.fundName
    formData.fundCode = fundDetail.fundCode
    formData.description = fundDetail.description
    formData.holdings = (fundDetail.holdings || []).map(h => ({
      stockCode: h.stockCode,
      weight: h.weight
    }))
    if (formData.holdings.length === 0) {
      formData.holdings = [{ stockCode: '', weight: null }]
    }
    editDialogVisible.value = true
  } catch (error) {
    console.error('获取基金详情失败', error)
    ElMessage.error('获取基金详情失败')
  }
}

// 删除基金
const handleDelete = (fund) => {
  ElMessageBox.confirm(
    `确定要删除基金"${fund.fundName}"吗？`,
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      await deleteFund(fund.fundId)
      ElMessage.success('删除成功')
      fetchFundList()
    } catch (error) {
      console.error('删除失败', error)
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

// 搜索股票
const searchStock = async (query) => {
  if (!query || query.length < 1) {
    stockOptions.value = []
    return
  }
  stockSearchLoading.value = true
  try {
    const res = await listStocks({ keyword: query, page: 1, size: 20 })
    stockOptions.value = res.data?.records || []
  } catch (error) {
    console.error('搜索股票失败', error)
  } finally {
    stockSearchLoading.value = false
  }
}

// 添加持仓
const addHolding = () => {
  formData.holdings.push({ stockCode: '', weight: null })
}

// 删除持仓
const removeHolding = (index) => {
  if (formData.holdings.length > 1) {
    formData.holdings.splice(index, 1)
  } else {
    ElMessage.warning('至少保留一个持仓')
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
  } catch {
    return
  }

  submitting.value = true
  try {
    const data = {
      fundName: formData.fundName,
      fundCode: formData.fundCode,
      description: formData.description,
      holdings: formData.holdings.filter(h => h.stockCode && h.weight !== null)
    }

    if (isEdit.value) {
      await updateFund(formData.id, data)
      ElMessage.success('更新成功')
    } else {
      await createFund(data)
      ElMessage.success('创建成功')
    }

    editDialogVisible.value = false
    fetchFundList()
  } catch (error) {
    console.error('保存失败', error)
    ElMessage.error('保存失败')
  } finally {
    submitting.value = false
  }
}

// 格式化缓存时间
const formatCacheTime = (timestamp) => {
  if (!timestamp) return ''
  const now = Date.now()
  const diff = now - timestamp
  const minutes = Math.floor(diff / 60000)
  if (minutes < 1) return '刚刚更新'
  if (minutes < 60) return `${minutes}分钟前`
  const hours = Math.floor(minutes / 60)
  return `${hours}小时前`
}

// 格式化涨跌幅
const formatChangePercent = (value) => {
  if (value === null || value === undefined) return '-'
  const num = Number(value)
  if (num > 0) return '+' + num.toFixed(2) + '%'
  return num.toFixed(2) + '%'
}

// 获取涨跌幅样式类
const getChangeClass = (value) => {
  if (value === null || value === undefined) return ''
  const num = Number(value)
  if (num > 0) return 'rise'
  if (num < 0) return 'fall'
  return ''
}
</script>

<style scoped>
.fund-container {
  padding: 20px;
}

.action-card {
  margin-bottom: 20px;
}

.fund-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.3s;
}

.fund-card:hover {
  transform: translateY(-5px);
}

.fund-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.fund-name {
  font-weight: bold;
  font-size: 16px;
}

.fund-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

.more-icon {
  cursor: pointer;
  font-size: 18px;
}

.fund-content {
  padding: 10px 0;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.info-row .label {
  color: #909399;
  flex-shrink: 0;
}

.info-row .value {
  text-align: right;
}

.fund-footer {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
  text-align: center;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.cache-time {
  font-size: 12px;
  color: #909399;
}

.valuation-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.valuation-row .value {
  font-size: 24px;
  font-weight: bold;
}

.rise {
  color: #f56c6c;
}

.fall {
  color: #67c23a;
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 20px;
}

.detail-header .el-descriptions {
  flex: 1;
}

.detail-actions {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}

.holdings-container {
  width: 100%;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 15px;
}

.holdings-header {
  display: flex;
  gap: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  margin-bottom: 10px;
  font-weight: bold;
  color: #606266;
}

.holdings-header span:first-child {
  width: 300px;
}

.holdings-header span:nth-child(2) {
  width: 150px;
}

.holding-item {
  display: flex;
  gap: 20px;
  align-items: center;
  margin-bottom: 10px;
}

.holdings-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.total-weight {
  color: #606266;
}
</style>
