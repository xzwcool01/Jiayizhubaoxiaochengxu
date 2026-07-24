<script setup lang="ts">
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import NavBar from '@/components/NavBar.vue'
import MsIcon from '@/components/MsIcon.vue'
import { getAiWearShowcase, type AiWearShowcaseItem } from '@/api/ai-wear'
import { getGuideArticles, type GuideArticleItem } from '@/api/guide-article'
import { getExpertPosts, type ExpertPostItem } from '@/api/expert-post'

const activeTab = ref(0)
const tabs = ['AI试戴秀', '珠宝指南', '达人晒单']

const guides = ref<GuideArticleItem[]>([])
const posts = ref<ExpertPostItem[]>([])

const showcaseList = ref<AiWearShowcaseItem[]>([])

function toFullUrl(url: string) {
  if (!url) return ''
  return url.startsWith('http') ? url : 'http://localhost:8080' + url
}

async function loadShowcase() {
  try {
    const res = await getAiWearShowcase()
    if (res.code === 200 && res.data) {
      showcaseList.value = res.data.map((r) => ({
        ...r,
        imageUrl: toFullUrl(r.imageUrl)
      })) as any
    }
  } catch {}
}

async function loadGuides() {
  try {
    const res = await getGuideArticles()
    if (res.code === 200 && res.data) {
      guides.value = res.data.map((g) => ({
        ...g,
        coverImage: toFullUrl(g.coverImage)
      })) as any
    }
  } catch {}
}

async function loadPosts() {
  try {
    const res = await getExpertPosts()
    if (res.code === 200 && res.data) {
      posts.value = res.data.map((p) => ({
        ...p,
        images: p.images?.map((img: string) => toFullUrl(img))
      })) as any
    }
  } catch {}
}

function onGuideTap(id: number) {
  uni.navigateTo({ url: '/pages/discovery/article-detail?id=' + id })
}

function onPostTap(id: number) {
  uni.navigateTo({ url: '/pages/discovery/expert-detail?id=' + id })
}

onShow(() => {
  loadShowcase()
  loadGuides()
  loadPosts()
})

const tabIndicatorStyle = computed(() => {
  const positions = ['0%', '33.33%', '66.66%']
  return { left: positions[activeTab.value], width: '33.33%' }
})

function switchTab(i: number) {
  activeTab.value = i
  if (i === 2) loadPosts()
}
function onPreviewImage(url: string) {
  if (url) uni.previewImage({ urls: [url] })
}
</script>

