<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getProductReviews, type ReviewVO } from '@/api/review'

const productId = ref(0)
const reviews = ref<ReviewVO[]>([])
const total = ref(0)
const page = ref(1)
const loading = ref(false)
const finished = ref(false)

onLoad(async (opt) => {
  productId.value = Number(opt?.id || 0)
  await loadData()
})

async function loadData() {
  if (loading.value || finished.value) return
  loading.value = true
  try {
    const res = await getProductReviews(productId.value, page.value, 20)
    if (res.code === 200) {
      const records = res.data.records || []
      reviews.value.push(...records)
      total.value = res.data.total
      if (reviews.value.length >= total.value) finished.value = true
      page.value++
    }
  } catch {} finally { loading.value = false }
}

function renderStars(rating: number) {
  return '★'.repeat(rating) + '☆'.repeat(5 - rating)
}
</script>

<template>
  <view class="page">
    <view class="top-bar">
      <view class="back-btn" @tap="uni.navigateBack()"><text class="back-icon">←</text></view>
      <text class="page-title">全部评价 ({{ total }})</text>
    </view>

    <scroll-view class="list" scroll-y @scrolltolower="loadData">
      <view class="list-inner">
        <view v-if="!reviews.length && !loading" class="empty"><text>暂无评价</text></view>
        <view v-for="r in reviews" :key="r.id" class="review-card">
          <view class="review-header">
            <text class="reviewer-name">{{ r.isAnonymous ? '匿名用户' : r.nickname || '用户' }}</text>
            <text class="reviewer-date">{{ r.createTime?.substring(0, 10) }}</text>
          </view>
          <view class="review-stars"><text :class="['star-text']">{{ renderStars(r.rating) }}</text></view>
          <text class="review-text" v-if="r.content">{{ r.content }}</text>
          <view class="review-images" v-if="r.images?.length">
            <image v-for="(img, i) in r.images" :key="i" :src="img" mode="aspectFill" class="review-img" @tap="uni.previewImage({ urls: r.images, current: i })" />
          </view>
        </view>
        <view v-if="loading" class="loading"><text>加载中...</text></view>
        <view v-if="finished && reviews.length > 0" class="finished"><text>—— 已显示全部 ——</text></view>
      </view>
    </scroll-view>
  </view>
</template>

<style scoped>
.page { background: #FAFAF8; min-height: 100vh; }
.top-bar { display: flex; align-items: center; height: 80rpx; padding: calc(var(--status-bar-height) + 32rpx) 32rpx 0; flex-shrink: 0; }
.back-btn { width: 60rpx; }
.back-icon { font-size: 36rpx; color: #775836; }
.page-title { font-size: 36rpx; font-weight: 600; color: #1C1B1B; margin-left: 16rpx; }
.list {}
.list-inner { padding: 28rpx 32rpx 40rpx; }
.empty { text-align: center; padding: 200rpx 0; color: #999; font-size: 28rpx; }
.review-card { background: #fff; border-radius: 20rpx; padding: 28rpx; margin-bottom: 16rpx; }
.review-header { display: flex; justify-content: space-between; align-items: center; }
.reviewer-name { font-size: 24rpx; font-weight: 600; color: #1C1B1B; }
.reviewer-date { font-size: 20rpx; color: #999; }
.review-stars { margin: 8rpx 0; }
.star-text { font-size: 28rpx; color: #E9C349; }
.review-text { font-size: 26rpx; color: #4F453C; line-height: 1.5; display: block; word-break: break-word; }
.review-images { display: flex; flex-wrap: wrap; gap: 12rpx; margin-top: 12rpx; overflow: hidden; }
.review-img { width: 200rpx; height: 200rpx; border-radius: 12rpx; }
.loading { text-align: center; padding: 24rpx; color: #999; font-size: 24rpx; }
.finished { text-align: center; padding: 24rpx; color: #ccc; font-size: 22rpx; }
</style>
