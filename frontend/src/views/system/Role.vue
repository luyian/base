<template>
  <div class="role-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="角色名称">
          <el-input
            v-model="queryForm.roleName"
            placeholder="请输入角色名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="角色编码">
          <el-input
            v-model="queryForm.roleCode"
            placeholder="请输入角色编码"
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
        <el-button v-permission="'system:role:add'" type="primary" :icon="Plus" @click="handleAdd">新增角色</el-button>
        <el-button
          v-permission="'system:role:delete'"
          type="danger"
          :icon="Delete"
          :disabled="selectedIds.length === 0"
          @click="handleBatchDelete"
        >
          批量删除
        </el-button>
      </div>

      <!-- 角色列表表格 -->
      <el-table
        :data="roleList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        v-loading="loading"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column prop="id" label="角色ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" min-width="120" />
        <el-table-column prop="roleCode" label="角色编码" min-width="120" />
        <el-table-column prop="dataScope" label="数据权限" min-width="120">
          <template #default="{ row }">
            <el-tag :type="getDataScopeType(row.dataScope)">
              {{ row.dataScopeName }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-switch
              v-model="row.status"
              :active-value="1"
              :inactive-value="0"
              @change="handleStatusChange(row)"
            />
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button v-permission="'system:role:edit'" type="primary" link :icon="Edit" @click="handleEdit(row)">
              编辑
            </el-button>
            <el-button v-permission="'system:role:permission'" type="primary" link :icon="Key" @click="handleAssignPermissions(row)">
              分配权限
            </el-button>
            <el-button v-permission="'system:role:delete'" type="danger" link :icon="Delete" @click="handleDelete(row)">
              删除
            </el-button>
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
        ref="roleFormRef"
        :model="roleForm"
        :rules="roleRules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="roleForm.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="角色编码" prop="roleCode">
          <el-input
            v-model="roleForm.roleCode"
            placeholder="请输入角色编码"
            :disabled="isEdit"
          />
        </el-form-item>
        <el-form-item label="数据权限" prop="dataScope">
          <el-select v-model="roleForm.dataScope" placeholder="请选择数据权限范围" @change="handleDataScopeChange">
            <el-option label="全部数据权限" :value="1" />
            <el-option label="自定义数据权限" :value="2" />
            <el-option label="本部门数据权限" :value="3" />
            <el-option label="本部门及以下数据权限" :value="4" />
            <el-option label="仅本人数据权限" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="数据权限范围" v-if="roleForm.dataScope === 2">
          <el-tree-select
            v-model="roleForm.departmentIds"
            :data="departmentTree"
            multiple
            :render-after-expand="false"
            show-checkbox
            check-strictly
            placeholder="请选择部门"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="roleForm.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="roleForm.sort" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="roleForm.remark"
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

    <!-- 分配权限对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="分配权限"
      width="600px"
      @close="handlePermissionDialogClose"
    >
      <el-tree
        ref="permissionTreeRef"
        :data="permissionTree"
        show-checkbox
        node-key="id"
        :props="{ children: 'children', label: 'permissionName' }"
        default-expand-all
      />
      <template #footer>
        <el-button @click="permissionDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitPermissions" :loading="submitLoading">
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Edit, Key } from '@element-plus/icons-vue'
import {
  pageRoles,
  getRoleById,
  addRole,
  updateRole,
  deleteRole,
  batchDeleteRoles,
  toggleRoleStatus,
  assignPermissions,
  getRolePermissionIds
} from '@/api/role'
import { getAllPermissionTree } from '@/api/permission'
import { getAllDepartmentTree } from '@/api/department'

// 查询表单
const queryForm = reactive({
  roleName: '',
  roleCode: '',
  status: null,
  current: 1,
  size: 10
})

// 角色列表
const roleList = ref([])
const total = ref(0)
const loading = ref(false)
const selectedIds = ref([])

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const roleFormRef = ref(null)
const submitLoading = ref(false)

// 角色表单
const roleForm = reactive({
  id: null,
  roleName: '',
  roleCode: '',
  dataScope: 1,
  departmentIds: [],
  status: 1,
  sort: 0,
  remark: ''
})

