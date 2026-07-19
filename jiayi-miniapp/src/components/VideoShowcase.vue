<script setup lang="ts">
defineProps<{ cover: string; videoUrl: string }>()
</script>

<template>
  <view class="video-section" v-if="videoUrl">
    <text class="section-label">商品展示</text>
    <view class="video-card" @tap="uni.navigateTo({ url: '/pages/webview/webview?url=' + encodeURIComponent(videoUrl) })">
      <image v-if="cover" class="video-cover" :src="cover" mode="aspectFill" />
      <view v-else class="video-cover video-cover-empty" />
      <view class="video-overlay">
        <view class="play-btn"><text class="play-icon">▶</text></view>
        <view class="play-tag">
          <text class="play-tag-text">点击看视频</text>
          <text class="play-tag-icon">↗</text>
        </view>
      </view>
      <view class="video-bottom">
        <view class="tiktok-icon">
          <svg class="tiktok-svg" viewBox="0 0 24 24" fill="#fff"><path d="M12.525.02c1.31-.02 2.61-.01 3.91-.02.08 1.53.63 3.09 1.75 4.17 1.12 1.11 2.7 1.62 4.24 1.79v4.03c-1.44-.06-2.89-.35-4.2-.97-.57-.26-1.1-.59-1.62-.93-.01 2.92.01 5.84-.02 8.75-.03 1.4-.54 2.79-1.35 3.94-1.31 1.92-3.58 3.17-5.91 3.21-1.43.08-2.86-.31-4.08-1.03-2.02-1.19-3.44-3.37-3.65-5.71-.02-.5-.03-1-.01-1.49.18-1.9 1.12-3.72 2.58-4.96 1.66-1.44 3.98-2.13 6.15-1.72.02 1.48-.04 2.96-.04 4.44-.9-.32-1.98-.23-2.81.33-.85.51-1.44 1.43-1.58 2.41-.14.99.13 2.02.74 2.81.59.75 1.55 1.21 2.52 1.29 1.02.03 2.03-.3 2.8-.99.65-.58.99-1.43 1.01-2.29.03-4.39.02-8.78.02-13.17z"/></svg>
        </view>
        <text class="video-bottom-text">前往抖音观看</text>
      </view>
    </view>
  </view>
</template>

<style scoped>
.video-section { margin: 0; }
.section-label { font-size: 28rpx; font-weight: 600; color: #1C1B1B; display: block; margin-bottom: 16rpx; }
.video-card { position: relative; border-radius: 20rpx; overflow: hidden; }
.video-cover { width: 100%; height: 360rpx; display: block; filter: brightness(0.75); }
.video-cover-empty { background: #333; }
.video-overlay {
  position: absolute; top: 0; left: 0; right: 0; bottom: 0;
  display: flex; flex-direction: column; align-items: center; justify-content: center;
  gap: 12rpx;
}
.play-btn {
  width: 96rpx; height: 96rpx; border-radius: 50%;
  background: rgba(255,255,255,0.2); border: 2rpx solid rgba(255,255,255,0.3);
  display: flex; align-items: center; justify-content: center;
}
.play-icon { font-size: 40rpx; color: #fff; margin-left: 4rpx; }
.play-tag {
  display: flex; align-items: center; gap: 6rpx;
  background: rgba(0,0,0,0.2); padding: 6rpx 24rpx; border-radius: 999rpx;
  border: 2rpx solid rgba(255,255,255,0.1);
}
.play-tag-text { font-size: 24rpx; font-weight: 600; color: #fff; }
.play-tag-icon { font-size: 22rpx; color: #fff; }
.video-bottom {
  position: absolute; bottom: 20rpx; left: 24rpx;
  display: flex; align-items: center; gap: 8rpx;
}
.tiktok-icon {
  width: 36rpx; height: 36rpx; border-radius: 50%; background: #000;
  display: flex; align-items: center; justify-content: center;
}
.tiktok-svg { width: 20rpx; height: 20rpx; }
.video-bottom-text { font-size: 20rpx; color: rgba(255,255,255,0.8); }
</style>
