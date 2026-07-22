<script setup lang="ts">
import { ref } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { getDelivery, getDeliveryTrack, type DeliveryVO } from '@/api/order'

const delivery = ref<DeliveryVO | null>(null)
const routes = ref<any[]>([])
const loaded = ref(false)

onShow(() => {
  const pages = getCurrentPages()
  const page = pages[pages.length - 1] as any
  const orderId = page.$page?.options?.orderId || page.options?.orderId
  if (orderId) load(orderId)
})

async function load(orderId: number) {
  const [delRes, trackRes] = await Promise.all([
    getDelivery(orderId),
    getDeliveryTrack(orderId).catch(() => null)
  ])
  if (delRes.code === 200 && delRes.data) {
    delivery.value = delRes.data
  }
  if (trackRes?.code === 200 && trackRes.data?.routes) {
    routes.value = trackRes.data.routes
  }
  loaded.value = true
}

function copyNo() {
  if (delivery.value?.trackingNo) {
    uni.setClipboardData({ data: delivery.value.trackingNo })
  }
}

function formatTrackTime(t?: string) {
  if (!t) return ''
  return t.replace('T', ' ').substring(0, 19)
}
</script>

<template>
  <view class="page">
    <view class="top-bar">
      <view class="back-btn" @tap="uni.navigateBack()"><text>←</text></view>
      <text class="page-title">物流详情</text>
    </view>

    <view v-if="!loaded" class="loading"><text>加载中...</text></view>

    <view v-else-if="!delivery" class="empty"><text>暂无物流信息</text></view>

    <view v-else class="content">
      <view class="card">
        <view class="card-row">
          <text class="card-label">快递公司</text>
          <text class="card-value">{{ delivery.expressCompany }}</text>
        </view>
        <view class="card-row">
          <text class="card-label">运单号</text>
          <view class="tracking-row">
            <text class="card-value tracking-no">{{ delivery.trackingNo }}</text>
            <text class="copy-btn" @tap="copyNo">复制</text>
          </view>
        </view>
        <view class="card-row">
          <text class="card-label">物流状态</text>
          <text :class="['card-value', delivery.status === 1 ? 'received' : '']">
            {{ delivery.status === 1 ? '已签收' : '已发货' }}
          </text>
        </view>
        <view class="card-row">
          <text class="card-label">发货时间</text>
          <text class="card-value">{{ delivery.shippedAt?.replace('T', ' ') || '-' }}</text>
        </view>
        <view class="card-row" v-if="delivery.receivedAt">
          <text class="card-label">签收时间</text>
          <text class="card-value">{{ delivery.receivedAt?.replace('T', ' ') }}</text>
        </view>
      </view>

      <view v-if="routes.length" class="card" style="margin-top:20rpx">
        <view style="font-size:28rpx;font-weight:600;margin-bottom:20rpx;color:#1C1B1B">物流轨迹</view>
        <view class="route-list">
          <view v-for="(r, i) in routes" :key="i" class="route-item" :class="{ first: i === 0 }">
            <view class="route-dot" :class="{ active: i === 0 }"></view>
            <view class="route-info">
              <text class="route-remark">{{ r.remark || r.acceptAddress || '' }}</text>
              <text class="route-time">{{ formatTrackTime(r.acceptTime) }}</text>
            </view>
          </view>
        </view>
      </view>
    </view>
  </view>
</template>

<style scoped>
.page { background: #FAFAF8; min-height: 100vh; padding-top: calc(var(--status-bar-height) + 20px); }
.top-bar { display: flex; align-items: center; height: 80rpx; padding: 0 32rpx; }
.back-btn { width: 60rpx; }
.back-btn text { font-size: 36rpx; color: #775836; }
.page-title { font-size: 36rpx; font-weight: 600; color: #1C1B1B; margin-left: 16rpx; }
.loading, .empty { text-align: center; padding: 200rpx 0; color: #999; font-size: 28rpx; }
.content { padding: 0 32rpx; }
.card { background: #fff; border-radius: 20rpx; padding: 32rpx; margin-top: 20rpx; }
.card-row { display: flex; justify-content: space-between; align-items: center; padding: 20rpx 0; border-bottom: 2rpx solid #f5f5f5; }
.card-row:last-child { border-bottom: none; }
.card-label { font-size: 26rpx; color: #999; }
.card-value { font-size: 26rpx; color: #1C1B1B; font-weight: 500; }
.tracking-row { display: flex; align-items: center; gap: 12rpx; }
.tracking-no { max-width: 360rpx; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.copy-btn { font-size: 22rpx; color: #775836; padding: 4rpx 16rpx; border: 2rpx solid #775836; border-radius: 8rpx; }
.received { color: #67c23a; }
.route-list { padding-left: 20rpx; border-left: 2rpx solid #e8e8e8; }
.route-item { position: relative; padding: 0 0 30rpx 24rpx; }
.route-item.first { padding-top: 0; }
.route-item:last-child { padding-bottom: 0; }
.route-dot { position: absolute; left: -8rpx; top: 6rpx; width: 14rpx; height: 14rpx; border-radius: 50%; background: #d9d9d9; }
.route-dot.active { background: #775836; }
.route-info { display: flex; flex-direction: column; gap: 4rpx; }
.route-remark { font-size: 26rpx; color: #1C1B1B; }
.route-time { font-size: 22rpx; color: #999; }
</style>
