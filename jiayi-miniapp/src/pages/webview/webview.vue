<template>
  <web-view :src="url" />
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { onLoad } from '@dcloudio/uni-app'

const url = ref('')

onLoad((options) => {
  url.value = decodeURIComponent(options?.url || '')
})
</script>
