import { get, post, remove, put } from './request'

export interface CartItemVO {
  id: number
  productId: number
  name: string
  mainImage: string
  specs: string
  price: number
  quantity: number
  selected: boolean
  createTime: string
}

function getOpenid(): string {
  return uni.getStorageSync('token') || ''
}

export function addToCart(productId: number, quantity = 1) {
  return post<null>('/cart/add', { openid: getOpenid(), productId, quantity })
}

export function getCartList() {
  return get<CartItemVO[]>('/cart/list', { openid: getOpenid() })
}

export function updateQuantity(id: number, quantity: number) {
  return put<null>('/cart/quantity', { id, quantity })
}

export function updateSelected(id: number, selected: boolean) {
  return put<null>('/cart/selected', { id, selected })
}

export function toggleSelectAll(selected: boolean) {
  return put<null>('/cart/selectAll', { openid: getOpenid(), selected })
}

export function removeCartItem(id: number) {
  return remove<null>('/cart/remove', { id })
}

export function removeBatch(ids: number[]) {
  return post<null>('/cart/removeBatch', { openid: getOpenid(), ids })
}