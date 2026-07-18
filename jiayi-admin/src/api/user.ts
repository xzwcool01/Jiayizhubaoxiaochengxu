import request from '@/utils/axios'

export function login(data: { username: string; password: string }) {
  return request.post('/admin/login', data)
}

export function getUserInfo() {
  return request.get('/admin/userinfo')
}
