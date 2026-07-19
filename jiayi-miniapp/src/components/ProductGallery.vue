<script setup lang="ts">
const props = withDefaults(defineProps<{ images: string[] }>(), {
  images: () => []
})
</script>

<template>
  <view class="gallery-section" v-if="props.images.length">
    <image v-for="(img, i) in props.images" :key="i" class="gallery-img" :src="img" mode="widthFix" />
  </view>
</template>

<style scoped>
.gallery-section { }
.gallery-img { width: 100%; display: block; }
</style>
