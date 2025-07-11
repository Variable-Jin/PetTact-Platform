<template>
  <div class="container mt-5" style="max-width: 500px;">
    <h3 class="text-center mb-4 fw-bold">비밀번호 재설정</h3>

    <!-- 로딩 -->
    <div v-if="loading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">로딩 중...</span>
      </div>
      <p class="mt-2">토큰 확인 중...</p>
    </div>

    <!-- 에러 메시지 -->
    <div v-if="error" class="alert alert-danger">
      {{ error }}
    </div>

    <!-- 비밀번호 재설정 폼 -->
    <form v-if="verified" @submit.prevent="resetPassword">
      <div class="mb-3">
        <label for="newPassword" class="form-label">새 비밀번호</label>
        <input type="password" v-model="newPassword" id="newPassword" class="form-control" required>
      </div>
      <button type="submit" class="btn btn-primary w-100 py-2">비밀번호 변경</button>
    </form>

    <!-- 성공 메시지 -->
    <div v-if="message" class="alert alert-success mt-4 text-center">
      {{ message }}
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

const route = useRoute();
const router = useRouter();

const token = route.query.token;
const userEmail = ref('');
const newPassword = ref('');
const loading = ref(true);
const verified = ref(false);
const error = ref('');
const message = ref('');

onMounted(async () => {
  try {
    const res = await axios.get('/v1/user/password/verify', { params: { token } });
    userEmail.value = res.data.email;
    verified.value = true;
  } catch (err) {
    error.value = err.response?.data || '유효하지 않거나 만료된 링크입니다.';
  } finally {
    loading.value = false;
  }
});

const resetPassword = async () => {
  try {
    const res = await axios.post('/v1/user/password/reset', {
      userEmail: userEmail.value,
      newPassword: newPassword.value
    });
    message.value = res.data;
    error.value = '';
    // 성공 후 로그인 화면으로 이동
    setTimeout(() => router.push('/user/login'), 2000);
  } catch (err) {
    message.value = '';
    error.value = err.response?.data || '오류가 발생했습니다.';
  }
};
</script>
