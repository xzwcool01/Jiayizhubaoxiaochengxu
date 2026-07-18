<script setup lang="ts">
import { ref } from 'vue'
import MsIcon from '@/components/MsIcon.vue'

const currentImg = ref(0)
const size = ref('')
const imgs = [
  'https://lh3.googleusercontent.com/aida-public/AB6AXuCUGJgpn3SVNhHYf5HAsXpsgbPSYKPF7BQy5HSoMXk99w4I2htRsNesAm-hH84JKFG3dByMLPcx7FqT_k0qQY3M5IGdqTLw0yO5dN0-2LNFVNW1ftfXvgFnZPmP-xdd8Y6cK7QoJL8-Sw7SDlJkDZxMMU9W3F6xH3tSPSQpQkmMfgM4FIXLxY3ILvvHRJ9U9PAYLx9BqWe86tCRYQIXH9bJMLj3F7QxEtqqA71OIGg0dE9plTT-m5sBYg',
  'https://lh3.googleusercontent.com/aida-public/AB6AXuBIwA8TT2tD3-Z6rlUhnOJsvpe2FPzED2gzzeX5S8Vv83xUL3jWjGJ1q6t_dYxafwsAnSUkoYQleM1MPbvXuavkb1FLIGHT5MDmMrrIUDgtSehdiE7tWjMjMbuAF9eJke-VUu2FyT7CHsjE1LvZxMmqutyeDTJAdaJEescJA_GWn0yDof0Blm8go0LU526MKVCJ4fBRFuE3MxLtKc9luxinC8_3-i5hxuDWzLfQg8svD8HxPA6KUY9GmQ',
  'https://lh3.googleusercontent.com/aida-public/AB6AXuCNz7F9e-301IuMu34Sr_HftxGeIrNlJF2wJCZEoCQsXj_TcI8dQd750nSPFxdZpXVzTAvZ__L439ZgNPd3hhF8BUcqMXfdoK5V4Xr65QF0IL4Xo2F6FCvkE1b7AIincjZAHBlfkW4vlix3bec87a7Ku_o8iL6vNW0fGJs5Gvo8p7dMA4M0Hg85gWwD7mlm0kScGHXJsrJeuQ-toUTqnatZn8WkDwnDuDeLUMB8-EnpdTPjL1EsbGGk8g'
]
const sizes = ['US 5', 'US 6', 'US 7', 'US 8', 'US 9']
</script>

