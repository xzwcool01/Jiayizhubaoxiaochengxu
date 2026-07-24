<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getReviewList, updateReview, deleteReview, createExpertPost, type AdminReviewVO } from '@/api/review'

const reviews = ref<AdminReviewVO[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const searchProductName = ref('')
const searchNickname = ref('')
const searchRating = ref<number | undefined>()
const editVisible = ref(false)
const editing = ref<AdminReviewVO | null>(null)
const expertVisible = ref(false)
const expertForm = ref({ content: '', nickname: '管理员', tag: '', likes: 0, sortOrder: 0 })
const expertFiles = ref<File[]>([])
const expertPreviews = ref<string[]>([])

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
    showOnExpert: editing.value.showOnExpert,
    expertSortOrder: editing.value.expertSortOrder,
    expertTag: editing.value.expertTag,
    expertLikes: editing.value.expertLikes,
    expertNickname: editing.value.expertNickname,
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

function openExpert() {
  expertForm.value = { content: '', nickname: '管理员', tag: '', likes: 0, sortOrder: 0 }
  expertFiles.value = []
  expertPreviews.value = []
  expertVisible.value = true
}

function onExpertFilesChange(e: Event) {
  const target = e.target as HTMLInputElement
  if (target.files) {
    expertFiles.value = Array.from(target.files)
    expertPreviews.value = expertFiles.value.map(f => URL.createObjectURL(f))
  }
}

function removeExpertPreview(i: number) {
  expertFiles.value.splice(i, 1)
  expertPreviews.value.splice(i, 1)
}

async function handleCreateExpert() {
  const fd = new FormData()
  fd.append('content', expertForm.value.content)
  fd.append('nickname', expertForm.value.nickname)
  fd.append('tag', expertForm.value.tag)
  fd.append('likes', String(expertForm.value.likes))
  fd.append('sortOrder', String(expertForm.value.sortOrder))
  expertFiles.value.forEach(f => fd.append('files', f))
  const res = await createExpertPost(fd)
  if (res.code === 200) {
    ElMessage.success('新增晒单成功')
    expertVisible.value = false
    fetchData()
  } else {
    ElMessage.error(res.message)
  }
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
    <div class="page-header">
      <h2>评价管理</h2>
      <el-button type="primary" @click="openExpert">+ 新增晒单</el-button>
    </div>
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
        <el-table-column prop="productName" label="商品" min-width="130" show-overflow-tooltip />
        <el-table-column prop="nickname" label="用户" width="90" show-overflow-tooltip />
        <el-table-column label="评分" width="80">
          <template #default="{ row }"><span style="color:#E9C349">{{ renderStars(row.rating) }}</span></template>
        </el-table-column>
        <el-table-column prop="content" label="内容" min-width="160" show-overflow-tooltip />
        <el-table-column label="图片" width="65">
          <template #default="{ row }">
            <img v-if="row.images?.length" :src="row.images[0]" style="width:36px;height:36px;border-radius:4px;object-fit:cover" />
            <span v-else style="color:#999">-</span>
          </template>
        </el-table-column>
        <el-table-column label="晒单" width="55">
          <template #default="{ row }"><el-tag v-if="row.showOnExpert" type="success" size="small">是</el-tag><span v-else style="color:#999">否</span></template>
        </el-table-column>
        <el-table-column label="晒单排序" width="80">
          <template #default="{ row }">{{ row.showOnExpert ? row.expertSortOrder : '-' }}</template>
        </el-table-column>
        <el-table-column label="晒单标签" width="100" show-overflow-tooltip>
          <template #default="{ row }">{{ row.showOnExpert && row.expertTag ? row.expertTag : '-' }}</template>
        </el-table-column>
        <el-table-column label="置顶" width="55">
          <template #default="{ row }"><el-tag v-if="row.isTop" type="warning" size="small">是</el-tag><span v-else style="color:#999">否</span></template>
        </el-table-column>
        <el-table-column label="状态" width="60">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '显示' : '隐藏' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="时间" width="130">
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
        <el-divider>达人晒单</el-divider>
        <el-form-item label="展示">
          <el-switch v-model="editing.showOnExpert" :active-value="1" :inactive-value="0" active-text="在达人晒单展示" inactive-text="不展示" />
        </el-form-item>
        <el-row :gutter="20" v-if="editing.showOnExpert">
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="editing.expertSortOrder" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="点赞数">
              <el-input-number v-model="editing.expertLikes" :min="0" style="width:100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="晒单用户" v-if="editing.showOnExpert">
          <el-input v-model="editing.expertNickname" placeholder="用户昵称" />
        </el-form-item>
        <el-form-item label="晒单标签" v-if="editing.showOnExpert">
          <el-input v-model="editing.expertTag" placeholder="#嘉艺瞬间" />
        </el-form-item>
        <el-divider>基础设置</el-divider>
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

    <el-dialog v-model="expertVisible" title="新增晒单" width="500px">
      <el-form :model="expertForm" label-width="100px">
        <el-form-item label="晒单图">
          <input type="file" accept="image/*" multiple @change="onExpertFilesChange" />
          <div v-if="expertPreviews.length" style="display:flex;gap:8px;flex-wrap:wrap;margin-top:8px">
            <div v-for="(url, i) in expertPreviews" :key="i" style="position:relative">
              <img :src="url" style="width:100px;height:100px;object-fit:cover;border-radius:8px" />
              <el-button size="small" type="danger" circle style="position:absolute;top:-8px;right:-8px;width:20px;height:20px;min-height:20px;padding:0" @click="removeExpertPreview(i)">×</el-button>
            </div>
          </div>
        </el-form-item>
        <el-form-item label="用户昵称">
          <el-input v-model="expertForm.nickname" placeholder="管理员" />
        </el-form-item>
        <el-form-item label="文案">
          <el-input v-model="expertForm.content" type="textarea" :rows="3" placeholder="晒单文案" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="expertForm.tag" placeholder="#嘉艺瞬间" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="点赞数">
              <el-input-number v-model="expertForm.likes" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序">
              <el-input-number v-model="expertForm.sortOrder" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="expertVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCreateExpert">添加</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
.page-header h2 { margin:0; font-size:20px; }
</style>
