<script setup lang="ts">
import { ref, onMounted } from 'vue'

const props = withDefaults(defineProps<{ active?: number }>(), { active: 0 })
const navActive = ref(props.active)

const navTabs = [
  { text: '首页', icon: 'home', page: '/pages/index/index' },
  { text: '分类', icon: 'category', page: '/pages/category/category' },
  { text: '发现', icon: 'auto_awesome', page: '/pages/discovery/discovery' },
  { text: '购物车', icon: 'shopping_cart', page: '/pages/cart/cart' },
  { text: '我的', icon: 'person', page: '/pages/my/my' },
]
const navPaths: Record<string, string> = {
  home: 'M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z',
  category: 'M3 3v8h8V3H3zm6 6H5V5h4v4zm-6 4v8h8v-8H3zm6 6H5v-4h4v4zm4-16v8h8V3h-8zm6 6h-4V5h4v4zm-6 4v8h8v-8h-8zm6 6h-4v-4h4v4z',
  auto_awesome: 'M19 9l1.25-2.75L23 5l-2.75-1.25L19 1l-1.25 2.75L15 5l2.75 1.25L19 9zm-7.5.5L9 4 6.5 9.5 1 12l5.5 2.5L9 20l2.5-5.5L17 12l-5.5-2.5zM19 15l-1.25 2.75L15 19l2.75 1.25L19 23l1.25-2.75L23 19l-2.75-1.25z',
  shopping_cart: 'M7 18c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm10 0c-1.1 0-2 .9-2 2s.9 2 2 2 2-.9 2-2-.9-2-2-2zm-11.22-3h11.44c.83 0 1.54-.5 1.84-1.22l3.5-7.51A1.002 1.002 0 0021 5H5.21L3.53 2H1v2h2l3.6 7.59-1.35 2.45C5.01 14.23 5.6 15 6.42 15h12.58v-2H7.42c-.14 0-.25-.11-.25-.25l.03-.12.9-1.63z',
  person: 'M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z',
}

function navIcon(name: string, active: boolean): string {
  const d = navPaths[name]
  if (!d) return ''
  const svg = active
    ? `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="${d}" fill="#775836"/></svg>`
    : `<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24"><path d="${d}" fill="none" stroke="#605E5A" stroke-width="2" stroke-linejoin="round" stroke-linecap="round"/></svg>`
  return 'data:image/svg+xml;charset=utf-8,' + encodeURIComponent(svg)
}

function onNavTap(idx: number) {
  if (idx === navActive.value) return
  uni.switchTab({ url: navTabs[idx].page })
}

onMounted(() => { navActive.value = props.active })
</script>

<template>
  <view class="nav-bar">
    <view
      v-for="(tab, idx) in navTabs"
      :key="idx"
      class="nav-item"
      :class="{ 'nav-item-active': navActive === idx }"
      @tap="onNavTap(idx)"
    >
      <image class="nav-icon" :src="navIcon(tab.icon, navActive === idx)" mode="scaleToFill" />
      <text class="nav-label" :class="{ 'nav-label-active': navActive === idx }">{{ tab.text }}</text>
    </view>
  </view>
</template>

<style scoped>
.nav-bar {
  position: fixed; left: 50%; bottom: 32rpx; transform: translateX(-50%);
  width: calc(100% - 64rpx); max-width: 640rpx; z-index: 999;
  background: rgba(252,249,248,0.92); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px);
  border-radius: 100rpx; border: 2rpx solid rgba(210,196,184,0.35);
  display: flex; align-items: center; justify-content: space-around;
  padding: 12rpx 24rpx; box-sizing: border-box;
  box-shadow: 0 4rpx 24rpx rgba(0,0,0,0.06);
}
.nav-item { flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 4rpx 0; }
.nav-icon { width: 48rpx; height: 48rpx; }
.nav-label { font-size: 20rpx; color: #605E5A; margin-top: 4rpx; font-weight: 400; line-height: 1.2; }
.nav-label-active { color: #775836; font-weight: 700; }
</style>
