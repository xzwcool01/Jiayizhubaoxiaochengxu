import request from '@/utils/axios'

export interface ActionPointsRule {
  id: number
  actionKey: string
  actionName: string
  points: number
  status: number
  createTime: string
}

export function getActionPointsList() {
  return request.get<any, { code: number; data: ActionPointsRule[] }>('/admin/action-points/list')
}

export function updateActionPoints(id: number, data: { points?: number; actionName?: string; status?: number }) {
  return request.put<any, { code: number }>(`/admin/action-points/${id}`, data)
}

export function createActionPoints(data: { actionKey: string; actionName: string; points: number; status: number }) {
  return request.post<any, { code: number }>('/admin/action-points/create', data)
}
