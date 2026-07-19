<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getProductList, getProduct, createProduct, updateProduct, deleteProduct,
  getCategories, PmsProduct, ProductDTO, Category
} from '@/api/product'
import request from '@/utils/axios'

const productTypeOptions = [
  { value: 0, label: '普通商品' },
  { value: 1, label: '限时抢购' },
  { value: 2, label: '限时活动' },
  { value: 3, label: '限时拍卖' },
  { value: 4, label: '积分商城' },
  { value: 5, label: '会员专享' },
  { value: 6, label: '新品上市' }
]
const typeMap: Record<number, string> = Object.fromEntries(productTypeOptions.map(o => [o.value, o.label]))
const typeColors: Record<number, string> = {
  0: '', 1: 'danger', 2: 'warning', 3: 'purple',
  4: 'success', 5: '', 6: 'primary'
}

const list = ref<PmsProduct[]>([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 20, productType: undefined as number | undefined, categoryId: undefined as number | undefined, keyword: '', status: undefined as number | undefined })
const categories = ref<Category[]>([])

const dialogVisible = ref(false)
const dialogTitle = ref('新增商品')
const editingId = ref<number | null>(null)
const formRef = ref<any>(null)
const form = reactive<ProductDTO>({
  categoryId: 0, productType: 0, name: '', subtitle: '', images: [],
  description: '', descriptionText: '', specs: '', price: 0, originalPrice: undefined, pointsPrice: 0,
  stock: 0, flashStock: 0, saleStart: undefined, saleEnd: undefined,
  memberLevel: 0, isNew: 0, isRecommend: 0, sortOrder: 0, weight: 0, status: 1
})
const imageUrlList = ref<string[]>([])
const uploadLoading = ref(false)
const specsList = ref<{ label: string; value: string }[]>([])

const showPrice = computed(() => form.productType !== 4)
const showPoints = computed(() => form.productType === 4)
const showTimeRange = computed(() => [1, 2, 3].includes(form.productType))
const showFlashStock = computed(() => form.productType === 1)
const showMemberLevel = computed(() => form.productType === 5)
const showOriginalPrice = computed(() => [0, 1, 2, 5, 6].includes(form.productType))

onMounted(async () => {
  await loadCategories()
  await fetchData()
})

