<script setup lang="ts">
import { ref } from 'vue'
import NavBar from '@/components/NavBar.vue'


const activeTab = ref(0)
const tabs = ['AI穿搭秀', '珠宝指南', '达人晒单']

const aiCards = [
  { title: '祖母绿晨间意蕴', tag: 'AI生成 · 职场风', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCH9R9MvotXXiEf_Je_nh89GQuV5C61W_umVCiQk_jVNWWIF7PLKj2YwMbqFZtta6Q8VEqihKrcbXUgrMS0uUy5asCIxFElPLhO2iy0mtQerpo3O0x7FgUPFAe1WfBHNr_TJKssoSxlEmuZfEvCnj-ZvGrRUm3Tbz1FT7q8dUGMEC2V1OVY0TfZW1I81Lv_sA5bOTXWo_yB8X-QFdUeorRxXm-5r-35DeUR9U4I-pm-Fo0OyKd3le9_bQ', ratio: '3/4' },
  { title: '晨曦系列：永恒之光', tag: 'AI生成 · 简约奢华', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuBJVATCKa2ATbbOBUtztBUts1mby0Iq8e_VCjOoc2L283MdrQfOJzIc3J2sHLsOpJ2I5V-6zEzzMZmvvhSNA8I2eEvSFFucnOYUirHHUc5Sjc9qHUyBo8xG0HJKA7hpSdC_1DtNDHy76ZM1R3sNFcE3DluxOKZuCXzlC2Bc5JjsX8AB5h9V4Ryi8bXBK90Ya3-ruubnyEeZvcexFF-2lVYSCzlNINwz-FaabKKsN8NZKo02aP-zMsEBYw', ratio: '1/1' },
  { title: '黑金盛宴：先锋设计', tag: 'AI生成 · 晚宴风', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCjD27bnXvTtiSk2wvhACxVRP-gRZ-UXAl-UQQUbvZu4wjniL8gzc96SrfgwQqZ9tVShxC8ZobbEgqyfhx7RccSaGU3P4Nt3QizUfwOtdNBGcj3DTeY-mxdEnBEEhafNSXnKMijq0Ic4Do2kKqYVAc8GgXdNHDj02DNU2_4oLn3b-9EhkmcecAGG0FQPnabRcQdXVxbrSMhUgsfCu4N2zaqeZ-ZX8jyq6IuYqiFkV2fyXLtXw7rOCXmhA', ratio: '4/5' },
  { title: '温润珍珠：暖冬序曲', tag: 'AI生成 · 复古经典', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuA2MCnU52LuDSNXX3-XDZtVzrQ6cy3-hmZM-lp6ocAd4r0hU0StUQB-bT4s3zSxp0zDR_xiW8TxLSzaL_dIHZ9YjHqX9C6U7ecKN7iGN1NOa5lejF3Dh5LcpUjU5SG_lFKjAJ73pJp7M6yErXNv_-Qdt1XhamQYEOtXACJVVgwdba4w2ZP5nMFg-jWUir4nBBe741VhyKTmNB57x6crApkJdp0JwmfN0JdnJPnb9fvpsvT8QszjxP1MRA', ratio: '3/4' }
]

const guides = [
  { title: '如何鉴别高品质红宝石：Jiayi 专业指南', desc: '从产地到净度，五个维度带您领略这种被称为"爱情之石"的珍贵红宝石。', views: '12.8k', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCwi8mQGubh5im5ZNTN-U4-lvtPXoTW19JkofouQa_xnIQjUCzD3OKyw0QIJK8FLd9Xic98Xhvw-9H0U_EueJQAR-Tm-OwpFfR3wXvS5qzmXOjbZ_R3-3hkTgUVQvj8aNjNGi0RtIXOtLr9FOR5foNoIsqJK8tGSZmLxQgSZj47IPLvUxqkre9AqPvB8wG8vCW5jQlnSm6iEQJYbl5xoDCm2RqVvzTgHTlnnchLxlP05DakdviIgJoNAA' },
  { title: '匠心之道：从手稿到成品的华丽蜕变', desc: '嘉艺珠宝每件作品背后都要经过超过300小时的纯手工打磨与精雕细琢。', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDsVi1HtUJHPu-hpeG_knlSHD-LDpZkm0zISPm1wmmuyVHTswadYx4-nisiz88vX1y6lwJ_CCbAvATphYP1s2TY53A0W42ZEhoH6BfrUppGkTL8ZnctM2fclyD3bMWCa7DAVAURXzw3Cpps3PAGNEF78I1UkPkprdgbTbLay2k-KGioutxmLJcqGS8phR5MAmGJftuvl81nBklDdK8NHw2dv3LkNVKpRfwJcdt72iq_BXsDbkj8uA6JhQ' },
  { title: '日常保养：让您的瑰宝永远闪耀如新', desc: '掌握正确的清洁与存放方法，让珍贵瞬间跨越时光长河。', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuCG_jsnbIkoiurU0oHxWiVYAaUIs420wWN01xJIsRmyIt9cBB8KDktMgOPpSvMMmVTNaqXOcbW45H2SfuoTPZNdG6d_XBXeOjjsFj6skcA3g0pRh29XTjXe78xccd7CZwLJCmamzneNns4XP273enAUBCatqd9S41RA5_3NJs3sZ1sLGxkhIu8s2GxOXRy7B0DtS96uAC0ROfooBp9LBAhhO0duBT0F7kTVbnOx8Q-N_3SOqhBILyOgJA' }
]

const posts = [
  { user: '@Elena.C', text: '收到纪念日礼物啦！蓝宝石的光泽在阳光下太迷人了✨', likes: '1.2k', tag: '#嘉艺瞬间', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuATW-QZc0MbQPQrdaGHEH2sQ6FnoUD0hzXNiGpJyUbnT3BTASXHJ_RxYX0L5D0S4oW1xu6y-hJMW6E0lDFWpkIfHgpzJNuAaOIZfp_YktMcKHt_VIOI3xyYLDkyAgSgIFKVujLEd7UKJi5zJd-OSxeoIiZj_ecuvdL8DAHOnT93nWx2Z5THjQZv7l-TSOlzUSvFQjUkqAXk8Eda4fpBIcIhS4wKGnmcpAS5x4SKPuSa4M_cxE9Cf0gyVw' },
  { user: '@Mr.J', text: '极简而不凡，这就是我想要的风格。', likes: '856', tag: '#穿搭日常', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuDUsUE6GOhYTCNOFm4dzK5r5xe1dnBGI6j6h6J9za3ypcoSEgqMPNaNOZC9BuBEa2SoLxVIYGg2dGygkKO95xtcrFiZlekQ8jebRHZCqJOYtWQk_FQ7B5YkyLwT5mW3D6L8Wf2ISNWp7xunfR_ysUCWUvIlMqoV-KhIRh7xhu5NjHG8SOCkiuL40f0j31ga2MBsM_R5oBW8_vUiMV3NfCrGdzZZGj-D9n7r5i75Ge1M-gS5-32RxezNLg' }
]

function switchTab(i: number) { activeTab.value = i }
function goAI() { uni.navigateTo({ url: '/pages/ai-wear/index' }) }

</script>

<template>
  <view class="page">
    <view class="top-bar"><text class="top-title">发现</text></view>
    <view class="tabs">
      <view v-for="(t, i) in tabs" :key="i" :class="['tab', i === activeTab ? 'tab-active' : '']" @tap="switchTab(i)">
        <text :class="['tab-text', i === activeTab ? 'tab-text-active' : '']">{{ t }}</text>
      </view>
      <view class="tab-indicator" :style="'left: ' + (activeTab * 33.33) + '%'" />
    </view>

    <!-- Tab: AI穿搭秀 -->
    <scroll-view class="tab-content" scroll-y v-show="activeTab === 0">
      <view class="ai-header">
        <view class="ai-header-left">
          <text class="ai-header-icon">auto_awesome</text>
          <text class="ai-header-title">AI 灵感造型</text>
        </view>
        <view class="ai-header-btn" @tap="goAI">
          <text>生成我的搭配</text>
          <text class="ai-header-btn-icon">spark</text>
        </view>
      </view>
      <view class="waterfall">
        <view v-for="(card, i) in aiCards" :key="i" :class="['waterfall-item', i % 2 === 1 ? 'waterfall-item-right' : '']">
          <view class="wf-card">
            <image class="wf-img" :style="'height: ' + (card.ratio === '3/4' ? '480rpx' : card.ratio === '1/1' ? '340rpx' : '400rpx')" :src="card.img" mode="aspectFill" />
            <view class="wf-info">
              <text class="wf-title">{{ card.title }}</text>
              <text class="wf-tag">{{ card.tag }}</text>
            </view>
            <view class="wf-like"><text class="wf-like-icon">favorite</text></view>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- Tab: 珠宝指南 -->
    <scroll-view class="tab-content" scroll-y v-show="activeTab === 1">
      <view v-for="(g, i) in guides" :key="i" :class="['guide-card', i === 0 ? 'guide-card-first' : '']">
        <image class="guide-img" :class="i === 0 ? 'guide-img-hero' : 'guide-img-thumb'" :src="g.img" mode="aspectFill" />
        <view v-if="i === 0" class="guide-tag"><text>深度专题</text></view>
        <view class="guide-info">
          <text class="guide-title">{{ g.title }}</text>
          <text class="guide-desc">{{ g.desc }}</text>
          <view v-if="i === 0" class="guide-stats">
            <text class="guide-stat">visibility {{ g.views }}</text>
            <text class="guide-stat">calendar_month 2024.11.20</text>
          </view>
        </view>
      </view>
    </scroll-view>

    <!-- Tab: 达人晒单 -->
    <scroll-view class="tab-content" scroll-y v-show="activeTab === 2">
      <view class="post-grid">
        <view v-for="(p, i) in posts" :key="i" class="post-card">
          <image class="post-img" :src="p.img" mode="aspectFill" />
          <view class="post-info">
            <view class="post-user"><view class="post-avatar" /><text class="post-name">{{ p.user }}</text></view>
            <text class="post-text">{{ p.text }}</text>
            <view class="post-actions">
              <text class="post-likes">favorite {{ p.likes }}</text>
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
.page { background-color: #FAFAF8; height: 100vh; display: flex; flex-direction: column; padding-bottom: 160rpx; box-sizing: border-box; }
.top-bar { display: flex; align-items: center; justify-content: center; height: 112rpx; background-color: rgba(252,249,248,0.8); }
.top-title { font-size: 56rpx; color: #775836; font-weight: 600; font-family: 'Noto Serif SC', serif; }
.tabs { position: sticky; top: 112rpx; z-index: 40; display: flex; background-color: rgba(252,249,248,0.9); padding: 24rpx 32rpx 16rpx; border-bottom: 2rpx solid #D9D2CC; }
.tab { flex: 1; text-align: center; position: relative; }
.tab-text { font-size: 24rpx; color: #605E5A; }
.tab-text-active { color: #775836; font-weight: bold; }
.tab-indicator { position: absolute; bottom: 0; height: 4rpx; width: 33.33%; background-color: #775836; transition: left 0.3s; }
.tab-content { flex: 1; padding: 32rpx; }
.ai-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 32rpx; }
.ai-header-left { display: flex; align-items: center; gap: 8rpx; }
.ai-header-icon { font-family: 'Material Symbols Outlined'; font-size: 32rpx; color: #775836; }
.ai-header-title { font-size: 40rpx; font-weight: 600; color: #775836; font-family: 'Noto Serif SC', serif; }
.ai-header-btn { display: flex; align-items: center; gap: 8rpx; font-size: 24rpx; color: #775836; background-color: rgba(200,162,122,0.15); padding: 12rpx 24rpx; border-radius: 40rpx; }
.ai-header-btn-icon { font-family: 'Material Symbols Outlined'; font-size: 24rpx; }
.waterfall { display: flex; flex-wrap: wrap; gap: 24rpx; }
.waterfall-item { width: calc(50% - 12rpx); }
.waterfall-item-right { margin-top: 48rpx; }
.wf-card { background-color: #fff; border-radius: 24rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); position: relative; }
.wf-img { width: 100%; }
.wf-info { padding: 16rpx; }
.wf-title { font-size: 24rpx; color: #1C1B1B; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.wf-tag { font-size: 20rpx; color: #605E5A; }
.wf-like { position: absolute; top: 16rpx; right: 16rpx; width: 56rpx; height: 56rpx; border-radius: 50%; background-color: rgba(255,255,255,0.8); display: flex; align-items: center; justify-content: center; }
.wf-like-icon { font-family: 'Material Symbols Outlined'; font-size: 32rpx; color: #775836; }
.guide-card { margin-bottom: 32rpx; }
.guide-card-first { }
.guide-img { border-radius: 24rpx; }
.guide-img-hero { width: 100%; height: 340rpx; position: relative; }
.guide-img-thumb { width: 240rpx; height: 240rpx; }
.guide-tag { position: absolute; margin-top: -600rpx; margin-left: 24rpx; background-color: rgba(255,255,255,0.9); padding: 8rpx 24rpx; border-radius: 40rpx; font-size: 20rpx; color: #775836; font-weight: bold; }
.guide-info { }
.guide-title { font-size: 28rpx; font-weight: 600; color: #1C1B1B; }
.guide-desc { font-size: 22rpx; color: #605E5A; }
.guide-stats { display: flex; gap: 24rpx; margin-top: 8rpx; }
.guide-stat { font-size: 20rpx; color: #605E5A; font-family: 'Material Symbols Outlined'; }
.post-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 24rpx; }
.post-card { background-color: #fff; border-radius: 24rpx; overflow: hidden; box-shadow: 0 4rpx 16rpx rgba(0,0,0,0.06); }
.post-img { width: 100%; height: 340rpx; }
.post-info { padding: 16rpx; }
.post-user { display: flex; align-items: center; gap: 8rpx; margin-bottom: 8rpx; }
.post-avatar { width: 40rpx; height: 40rpx; border-radius: 50%; background-color: #E6E2DD; }
.post-name { font-size: 20rpx; font-weight: bold; color: #1C1B1B; }
.post-text { font-size: 22rpx; color: #1C1B1B; overflow: hidden; text-overflow: ellipsis; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; }
.post-actions { display: flex; justify-content: space-between; align-items: center; margin-top: 12rpx; }
.post-likes { font-size: 18rpx; color: #605E5A; font-family: 'Material Symbols Outlined'; }
.post-tag { font-size: 18rpx; color: #775836; }

</style>
