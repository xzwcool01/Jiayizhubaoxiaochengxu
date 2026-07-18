<script setup lang="ts">
const level = '钻石会员'
const points = 3580
const memberNo = '8829 1042'
const since = '2023'
const nextLevel = '皇冠会员'
const pointsNext = 5000
const progress = Math.round((points / pointsNext) * 100)

const benefits = [
  { icon: 'auto_awesome', label: 'AI试戴上限', desc: '每日限额50次' },
  { icon: 'cake', label: '生日礼遇', desc: '专享神秘珠宝礼' },
  { icon: 'local_shipping', label: '全场包邮', desc: '顺丰尊享配送' },
  { icon: 'support_agent', label: '专属客服', desc: '24/7私人顾问' }
]

const recommendations = [
  { name: '永恒之光项链', price: '12,800', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuAHDsHxZR5CDIHz1sM0CPRPYiEZKG3UccusQVQBb_-LCR11wF3xnN3huxtXrr0WEuwEVAmtlFo2Nk81VCkbxrbB2Ad8Sgsd1uSfAgaXhOnk569wFswATReRnZj4FLeRetdaQSgRMt3LCfc2Ezosq9DNX1lg8OdpZNHAbYYOKqiH5KIFcl5sRY486Rr2odN4s-cSIepj1_77GMp9XegSuq5XxBJxCsimifsjhLVMFfLv7YlNGEuJmHTG3Q' },
  { name: '优雅绽放戒指', price: '8,500', img: 'https://lh3.googleusercontent.com/aida-public/AB6AXuA0d6kOH8GKvyZk-_U5x9JbkFFGwPyezXICfx6-M1jgeR0HmSazSuOHmXRb15am0y-Jj_zS64fi1g5kIeMbaZi8doo6lZnrE4BFfzvDiw_o0Ey8q-iHglo_sYAvb0bn6mnknRwA52iN74zsY6_xuzGp8YAaCarB7jUXOhU2iq6gzShtRcI4nOBNkrQIAIKZzB0NuJwDzlrG_k7bDVxePfz3jzYVUxtMEEZkHB5Yzn7Htf-aaRNqK2pCVw' }
]

const menus = [
  { icon: 'inventory_2', label: '我的订单', color: '#775836' },
  { icon: 'favorite', label: '我的收藏', color: '#CF6679' },
  { icon: 'location_on', label: '收货地址', color: '#A67B52' },
  { icon: 'card_giftcard', label: '优惠券', color: '#C9A52D' },
  { icon: 'support_agent', label: '联系客服', color: '#605E5A' },
  { icon: 'settings', label: '设置', color: '#8C9399' }
]
</script>

<template>
  <view class="page">
    <view class="top-bar">
      <view class="back-btn" @tap="uni.navigateBack()"><text class="back-icon">arrow_back</text></view>
      <text class="top-title">会员中心</text>
    </view>

    <!-- Member Card -->
    <view class="member-card">
      <view class="card-top">
        <view class="card-user">
          <view class="card-avatar" />
          <view>
            <text class="card-name">嘉怡贵宾</text>
            <view class="card-badge">
              <text class="badge-icon">diamond</text>
              <text class="badge-text">{{ level }}</text>
            </view>
          </view>
        </view>
        <text class="card-qr">qr_code_2</text>
      </view>
      <view class="card-bottom">
        <view>
          <text class="card-meta-label">Membership No.</text>
          <text class="card-meta-no">{{ memberNo }}</text>
        </view>
        <text class="card-since">自 {{ since }} 年起</text>
      </view>
    </view>

    <!-- Points & Progress -->
    <view class="points-section">
      <view class="points-header">
        <view>
          <text class="points-label">当前可用积分</text>
          <text class="points-value">{{ points.toLocaleString() }}</text>
        </view>
        <view class="points-link" @tap="uni.navigateTo({url:'/pages/points/index'})">
          <text>积分商城</text>
          <text class="link-arrow">chevron_right</text>
        </view>
      </view>
      <view class="progress-card">
        <view class="progress-row">
          <text class="progress-current">{{ level }}</text>
          <text class="progress-next">下一等级: {{ nextLevel }} ({{ pointsNext.toLocaleString() }})</text>
        </view>
        <view class="progress-bar"><view class="progress-fill" :style="'width:' + progress + '%'" /></view>
        <text class="progress-hint">距离升级还需 {{ (pointsNext - points).toLocaleString() }} 积分，继续加油！</text>
      </view>
    </view>

    <!-- Daily Check-in -->
    <view class="checkin-card">
      <view class="checkin-left">
        <view class="checkin-icon"><text>calendar_today</text></view>
        <view>
          <text class="checkin-title">每日签到</text>
          <text class="checkin-desc">今日签到可领 10 积分</text>
        </view>
      </view>
      <view class="checkin-btn"><text>立即签到</text></view>
    </view>

    <!-- Exclusive Benefits -->
    <view class="benefits">
      <text class="benefits-title">专属权益</text>
      <view class="benefits-grid">
        <view v-for="(b, i) in benefits" :key="i" class="benefit-item">
          <view class="benefit-icon"><text>{{ b.icon }}</text></view>
          <text class="benefit-label">{{ b.label }}</text>
          <text class="benefit-desc">{{ b.desc }}</text>
        </view>
      </view>
    </view>

    <!-- Recommended Products -->
    <view class="recommend">
      <view class="recommend-header">
        <text class="recommend-title">会员推荐</text>
        <text class="recommend-sub">专属定制</text>
      </view>
      <scroll-view class="recommend-scroll" scroll-x enhanced show-scrollbar="false">
        <view class="recommend-track">
          <view v-for="(r, i) in recommendations" :key="i" class="recommend-card">
            <image class="recommend-img" :src="r.img" mode="aspectFill" />
            <view class="recommend-info">
              <text class="recommend-name">{{ r.name }}</text>
              <text class="recommend-price">¥{{ r.price }}</text>
              <view class="recommend-btn"><text>会员专享</text></view>
            </view>
          </view>
        </view>
      </scroll-view>
    </view>

    <!-- Menu List -->
    <view class="menu-list">
      <view v-for="(m, i) in menus" :key="i" class="menu-item" @tap="uni.showToast({title:m.label + '（开发中）', icon:'none'})">
        <view class="menu-left">
          <view class="menu-icon-wrap" :style="'background-color:' + m.color + '20'">
            <text class="menu-icon" :style="'color:' + m.color">{{ m.icon }}</text>
          </view>
          <text class="menu-label">{{ m.label }}</text>
        </view>
        <text class="menu-arrow">chevron_right</text>
      </view>
    </view>
  </view>
</template>

<style scoped>
.page { background-color: #FAFAF8; min-height: 100vh; padding-bottom: 48rpx; }
.top-bar { display: flex; align-items: center; gap: 16rpx; padding: 32rpx; height: 112rpx; }
.back-icon { font-family: 'Material Symbols Outlined'; font-size: 40rpx; color: #1C1B1B; }
.top-title { font-size: 48rpx; color: #775836; font-weight: 600; font-family: 'Noto Serif SC', serif; }

/* Member Card */
.member-card { background: linear-gradient(135deg, #775836, #C8A27A, #E8BF95); margin: 0 32rpx; border-radius: 24rpx; padding: 32rpx; position: relative; overflow: hidden; }
.member-card::after { content: ''; position: absolute; top: -20%; right: -10%; width: 300rpx; height: 300rpx; background: radial-gradient(circle, rgba(255,255,255,0.15), transparent 70%); border-radius: 50%; }
.card-top { display: flex; justify-content: space-between; align-items: flex-start; position: relative; z-index: 1; }
.card-user { display: flex; align-items: center; gap: 16rpx; }
.card-avatar { width: 112rpx; height: 112rpx; border-radius: 50%; border: 4rpx solid #E8D5C0; background: rgba(255,255,255,0.2); }
.card-name { font-size: 36rpx; font-weight: 600; color: #fff; font-family: 'Noto Serif SC', serif; }
.card-badge { display: flex; align-items: center; gap: 4rpx; background: rgba(255,255,255,0.2); padding: 4rpx 16rpx; border-radius: 40rpx; margin-top: 8rpx; }
.badge-icon { font-family: 'Material Symbols Outlined'; font-size: 24rpx; color: #E9C349; }
.badge-text { font-size: 22rpx; color: #fff; font-weight: 500; }
.card-qr { font-family: 'Material Symbols Outlined'; font-size: 48rpx; color: rgba(255,255,255,0.8); }
.card-bottom { display: flex; justify-content: space-between; align-items: flex-end; margin-top: 32rpx; position: relative; z-index: 1; }
.card-meta-label { font-size: 18rpx; color: rgba(255,255,255,0.7); text-transform: uppercase; letter-spacing: 2px; }
.card-meta-no { font-size: 28rpx; color: #fff; font-family: monospace; letter-spacing: 2px; display: block; }
.card-since { font-size: 20rpx; color: rgba(255,255,255,0.8); }

/* Points */
.points-section { padding: 48rpx 32rpx 0; }
.points-header { display: flex; justify-content: space-between; align-items: flex-end; }
.points-label { font-size: 20rpx; color: #605E5A; letter-spacing: 1px; }
.points-value { font-size: 56rpx; font-weight: 700; color: #775836; }
.points-link { display: flex; align-items: center; gap: 4rpx; font-size: 22rpx; color: #775836; font-weight: bold; }
.link-arrow { font-family: 'Material Symbols Outlined'; font-size: 28rpx; }
.progress-card { background: #fff; border-radius: 24rpx; padding: 24rpx; margin-top: 24rpx; border: 2rpx solid rgba(210,196,184,0.3); }
.progress-row { display: flex; justify-content: space-between; font-size: 20rpx; color: #605E5A; margin-bottom: 12rpx; }
.progress-current { font-weight: 600; color: #1C1B1B; }
.progress-bar { height: 12rpx; background-color: #F0EDED; border-radius: 12rpx; overflow: hidden; }
.progress-fill { height: 100%; background-color: #775836; border-radius: 12rpx; }
.progress-hint { font-size: 18rpx; color: #605E5A; font-style: italic; margin-top: 8rpx; }

/* Check-in */
.checkin-card { display: flex; align-items: center; justify-content: space-between; margin: 32rpx 32rpx 0; background: rgba(200,162,122,0.1); border-radius: 24rpx; padding: 24rpx; border: 2rpx solid rgba(200,162,122,0.2); }
.checkin-left { display: flex; align-items: center; gap: 16rpx; }
.checkin-icon { width: 80rpx; height: 80rpx; border-radius: 50%; background: rgba(200,162,122,0.2); display: flex; align-items: center; justify-content: center; font-family: 'Material Symbols Outlined'; font-size: 40rpx; color: #775836; }
.checkin-title { font-size: 28rpx; font-weight: bold; color: #775836; }
.checkin-desc { font-size: 20rpx; color: #605E5A; }
.checkin-btn { background-color: #775836; color: #fff; padding: 12rpx 40rpx; border-radius: 60rpx; font-size: 22rpx; font-weight: bold; }

/* Benefits */
.benefits { padding: 48rpx 32rpx; }
.benefits-title { font-size: 36rpx; font-weight: 600; color: #775836; font-family: 'Noto Serif SC', serif; margin-bottom: 24rpx; }
.benefits-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16rpx; }
.benefit-item { background: #fff; border-radius: 24rpx; padding: 24rpx; text-align: center; border: 2rpx solid rgba(210,196,184,0.2); }
.benefit-icon { width: 80rpx; height: 80rpx; border-radius: 50%; background: rgba(232,213,192,0.2); display: flex; align-items: center; justify-content: center; margin: 0 auto; font-family: 'Material Symbols Outlined'; font-size: 44rpx; color: #775836; }
.benefit-label { font-size: 24rpx; font-weight: 600; color: #1C1B1B; display: block; margin-top: 12rpx; }
.benefit-desc { font-size: 20rpx; color: #605E5A; display: block; margin-top: 4rpx; }

/* Recommended */
.recommend { padding: 0 32rpx 48rpx; }
.recommend-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24rpx; }
.recommend-title { font-size: 36rpx; font-weight: 600; color: #775836; font-family: 'Noto Serif SC', serif; }
.recommend-sub { font-size: 20rpx; color: #605E5A; font-style: italic; }
.recommend-scroll { overflow: hidden; margin: 0 -32rpx; padding: 0 32rpx; }
.recommend-track { display: flex; gap: 24rpx; }
.recommend-card { min-width: 340rpx; background: #fff; border-radius: 24rpx; overflow: hidden; border: 2rpx solid rgba(210,196,184,0.2); }
.recommend-img { width: 100%; height: 320rpx; }
.recommend-info { padding: 16rpx; }
.recommend-name { font-size: 24rpx; color: #1C1B1B; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.recommend-price { font-size: 28rpx; color: #775836; font-weight: bold; display: block; margin-top: 8rpx; }
.recommend-btn { border: 2rpx solid #775836; color: #775836; font-size: 20rpx; font-weight: bold; text-align: center; padding: 8rpx; border-radius: 8rpx; margin-top: 12rpx; }

/* Menu List */
.menu-list { margin: 0 32rpx; border-radius: 24rpx; overflow: hidden; background: #fff; }
.menu-item { display: flex; justify-content: space-between; align-items: center; padding: 32rpx; border-bottom: 2rpx solid rgba(217,210,204,0.3); }
.menu-item:last-child { border-bottom: none; }
.menu-left { display: flex; align-items: center; gap: 24rpx; }
.menu-icon-wrap { width: 72rpx; height: 72rpx; border-radius: 50%; display: flex; align-items: center; justify-content: center; }
.menu-icon { font-family: 'Material Symbols Outlined'; font-size: 36rpx; }
.menu-label { font-size: 26rpx; color: #1C1B1B; font-weight: 500; }
.menu-arrow { font-family: 'Material Symbols Outlined'; font-size: 36rpx; color: #D2C4B8; }
</style>
