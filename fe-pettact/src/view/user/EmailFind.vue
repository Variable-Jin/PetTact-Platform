<template>
  <div class="container mt-5" style="max-width: 500px;">
    <h3 class="text-center mb-4 fw-bold">아이디(이메일) 찾기</h3>

    <form @submit.prevent="findEmail">
      <!-- 이름 -->
      <div class="mb-3">
        <label for="userName" class="form-label">이름</label>
        <input type="text" v-model="userName" id="userName" class="form-control" required>
      </div>

      <!-- 전화번호 -->
      <div class="mb-3">
        <label for="userTel" class="form-label">전화번호</label>
        <input type="text" v-model="userTel" id="userTel" class="form-control" required>
      </div>

      <!-- 찾기 버튼 -->
      <button type="submit" class="btn btn-primary w-100 py-2">찾기</button>
    </form>

    <!-- 결과 -->
    <div v-if="userEmail" class="alert alert-success mt-4">
      위의 정보로 가입된 이메일: {{ userEmail }}
    </div>

    <!-- 에러 메시지 -->
    <div v-if="error" class="alert alert-danger mt-4">
      {{ error }}
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import { ref } from 'vue';

const userName = ref('');
const userTel = ref('');
const userEmail = ref('');
const error = ref('');

const findEmail = async () => {
  try {
    const res = await axios.post('/v1/user/email/find', {
      userName: userName.value,
      userTel: userTel.value
    });
    userEmail.value = res.data.userEmail;
    error.value = '';
  } catch (err) {
    userEmail.value = '';
    error.value = err.response?.data || '오류가 발생했습니다.';
  }
};
</script>