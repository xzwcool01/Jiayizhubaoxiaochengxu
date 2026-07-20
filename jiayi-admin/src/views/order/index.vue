<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { getOrderList, type AdminOrderVO } from '@/api/order'

const list = ref<AdminOrderVO[]>([])
const total = ref(0)
const loading = ref(false)
const query = reactive({ page: 1, size: 20, userName: '', status: undefined as number | undefined })

const statusMap: Record<number, string> = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已关闭' }
const statusColors: Record<number, string> = { 0: '#e6a23c', 1: '#409eff', 2: '#67c23a', 3: '#909399', 4: '#c0c4cc' }
const statusOptions = [
  { value: undefined, label: '全部状态' },
  { value: 0, label: '待付款' },
  { value: 1, label: '待发货' },
  { value: 2, label: '待收货' },
  { value: 3, label: '已完成' },
  { value: 4, label: '已关闭' }
]

async function fetchData() {
  loading.value = true
  try {
    const params: Record<string, any> = { page: query.page, size: query.size }
    if (query.userName.trim()) params.userName = query.userName.trim()
    if (query.status !== undefined) params.status = query.status
    const res = await getOrderList(params)
    if (res.code === 200) { list.value = res.data.records; total.value = res.data.total }
  } finally { loading.value = false }
}

function onSearch() { query.page = 1; fetchData() }
function onReset() { query.userName = ''; query.status = undefined; query.page = 1; fetchData() }

function formatTime(t?: string) { return t ? t.replace('T', ' ').substring(0, 19) : '-' }

onMounted(fetchData)
</script>

<template>
  <div>
    <div class="page-header"><h2>订单管理</h2></div>

    <el-card shadow="never" style="margin-bottom:16px">
      <el-form :inline="true" @keyup.enter="onSearch">
        <el-form-item label="用户">
          <el-input v-model="query.userName" placeholder="输入用户昵称或手机号" clearable style="width:260px" @clear="onSearch" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部状态" clearable style="width:140px">
            <el-option v-for="opt in statusOptions" :key="opt.label" :value="opt.value" :label="opt.label" />
          </el-select>
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
        <el-table-column prop="orderSn" label="订单号" width="190" />
        <el-table-column prop="userId" label="用户ID" width="70" />
        <el-table-column prop="payAmount" label="实付金额" width="100">
          <template #default="{ row }">¥{{ row.payAmount }}</template>
        </el-table-column>
        <el-table-column label="订单状态" width="90">
          <template #default="{ row }">
            <el-tag :color="statusColors[row.status]" style="color:#fff;border:none;font-size:12px">{{ statusMap[row.status] || '未知' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="支付方式" width="80">
          <template #default="{ row }">{{ row.paymentMethod === 1 ? '微信支付' : '未支付' }}</template>
        </el-table-column>
        <el-table-column label="支付时间" width="160">
          <template #default="{ row }">{{ row.paidAt ? formatTime(row.paidAt) : '-' }}</template>
        </el-table-column>
        <el-table-column label="下单时间" width="160">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column prop="note" label="备注" min-width="100" show-overflow-tooltip />
      </el-table>
      <el-pagination v-model:current-page="query.page" v-model:page-size="query.size" :total="total" layout="total, sizes, prev, pager, next, jumper" :page-sizes="[10, 20, 50]" style="margin-top:16px; justify-content:flex-end" @current-change="fetchData" @size-change="fetchData" />
    </el-card>
  </div>
</template>

<style scoped>
.page-header { margin-bottom:16px; }
.page-header h2 { margin:0; font-size:20px; }
</style>