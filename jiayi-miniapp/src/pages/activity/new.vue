<script setup lang="ts">
import MsIcon from '@/components/MsIcon.vue'
const products = [
  { name: '朝露玫瑰金戒指', price: '3,280', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDKICPrZNcUy4oeFOwQTw4q6EqIMRV3ooNbTqBMRxEWcYAYCkFs1Ns8gWMzadJF1olwG9uJ98Afuvj8hQSbabE5HUeTudwm_h2X0Qx3wX9Xnlj6PISBb1DomWBOXPJweWTL2v9Q8EP4AwHfjzrF1xYtNbsZMadRW5sHJunOWWnGAgNgnqWRALo8dpTTQLCsVqH0AKNt8tyh9NncznaETsZC_uB5jdYnBnrP4PDeJOiSduaBExJOvFhDsQ', ratio: '3/4' },
  { name: '月光泪珍珠耳坠', price: '2,150', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBIwA8TT2tD3-Z6rlUhnOJsvpe2FPzED2gzzeX5S8Vv83xUL3jWjGJ1q6t_dYxafwsAnSUkoYQleM1MPbvXuavkb1FLIGHT5MDmMrrIUDgtSehdiE7tWjMjMbuAF9eJke-VUu2FyT7CHsjE1LvZxMmqutyeDTJAdaJEescJA_GWn0yDof0Blm8go0LU526MKVCJ4fBRFuE3MxLtKc9luxinC8_3-i5hxuDWzLfQg8svD8HxPA6KUY9GmQ', ratio: '3/4' },
  { name: '筑梦系列金质手镯', price: '5,800', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDZ9JOtcK6hpm3zqYWNKGZbXu42xx2ga_IdaHTtMJeFDZSFbey5udqKB8hMT3Ues00D6fuZkINv1SZNBzJNanfx9qZIZsZftU_FDCwiM3yTSYtyxdwDVXAA4fW3n7sXqDykLht6w7ZmvOEkKgsoFsOUsF_GZJKOCOboo1Dgwyp2q4gNwqJ83Yxw7umvPj5sKE71NNEys26b6UTw4Gpo4hmXGNSZyJvnGl-nRAY6UGYQM-0ibivHnWa3Pw', ratio: '1/1' },
  { name: '深海之眸蓝宝石项链', price: '12,600', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAOkCdpS63hazPHyLJOX4AXFioEy9NL4SH2Fh3K5ssWXDQ4wNodBMc2QS9BDUrcA_AwApLeTL5mneQTFAC3kye3FQqdwF1tVOgs0VlOMobIuJv7lZHyWr2GxDq10X07pH9fGbcEHXe6PsAkyy7uym4NCDVcSxcajZbBkNeVW2hJ2tkxW7hyS8UbGbZRmoqBYQOzhUU9qDIoFXDlHJutHgpcOIh6j8mMYGyKJIdoUBPbvYTSX1ZXG2qNjg', ratio: '3/4' }
]
</script>

<template>
  <view class="page">
    <view class="top-bar">
      <view class="back-btn" @tap="uni.navigateBack()"><MsIcon name="arrow_back" size="40rpx" color="#1C1B1B" /></view>
      <text class="top-title">嘉怡珠宝</text>
    </view>
    <view class="hero">
      <view class="hero-bg" :style="'background-image: url(' + products[0].img + ')'" />
      <view class="hero-overlay" />
      <view class="hero-text">
        <text class="hero-tag">Collection of the Month</text>
        <text class="hero-title">鎏金岁月系列</text>
        <text class="hero-desc">以时光为笔，绘就永恒的优雅。</text>
        <view class="hero-btn"><text>立即探索</text></view>
      </view>
    </view>
    <view class="section">
      <view class="section-header">
        <view><text class="section-title">新品首发</text><text class="section-sub">New Arrivals / Autumn 2024</text></view>
        <MsIcon name="filter_list" size="40rpx" color="#775836" />
      </view>
      <view class="grid">
        <view v-for="(p, i) in products" :key="i" class="product-card" @tap="uni.navigateTo({url:'/pages/product/detail'})">
          <view class="product-img-wrap">
            <image class="product-img" :style="'height: ' + (p.ratio === '3/4' ? '420rpx' : '300rpx')" :src="p.img" mode="aspectFill" />
            <view class="product-new">New</view>
          </view>
          <text class="product-name">{{ p.name }}</text>
          <text class="product-price">¥ {{ p.price }}</text>
        </view>
      </view>
    </view>
    <view class="quote">
      <MsIcon name="format_quote" size="56rpx" color="#E8BF95" />
      <text class="quote-text">"珠宝不仅是装饰，更是佩戴者内心世界的低语。"</text>
      <view class="quote-line" />
    </view>
    <view class="ai-teaser">
      <view class="teaser-left">
        <view class="teaser-icon"><MsIcon name="auto_awesome" size="48rpx" color="#fff" /></view>
        <view><text class="teaser-title">AI 智能试戴</text><text class="teaser-desc">上传照片，即刻预览佩戴效果 ✨</text></view>
      </view>
      <view class="teaser-btn" @tap="uni.navigateTo({url:'/pages/ai-wear/index'})"><text>开启</text></view>
    </view>
  </view>
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; }
.top-bar { display: flex; align-items: center; gap: 16rpx; padding: 32rpx; height: 112rpx; background-color: rgba(252,249,248,0.8); }
.top-title { font-size: 48rpx; color: #775836; font-weight: 600; font-family: 'Noto Serif SC', serif; }
.hero { height: 70vh; width: 100%; position: relative; overflow: hidden; }
.hero-bg { position: absolute; inset: 0; background-size: cover; background-position: center; }
.hero-overlay { position: absolute; inset: 0; background: linear-gradient(to top, #FAFAF8, transparent); opacity: 0.6; }
.hero-text { position: absolute; bottom: 64rpx; left: 0; width: 100%; display: flex; flex-direction: column; align-items: center; text-align: center; padding: 0 32rpx; }
.hero-tag { font-size: 24rpx; color: #775836; letter-spacing: 4px; text-transform: uppercase; }
.hero-title { font-size: 64rpx; font-weight: 700; color: #1C1B1B; font-family: 'Noto Serif SC', serif; }
.hero-desc { font-size: 24rpx; color: #605E5A; max-width: 560rpx; margin-top: 16rpx; }
.hero-btn { margin-top: 32rpx; background-color: #775836; color: #fff; padding: 16rpx 64rpx; border-radius: 60rpx; font-size: 24rpx; text-transform: uppercase; letter-spacing: 2px; }
.section { padding: 48rpx 32rpx; }
.section-header { display: flex; justify-content: space-between; align-items: baseline; margin-bottom: 32rpx; }
.section-title { font-size: 56rpx; font-weight: 600; color: #1C1B1B; font-family: 'Noto Serif SC', serif; }
.section-sub { font-size: 22rpx; color: rgba(96,94,90,0.6); }
.grid { display: grid; grid-template-columns: 1fr 1fr; gap: 32rpx; }
.product-card { }
.product-img-wrap { position: relative; border-radius: 24rpx; overflow: hidden; background-color: #fff; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); }
.product-img { width: 100%; }
.product-new { position: absolute; top: 16rpx; left: 16rpx; background-color: #CF6679; color: #fff; font-size: 18rpx; padding: 4rpx 16rpx; border-radius: 40rpx; text-transform: uppercase; }
.product-name { font-size: 28rpx; font-weight: 600; color: #1C1B1B; margin-top: 12rpx; }
.product-price { font-size: 26rpx; color: #775836; font-weight: bold; display: block; }
.quote { text-align: center; padding: 64rpx 32rpx; }
.quote-text { font-size: 32rpx; color: #605E5A; font-style: italic; display: block; margin-top: 16rpx; max-width: 600rpx; margin-left: auto; margin-right: auto; }
.quote-line { width: 64rpx; height: 2rpx; background-color: #D2C4B8; margin: 24rpx auto 0; }
.ai-teaser { display: flex; align-items: center; justify-content: space-between; background-color: #E6E2DD; margin: 0 32rpx 48rpx; padding: 32rpx; border-radius: 32rpx; }
.teaser-left { display: flex; align-items: center; gap: 24rpx; flex: 1; }
.teaser-icon { width: 96rpx; height: 96rpx; border-radius: 50%; background-color: #775836; display: flex; align-items: center; justify-content: center; }
.teaser-title { font-size: 24rpx; font-weight: bold; color: #1C1B1B; }
.teaser-desc { font-size: 20rpx; color: #605E5A; }
.teaser-btn { background-color: #fff; color: #775836; font-size: 22rpx; font-weight: bold; padding: 12rpx 32rpx; border-radius: 60rpx; }
</style>
