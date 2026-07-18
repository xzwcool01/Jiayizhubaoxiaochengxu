<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMemberList, updateMember, deleteMember, createMember, type UmsUser } from '@/api/member'

const list = ref<UmsUser[]>([])
const total = ref(0)
const page = ref(1)
const size = ref(10)
const keyword = ref('')
const loading = ref(false)
const editVisible = ref(false)
const addVisible = ref(false)
const editForm = ref<Partial<UmsUser>>({})
const addForm = ref({ nickname: '', phone: '', gender: 0, levelId: 1, points: 0 })

const genderMap: Record<number, string> = { 0: '未知', 1: '男', 2: '女' }

async function fetchData() {
  loading.value = true
  try {
    const res = await getMemberList({ page: page.value, size: size.value, keyword: keyword.value || undefined })
    list.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function onSearch() { page.value = 1; fetchData() }

function handleEdit(row: UmsUser) {
  editForm.value = { ...row }
  editVisible.value = true
}

function openAdd() {
  addForm.value = { nickname: '', phone: '', gender: 0, levelId: 1, points: 0 }
  addVisible.value = true
}

async function handleAdd() {
  await createMember(addForm.value)
  ElMessage.success('新增成功')
  addVisible.value = false
  fetchData()
}

async function handleSave() {
  if (!editForm.value.id) return
  await updateMember(editForm.value.id, editForm.value)
  ElMessage.success('更新成功')
  editVisible.value = false
  fetchData()
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该会员？', '提示', { confirmButtonText: '确定', cancelButtonText: '取消', type: 'warning' })
  await deleteMember(id)
  ElMessage.success('删除成功')
  fetchData()
}

function formatTime(t?: string) {
  if (!t) return '-'
  return t.replace('T', ' ').substring(0, 19)
}

function avatarError(e: Event) {
  (e.target as HTMLElement).style.display = 'none'
}

onMounted(fetchData)
</script>

<template>
  <div class="member-page">
    <div class="page-header">
      <h2>会员管理</h2>
      <el-button type="primary" @click="openAdd">新增会员</el-button>
    </div>

    <el-card shadow="never">
      <el-form :inline="true" @submit.prevent="onSearch">
        <el-form-item>
          <el-input v-model="keyword" placeholder="搜索昵称 / 手机号 / 会员编号" clearable style="width:320px" @clear="onSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="onSearch">搜索</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" style="margin-top:16px">
      <el-table :data="list" v-loading="loading" stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="头像" width="70">
          <template #default="{ row }">
            <div class="avatar-cell">
              <img v-if="row.avatar" :src="row.avatar" class="avatar-img" @error="avatarError" />
              <span v-if="!row.avatar || !list" class="avatar-fallback">{{ row.nickname?.charAt(0) || '?' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="memberNo" label="会员编号" width="110" />
        <el-table-column prop="nickname" label="昵称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column label="性别" width="60">
          <template #default="{ row }">{{ genderMap[row.gender] || '未知' }}</template>
        </el-table-column>
        <el-table-column label="等级" width="60">
          <template #default="{ row }">{{ row.levelId === 2 ? '正式' : '普通' }}</template>
        </el-table-column>
        <el-table-column prop="points" label="积分" width="70" />
        <el-table-column label="注册时间" width="170">
          <template #default="{ row }">{{ formatTime(row.createTime) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        :page-sizes="[10, 20, 50]"
        style="margin-top:16px; justify-content:flex-end"
        @current-change="fetchData"
        @size-change="fetchData"
      />
    </el-card>

    <el-dialog v-model="editVisible" title="编辑会员" width="500px">
      <el-form v-if="editForm" label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="editForm.nickname" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="editForm.phone" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="editForm.gender">
            <el-option :value="0" label="未知" />
            <el-option :value="1" label="男" />
            <el-option :value="2" label="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="editForm.levelId">
            <el-option :value="1" label="普通会员" />
            <el-option :value="2" label="正式会员" />
          </el-select>
        </el-form-item>
        <el-form-item label="积分">
          <el-input-number v-model="editForm.points" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="addVisible" title="新增会员" width="500px">
      <el-form label-width="80px">
        <el-form-item label="昵称">
          <el-input v-model="addForm.nickname" placeholder="默认：新会员" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="addForm.phone" placeholder="选填" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="addForm.gender">
            <el-option :value="0" label="未知" />
            <el-option :value="1" label="男" />
            <el-option :value="2" label="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="addForm.levelId">
            <el-option :value="1" label="普通会员" />
            <el-option :value="2" label="正式会员" />
          </el-select>
        </el-form-item>
        <el-form-item label="积分">
          <el-input-number v-model="addForm.points" :min="0" />
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
.member-page { padding: 0; }
.page-header { margin-bottom: 16px; display: flex; justify-content: space-between; align-items: center; }
.page-header h2 { margin: 0; font-size: 20px; color: #1C1B1B; }
.avatar-cell { display: flex; align-items: center; }
.avatar-img { width: 32px; height: 32px; border-radius: 50%; object-fit: cover; }
.avatar-fallback { width: 32px; height: 32px; border-radius: 50%; background-color: #f0f0f0; display: flex; align-items: center; justify-content: center; font-size: 12px; color: #999; }
</style>
