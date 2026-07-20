<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getCartList, type AdminCartVO } from '@/api/cart'

const list = ref<AdminCartVO[]>([])
const total = ref(0)
const loading = ref(false)
const query = reactive({
  page: 1, size: 20,
  userId: undefined as number | undefined,
  productId: undefined as number | undefined,
  userName: '',
  productName: ''
})

async function fetchData() {
  loading.value = true
  try {
    const params: Record<string, any> = { page: query.page, size: query.size }
    if (query.userId) params.userId = query.userId
    if (query.productId) params.productId = query.productId
    if (query.userName) params.userName = query.userName
    if (query.productName) params.productName = query.productName
    const res = await getCartList(params)
    if (res.code === 200) {
      list.value = res.data.records
      total.value = res.data.total
    }
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  query.page = 1
  fetchData()
}

function resetQuery() {
  query.userId = undefined
  query.productId = undefined
  query.userName = ''
  query.productName = ''
  query.page = 1
  fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div>
    <div class="page-header"><h2>购物车管理</h2></div>
    <el-card shadow="never" style="margin-bottom:16px">
      <el-form :inline="true" @keyup.enter="handleQuery">
        <el-form-item label="用户ID">
          <el-input-number v-model="query.userId" :min="0" placeholder="按ID" clearable style="width:140px" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="query.userName" placeholder="按名称模糊搜索" clearable style="width:160px" />
        </el-form-item>
        <el-form-item label="商品ID">
          <el-input-number v-model="query.productId" :min="0" placeholder="按ID" clearable style="width:140px" />
        </el-form-item>
        <el-form-item label="商品名">
          <el-input v-model="query.productName" placeholder="按名称模糊搜索" clearable style="width:160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    <el-card shadow="never">
      <el-table :data="list" stripe style="width:100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="userName" label="用户名" min-width="120" />
        <el-table-column prop="productId" label="商品ID" width="80" />
        <el-table-column prop="productName" label="商品名称" min-width="200" />
        <el-table-column prop="quantity" label="数量" width="70" />
        <el-table-column label="选中" width="70">
          <template #default="{ row }">{{ row.selected ? '✓' : '✗' }}</template>
        </el-table-column>
        <el-table-column label="添加时间" width="180">
          <template #default="{ row }">{{ row.createTime?.replace('T', ' ') }}</template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="query.page"
          v-model:page-size="query.size"
          :total="total"
          layout="total, prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
.page-header h2 { margin:0; font-size:20px; }
.pagination-wrap { margin-top:16px; display:flex; justify-content:flex-end; }
</style>