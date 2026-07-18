import request from '@/utils/axios'

export interface UmsLevel {
  id: number
  name: string
  minPoints: number
  maxPoints: number
  levelOrder: number
}

export function getLevelList() {
  return request.get<any, { code: number; data: UmsLevel[] }>('/admin/level/list')
}

export function getLevel(id: number) {
  return request.get(`/admin/level/${id}`)
}

export function createLevel(data: Partial<UmsLevel>) {
  return request.post('/admin/level', data)
}

export function updateLevel(id: number, data: Partial<UmsLevel>) {
  return request.put(`/admin/level/${id}`, data)
}

export function deleteLevel(id: number) {
  return request.delete(`/admin/level/${id}`)
}
