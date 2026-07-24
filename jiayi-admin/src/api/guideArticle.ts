import request from '@/utils/axios'

export interface GuideArticleItem {
  id: number
  title: string
  summary: string
  coverImage: string
  content: string
  views: number
  author: string
  publishDate: string
  isHero: number
  status: number
  sortOrder: number
  createTime: string
  updateTime: string
}

export function getList() {
  return request.get<any, { code: number; message: string; data: GuideArticleItem[] }>('/admin/guide-article/list')
}

export function addItem(data: FormData) {
  return request.post<any, { code: number; message: string }>('/admin/guide-article/add', data, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function updateItem(data: Partial<GuideArticleItem>) {
  return request.post<any, { code: number; message: string }>('/admin/guide-article/update', data)
}

export function deleteItem(id: number) {
  return request.post<any, { code: number; message: string }>(`/admin/guide-article/delete/${id}`)
}
