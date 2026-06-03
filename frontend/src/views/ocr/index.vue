<template>
  <div class="ocr-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>OCR 智能识别</span>
          <el-select v-model="selectedProvider" placeholder="自动选择供应商" clearable size="small" style="width: 160px">
            <el-option v-for="p in providers" :key="p.name" :label="p.displayName" :value="p.name" />
          </el-select>
        </div>
      </template>

      <el-tabs v-model="activeTab" type="border-card">
        <!-- 身份证识别 -->
        <el-tab-pane label="身份证" name="idCard">
          <div class="ocr-content">
            <div class="upload-area">
              <el-radio-group v-model="idCardSide" style="margin-bottom: 12px">
                <el-radio-button value="front">正面（人像面）</el-radio-button>
                <el-radio-button value="back">背面（国徽面）</el-radio-button>
              </el-radio-group>
              <el-upload
                class="upload-box"
                drag
                :auto-upload="false"
                :show-file-list="false"
                accept="image/*"
                :on-change="(file) => handleFileChange(file, 'idCard')"
              >
                <img v-if="idCardPreview" :src="idCardPreview" class="preview-img" />
                <div v-else class="upload-placeholder">
                  <el-icon size="40"><UploadFilled /></el-icon>
                  <div>将身份证图片拖到此处，或点击上传</div>
                </div>
              </el-upload>
              <el-button type="primary" :loading="loading" :disabled="!idCardFile" @click="doRecognize('idCard')" style="margin-top: 12px">
                开始识别
              </el-button>
            </div>
            <div class="result-area" v-if="idCardResult">
              <el-descriptions title="识别结果" :column="1" border>
                <template v-if="idCardSide === 'front'">
                  <el-descriptions-item label="姓名">{{ idCardResult.name }}</el-descriptions-item>
                  <el-descriptions-item label="性别">{{ idCardResult.gender }}</el-descriptions-item>
                  <el-descriptions-item label="民族">{{ idCardResult.nation }}</el-descriptions-item>
                  <el-descriptions-item label="出生日期">{{ idCardResult.birthday }}</el-descriptions-item>
                  <el-descriptions-item label="住址">{{ idCardResult.address }}</el-descriptions-item>
                  <el-descriptions-item label="身份证号">{{ idCardResult.idNumber }}</el-descriptions-item>
                </template>
                <template v-else>
                  <el-descriptions-item label="签发机关">{{ idCardResult.authority }}</el-descriptions-item>
                  <el-descriptions-item label="有效期起始">{{ idCardResult.validDateStart }}</el-descriptions-item>
                  <el-descriptions-item label="有效期截止">{{ idCardResult.validDateEnd }}</el-descriptions-item>
                </template>
                <el-descriptions-item label="识别供应商">{{ idCardResult.provider }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
        </el-tab-pane>

        <!-- 发票识别 -->
        <el-tab-pane label="发票" name="invoice">
          <div class="ocr-content">
            <div class="upload-area">
              <el-upload
                class="upload-box"
                drag
                :auto-upload="false"
                :show-file-list="false"
                accept="image/*,.pdf"
                :on-change="(file) => handleFileChange(file, 'invoice')"
              >
                <img v-if="invoicePreview && !invoiceIsPdf" :src="invoicePreview" class="preview-img" />
                <div v-else-if="invoiceIsPdf" class="preview-pdf">
                  <el-icon size="40"><Document /></el-icon>
                  <div>{{ invoiceFileName }}</div>
                </div>
                <div v-else class="upload-placeholder">
                  <el-icon size="40"><UploadFilled /></el-icon>
                  <div>将发票图片或PDF拖到此处，或点击上传</div>
                </div>
              </el-upload>
              <el-button type="primary" :loading="loading" :disabled="!invoiceFile" @click="doRecognize('invoice')" style="margin-top: 12px">
                开始识别
              </el-button>
            </div>
            <div class="result-area" v-if="invoiceResult">
              <el-descriptions title="识别结果" :column="1" border>
                <el-descriptions-item label="发票类型">{{ invoiceResult.invoiceType }}</el-descriptions-item>
                <el-descriptions-item label="发票代码">{{ invoiceResult.invoiceCode }}</el-descriptions-item>
                <el-descriptions-item label="发票号码">{{ invoiceResult.invoiceNumber }}</el-descriptions-item>
                <el-descriptions-item label="开票日期">{{ invoiceResult.invoiceDate }}</el-descriptions-item>
                <el-descriptions-item label="校验码">{{ invoiceResult.checkCode }}</el-descriptions-item>
                <el-descriptions-item label="购买方">{{ invoiceResult.buyerName }}</el-descriptions-item>
                <el-descriptions-item label="购买方税号">{{ invoiceResult.buyerTaxId }}</el-descriptions-item>
                <el-descriptions-item label="销售方">{{ invoiceResult.sellerName }}</el-descriptions-item>
                <el-descriptions-item label="销售方税号">{{ invoiceResult.sellerTaxId }}</el-descriptions-item>
                <el-descriptions-item label="金额（不含税）">{{ invoiceResult.totalAmount }}</el-descriptions-item>
                <el-descriptions-item label="税额">{{ invoiceResult.totalTax }}</el-descriptions-item>
                <el-descriptions-item label="价税合计">{{ invoiceResult.amountInFigures }}</el-descriptions-item>
                <el-descriptions-item label="识别供应商">{{ invoiceResult.provider }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
        </el-tab-pane>

        <!-- 银行卡识别 -->
        <el-tab-pane label="银行卡" name="bankCard">
          <div class="ocr-content">
            <div class="upload-area">
              <el-upload
                class="upload-box"
                drag
                :auto-upload="false"
                :show-file-list="false"
                accept="image/*"
                :on-change="(file) => handleFileChange(file, 'bankCard')"
              >
                <img v-if="bankCardPreview" :src="bankCardPreview" class="preview-img" />
                <div v-else class="upload-placeholder">
                  <el-icon size="40"><UploadFilled /></el-icon>
                  <div>将银行卡图片拖到此处，或点击上传</div>
                </div>
              </el-upload>
              <el-button type="primary" :loading="loading" :disabled="!bankCardFile" @click="doRecognize('bankCard')" style="margin-top: 12px">
                开始识别
              </el-button>
            </div>
            <div class="result-area" v-if="bankCardResult">
              <el-descriptions title="识别结果" :column="1" border>
                <el-descriptions-item label="卡号">{{ bankCardResult.cardNumber }}</el-descriptions-item>
                <el-descriptions-item label="银行">{{ bankCardResult.bankName }}</el-descriptions-item>
                <el-descriptions-item label="卡类型">{{ bankCardResult.cardType }}</el-descriptions-item>
                <el-descriptions-item label="持卡人" v-if="bankCardResult.holderName">{{ bankCardResult.holderName }}</el-descriptions-item>
                <el-descriptions-item label="有效期" v-if="bankCardResult.validDate">{{ bankCardResult.validDate }}</el-descriptions-item>
                <el-descriptions-item label="识别供应商">{{ bankCardResult.provider }}</el-descriptions-item>
              </el-descriptions>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { UploadFilled, Document } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { recognizeIdCard, recognizeInvoice, recognizeBankCard, getOcrProviders } from '@/api/ocr'

const activeTab = ref('idCard')
const selectedProvider = ref('')
const providers = ref([])
const loading = ref(false)

// 身份证
const idCardSide = ref('front')
const idCardFile = ref(null)
const idCardPreview = ref('')
const idCardResult = ref(null)

// 发票
const invoiceFile = ref(null)
const invoicePreview = ref('')
const invoiceIsPdf = ref(false)
const invoiceFileName = ref('')
const invoiceResult = ref(null)

// 银行卡
const bankCardFile = ref(null)
const bankCardPreview = ref('')
const bankCardResult = ref(null)

onMounted(async () => {
  try {
    const res = await getOcrProviders()
    if (res.code === 200) {
      providers.value = res.data || []
    }
  } catch (e) {
    // 供应商列表加载失败不影响使用
  }
})

function handleFileChange(uploadFile, type) {
  const file = uploadFile.raw
  const isPdf = file.type === 'application/pdf'
  const isImage = file.type.startsWith('image/')

  // 发票支持图片和PDF，其他场景只支持图片
  if (type === 'invoice') {
    if (!isImage && !isPdf) {
      ElMessage.warning('请上传图片或PDF文件')
      return
    }
  } else {
    if (!isImage) {
      ElMessage.warning('请上传图片文件')
      return
    }
  }
  if (file.size > 10 * 1024 * 1024) {
    ElMessage.warning('文件大小不能超过 10MB')
    return
  }

  const previewUrl = isImage ? URL.createObjectURL(file) : ''

  if (type === 'idCard') {
    idCardFile.value = file
    idCardPreview.value = previewUrl
    idCardResult.value = null
  } else if (type === 'invoice') {
    invoiceFile.value = file
    invoicePreview.value = previewUrl
    invoiceIsPdf.value = isPdf
    invoiceFileName.value = file.name
    invoiceResult.value = null
  } else if (type === 'bankCard') {
    bankCardFile.value = file
    bankCardPreview.value = previewUrl
    bankCardResult.value = null
  }
}

async function doRecognize(type) {
  loading.value = true
  try {
    let res
    if (type === 'idCard') {
      res = await recognizeIdCard(idCardFile.value, idCardSide.value, selectedProvider.value)
      idCardResult.value = res.data
      ElMessage.success('身份证识别成功')
    } else if (type === 'invoice') {
      res = await recognizeInvoice(invoiceFile.value, selectedProvider.value)
      invoiceResult.value = res.data
      ElMessage.success('发票识别成功')
    } else if (type === 'bankCard') {
      res = await recognizeBankCard(bankCardFile.value, selectedProvider.value)
      bankCardResult.value = res.data
      ElMessage.success('银行卡识别成功')
    }
  } catch (e) {
    ElMessage.error('识别请求失败，请稍后重试')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.ocr-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ocr-content {
  display: flex;
  gap: 24px;
  padding: 16px 0;
}

.upload-area {
  flex: 0 0 400px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.upload-box {
  width: 100%;
}

.upload-box :deep(.el-upload-dragger) {
  width: 100%;
  height: 240px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.upload-placeholder {
  text-align: center;
  color: #909399;
}

.preview-img {
  max-width: 100%;
  max-height: 220px;
  object-fit: contain;
}

.result-area {
  flex: 1;
  min-width: 0;
}

@media (max-width: 900px) {
  .ocr-content {
    flex-direction: column;
  }
  .upload-area {
    flex: none;
  }
}
</style>
