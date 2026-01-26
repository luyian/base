<template>
  <div class="region-container">
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true" :model="queryForm" class="search-form">
        <el-form-item label="区划名称">
          <el-input
            v-model="queryForm.regionName"
            placeholder="请输入区划名称"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="区划代码">
          <el-input
            v-model="queryForm.regionCode"
            placeholder="请输入区划代码"
            clearable
            @keyup.enter="handleQuery"
          />
        </el-form-item>
        <el-form-item label="层级">
          <el-select v-model="queryForm.level" placeholder="请选择层级" clearable>
            <el-option label="省" :value="1" />
            <el-option label="市" :value="2" />
            <el-option label="区" :value="3" />
            <el-option label="街道" :value="4" />
          </el-select>
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
        <el-button type="primary" :icon="Plus" @click="handleAdd">新增区划</el-button>
        <el-button :icon="Refresh" @click="handleRefresh">刷新</el-button>
      </div>

      <!-- 区划树表格 -->
      <el-table
        ref="tableRef"
        :data="regionTree"
        style="width: 100%"
        row-key="id"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        v-loading="loading"
        lazy
        :load="loadChildren"
      >
        <el-table-column prop="regionName" label="区划名称" min-width="200" />
        <el-table-column prop="regionCode" label="区划代码" min-width="150" />
        <el-table-column prop="level" label="层级" width="100">
          <template #default="{ row }">
            <el-tag :type="getLevelTagType(row.level)">
              {{ getLevelText(row.level) }}
            </el-tag>
          </template>
        </el-table-column>
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
        ref="regionFormRef"
        :model="regionForm"
        :rules="regionRules"
        label-width="120px"
      >
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="上级区划" prop="parentId">
              <el-tree-select
                v-model="regionForm.parentId"
                :data="regionTreeOptions"
                :props="{ value: 'id', label: 'regionName', children: 'children' }"
      check-strictly
                placeholder="请选择上级区划"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="区划代码" prop="regionCode">
              <el-input v-model="regionForm.regionCode" placeholder="请输入区划代码" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="区划名称" prop="regionName">
              <el-input v-model="regionForm.regionName"older="请输入区划名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="层级" prop="level">
              <el-select v-model="regionForm.level" placeholder="请选择层级" style="width: 100%">
                <el-option label="省" :value="1" />
                <el-option label="市" :value="2" />
                <el-option label="区" :value="3" />
                <el-option label="街道" :value="4" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">    <el-col :span="12">
            <el-form-item label="简称" prop="shortName">
              <el-input v-model="regionForm.shortName" placeholder="请输入简称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="拼音" prop="pinyin">
              <el-input v-model="regionForm.pinyin" placeholder="请输入拼音" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="拼音首字母" prop="pinyinInitial">
              <el-input v-model="regionForm.pinyinInitial" placeholder="请输入拼音首字母" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="regionForm.status">
                <el-radio :value="1">正常</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="regionForm.sort" :min="0" :max="9999" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注" prop="remark">
          <el-input
            v-model="regionForm.remark"
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
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import {
  treeRegions,
  getRegionById,
  addRegion,
  updateRegion,
  deleteRegion,
  getChildrenByParentId,
  getRegionsByLevel
} from '@/api/region'

// 查询表单
const queryForm = reactive({
  regionName: '',
  regionCode: '',
  level: null,
  status: null
})

// 区划树
const regionTree = ref([])
const regionTreeOptions = ref([])
const loading = ref(false)
const tableRef = ref(null)

// 对话框
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const regionFormRef = ref(null)
const submitLoading = ref(false)

// 区划表单
const regionForm = reactive({
  id: null,
  parentId: 0,
  regionCode: '',
  regionName: '',
  level: null,
  shortName: '',
  pinyin: '',
  pinyinInitial: '',
  status: 1,
  sort: 0,
  remark: ''
})

