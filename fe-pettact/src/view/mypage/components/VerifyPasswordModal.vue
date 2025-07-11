<template>
  <div class="modal fade show" style="display:block;" tabindex="-1" role="dialog">
    <div class="modal-dialog" role="document">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">비밀번호 확인</h5>
          <button type="button" class="btn-close" @click="$emit('close')"></button>
        </div>
        <div class="modal-body">
          <p>보안을 위해 비밀번호를 다시 입력해주세요.</p>
          
          <input type="password" v-model="inputPassword" class="form-control mb-2" placeholder="비밀번호 입력" />
          
          <button class="btn btn-primary" @click="verifyPassword">확인</button>
          
          <p v-if="message" :class="isVerified ? 'text-success' : 'text-danger'">
            {{ message }}
          </p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

const props = defineProps({
  onVerified: Function,
});

const emits = defineEmits(['close']);

const inputPassword = ref('');
const message = ref('');
const isVerified = ref(false);

const verifyPassword = async () => {
  try {
    const res = await axios.post('/v1/user/mypage/verify-password', {
      inputPassword: inputPassword.value,
    });
    message.value = res.data;
    isVerified.value = true;
    
    if (props.onVerified) props.onVerified();

    emits('close');
  } catch (err) {
    message.value = err.response?.data || '오류가 발생했습니다.';
    isVerified.value = false;
  }
};
</script>
