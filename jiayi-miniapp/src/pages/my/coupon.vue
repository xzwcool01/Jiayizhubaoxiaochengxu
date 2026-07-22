<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getMyCoupons, type UserCouponVO } from '@/api/coupon'

const list = ref<UserCouponVO[]>([])
const loading = ref(false)

function checkLogin(): boolean {
  if (uni.getStorageSync('token')) return true
  uni.showToast({ title: '请先登录', icon: 'none' })
  uni.switchTab({ url: '/pages/my/my' })
  return false
}

async function fetchData() {
  if (!checkLogin()) return
  loading.value = true
  try {
    const res = await getMyCoupons()
    if (res.code === 200) list.value = res.data || []
  } catch { uni.showToast({ title: '加载失败', icon: 'none' }) }
  finally { loading.value = false }
}

function getAmountDesc(c: UserCouponVO): string {
  if (c.type === 0) {
    const parts: string[] = []
    if (c.minAmount > 0) parts.push('满¥' + c.minAmount.toLocaleString())
    parts.push('减¥' + c.value.toLocaleString())
    if (c.maxAmount > 0) parts.push('至高减¥' + c.maxAmount.toLocaleString())
    return parts.join('')
  }
  const rate = c.value / 10
  return rate % 1 === 0 ? rate + '折' : rate + '折'
}

function getMinDesc(c: UserCouponVO): string {
  if (c.type === 0) {
    return c.minAmount > 0 ? '满¥' + c.minAmount.toLocaleString() + '可用' : '无门槛'
  }
  return c.minAmount > 0 ? '满¥' + c.minAmount.toLocaleString() + '可用' : '无门槛'
}

onShow(fetchData)
</script>

<template>
  <view class="page">
    <view class="top-bar">
      <view class="back-btn" @tap="uni.navigateBack()"><text class="back-icon">←</text></view>
      <text class="page-title">我的优惠券</text>
    </view>

    <view v-if="loading" class="loading-mask"><text>加载中...</text></view>
    <view v-else-if="!list.length" class="empty"><text class="empty-text">暂无优惠券</text></view>
    <scroll-view v-else class="list" scroll-y>
      <view v-for="c in list" :key="c.id" class="coupon-card">
        <view class="coupon-left">
          <text class="coupon-value">{{ getAmountDesc(c) }}</text>
          <text class="coupon-min">{{ getMinDesc(c) }}</text>
        </view>
        <view class="coupon-right">
          <text class="coupon-name">{{ c.name }}</text>
        </view>
      </view>
    </scroll-view>
  </view>
</template>

<style scoped>
.page { background: #FAFAF8; min-height: 100vh; display: flex; flex-direction: column; }
.top-bar { display: flex; align-items: center; padding: calc(var(--status-bar-height) + 28rpx) 32rpx 0; height: 88rpx; flex-shrink: 0; }
.back-icon { font-size: 36rpx; color: #775836; }
.back-btn { width: 60rpx; }
.page-title { font-size: 36rpx; font-weight: 600; color: #1C1B1B; margin-left: 16rpx; }
.list { flex: 1; padding: 32rpx 24rpx 32rpx; overflow-x: hidden; box-sizing: border-box; }
.loading-mask, .empty { display: flex; align-items: center; justify-content: center; height: 400rpx; }
.empty-text, .loading-mask text { font-size: 28rpx; color: #999; }
.coupon-card { display: flex; background: #fff; border-radius: 20rpx; margin-bottom: 20rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.05); }
.coupon-left { width: 200rpx; flex-shrink: 0; background: linear-gradient(135deg, #775836, #9A7B5A); display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 20rpx; box-sizing: border-box; }
.coupon-value { font-size: 30rpx; font-weight: bold; color: #FFDDA9; text-align: center; word-break: break-all; }
.coupon-min { font-size: 20rpx; color: rgba(255,255,255,0.75); margin-top: 8rpx; text-align: center; }
.coupon-right { flex: 1; display: flex; flex-direction: column; justify-content: center; padding: 24rpx; min-width: 0; box-sizing: border-box; }
.coupon-name { font-size: 28rpx; font-weight: 500; color: #1C1B1B; word-break: break-all; }
</style>
