<template>
  <div class="gaokao-page">
    <el-card class="search-card" shadow="never">
      <div class="page-head">
        <div>
          <h2>{{ pageConfig.title }}</h2>
          <span>{{ pageConfig.subtitle }}</span>
        </div>
        <el-tag type="info" effect="plain">{{ pageConfig.badge }}</el-tag>
      </div>

      <el-form :model="queryForm" label-width="88px" class="query-form">
        <el-row :gutter="12">
          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="年份">
              <el-input-number
                v-model="queryForm.admissionYear"
                :min="2000"
                :max="2100"
                controls-position="right"
                class="full-width"
              />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="考生省份">
              <el-select
                v-model="queryForm.candidateProvinceCode"
                clearable
                filterable
                class="full-width"
                placeholder="全部省份"
              >
                <el-option
                  v-for="item in provinceOptions"
                  :key="item.code"
                  :label="item.name"
                  :value="item.code"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="科类">
              <el-select v-model="queryForm.subjectCategory" clearable filterable class="full-width" placeholder="全部">
                <el-option v-for="item in subjectOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="批次">
              <el-select
                v-model="queryForm.batchName"
                clearable
                filterable
                allow-create
                default-first-option
                class="full-width"
                placeholder="全部"
              >
                <el-option v-for="item in batchOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="院校">
              <el-input v-model="queryForm.collegeName" clearable placeholder="院校名称" />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="院校代码">
              <el-input v-model="queryForm.collegeCode" clearable placeholder="如 1105" />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="专业组">
              <el-input v-model="queryForm.majorGroupCode" clearable placeholder="如 101" />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="选科">
              <el-input v-model="queryForm.selectionRequirement" clearable placeholder="选科要求" />
            </el-form-item>
          </el-col>

          <el-col v-if="showMajorFields" :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="专业">
              <el-input v-model="queryForm.majorName" clearable placeholder="专业名称" />
            </el-form-item>
          </el-col>

          <el-col v-if="showMajorFields" :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="专业代码">
              <el-input v-model="queryForm.majorCode" clearable placeholder="如 01" />
            </el-form-item>
          </el-col>

          <el-col v-if="showMajorFields" :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="专业备注">
              <el-input v-model="queryForm.majorRemark" clearable placeholder="培养方向/备注" />
            </el-form-item>
          </el-col>

          <el-col v-if="showScoreFields" :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="录取类型">
              <el-select
                v-model="queryForm.admissionType"
                clearable
                filterable
                allow-create
                default-first-option
                class="full-width"
                placeholder="全部"
              >
                <el-option v-for="item in admissionTypeOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col v-if="showScoreFields" :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="学校所在">
              <el-select
                v-model="queryForm.collegeProvinceName"
                clearable
                filterable
                class="full-width"
                placeholder="全部省份"
              >
                <el-option
                  v-for="item in provinceOptions"
                  :key="item.code"
                  :label="item.name"
                  :value="item.name"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col v-if="showScoreFields" :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="学校性质">
              <el-select
                v-model="queryForm.collegeNature"
                clearable
                filterable
                allow-create
                default-first-option
                class="full-width"
                placeholder="全部"
              >
                <el-option v-for="item in collegeNatureOptions" :key="item" :label="item" :value="item" />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col v-if="showScoreFields" :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="985">
              <el-segmented v-model="queryForm.is985" :options="flagOptions" class="full-width" />
            </el-form-item>
          </el-col>

          <el-col v-if="showScoreFields" :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="211">
              <el-segmented v-model="queryForm.is211" :options="flagOptions" class="full-width" />
            </el-form-item>
          </el-col>

          <el-col v-if="showScoreFields" :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="分数">
              <div class="range-input">
                <el-input-number v-model="queryForm.minScoreStart" :min="0" controls-position="right" />
                <span>至</span>
                <el-input-number v-model="queryForm.minScoreEnd" :min="0" controls-position="right" />
              </div>
            </el-form-item>
          </el-col>

          <el-col v-if="showScoreFields" :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="位次">
              <div class="range-input">
                <el-input-number v-model="queryForm.minRankStart" :min="0" controls-position="right" />
                <span>至</span>
                <el-input-number v-model="queryForm.minRankEnd" :min="0" controls-position="right" />
              </div>
            </el-form-item>
          </el-col>

          <el-col v-if="isPlan" :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="学制">
              <div class="range-input">
                <el-input-number v-model="queryForm.studyYearsStart" :min="0" controls-position="right" />
                <span>至</span>
                <el-input-number v-model="queryForm.studyYearsEnd" :min="0" controls-position="right" />
              </div>
            </el-form-item>
          </el-col>

          <el-col v-if="isPlan" :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="计划人数">
              <div class="range-input">
                <el-input-number v-model="queryForm.planCountStart" :min="0" controls-position="right" />
                <span>至</span>
                <el-input-number v-model="queryForm.planCountEnd" :min="0" controls-position="right" />
              </div>
            </el-form-item>
          </el-col>

          <el-col v-if="isPlan" :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="学费">
              <el-input v-model="queryForm.tuitionFee" clearable placeholder="如 5000 / 待定" />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="关键词">
              <el-input v-model="queryForm.keyword" clearable placeholder="院校/专业/代码" @keyup.enter="handleQuery" />
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="排序字段">
              <el-select v-model="queryForm.sortField" clearable filterable class="full-width" placeholder="默认排序">
                <el-option
                  v-for="item in sortOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :xs="24" :sm="12" :md="8" :lg="6">
            <el-form-item label="排序方向">
              <el-segmented v-model="queryForm.sortOrder" :options="sortOrderOptions" class="full-width" />
            </el-form-item>
          </el-col>
        </el-row>

        <div class="query-actions">
          <el-button type="primary" :icon="Search" :loading="loading" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </div>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        class="result-table"
        @sort-change="handleSortChange"
      >
        <el-table-column type="index" label="序号" width="72" align="center" fixed="left" :index="getTableIndex" />
        <el-table-column
          v-for="column in tableColumns"
          :key="column.prop"
          :prop="column.prop"
          :label="column.label"
          :width="column.width"
          :min-width="column.minWidth"
          :align="column.align || 'center'"
          :fixed="column.fixed"
          :sortable="column.sortable ? 'custom' : false"
          show-overflow-tooltip
        >
          <template #default="{ row }">
            <template v-if="column.type === 'flag'">
              <el-tag v-if="isFlagEnabled(row[column.prop])" type="success" size="small">是</el-tag>
              <el-tag v-else-if="isFlagDisabled(row[column.prop])" type="info" size="small">否</el-tag>
              <span v-else>-</span>
            </template>
            <el-tag v-else-if="column.type === 'subject'" :type="subjectTagType(row[column.prop])" size="small">
              {{ row[column.prop] || '-' }}
            </el-tag>
            <span v-else>{{ formatCell(row[column.prop], column) }}</span>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100, 200]"
        layout="total, sizes, prev, pager, next, jumper"
        class="pager"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Refresh, Search } from '@element-plus/icons-vue'
