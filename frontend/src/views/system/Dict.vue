<template>
  <div class="dict-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="字典类型">
          <el-input v-model="queryForm.dictType" placeholder="请输入字典类型编码" clearable />
        </el-form-item>
        <el-form-item label="字典名称">
          <el-input v-model="queryForm.dictName" placeholder="请输入字典名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleQuery">查询</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card class="table-card">
      <div class="toolbar">
        <el-button v-permission="'system:dict:add'" type="primary" :icon="Plus" @click="handleAddType">新增字典类型</el-button>
        <el-button type="success" :icon="Refresh" @click="handleRefreshCache">刷新缓存</el-button>
      </div>

      <!-- 字典类型列表表格 -->
      <el-table
        :data="tableData"
        style="width: 100%; margin-top: 20px"
        v-loading="loading"
      >
        <el-table-column prop="dictType" label="字典类型" min-width="120" />
        <el-table-column prop="dictName" label="字典名称" min-width="120" />
        <el-table-column prop="status" label="状态" min-width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" />
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column label="操作" min-width="220" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'system:dict:list'" link type="primary" :icon="List" @click="handleViewData(row)">数据项</el-button>
            <el-button v-permission="'system:dict:edit'" link type="primary" :icon="Edit" @click="handleEditType(row)">编辑</el-button>
            <el-button v-permission="'system:dict:delete'" link type="danger" :icon="Delete" @click="handleDeleteType(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-if="total > 0"
        style="margin-top: 20px; justify-content: flex-end"
        :current-page="queryForm.pageNum"
        :page-size="queryForm.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增/编辑字典类型对话框 -->
    <el-dialog
      v-model="typeDialogVisible"
      :title="typeDialogTitle"
      width="500px"
      @close="handleTypeDialogClose"
    >
      <el-form
        ref="typeFormRef"
        :model="typeForm"
        :rules="typeFormRules"
        label-width="100px"
      >
        <el-form-item label="字典类型" prop="dictType">
          <el-input
            v-model="typeForm.dictType"
            placeholder="请输入字典类型编码"
            :disabled="isEditType"
          />
        </el-form-item>
        <el-form-item label="字典名称" prop="dictName">
          <el-input v-model="typeForm.dictName" placeholder="请输入字典名称" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="typeForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="typeForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="typeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitType" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 字典数据对话框 -->
    <el-dialog
      v-model="dataDialogVisible"
      :title="`字典数据 - ${currentDictType}`"
      width="800px"
      @close="handleDataDialogClose"
    >
      <div class="data-toolbar">
        <el-button type="primary" size="small" :icon="Plus" @click="handleAddData">新增数据项</el-button>
      </div>
      <el-table :data="dictDataList" border size="small" v-loading="dataLoading">
        <el-table-column prop="dictValue" label="键值" width="120" />
        <el-table-column prop="dictLabel" label="标签" min-width="150" />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" />
        <el-table-column label="操作" width="150" align="center">
          <template #default="{ row }">
            <el-button link type="primary" size="small" :icon="Edit" @click="handleEditData(row)">编辑</el-button>
            <el-button link type="danger" size="small" :icon="Delete" @click="handleDeleteData(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 新增/编辑字典数据对话框 -->
    <el-dialog
      v-model="dataFormDialogVisible"
      :title="dataFormTitle"
      width="500px"
      append-to-body
      @close="handleDataFormClose"
    >
      <el-form
        ref="dataFormRef"
        :model="dataForm"
        :rules="dataFormRules"
        label-width="100px"
      >
        <el-form-item label="字典键值" prop="dictValue">
          <el-input v-model="dataForm.dictValue" placeholder="请输入字典键值" />
        </el-form-item>
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input v-model="dataForm.dictLabel" placeholder="请输入字典标签" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="dataForm.sort" :min="0" :max="9999" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dataForm.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="dataForm.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dataFormDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitData" :loading="dataSubmitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, List } from '@element-plus/icons-vue'
import {
  pageDictTypes,
  addDictType,
  updateDictType,
  deleteDictType,
  refreshDictCache,
  listByDictType,
  addDictData,
  updateDictData,
  deleteDictData
} from '@/api/dict'

// ==================== 字典类型 ====================
const queryForm = reactive({
  dictType: '',
  dictName: '',
  pageNum: 1,
  pageSize: 10
})
const tableData = ref([])
const total = ref(0)
const loading = ref(false)
const submitLoading = ref(false)

// 字典类型对话框
const typeDialogVisible = ref(false)
const typeDialogTitle = ref('')
const isEditType = ref(false)
const typeFormRef = ref(null)
const typeForm = reactive({
  id: null,
  dictType: '',
  dictName: '',
  status: 1,
  remark: ''
})

