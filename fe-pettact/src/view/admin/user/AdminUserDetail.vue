<template>
  <div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2>회원 상세 정보</h2>
      <button @click="goBack" class="btn btn-secondary">목록으로 돌아가기</button>
    </div>

    <div v-if="userDetail">
      <div class="card mb-4">
        <div class="card-header">기본 정보</div>
        <div class="card-body">
          <div class="mb-3 row" v-for="(value, label) in {
            '회원번호': userDetail.userNo,
            '이름': userDetail.userName,
            '닉네임': userDetail.userNickname,
            '이메일': userDetail.userEmail,
            '연락처': userDetail.userTel,
            '생년월일': userDetail.userBirth || '-'
          }" :key="label">
            <label class="col-sm-2 col-form-label">{{ label }}</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ value }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="card mb-4">
        <div class="card-header">주소 정보</div>
        <div class="card-body">
          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">우편번호:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ userDetail.userZipcode || '-' }}</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">주소:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ userDetail.userStreet1 }} {{ userDetail.userDetailAddress }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="card mb-4">
        <div class="card-header">계정 정보</div>
        <div class="card-body">
          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">가입일:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ formatDateTime(userDetail.createdAt) }}</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">최종 수정일:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ formatDateTime(userDetail.updatedAt) }}</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">권한:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ userDetail.roleCode }}</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">상태:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext" :class="getStatusClass()">{{ userDetail.statusCode }}</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">이메일 수신 여부:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext" :class="userDetail.userEmailChecked ? 'text-success' : 'text-danger'">
                {{ userDetail.userEmailChecked ? '동의' : '미동의' }}
              </p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">반려동물 보유:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ userDetail.userHasPet ? 'Y' : 'N' }}</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">가입 방식:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ userDetail.provider || '일반' }}</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">블랙리스트:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext" :class="userDetail.userBlacklist ? 'text-danger' : 'text-success'">
                {{ userDetail.userBlacklist ? '차단됨' : '정상' }}
              </p>
            </div>
          </div>
        </div>
      </div>

      <div v-if="userDetail.isDeleted" class="card mb-4">
        <div class="card-header">삭제 정보</div>
        <div class="card-body">
          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">삭제 상태:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">삭제됨</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">삭제일:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ formatDateTime(userDetail.deletedAt) }}</p>
            </div>
          </div>
        </div>
      </div>

      <div v-if="!userDetail.isDeleted" class="d-flex gap-2">
        <button v-if="userDetail.statusCode !== 'STATUS_BLOCKED'" @click="blockUser" class="btn btn-danger">
          {{ processing ? '처리중...' : '회원 잠금' }}
        </button>
        <button v-else-if="userDetail.statusCode === 'STATUS_BLOCKED'" @click="unblockUser" class="btn btn-success">
          {{ processing ? '처리중...' : '잠금 해제' }}
        </button>
      </div>
    </div>

    <div v-else class="alert alert-danger">
      회원 정보를 불러올 수 없습니다.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const userDetail = ref(null)
const processing = ref(false)

const getUserDetail = async () => {
  try {
    const userNo = route.params.userNo
    const res = await axios.get(`/v1/admin/user/${userNo}`)
    userDetail.value = res.data
  } catch (err) {
    console.error('회원 정보 조회 실패:', err)
    alert('회원 정보를 불러오는 중 오류가 발생했습니다.')
  }
}

const blockUser = async () => {
  if (!confirm('정말로 이 회원을 잠금 처리하시겠습니까?')) return
  
  try {
    processing.value = true
    const userNo = userDetail.value.userNo
    const res = await axios.post(`/v1/admin/user/${userNo}/block`)
    
    alert(res.data)
    // 정보 새로고침
    await getUserDetail()
  } catch (err) {
    console.error('회원 잠금 실패:', err)
    alert(err.response?.data || '회원 잠금 처리 중 오류가 발생했습니다.')
  } finally {
    processing.value = false
  }
}

const unblockUser = async () => {
  if (!confirm('정말로 이 회원의 잠금을 해제하시겠습니까?')) return
  
  try {
    processing.value = true
    const userNo = userDetail.value.userNo
    const res = await axios.post(`/v1/admin/user/${userNo}/unblock`)
    
    alert(res.data)
    // 정보 새로고침
    await getUserDetail()
  } catch (err) {
    console.error('잠금 해제 실패:', err)
    alert(err.response?.data || '잠금 해제 처리 중 오류가 발생했습니다.')
  } finally {
    processing.value = false
  }
}

const goBack = () => {
  router.push({ name: 'adminUserList' })
}

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '-'
  return dateTimeStr.replace('T', ' ').split('.')[0]
}

const getStatusClass = () => {
  if (!userDetail.value) return ''
  if (userDetail.value.userBlacklist) return 'blocked'
  return 'normal'
}

onMounted(() => {
  getUserDetail()
})
</script>