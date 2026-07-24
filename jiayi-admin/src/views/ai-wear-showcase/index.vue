<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getList, addItem, updateItem, deleteItem, sortItems, type AiWearShowcaseItem } from '@/api/aiWearShowcase'

const list = ref<AiWearShowcaseItem[]>([])
const dialogVisible = ref(false)
const editDialogVisible = ref(false)
const editing = ref<AiWearShowcaseItem | null>(null)
const form = ref({ title: '', tag: 'AI试戴', sortOrder: 0 })
const file = ref<File | null>(null)
const previewUrl = ref('')

async function fetchData() {
  const res = await getList()
  if (res.code === 200) list.value = res.data || []
}

function openAdd() {
  form.value = { title: '', tag: 'AI试戴', sortOrder: 0 }
  file.value = null
  previewUrl.value = ''
  dialogVisible.value = true
}

function openEdit(row: AiWearShowcaseItem) {
  editing.value = { ...row }
  editDialogVisible.value = true
}

async function handleAdd() {
  const fd = new FormData()
  fd.append('title', form.value.title)
  fd.append('tag', form.value.tag)
  fd.append('sortOrder', String(form.value.sortOrder))
  if (file.value) fd.append('file', file.value)
  const res = await addItem(fd)
  if (res.code === 200) {
    ElMessage.success('添加成功')
    dialogVisible.value = false
    fetchData()
  } else {
    ElMessage.error(res.message)
  }
}

async function handleEdit() {
  if (!editing.value) return
  const res = await updateItem({
    id: editing.value.id,
    title: editing.value.title,
    tag: editing.value.tag,
    sortOrder: editing.value.sortOrder
  })
  if (res.code === 200) {
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    fetchData()
  }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该展示项？')
  const res = await deleteItem(id)
  if (res.code === 200) {
    ElMessage.success('已删除')
    fetchData()
  }
}

function onFileChange(e: Event) {
  const target = e.target as HTMLInputElement
  if (target.files && target.files[0]) {
    file.value = target.files[0]
    previewUrl.value = URL.createObjectURL(target.files[0])
  }
}

function imgUrl(url: string) {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `http://localhost:8080${url}`
}

onMounted(fetchData)
</script>

<template>
  <div>
    <div class="page-header">
      <h2>AI 试戴展示管理</h2>
      <el-button type="primary" @click="openAdd">+ 新增展示</el-button>
    </div>
    <p style="color:#999;font-size:13px;margin-bottom:16px">
      用户发布到发现页的试戴图自动出现在此列表。可手动新增、编辑标题/标签、调整排序、删除。
    </p>
    <el-card shadow="never">
      <el-table :data="list" stripe style="width:100%">
        <el-table-column label="图片" width="100">
          <template #default="{ row }">
            <el-image :src="imgUrl(row.imageUrl)" style="width:72px;height:72px;border-radius:8px;object-fit:cover" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="160">
          <template #default="{ row }">
            <span :style="{ color: row.title ? '#1C1B1B' : '#bbb' }">{{ row.title || '（无标题）' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="tag" label="标签" width="120" />
        <el-table-column prop="nickname" label="来源" width="120">
          <template #default="{ row }">
            <span v-if="row.userId" style="color:#775836">{{ row.nickname || '用户' }}</span>
            <span v-else style="color:#999">管理员</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增展示" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="展示标题" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="form.tag" placeholder="AI试戴" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="上传图片">
          <input type="file" accept="image/*" @change="onFileChange" />
          <img v-if="previewUrl" :src="previewUrl" style="width:120px;height:120px;object-fit:cover;border-radius:8px;margin-top:8px;display:block" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">添加</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editDialogVisible" title="编辑展示" width="480px">
      <el-form v-if="editing" :model="editing" label-width="80px">
        <el-form-item label="图片">
          <el-image :src="imgUrl(editing.imageUrl)" style="width:120px;height:120px;object-fit:cover;border-radius:8px" />
        </el-form-item>
        <el-form-item label="标题">
          <el-input v-model="editing.title" placeholder="展示标题" />
        </el-form-item>
        <el-form-item label="标签">
          <el-input v-model="editing.tag" placeholder="AI试戴" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="editing.sortOrder" :min="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>
