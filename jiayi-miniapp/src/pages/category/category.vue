<script setup lang="ts">
import { ref } from 'vue'
import NavBar from '@/components/NavBar.vue'

const categories = ['全部', '戒指', '项链', '耳饰', '手链', '手镯', '套装']
const activeCat = ref(0)

const products = [
  { name: '晨露系列 18K金钻戒', price: '12,800', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAlnhHLw4txewdhBdR2wJhYPECF2UwGYeokQUHcEPkyHbYuJHUtjEvgQOSAKjkw9U1BrolVqARxLWID4gmr3kS6Kzme41PVOuXiAge0qkLHNxz6COcYistxQexmFYNkes7bh8ow-JxV_T2sKBFi-8_xwB4692yJ28nqUuu0saaYh9WcZT3vY4rsYkzqMscYpuVCZNzZHnUBb_hRdU0O-y3GMBysDnSlUyJq8B6QotXwLpRpR8y879Ik5A' },
  { name: '初雪珍珠 925银吊坠', price: '2,180', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuClDfiaTZTjJjwy523Bl_ZEXmxUO7y0d9FC9qW0LSqaYsLS5c56jtpP14UVHetVJfH65neomh_0AnwBVwC_kBwYOfukDZmWW9n6nNV_3Uo6RmXgtnSeIR34Xf5-8O5MD3LpFCcysELomIFs30pO4FXCr3VUVlRo5Kz-jlpLERMU7r52Z7py2NrQ6AZXBXbWW6AM9Y3hPSelHUtPT3Ge369BZw2lEsw9K3dd4GWxlkNRkpLhX3m_tCtp4A' },
  { name: '建筑美学 几何耳钉', price: '4,560', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuD6OJNu3YRwDRxlEOBQtVYuD5otc7UOK9vyaiqqsZlMLPnLmZ_N7k05HurYluc2Wousp4n73XmRk1KAcFhMJvvTNrXKAPL3UgQ6RFO8uTIZ5WUeUEb3ddYkc9mECbXK4m1JSRr09GMkFwJotttTSCovkqcWINQ1fWuIB54roTdOIgsvl8tzJzGGG-3IjXvK6r6svBH2oH_ULd5eb5uOJQT_MDNVaM58FcULlzldFL4drIUm-qUf7Trr-g' },
  { name: '翡翠时光 纤细手链', price: '8,900', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDc0YcyRmnq6Fob6k6KwuMUFOUA3tlNPiS6e5drDWf2qdgoz-tPZ4FwMBDMUI2A1bT3mjmNGMBQ1A0zCZeQHFy3b1RNWZkW3KhhzSdUs6hDvqezy1TFRSuXMGe13OB-x0YOCEx4ahE6p_PZLMFaVLoPhZ_BuYrfi6pgtcecDmQEUu4kZHgooP3Uf9oTQpCBg4Fp3w61BW56yOwVWHwlG6aDcfFz13TosxkYzs7rCDdJDqNuPGfuLr28Ug' }
]

function goDetail() { uni.navigateTo({ url: '/pages/product/detail' }) }


</script>

<template>
  <view class="page">
    <view class="top-bar"><text class="top-title">嘉怡珠宝</text></view>
    <view class="search-bar">
      <text class="search-icon">search</text>
      <input class="search-input" placeholder="搜索奢华珠宝..." />
    </view>
    <view class="content">
      <scroll-view class="sidebar" scroll-y enhanced show-scrollbar="false">
        <view v-for="(cat, i) in categories" :key="i" :class="['cat-item', i === activeCat ? 'cat-active' : '']" @tap="activeCat = i">
          <view v-if="i === activeCat" class="cat-indicator" />
          <text :class="['cat-text', i === activeCat ? 'cat-text-active' : '']">{{ cat }}</text>
        </view>
      </scroll-view>
      <scroll-view class="right-panel" scroll-y enhanced>
        <view class="grid">
          <view v-for="(p, i) in products" :key="i" class="product-card" @tap="goDetail">
            <image class="product-img" :src="p.img" mode="aspectFill" />
            <view class="product-like"><text class="like-icon">favorite</text></view>
            <view class="product-info">
              <text class="product-name">{{ p.name }}</text>
              <view class="product-price"><text style="font-size:24rpx">¥</text><text style="font-size:32rpx; font-weight:bold"> {{ p.price }}</text></view>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>
    <view class="filter-btn"><text class="filter-icon">tune</text></view>
  </view>

  <NavBar :active="1" />
</template>

<style scoped>
.page { background-color: #FAFAF8; height: 100vh; display: flex; flex-direction: column; padding-bottom: 160rpx; box-sizing: border-box; }
.top-bar { display: flex; align-items: center; justify-content: center; height: 112rpx; background-color: rgba(252,249,248,0.8); }
.top-title { font-size: 56rpx; color: #775836; font-weight: 600; font-family: 'Noto Serif SC', serif; }
.search-bar { padding: 16rpx 32rpx; background-color: #FCF9F8; display: flex; align-items: center; }
.search-icon { font-family: 'Material Symbols Outlined'; font-size: 32rpx; color: #81756A; position: absolute; margin-left: 48rpx; z-index: 1; }
.search-input { width: 100%; height: 80rpx; background-color: #F6F3F2; border-radius: 100rpx; padding-left: 88rpx; padding-right: 32rpx; font-size: 28rpx; border: none; }
.content { display: flex; flex: 1; overflow: hidden; }
.sidebar { width: 192rpx; background-color: #fff; border-right: 2rpx solid rgba(210,196,184,0.3); }
.cat-item { position: relative; padding: 48rpx 16rpx; text-align: center; }
.cat-indicator { position: absolute; left: 0; top: 50%; transform: translateY(-50%); width: 6rpx; height: 32rpx; background-color: #775836; border-radius: 0 8rpx 8rpx 0; }
.cat-text { font-size: 24rpx; color: #605E5A; }
.cat-text-active { color: #775836; font-weight: bold; }
.right-panel { flex: 1; padding: 32rpx; }
.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24rpx; }
.product-card { background-color: #fff; border-radius: 16rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); position: relative; }
.product-img { width: 100%; height: 340rpx; }
.product-like { position: absolute; top: 16rpx; right: 16rpx; width: 64rpx; height: 64rpx; border-radius: 50%; background-color: rgba(255,255,255,0.8); display: flex; align-items: center; justify-content: center; }
.like-icon { font-family: 'Material Symbols Outlined'; font-size: 36rpx; color: #775836; }
.product-info { padding: 24rpx; }
.product-name { font-size: 28rpx; color: #1C1B1B; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.product-price { display: flex; align-items: baseline; gap: 4rpx; margin-top: 8rpx; color: #775836; }
.filter-btn { position: fixed; right: 48rpx; bottom: 200rpx; width: 96rpx; height: 96rpx; border-radius: 50%; background-color: #775836; color: #fff; display: flex; align-items: center; justify-content: center; box-shadow: 0 8rpx 24rpx rgba(119,88,54,0.3); z-index: 40; }
.filter-icon { font-family: 'Material Symbols Outlined'; font-size: 40rpx; }
</style>
