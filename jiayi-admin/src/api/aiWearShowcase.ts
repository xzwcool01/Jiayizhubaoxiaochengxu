import request from '@/utils/axios'

export interface AiWearShowcaseItem {
  id: number
  imageUrl: string
  title: string
  tag: string
  userId: number | null
  nickname: string
  sortOrder: number
  createTime: string
}

export function getList() {
  return request.get<any, { code: number; message: string; data: AiWearShowcaseItem[] }>('/admin/ai-wear-showcase/list')
}

export function addItem(data: FormData) {
  return request.post<any, { code: number; message: string }>('/admin/ai-wear-showcase/add', data, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function updateItem(data: Partial<AiWearShowcaseItem>) {
  return request.post<any, { code: number; message: string }>('/admin/ai-wear-showcase/update', data)
}

export function deleteItem(id: number) {
  return request.post<any, { code: number; message: string }>(`/admin/ai-wear-showcase/delete/${id}`)
}

export function sortItems(list: { id: number; sortOrder: number }[]) {
  return request.post<any, { code: number; message: string }>('/admin/ai-wear-showcase/sort', list)
}
