<template>
  <div class="export-config-container">
    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryForm" inline>
        <el-form-item label="配置编码">
          <el-input v-model="queryForm.configCode" placeholder="请输入配置编码" clearable />
        </el-form-item>
        <el-form-item label="配置名称">
          <el-input v-model="queryForm.configName" placeholder="请输入配置名称" clearable />
        </el-form-item>
        <el-form-item label="数据源类型">
          <el-select v-model="queryForm.dataSourceType" placeholder="请选择" clearable>
            <el-option label="服务方法" value="SERVICE" />
            <el-option label="自定义SQL" value="SQL" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择" clearable>
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作按钮 -->
    <el-card class="table-card" shadow="never">
      <template #header>
        <el-button type="primary" @click="handleAdd">新增配置</el-button>
      </template>

      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="configCode" label="配置编码" min-width="120" />
        <el-table-column prop="configName" label="配置名称" min-width="150" />
        <el-table-column prop="dataSourceTypeDesc" label="数据源类型" width="120" />
        <el-table-column prop="dataSourceBean" label="数据源Bean" min-width="150" />
        <el-table-column prop="dataSourceMethod" label="数据源方法" min-width="150" />
        <el-table-column prop="batchSize" label="批次大小" width="100" />
        <el-table-column prop="maxExportCount" label="最大导出数" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="170" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleFields(row)">字段配置</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.pageNum"
        v-model:page-size="queryForm.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 16px; justify-content: flex-end;"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      destroy-on-close
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="120px"
      >
        <el-form-item label="配置编码" prop="configCode">
          <el-input v-model="form.configCode" placeholder="请输入配置编码" :disabled="!!form.id" />
        </el-form-item>
        <el-form-item label="配置名称" prop="configName">
          <el-input v-model="form.configName" placeholder="请输入配置名称" />
        </el-form-item>
        <el-form-item label="配置描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入配置描述" />
        </el-form-item>
        <el-form-item label="数据源类型" prop="dataSourceType">
          <el-select v-model="form.dataSourceType" placeholder="请选择数据源类型" style="width: 100%;">
            <el-option label="服务方法" value="SERVICE" />
            <el-option label="自定义SQL" value="SQL" />
          </el-select>
        </el-form-item>
        <template v-if="form.dataSourceType === 'SERVICE'">
          <el-form-item label="数据源Bean" prop="dataSourceBean">
            <el-input v-model="form.dataSourceBean" placeholder="请输入Spring Bean名称" />
          </el-form-item>
          <el-form-item label="数据源方法" prop="dataSourceMethod">
            <el-input v-model="form.dataSourceMethod" placeholder="请输入方法名" />
          </el-form-item>
          <el-form-item label="查询参数类" prop="queryParamClass">
            <el-input v-model="form.queryParamClass" placeholder="请输入查询参数类全路径" />
          </el-form-item>
        </template>
        <template v-if="form.dataSourceType === 'SQL'">
          <el-form-item label="自定义SQL" prop="dataSourceSql">
            <el-input v-model="form.dataSourceSql" type="textarea" :rows="4" placeholder="请输入SQL语句" />
          </el-form-item>
        </template>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="批次大小" prop="batchSize">
              <el-input-number v-model="form.batchSize" :min="100" :max="10000" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最大导出数" prop="maxExportCount">
              <el-input-number v-model="form.maxExportCount" :min="1000" :max="10000000" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="文件名模式" prop="fileNamePattern">
              <el-input v-model="form.fileNamePattern" placeholder="导出文件名前缀" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="权限编码" prop="permissionCode">
              <el-input v-model="form.permissionCode" placeholder="权限编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="多Sheet" prop="enableMultiSheet">
              <el-switch v-model="form.enableMultiSheet" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Sheet最大行" prop="sheetMaxRows" v-if="form.enableMultiSheet">
              <el-input-number v-model="form.sheetMaxRows" :min="10000" :max="1000000" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="form.sort" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 字段配置弹窗 -->
    <el-dialog
      v-model="fieldDialogVisible"
      title="字段配置"
      width="1000px"
      destroy-on-close
    >
      <div class="field-toolbar">
        <el-button type="primary" size="small" @click="handleAddField">添加字段</el-button>
      </div>
      <el-table :data="fieldList" border stripe max-height="400">
        <el-table-column prop="sort" label="排序" width="60">
          <template #default="{ $index }">
            {{ $index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="fieldName" label="字段名" min-width="120">
          <template #default="{ row }">
            <el-input v-model="row.fieldName" size="small" placeholder="字段名" />
          </template>
        </el-table-column>
        <el-table-column prop="fieldLabel" label="列标题" min-width="120">
          <template #default="{ row }">
            <el-input v-model="row.fieldLabel" size="small" placeholder="列标题" />
          </template>
        </el-table-column>
        <el-table-column prop="fieldType" label="类型" width="100">
          <template #default="{ row }">
            <el-select v-model="row.fieldType" size="small">
              <el-option label="字符串" value="STRING" />
              <el-option label="数字" value="NUMBER" />
              <el-option label="日期" value="DATE" />
              <el-option label="日期时间" value="DATETIME" />
              <el-option label="布尔" value="BOOLEAN" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="fieldWidth" label="宽度" width="80">
          <template #default="{ row }">
            <el-input-number v-model="row.fieldWidth" size="small" :min="5" :max="100" controls-position="right" />
          </template>
        </el-table-column>
        <el-table-column prop="dictType" label="字典类型" width="120">
          <template #default="{ row }">
            <el-input v-model="row.dictType" size="small" placeholder="字典类型" />
          </template>
        </el-table-column>
        <el-table-column prop="maskType" label="脱敏类型" width="110">
          <template #default="{ row }">
            <el-select v-model="row.maskType" size="small" clearable placeholder="无">
              <el-option label="手机号" value="PHONE" />
              <el-option label="身份证" value="ID_CARD" />
              <el-option label="邮箱" value="EMAIL" />
              <el-option label="银行卡" value="BANK_CARD" />
              <el-option label="姓名" value="NAME" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="70">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ $index }">
            <el-button type="danger" link size="small" @click="handleRemoveField($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <template #footer>
        <el-button @click="fieldDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSaveFields" :loading="fieldSaveLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  pageExportConfig,
  getExportConfig,
  addExportConfig,
  updateExportConfig,
  deleteExportConfig,
  getExportFields,
  saveExportFields
} from '@/api/exportConfig'

