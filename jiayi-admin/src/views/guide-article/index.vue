<script setup lang="ts">
import { ref, onMounted, shallowRef, onBeforeUnmount } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getList, addItem, updateItem, deleteItem, type GuideArticleItem } from '@/api/guideArticle'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'
import request from '@/utils/axios'

const list = ref<GuideArticleItem[]>([])
const dialogVisible = ref(false)
const editDialogVisible = ref(false)
const editing = ref<GuideArticleItem | null>(null)
const form = ref({ title: '', summary: '', author: '', publishDate: '', isHero: 0, status: 1, sortOrder: 0, views: 0 })
const file = ref<File | null>(null)
const previewUrl = ref('')

const editorContent = ref('')
const editorRef = shallowRef<any>(null)
const toolbarConfig = { excludeKeys: ['fullScreen'] as any }
const editorConfig = {
  placeholder: '请输入文章内容...',
  MENU_CONF: {
    uploadImage: {
      async customUpload(file: File, insertFn: (url: string, alt: string, href: string) => void) {
        const fd = new FormData()
        fd.append('file', file)
        try {
          const res = await request.post('/admin/guide-article/upload-image', fd, {
            headers: { 'Content-Type': 'multipart/form-data' }
          }) as any
          if (res.code === 200) {
            insertFn(res.data, '', '')
          } else {
            ElMessage.error(res.message || '上传失败')
          }
        } catch {
          ElMessage.error('图片上传失败')
        }
      }
    }
  }
}

function handleCreated(editor: any) {
  editorRef.value = editor
}

onBeforeUnmount(() => {
  if (editorRef.value) editorRef.value.destroy()
})

async function fetchData() {
  const res = await getList()
  if (res.code === 200) list.value = res.data || []
}

function openAdd() {
  form.value = { title: '', summary: '', author: '', publishDate: '', isHero: 0, status: 1, sortOrder: 0, views: 0 }
  file.value = null
  previewUrl.value = ''
  editorContent.value = ''
  dialogVisible.value = true
}

function openEdit(row: GuideArticleItem) {
  editing.value = { ...row }
  editorContent.value = row.content || ''
  editDialogVisible.value = true
}

async function handleAdd() {
  const fd = new FormData()
  fd.append('title', form.value.title)
  fd.append('summary', form.value.summary)
  fd.append('author', form.value.author)
  fd.append('isHero', String(form.value.isHero))
  fd.append('status', String(form.value.status))
  fd.append('sortOrder', String(form.value.sortOrder))
  fd.append('publishDate', form.value.publishDate)
  fd.append('views', String(form.value.views))
  fd.append('content', editorContent.value)
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
  const data: any = { id: editing.value.id, content: editorContent.value }
  if (editing.value.title !== undefined) data.title = editing.value.title
  if (editing.value.summary !== undefined) data.summary = editing.value.summary
  if (editing.value.author !== undefined) data.author = editing.value.author
  if (editing.value.publishDate !== undefined) data.publishDate = editing.value.publishDate
  if (editing.value.isHero !== undefined) data.isHero = editing.value.isHero
  if (editing.value.status !== undefined) data.status = editing.value.status
  if (editing.value.sortOrder !== undefined) data.sortOrder = editing.value.sortOrder
  if (editing.value.views !== undefined) data.views = editing.value.views
  if (editing.value.coverImage !== undefined) data.coverImage = editing.value.coverImage
  const res = await updateItem(data)
  if (res.code === 200) {
    ElMessage.success('更新成功')
    editDialogVisible.value = false
    fetchData()
  }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该文章？')
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
      <h2>珠宝指南</h2>
      <el-button type="primary" @click="openAdd">+ 新增文章</el-button>
    </div>
    <el-card shadow="never">
      <el-table :data="list" stripe style="width:100%">
        <el-table-column label="封面" width="90">
          <template #default="{ row }">
            <el-image :src="imgUrl(row.coverImage)" style="width:64px;height:64px;border-radius:8px;object-fit:cover" />
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="180">
          <template #default="{ row }">
            <div>
              <span :style="{ color: row.title ? '#1C1B1B' : '#bbb' }">{{ row.title || '（无标题）' }}</span>
              <el-tag v-if="row.isHero" size="small" type="warning" style="margin-left:8px">深度专题</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column prop="views" label="阅读" width="70" />
        <el-table-column prop="sortOrder" label="排序" width="70" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'info'" size="small">
              {{ row.status === 1 ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="150" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="openEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" title="新增文章" width="800px" destroy-on-close>
      <el-form :model="form" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="form.title" placeholder="文章标题" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="作者">
              <el-input v-model="form.author" placeholder="作者" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布日期">
              <el-input v-model="form.publishDate" placeholder="2024-11-20" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="摘要">
          <el-input v-model="form.summary" type="textarea" :rows="2" placeholder="文章摘要" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="深度专题">
              <el-switch v-model="form.isHero" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态">
              <el-switch v-model="form.status" :active-value="1" :inactive-value="0" active-text="发布" inactive-text="草稿" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序">
              <el-input-number v-model="form.sortOrder" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="阅读量">
              <el-input-number v-model="form.views" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面图">
          <input type="file" accept="image/*" @change="onFileChange" />
          <img v-if="previewUrl" :src="previewUrl" style="width:120px;height:120px;object-fit:cover;border-radius:8px;margin-top:8px;display:block" />
        </el-form-item>
        <el-form-item label="文章内容">
          <div style="border:1px solid #d9d9d9;border-radius:4px">
            <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" mode="default" style="border-bottom:1px solid #d9d9d9" />
            <Editor v-model="editorContent" :defaultConfig="editorConfig" mode="default" @onCreated="handleCreated" style="height:400px;overflow-y:hidden" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAdd">添加</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="editDialogVisible" title="编辑文章" width="800px" destroy-on-close>
      <el-form v-if="editing" :model="editing" label-width="100px">
        <el-form-item label="标题">
          <el-input v-model="editing.title" placeholder="文章标题" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="作者">
              <el-input v-model="editing.author" placeholder="作者" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布日期">
              <el-input v-model="editing.publishDate" placeholder="2024-11-20" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="摘要">
          <el-input v-model="editing.summary" type="textarea" :rows="2" placeholder="文章摘要" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="深度专题">
              <el-switch v-model="editing.isHero" :active-value="1" :inactive-value="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="状态">
              <el-switch v-model="editing.status" :active-value="1" :inactive-value="0" active-text="发布" inactive-text="草稿" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="排序">
              <el-input-number v-model="editing.sortOrder" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="阅读量">
              <el-input-number v-model="editing.views" :min="0" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="封面图">
          <el-image :src="imgUrl(editing.coverImage)" style="width:120px;height:120px;object-fit:cover;border-radius:8px" />
        </el-form-item>
        <el-form-item label="文章内容">
          <div style="border:1px solid #d9d9d9;border-radius:4px">
            <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" mode="default" style="border-bottom:1px solid #d9d9d9" />
            <Editor v-model="editorContent" :defaultConfig="editorConfig" mode="default" @onCreated="handleCreated" style="height:400px;overflow-y:hidden" />
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleEdit">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-header h2 { margin: 0; font-size: 18px; color: #1C1B1B; }
</style>
