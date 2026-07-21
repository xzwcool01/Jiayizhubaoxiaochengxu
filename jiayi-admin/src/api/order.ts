import request from '@/utils/axios'

export interface AdminOrderItemVO {
  id: number
  productId: number
  productName: string
  productSpecs: string
  productImage: string
  price: number
  quantity: number
  subtotal: number
}

export interface AdminOrderVO {
  id: number
  orderSn: string
  userId: number
  userName: string
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
  items: AdminOrderItemVO[]
  trackingNo: string
  expressCompany: string
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

export function shipOrder(data: { orderId: number; expressCompany: string; trackingNo: string }) {
  return request.post<any, { code: number; message: string }>('/admin/delivery/ship', data)
}

export function trackOrder(params: { trackingNo: string; phone?: string }) {
  return request.get<any, { code: number; data: any; message: string }>('/admin/delivery/track', { params })
}

export function previewWaybill(params: { waybillNo: string }) {
  return request.get<any, { code: number; data: any; message: string }>('/admin/delivery/preview-waybill', { params })
}