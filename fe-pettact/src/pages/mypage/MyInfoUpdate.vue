<template>
  <div class="container mt-4">
    <div class="card shadow-sm rounded">
      <div class="card-body">
        <h4 class="card-title mb-4">회원 정보 수정</h4>

        <div v-if="error" class="alert alert-danger">
          {{ error }}
        </div>
        <div v-if="success" class="alert alert-success">
          {{ success }}
        </div>

        <form @submit.prevent="handleUpdate">
          <div class="mb-3">
            <label class="form-label">이메일</label>
            <input type="email" :value="userEmail" class="form-control" readonly />
          </div>

          <div class="mb-3">
            <label for="userNickname" class="form-label">닉네임</label>
            <div class="input-group">
              <input type="text" v-model="form.userNickname" id="userNickname" class="form-control" required />
              <button type="button" @click="checkNickname" class="btn btn-outline-secondary">중복확인</button>
            </div>
            <div v-if="nicknameAvailable === true" class="form-text text-success">사용 가능</div>
            <div v-else-if="nicknameAvailable === false" class="form-text text-danger">이미 사용 중</div>
          </div>

          <div class="mb-3">
            <label class="form-label">전화번호</label>
            <input type="tel" v-model="form.userTel" class="form-control" required />
          </div>

          <div class="mb-3">
            <label class="form-label">생년월일</label>
            <input type="date" :value="userBirth" class="form-control" readonly />
          </div>

          <div class="mb-3">
            <label for="userZipcode" class="form-label">우편번호</label>
            <input type="text" v-model="form.userZipcode" id="userZipcode" class="form-control" />
          </div>

          <div class="mb-3">
            <label for="userStreet1" class="form-label">주소</label>
            <input type="text" v-model="form.userStreet1" id="userStreet1" class="form-control" />
          </div>

          <div class="mb-3">
            <label for="userDetailAddress" class="form-label">상세주소</label>
            <input type="text" v-model="form.userDetailAddress" id="userDetailAddress" class="form-control" />
          </div>

          <div class="d-flex gap-2 mt-4">
            <button type="submit" class="btn btn-primary">저장</button>
            <button type="button" @click="router.push({ name: 'myInfo' })" class="btn btn-secondary">취소</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const router = useRouter()
const form = ref({
  userNickname: '',
  userTel: '',
  userZipcode: '',
  userStreet1: '',
  userDetailAddress: ''
})
const userEmail = ref('')
const userBirth = ref('')
const error = ref('')
const success = ref('')
const nicknameAvailable = ref(null)

// 기존 정보 조회
onMounted(async () => {
  try {
    const res = await axios.get('/v1/user/mypage/myInfo')
    const data = res.data
    userEmail.value = data.userEmail
    userBirth.value = data.userBirth
    form.value.userNickname = data.userNickname
    form.value.userTel = data.userTel
    form.value.userZipcode = data.userZipcode
    form.value.userStreet1 = data.userStreet1
    form.value.userDetailAddress = data.userDetailAddress
  } catch (err) {
    console.error(err)
    error.value = '회원 정보를 불러오는데 실패했습니다.'
  }
})

watch(() => form.value.userNickname, () => {
    nicknameAvailable.value = null
})
// 닉네임 중복확인
const checkNickname = async () => {
  nicknameAvailable.value = null
  if (!form.value.userNickname) {
    alert('닉네임을 입력하세요.')
    return
  }
  try {
    const res = await axios.get(`/v1/user/nickname/check?nickname=${form.value.userNickname}`)
    nicknameAvailable.value = !res.data
  } catch (err) {
    console.error(err)
    error.value = '닉네임 중복 확인 실패'
  }
}

// 정보 수정
const handleUpdate = async () => {
  error.value = ''
  success.value = ''

  if (!nicknameAvailable.value) {
    error.value = '닉네임 중복확인을 해주세요.'
    return
  }

  try {
    await axios.patch('/v1/user/mypage/update', form.value)
    success.value = '회원 정보가 성공적으로 수정되었습니다.'
    router.push({ name: 'myInfo' })
  } catch (err) {
    console.error(err)
    error.value = err.response?.data || '수정 실패'
  }
}
</script>