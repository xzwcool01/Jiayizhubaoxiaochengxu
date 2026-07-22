<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReviewList, updateReview, deleteReview, type AdminReviewVO } from '@/api/review'

const reviews = ref<AdminReviewVO[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const searchProductName = ref('')
const searchNickname = ref('')
const searchRating = ref<number | undefined>()
const editVisible = ref(false)
const editing = ref<AdminReviewVO | null>(null)

async function fetchData() {
  const res = await getReviewList({
    page: page.value,
    size: size.value,
    productName: searchProductName.value || undefined,
    nickname: searchNickname.value || undefined,
    rating: searchRating.value
  })
  if (res.code === 200) {
    reviews.value = res.data.records || []
    total.value = res.data.total
  }
}

function handleSearch() {
  page.value = 1
  fetchData()
}

function openEdit(row: AdminReviewVO) {
  editing.value = { ...row, images: [...(row.images || [])] }
  editVisible.value = true
}

async function handleSave() {
  if (!editing.value) return
  await updateReview(editing.value.id, {
    rating: editing.value.rating,
    content: editing.value.content,
    isAnonymous: editing.value.isAnonymous,
    isTop: editing.value.isTop,
    status: editing.value.status
  })
  ElMessage.success('保存成功')
  editVisible.value = false
  fetchData()
}

async function handleDelete(row: AdminReviewVO) {
  await ElMessageBox.confirm('确定删除该评价？', '提示')
  await deleteReview(row.id)
  ElMessage.success('已删除')
  fetchData()
}

function renderStars(rating: number) {
  return '★'.repeat(rating) + '☆'.repeat(5 - rating)
}

function formatPrice(v: number | undefined | null): string {
  return v != null ? '¥' + v.toLocaleString() : '-'
}

function previewImg(url: string) {
  window.open(url, '_blank')
}

onMounted(fetchData)
</script>

<template>
  <div>
    <div class="page-header"><h2>评价管理</h2></div>
    <el-card shadow="never" style="margin-bottom:16px">
      <el-form :inline="true" size="small">
        <el-form-item label="商品名"><el-input v-model="searchProductName" placeholder="模糊搜索" clearable /></el-form-item>
        <el-form-item label="用户昵称"><el-input v-model="searchNickname" placeholder="模糊搜索" clearable /></el-form-item>
        <el-form-item label="评分">
          <el-select v-model="searchRating" placeholder="全部" clearable style="width:120px">
            <el-option v-for="i in 5" :key="i" :value="i" :label="i + '星'" />
          </el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="handleSearch">搜索</el-button><el-button @click="searchProductName='';searchNickname='';searchRating=undefined;handleSearch()">重置</el-button></el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <el-table :data="reviews" stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="productName" label="商品" min-width="140" show-overflow-tooltip />
        <el-table-column prop="nickname" label="用户" width="100" show-overflow-tooltip />
        <el-table-column label="评分" width="100">
          <template #default="{ row }"><span style="color:#E9C349">{{ renderStars(row.rating) }}</span></template>
        </el-table-column>
        <el-table-column prop="content" label="评价内容" min-width="180" show-overflow-tooltip />
        <el-table-column label="图片" width="80">
          <template #default="{ row }">
            <img v-if="row.images?.length" :src="row.images[0]" style="width:36px;height:36px;border-radius:4px;object-fit:cover" />
            <span v-else style="color:#999">无</span>
          </template>
        </el-table-column>
        <el-table-column label="订单金额" width="100">
          <template #default="{ row }">{{ formatPrice(row.totalAmount) }}</template>
        </el-table-column>
        <el-table-column label="实付金额" width="100">
          <template #default="{ row }"><span style="color:#775836;font-weight:bold">{{ formatPrice(row.payAmount) }}</span></template>
        </el-table-column>
        <el-table-column label="置顶" width="60">
          <template #default="{ row }"><el-tag v-if="row.isTop" type="warning" size="small">是</el-tag><span v-else style="color:#999">否</span></template>
        </el-table-column>
        <el-table-column label="匿名" width="55">
          <template #default="{ row }">{{ row.isAnonymous ? '是' : '否' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="65">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '显示' : '隐藏' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="时间" width="140">
          <template #default="{ row }">{{ row.createTime?.replace('T', ' ').substring(0, 16) || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-if="total > size" v-model:current-page="page" v-model:page-size="size" :total="total" layout="prev,pager,next" style="margin-top:16px;justify-content:center" @current-change="fetchData" />
    </el-card>

    <el-dialog v-model="editVisible" title="编辑评价" width="600px">
      <el-form v-if="editing" label-width="100px">
        <el-form-item label="商品">{{ editing.productName }}</el-form-item>
        <el-form-item label="用户">{{ editing.nickname }}</el-form-item>
        <el-form-item label="订单金额">{{ formatPrice(editing.totalAmount) }}</el-form-item>
        <el-form-item label="实付金额">{{ formatPrice(editing.payAmount) }}</el-form-item>
        <el-form-item label="评分">
          <el-rate v-model="editing.rating" :max="5" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="editing.content" type="textarea" :rows="3" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="图片" v-if="editing.images?.length">
          <div style="display:flex;gap:8px;flex-wrap:wrap">
            <img v-for="(img, i) in editing.images" :key="i" :src="img" style="width:80px;height:80px;border-radius:6px;object-fit:cover;cursor:pointer" @click="previewImg(img)" />
          </div>
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch v-model="editing.isTop" :active-value="1" :inactive-value="0" active-text="是（商品详情页优先展示）" inactive-text="否" />
        </el-form-item>
        <el-form-item label="匿名">
          <el-switch v-model="editing.isAnonymous" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="editing.status" :active-value="1" :inactive-value="0" active-text="显示" inactive-text="隐藏" />
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="editVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
.page-header h2 { margin:0; font-size:20px; }
</style>
