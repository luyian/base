<template>
  <div class="recommend-container">
    <!-- 操作栏 -->
    <el-card class="action-card" shadow="never">
      <el-form :inline="true">
        <el-form-item label="推荐日期">
          <el-date-picker
            v-model="queryParams.recommendDate"
            type="date"
            placeholder="选择日期"
            value-format="YYYY-MM-DD"
            style="width: 180px"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="fetchRecommendList">查询</el-button>
          <el-button v-permission="'stock:recommend:execute'" type="success" :icon="Refresh" @click="handleExecuteScore" :loading="executing">
            手动打分
          </el-button>
          <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 推荐列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="80" align="center" :index="getTableIndex" />
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
        <el-table-column prop="totalScore" label="总分" width="100" align="center" sortable>
          <template #default="{ row }">
            <el-tag type="success" effect="dark">{{ row.totalScore }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="hitRuleCount" label="命中规则" width="100" align="center">
          <template #default="{ row }">
            <span>{{ row.hitRuleCount }} / {{ row.totalRuleCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="hitRate" label="命中率" width="100" align="center" sortable>
          <template #default="{ row }">
            <span>{{ row.hitRate }}%</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-permission="'stock:recommend:score'"
              type="warning"
              link
              :icon="Refresh"
              @click="handleSingleScore(row)"
              :loading="row.scoring"
            >
              重新打分
            </el-button>
            <el-button type="primary" link :icon="View" @click="handleViewDetail(row)">
              明细
            </el-button>
            <el-button type="success" link :icon="TrendCharts" @click="handleViewKline(row)">
              K线
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.current"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="fetchRecommendList"
        @current-change="fetchRecommendList"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 打分明细弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="`${currentStock.stockName || currentStock.stockCode} - 打分明细`"
      width="80%"
      destroy-on-close
    >
      <el-table :data="scoreDetails" border stripe>
        <el-table-column prop="ruleCode" label="规则编码" width="180" align="center" />
        <el-table-column label="规则名称" width="150" align="center">
          <template #default="{ row }">
            {{ getRuleName(row.ruleCode) }}
          </template>
        </el-table-column>
        <el-table-column prop="hitFlag" label="命中状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.hitFlag === 1" type="success">命中</el-tag>
            <el-tag v-else type="info">未命中</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="score" label="得分" width="100" align="center" />
        <el-table-column prop="weightedScore" label="加权得分" width="120" align="center">
          <template #default="{ row }">
            <el-tag type="success">{{ row.weightedScore }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="detailJson" label="计算详情" min-width="300">
          <template #default="{ row }">
            <pre style="margin: 0; font-size: 12px; max-height: 200px; overflow-y: auto;">{{ formatDetail(row.detailJson) }}</pre>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 趋势弹窗（复用公共组件） -->
    <TrendDialog
      v-model="trendDialogVisible"
      :stock-code="currentStock.stockCode"
      :stock-name="currentStock.stockName"
    />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, TrendCharts } from '@element-plus/icons-vue'
import { listRecommend, getScoreDetail, executeScore, executeSingleScore, listRules } from '@/api/recommend'
import TrendDialog from '../components/TrendDialog.vue'

// 查询参数
const queryParams = reactive({
  recommendDate: '',
  current: 1,
  size: 10
})

// 表格数据
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const executing = ref(false)

// 打分明细
const detailDialogVisible = ref(false)
const scoreDetails = ref([])
const currentStock = ref({})

// 趋势弹窗
const trendDialogVisible = ref(false)

// 规则列表（用于显示规则名称）
const rules = ref([])

/**
 * 获取规则列表
 */
const fetchRules = async () => {
  try {
    const res = await listRules()
    if (res.code === 200) {
      rules.value = res.data || []
    }
  } catch (error) {
    console.error('获取规则列表失败', error)
  }
}

/**
 * 获取规则名称
 */
const getRuleName = (ruleCode) => {
  const rule = rules.value.find(r => r.ruleCode === ruleCode)
  return rule ? rule.ruleName : ruleCode
}

/**
 * 初始化日期为当天
 */
const initDate = () => {
  queryParams.recommendDate = new Date().toISOString().split('T')[0]
}

/**
 * 获取推荐列表
 */
const fetchRecommendList = async () => {
  loading.value = true
  try {
    const res = await listRecommend(queryParams)
    if (res.code === 200) {
      tableData.value = res.data.records || []
      total.value = res.data.total || 0
    } else {
      ElMessage.error(res.message || '查询失败')
    }
  } catch (error) {
    console.error('查询推荐列表失败', error)
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

/**
 * 日期变更
 */
const handleDateChange = () => {
  queryParams.current = 1
  fetchRecommendList()
}

/**
 * 刷新
 */
const handleRefresh = () => {
  fetchRecommendList()
}

/**
 * 手动触发打分（全部）
 */
const handleExecuteScore = async () => {
  try {
    await ElMessageBox.confirm(
      `确定要对 ${queryParams.recommendDate} 的所有股票执行打分吗？此操作可能需要较长时间。`,
      '确认打分',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    executing.value = true
    const res = await executeScore(queryParams.recommendDate)
    if (res.code === 200) {
      ElMessage.success('打分任务已提交，请稍后刷新查看结果')
      setTimeout(() => {
        fetchRecommendList()
      }, 3000)
    } else {
      ElMessage.error(res.message || '打分失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('执行打分失败', error)
      ElMessage.error('打分失败')
    }
  } finally {
    executing.value = false
  }
}

/**
 * 单条重新打分
 */
const handleSingleScore = async (row) => {
  try {
    row.scoring = true
    const res = await executeSingleScore(row.stockCode, queryParams.recommendDate)
    if (res.code === 200) {
      ElMessage.success(`${row.stockName || row.stockCode} 打分成功`)
      fetchRecommendList()
    } else {
      ElMessage.error(res.message || '打分失败')
    }
  } catch (error) {
    console.error('单条打分失败', error)
    ElMessage.error('打分失败')
  } finally {
    row.scoring = false
  }
}

/**
 * 查看打分明细
 */
const handleViewDetail = async (row) => {
  currentStock.value = row
  try {
    const res = await getScoreDetail(row.stockCode, queryParams.recommendDate)
    if (res.code === 200) {
      scoreDetails.value = res.data || []
      detailDialogVisible.value = true
    } else {
      ElMessage.error(res.message || '查询失败')
    }
  } catch (error) {
    console.error('查询打分明细失败', error)
    ElMessage.error('查询失败')
  }
}

/**
 * 查看K线图
 */
const handleViewKline = (row) => {
  currentStock.value = row
  trendDialogVisible.value = true
}

/**
 * 格式化详情JSON
 */
const formatDetail = (detailJson) => {
  if (!detailJson) return ''
  try {
    const obj = typeof detailJson === 'string' ? JSON.parse(detailJson) : detailJson
    return JSON.stringify(obj, null, 2)
  } catch (error) {
    return detailJson
  }
}

/**
 * 计算表格序号（考虑分页）
 */
const getTableIndex = (index) => {
  return (queryParams.current - 1) * queryParams.size + index + 1
}

onMounted(async () => {
  await fetchRules()
  initDate()
  await fetchRecommendList()
})
</script>

<style scoped>
.recommend-container {
  padding: 20px;
}

.action-card {
  margin-bottom: 20px;
}

.table-card {
  min-height: 600px;
}
</style>