// 查询表单
const queryForm = reactive({
  configCode: '',
  configName: '',
  dataSourceType: '',
  status: null,
  pageNum: 1,
  pageSize: 10
})

// 表格数据
const tableData = ref([])
const total = ref(0)
const loading = ref(false)

// 弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const submitLoading = ref(false)

// 表单数据
const form = reactive({
  id: null,
  configCode: '',
  configName: '',
  description: '',
  dataSourceType: 'SERVICE',
  dataSourceBean: '',
  dataSourceMethod: '',
  dataSourceSql: '',
  queryParamClass: '',
  batchSize: 5000,
  maxExportCount: 1000000,
  fileNamePattern: '',
  enableMultiSheet: false,
  sheetMaxRows: 100000,
  permissionCode: '',
  status: 1,
  sort: 0,
  remark: ''
})

// 表单校验规则
const rules = {
  configCode: [{ required: true, message: '请输入配置编码', trigger: 'blur' }],
  configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  dataSourceType: [{ required: true, message: '请选择数据源类型', trigger: 'change' }],
  dataSourceBean: [{ required: true, message: '请输入数据源Bean', trigger: 'blur' }],
  dataSourceMethod: [{ required: true, message: '请输入数据源方法', trigger: 'blur' }]
}

// 字段配置弹窗
const fieldDialogVisible = ref(false)
const fieldList = ref([])
const currentConfigId = ref(null)
const fieldSaveLoading = ref(false)

// 查询列表
const handleQuery = async () => {
  loading.value = true
  try {
    const res = await pageExportConfig(queryForm)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

// 重置查询
const handleReset = () => {
  queryForm.configCode = ''
  queryForm.configName = ''
  queryForm.dataSourceType = ''
  queryForm.status = null
  queryForm.pageNum = 1
  handleQuery()
}

// 新增
const handleAdd = () => {
  dialogTitle.value = '新增配置'
  Object.assign(form, {
    id: null,
    configCode: '',
    configName: '',
    description: '',
    dataSourceType: 'SERVICE',
    dataSourceBean: '',
    dataSourceMethod: '',
    dataSourceSql: '',
    queryParamClass: '',
    batchSize: 5000,
    maxExportCount: 1000000,
    fileNamePattern: '',
    enableMultiSheet: false,
    sheetMaxRows: 100000,
    permissionCode: '',
    status: 1,
    sort: 0,
    remark: ''
  })
  dialogVisible.value = true
}

// 编辑
const handleEdit = async (row) => {
  dialogTitle.value = '编辑配置'
  const res = await getExportConfig(row.id)
  Object.assign(form, res.data)
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该配置吗？', '提示', {
    type: 'warning'
  }).then(async () => {
    await deleteExportConfig(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  }).catch(() => {})
}

// 提交表单
const handleSubmit = async () => {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (form.id) {
      await updateExportConfig(form)
      ElMessage.success('更新成功')
    } else {
      await addExportConfig(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    handleQuery()
  } finally {
    submitLoading.value = false
  }
}

// 字段配置
const handleFields = async (row) => {
  currentConfigId.value = row.id
  const res = await getExportFields(row.id)
  fieldList.value = res.data || []
  fieldDialogVisible.value = true
}

// 添加字段
const handleAddField = () => {
  fieldList.value.push({
    fieldName: '',
    fieldLabel: '',
    fieldType: 'STRING',
    fieldWidth: 20,
    dictType: '',
    maskType: '',
    status: 1
  })
}

// 删除字段
const handleRemoveField = (index) => {
  fieldList.value.splice(index, 1)
}

// 保存字段配置
const handleSaveFields = async () => {
  fieldSaveLoading.value = true
  try {
    await saveExportFields(currentConfigId.value, fieldList.value)
    ElMessage.success('保存成功')
    fieldDialogVisible.value = false
  } finally {
    fieldSaveLoading.value = false
  }
}

// 初始化
handleQuery()
</script>

<style scoped>
.export-config-container {
  padding: 16px;
}
.search-card {
  margin-bottom: 16px;
}
.table-card :deep(.el-card__header) {
  padding: 12px 16px;
}
.field-toolbar {
  margin-bottom: 12px;
}
</style>