async function loadCategories() {
  const res = await getCategories()
  if (res.code === 200) categories.value = res.data
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getProductList(query)
    if (res.code === 200) {
      list.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function handleSizeChange(size: number) {
  query.size = size
  query.page = 1
  fetchData()
}

function handleCurrentChange(page: number) {
  query.page = page
  fetchData()
}

function resetQuery() {
  query.productType = undefined
  query.categoryId = undefined
  query.keyword = ''
  query.status = undefined
  query.page = 1
  fetchData()
}

function openCreate() {
  editingId.value = null
  dialogTitle.value = '新增商品'
  Object.assign(form, {
    categoryId: categories.value[0]?.id || 0, productType: 0, name: '', subtitle: '',
    images: [], description: '', descriptionText: '', specs: '', price: 0,
    originalPrice: undefined, pointsPrice: 0,
    stock: 0, flashStock: 0, saleStart: undefined, saleEnd: undefined,
    memberLevel: 0, isNew: 0, isRecommend: 0, sortOrder: 0, weight: 0, status: 1
  })
  imageUrlList.value = []
  specsList.value = []
  dialogVisible.value = true
}

async function openEdit(id: number) {
  const res = await getProduct(id)
  if (res.code !== 200) { ElMessage.error('获取商品失败'); return }
  const p = res.data
  editingId.value = id
  dialogTitle.value = '编辑商品'
  let images: string[] = []
  if (p.images) {
    try { images = JSON.parse(p.images) } catch { images = [] }
  }
  let parsedSpecs: { label: string; value: string }[] = []
  if (p.specs) {
    try { const arr = JSON.parse(p.specs); if (Array.isArray(arr)) parsedSpecs = arr } catch {}
  }
  specsList.value = parsedSpecs.length ? parsedSpecs : []
  Object.assign(form, {
    categoryId: p.categoryId, productType: p.productType, name: p.name,
    subtitle: p.subtitle || '', images: images.length ? images : p.mainImage ? [p.mainImage] : [],
    description: p.description || '', descriptionText: p.descriptionText || '',
    specs: p.specs || (parsedSpecs.length ? JSON.stringify(parsedSpecs) : ''),
    price: p.price, originalPrice: p.originalPrice || undefined,
    pointsPrice: p.pointsPrice || 0, stock: p.stock || 0, flashStock: p.flashStock || 0,
    saleStart: p.saleStart || undefined, saleEnd: p.saleEnd || undefined,
    memberLevel: p.memberLevel || 0, isNew: p.isNew || 0, isRecommend: p.isRecommend || 0,
    sortOrder: p.sortOrder || 0, weight: p.weight ?? 0, status: p.status ?? 1
  })
  imageUrlList.value = images.length ? images : p.mainImage ? [p.mainImage] : []
  dialogVisible.value = true
}

async function handleDelete(id: number) {
  try {
    await ElMessageBox.confirm('确定删除该商品？', '提示', { type: 'warning' })
    const res = await deleteProduct(id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchData()
    }
  } catch {}
}

async function handleImageUpload(options: any) {
  uploadLoading.value = true
  try {
    const formData = new FormData()
    formData.append('file', options.file)
    formData.append('productType', String(form.productType))
    const res = await request.post<any, { code: number; data: string }>('/admin/product/upload-image', formData)
    if (res.code === 200) {
      imageUrlList.value.push(res.data)
      form.images = [...imageUrlList.value]
      ElMessage.success('上传成功')
    } else {
      ElMessage.error(res.message || '上传失败')
    }
  } catch (e: any) {
    ElMessage.error(e?.message || '上传失败')
  } finally {
    uploadLoading.value = false
  }
}

function removeImage(index: number) {
  imageUrlList.value.splice(index, 1)
  form.images = [...imageUrlList.value]
}

function addSpecRow() {
  specsList.value.push({ label: '', value: '' })
}
function removeSpecRow(index: number) {
  specsList.value.splice(index, 1)
}

async function handleSave() {
  if (!form.name) { ElMessage.warning('请输入商品名称'); return }
  if (!form.categoryId) { ElMessage.warning('请选择商品类目'); return }
  if (form.price == null || form.price <= 0) { ElMessage.warning('请输入有效价格'); return }
  if (showTimeRange.value && !form.saleStart) { ElMessage.warning('请设置开始时间'); return }
  if (showTimeRange.value && !form.saleEnd) { ElMessage.warning('请设置结束时间'); return }
  form.specs = specsList.value.filter(s => s.label || s.value).length ? JSON.stringify(specsList.value.filter(s => s.label || s.value)) : ''
  try {
    if (editingId.value) {
      const res = await updateProduct(editingId.value, { ...form, images: imageUrlList.value })
      if (res.code === 200) { ElMessage.success('更新成功') }
    } else {
      const res = await createProduct({ ...form, images: imageUrlList.value })
      if (res.code === 200) { ElMessage.success('创建成功') }
    }
    dialogVisible.value = false
    fetchData()
  } catch {}
}

async function toggleStatus(row: PmsProduct) {
  const newStatus = row.status === 1 ? 0 : 1
  const dto: ProductDTO = {
    categoryId: row.categoryId, productType: row.productType, name: row.name,
    price: row.price, status: newStatus
  }
  try {
    const res = await updateProduct(row.id, dto)
    if (res.code === 200) {
      row.status = newStatus
      ElMessage.success(newStatus === 1 ? '已上架' : '已下架')
    }
  } catch {}
}

function getMainImageUrl(row: PmsProduct): string {
  if (row.mainImage) return row.mainImage
  if (row.images) {
    try {
      const arr = JSON.parse(row.images)
      return Array.isArray(arr) && arr.length > 0 ? arr[0] : ''
    } catch { return '' }
  }
  return ''
}
</script>

<template>
  <div>
    <!-- Header -->
    <div style="display:flex; justify-content:space-between; align-items:center; margin-bottom:20px">
      <h2 style="margin:0; font-size:20px">商品管理</h2>
      <el-button type="primary" @click="openCreate">+ 新增商品</el-button>
    </div>

    <!-- Filters -->
    <el-card shadow="never" style="margin-bottom:16px">
      <el-form :inline="true" size="small" label-width="auto">
        <el-form-item label="商品类型">
          <el-select v-model="query.productType" placeholder="全部类型" clearable style="width:150px">
            <el-option v-for="o in productTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品类目">
          <el-select v-model="query.categoryId" placeholder="全部分类" clearable style="width:150px">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="上架状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width:120px">
            <el-option label="上架" :value="1" />
            <el-option label="下架" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item label="关键词">
          <el-input v-model="query.keyword" placeholder="名称/副标题" clearable style="width:180px" @keyup.enter="fetchData" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="query.page=1; fetchData()">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- Table -->
    <el-card shadow="never">
      <el-table :data="list" v-loading="loading" stripe size="small" style="width:100%">
        <el-table-column label="商品" min-width="280">
          <template #default="{ row }">
            <div style="display:flex; align-items:center; gap:12px">
              <el-image :src="getMainImageUrl(row)" style="width:60px;height:60px;border-radius:6px;object-fit:cover;background:#f5f5f5" fit="cover" />
              <div>
                <div style="font-weight:600; font-size:14px">{{ row.name }}</div>
                <div style="color:#999; font-size:12px; margin-top:4px">{{ row.subtitle || '' }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="类目" width="100" align="center" prop="categoryName" />
        <el-table-column label="类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="typeColors[row.productType] as any" size="small" effect="plain">
              {{ typeMap[row.productType] || '普通' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="价格" width="120" align="center">
          <template #default="{ row }">
            <div style="color:#C8A27A; font-weight:600">¥{{ row.price }}</div>
            <div v-if="row.originalPrice" style="color:#999; font-size:12px; text-decoration:line-through">¥{{ row.originalPrice }}</div>
          </template>
        </el-table-column>
        <el-table-column label="库存" width="80" align="center">
          <template #default="{ row }">{{ row.stock }}</template>
        </el-table-column>
        <el-table-column label="已售" width="80" align="center">
          <template #default="{ row }">{{ row.sales }}</template>
        </el-table-column>
        <el-table-column label="权重" width="70" align="center">
          <template #default="{ row }">{{ row.weight }}</template>
        </el-table-column>
        <el-table-column label="排序" width="70" align="center">
          <template #default="{ row }">{{ row.sortOrder }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch :model-value="row.status === 1" @click="toggleStatus(row)" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button size="small" link type="primary" @click="openEdit(row.id)">编辑</el-button>
            <el-button size="small" link type="danger" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="display:flex; justify-content:flex-end; margin-top:16px">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- Create/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="720px" destroy-on-close>
      <el-form ref="formRef" :model="form" label-width="100px" size="small">
        <el-form-item label="商品类型" required>
          <el-select v-model="form.productType" placeholder="选择类型" style="width:100%">
            <el-option v-for="o in productTypeOptions" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品类目" required>
          <el-select v-model="form.categoryId" placeholder="选择类目" style="width:100%">
            <el-option v-for="c in categories" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品名称" required>
          <el-input v-model="form.name" placeholder="输入商品名称" />
        </el-form-item>
        <el-form-item label="副标题">
          <el-input v-model="form.subtitle" placeholder="副标题（选填）" />
        </el-form-item>

        <!-- Price fields by type -->
        <el-form-item v-if="showOriginalPrice" label="划线原价">
          <el-input-number v-model="form.originalPrice" :min="0" :precision="2" style="width:200px" />
        </el-form-item>
        <el-form-item v-if="showPrice" label="售价" required>
          <el-input-number v-model="form.price" :min="0.01" :precision="2" style="width:200px" />
        </el-form-item>
        <el-form-item v-if="showPoints" label="积分价" required>
          <el-input-number v-model="form.pointsPrice" :min="1" style="width:200px" />
          <span style="margin-left:8px; color:#999">积分</span>
        </el-form-item>

        <!-- Stock -->
        <el-form-item label="库存">
          <el-input-number v-model="form.stock" :min="0" style="width:200px" />
        </el-form-item>
        <el-form-item v-if="showFlashStock" label="抢购库存">
          <el-input-number v-model="form.flashStock" :min="0" style="width:200px" />
        </el-form-item>

        <!-- Time range -->
        <el-form-item v-if="showTimeRange" label="开始时间" required>
          <el-date-picker v-model="form.saleStart" type="datetime" placeholder="选择开始时间" style="width:100%" />
        </el-form-item>
        <el-form-item v-if="showTimeRange" label="结束时间" required>
          <el-date-picker v-model="form.saleEnd" type="datetime" placeholder="选择结束时间" style="width:100%" />
        </el-form-item>

        <!-- Member level -->
        <el-form-item v-if="showMemberLevel" label="最低等级">
          <el-input-number v-model="form.memberLevel" :min="1" :max="5" style="width:200px" />
          <span style="margin-left:8px; color:#999">级及以上可见</span>
        </el-form-item>

        <!-- Images -->
        <el-form-item label="商品图片">
          <div style="display:flex; flex-wrap:wrap; gap:8px; align-items:center">
            <div v-for="(url, i) in imageUrlList" :key="i" style="position:relative; width:80px; height:80px">
              <el-image :src="url" style="width:80px;height:80px;border-radius:6px;object-fit:cover" fit="cover" />
              <div style="position:absolute; top:-6px;right:-6px; cursor:pointer; background:#fff; border-radius:50%; line-height:1" @click="removeImage(i)">
                <el-tag type="danger" size="small" style="cursor:pointer">×</el-tag>
              </div>
            </div>
            <el-upload
              :show-file-list="false"
              :http-request="handleImageUpload"
              accept="image/*"
            >
              <el-button :loading="uploadLoading" size="small">上传图片</el-button>
            </el-upload>
          </div>
        </el-form-item>

        <!-- Description -->
        <el-form-item label="商品介绍">
          <el-input v-model="form.descriptionText" type="textarea" :rows="4" placeholder="输入商品介绍文字，无需写HTML标签" />
        </el-form-item>
        <el-form-item label="规格参数">
          <div style="width:100%">
            <div v-for="(s, i) in specsList" :key="i" style="display:flex; gap:8px; margin-bottom:8px; align-items:center">
              <el-input v-model="s.label" placeholder="参数名称" style="width:160px" />
              <el-input v-model="s.value" placeholder="参数内容" style="width:240px" />
              <el-button size="small" type="danger" link @click="removeSpecRow(i)">删除</el-button>
            </div>
            <el-button size="small" @click="addSpecRow">+ 添加参数</el-button>
          </div>
        </el-form-item>

        <!-- Flags -->
        <el-form-item label="标签设置">
          <el-checkbox v-model="form.isNew" :true-value="1" :false-value="0">新品</el-checkbox>
          <el-checkbox v-model="form.isRecommend" :true-value="1" :false-value="0" style="margin-left:16px">推荐</el-checkbox>
        </el-form-item>

        <!-- Sort & Status -->
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" style="width:200px" />
        </el-form-item>
        <el-form-item label="权重">
          <el-input-number v-model="form.weight" :min="0" :max="9999" style="width:200px" />
          <span style="margin-left:8px; color:#999">越大越靠前</span>
        </el-form-item>
        <el-form-item label="上架">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave" :loading="uploadLoading">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
