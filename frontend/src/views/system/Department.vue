<template>
  <div class="department-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="部门名称">
          <el-input
            v-model="queryForm.deptName"
            placeholder="请输入部门名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="部门编码">
          <el-input
            v-model="queryForm.deptCode"
            placeholder="请输入部门编码"
            clearable
            @keyup.enter="handleQuery"
          />
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
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增部门</el-button>
        <el-button :icon="Expand" @click="handleExpandAll">展开全部</el-button>
        <el-button :icon="Fold" @click="handleCollapseAll">折叠全部</el-button>
      </div>

      <!-- 部门树表格 -->
      <el-table
        ref="tableRef"
        :data="departmentTree"
        style="width: 100%"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        v-loading="loading"
        default-expand-all
      >
        <el-table-column prop="deptName" label="部门名称" min-width="200" />
        <el-table-column prop="deptCode" label="部门编码" min-width="150" />
        <el-table-column prop="leader" label="负责人" width="120" />
        <el-table-column prop="phone" label="联系电话" width="150" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Plus" @click="handleAddChild(row)">
              新增
            </el-button>
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      @close="handleDialogClose"
    >
      <el-form
        ref="departmentFormRef"
        :model="departmentForm"
        :rules="departmentRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="上级部门" prop="parentId">
              <el-tree-select
                v-model="departmentForm.parentId"
                :data="departmentTreeOptions"
                :props="{ value: 'id', label: 'deptName', children: 'children' }"
                check-strictly
                placeholder="请选择上级部门"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="部门名称" prop="deptName">
              <el-input v-model="departmentForm.deptName" placeholder="请输入部门名称" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="部门编码" prop="deptCode">
              <el-input v-model="departmentForm.deptCode" placeholder="请输入部门编码" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人" prop="leader">
              <el-input v-model="departmentForm.leader" placeholder="请输入负责人" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系电话" prop="phone">
              <el-input v-model="departmentForm.phone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="departmentForm.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="departmentForm.status">
                <el-radio :value="1">正常</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="departmentForm.sort" :min="0" :max="9999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="departmentForm.remark"
            type="textarea"
            :rows="3"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Expand, Fold } from '@element-plus/icons-vue'
import {
  treeDepartments,
  getDepartmentById,
  addDepartment,
  updateDepartment,
  deleteDepartment,
  getAllDepartmentTree
} from '@/api/department'

// 查询表单
const queryForm = reactive({
  deptName: '',
  deptCode: '',
  status: null
})

// 部门树
const departmentTree = ref([])
const departmentTreeOptions = ref([])
const loading = ref(false)
const tableRef = ref(null)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const departmentFormRef = ref(null)
const submitLoading = ref(false)

// 部门表单
const departmentForm = reactive({
  id: null,
  parentId: 0,
  deptName: '',
  deptCode: '',
  leader: '',
  phone: '',
  email: '',
  status: 1,
  sort: 0,
  remark: ''
})

// 表单验证规则
const departmentRules = {
  deptName: [
    { required: true, message: '请输入部门名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  deptCode: [
    { required: true, message: '请输入部门编码', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  leader: [
    { max: 20, message: '长度不能超过 20 个字符', trigger: 'blur' }
  ],
  phone: [
    { max: 20, message: '长度不能超过 20 个字符', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { max: 50, message: '长度不能超过 50 个字符', trigger: 'blur' },
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  sort: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
}

// 查询部门树
const handleQuery = async () => {
  loading.value = true
  try {
    const { data } = await treeDepartments(queryForm)
    departmentTree.value = data
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
const handleReset = () => {
  queryForm.deptName = ''
  queryForm.deptCode = ''
  queryForm.status = null
  handleQuery()
}

// 新增部门
const handleAdd = () => {
  dialogTitle.value = '新增部门'
  isEdit.value = false
  departmentForm.parentId = 0
  dialogVisible.value = true
  loadDepartmentTreeOptions()
}

// 新增子部门
const handleAddChild = (row) => {
  dialogTitle.value = '新增子部门'
  isEdit.value = false
  departmentForm.parentId = row.id
  dialogVisible.value = true
  loadDepartmentTreeOptions()
}

// 编辑部门
const handleEdit = async (row) => {
  dialogTitle.value = '编辑部门'
  isEdit.value = true
  try {
    const { data } = await getDepartmentById(row.id)
    Object.assign(departmentForm, data)
    dialogVisible.value = true
    loadDepartmentTreeOptions()
  } catch (error) {
    ElMessage.error('获取部门信息失败')
  }
}

// 删除部门
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该部门吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteDepartment(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 展开全部
const handleExpandAll = () => {
  toggleRowExpansion(departmentTree.value, true)
}

// 折叠全部
const handleCollapseAll = () => {
  toggleRowExpansion(departmentTree.value, false)
}

// 切换展开/折叠
const toggleRowExpansion = (data, isExpand) => {
  data.forEach(item => {
    tableRef.value.toggleRowExpansion(item, isExpand)
    if (item.children && item.children.length > 0) {
      toggleRowExpansion(item.children, isExpand)
    }
  })
}

// 提交表单
const handleSubmit = async () => {
  try {
    await departmentFormRef.value.validate()
    submitLoading.value = true
    if (isEdit.value) {
      await updateDepartment(departmentForm)
      ElMessage.success('编辑成功')
    } else {
      await addDepartment(departmentForm)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(isEdit ? '编辑失败' : '新增失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 对话框关闭
const handleDialogClose = () => {
  departmentFormRef.value?.resetFields()
  Object.assign(departmentForm, {
    id: null,
    parentId: 0,
    deptName: '',
    deptCode: '',
    leader: '',
    phone: '',
    email: '',
    status: 1,
    sort: 0,
    remark: ''
  })
}

// 加载部门树选项（用于选择上级部门）
const loadDepartmentTreeOptions = async () => {
  try {
    const { data } = await getAllDepartmentTree()
    // 添加顶级选项
    departmentTreeOptions.value = [
      {
        id: 0,
        deptName: '顶级部门',
        children: data
      }
    ]
  } catch (error) {
    ElMessage.error('加载部门树失败')
  }
}

// 页面加载时查询数据
onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.department-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-form {
  margin-bottom: 0;
}

.table-card {
  margin-bottom: 20px;
}

.toolbar {
  margin-bottom: 20px;
}
</style>
