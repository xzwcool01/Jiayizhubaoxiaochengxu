<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import MsIcon from '@/components/MsIcon.vue'
import { getExpertPostDetail, type ExpertPostItem } from '@/api/expert-post'

const post = ref<ExpertPostItem | null>(null)
const loading = ref(true)
const currentImg = ref(0)

onLoad(async (query) => {
  const id = Number(query.id)
  if (!id) return
  try {
    const res = await getExpertPostDetail(id)
    if (res.code === 200 && res.data) {
      post.value = {
        ...res.data,
        images: res.data.images?.map((img: string) => img.startsWith('http') ? img : 'http://localhost:8080' + img)
      } as any
    }
  } catch {}
  loading.value = false
})

function onPreviewImage(url: string) {
  if (post.value?.images) uni.previewImage({ urls: post.value.images, current: url })
}
</script>

<template>
  <view class="page">
    <view class="nav-bar">
      <view class="back-btn" @tap="uni.navigateBack()">
        <MsIcon name="arrow_back" size="40rpx" color="#1C1B1B" />
      </view>
      <text class="nav-title">达人晒单</text>
      <view class="nav-placeholder" />
    </view>

    <scroll-view scroll-y class="scroll-area" :show-scrollbar="false">
      <view v-if="loading" class="loading">加载中...</view>
      <view v-else-if="post">
        <view v-if="post.images?.length" class="swiper-wrap">
          <swiper class="img-swiper" :indicator-dots="post.images.length > 1" indicator-color="rgba(255,255,255,0.4)" indicator-active-color="#fff" @change="e => currentImg = e.detail.current">
            <swiper-item v-for="(img, i) in post.images" :key="i">
              <image class="swiper-img" :src="img" mode="aspectFill" @tap="onPreviewImage(img)" />
            </swiper-item>
          </swiper>
          <view v-if="post.images.length > 1" class="img-counter">{{ currentImg + 1 }}/{{ post.images.length }}</view>
        </view>
        <view class="content-wrap">
          <view class="user-row">
            <image v-if="post.avatar" class="user-avatar" :src="post.avatar" mode="aspectFill" />
            <view v-else class="user-avatar default-avatar">
              <text class="avatar-text">{{ post.nickname?.charAt(0) || '?' }}</text>
            </view>
            <text class="user-name">@{{ post.nickname }}</text>
          </view>
          <text class="content-text">{{ post.content }}</text>
          <view class="actions-row">
            <view class="action-item">
              <MsIcon name="favorite" size="28rpx" color="#605E5A" />
              <text class="action-text">{{ post.expertLikes }}</text>
            </view>
            <text class="tag-text">{{ post.expertTag || '达人晒单' }}</text>
          </view>
        </view>
      </view>
      <view v-else class="loading">晒单不存在</view>
    </scroll-view>
  </view>
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; display: flex; flex-direction: column; padding-top: calc(var(--status-bar-height) + 32rpx); box-sizing: border-box; }
.nav-bar { display: flex; align-items: center; height: 88rpx; padding: 0 24rpx; background: rgba(252,249,248,0.85); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px); position: sticky; top: 0; z-index: 50; flex-shrink: 0; }
.back-btn { width: 60rpx; height: 60rpx; display: flex; align-items: center; justify-content: center; }
.nav-title { font-size: 28rpx; font-weight: 600; color: #1C1B1B; margin-left: 8rpx; flex: 1; }
.nav-placeholder { width: 60rpx; }
.scroll-area { flex: 1; }
.loading { text-align: center; padding: 80rpx 0; color: #999; font-size: 26rpx; }
.swiper-wrap { position: relative; }
.img-swiper { width: 100%; height: 600rpx; }
.swiper-img { width: 100%; height: 100%; display: block; }
.img-counter { position: absolute; bottom: 24rpx; right: 24rpx; background: rgba(0,0,0,0.45); color: #fff; font-size: 22rpx; padding: 4rpx 16rpx; border-radius: 40rpx; }
.content-wrap { padding: 32rpx; }
.user-row { display: flex; align-items: center; gap: 16rpx; margin-bottom: 24rpx; }
.user-avatar { width: 64rpx; height: 64rpx; border-radius: 50%; flex-shrink: 0; }
.default-avatar { background: #E6E2DD; display: flex; align-items: center; justify-content: center; }
.avatar-text { font-size: 28rpx; color: #775836; font-weight: 700; }
.user-name { font-size: 28rpx; font-weight: 600; color: #1C1B1B; }
.content-text { font-size: 28rpx; color: #1C1B1B; line-height: 1.8; display: block; }
.actions-row { display: flex; align-items: center; justify-content: space-between; margin-top: 32rpx; padding-top: 24rpx; border-top: 2rpx solid #E6E2DD; }
.action-item { display: flex; align-items: center; gap: 8rpx; }
.action-text { font-size: 24rpx; color: #605E5A; }
.tag-text { font-size: 22rpx; color: #775836; font-weight: 600; }
</style>
