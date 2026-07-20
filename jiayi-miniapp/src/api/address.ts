import { get, post, put, remove } from './request'

export interface UmsUserAddress {
  id?: number
  userId?: number
  name: string
  phone: string
  province: string
  city: string
  district: string
  detail: string
  isDefault?: boolean
}

function getOpenid(): string {
  return uni.getStorageSync('token') || ''
}

export function getAddressList() {
  return get<UmsUserAddress[]>('/address/list', { openid: getOpenid() })
}

export function addAddress(addr: UmsUserAddress) {
  addr.userId = 0
  return post<null>('/address/add', addr)
}

export function updateAddress(addr: UmsUserAddress) {
  return put<null>('/address/update', addr)
}

export function setDefaultAddress(id: number) {
  return put<null>('/address/default', { id, openid: getOpenid() })
}

export function removeAddress(id: number) {
  return remove<null>('/address/remove', { id, openid: getOpenid() })
}