<template>
  <view class="page">
    <view class="top-bar">
      <view class="back-btn" @tap="uni.navigateBack()"><MsIcon name="arrow_back" size="40rpx" color="#1C1B1B" /></view>
      <view class="top-actions">
        <MsIcon name="share" size="40rpx" color="#1C1B1B" />
        <MsIcon name="favorite" size="40rpx" color="#1C1B1B" />
      </view>
    </view>
    <view class="gallery">
      <image class="gallery-main" :src="imgs[currentImg]" mode="aspectFill" />
      <view class="gallery-indicator"><text>{{ currentImg + 1 }} / {{ imgs.length }}</text></view>
      <view class="gallery-thumbs">
        <image v-for="(img, i) in imgs" :key="i" :class="['thumb', i === currentImg ? 'thumb-active' : '']" :src="img" mode="aspectFill" @tap="currentImg = i" />
      </view>
    </view>
    <view class="info-section">
      <view class="info-header">
        <view><text class="info-name">朝露玫瑰金戒指</text><text class="info-sub">18K Rose Gold & Freshwater Pearl</text></view>
        <view class="info-share"><text>¥3,280</text></view>
      </view>
      <view class="info-price">¥ 3,280</view>
      <view class="info-tags">
        <view class="tag"><text>免运费</text></view>
        <view class="tag"><text>7天无理由退换</text></view>
        <view class="tag"><text>赠定制包装</text></view>
      </view>
      <view class="selector">
        <text class="selector-label">选择尺码</text>
        <view class="size-pills">
          <view v-for="(s, i) in sizes" :key="i" :class="['pill', size === s ? 'pill-active' : 'pill-inactive']" @tap="size = s"><text>{{ s }}</text></view>
        </view>
      </view>
      <view class="desc">
        <text class="desc-label">商品描述</text>
        <text class="desc-text">朝露玫瑰金戒指以初晨露珠为灵感，选用18K玫瑰金精制而成。中央镶嵌5-6mm天然淡水珍珠，光泽温润如朝露般晶莹剔透。戒臂采用流线型设计，优雅贴合指间曲线。无论是日常通勤还是晚宴派对，都是低调又精致的点睛之作。</text>
      </view>
      <view class="specs">
        <view class="spec-row"><text class="spec-key">材质</text><text class="spec-val">18K 玫瑰金</text></view>
        <view class="spec-row"><text class="spec-key">主石</text><text class="spec-val">天然淡水珍珠 5-6mm</text></view>
        <view class="spec-row"><text class="spec-key">副石</text><text class="spec-val">天然钻石 0.03ct</text></view>
        <view class="spec-row"><text class="spec-key">工艺</text><text class="spec-val">手工微镶</text></view>
        <view class="spec-row"><text class="spec-key">产地</text><text class="spec-val">中国 · 深圳</text></view>
      </view>
    </view>
    <view class="reviews">
      <view class="review-header"><text class="review-title">用户评价 (128)</text><text class="review-link">查看全部</text></view>
      <view class="review-item">
        <view class="reviewer"><text class="reviewer-name">C.**</text><text class="reviewer-date">2024.07.10</text></view>
        <view class="review-stars"><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /></view>
        <text class="review-text">非常精致的戒指，实物比图片更有质感。珍珠的光泽非常温润，戴上去也很合适</text>
      </view>
      <view class="review-item">
        <view class="reviewer"><text class="reviewer-name">L***y</text><text class="reviewer-date">2024.07.08</text></view>
        <view class="review-stars"><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /><MsIcon name="star" size="28rpx" color="#E9C349" /></view>
        <text class="review-text">送朋友的结婚礼物，包装非常精美，朋友很喜欢。玫瑰金很显白。</text>
      </view>
    </view>
    <view class="bottom-bar">
      <view class="bar-left">
        <view class="bar-icon"><MsIcon name="chat" size="36rpx" color="#1C1B1B" /></view>
        <view class="bar-icon"><MsIcon name="favorite" size="36rpx" color="#1C1B1B" /></view>
      </view>
      <view class="bar-btn secondary" @tap="uni.switchTab({url:'/pages/cart/cart'})"><text>加入购物车</text></view>
      <view class="bar-btn primary"><text>立即购买</text></view>
    </view>
  </view>
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; padding-bottom: calc(120rpx + env(safe-area-inset-bottom)); }
.top-bar { display: flex; align-items: center; justify-content: space-between; padding: 32rpx; height: 112rpx; }
.top-actions { display: flex; gap: 16rpx; }
.gallery { padding: 0 32rpx; }
.gallery-main { width: 100%; height: 560rpx; border-radius: 24rpx; }
.gallery-indicator { text-align: center; margin-top: 8rpx; font-size: 22rpx; color: #605E5A; }
.gallery-thumbs { display: flex; gap: 16rpx; margin-top: 16rpx; justify-content: center; }
.thumb { width: 120rpx; height: 120rpx; border-radius: 16rpx; border: 4rpx solid transparent; }
.thumb-active { border-color: #775836; }
.info-section { padding: 48rpx 32rpx; }
.info-header { }
.info-name { font-size: 40rpx; font-weight: 600; color: #1C1B1B; font-family: 'Noto Serif SC', serif; }
.info-sub { font-size: 22rpx; color: #605E5A; display: block; margin-top: 4rpx; }
.info-price { font-size: 56rpx; font-weight: 700; color: #775836; margin-top: 16rpx; }
.info-tags { display: flex; gap: 16rpx; margin-top: 24rpx; }
.tag { background-color: #F0EBE7; font-size: 20rpx; color: #775836; padding: 8rpx 20rpx; border-radius: 40rpx; }
.selector { margin-top: 32rpx; }
.selector-label { font-size: 24rpx; font-weight: 600; color: #1C1B1B; }
.size-pills { display: flex; gap: 24rpx; margin-top: 16rpx; }
.pill { width: 100rpx; height: 100rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; font-size: 24rpx; }
.pill-active { background-color: #775836; color: #fff; }
.pill-inactive { background-color: #F0EBE7; color: #605E5A; }
.desc { margin-top: 48rpx; }
.desc-label { font-size: 28rpx; font-weight: 600; color: #1C1B1B; display: block; margin-bottom: 12rpx; }
.desc-text { font-size: 24rpx; color: #4F453C; line-height: 1.6; }
.specs { margin-top: 32rpx; }
.spec-row { display: flex; justify-content: space-between; padding: 16rpx 0; border-bottom: 2rpx solid rgba(217,210,204,0.3); }
.spec-key { font-size: 24rpx; color: #605E5A; }
.spec-val { font-size: 24rpx; color: #1C1B1B; }
.reviews { padding: 0 32rpx 48rpx; }
.review-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; }
.review-title { font-size: 32rpx; font-weight: 600; color: #1C1B1B; }
.review-link { font-size: 22rpx; color: #775836; }
.review-item { background-color: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; }
.reviewer { display: flex; justify-content: space-between; }
.reviewer-name { font-size: 22rpx; font-weight: 600; color: #1C1B1B; }
.reviewer-date { font-size: 18rpx; color: #605E5A; }
.review-stars { margin: 8rpx 0; }
.review-text { font-size: 24rpx; color: #4F453C; line-height: 1.5; }
.bottom-bar { position: fixed; bottom: 0; left: 0; width: 100%; background-color: #fff; padding: 16rpx 32rpx; display: flex; align-items: center; gap: 24rpx; box-shadow: 0 -4rpx 16rpx rgba(0,0,0,0.06); }
.bar-left { display: flex; gap: 8rpx; }
.bar-icon { width: 80rpx; height: 80rpx; border-radius: 50%; background-color: #F0EBE7; display: flex; align-items: center; justify-content: center; }
.bar-btn { flex: 1; text-align: center; padding: 24rpx; border-radius: 16rpx; font-size: 28rpx; font-weight: bold; }
.bar-btn.primary { background-color: #775836; color: #fff; }
.bar-btn.secondary { border: 2rpx solid #D9D2CC; color: #775836; }
</style>
