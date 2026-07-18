const BASE_URL = 'http://localhost:8080/api'

interface RequestOptions {
  url: string
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  data?: any
  header?: Record<string, string>
}

interface R<T> {
  code: number
  message: string
  data: T
}

export function request<T = any>(options: RequestOptions): Promise<R<T>> {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data,
      header: { 'Content-Type': 'application/json', ...options.header },
      success: (res) => {
        resolve(res.data as R<T>)
      },
      fail: (err) => {
        reject(err)
      }
    })
  })
}

export function get<T = any>(url: string, data?: any): Promise<R<T>> {
  return request<T>({ url, method: 'GET', data })
}

export function post<T = any>(url: string, data?: any): Promise<R<T>> {
  return request<T>({ url, method: 'POST', data })
}
