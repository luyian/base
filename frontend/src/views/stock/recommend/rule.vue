<template>
  <div class="rule-container">
    <!-- 操作栏 -->
    <el-card class="action-card" shadow="never">
      <el-button type="primary" :icon="Refresh" @click="fetchRuleList">刷新</el-button>
    </el-card>

    <!-- 规则列表 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        stripe
        style="width: 100%"
      >
        <el-table-column prop="ruleCode" label="规则编码" width="180" align="center" />
        <el-table-column prop="ruleName" label="规则名称" width="150" align="center" />
        <el-table-column prop="ruleDesc" label="规则描述" min-width="300" show-overflow-tooltip />
        <el-table-column prop="category" label="分类" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.category === 'TECHNICAL'" type="primary">技术面</el-tag>
            <el-tag v-else-if="row.category === 'FUNDAMENTAL'" type="success">基本面</el-tag>
            <el-tag v-else type="info">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="scoreType" label="打分类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.scoreType === 'FIXED'" type="success">固定分</el-tag>
            <el-tag v-else-if="row.scoreType === 'DYNAMIC'" type="warning">动态分</el-tag>
            <el-tag v-else type="info">{{ row.scoreType }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="baseScore" label="基础分数" width="100" align="center" />
        <el-table-column prop="maxScore" label="最高分数" width="100" align="center" />
        <el-table-column prop="weight" label="权重" width="80" align="center">
          <template #default="{ row }">
            <el-tag type="danger">{{ row.weight }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">启用</el-tag>
            <el-tag v-else type="info">禁用</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="操作" width="250" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(row)">编辑</el-button>
            <el-button
              v-if="row.status === 1"
              type="warning"
              link
              :icon="Close"
              @click="handleDisable(row)"
            >
              禁用
            </el-button>
            <el-button
              v-else
              type="success"
              link
              :icon="Check"
              @click="handleEnable(row)"
            >
              启用
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 编辑规则弹窗 -->
    <el-dialog
      v-model="editDialogVisible"
      :title="dialogTitle"
      width="600px"
      destroy-on-close
    >
      <el-form :model="editForm" :rules="rules" ref="editFormRef" label-width="120px">
        <el-form-item label="规则编码">
          <el-input v-model="editForm.ruleCode" disabled />
        </el-form-item>
        <el-form-item label="规则名称" prop="ruleName">
          <el-input v-model="editForm.ruleName" placeholder="请输入规则名称" />
        </el-form-item>
        <el-form-item label="规则描述">
          <el-input
            v-model="editForm.ruleDesc"
            type="textarea"
            :rows="3"
            placeholder="请输入规则描述"
          />
        </el-form-item>
        <el-form-item label="规则分类" prop="category">
          <el-select v-model="editForm.category" placeholder="请选择分类" style="width: 100%">
            <el-option label="技术面" value="TECHNICAL" />
            <el-option label="基本面" value="FUNDAMENTAL" />
          </el-select>
        </el-form-item>
        <el-form-item label="打分类型" prop="scoreType">
          <el-select v-model="editForm.scoreType" placeholder="请选择打分类型" style="width: 100%">
            <el-option label="固定分" value="FIXED" />
            <el-option label="动态分" value="DYNAMIC" />
          </el-select>
        </el-form-item>
        <el-form-item label="基础分数" prop="baseScore">
          <el-input-number
            v-model="editForm.baseScore"
            :min="0"
            :max="100"
            placeholder="请输入基础分数"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="最高分数" prop="maxScore">
          <el-input-number
            v-model="editForm.maxScore"
            :min="0"
            :max="100"
            placeholder="请输入最高分数"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="权重系数" prop="weight">
          <el-input-number
            v-model="editForm.weight"
            :min="0"
            :max="10"
            :step="0.1"
            :precision="2"
            placeholder="请输入权重系数"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="规则参数">
          <el-input
            v-model="editForm.configJson"
            type="textarea"
            :rows="5"
            placeholder='请输入JSON格式参数，例如：{"period":20,"threshold":1.5}'
          />
          <div style="color: #909399; font-size: 12px; margin-top: 5px;">
            JSON格式，用于配置规则的参数（如周期、阈值等）
          </div>
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number
            v-model="editForm.sortOrder"
            :min="0"
            placeholder="请输入排序号"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input
            v-model="editForm.remark"
            type="textarea"
            :rows="2"
            placeholder="请输入备注"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Edit, Close, Check } from '@element-plus/icons-vue'
import { listRules, updateRule, enableRule, disableRule } from '@/api/recommend'

// 表格数据
const tableData = ref([])
const loading = ref(false)

// 编辑弹窗
const editDialogVisible = ref(false)
const dialogTitle = ref('编辑规则')
const editFormRef = ref(null)
const saving = ref(false)

const editForm = reactive({
  id: null,
  ruleCode: '',
  ruleName: '',
  ruleDesc: '',
  category: 'TECHNICAL',
  scoreType: 'FIXED',
  baseScore: 10,
  maxScore: 10,
  weight: 1.0,
  configJson: '',
  sortOrder: 0,
  remark: ''
})

// 表单验证规则
const rules = {
  ruleName: [{ required: true, message: '请输入规则名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择规则分类', trigger: 'change' }],
  scoreType: [{ required: true, message: '请选择打分类型', trigger: 'change' }],
  baseScore: [{ required: true, message: '请输入基础分数', trigger: 'blur' }],
  maxScore: [{ required: true, message: '请输入最高分数', trigger: 'blur' }],
  weight: [{ required: true, message: '请输入权重系数', trigger: 'blur' }],
  sortOrder: [{ required: true, message: '请输入排序号', trigger: 'blur' }]
}

/**
 * 获取规则列表
 */
const fetchRuleList = async () => {
  loading.value = true
  try {
    const res = await listRules()
    if (res.code === 200) {
      tableData.value = res.data || []
    } else {
      ElMessage.error(res.message || '查询失败')
    }
  } catch (error) {
    console.error('查询规则列表失败', error)
    ElMessage.error('查询失败')
  } finally {
    loading.value = false
  }
}

/**
 * 编辑规则
 */
const handleEdit = (row) => {
  Object.assign(editForm, {
    id: row.id,
    ruleCode: row.ruleCode,
    ruleName: row.ruleName,
    ruleDesc: row.ruleDesc,
    category: row.category,
    scoreType: row.scoreType,
    baseScore: row.baseScore,
    maxScore: row.maxScore,
    weight: row.weight,
    configJson: row.configJson,
    sortOrder: row.sortOrder,
    remark: row.remark
  })
  dialogTitle.value = `编辑规则 - ${row.ruleName}`
  editDialogVisible.value = true
}

/**
 * 保存规则
 */
const handleSave = async () => {
  try {
    await editFormRef.value.validate()

    // 验证JSON格式
    if (editForm.configJson && editForm.configJson.trim()) {
      try {
        JSON.parse(editForm.configJson)
      } catch (error) {
        ElMessage.error('规则参数格式错误，请输入有效的JSON')
        return
      }
    }

    saving.value = true
    const res = await updateRule(editForm.id, editForm)
    if (res.code === 200) {
      ElMessage.success('保存成功')
      editDialogVisible.value = false
      fetchRuleList()
    } else {
      ElMessage.error(res.message || '保存失败')
    }
  } catch (error) {
    if (error !== false) {
      console.error('保存规则失败', error)
      ElMessage.error('保存失败')
    }
  } finally {
    saving.value = false
  }
}

/**
 * 启用规则
 */
const handleEnable = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要启用规则"${row.ruleName}"吗？`, '确认启用', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await enableRule(row.id)
    if (res.code === 200) {
      ElMessage.success('启用成功')
      fetchRuleList()
    } else {
      ElMessage.error(res.message || '启用失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('启用规则失败', error)
      ElMessage.error('启用失败')
    }
  }
}

/**
 * 禁用规则
 */
const handleDisable = async (row) => {
  try {
    await ElMessageBox.confirm(`确定要禁用规则"${row.ruleName}"吗？`, '确认禁用', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })

    const res = await disableRule(row.id)
    if (res.code === 200) {
      ElMessage.success('禁用成功')
      fetchRuleList()
    } else {
      ElMessage.error(res.message || '禁用失败')
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('禁用规则失败', error)
      ElMessage.error('禁用失败')
    }
  }
}

onMounted(() => {
  fetchRuleList()
})
</script>

<style scoped>
.rule-container {
  padding: 20px;
}

.action-card {
  margin-bottom: 20px;
}

.table-card {
  min-height: 600px;
}
</style>
