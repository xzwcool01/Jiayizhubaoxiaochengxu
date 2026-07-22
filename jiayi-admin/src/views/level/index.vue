<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getLevelList, createLevel, updateLevel, deleteLevel, type UmsLevel } from '@/api/level'

const list = ref<UmsLevel[]>([])
const editVisible = ref(false)
const addVisible = ref(false)
const editForm = ref<Partial<UmsLevel>>({})
const addForm = ref({ name: '', minPoints: 0, maxPoints: 999999, levelOrder: 0, color: '#775836', perks: '', discountRate: 10 })

async function fetchData() {
  const res = await getLevelList()
  list.value = res.data
}

function handleEdit(row: UmsLevel) {
  editForm.value = { ...row }
  editVisible.value = true
}

function openAdd() {
  addForm.value = { name: '', minPoints: 0, maxPoints: 999999, levelOrder: list.value.length + 1, color: '#775836', perks: '', discountRate: 10 }
  addVisible.value = true
}

async function handleAdd() {
  await createLevel(addForm.value)
  ElMessage.success('新增成功')
  addVisible.value = false
  fetchData()
}

async function handleSave() {
  if (!editForm.value.id) return
  try {
    const payload = { ...editForm.value }
    if (typeof payload.perks === 'object') payload.perks = JSON.stringify(payload.perks)
    await updateLevel(editForm.value.id, payload)
    ElMessage.success('更新成功')
    editVisible.value = false
    fetchData()
  } catch { ElMessage.error('保存失败') }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该等级？', '提示')
  await deleteLevel(id)
  ElMessage.success('删除成功')
  fetchData()
}

function parsePerks(perks: any): string {
  if (!perks) return '-'
  try {
    const arr = typeof perks === 'string' ? JSON.parse(perks) : perks
    return Array.isArray(arr) ? arr.join('、') : perks
  } catch { return perks }
}

onMounted(fetchData)
</script>

<template>
  <div>
    <div class="page-header">
      <h2>等级配置</h2>
      <el-button type="primary" @click="openAdd">新增等级</el-button>
    </div>

    <el-card shadow="never">
      <el-table :data="list" stripe style="width:100%">
        <el-table-column prop="levelOrder" label="排序" width="60" />
        <el-table-column label="颜色" width="50">
          <template #default="{ row }"><div :style="'width:16px;height:16px;border-radius:50%;background:' + (row.color || '#999')" /></template>
        </el-table-column>
        <el-table-column prop="name" label="等级名称" width="100" />
        <el-table-column prop="minPoints" label="积分下限" width="80" />
        <el-table-column prop="maxPoints" label="积分上限" width="80" />
        <el-table-column label="权益" min-width="200" show-overflow-tooltip>
          <template #default="{ row }">{{ parsePerks(row.perks) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="130" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="editVisible" title="编辑等级" width="550px">
      <el-form v-if="editForm" label-width="100px">
        <el-form-item label="等级名称"><el-input v-model="editForm.name" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="editForm.levelOrder" :min="1" /></el-form-item>
        <el-form-item label="积分下限"><el-input-number v-model="editForm.minPoints" :min="0" /></el-form-item>
        <el-form-item label="积分上限"><el-input-number v-model="editForm.maxPoints" :min="0" /></el-form-item>
        <el-form-item label="折扣率"><el-input-number v-model="editForm.discountRate" :min="0" :max="10" :step="0.5" /><span style="margin-left:8px;color:#999">折</span></el-form-item>
        <el-form-item label="显示颜色"><el-input v-model="editForm.color" placeholder="#775836" /></el-form-item>
        <el-form-item label="权益列表">
          <el-input v-model="editForm.perks" type="textarea" :rows="3" placeholder='JSON数组，如 ["权益1","权益2"]' />
          <div style="font-size:12px;color:#999;margin-top:4px">JSON 数组格式</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="addVisible" title="新增等级" width="550px">
      <el-form label-width="100px">
        <el-form-item label="等级名称"><el-input v-model="addForm.name" placeholder="如：钻石会员" /></el-form-item>
        <el-form-item label="排序"><el-input-number v-model="addForm.levelOrder" :min="1" /></el-form-item>
        <el-form-item label="积分下限"><el-input-number v-model="addForm.minPoints" :min="0" /></el-form-item>
        <el-form-item label="积分上限"><el-input-number v-model="addForm.maxPoints" :min="0" /></el-form-item>
        <el-form-item label="折扣率"><el-input-number v-model="addForm.discountRate" :min="0" :max="10" :step="0.5" /><span style="margin-left:8px;color:#999">折</span></el-form-item>
        <el-form-item label="显示颜色"><el-input v-model="addForm.color" placeholder="#775836" /></el-form-item>
        <el-form-item label="权益列表">
          <el-input v-model="addForm.perks" type="textarea" :rows="3" placeholder='JSON数组，如 ["权益1","权益2"]' />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="addVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-header { margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center; }
.page-header h2 { margin: 0; font-size: 20px; color: #1C1B1B; }
</style>
