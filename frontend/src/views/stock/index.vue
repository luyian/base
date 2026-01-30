<template>
  <div class="stock-container">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="市场">
          <el-select v-model="queryForm.market" placeholder="请选择市场" clearable style="width: 120px">
            <el-option label="港股" value="HK" />
            <el-option label="沪市" value="SH" />
            <el-option label="深市" value="SZ" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="queryForm.keyword" placeholder="股票代码/名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <el-table v-loading="loading" :data="tableData" border stripe>
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
        <el-table-column prop="exchange" label="交易所" width="120" align="center" />
        <el-table-column prop="currency" label="货币" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success" size="small">正常</el-tag>
            <el-tag v-else type="danger" size="small">退市</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="View" @click="handleView(row)">查看K线</el-button>
            <el-button type="success" link :icon="Star" @click="handleAddWatchlist(row)">加自选</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.size"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { Search, Refresh, View, Star } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { listStocks, addWatchlist } from '@/api/stock'
import { useRouter } from 'vue-router'

const router = useRouter()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryForm = ref({
  page: 1,
  size: 20,
  market: '',
  keyword: ''
})

const handleQuery = async () => {
  loading.value = true
  try {
    const res = await listStocks(queryForm.value)
    tableData.value = res.data.records || []
    total.value = res.data.total || 0
  } catch (error) {
    console.error('查询失败', error)
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.value = {
    page: 1,
    size: 20,
    market: '',
    keyword: ''
  }
  handleQuery()
}

const handleView = (row) => {
  router.push(`/stock/detail/${row.stockCode}`)
}

const handleAddWatchlist = async (row) => {
  try {
    await addWatchlist(row.stockCode, '')
    ElMessage.success('添加自选成功')
  } catch (error) {
    ElMessage.error(error.message || '添加自选失败')
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.stock-container {
  padding: 20px;
}
.search-card {
  margin-bottom: 15px;
}
.table-card {
  margin-bottom: 15px;
}
</style>
