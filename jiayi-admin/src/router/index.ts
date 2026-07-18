import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue')
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/dashboard',
    children: [
      { path: 'dashboard', name: 'Dashboard', component: () => import('@/views/dashboard/index.vue'), meta: { title: '仪表盘' } },
      { path: 'product', name: 'Product', component: () => import('@/views/product/index.vue'), meta: { title: '商品管理' } },
      { path: 'order', name: 'Order', component: () => import('@/views/order/index.vue'), meta: { title: '订单管理' } },
      { path: 'user', name: 'User', component: () => import('@/views/user/index.vue'), meta: { title: '会员管理' } },
      { path: 'level', name: 'Level', component: () => import('@/views/level/index.vue'), meta: { title: '等级配置' } },
      { path: 'content', name: 'Content', component: () => import('@/views/content/index.vue'), meta: { title: '内容管理' } },
      { path: 'settings', name: 'Settings', component: () => import('@/views/settings/index.vue'), meta: { title: '系统设置' } }
    ]
  }
]

const router = createRouter({ history: createWebHistory(), routes })

router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
