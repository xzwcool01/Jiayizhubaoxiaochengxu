import request from '@/utils/axios'

export interface AdminOrderVO {
  id: number
  orderSn: string
  userId: number
  totalAmount: number
  payAmount: number
  couponAmount: number
  pointsAmount: number
  pointsDeduct: number
  paymentMethod: number
  status: number
  addressSnapshot: string
  note: string
  couponId: number
  paidAt: string
  createTime: string
}

export function getOrderList(params: {
  page: number
  size: number
  userId?: number
  status?: number
  userName?: string
}) {
  return request.get<any, { code: number; data: { records: AdminOrderVO[]; total: number } }>('/admin/order/list', { params })
}