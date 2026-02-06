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
          <el-button type="success" :icon="Refresh" @click="handleExecuteScore" :loading="executing">
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
        <el-table-column prop="rank" label="排名" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.rank <= 3" type="danger" effect="dark">{{ row.rank }}</el-tag>
            <el-tag v-else-if="row.rank <= 10" type="warning">{{ row.rank }}</el-tag>
            <span v-else>{{ row.rank }}</span>
          </template>
        </el-table-column>
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
            {{ row.hitRuleCount }} / {{ row.totalRuleCount }}
          </template>
        </el-table-column>
        <el-table-column prop="hitRate" label="命中率" width="100" align="center" sortable>
          <template #default="{ row }">
            {{ row.hitRate }}%
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleViewDetail(row)">
              打分明细
            </el-button>
            <el-button type="success" link :icon="TrendCharts" @click="handleViewKline(row)">
              K线图
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryParams.page"
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View, TrendCharts } from '@element-plus/icons-vue'
import { listRecommend, getScoreDetail, executeScore, getLatestRecommendDate, listRules } from '@/api/recommend'
import { useRouter } from 'vue-router'

const router = useRouter()

// 查询参数
const queryParams = reactive({
  recommendDate: '',
  page: 1,
  size: 20
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
 * 获取最新推荐日期
 */
const fetchLatestDate = async () => {
  try {
    const res = await getLatestRecommendDate()
    if (res.code === 200 && res.data) {
      queryParams.recommendDate = res.data
    } else {
      // 如果没有推荐数据，使用今天
      queryParams.recommendDate = new Date().toISOString().split('T')[0]
    }
  } catch (error) {
    console.error('获取最新推荐日期失败', error)
    queryParams.recommendDate = new Date().toISOString().split('T')[0]
  }
}

/**
 * 获取推荐列表
 */
const fetchRecommendList = async () => {
  loading.value = true
  try {
    const res = await listRecommend(queryParams.recommendDate, queryParams.page, queryParams.size)
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
  queryParams.page = 1
  fetchRecommendList()
}

/**
 * 刷新
 */
const handleRefresh = () => {
  fetchRecommendList()
}

/**
 * 手动触发打分
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
    const res = await executeScore(null, queryParams.recommendDate)
    if (res.code === 200) {
      ElMessage.success('打分任务已提交，请稍后刷新查看结果')
      // 延迟刷新列表
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
  router.push({
    path: '/stock/detail',
    query: { code: row.stockCode }
  })
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

onMounted(async () => {
  await fetchRules()
  await fetchLatestDate()
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