// 表单验证规则
const roleRules = {
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' },
    { pattern: /^[A-Z_]+$/, message: '角色编码只能包含大写字母和下划线', trigger: 'blur' }
  ],
  dataScope: [
    { required: true, message: '请选择数据权限范围', trigger: 'change' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  sort: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
}

// 分配权限对话框
const permissionDialogVisible = ref(false)
const permissionTreeRef = ref(null)
const permissionTree = ref([])
const currentRoleId = ref(null)

// 部门树
const departmentTree = ref([])

// 查询角色列表
const handleQuery = async () => {
  loading.value = true
  try {
    const { data } = await pageRoles(queryForm)
    roleList.value = data.records
    total.value = data.total
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 重置查询
const handleReset = () => {
  queryForm.roleName = ''
  queryForm.roleCode = ''
  queryForm.status = null
  queryForm.current = 1
  queryForm.size = 10
  handleQuery()
}

// 新增角色
const handleAdd = () => {
  dialogTitle.value = '新增角色'
  isEdit.value = false
  dialogVisible.value = true
}

// 编辑角色
const handleEdit = async (row) => {
  dialogTitle.value = '编辑角色'
  isEdit.value = true
  try {
    const { data } = await getRoleById(row.id)
    Object.assign(roleForm, data)
    dialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取角色信息失败')
  }
}

// 删除角色
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteRole(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  try {
    await ElMessageBox.confirm('确定要删除选中的角色吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await batchDeleteRoles(selectedIds.value)
    ElMessage.success('删除成功')
    selectedIds.value = []
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 切换状态
const handleStatusChange = async (row) => {
  try {
    await toggleRoleStatus(row.id, row.status)
    ElMessage.success('状态切换成功')
  } catch (error) {
    row.status = row.status === 1 ? 0 : 1
    ElMessage.error('状态切换失败')
  }
}

// 表格选择变化
const handleSelectionChange = (selection) => {
  selectedIds.value = selection.map(item => item.id)
}

// 提交表单
const handleSubmit = async () => {
  try {
    await roleFormRef.value.validate()
    submitLoading.value = true
    if (isEdit.value) {
      await updateRole(roleForm)
      ElMessage.success('编辑成功')
    } else {
      await addRole(roleForm)
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
  roleFormRef.value?.resetFields()
  Object.assign(roleForm, {
    id: null,
    roleName: '',
    roleCode: '',
    dataScope: 1,
    departmentIds: [],
    status: 1,
    sort: 0,
    remark: ''
  })
}

// 分配权限
const handleAssignPermissions = async (row) => {
  currentRoleId.value = row.id
  permissionDialogVisible.value = true

  try {
    // 获取所有权限树
    const { data: treeData } = await getAllPermissionTree()
    permissionTree.value = treeData

    // 获取角色已有权限
    const { data: permissionIds } = await getRolePermissionIds(row.id)

    // 设置已选中的权限（只设置叶子节点，避免父节点自动选中）
    setTimeout(() => {
      const leafKeys = getLeafKeys(treeData, permissionIds)
      permissionTreeRef.value?.setCheckedKeys(leafKeys)
    }, 100)
  } catch (error) {
    ElMessage.error('获取权限数据失败')
  }
}

// 获取叶子节点的权限ID（只选中叶子节点，父节点会自动半选）
const getLeafKeys = (tree, checkedIds) => {
  const leafKeys = []
  const traverse = (nodes) => {
    nodes.forEach(node => {
      if (!node.children || node.children.length === 0) {
        // 叶子节点
        if (checkedIds.includes(node.id)) {
          leafKeys.push(node.id)
        }
      } else {
        // 非叶子节点，继续遍历
        traverse(node.children)
      }
    })
  }
  traverse(tree)
  return leafKeys
}

// 提交权限分配
const handleSubmitPermissions = async () => {
  try {
    submitLoading.value = true
    const checkedKeys = permissionTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = permissionTreeRef.value.getHalfCheckedKeys()
    const permissionIds = [...checkedKeys, ...halfCheckedKeys]

    await assignPermissions({
      roleId: currentRoleId.value,
      permissionIds
    })
    ElMessage.success('分配权限成功')
    permissionDialogVisible.value = false
  } catch (error) {
    ElMessage.error('分配权限失败')
  } finally {
    submitLoading.value = false
  }
}

// 权限对话框关闭
const handlePermissionDialogClose = () => {
  permissionTreeRef.value?.setCheckedKeys([])
  currentRoleId.value = null
}

// 获取数据权限标签类型
const getDataScopeType = (dataScope) => {
  const typeMap = {
    1: '',
    2: 'success',
    3: 'warning',
    4: 'danger',
    5: 'info'
  }
  return typeMap[dataScope] || ''
}

// 数据权限变化处理
const handleDataScopeChange = (value) => {
  if (value === 2) {
    // 选择自定义数据权限时，加载部门树
    loadDepartmentTree()
  } else {
    // 其他情况清空部门选择
    roleForm.departmentIds = []
  }
}

// 加载部门树
const loadDepartmentTree = async () => {
  try {
    const { data } = await getAllDepartmentTree()
    // 转换数据格式：id -> value, deptName -> label
    departmentTree.value = convertDeptTree(data)
  } catch (error) {
    ElMessage.error('加载部门树失败')
  }
}

// 转换部门树数据格式
const convertDeptTree = (tree) => {
  return tree.map(node => ({
    value: node.id,
    label: node.deptName,
    children: node.children && node.children.length > 0 ? convertDeptTree(node.children) : undefined
  }))
}

// 页面加载时查询数据
onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.role-container {
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

.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
