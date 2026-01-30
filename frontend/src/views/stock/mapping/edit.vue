<template>
  <div class="mapping-edit-container">
    <el-card shadow="never">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑映射配置' : '添加映射配置' }}</span>
          <el-button :icon="Back" @click="handleBack">返回</el-button>
        </div>
      </template>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" style="max-width: 800px">
        <el-form-item label="映射编码" prop="mappingCode">
          <el-input v-model="form.mappingCode" placeholder="请输入映射编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="映射名称" prop="mappingName">
          <el-input v-model="form.mappingName" placeholder="请输入映射名称" />
        </el-form-item>
        <el-form-item label="源数据类型" prop="sourceType">
          <el-select v-model="form.sourceType" placeholder="请选择源数据类型" style="width: 100%">
            <el-option label="iTick股票列表" value="itick_stock_list" />
            <el-option label="iTick K线数据" value="itick_kline" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标表" prop="targetTable">
          <el-select v-model="form.targetTable" placeholder="请选择目标表" style="width: 100%">
            <el-option label="stk_stock_info" value="stk_stock_info" />
            <el-option label="stk_kline_daily" value="stk_kline_daily" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>

        <!-- 字段映射配置 -->
        <el-form-item label="字段映射">
          <div class="mapping-editor">
            <el-table :data="form.fieldMappings" border size="small">
              <el-table-column prop="source" label="源字段" width="180">
                <template #default="{ row, $index }">
                  <el-input v-model="row.source" placeholder="源字段名" size="small" />
                </template>
              </el-table-column>
              <el-table-column prop="target" label="目标字段" width="180">
                <template #default="{ row, $index }">
                  <el-input v-model="row.target" placeholder="目标字段名" size="small" />
                </template>
              </el-table-column>
              <el-table-column prop="type" label="类型" width="120">
                <template #default="{ row, $index }">
                  <el-select v-model="row.type" placeholder="类型" size="small">
                    <el-option label="string" value="string" />
                    <el-option label="int" value="int" />
                    <el-option label="long" value="long" />
                    <el-option label="decimal" value="decimal" />
                    <el-option label="date" value="date" />
                    <el-option label="datetime" value="datetime" />
                  </el-select>
                </template>
              </el-table-column>
              <el-table-column prop="scale" label="精度" width="100">
                <template #default="{ row, $index }">
                  <el-input-number
                    v-model="row.scale"
                    :min="0"
                    :max="10"
                    size="small"
                    :disabled="row.type !== 'decimal'"
                    style="width: 80px"
                  />
                </template>
              </el-table-column>
              <el-table-column label="操作" width="80" align="center">
                <template #default="{ $index }">
                  <el-button type="danger" link :icon="Delete" @click="removeMapping($index)" />
                </template>
              </el-table-column>
            </el-table>
            <el-button type="primary" link :icon="Plus" @click="addMapping" style="margin-top: 10px">
              添加字段映射
            </el-button>
          </div>
        </el-form-item>

        <el-form-item label="转换脚本" prop="transformScript">
          <el-input
            v-model="form.transformScript"
            type="textarea"
            :rows="4"
            placeholder="可选的转换脚本（JavaScript）"
          />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="submitting">保存</el-button>
          <el-button @click="handleBack">取消</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Back, Plus, Delete } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getMappingById, saveMapping, updateMapping } from '@/api/stock'

const route = useRoute()
const router = useRouter()

const isEdit = computed(() => !!route.params.id)
const submitting = ref(false)
const formRef = ref(null)

const form = ref({
  id: null,
  mappingCode: '',
  mappingName: '',
  sourceType: '',
  targetTable: '',
  status: 1,
  fieldMappings: [],
  transformScript: ''
})

const rules = {
  mappingCode: [{ required: true, message: '请输入映射编码', trigger: 'blur' }],
  mappingName: [{ required: true, message: '请输入映射名称', trigger: 'blur' }],
  sourceType: [{ required: true, message: '请选择源数据类型', trigger: 'change' }],
  targetTable: [{ required: true, message: '请选择目标表', trigger: 'change' }]
}

// 获取映射配置详情
const fetchMapping = async () => {
  if (!route.params.id) {
    return
  }
  try {
    const res = await getMappingById(route.params.id)
    const data = res.data || {}
    form.value = {
      ...data,
      fieldMappings: data.fieldMapping ? JSON.parse(data.fieldMapping).mappings || [] : []
    }
  } catch (error) {
    console.error('获取映射配置失败', error)
    ElMessage.error('获取映射配置失败')
  }
}

// 添加字段映射
const addMapping = () => {
  form.value.fieldMappings.push({
    source: '',
    target: '',
    type: 'string',
    scale: 0
  })
}

// 删除字段映射
const removeMapping = (index) => {
  form.value.fieldMappings.splice(index, 1)
}

// 提交
const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    submitting.value = true

    // 构建提交数据
    const submitData = {
      ...form.value,
      fieldMapping: JSON.stringify({
        mappings: form.value.fieldMappings
      })
    }
    delete submitData.fieldMappings

    if (isEdit.value) {
      await updateMapping(form.value.id, submitData)
      ElMessage.success('更新成功')
    } else {
      await saveMapping(submitData)
      ElMessage.success('添加成功')
    }

    router.push('/stock/mapping')
  } catch (error) {
    if (error !== false) {
      ElMessage.error(error.message || '操作失败')
    }
  } finally {
    submitting.value = false
  }
}

// 返回
const handleBack = () => {
  router.push('/stock/mapping')
}

onMounted(() => {
  fetchMapping()
})
</script>

<style scoped>
.mapping-edit-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.mapping-editor {
  width: 100%;
}
</style>
