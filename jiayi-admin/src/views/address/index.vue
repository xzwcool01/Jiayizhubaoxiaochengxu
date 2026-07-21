<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getAddressList, type AdminAddressVO } from '@/api/address'

const list = ref<AdminAddressVO[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(20)
const loading = ref(false)
const userName = ref('')

async function fetchData() {
  loading.value = true
  try {
    const params: Record<string, any> = { page: page.value, size: size.value }
    if (userName.value.trim()) params.userName = userName.value.trim()
    const res = await getAddressList(params)
    if (res.code === 200) { list.value = res.data.records; total.value = res.data.total }
  } finally { loading.value = false }
}

function onSearch() { page.value = 1; fetchData() }
function onReset() { userName.value = ''; page.value = 1; fetchData() }
function formatTime(t?: string) { return t ? t.replace('T', ' ').substring(0, 16) : '-' }

onMounted(fetchData)
</script>

<template>
  <div>
    <div class="page-header"><h2>地址管理</h2></div>

    <el-card shadow="never" style="margin-bottom:16px">
      <el-form :inline="true" @keyup.enter="onSearch">
        <el-form-item label="用户名称">
          <el-input v-model="userName" placeholder="输入用户昵称或手机号搜索" clearable style="width:300px" @clear="onReset" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">查询</el-button>
          <el-button @click="onReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never">
      <el-table :data="list" v-loading="loading" stripe style="width:100%">
        <el-table-column prop="id" label="编号" width="60" />
        <el-table-column prop="userName" label="用户昵称" min-width="120" />
        <el-table-column prop="userId" label="用户ID" width="70" />
        <el-table-column prop="name" label="收件人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column label="详细地址" min-width="300">
          <template #default="{ row }">{{ row.province }}{{ row.city }}{{ row.district }}{{ row.detail }}</template>
        </el-table-column>
        <el-table-column label="默认地址" width="80">
          <template #default="{ row }">{{ row.isDefault ? '✓ 是' : '✗ 否' }}</template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="page" v-model:page-size="size" :total="total" layout="total, sizes, prev, pager, next, jumper" :page-sizes="[10, 20, 50]" style="margin-top:16px; justify-content:flex-end" @current-change="fetchData" @size-change="fetchData" />
    </el-card>
  </div>
</template>

<style scoped>
.page-header { margin-bottom:16px; }
.page-header h2 { margin:0; font-size:20px; }
</style>