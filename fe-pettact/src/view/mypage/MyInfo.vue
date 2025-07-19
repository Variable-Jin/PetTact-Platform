<template>
  <div class="container mt-4">
    <div class="card shadow-sm rounded">
      <div class="card-body">
        <h4 class="card-title mb-4">나의 정보</h4>

        <div v-if="error" class="alert alert-danger">
          {{ error }}
        </div>

        <div v-if="userInfo">
          <div class="mb-3">
            <strong>이메일:</strong>
            <div>{{ userInfo.userEmail }}</div>
          </div>
          <div class="mb-3">
            <strong>이름:</strong>
            <div>{{ userInfo.userName }}</div>
          </div>
          <div class="mb-3">
            <strong>생년월일:</strong>
            <div>{{ userInfo.userBirth }}</div>
          </div>
          <div class="mb-3">
            <strong>닉네임:</strong>
            <div>{{ userInfo.userNickname }}</div>
          </div>
          <div class="mb-3">
            <strong>전화번호:</strong>
            <div>{{ userInfo.userTel }}</div>
          </div>
          <div class="mb-3">
            <strong>우편번호:</strong>
            <div>{{ userInfo.userZipcode }}</div>
          </div>
          <div class="mb-3">
            <strong>주소:</strong>
            <div>{{ userInfo.userStreet1 }} {{ userInfo.userDetailAddress }}</div>
          </div>
        </div>

        <div class="d-flex flex-column flex-md-row gap-2 mt-4">
          <button class="btn" @click="showUpdateModal = true">정보 수정</button>
          <button class="btn" @click="showWithdrawModal = true">탈퇴하기</button>

          <VerifyPasswordModal v-if="showUpdateModal" @close="showUpdateModal = false" :onVerified="goToUpdate" />
          <VerifyPasswordModal v-if="showWithdrawModal" @close="showWithdrawModal = false" :onVerified="withdraw" />
          <!-- <button @click="handleRequestSeller" class="btn">
            판매자 권한 요청하기
          </button> -->
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import axios from 'axios';
import { onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import VerifyPasswordModal from './components/VerifyPasswordModal.vue';

const userInfo = ref('');
const error = ref('');
const router = useRouter();

const showUpdateModal = ref(false);
const showWithdrawModal = ref(false);

onMounted(async () => {
  try {
    const res = await axios.get('/v1/user/mypage/myInfo');
    userInfo.value = res.data;
  } catch (err) {
    console.error(err);
    if (err.response && err.response.status === 401) {
      alert(err.response.data);
      router.push({ name: 'login' });
    } else {
      error.value = err.response?.data || '오류가 발생했습니다.';
    }
  }
});

const goToUpdate = () => {
  router.push({ name: 'myInfoUpdate' });
};

const withdraw = async () => {
  try {
    await axios.delete('/v1/user/mypage/withdraw');
    alert('탈퇴 완료');
    router.push('/');
  } catch {
    alert('탈퇴 실패');
  }
};

const handleRequestSeller = async () => {
  if (!confirm('판매자 권한을 요청하시겠습니까?')) return;

  try {
    const res = await axios.post('/v1/user/seller/request');
    alert('판매자 권한 요청이 접수되었습니다.');
  } catch (err) {
    console.error(err);
    alert(err.response.data)
  }
}
</script>