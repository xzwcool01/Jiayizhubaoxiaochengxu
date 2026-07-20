import request from '@/utils/axios'

export interface AdminCouponVO {
  id?: number
  name: string
  type: number
  value: number
  minAmount: number
  maxAmount: number
  startTime?: string
  endTime?: string
  totalCount?: number
  perUserLimit?: number
  usedCount?: number
  status?: number
  productIds?: number[]
  productNames?: string[]
}

export interface UmsUser {
  id: number
  nickname: string
  phone: string
}

export function getCouponList(params: { page: number; size: number }) {
  return request.get<any, { code: number; data: { records: AdminCouponVO[]; total: number } }>('/admin/coupon/list', { params })
}

export function getCoupon(id: number) {
  return request.get<any, { code: number; data: AdminCouponVO }>('/admin/coupon/' + id)
}

export function createCoupon(data: AdminCouponVO) {
  return request.post<any, { code: number }>('/admin/coupon', data)
}

export function updateCoupon(id: number, data: AdminCouponVO) {
  return request.put<any, { code: number }>('/admin/coupon/' + id, data)
}

export function issueCoupon(params: { couponId: number; userIds?: number[]; all?: boolean }) {
  return request.post<any, { code: number }>('/admin/coupon/issue', params)
}

export function searchUsers(keyword: string) {
  return request.get<any, { code: number; data: UmsUser[] }>('/admin/coupon/search-users', { params: { keyword } })
}