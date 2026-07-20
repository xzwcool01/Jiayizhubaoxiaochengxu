<script setup lang="ts">
import { ref, onShow } from '@dcloudio/uni-app'
import MsIcon from '@/components/MsIcon.vue'
import { getFavoriteList, removeFavorite } from '@/api/favorite'
import type { PmsProduct } from '@/api/product'

const list = ref<PmsProduct[]>([])
const loading = ref(false)

async function fetchData() {
  loading.value = true
  try {
    const res = await getFavoriteList()
    if (res.code === 200) list.value = res.data || []
  } catch { uni.showToast({ title: '加载失败', icon: 'none' }) }
  finally { loading.value = false }
}

async function handleRemove(productId: number) {
  try {
    const res = await removeFavorite(productId)
    if (res.code === 200) {
      list.value = list.value.filter(p => p.id !== productId)
      uni.showToast({ title: '已取消收藏', icon: 'success' })
    }
  } catch { uni.showToast({ title: '操作失败', icon: 'none' }) }
}

function goDetail(id: number) {
  uni.navigateTo({ url: '/pages/product/detail?id=' + id })
}

function getImageUrl(p: PmsProduct): string {
  if (p.mainImage) return p.mainImage
  if (p.images) {
    try { const arr = JSON.parse(p.images); return Array.isArray(arr) && arr.length > 0 ? arr[0] : '' } catch { return '' }
  }
  return ''
}

onShow(fetchData)
</script>

<template>
  <view class="page">
    <view class="top-bar">
      <view class="top-left">
        <view class="back-btn" @tap="uni.navigateBack()"><MsIcon name="arrow_back" size="44rpx" color="#1C1B1B" /></view>
        <text class="page-title">我的收藏</text>
      </view>
    </view>
    <view v-if="loading" class="loading-mask"><text>加载中...</text></view>
    <view v-else-if="!list.length" class="empty"><text class="empty-text">暂无收藏商品</text></view>
    <view v-else class="product-list">
      <view v-for="p in list" :key="p.id" class="product-card" @tap="goDetail(p.id)">
        <image class="product-img" :src="getImageUrl(p)" mode="aspectFill" />
        <view class="product-info">
          <text class="product-name">{{ p.name }}</text>
          <text class="product-price">¥{{ Math.floor(p.price).toLocaleString() }}</text>
        </view>
        <view class="remove-btn" @tap.stop="handleRemove(p.id)"><text>取消收藏</text></view>
      </view>
    </view>
  </view>
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; }
.top-bar { display: flex; align-items: center; padding: 32rpx; height: 112rpx; }
.top-left { display: flex; flex-direction: row; align-items: center; gap: 28rpx; padding-top: 28rpx; }
.page-title { font-size: 36rpx; font-weight: 600; color: #1C1B1B; }
.loading-mask { display: flex; align-items: center; justify-content: center; height: 400rpx; font-size: 28rpx; color: #999; }
.empty { display: flex; align-items: center; justify-content: center; height: 400rpx; }
.empty-text { font-size: 28rpx; color: #999; }
.product-list { padding: 0 32rpx; }
.product-card { display: flex; align-items: center; background: #fff; border-radius: 16rpx; padding: 24rpx; margin-bottom: 16rpx; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); }
.product-img { width: 160rpx; height: 160rpx; border-radius: 12rpx; flex-shrink: 0; }
.product-info { flex: 1; margin-left: 24rpx; }
.product-name { font-size: 28rpx; color: #1C1B1B; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; display: block; }
.product-price { font-size: 32rpx; font-weight: 700; color: #775836; margin-top: 8rpx; display: block; }
.remove-btn { padding: 12rpx 24rpx; border: 2rpx solid #D9D2CC; border-radius: 12rpx; flex-shrink: 0; }
.remove-btn text { font-size: 22rpx; color: #605E5A; }
</style>
