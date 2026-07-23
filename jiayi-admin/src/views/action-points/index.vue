<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getActionPointsList, updateActionPoints, type ActionPointsRule } from '@/api/actionPoints'

const rules = ref<ActionPointsRule[]>([])
const editVisible = ref(false)
const editing = ref<ActionPointsRule | null>(null)

async function fetchData() {
  const res = await getActionPointsList()
  if (res.code === 200) rules.value = res.data || []
}

function openEdit(row: ActionPointsRule) {
  editing.value = { ...row }
  editVisible.value = true
}

async function handleSave() {
  if (!editing.value) return
  await updateActionPoints(editing.value.id, {
    points: editing.value.points,
    actionName: editing.value.actionName,
    status: editing.value.status
  })
  ElMessage.success('保存成功')
  editVisible.value = false
  fetchData()
}

onMounted(fetchData)
</script>

<template>
  <div>
    <div class="page-header"><h2>行为积分</h2></div>
    <p style="color:#999;font-size:13px;margin-bottom:16px">如需添加新行为，联系开发人员</p>
    <el-card shadow="never">
      <el-alert type="info" show-icon :closable="false" style="margin-bottom:16px">
        <template #title>配置用户行为可获得的积分数。「下单赠送」的数值代表百分比（如 10 即实付金额的 10%），其他行为代表固定积分数。禁用某项后对应行为不再赠送积分。</template>
      </el-alert>
      <el-table :data="rules" stripe style="width:100%">
        <el-table-column prop="id" label="编号" width="60" />
        <el-table-column prop="actionKey" label="行为标识" width="120" />
        <el-table-column prop="actionName" label="行为名称" min-width="160" />
        <el-table-column prop="points" label="获得积分" width="120">
          <template #default="{ row }"><span style="font-weight:bold;color:#775836">{{ row.points }}</span></template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag></template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">{{ row.createTime?.replace('T', ' ').substring(0, 16) || '-' }}</template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="editVisible" title="编辑行为积分" width="500px">
      <el-form v-if="editing" label-width="120px">
        <el-form-item label="行为名称"><el-input v-model="editing.actionName" /></el-form-item>
        <el-form-item label="获得积分"><el-input-number v-model="editing.points" :min="0" style="width:200px" /></el-form-item>
        <el-form-item label="状态"><el-select v-model="editing.status"><el-option :value="1" label="启用" /><el-option :value="0" label="禁用" /></el-select></el-form-item>
      </el-form>
      <template #footer><el-button @click="editVisible = false">取消</el-button><el-button type="primary" @click="handleSave">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:16px; }
.page-header h2 { margin:0; font-size:20px; }
</style>