const typeFormRules = {
  dictType: [
    { required: true, message: '请输入字典类型编码', trigger: 'blur' },
    { min: 2, max: 100, message: '长度在 2 到 100 个字符', trigger: 'blur' },
    { pattern: /^[a-zA-Z_][a-zA-Z0-9_]*$/, message: '只能包含字母、数字和下划线，且以字母或下划线开头', trigger: 'blur' }
  ],
  dictName: [
    { required: true, message: '请输入字典名称', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const handleQuery = async () => {
  loading.value = true
  try {
    const { data } = await pageDictTypes(queryForm)
    tableData.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

const handleReset = () => {
  queryForm.dictType = ''
  queryForm.dictName = ''
  queryForm.pageNum = 1
  handleQuery()
}

const handleSizeChange = (size) => {
  queryForm.pageSize = size
  queryForm.pageNum = 1
  handleQuery()
}

const handleCurrentChange = (page) => {
  queryForm.pageNum = page
  handleQuery()
}

const handleAddType = () => {
  isEditType.value = false
  typeDialogTitle.value = '新增字典类型'
  typeForm.id = null
  typeForm.dictType = ''
  typeForm.dictName = ''
  typeForm.status = 1
  typeForm.remark = ''
  typeDialogVisible.value = true
}

const handleEditType = (row) => {
  isEditType.value = true
  typeDialogTitle.value = '编辑字典类型'
  typeForm.id = row.id
  typeForm.dictType = row.dictType
  typeForm.dictName = row.dictName
  typeForm.status = row.status
  typeForm.remark = row.remark || ''
  typeDialogVisible.value = true
}

const handleDeleteType = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除字典类型 "${row.dictName}" 吗？`,
      '提示',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteDictType(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(error?.response?.data?.message || '删除失败')
    }
  }
}

const handleSubmitType = async () => {
  try {
    await typeFormRef.value.validate()
    submitLoading.value = true
    if (isEditType.value) {
      await updateDictType(typeForm)
    } else {
      await addDictType(typeForm)
    }
    ElMessage.success(isEditType.value ? '编辑成功' : '新增成功')
    typeDialogVisible.value = false
    handleQuery()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error?.response?.data?.message || '操作失败')
    }
  } finally {
    submitLoading.value = false
  }
}

const handleTypeDialogClose = () => {
  typeFormRef.value?.resetFields()
}

const handleRefreshCache = async () => {
  try {
    await refreshDictCache()
    ElMessage.success('刷新缓存成功')
  } catch (error) {
    ElMessage.error('刷新缓存失败')
  }
}

// ==================== 字典数据 ====================
const dataDialogVisible = ref(false)
const currentDictType = ref('')
const dictDataList = ref([])
const dataLoading = ref(false)

const dataFormDialogVisible = ref(false)
const dataFormTitle = ref('')
const isEditData = ref(false)
const dataFormRef = ref(null)
const dataSubmitLoading = ref(false)
const dataForm = reactive({
  id: null,
  dictType: '',
  dictValue: '',
  dictLabel: '',
  sort: 0,
  status: 1,
  remark: ''
})

const dataFormRules = {
  dictValue: [
    { required: true, message: '请输入字典键值', trigger: 'blur' }
  ],
  dictLabel: [
    { required: true, message: '请输入字典标签', trigger: 'blur' }
  ],
  sort: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
}

const handleViewData = async (row) => {
  currentDictType.value = row.dictType
  dataDialogVisible.value = true
  await loadDictData()
}

const loadDictData = async () => {
  dataLoading.value = true
  try {
    const { data } = await listByDictType(currentDictType.value)
    dictDataList.value = data
  } catch (error) {
    ElMessage.error('查询字典数据失败')
  } finally {
    dataLoading.value = false
  }
}

const handleAddData = () => {
  isEditData.value = false
  dataFormTitle.value = '新增数据项'
  dataForm.id = null
  dataForm.dictType = currentDictType.value
  dataForm.dictValue = ''
  dataForm.dictLabel = ''
  dataForm.sort = dictDataList.value.length
  dataForm.status = 1
  dataForm.remark = ''
  dataFormDialogVisible.value = true
}

const handleEditData = (row) => {
  isEditData.value = true
  dataFormTitle.value = '编辑数据项'
  dataForm.id = row.id
  dataForm.dictType = currentDictType.value
  dataForm.dictValue = row.dictValue
  dataForm.dictLabel = row.dictLabel
  dataForm.sort = row.sort
  dataForm.status = row.status
  dataForm.remark = row.remark || ''
  dataFormDialogVisible.value = true
}

const handleDeleteData = async (row) => {
  try {
    await ElMessageBox.confirm(
      `确定要删除数据项 "${row.dictLabel}" 吗？`,
      '提示',
      { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' }
    )
    await deleteDictData(row.id)
    ElMessage.success('删除成功')
    await loadDictData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSubmitData = async () => {
  try {
    await dataFormRef.value.validate()
    dataSubmitLoading.value = true
    if (isEditData.value) {
      await updateDictData(dataForm)
    } else {
      await addDictData(dataForm)
    }
    ElMessage.success(isEditData.value ? '编辑成功' : '新增成功')
    dataFormDialogVisible.value = false
    await loadDictData()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error?.response?.data?.message || '操作失败')
    }
  } finally {
    dataSubmitLoading.value = false
  }
}

const handleDataDialogClose = () => {
  dictDataList.value = []
}

const handleDataFormClose = () => {
  dataFormRef.value?.resetFields()
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.dict-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.toolbar {
  margin-bottom: 20px;
}

.data-toolbar {
  margin-bottom: 10px;
}
</style>