import { pageAdmissionPlans, pageCollegeScores, pageMajorScores } from '@/api/gaokao'

const props = defineProps({
  dataType: {
    type: String,
    required: true
  }
})

const DEFAULT_YEAR = 2025
const DEFAULT_PROVINCE_CODE = '410000'

const sortOrderOptions = [
  { label: '降序', value: 'desc' },
  { label: '升序', value: 'asc' }
]

const flagOptions = [
  { label: '全部', value: '' },
  { label: '是', value: 1 },
  { label: '否', value: 0 }
]

const provinceOptions = [
  { code: '110000', name: '北京' },
  { code: '120000', name: '天津' },
  { code: '130000', name: '河北' },
  { code: '140000', name: '山西' },
  { code: '150000', name: '内蒙古' },
  { code: '210000', name: '辽宁' },
  { code: '220000', name: '吉林' },
  { code: '230000', name: '黑龙江' },
  { code: '310000', name: '上海' },
  { code: '320000', name: '江苏' },
  { code: '330000', name: '浙江' },
  { code: '340000', name: '安徽' },
  { code: '350000', name: '福建' },
  { code: '360000', name: '江西' },
  { code: '370000', name: '山东' },
  { code: '410000', name: '河南' },
  { code: '420000', name: '湖北' },
  { code: '430000', name: '湖南' },
  { code: '440000', name: '广东' },
  { code: '450000', name: '广西' },
  { code: '460000', name: '海南' },
  { code: '500000', name: '重庆' },
  { code: '510000', name: '四川' },
  { code: '520000', name: '贵州' },
  { code: '530000', name: '云南' },
  { code: '540000', name: '西藏' },
  { code: '610000', name: '陕西' },
  { code: '620000', name: '甘肃' },
  { code: '630000', name: '青海' },
  { code: '640000', name: '宁夏' },
  { code: '650000', name: '新疆' }
]

const subjectOptions = [
  '物理类',
  '历史类',
  '理科',
  '文科',
  '综合',
  '艺术类（物理）',
  '艺术类（历史）',
  '体育类（物理）',
  '体育类（历史）'
]

