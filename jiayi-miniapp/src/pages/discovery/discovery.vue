<script setup lang="ts">
import { ref, computed, onMounted, nextTick } from 'vue'
import NavBar from '@/components/NavBar.vue'
import MsIcon from '@/components/MsIcon.vue'
import { getAiWearShowcase } from '@/api/ai-wear'

const activeTab = ref(0)
const tabs = ['AI穿搭秀', '珠宝指南', '达人晒单']

const aiCards = ref([
  { title: '祖母绿晨间意蕴', tag: 'AI生成 · 职场风', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCH9R9MvotXXiEf_Je_nh89GQuV5C61W_umVCiQk_jVNWWIF7PLKj2YwMbqFZtta6Q8VEqihKrcbXUgrMS0uUy5asCIxFElPLhO2iy0mtQerpo3O0x7FgUPFAe1WfBHNr_TJKssoSxlEmuZfEvCnj-ZvGrRUm3Tbz1FT7q8dUGMEC2V1OVY0TfZW1I81Lv_sA5bOTXWo_yB8X-QFdUeorRxXm-5r-35DeUR9U4I-pm-Fo0OyKd3le9_bQ', ratio: '3/4' },
  { title: '晨曦系列：永恒之光', tag: 'AI生成 · 简约奢华', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBJVATCKa2ATbbOBUtztBUts1mby0Iq8e_VCjOoc2L283MdrQfOJzIc3J2sHLsOpJ2I5V-6zEzzMZmvvhSNA8I2eEvSFFucnOYUirHHUc5Sjc9qHUyBo8xG0HJKA7hpSdC_1DtNDHy76ZM1R3sNFcE3DluxOKZuCXzlC2Bc5JjsX8AB5h9V4Ryi8bXBK90Ya3-ruubnyEeZvcexFF-2lVYSCzlNINwz-FaabKKsN8NZKo02aP-zMsEBYw', ratio: '1/1' },
  { title: '黑金盛宴：先锋设计', tag: 'AI生成 · 晚宴风', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCjD27bnXvTtiSk2wvhACxVRP-gRZ-UXAl-UQQUbvZu4wjniL8gzc96SrfgwQqZ9tVShxC8ZobbEgqyfhx7RccSaGU3P4Nt3QizUfwOtdNBGcj3DTeY-mxdEnBEEhafNSXnKMijq0Ic4Do2kKqYVAc8GgXdNHDj02DNU2_4oLn3b-9EhkmcecAGG0FQPnabRcQdXVxbrSMhUgsfCu4N2zaqeZ-ZX8jyq6IuYqiFkV2fyXLtXw7rOCXmhA', ratio: '4/5' },
  { title: '温润珍珠：暖冬序曲', tag: 'AI生成 · 复古经典', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuA2MCnU52LuDSNXX3-XDZtVzrQ6cy3-hmZM-lp6ocAd4r0hU0StUQB-bT4s3zSxp0zDR_xiW8TxLSzaL_dIHZ9YjHqX9C6U7ecKN7iGN1NOa5lejF3Dh5LcpUjU5SG_lFKjAJ73pJp7M6yErXNv_-Qdt1XhamQYEOtXACJVVgwdba4w2ZP5nMFg-jWUir4nBBe741VhyKTmNB57x6crApkJdp0JwmfN0JdnJPnb9fvpsvT8QszjxP1MRA', ratio: '3/4' }
])

const guides = [
  { title: '如何鉴别高品质红宝石：Jiayi 专业指南', desc: '从产地到净度，五个维度带您领略这种被称为"爱情之石"的珍贵红宝石。我们的首席专家为您揭秘收藏级的奥秘...', views: '12.8k', date: '2024.11.20', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCwi8mQGubh5im5ZNTN-U4-lvtPXoTW19JkofouQa_xnIQjUCzD3OKyw0QIJK8FLd9Xic98Xhvw-9H0U_EueJQAR-Tm-OwpFfR3wXvS5qzmXOjbZ_R3-3hkTgUVQvj8aNjNGi0RtIXOtLr9FOR5foNoIsqJK8tGSZmLxQgSZj47IPLvUxqkre9AqPvB8wG8vCW5jQlnSm6iEQJYbl5xoDCm2RqVvzTgHTlnnchLxlP05DakdviIgJoNAA', isHero: true },
  { title: '匠心之道：从手稿到成品的华丽蜕变', desc: '嘉艺珠宝每件作品背后都要经过超过300小时的纯手工打磨与精雕细琢。', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDsVi1HtUJHPu-hpeG_knlSHD-LDpZkm0zISPm1wmmuyVHTswadYx4-nisiz88vX1y6lwJ_CCbAvATphYP1s2TY53A0W42ZEhoH6BfrUppGkTL8ZnctM2fclyD3bMWCa7DAVAURXzw3Cpps3PAGNEF78I1UkPkprdgbTbLay2k-KGioutxmLJcqGS8phR5MAmGJftuvl81nBklDdK8NHw2dv3LkNVKpRfwJcdt72iq_BXsDbkj8uA6JhQ', isHero: false },
  { title: '日常保养：让您的瑰宝永远闪耀如新', desc: '掌握正确的清洁与存放方法，让珍贵瞬间跨越时光长河。', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCG_jsnbIkoiurU0oHxWiVYAaUIs420wWN01xJIsRmyIt9cBB8KDktMgOPpSvMMmVTNaqXOcbW45H2SfuoTPZNdG6d_XBXeOjjsFj6skcA3g0pRh29XTjXe78xccd7CZwLJCmamzneNns4XP273enAUBCatqd9S41RA5_3NJs3sZ1sLGxkhIu8s2GxOXRy7B0DtS96uAC0ROfooBp9LBAhhO0duBT0F7kTVbnOx8Q-N_3SOqhBILyOgJA', isHero: false }
]

const posts = [
  { user: '@Elena.C', text: '收到纪念日礼物啦！蓝宝石的光泽在阳光下太迷人了✨', likes: '1.2k', tag: '#嘉艺瞬间', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuATW-QZc0MbQPQrdaGHEH2sQ6FnoUD0hzXNiGpJyUbnT3BTASXHJ_RxYX0L5D0S4oW1xu6y-hJMW6E0lDFWpkIfHgpzJNuAaOIZfp_YktMcKHt_VIOI3xyYLDkyAgSgIFKVujLEd7UKJi5zJd-OSxeoIiZj_ecuvdL8DAHOnT93nWx2Z5THjQZv7l-TSOlzUSvFQjUkqAXk8Eda4fpBIcIhS4wKGnmcpAS5x4SKPuSa4M_cxE9Cf0gyVw' },
  { user: '@Mr.J', text: '极简而不凡，这就是我想要的风格。', likes: '856', tag: '#穿搭日常', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDUsUE6GOhYTCNOFm4dzK5r5xe1dnBGI6j6h6J9za3ypcoSEgqMPNaNOZC9BuBEa2SoLxVIYGg2dGygkKO95xtcrFiZlekQ8jebRHZCqJOYtWQk_FQ7B5YkyLwT5mW3D6L8Wf2ISNWp7xunfR_ysUCWUvIlMqoV-KhIRh7xhu5NjHG8SOCkiuL40f0j31ga2MBsM_R5oBW8_vUiMV3NfCrGdzZZGj-D9n7r5i75Ge1M-gS5-32RxezNLg' }
]

const showcaseList = ref<any[]>([])

onMounted(async () => {
  try {
    const res = await getAiWearShowcase()
    if (res.code === 200 && res.data) {
      showcaseList.value = res.data.map((r: any) => ({
        title: r.productName || 'AI 试戴',
        tag: 'AI生成',
        img: r.resultUrl?.startsWith('http') ? r.resultUrl : 'http://localhost:8080' + r.resultUrl,
        ratio: '3/4'
      }))
    }
  } catch {}
})

const tabIndicatorStyle = computed(() => {
  const positions = ['0%', '33.33%', '66.66%']
  return { left: positions[activeTab.value], width: '33.33%' }
})

const imgHeight = (ratio: string) => {
  if (ratio === '3/4') return '480rpx'
  if (ratio === '1/1') return '340rpx'
  if (ratio === '4/5') return '400rpx'
  return '340rpx'
}

function switchTab(i: number) { activeTab.value = i }
function goAI() { uni.navigateTo({ url: '/pages/ai-wear/index' }) }
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
      <view class="ai-header">
        <view class="ai-header-left">
          <MsIcon name="auto_awesome" size="28rpx" color="#775836" />
          <text class="ai-header-title">AI 灵感造型</text>
        </view>
        <button class="ai-header-btn" @tap="goAI">
          <text>生成我的搭配</text>
          <MsIcon name="star" size="18rpx" color="#775836" />
        </button>
      </view>

      <view class="waterfall">
        <view v-for="(card, i) in aiCards" :key="i" class="waterfall-item">
          <view class="wf-card">
            <view class="wf-img-wrap">
              <image class="wf-img" :style="{ height: imgHeight(card.ratio) }" :src="card.img" mode="aspectFill" />
            </view>
            <view class="wf-info">
              <text class="wf-title">{{ card.title }}</text>
              <text class="wf-tag">{{ card.tag }}</text>
            </view>
            <view class="wf-like">
              <MsIcon name="favorite" size="24rpx" color="#775836" />
            </view>
          </view>
        </view>
      </view>

      <view v-if="showcaseList.length" class="waterfall" style="margin-top:32rpx">
        <view v-for="(card, i) in showcaseList" :key="'s'+i" class="waterfall-item">
          <view class="wf-card">
            <view class="wf-img-wrap">
              <image class="wf-img" :style="{ height: imgHeight(card.ratio) }" :src="card.img" mode="aspectFill" />
            </view>
            <view class="wf-info">
              <text class="wf-title">{{ card.title }}</text>
              <text class="wf-tag">{{ card.tag }}</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>

    <scroll-view class="tab-content" scroll-y :show-scrollbar="false" v-show="activeTab === 1">
      <view v-for="(g, i) in guides" :key="i" class="guide-card">
        <view class="guide-img-wrap" :class="g.isHero ? 'guide-img-hero-wrap' : 'guide-img-thumb-wrap'">
          <image class="guide-img" :src="g.img" mode="aspectFill" />
          <view v-if="g.isHero" class="guide-overlay" />
          <view v-if="g.isHero" class="guide-tag">深度专题</view>
        </view>
        <view :class="g.isHero ? 'guide-info-hero' : 'guide-info-thumb'">
          <text class="guide-title">{{ g.title }}</text>
          <text class="guide-desc">{{ g.desc }}</text>
          <view v-if="g.isHero" class="guide-stats">
            <text class="guide-stat">visibility {{ g.views }}</text>
            <text class="guide-stat">calendar_month {{ g.date }}</text>
          </view>
          <button v-if="!g.isHero" class="guide-readmore">
            <text>阅读更多</text>
            <MsIcon name="chevron_right" size="14rpx" color="#775836" />
          </button>
        </view>
      </view>
    </scroll-view>

    <scroll-view class="tab-content" scroll-y :show-scrollbar="false" v-show="activeTab === 2">
      <view class="post-grid">
        <view v-for="(p, i) in posts" :key="i" class="post-card">
          <image class="post-img" :src="p.img" mode="aspectFill" />
          <view class="post-info">
            <view class="post-user">
              <view class="post-avatar" />
              <text class="post-name">{{ p.user }}</text>
            </view>
            <text class="post-text">{{ p.text }}</text>
            <view class="post-actions">
              <view class="post-likes">
                <MsIcon name="favorite" size="14rpx" color="#605E5A" />
                <text>{{ p.likes }}</text>
              </view>
              <text class="post-tag">{{ p.tag }}</text>
            </view>
          </view>
        </view>
      </view>
    </scroll-view>
  </view>

  <NavBar :active="2" />
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; display: flex; flex-direction: column; padding-bottom: 160rpx; box-sizing: border-box; }

/* Top Bar */
.top-bar { display: flex; align-items: center; justify-content: center; height: 112rpx; background: rgba(252,249,248,0.85); backdrop-filter: blur(20px); -webkit-backdrop-filter: blur(20px); position: sticky; top: 0; z-index: 50; }
.top-title { font-size: 48rpx; color: #775836; font-weight: 600; font-family: 'Noto Serif SC', serif; letter-spacing: 0.05em; }

/* Tabs */
.tabs { position: sticky; top: 112rpx; z-index: 40; display: flex; background: rgba(252,249,248,0.92); backdrop-filter: blur(12px); -webkit-backdrop-filter: blur(12px); padding: 24rpx 32rpx 16rpx; border-bottom: 2rpx solid #D9D2CC; }
.tab { flex: 1; text-align: center; position: relative; padding-bottom: 8rpx; }
.tab-text { font-size: 24rpx; color: #605E5A; transition: color 0.3s; }
.tab-text-active { color: #775836; font-weight: 700; }
.tab-indicator { position: absolute; bottom: 0; height: 4rpx; background-color: #775836; transition: left 0.3s cubic-bezier(0.4, 0, 0.2, 1); border-radius: 2rpx; }

/* Tab Content */
.tab-content { flex: 1; padding: 32rpx; }

/* AI Header */
.ai-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 32rpx; }
.ai-header-left { display: flex; align-items: center; gap: 8rpx; }
.ai-header-title { font-size: 36rpx; font-weight: 600; color: #775836; font-family: 'Noto Serif SC', serif; }
.ai-header-btn { display: flex; align-items: center; gap: 8rpx; font-size: 22rpx; color: #775836; background: rgba(200,162,122,0.15); padding: 12rpx 24rpx; border-radius: 40rpx; border: none; line-height: 1; }
.ai-header-btn:active { transform: scale(0.95); }

/* Waterfall */
.waterfall { display: flex; flex-wrap: wrap; gap: 24rpx; }
.waterfall-item { width: calc(50% - 12rpx); }
.wf-card { background: #fff; border-radius: 20rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); position: relative; }
.wf-img-wrap { overflow: hidden; }
.wf-img { width: 100%; display: block; transition: transform 0.5s; }
.wf-card:active .wf-img { transform: scale(1.1); }
.wf-info { padding: 16rpx; }
.wf-title { font-size: 24rpx; color: #1C1B1B; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; display: block; }
.wf-tag { font-size: 20rpx; color: #605E5A; display: block; margin-top: 4rpx; }
.wf-like { position: absolute; top: 12rpx; right: 12rpx; width: 52rpx; height: 52rpx; border-radius: 50%; background: rgba(255,255,255,0.85); backdrop-filter: blur(8px); display: flex; align-items: center; justify-content: center; }

/* Guide Tab */
.guide-card { margin-bottom: 40rpx; }
.guide-img-wrap { border-radius: 20rpx; overflow: hidden; position: relative; }
.guide-img-hero-wrap { width: 100%; height: 340rpx; }
.guide-img-thumb-wrap { width: 240rpx; height: 240rpx; flex-shrink: 0; }
.guide-img { width: 100%; height: 100%; }
.guide-overlay { position: absolute; inset: 0; background: linear-gradient(to top, rgba(0,0,0,0.35), transparent); }
.guide-tag { position: absolute; top: 24rpx; left: 24rpx; background: rgba(255,255,255,0.9); backdrop-filter: blur(8px); padding: 8rpx 24rpx; border-radius: 40rpx; font-size: 20rpx; color: #775836; font-weight: 700; }
.guide-info-hero { margin-top: 16rpx; }
.guide-info-thumb { display: flex; flex-direction: column; justify-content: center; }
.guide-title { font-size: 28rpx; font-weight: 600; color: #1C1B1B; display: block; }
.guide-desc { font-size: 22rpx; color: #605E5A; display: block; margin-top: 8rpx; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.guide-stats { display: flex; gap: 32rpx; margin-top: 16rpx; }
.guide-stat { font-size: 20rpx; color: #605E5A; display: flex; align-items: center; gap: 4rpx; }
.guide-readmore { display: flex; align-items: center; gap: 4rpx; font-size: 22rpx; color: #775836; font-weight: 700; margin-top: 8rpx; padding: 0; background: none; border: none; line-height: 1; }
.guide-readmore:active { opacity: 0.7; }

/* Post Grid */
.post-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24rpx; }
.post-card { background: #fff; border-radius: 20rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); }
.post-img { width: 100%; height: 340rpx; display: block; }
.post-info { padding: 16rpx; }
.post-user { display: flex; align-items: center; gap: 8rpx; margin-bottom: 8rpx; }
.post-avatar { width: 40rpx; height: 40rpx; border-radius: 50%; background: #E6E2DD; }
.post-name { font-size: 20rpx; font-weight: 700; color: #1C1B1B; }
.post-text { font-size: 22rpx; color: #1C1B1B; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.post-actions { display: flex; justify-content: space-between; align-items: center; margin-top: 12rpx; }
.post-likes { display: flex; align-items: center; gap: 4rpx; font-size: 18rpx; color: #605E5A; }
.post-tag { font-size: 18rpx; color: #775836; }
</style>
