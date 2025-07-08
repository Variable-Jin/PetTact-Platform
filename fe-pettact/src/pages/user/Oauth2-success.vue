<template>
  <div>소셜 로그인 처리중...</div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';

const router = useRouter();
const route = useRoute();

onMounted(() => {
  const accessToken = route.query.accessToken;
  const refreshToken = route.query.refreshToken;

  if (accessToken && refreshToken) {
    localStorage.setItem('accessToken', accessToken);
    localStorage.setItem('refreshToken', refreshToken);
    router.push('/');
  } else {
    alert('소셜 로그인 실패');
    router.push('/login');
  }
});
</script>

