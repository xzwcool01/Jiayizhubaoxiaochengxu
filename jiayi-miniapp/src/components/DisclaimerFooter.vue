<script setup lang="ts">
const props = withDefaults(defineProps<{ content: string; textColor: string }>(), {
  content: '',
  textColor: '#999'
})
</script>

<template>
  <view class="disclaimer-wrap" v-if="props.content">
    <text class="disclaimer-text" :style="{ color: props.textColor }">{{ props.content }}</text>
  </view>
</template>

<style scoped>
.disclaimer-wrap { padding: 24rpx 32rpx 48rpx; }
.disclaimer-text { font-size: 20rpx; line-height: 1.5; }
</style>