// 表单验证规则
const regionRules = {
  regionCode: [
    { required: true, message: '请输入区划代码', trigger: 'blur' },
    { min: 2, max: 20, message: '长度在 2 到 20 个字符', trigger: 'blur' }
  ],
  regionName: [
    { required: true, message: '请输入区划名称', trigger: 'blur' },
    { min: 2, max: 50, message: '长度在 2 到 50 个字符', trigger: 'blur' }
  ],
  level: [
    { required: true, message: '请选择层级', trigger: 'change' }
  ],
  shortName: [
    { max: 20, message: '长度不能超过 20 个字符', trigger: 'blur' }
  ],
  pinyin: [
    { max: 100, message: '长度不能超过 100 个字符', trigger: 'blur' }
  ],
  pinyinInitial: [
    { max: 20, message: '长度不能超过 20 个字符', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ],
  sort: [
    { required: true, message: '请输入排序', trigger: 'blur' }
  ]
}

// 获取层级文本
const getLevelText = (level) => {
  const levelMap = {
    1: '省',
    2: '市',
    3: '区',
    4: '街道'
  }
  return levelMap[level] || '未知'
}

// 获取层级标签类型
const getLevelTagType = (level) => {
  const typeMap = {
    1: 'danger',
    2: 'warning',
    3: 'success',
    4: 'info'
  }
  return typeMap[level] || ''
}

// 查询区划树
const handleQuery = async () => {
  loading.value = true
  try {
    // 如果有搜索条件，使用树形查询
    if (queryForm.regionName || queryForm.regionCode || queryForm.level || queryForm.status !== null) {
      const { data } = await treeRegions(queryForm)
      regionTree.value = data
    } else {
      // 无搜索条件时，只加载省级数据（懒加载模式）
      const { data } = await getRegionsByLevel(1)
      regionTree.value = data.map(item => ({
        ...item,
        hasChildren: true // 省级数据都有子级
      }))
    }
  } catch (error) {
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

// 懒加载子级数据
const loadChildren = async (row, treeNode, resolve) => {
  try {
    const { data } = await getChildrenByParentId(row.id)
    // 标记是否有子级（市级和区级可能有子级，街道级没有）
    const children = data.map(item => ({
      ...item,
      hasChildren: item.level < 4 // 只有省、市、区有子级，街道没有
    }))
    resolve(children)
  } catch (error) {
    ElMessage.error('加载子级数据失败')
    resolve([])
  }
}

// 重置查询
const handleReset = () => {
  queryForm.regionName = ''
  queryForm.regionCode = ''
  queryForm.level = null
  queryForm.status = null
  handleQuery()
}

// 新增区划
const handleAdd = () => {
  dialogTitle.value = '新增区划'
  isEdit.value = false
  regionForm.parentId = 0
  dialogVisible.value = true
  loadRegionTreeOptions()
}

// 新增子区划
const handleAddChild = (row) => {
  dialogTitle.value = '新增子区划'
  isEdit.value = false
  regionForm.parentId = row.id
  dialogVisible.value = true
  loadRegionTreeOptions()
}

// 编辑区划
const handleEdit = async (row) => {
  dialogTitle.value = '编辑区划'
  isEdit.value = true
  try {
    const { data } = await getRegionById(row.id)
    Object.assign(regionForm, data)
    dialogVisible.value = true
    loadRegionTreeOptions()
  } catch (error) {
    ElMessage.error('获取区划信息失败')
  }
}

// 删除区划
const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该区划吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteRegion(row.id)
    ElMessage.success('删除成功')
    handleQuery()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 刷新数据
const handleRefresh = () => {
  handleQuery()
}

// 提交表单
const handleSubmit = async () => {
  try {
    await regionFormRef.value.validate()
    submitLoading.value = true
    if (isEdit.value) {
      await updateRegion(regionForm)
      ElMessage.success('编辑成功')
    } else {
      await addRegion(regionForm)
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
  regionFormRef.value?.resetFields()
  Object.assign(regionForm, {
    id: null,
    parentId: 0,
    regionCode: '',
    regionName: '',
    level: null,
    shortName: '',
    pinyin: '',
    pinyinInitial: '',
    status: 1,
    sort: 0,
    remark: ''
  })
}

// 加载区划树选项（用于选择上级区划）
const loadRegionTreeOptions = async () => {
  try {
    const { data } = await treeRegions({ status: 1 })
    // 添加顶级选项
    regionTreeOptions.value = [
      {
        id: 0,
        regionName: '顶级区划',
        children: data
      }
    ]
  } catch (error) {
    ElMessage.error('加载区划树失败')
  }
}

// 页面加载时查询数据
onMounted(() => {
  handleQuery()
})
</script>

<style scoped>
.region-container {
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
