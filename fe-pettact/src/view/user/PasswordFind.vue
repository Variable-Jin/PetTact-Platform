<template>
  <div class="container mt-5" style="max-width: 500px;">
    <h3 class="text-center mb-4 fw-bold">비밀번호 찾기</h3>

    <form @submit.prevent="sendResetMail">
      <!-- 이메일 -->
      <div class="mb-3">
        <label for="userEmail" class="form-label">이메일</label>
        <input type="email" v-model="userEmail" id="userEmail" class="form-control" required>
      </div>

      <!-- 제출 버튼 -->
      <button type="submit" class="btn btn-primary w-100 py-2">비밀번호 재설정 메일 보내기</button>
    </form>

    <!-- 성공 메시지 -->
    <div v-if="message" class="alert alert-success mt-4">
      {{ message }}
    </div>

    <!-- 에러 메시지 -->
    <div v-if="error" class="alert alert-danger mt-4">
      {{ error }}
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

const userEmail = ref('');
const message = ref('');
const error = ref('');

const sendResetMail = async () => {
  try {
    const res = await axios.post('/v1/user/password/send', null, {
      params: { userEmail: userEmail.value }
    });
    message.value = res.data; // "비밀번호 재설정 링크를 이메일로 전송했습니다." 예상
    error.value = '';
  } catch (err) {
    message.value = '';
    error.value = err.response?.data || '오류가 발생했습니다.';
  }
};
</script>
