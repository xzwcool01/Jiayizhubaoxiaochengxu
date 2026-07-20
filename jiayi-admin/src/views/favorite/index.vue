<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getFavoriteList, type UmsUserFavorite } from '@/api/favorite'

const list = ref<(UmsUserFavorite & { userName?: string; productName?: string; productImage?: string })[]>([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 20, userId: undefined as number | undefined, productId: undefined as number | undefined })

async function fetchData() {
  loading.value = true
  try {
    const res = await getFavoriteList(query)
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
  query.page = 1
  fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div>
    <div class="page-header"><h2>收藏管理</h2></div>
    <el-card shadow="never" style="margin-bottom:16px">
      <el-form :inline="true" @keyup.enter="handleQuery">
        <el-form-item label="用户ID">
          <el-input-number v-model="query.userId" :min="0" placeholder="按用户ID筛选" clearable style="width:160px" />
        </el-form-item>
        <el-form-item label="商品ID">
          <el-input-number v-model="query.productId" :min="0" placeholder="按商品ID筛选" clearable style="width:160px" />
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
        <el-table-column prop="userId" label="用户ID" width="90" />
        <el-table-column prop="productId" label="商品ID" width="90" />
        <el-table-column label="收藏时间" width="180">
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
