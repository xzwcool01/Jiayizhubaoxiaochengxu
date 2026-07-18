<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getLevelList, createLevel, updateLevel, deleteLevel, type UmsLevel } from '@/api/level'

const list = ref<UmsLevel[]>([])
const editVisible = ref(false)
const addVisible = ref(false)
const editForm = ref<Partial<UmsLevel>>({})
const addForm = ref({ name: '', minPoints: 0, maxPoints: 0, levelOrder: 0 })

async function fetchData() {
  const res = await getLevelList()
  list.value = res.data
}

function handleEdit(row: UmsLevel) {
  editForm.value = { ...row }
  editVisible.value = true
}

function openAdd() {
  addForm.value = { name: '', minPoints: 0, maxPoints: 0, levelOrder: list.value.length + 1 }
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
  await updateLevel(editForm.value.id, editForm.value)
  ElMessage.success('更新成功')
  editVisible.value = false
  fetchData()
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该等级？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
  await deleteLevel(id)
  ElMessage.success('删除成功')
  fetchData()
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
        <el-table-column prop="levelOrder" label="排序" width="70" />
        <el-table-column prop="name" label="等级名称" min-width="150" />
        <el-table-column prop="minPoints" label="积分下限" width="120" />
        <el-table-column prop="maxPoints" label="积分上限" width="120" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="editVisible" title="编辑等级" width="500px">
      <el-form v-if="editForm" label-width="100px">
        <el-form-item label="等级名称">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="积分下限">
          <el-input-number v-model="editForm.minPoints" :min="0" />
        </el-form-item>
        <el-form-item label="积分上限">
          <el-input-number v-model="editForm.maxPoints" :min="0" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="editForm.levelOrder" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="addVisible" title="新增等级" width="500px">
      <el-form label-width="100px">
        <el-form-item label="等级名称">
          <el-input v-model="addForm.name" placeholder="如：钻石会员" />
        </el-form-item>
        <el-form-item label="积分下限">
          <el-input-number v-model="addForm.minPoints" :min="0" />
        </el-form-item>
        <el-form-item label="积分上限">
          <el-input-number v-model="addForm.maxPoints" :min="0" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="addForm.levelOrder" :min="1" />
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
