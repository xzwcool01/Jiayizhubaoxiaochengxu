<script setup lang="ts">
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const store = useUserStore()

const menuItems = [
  { path: '/dashboard', icon: 'Odometer', label: '仪表盘' },
  { path: '/product', icon: 'Goods', label: '商品管理' },
  { path: '/order', icon: 'List', label: '订单管理' },
  { path: '/user', icon: 'User', label: '会员管理' },
  { path: '/level', icon: 'Coin', label: '等级配置' },
  { path: '/content', icon: 'EditPen', label: '内容管理' },
  { path: '/ai-wear-prompt', icon: 'Coin', label: 'AI试戴配置' },
  { path: '/settings', icon: 'Setting', label: '系统设置' },
  { path: '/favorite', icon: 'Star', label: '收藏管理' },
  { path: '/cart', icon: 'ShoppingCart', label: '购物车管理' },
  { path: '/coupon', icon: 'Ticket', label: '优惠券管理' },
  { path: '/points', icon: 'Coin', label: '积分规则' },
  { path: '/action-points', icon: 'Coin', label: '行为积分' },
  { path: '/review', icon: 'ChatDotSquare', label: '评价管理' },
  { path: '/address', icon: 'Location', label: '地址管理' }
]

function handleLogout() {
  store.logout()
  router.push('/login')
}
</script>
<template>
  <el-container style="height:100vh">
    <el-aside width="220px" style="background:#1d1e1f">
      <div style="height:60px; display:flex; align-items:center; justify-content:center; color:#C8A27A; font-size:18px; font-weight:bold; border-bottom:1px solid #333">
        嘉怡珠宝 · 管理
      </div>
      <el-menu :default-active="route.path" router background-color="#1d1e1f" text-color="#ccc" active-text-color="#C8A27A">
        <el-menu-item v-for="item in menuItems" :key="item.path" :index="item.path">
          <el-icon><component :is="item.icon" /></el-icon>
          <span>{{ item.label }}</span>
        </el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header style="background:#fff; border-bottom:1px solid #eee; display:flex; align-items:center; justify-content:flex-end; height:60px">
        <el-dropdown @command="handleLogout">
          <span style="cursor:pointer; color:#333">管理员 <el-icon><ArrowDown /></el-icon></span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="logout">退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </el-header>
      <el-main style="background:#f5f5f5; padding:24px">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>