const batchOptions = [
  '本科批',
  '专科批',
  '本科提前批',
  '专科提前批',
  '高校专项计划批',
  '国家专项计划批',
  '地方专项计划批',
  '艺术类本科批',
  '体育类本科批'
]

const admissionTypeOptions = [
  '普通类',
  '中外合作办学',
  '较高收费',
  '医护类',
  '护理类',
  '软件类',
  '地方专项计划',
  '国家专项计划'
]

const collegeNatureOptions = [
  '公办',
  '民办',
  '中外合作办学',
  '内地与港澳台地区合作办学'
]

const baseColumns = [
  { prop: 'admissionYear', label: '年份', width: 82, sortable: true },
  { prop: 'candidateProvinceName', label: '省份', width: 82, sortable: true },
  { prop: 'collegeName', label: '院校名称', minWidth: 170, sortable: true, fixed: 'left' },
  { prop: 'collegeCode', label: '院校代码', width: 96, sortable: true },
  { prop: 'subjectCategory', label: '科类', width: 116, sortable: true, type: 'subject' },
  { prop: 'batchName', label: '批次', width: 128, sortable: true },
  { prop: 'majorGroupCode', label: '专业组', width: 96, sortable: true },
  { prop: 'selectionRequirement', label: '选科要求', minWidth: 180, sortable: true }
]

const scoreExtraColumns = [
  { prop: 'admissionType', label: '录取类型', width: 124, sortable: true },
  { prop: 'admissionCount', label: '录取人数', width: 100, sortable: true, format: 'integer' },
  { prop: 'minScore', label: '最低分', width: 92, sortable: true },
  { prop: 'minRank', label: '最低位次', width: 116, sortable: true, format: 'integer' },
  { prop: 'collegeProvinceName', label: '学校所在', width: 100, sortable: true },
  { prop: 'collegeNature', label: '学校性质', width: 124, sortable: true },
  { prop: 'is985', label: '985', width: 72, sortable: true, type: 'flag' },
  { prop: 'is211', label: '211', width: 72, sortable: true, type: 'flag' }
]

const collegeColumns = [
  ...baseColumns,
  ...scoreExtraColumns.slice(0, 4),
  { prop: 'batchLineDiff', label: '线差', width: 88, sortable: true },
  ...scoreExtraColumns.slice(4),
  { prop: 'remark', label: '备注', minWidth: 180 }
]

const majorScoreColumns = [
  ...baseColumns,
  { prop: 'majorName', label: '专业名称', minWidth: 170, sortable: true },
  { prop: 'majorCode', label: '专业代码', width: 96, sortable: true },
  { prop: 'majorRemark', label: '专业备注', minWidth: 220, sortable: true },
  ...scoreExtraColumns,
  { prop: 'remark', label: '备注', minWidth: 180 }
]

const planColumns = [
  ...baseColumns,
  { prop: 'majorName', label: '专业名称', minWidth: 170, sortable: true },
  { prop: 'majorCode', label: '专业代码', width: 96, sortable: true },
  { prop: 'majorRemark', label: '专业备注', minWidth: 220, sortable: true },
  { prop: 'studyYears', label: '学制', width: 82, sortable: true },
  { prop: 'tuitionFee', label: '学费', width: 116, sortable: true },
  { prop: 'planCount', label: '计划人数', width: 100, sortable: true, format: 'integer' },
  { prop: 'remark', label: '备注', minWidth: 180 }
]

const pageConfigs = {
  collegeScore: {
    title: '院校录取分数',
    subtitle: '院校专业组录取结果',
    badge: '录取线',
    fetch: pageCollegeScores,
    columns: collegeColumns
  },
  majorScore: {
    title: '专业录取分数',
    subtitle: '专业维度录取分数、位次与备注',
    badge: '专业线',
    fetch: pageMajorScores,
    columns: majorScoreColumns
  },
  admissionPlan: {
    title: '招生计划',
    subtitle: '专业招生计划、学制、学费与计划人数',
    badge: '计划数',
    fetch: pageAdmissionPlans,
    columns: planColumns
  }
}

