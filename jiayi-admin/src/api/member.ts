import request from '@/utils/axios'

export interface UmsUser {
  id: number
  openid: string
  memberNo: string
  nickname: string
  avatar: string
  phone: string
  gender: number
  levelId: number
  points: number
  createTime: string
  updateTime: string
}

export interface PageRes<T> {
  code: number
  message: string
  data: {
    records: T[]
    total: number
    current: number
    size: number
  }
}

export function getMemberList(params: { page: number; size: number; keyword?: string }) {
  return request.get<any, PageRes<UmsUser>>('/admin/member/list', { params })
}

export function getMember(id: number) {
  return request.get(`/admin/member/${id}`)
}

export function updateMember(id: number, data: Partial<UmsUser>) {
  return request.put(`/admin/member/${id}`, data)
}

export function deleteMember(id: number) {
  return request.delete(`/admin/member/${id}`)
}

export function createMember(data: { nickname?: string; phone?: string; gender?: number; levelId?: number; points?: number }) {
  return request.post<any, any>('/admin/member', data)
}
