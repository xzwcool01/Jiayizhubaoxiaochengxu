<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import MsIcon from '@/components/MsIcon.vue'
import { getGuideArticleDetail, type GuideArticleItem } from '@/api/guide-article'

const article = ref<GuideArticleItem | null>(null)
const contentNodes = ref<any>(null)
const loading = ref(true)

function fixContentHtml(html: string) {
  html = html.replace(/src="\/uploads\//g, 'src="http://localhost:8080/uploads/')
  return html.replace(/<img\s([^>]*)>/g, (_m, attrs) => {
    if (/style="/i.test(attrs)) {
      return _m.replace(/style="([^"]*)"/, 'style="max-width:100%;height:auto;display:block;margin:0 auto;$1"')
    }
    return '<img style="max-width:100%;height:auto;display:block;margin:0 auto;" ' + attrs + '>'
  })
}

onLoad(async (query) => {
  const id = Number(query.id)
  if (!id) return
  try {
    const res = await getGuideArticleDetail(id)
    if (res.code === 200 && res.data) {
      article.value = {
        ...res.data,
        coverImage: res.data.coverImage?.startsWith('http') ? res.data.coverImage : 'http://localhost:8080' + res.data.coverImage
      } as any
      contentNodes.value = fixContentHtml(res.data.content || '')
    }
  } catch {}
  loading.value = false
})
</script>

<template>
  <view class="page">
    <view class="nav-bar">
      <view class="back-btn" @tap="uni.navigateBack()">
        <MsIcon name="arrow_back" size="40rpx" color="#1C1B1B" />
      </view>
      <text class="nav-title">珠宝指南</text>
      <view class="nav-placeholder" />
    </view>

    <scroll-view scroll-y class="scroll-area" :show-scrollbar="false">
      <view v-if="loading" class="loading">加载中...</view>
      <view v-else-if="article">
        <image v-if="article.coverImage" class="cover" :src="article.coverImage" mode="aspectFill" />
        <view class="content-wrap">
          <text class="title">{{ article.title }}</text>
          <view class="meta" v-if="article.author || article.publishDate">
            <text class="meta-item" v-if="article.author">{{ article.author }}</text>
            <text class="meta-item" v-if="article.publishDate">{{ article.publishDate }}</text>
            <text class="meta-item">{{ article.views || 0 }} 阅读</text>
          </view>
          <view class="divider" />
          <rich-text class="content" :nodes="contentNodes" />
        </view>
      </view>
      <view v-else class="loading">文章不存在</view>
    </scroll-view>
  </view>
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; display: flex; flex-direction: column; padding-top: calc(var(--status-bar-height) + 16rpx); box-sizing: border-box; }
.nav-bar { display: flex; align-items: center; height: 88rpx; padding: 0 24rpx; background: rgba(252,249,248,0.85); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px); position: sticky; top: 0; z-index: 50; flex-shrink: 0; }
.back-btn { width: 60rpx; height: 60rpx; display: flex; align-items: center; justify-content: center; }
.nav-title { font-size: 28rpx; font-weight: 600; color: #1C1B1B; margin-left: 8rpx; flex: 1; }
.nav-placeholder { width: 60rpx; }
.scroll-area { flex: 1; }
.loading { text-align: center; padding: 80rpx 0; color: #999; font-size: 26rpx; }
.cover { width: 100%; height: 420rpx; display: block; }
.content-wrap { padding: 32rpx 32rpx 60rpx; }
.title { font-size: 34rpx; font-weight: 700; color: #1C1B1B; line-height: 1.5; display: block; }
.meta { display: flex; gap: 24rpx; margin-top: 20rpx; }
.meta-item { font-size: 22rpx; color: #7A7A7A; }
.divider { height: 2rpx; background: #E6E2DD; margin: 28rpx 0; }
.content { font-size: 28rpx; color: #1C1B1B; line-height: 1.8; }
</style>