const defaultQuery = () => ({
  current: 1,
  size: 20,
  admissionYear: DEFAULT_YEAR,
  candidateProvinceCode: DEFAULT_PROVINCE_CODE,
  candidateProvinceName: '',
  subjectCategory: '',
  batchName: '',
  collegeName: '',
  collegeCode: '',
  majorGroupCode: '',
  selectionRequirement: '',
  majorName: '',
  majorCode: '',
  majorRemark: '',
  admissionType: '',
  collegeProvinceName: '',
  collegeNature: '',
  is985: '',
  is211: '',
  keyword: '',
  minScoreStart: undefined,
  minScoreEnd: undefined,
  minRankStart: undefined,
  minRankEnd: undefined,
  studyYearsStart: undefined,
  studyYearsEnd: undefined,
  planCountStart: undefined,
  planCountEnd: undefined,
  tuitionFee: '',
  sortField: '',
  sortOrder: 'desc'
})

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryForm = reactive(defaultQuery())

const pageConfig = computed(() => pageConfigs[props.dataType] || pageConfigs.collegeScore)
const tableColumns = computed(() => pageConfig.value.columns)
const isPlan = computed(() => props.dataType === 'admissionPlan')
const showScoreFields = computed(() => props.dataType !== 'admissionPlan')
const showMajorFields = computed(() => props.dataType !== 'collegeScore')
const sortOptions = computed(() => tableColumns.value
  .filter(column => column.sortable)
  .map(column => ({ label: column.label, value: column.prop })))

function validateRanges() {
  const rangeRules = [
    ['minScoreStart', 'minScoreEnd', '分数'],
    ['minRankStart', 'minRankEnd', '位次'],
    ['studyYearsStart', 'studyYearsEnd', '学制'],
    ['planCountStart', 'planCountEnd', '计划人数']
  ]

  for (const [startKey, endKey, label] of rangeRules) {
    const startValue = queryForm[startKey]
    const endValue = queryForm[endKey]
    if (startValue !== undefined && endValue !== undefined && startValue > endValue) {
      ElMessage.warning(`${label}起始值不能大于结束值`)
      return false
    }
  }
  return true
}

function cleanPayload() {
  const payload = {}
  Object.keys(queryForm).forEach(key => {
    const value = queryForm[key]
    if (value !== '' && value !== null && value !== undefined) {
      payload[key] = value
    }
  })
  return payload
}

async function loadData() {
  if (!validateRanges()) {
    return
  }

  loading.value = true
  try {
    const res = await pageConfig.value.fetch(cleanPayload())
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error('查询高考数据失败', error)
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  queryForm.current = 1
  loadData()
}

function handleReset() {
  Object.assign(queryForm, defaultQuery())
  loadData()
}

function handleSizeChange() {
  queryForm.current = 1
  loadData()
}

function handleCurrentChange() {
  loadData()
}

function handleSortChange({ prop, order }) {
  if (!order) {
    queryForm.sortField = ''
    queryForm.sortOrder = 'desc'
  } else {
    queryForm.sortField = prop || ''
    queryForm.sortOrder = order === 'ascending' ? 'asc' : 'desc'
  }
  queryForm.current = 1
  loadData()
}

function getTableIndex(index) {
  return (queryForm.current - 1) * queryForm.size + index + 1
}

function formatCell(value, column) {
  if (value === null || value === undefined || value === '') {
    return '-'
  }
  if (column.format === 'integer' && Number.isFinite(Number(value))) {
    return Number(value).toLocaleString()
  }
  return value
}

function isFlagEnabled(value) {
  return value === 1 || value === true || value === '1'
}

function isFlagDisabled(value) {
  return value === 0 || value === false || value === '0'
}

function subjectTagType(value) {
  if (!value) {
    return 'info'
  }
  if (String(value).includes('物理') || value === '理科') {
    return 'primary'
  }
  if (String(value).includes('历史') || value === '文科') {
    return 'success'
  }
  return 'warning'
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.gaokao-page {
  padding: 20px;
}

.search-card,
.table-card {
  border-radius: 8px;
}

.table-card {
  margin-top: 14px;
}

.page-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}

.page-head h2 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
}

.page-head span {
  display: block;
  margin-top: 4px;
  color: #64748b;
  font-size: 13px;
}

.query-form :deep(.el-form-item) {
  margin-bottom: 14px;
}

.full-width {
  width: 100%;
}

.range-input {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 24px minmax(0, 1fr);
  align-items: center;
  gap: 6px;
  width: 100%;
}

.range-input span {
  text-align: center;
  color: #64748b;
}

.range-input :deep(.el-input-number) {
  width: 100%;
}

.query-actions {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.result-table {
  width: 100%;
}

.pager {
  margin-top: 16px;
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .gaokao-page {
    padding: 12px;
  }

  .page-head {
    align-items: flex-start;
    flex-direction: column;
  }

  .query-actions {
    justify-content: stretch;
  }

  .query-actions .el-button {
    flex: 1;
  }
}
</style>