<template>
  <view class="page">
    <view class="top-bar">
      <text class="top-title">发现</text>
    </view>

    <view class="tabs">
      <view v-for="(t, i) in tabs" :key="i" class="tab" @tap="switchTab(i)">
        <text :class="['tab-text', i === activeTab ? 'tab-text-active' : '']">{{ t }}</text>
      </view>
      <view class="tab-indicator" :style="tabIndicatorStyle" />
    </view>

    <scroll-view class="tab-content" scroll-y :show-scrollbar="false" v-show="activeTab === 0">
      <view class="tab-inner">
        <view class="ai-header">
          <view class="ai-header-left">
            <MsIcon name="auto_awesome" size="28rpx" color="#775836" />
            <text class="ai-header-title">AI 灵感造型</text>
          </view>
        <view class="ai-header-btn">
          <text>生成我的搭配</text>
          <MsIcon name="auto_awesome" size="26rpx" color="#775836" />
        </view>
        </view>
        <view v-if="showcaseList.length" class="waterfall">
          <view v-for="(card, i) in showcaseList" :key="'s'+i" class="waterfall-item">
            <view class="wf-card">
              <view class="wf-img-wrap">
                <image class="wf-img" style="height:440rpx" :src="card.imageUrl" mode="aspectFill" @tap="onPreviewImage(card.imageUrl)" />
              </view>
              <view class="wf-info">
                <text class="wf-title">{{ card.title || 'AI 试戴' }}</text>
                <text class="wf-tag">{{ card.nickname ? card.nickname + ' · ' : '' }}{{ card.tag }}</text>
              </view>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <scroll-view class="tab-content" scroll-y :show-scrollbar="false" v-show="activeTab === 1">
      <view class="tab-inner">
        <view v-for="(g, i) in guides" :key="i" :class="['guide-card', g.isHero ? 'guide-card-hero' : 'guide-card-thumb']" @tap="onGuideTap(g.id)">
          <view class="guide-img-wrap" :class="g.isHero ? 'guide-img-hero-wrap' : 'guide-img-thumb-wrap'">
            <image class="guide-img" :src="g.coverImage" mode="aspectFill" />
            <view v-if="g.isHero" class="guide-overlay" />
            <view v-if="g.isHero" class="guide-tag">深度专题</view>
          </view>
          <view v-if="g.isHero" class="guide-info-hero">
            <text class="guide-title-hero">{{ g.title }}</text>
            <text class="guide-desc-hero">{{ g.summary }}</text>
            <view class="guide-stats">
              <text class="guide-stat">{{ g.views }} 阅读</text>
              <text class="guide-stat">{{ g.publishDate }}</text>
            </view>
          </view>
          <view v-if="!g.isHero" class="guide-info-thumb-text">
            <text class="guide-title-thumb">{{ g.title }}</text>
            <text class="guide-desc-thumb">{{ g.summary }}</text>
            <view class="guide-readmore">
              <text>阅读更多</text>
              <MsIcon name="chevron_right" size="14rpx" color="#775836" />
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <scroll-view class="tab-content" scroll-y :show-scrollbar="false" v-show="activeTab === 2">
      <view class="post-grid">
        <view v-for="(p, i) in posts" :key="i" class="post-card" @tap="onPostTap(p.id)">
          <image class="post-img" :src="p.images?.[0] || ''" mode="aspectFill" />
          <view class="post-info">
            <view class="post-user">
              <image v-if="p.avatar" class="post-avatar-img" :src="p.avatar" mode="aspectFill" />
              <view v-else class="post-avatar" />
              <text class="post-name">@{{ p.nickname }}</text>
            </view>
            <text class="post-text">{{ p.content }}</text>
            <view class="post-actions">
              <view class="post-likes">
                <MsIcon name="favorite" size="14rpx" color="#605E5A" />
                <text>{{ p.expertLikes }}</text>
              </view>
              <text class="post-tag">{{ p.expertTag || '达人晒单' }}</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>

  <NavBar :active="2" />
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; display: flex; flex-direction: column; padding-bottom: 160rpx; padding-top: calc(var(--status-bar-height) + 16rpx); box-sizing: border-box; }

