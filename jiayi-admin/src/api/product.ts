import request from '@/utils/axios'

export interface PmsProduct {
  id: number
  categoryId: number
  categoryName?: string
  productType: number
  name: string
  subtitle: string
  mainImage: string
  images: string
  description: string
  price: number
  originalPrice: number
  pointsPrice: number
  stock: number
  sales: number
  flashStock: number
  flashSold: number
  saleStart: string
  saleEnd: string
  memberLevel: number
  isNew: number
  isRecommend: number
  sortOrder: number
  weight: number
  status: number
  extraAttrs: string
  createTime: string
  updateTime: string
}

export interface ProductDTO {
  categoryId: number
  productType: number
  name: string
  subtitle?: string
  mainImage?: string
  images?: string[]
  description?: string
  price: number
  originalPrice?: number
  pointsPrice?: number
  stock?: number
  flashStock?: number
  saleStart?: string
  saleEnd?: string
  memberLevel?: number
  isNew?: number
  isRecommend?: number
  sortOrder?: number
  weight?: number
  status?: number
  extraAttrs?: string
}

export interface Category {
  id: number
  parentId: number
  name: string
  sort: number
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

export function getProductList(params: {
  page: number
  size: number
  productType?: number
  categoryId?: number
  keyword?: string
  status?: number
}) {
  return request.get<any, PageRes<PmsProduct>>('/admin/product/list', { params })
}

export function getProduct(id: number) {
  return request.get<any, { code: number; data: PmsProduct }>(`/admin/product/${id}`)
}

export function createProduct(data: ProductDTO) {
  return request.post<any, { code: number; data: PmsProduct }>('/admin/product', data)
}

export function updateProduct(id: number, data: ProductDTO) {
  return request.put<any, { code: number; data: PmsProduct }>(`/admin/product/${id}`, data)
}

export function deleteProduct(id: number) {
  return request.delete<any, { code: number }>(`/admin/product/${id}`)
}

export function getCategories() {
  return request.get<any, { code: number; data: Category[] }>('/admin/product/categories')
}
