<template>
  <div class="modal-overlay" @click.self="emit('close')">
    <div class="modal-box">
      <h5>닉네임으로 사용자 검색</h5>
      <input v-model="nickName" placeholder="닉네임 입력" @keyup.enter="search" />
      <button @click="search">검색</button>
      <div v-if="errorMessage" style="color: red; margin-top: 10px;">
        {{ errorMessage }}
      </div>
      <div v-if="result">
        <p>이름: {{ result.userName }}</p>
        <p>생년월일: {{ (result.userBirth) }}</p>
        <button @click="requestChat">채팅 신청</button>
      </div>

      <button @click="emit('close')">닫기</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from '@/js/axios'

const emit = defineEmits(['close', 'roomOpen']);

const nickName = ref('');
const result = ref(null);
const currentUserNo = 1;
const errorMessage = ref('');
function search() {
  errorMessage.value = ''; 
  if (!nickName.value.trim()) {
    alert('닉네임을 입력해주세요');
    return;
  }


  axios.get(`/chat/user/${nickName.value}`)
    .then(res => {
      result.value = res.data;
    })
    .catch(() => {
      errorMessage.value = '해당 회원을 찾을 수 없습니다.';
    });
}

function requestChat() {
  axios.post(`/chat/room/create/${result.value.userNo}`, null).then(res => {
    emit('roomOpen', res.data.roomNo); // 채팅방 번호 전달
    emit('close');
  }).catch(() => {
    alert('채팅방 생성 중 오류가 발생했습니다.');
  });
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.modal-box {
  background: salmon;
  padding: 20px;
  width: 360px;
  border-radius: 10px;
}
</style>
