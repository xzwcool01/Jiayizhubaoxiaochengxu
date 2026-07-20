import { get, post } from './request'

export interface OrderItemVO {
  id: number
  productId: number
  productName: string
  productSpecs: string
  productImage: string
  price: number
  quantity: number
  subtotal: number
}

export interface OrderVO {
  id: number
  orderSn: string
  totalAmount: number
  payAmount: number
  couponAmount: number
  pointsAmount: number
  pointsDeduct: number
  paymentMethod: number
  status: number
  addressSnapshot: string
  note: string
  paidAt: string
  createTime: string
  items: OrderItemVO[]
}

function getOpenid(): string {
  return uni.getStorageSync('token') || ''
}

export function createOrder(params: {
  addressId: number
  couponId?: number
  usePoints?: boolean
  note?: string
  cartItemIds?: number[]
}) {
  return post<OrderVO>('/order/create', { openid: getOpenid(), ...params })
}

export function getOrderDetail(id: number) {
  return get<OrderVO>('/order/detail', { id, openid: getOpenid() })
}

export function getOrderList(status?: number) {
  const params: Record<string, any> = { openid: getOpenid() }
  if (status !== undefined) params.status = status
  return get<OrderVO[]>('/order/list', params)
}

export function payOrder(id: number) {
  return post<null>('/order/pay', { id })
}

export function cancelOrder(id: number) {
  return post<null>('/order/cancel', { id, openid: getOpenid() })
}