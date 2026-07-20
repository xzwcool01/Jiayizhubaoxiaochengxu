<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getPointsRules, savePointsRule, getPointsProductIds, savePointsProducts, type SmsPointsRule } from '@/api/points'
import { getProductList, type PmsProduct } from '@/api/product'

const rules = ref<SmsPointsRule[]>([])
const editVisible = ref(false)
const editing = ref<SmsPointsRule>({ points: 100, amount: 1, type: 1, status: 1 })
const productLinkVisible = ref(false)
const linkRuleId = ref(0)
const linkedProductIds = ref<number[]>([])
const allProducts = ref<PmsProduct[]>([])

async function fetchRules() {
  const res = await getPointsRules()
  if (res.code === 200) rules.value = res.data || []
}

function openAdd() {
  editing.value = { points: 10, amount: 1, status: 1 }
  editVisible.value = true
}

function openEdit(row: SmsPointsRule) {
  editing.value = { ...row }
  editVisible.value = true
}

async function handleSave() {
  await savePointsRule(editing.value)
  ElMessage.success('保存成功')
  editVisible.value = false
  fetchRules()
}

async function openLink(row: SmsPointsRule) {
  linkRuleId.value = row.id || 0
  linkedProductIds.value = []
  const res = await getPointsProductIds(linkRuleId.value)
  if (res.code === 200) linkedProductIds.value = res.data || []
  const pres = await getProductList({ page: 1, size: 999, productType: 0 })
  if (pres.code === 200) allProducts.value = pres.data.records || []
  productLinkVisible.value = true
}

async function handleSaveProducts() {
  await savePointsProducts(linkRuleId.value, linkedProductIds.value)
  ElMessage.success('关联保存成功')
  productLinkVisible.value = false
}

function toggleProduct(id: number) {
  const idx = linkedProductIds.value.indexOf(id)
  if (idx >= 0) linkedProductIds.value.splice(idx, 1)
  else linkedProductIds.value.push(id)
}

onMounted(fetchRules)
</script>

<template>
  <div>
    <div class="page-header"><h2>积分规则</h2><el-button type="primary" @click="openAdd">新增规则</el-button></div>

    <el-card shadow="never">
      <el-alert type="info" show-icon :closable="false" style="margin-bottom:16px">
        <template #title>
          两种规则类型：
          <b>固定抵扣</b>— 消耗固定积分换固定金额（如 10000分 = ¥500），只能使用一次；
          <b>全积分抵扣</b>— 按比例消耗全部可用积分（如 100分 = ¥1，20000分 = ¥200）。
          每个商品只能关联一个积分规则，多个商品有不同规则时系统自动选用第一个。
        </template>
      </el-alert>
      <el-table :data="rules" stripe style="width:100%">
        <el-table-column prop="id" label="编号" width="60" />
        <el-table-column label="类型" width="120">
          <template #default="{ row }">{{ row.type === 1 ? '全积分抵扣' : '固定抵扣' }}</template>
        </el-table-column>
        <el-table-column prop="points" label="所需积分" width="120" />
        <el-table-column prop="amount" label="抵扣金额(¥)" width="140" />
        <el-table-column label="使用方式" min-width="240">
          <template #default="{ row }">
            {{ row.type === 1 ? `所有积分按 ${row.points}分=¥${row.amount} 比例抵扣` : `每次消耗 ${row.points} 积分抵扣 ¥${row.amount}` }}
          </template>
        </el-table-column>
        <el-table-column label="状态" width="70">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ row.createTime?.replace('T', ' ').substring(0, 16) || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="success" link @click="openLink(row)">关联商品</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="editVisible" :title="editing.id ? '编辑规则' : '新增规则'" width="500px">
      <el-form label-width="120px">
        <el-form-item label="规则类型">
          <el-radio-group v-model="editing.type">
            <el-radio :value="0">固定抵扣</el-radio>
            <el-radio :value="1">全积分抵扣</el-radio>
          </el-radio-group>
          <div style="font-size:12px;color:#999;margin-top:4px;line-height:1.5">
            <template v-if="editing.type === 0">固定消耗指定积分抵扣指定金额，例：10000分抵¥500，用户只能消耗10000分抵¥500</template>
            <template v-else>用户全部积分按比例抵扣，例：100分=¥1，用户有20000分则抵¥200</template>
          </div>
        </el-form-item>
        <el-form-item label="所需积分"><el-input-number v-model="editing.points" :min="1" style="width:200px" /></el-form-item>
        <el-form-item label="抵扣金额(¥)"><el-input-number v-model="editing.amount" :min="0.01" :precision="2" style="width:200px" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="editing.status"><el-option :value="1" label="启用" /><el-option :value="0" label="禁用" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="editVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>

    <el-dialog v-model="productLinkVisible" title="关联可用商品" width="600px">
      <p style="color:#999;margin-bottom:12px">勾选可使用积分抵扣的商品</p>
      <div style="max-height:400px;overflow-y:auto">
        <el-checkbox v-for="p in allProducts" :key="p.id" :checked="linkedProductIds.includes(p.id)" @change="toggleProduct(p.id)" style="display:flex;margin-bottom:6px;padding:6px 0">
          <span style="margin-left:8px">{{ p.name }} (¥{{ p.price }})</span>
        </el-checkbox>
        <p v-if="!allProducts.length" style="color:#ccc;text-align:center">加载中...</p>
      </div>
      <template #footer><el-button @click="productLinkVisible = false">取消</el-button><el-button type="primary" @click="handleSaveProducts">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
.page-header h2 { margin:0; font-size:20px; }
</style>