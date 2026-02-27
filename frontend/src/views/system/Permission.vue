<template>
  <div class="permission-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="权限名称">
          <el-input
            v-model="queryForm.permissionName"
            placeholder="请输入权限名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="权限编码">
          <el-input
            v-model="queryForm.permissionCode"
            placeholder="请输入权限编码"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="权限类型">
          <el-select v-model="queryForm.type" placeholder="请选择权限类型" clearable style="width: 140px">
            <el-option label="目录" :value="1" />
            <el-option label="菜单" :value="2" />
            <el-option label="按钮" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryForm.status" placeholder="请选择状态" clearable style="width: 120px">
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
        <el-button v-permission="'system:permission:add'" type="primary" :icon="Plus" @click="handleAdd">新增权限</el-button>
        <el-button :icon="Expand" @click="handleExpandAll">展开全部</el-button>
        <el-button :icon="Fold" @click="handleCollapseAll">折叠全部</el-button>
      </div>

      <!-- 权限树表格 -->
      <el-table
        ref="tableRef"
        :data="permissionTree"
        style="width: 100%"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        v-loading="loading"
      >
        <el-table-column prop="permissionName" label="权限名称" min-width="200" />
        <el-table-column prop="permissionCode" label="权限编码" min-width="150" />
        <el-table-column prop="type" label="权限类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTagType(row.type)">
              {{ row.typeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="80">
          <template #default="{ row }">
            <el-icon v-if="row.icon">
              <component :is="row.icon" />
            </el-icon>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" min-width="150" />
        <el-table-column prop="component" label="组件路径" min-width="150" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="visible" label="可见" width="80">
          <template #default="{ row }">
            <el-tag :type="row.visible === 1 ? 'success' : 'info'">
              {{ row.visible === 1 ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'system:permission:add'" type="primary" link :icon="Plus" @click="handleAddChild(row)">
              新增
            </el-button>
            <el-button v-permission="'system:permission:edit'" type="primary" link :icon="Edit" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button v-permission="'system:permission:delete'" type="danger" link :icon="Delete" @click="handleDelete(row)">
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
        ref="permissionFormRef"
        :model="permissionForm"
        :rules="permissionRules"
        label-width="100px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="上级权限" prop="parentId">
              <el-tree-select
                v-model="permissionForm.parentId"
                :data="permissionTreeOptions"
                :props="{ value: 'id', label: 'permissionName', children: 'children' }"
                check-strictly
                placeholder="请选择上级权限"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="权限类型" prop="type">
              <el-radio-group v-model="permissionForm.type">
                <el-radio :value="1">目录</el-radio>
                <el-radio :value="2">菜单</el-radio>
                <el-radio :value="3">按钮</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="权限名称" prop="permissionName">
              <el-input v-model="permissionForm.permissionName" placeholder="请输入权限名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="权限编码" prop="permissionCode">
              <el-input v-model="permissionForm.permissionCode" placeholder="请输入权限编码" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="permissionForm.type === 1">
          <el-col :span="12">
            <el-form-item label="路由路径" prop="path">
              <el-input v-model="permissionForm.path" placeholder="请输入路由路径" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="组件路径" prop="component">
              <el-input v-model="permissionForm.component" placeholder="请输入组件路径" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20" v-if="permissionForm.type === 1">
          <el-col :span="12">
            <el-form-item label="图标" prop="icon">
              <el-input v-model="permissionForm.icon" placeholder="请输入图标名称">
                <template #prefix>
               <el-icon v-if="permissionForm.icon">
                    <component :is="permissionForm.icon" />
                  </el-icon>
                </template>
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否可见" prop="visible">
              <el-radio-group v-model="permissionForm.visible">
                <el-radio :value="1">显示</el-radio>
                <el-radio :value="0">隐藏</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="permissionForm.status">
                <el-radio :value="1">正常</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="permissionForm.sort" :min="0" :max="9999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="permissionForm.remark"
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
  treePermissions,
  getPermissionById,
  addPermission,
  updatePermission,
  deletePermission
} from '@/api/permission'

// 查询表单
const queryForm = reactive({
  permissionName: '',
  permissionCode: '',
  type: null,
  status: null
})

// 权限树
const permissionTree = ref([])
const permissionTreeOptions = ref([])
const loading = ref(false)
const tableRef = ref(null)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const permissionFormRef = ref(null)
const submitLoading = ref(false)

// 权限表单
const permissionForm = reactive({
  id: null,
  parentId: 0,
  permissionName: '',
  permissionCode: '',
  type: 1,
  path: '',
  component: '',
  icon: '',
  status: 1,
  sort: 0,
  visible: 1,
  remark: ''
})

// 表单验证规则
const permissionRules = {
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  permissionCode: [
    { required: true, message: '请输入权限编码', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  type: [
    { required: true, message: '请选择权限类型', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  sort: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
}

// 获取类型标签颜色
const getTypeTagType = (type) => {
  const typeMap = {
    1: 'info',      // 目录 - 灰色
    2: 'primary',   // 菜单 - 蓝色
    3: 'success'    // 按钮 - 绿色
  }
  return typeMap[type] || ''
}

// 查询权限树
const handleQuery = async () => {
  loading.value = true
  try {
    const { data } = await treePermissions(queryForm)
    permissionTree.value = data
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
const handleReset = () => {
  queryForm.permissionName = ''
  queryForm.permissionCode = ''
  queryForm.type = null
  queryForm.status = null
  handleQuery()
}

// 新增权限
const handleAdd = () => {
  dialogTitle.value = '新增权限'
  isEdit.value = false
  permissionForm.parentId = 0
  dialogVisible.value = true
  loadPermissionTreeOptions()
}

// 新增子权限
const handleAddChild = (row) => {
  dialogTitle.value = '新增子权限'
  isEdit.value = false
  permissionForm.parentId = row.id
  dialogVisible.value = true
  loadPermissionTreeOptions()
}

// 编辑权限
const handleEdit = async (row) => {
  dialogTitle.value = '编辑权限'
  isEdit.value = true
  try {
    const { data } = await getPermissionById(row.id)
    Object.assign(permissionForm, data)
    dialogVisible.value = true
    loadPermissionTreeOptions()
  } catch (error) {
    ElMessage.error('获取权限信息失败')
  }
}

// 删除权限
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该权限吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deletePermission(row.id)
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
  toggleRowExpansion(permissionTree.value, true)
}

// 折叠全部
const handleCollapseAll = () => {
  toggleRowExpansion(permissionTree.value, false)
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
    await permissionFormRef.value.validate()
    submitLoading.value = true
    if (isEdit.value) {
      await updatePermission(permissionForm)
      ElMessage.success('编辑成功')
    } else {
      await addPermission(permissionForm)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    handleQuery()
  } catch (error) {
    if (error !== false) {
      ElMessage.error(isEdit.value ? '编辑失败' : '新增失败')
    }
  } finally {
    submitLoading.value = false
  }
}

// 对话框关闭
const handleDialogClose = () => {
  permissionFormRef.value?.resetFields()
  Object.assign(permissionForm, {
    id: null,
    parentId: 0,
    permissionName: '',
    permissionCode: '',
    type: 1,
    path: '',
    component: '',
    icon: '',
    status: 1,
    sort: 0,
    visible: 1,
    remark: ''
  })
}

// 加载权限树选项（用于选择上级权限）
const loadPermissionTreeOptions = async () => {
  try {
    const { data } = await treePermissions({ status: 1 })
    // 添加顶级选项
    permissionTreeOptions.value = [
      {
        id: 0,
        permissionName: '顶级权限',
        children: data
      }
    ]
  } catch (error) {
    ElMessage.error('加载权限树失败')
  }
}

// 页面加载时查询数据
onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.permission-container {
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
