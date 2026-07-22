<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCouponList, createCoupon, updateCoupon, issueCoupon, searchUsers, type AdminCouponVO, type UmsUser } from '@/api/coupon'
import { getProductList, type PmsProduct } from '@/api/product'

const list = ref<AdminCouponVO[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const loading = ref(false)
const editVisible = ref(false)
const editing = ref<AdminCouponVO>({ name: '', type: 0, value: 0, minAmount: 0, maxAmount: 0, productIds: [] })
const isNew = ref(true)
const issueVisible = ref(false)
const issueCouponId = ref(0)
const searchKeyword = ref('')
const candidates = ref<UmsUser[]>([])
const selectedUserIds = ref<number[]>([])
const productOptions = ref<PmsProduct[]>([])

async function fetchData() {
  loading.value = true
  try {
    const res = await getCouponList({ page: page.value, size: size.value })
    if (res.code === 200) { list.value = res.data.records; total.value = res.data.total }
  } finally { loading.value = false }
}

function openAdd() {
  isNew.value = true
  editing.value = { name: '', type: 0, value: 0, minAmount: 0, maxAmount: 0 }
  editVisible.value = true
}

function openEdit(row: AdminCouponVO) {
  isNew.value = false
  editing.value = { ...row }
  editVisible.value = true
}

async function handleSave() {
  const data = { ...editing.value }
  if (isNew.value) await createCoupon(data)
  else if (editing.value.id) await updateCoupon(editing.value.id, data)
  ElMessage.success(isNew.value ? '创建成功' : '更新成功')
  editVisible.value = false
  fetchData()
}

function openIssue(row: AdminCouponVO) {
  issueCouponId.value = row.id || 0
  candidates.value = []
  searchKeyword.value = ''
  selectedUserIds.value = []
  issueVisible.value = true
}

async function searchUser() {
  if (!searchKeyword.value.trim()) return
  const res = await searchUsers(searchKeyword.value.trim())
  if (res.code === 200) candidates.value = res.data || []
}

function toggleUser(id: number) {
  const idx = selectedUserIds.value.indexOf(id)
  if (idx >= 0) selectedUserIds.value.splice(idx, 1)
  else selectedUserIds.value.push(id)
}

async function handleIssue() {
  if (!selectedUserIds.value.length) { ElMessage.warning('请先搜索并勾选用户，或使用"发放给全部用户"'); return }
  const res = await issueCoupon({ couponId: issueCouponId.value, userIds: selectedUserIds.value })
  ElMessage.success(`发放成功，共发放 ${res.data?.issuedCount || 0} 张`)
  issueVisible.value = false
  fetchData()
}

async function handleIssueAll() {
  await ElMessageBox.confirm('确定要发放给所有用户？此操作不可撤销', '确认', { confirmButtonText: '确认发放', cancelButtonText: '取消', type: 'warning' })
  const res = await issueCoupon({ couponId: issueCouponId.value, all: true })
  ElMessage.success(`已发放给全部用户，共发放 ${res.data?.issuedCount || 0} 张`)
  issueVisible.value = false
  fetchData()
}

function getRemain(row: AdminCouponVO) {
  if (!row.totalCount) return Infinity
  return row.totalCount - (row.issuedCount || 0)
}

function formatTime(t?: string) { return t ? t.replace('T', ' ').substring(0, 16) : '-' }

async function searchProducts(keyword: string) {
  const res = await getProductList({ page: 1, size: 20, productType: 0, keyword })
  if (res.code === 200) productOptions.value = res.data.records || []
}

onMounted(fetchData)
</script>

<template>
  <div>
    <div class="page-header"><h2>优惠券管理</h2><el-button type="primary" @click="openAdd">新建优惠券</el-button></div>

    <el-card shadow="never">
      <el-table :data="list" v-loading="loading" stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="名称" min-width="140" />
        <el-table-column label="类型" width="70">
          <template #default="{ row }">{{ row.type === 0 ? '满减' : '折扣' }}</template>
        </el-table-column>
        <el-table-column prop="value" label="面值" width="100">
          <template #default="{ row }">{{ row.type === 0 ? '¥' : '' }}{{ row.value }}{{ row.type === 1 ? '%' : '' }}</template>
        </el-table-column>
        <el-table-column prop="minAmount" label="最低门槛" width="100"><template #default="{ row }">¥{{ row.minAmount }}</template></el-table-column>
        <el-table-column prop="maxAmount" label="限额" width="100"><template #default="{ row }">{{ row.maxAmount > 0 ? '¥' + row.maxAmount : '不限' }}</template></el-table-column>
        <el-table-column label="有效期" min-width="220">
          <template #default="{ row }">{{ formatTime(row.startTime) }} ~ {{ formatTime(row.endTime) }}</template>
        </el-table-column>
        <el-table-column label="已发放" width="100">
          <template #default="{ row }">{{ row.issuedCount || 0 }} / {{ row.totalCount || '不限' }}</template>
        </el-table-column>
        <el-table-column label="已使用" width="80">
          <template #default="{ row }">{{ row.usedCount || 0 }}</template>
        </el-table-column>
        <el-table-column label="剩余" width="80">
          <template #default="{ row }">
            <span :style="{ color: getRemain(row) <= 0 ? '#ba1a1a' : '#67c23a' }">{{ getRemain(row) <= 0 ? '已发完' : getRemain(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button size="small" :type="getRemain(row) <= 0 ? 'info' : 'success'" link :disabled="getRemain(row) <= 0" @click="openIssue(row)">{{ getRemain(row) <= 0 ? '已发完' : '发券' }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, sizes, prev, pager, next" :page-sizes="[10, 20, 50]" style="margin-top:16px; justify-content:flex-end" @current-change="fetchData" @size-change="fetchData" />
    </el-card>

    <el-dialog v-model="editVisible" :title="isNew ? '新建优惠券' : '编辑优惠券'" width="600px">
      <el-form label-width="100px">
        <el-form-item label="名称"><el-input v-model="editing.name" /></el-form-item>
        <el-form-item label="类型">
          <el-select v-model="editing.type"><el-option :value="0" label="满减" /><el-option :value="1" label="折扣" /></el-select>
        </el-form-item>
        <el-form-item label="面值"><el-input-number v-model="editing.value" :min="0" :precision="2" style="width:200px" /></el-form-item>
        <el-form-item label="最低消费"><el-input-number v-model="editing.minAmount" :min="0" :precision="2" style="width:200px" /></el-form-item>
        <el-form-item label="最大抵扣"><el-input-number v-model="editing.maxAmount" :min="0" :precision="2" style="width:200px" /><span style="color:#999;margin-left:8px">0 表示不限</span></el-form-item>
        <el-form-item label="开始时间"><el-date-picker v-model="editing.startTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="结束时间"><el-date-picker v-model="editing.endTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="总发行量"><el-input-number v-model="editing.totalCount" :min="0" style="width:200px" /></el-form-item>
        <el-form-item label="每人限领"><el-input-number v-model="editing.perUserLimit" :min="1" style="width:200px" /></el-form-item>
        <el-form-item label="关联商品">
          <el-select v-model="editing.productIds" multiple filterable remote :remote-method="searchProducts" placeholder="搜索并选择商品" style="width:100%">
            <el-option v-for="p in productOptions" :key="p.id" :label="p.name" :value="p.id" />
          </el-select>
          <span style="color:#999;font-size:12px;margin-top:4px;display:block">不选 = 全部商品可用</span>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="editVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="issueVisible" title="发放优惠券" width="500px">
      <p style="color:#999;margin-bottom:12px">优惠券ID: {{ issueCouponId }} | 可发放: {{ list.find(r => r.id === issueCouponId) ? getRemain(list.find(r => r.id === issueCouponId)!) : '-' }} 张</p>
      <el-form :inline="true" @submit.prevent="searchUser">
        <el-form-item><el-input v-model="searchKeyword" placeholder="搜索用户昵称/手机号" clearable style="width:240px" /></el-form-item>
        <el-form-item><el-button type="primary" @click="searchUser">搜索</el-button></el-form-item>
      </el-form>
      <el-checkbox-group v-model="selectedUserIds">
        <el-checkbox v-for="u in candidates" :key="u.id" :label="u.id" style="display:flex;margin-bottom:8px">
          {{ u.nickname }}（{{ u.phone || '无手机' }}）
        </el-checkbox>
      </el-checkbox-group>
      <p v-if="!candidates.length" style="color:#ccc;text-align:center">请搜索用户</p>
      <template #footer>
        <el-button @click="issueVisible = false">取消</el-button>
        <el-button type="danger" @click="handleIssueAll" style="margin-right:auto">发放给全部用户</el-button>
        <el-button type="primary" @click="handleIssue">发放给选中用户</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
.page-header h2 { margin:0; font-size:20px; }
</style>