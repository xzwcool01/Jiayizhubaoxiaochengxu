import request from '@/utils/axios'

export interface AdminAddressVO {
  id: number
  userId: number
  userName: string
  name: string
  phone: string
  province: string
  city: string
  district: string
  detail: string
  isDefault: boolean
  createTime: string
}

export function getAddressList(params: { page: number; size: number; userName?: string }) {
  return request.get<any, { code: number; data: { records: AdminAddressVO[]; total: number } }>('/admin/address/list', { params })
}