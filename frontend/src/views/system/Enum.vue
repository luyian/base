<template>
  <div class="enum-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="枚举类型">
          <el-input v-model="queryForm.enumType" placeholder="请输入枚举类型" clearable />
        </el-form-item>
        <el-form-item label="枚举标签">
          <el-input v-model="queryForm.enumLabel" placeholder="请输入枚举标签" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
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
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增</el-button>
        <el-button type="danger" :icon="Delete" :disabled="selectedIds.length === 0" @click="handleBatchDelete">
          批量删除
        </el-button>
        <el-button type="success" :icon="Refresh" @click="handleRefreshCache">刷新缓存</el-button>
      </div>

      <!-- 枚举列表表格 -->
      <el-table
        :data="tableData"
        style="width: 100%; margin-top: 20px"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="enumType" label="枚举类型" width="150" />
        <el-table-column prop="enumValue" label="枚举值" width="120" />
        <el-table-column prop="enumLabel" label="枚举标签" width="120" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" :icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="queryForm.current"
        v-model:page-size="queryForm.size"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleQuery"
        @current-change="handleQuery"
        style="margin-top: 20px; justify-content: flex-end"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      @close="handleDialogClose"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="枚举类型" prop="enumType">
          <el-input v-model="form.enumType" placeholder="请输入枚举类型" />
        </el-form-item>
        <el-form-item label="枚举值" prop="enumValue">
          <el-input v-model="form.enumValue" placeholder="请输入枚举值" />
        </el-form-item>
        <el-form-item label="枚举标签" prop="enumLabel">
          <el-input v-model="form.enumLabel" placeholder="请输入枚举标签" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="form.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  pageEnums,
  getEnumById,
  addEnum,
  updateEnum,
  deleteEnum,
  batchDeleteEnums,
  refreshCache
} from '@/api/enum'

// 查询表单
const queryForm = reactive({
  enumType: '',
  enumLabel: '',
  status: null,
  current: 1,
  size: 10
})

// 表格数据
const tableData = ref([])
const total = ref(0)
const selectedIds = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const form = reactive({
  id: null,
  enumType: '',
  enumValue: '',
  enumLabel: '',
  status: 1,
  sort: 0,
  remark: ''
})

// 表单验证规则
const formRules = {
  enumType: [
    { required: true, message: '请输入枚举类型', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  enumValue: [
    { required: true, message: '请输入枚举值', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  enumLabel: [
    { required: true, message: '请输入枚举标签', trigger: 'blur' },
    { min: 1, max: 50, message: '长度在 1 到 50 个字符', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  sort: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
}

// 查询枚举列表
const handleQuery = async () => {
  try {
    const { data } = await pageEnums(queryForm)
    tableData.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error('查询失败')
  }
}

// 重置查询
const handleReset = () => {
  queryForm.enumType = ''
  queryForm.enumLabel = ''
  queryForm.status = null
  queryForm.current = 1
  queryForm.size = 10
  handleQuery()
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 新增枚举
const handleAdd = () => {
  dialogTitle.value = '新增枚举'
  dialogVisible.value = true
}

// 编辑枚举
const handleEdit = async (row) => {
  try {
    const { data } = await getEnumById(row.id)
    Object.assign(form, data)
    dialogTitle.value = '编辑枚举'
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取枚举详情失败')
  }
}

// 删除枚举
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该枚举吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteEnum(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除枚举
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除选中的枚举吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await batchDeleteEnums(selectedIds.value)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 刷新缓存
const handleRefreshCache = async () => {
  try {
    await refreshCache()
    ElMessage.success('刷新缓存成功')
  } catch (error) {
    ElMessage.error('刷新缓存失败')
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (form.id) {
      await updateEnum(form)
      ElMessage.success('编辑成功')
    } else {
      await addEnum(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(form.id ? '编辑失败' : '新增失败')
    }
  }
}

// 对话框关闭
const handleDialogClose = () => {
  formRef.value?.resetFields()
  form.id = null
  form.enumType = ''
  form.enumValue = ''
  form.enumLabel = ''
  form.status = 1
  form.sort = 0
  form.remark = ''
}

// 页面加载时查询数据
onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.enum-container {
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
</style>
