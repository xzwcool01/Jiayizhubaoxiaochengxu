<script setup lang="ts">
import NavBar from '@/components/NavBar.vue'
const cartItems = [
  { name: '星芒系列 · 钻戒', spec: '18K白金 / 50分 / 12号', price: '12,800.00', qty: 1, img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDHKoMjQjLvRNxgGAwZtB9fZqONz6PvyP8wWDQA-HGUd6wVRDDI126_IZdoL7HXZkEraHheoVdS7kBDWy32Ex1LzQ7VZ6VSk6ahqgkA9kdj_lVV5uN46KshykGpv4f8axbb-EpPaQMDhCXGU1AuIcr7YrFebGMl8nzXthID37pWD_0z6B0IX-5lTm3oPLAmZz9J_byGujzIVVsfwLSji2kSSiYBcAMeJOL73UvBW06SE1bd66Kwmm5yOQ' },
  { name: '月光泪 · 蓝宝石项链', spec: '18K黄金 / 0.8克拉', price: '8,500.00', qty: 1, img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCDBjVBBNKFvbEcqM12vQB4dzloLk0bAORJsVnp8P0kOQU2fi4YbWmT7OcdIwaPS_xPPDZNbEfsDaUYZXeh95eAbJjtN--OL2ZIAQwudLaM79bLFayB83f_PzUsKYaQ54PiavGq3dtKV6-RcaRtvagUk_bhE5axr2CXWuXVKpBeGp4WOhLfn-k8ClgPId2XUu2SOOu4sn48j_q0w9w7OQM_7ANIKNSD0kJexXjElhNzJOaYeMZNJKKC4Q' },
  { name: '初露 · 珍珠耳饰', spec: 'Akoya海水珍珠 / 8mm', price: '4,280.00', qty: 1, img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCwmhOO0omlnXEkb_yoPvN5e2wp3MS1l2aJBL_v7YYA2xjd5vjeJyc1y1I-rEdklQF0KCl0YKP_w1IdbEXQgHyvB7ggF4gKXygKZp1VBI7Ax8UsDKAR92CV8YioAbRWVuqWTDRZX5yHfzUD3ePdngexajDbKgmS_i_S3lDEeQ-GVIAmwvxf_NmNhA6af7YRJW6NG974XLvIqcQ0TL1sHvGoeIe7zKuxDanD2jR00f5McokvNGTR2EoY3w' }
]

function goDetail() { uni.navigateTo({ url: '/pages/product/detail' }) }

</script>

<template>
  <view class="page">
    <view class="top-bar"><text class="top-title">购物车</text></view>
    <scroll-view class="list" scroll-y>
      <view v-for="(item, i) in cartItems" :key="i" class="cart-item">
        <view class="item-checkbox" />
        <image class="item-img" :src="item.img" mode="aspectFill" />
        <view class="item-info">
          <text class="item-name">{{ item.name }}</text>
          <text class="item-spec">{{ item.spec }}</text>
          <view class="item-bottom">
            <text class="item-price">¥{{ item.price }}</text>
            <view class="item-qty">
              <text class="qty-btn">−</text>
              <text class="qty-num">{{ item.qty }}</text>
              <text class="qty-btn">+</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <view class="recommend-section">
      <view class="rec-header">
        <text class="rec-title">你可能还喜欢</text>
        <text class="rec-more">查看更多 ›</text>
      </view>
      <scroll-view class="rec-scroll" scroll-x enhanced show-scrollbar="false">
        <view v-for="(item, i) in cartItems" :key="'r'+i" class="rec-card" @tap="goDetail">
          <image class="rec-img" :src="item.img" mode="aspectFill" />
          <text class="rec-name">{{ item.name }}</text>
          <text class="rec-price">¥{{ item.price }}</text>
        </view>
      </scroll-view>
    </view>

    <view class="bottom-bar">
      <view class="total">
        <text class="total-label">合计: </text>
        <text class="total-price">¥{{ (12800 + 8500 + 4280).toLocaleString() }}.00</text>
      </view>
      <view class="checkout-btn"><text class="checkout-text">结算 (3)</text></view>
    </view>
  </view>

  <NavBar :active="3" />
</template>

<style scoped>
.page { background-color: #FAFAF8; height: 100vh; display: flex; flex-direction: column; padding-bottom: 160rpx; box-sizing: border-box; }
.top-bar { display: flex; align-items: center; justify-content: flex-start; height: 112rpx; padding: 0 32rpx; background-color: rgba(255,255,255,0.8); }
.top-title { font-size: 40rpx; font-weight: 600; color: #1C1B1B; }
.list { flex: 1; padding: 24rpx; }
.cart-item { display: flex; align-items: center; gap: 24rpx; background-color: #fff; border-radius: 24rpx; padding: 24rpx; margin-bottom: 16rpx; }
.item-checkbox { width: 36rpx; height: 36rpx; border-radius: 50%; border: 3rpx solid #775836; background-color: #775836; }
.item-img { width: 160rpx; height: 160rpx; border-radius: 16rpx; flex-shrink: 0; }
.item-info { flex: 1; display: flex; flex-direction: column; justify-content: space-between; height: 160rpx; min-width: 0; }
.item-name { font-size: 28rpx; color: #1C1B1B; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.item-spec { font-size: 22rpx; color: #605E5A; }
.item-bottom { display: flex; justify-content: space-between; align-items: center; }
.item-price { font-size: 30rpx; color: #775836; font-weight: bold; }
.item-qty { display: flex; align-items: center; border: 2rpx solid #D9D2CC; border-radius: 40rpx; }
.qty-btn { width: 48rpx; height: 48rpx; display: flex; align-items: center; justify-content: center; font-size: 28rpx; color: #605E5A; }
.qty-num { width: 56rpx; text-align: center; font-size: 24rpx; font-weight: bold; }
.recommend-section { padding: 32rpx 24rpx; }
.rec-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; }
.rec-title { font-size: 34rpx; font-weight: 600; color: #1C1B1B; }
.rec-more { font-size: 22rpx; color: #605E5A; }
.rec-scroll { display: flex; }
.rec-card { width: 280rpx; margin-right: 24rpx; flex-shrink: 0; }
.rec-img { width: 280rpx; height: 360rpx; border-radius: 24rpx; }
.rec-name { font-size: 26rpx; color: #1C1B1B; margin-top: 8rpx; }
.rec-price { font-size: 24rpx; color: #775836; font-weight: bold; }
.bottom-bar { position: fixed; bottom: 120rpx; left: 32rpx; right: 32rpx; display: flex; align-items: center; justify-content: space-between; background-color: rgba(255,255,255,0.9); border-radius: 60rpx; padding: 16rpx 24rpx; box-shadow: 0 4rpx 24rpx rgba(0,0,0,0.08); }
.total-label { font-size: 24rpx; color: #1C1B1B; }
.total-price { font-size: 32rpx; color: #775836; font-weight: bold; }
.checkout-btn { background-color: #775836; padding: 20rpx 56rpx; border-radius: 60rpx; }
.checkout-text { color: #fff; font-size: 28rpx; font-weight: bold; }

</style>
