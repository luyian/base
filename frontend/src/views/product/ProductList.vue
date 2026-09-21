<template>
  <div class="product-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :model="queryForm" inline>
        <el-form-item label="商品名称">
          <el-input v-model="queryForm.name" placeholder="请输入商品名称" clearable style="width: 200px" @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card">
      <div class="toolbar">
        <el-button type="primary" @click="handleAdd">新增商品</el-button>
      </div>

      <el-table
        :data="tableData"
        border
        stripe
        v-loading="loading"
        style="margin-top: 20px"
      >
        <el-table-column type="index" label="序号" width="55" />
        <el-table-column prop="code" label="商品编码" min-width="160" show-overflow-tooltip />
        <el-table-column prop="name" label="商品名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" min-width="90" show-overflow-tooltip />
        <el-table-column prop="spec" label="规格" min-width="90" show-overflow-tooltip />
        <el-table-column prop="unit" label="单位" width="60" />
        <el-table-column prop="salePrice" label="售价" width="90" align="right" />
        <el-table-column prop="costPrice" label="成本价" width="90" align="right" />
        <el-table-column prop="stock" label="库存" width="70" align="right" />
        <el-table-column prop="supplier" label="供应商" min-width="100" show-overflow-tooltip />
        <el-table-column prop="productionDate" label="生产日期" width="110" />
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleCodes(row)">条码</el-button>
            <el-button type="danger" link @click="handleDelete(row.id, row.name)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.page"
        v-model:page-size="queryForm.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 16px; justify-content: flex-end"
      />
    </el-card>

    <!-- 商品编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑商品' : '新增商品'" width="640px" destroy-on-close>
      <ProductEdit
        :product-id="currentId"
        :is-edit="isEdit"
        @saved="handleSaved"
        @cancel="dialogVisible = false"
      />
    </el-dialog>

    <!-- 条码列表弹窗 -->
    <el-dialog v-model="codesVisible" title="商品条码" width="640px">
      <ProductCodes :product-id="currentId" @refresh="handleQuery" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listProducts, deleteProduct } from '@/api/product'
import ProductEdit from './ProductEdit.vue'
import ProductCodes from './ProductCodes.vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryForm = reactive({ name: '', page: 1, size: 10 })

const dialogVisible = ref(false)
const isEdit = ref(false)
const currentId = ref(null)

const codesVisible = ref(false)

async function loadList() {
  loading.value = true
  try {
    const res = await listProducts({ name: queryForm.name || undefined, page: queryForm.page, size: queryForm.size })
    tableData.value = res.records || res.data?.records || []
    total.value = res.total || res.data?.total || 0
    // 兼容 IPage 结构
    if (res.data && res.data.records) {
      tableData.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  queryForm.page = 1
  loadList()
}

function handleReset() {
  queryForm.name = ''
  handleQuery()
}

function handleAdd() {
  isEdit.value = false
  currentId.value = null
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  currentId.value = row.id
  dialogVisible.value = true
}

function handleCodes(row) {
  currentId.value = row.id
  codesVisible.value = true
}

async function handleDelete(id, name) {
  await ElMessageBox.confirm(`确定删除商品「${name}」吗？删除后不可恢复。`, '提示', {
    type: 'warning'
  })
  await deleteProduct(id)
  ElMessage.success('删除成功')
  handleQuery()
}

function handleSaved() {
  dialogVisible.value = false
  handleQuery()
}

onMounted(loadList)
</script>

<style scoped>
.product-container {
  padding: 16px;
}
.search-card {
  margin-bottom: 16px;
}
.toolbar {
  display: flex;
  justify-content: flex-end;
}
</style>