/* Top Bar */
.top-bar { display: flex; align-items: center; justify-content: flex-start; height: 112rpx; padding-left: 48rpx; background: rgba(252,249,248,0.85); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px); position: sticky; top: 0; z-index: 50; }
.top-title { font-size: 48rpx; color: #775836; font-weight: 600; font-family: 'Noto Serif SC', serif; letter-spacing: 0.05em; }

/* Tabs */
.tabs { position: sticky; top: 112rpx; z-index: 40; display: flex; background: rgba(252,249,248,0.92); backdrop-filter: blur(12px); -webkit-backdrop-filter: blur(12px); padding: 24rpx 32rpx 16rpx; border-bottom: 2rpx solid #D9D2CC; }
.tab { flex: 1; text-align: center; position: relative; padding-bottom: 8rpx; }
.tab-text { font-size: 24rpx; color: #605E5A; transition: color 0.3s; }
.tab-text-active { color: #775836; font-weight: 700; }
.tab-indicator { position: absolute; bottom: 0; height: 4rpx; background-color: #775836; transition: left 0.3s cubic-bezier(0.4, 0, 0.2, 1); border-radius: 2rpx; }

/* Tab Content */
.tab-content { flex: 1; }
.tab-inner { padding: 32rpx 24rpx; width: 100%; box-sizing: border-box; }

/* AI Header */
.ai-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 32rpx; }
.ai-header-left { display: flex; align-items: center; gap: 8rpx; }
.ai-header-title { font-size: 36rpx; font-weight: 600; color: #775836; font-family: 'Noto Serif SC', serif; }
.ai-header-btn { display: flex; align-items: center; gap: 8rpx; font-size: 24rpx; color: #775836; background: rgba(200,162,122,0.15); padding: 14rpx 28rpx; border-radius: 40rpx; border: none; line-height: 1; flex-shrink: 0; }
.ai-header-btn:active { transform: scale(0.95); }

/* Waterfall */
.waterfall { display: flex; flex-wrap: wrap; justify-content: space-between; }
.waterfall-item { width: calc(50% - 12rpx); margin-bottom: 24rpx; box-sizing: border-box; }
.wf-card { background: #fff; border-radius: 20rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); position: relative; }
.wf-img-wrap { overflow: hidden; }
.wf-img { width: 100%; display: block; transition: transform 0.5s; }
.wf-card:active .wf-img { transform: scale(1.1); }
.wf-info { padding: 16rpx; }
.wf-title { font-size: 24rpx; color: #1C1B1B; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; display: block; }
.wf-tag { font-size: 20rpx; color: #605E5A; display: block; margin-top: 4rpx; }

/* Guide Tab */
.guide-card-hero { margin-bottom: 48rpx; }
.guide-card-thumb { display: flex; flex-direction: row; gap: 24rpx; margin-bottom: 40rpx; width: 100%; }
.guide-img-wrap { border-radius: 20rpx; overflow: hidden; position: relative; flex-shrink: 0; }
.guide-img-hero-wrap { width: 100%; height: 340rpx; }
.guide-img-thumb-wrap { width: 240rpx; height: 240rpx; flex-shrink: 0; }
.guide-img { width: 100%; height: 100%; display: block; }
.guide-overlay { position: absolute; inset: 0; background: linear-gradient(to top, rgba(0,0,0,0.35), transparent); }
.guide-tag { position: absolute; top: 24rpx; left: 24rpx; background: rgba(255,255,255,0.9); backdrop-filter: blur(8px); padding: 8rpx 24rpx; border-radius: 40rpx; font-size: 20rpx; color: #775836; font-weight: 700; }
.guide-info-hero { margin-top: 20rpx; }
.guide-title-hero { font-size: 28rpx; font-weight: 700; color: #1C1B1B; line-height: 1.5; display: block; }
.guide-desc-hero { font-size: 24rpx; color: #605E5A; line-height: 1.6; margin-top: 12rpx; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.guide-stats { display: flex; gap: 32rpx; margin-top: 20rpx; }
.guide-stat { font-size: 22rpx; color: #7A7A7A; }
.guide-info-thumb-text { flex: 1; min-width: 0; display: flex; flex-direction: column; justify-content: center; }
.guide-title-thumb { font-size: 26rpx; font-weight: 700; color: #1C1B1B; line-height: 1.5; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; word-break: break-word; }
.guide-desc-thumb { font-size: 22rpx; color: #605E5A; line-height: 1.5; margin-top: 8rpx; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; word-break: break-word; }
.guide-readmore { display: flex; align-items: center; gap: 4rpx; font-size: 22rpx; color: #775836; font-weight: 700; margin-top: 12rpx; flex-shrink: 0; }
.guide-readmore:active { opacity: 0.7; }

/* Post Grid */
.post-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24rpx; padding: 0 24rpx; box-sizing: border-box; }
.post-card { background: #fff; border-radius: 20rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); }
.post-img { width: 100%; height: 340rpx; display: block; }
.post-info { padding: 16rpx; }
.post-user { display: flex; align-items: center; gap: 8rpx; margin-bottom: 8rpx; }
.post-avatar { width: 40rpx; height: 40rpx; border-radius: 50%; background: #E6E2DD; flex-shrink: 0; }
.post-avatar-img { width: 40rpx; height: 40rpx; border-radius: 50%; flex-shrink: 0; }
.post-name { font-size: 20rpx; font-weight: 700; color: #1C1B1B; }
.post-text { font-size: 22rpx; color: #1C1B1B; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.post-actions { display: flex; justify-content: space-between; align-items: center; margin-top: 12rpx; }
.post-likes { display: flex; align-items: center; gap: 4rpx; font-size: 18rpx; color: #605E5A; }
.post-tag { font-size: 18rpx; color: #775836; }
</style>
