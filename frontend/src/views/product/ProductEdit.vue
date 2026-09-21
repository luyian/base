<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" v-loading="loading">
    <el-form-item label="商品名称" prop="name">
      <el-input v-model="form.name" placeholder="请输入商品名称" maxlength="100" />
    </el-form-item>
    <el-form-item label="分类">
      <el-input v-model="form.category" placeholder="如：食品/日用品" maxlength="50" />
    </el-form-item>
    <el-form-item label="规格">
      <el-input v-model="form.spec" placeholder="如：500ml/瓶装" maxlength="50" />
    </el-form-item>
    <el-form-item label="单位">
      <el-input v-model="form.unit" placeholder="如：瓶/盒/件" maxlength="20" />
    </el-form-item>
    <el-row :gutter="16">
      <el-col :span="12">
        <el-form-item label="售价">
          <el-input-number v-model="form.salePrice" :min="0" :precision="2" :controls="false" style="width: 100%" placeholder="0.00" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="成本价">
          <el-input-number v-model="form.costPrice" :min="0" :precision="2" :controls="false" style="width: 100%" placeholder="0.00" />
        </el-form-item>
      </el-col>
    </el-row>
    <el-row :gutter="16">
      <el-col :span="12">
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" :precision="0" :controls="false" style="width: 100%" placeholder="0" />
        </el-form-item>
      </el-col>
      <el-col :span="12">
        <el-form-item label="生产日期">
          <el-date-picker v-model="form.productionDate" type="date" value-format="YYYY-MM-DD" style="width: 100%" placeholder="选择日期" />
        </el-form-item>
      </el-col>
    </el-row>
    <el-form-item label="供应商">
      <el-input v-model="form.supplier" placeholder="请输入供应商" maxlength="100" />
    </el-form-item>
    <el-form-item label="备注">
      <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注" maxlength="200" />
    </el-form-item>

    <!-- 编辑态回显已绑定条码/二维码（含图片，可查看大图、可解绑） -->
    <el-collapse v-if="props.isEdit && props.productId" class="barcode-collapse">
      <el-collapse-item>
        <template #title>
          <span class="collapse-title">商品条码 / 二维码（点击展开查看）</span>
        </template>
        <ProductCodes :product-id="props.productId" />
      </el-collapse-item>
    </el-collapse>

    <div class="dialog-footer">
      <el-button @click="$emit('cancel')">取消</el-button>
      <el-button type="primary" :loading="saving" @click="handleSave">保存</el-button>
    </div>
  </el-form>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getProduct, createProduct, updateProduct } from '@/api/product'
import ProductCodes from './ProductCodes.vue'

const props = defineProps({
  productId: { type: Number, default: null },
  isEdit: { type: Boolean, default: false }
})
const emit = defineEmits(['saved', 'cancel'])

const formRef = ref(null)
const loading = ref(false)
const saving = ref(false)
const form = reactive({
  name: '',
  category: '',
  spec: '',
  unit: '',
  salePrice: null,
  costPrice: null,
  stock: 0,
  supplier: '',
  productionDate: null,
  remark: ''
})

const rules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }]
}

onMounted(async () => {
  if (props.isEdit && props.productId) {
    loading.value = true
    try {
      const res = await getProduct(props.productId)
      const data = res.data || res
      Object.assign(form, {
        name: data.name,
        category: data.category,
        spec: data.spec,
        unit: data.unit,
        salePrice: data.salePrice,
        costPrice: data.costPrice,
        stock: data.stock || 0,
        supplier: data.supplier,
        productionDate: data.productionDate,
        remark: data.remark
      })
    } finally {
      loading.value = false
    }
  }
})

async function handleSave() {
  await formRef.value.validate()
  saving.value = true
  try {
    const payload = { ...form, stock: form.stock || 0 }
    if (props.isEdit) {
      await updateProduct(props.productId, payload)
      ElMessage.success('保存成功')
    } else {
      await createProduct(payload)
      ElMessage.success('新增成功')
    }
    emit('saved')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
.barcode-collapse {
  margin-bottom: 16px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
}
.collapse-title {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}
</style>