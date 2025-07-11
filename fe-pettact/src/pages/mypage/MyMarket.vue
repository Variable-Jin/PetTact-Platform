<template>
  <div class="container mt-4">
    <div class="card shadow-sm rounded">
      <div class="card-body">
        <h4 class="card-title mb-4">나의 마켓</h4>

        <!-- 로딩 상태 -->
        <div v-if="isLoading" class="text-center my-3">
          <div class="spinner-border text-primary" role="status">
            <span class="visually-hidden">로딩 중...</span>
          </div>
        </div>

        <div v-else>
          <!-- 오류 메시지 -->
          <div v-if="error" class="alert alert-danger">
            {{ error }}
          </div>

          <div v-else>
            <!-- 승인 대기 상태 -->
            <div v-if="status === 'STATUS_PENDING'" class="alert alert-info">
              판매자 권한 승인 대기중입니다.
            </div>

            <!-- 승인 완료 상태 -->
            <div v-else-if="status === 'STATUS_ACTIVE' && role === 'ROLE_SELLER'">
              <h5 class="mb-3">내 상품 관리</h5>
              <p>상품 관리 기능은 추후 구현됩니다.</p>
            </div>

            <!-- 권한 요청 버튼 -->
            <div v-else>
              <button @click="requestSellerRole" class="btn btn-success">판매자 권한 요청하기</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const status = ref('')
const role = ref('')
const error = ref('')
const isLoading = ref(true)

onMounted(() => {
  fetchSellerStatus()
})

const fetchSellerStatus = async () => {
  try {
    const res = await axios.get('/v1/user/seller/request/status')
    status.value = res.data.status

    // 현재 사용자 role 정보도 불러옴 (예: ROLE_USER, ROLE_SELLER)
    const userRes = await axios.get('/v1/user/me')
    role.value = userRes.data.userRole

  } catch (err) {
    console.error(err)
    error.value = '상태를 불러오는 데 실패했습니다.'
  } finally {
    isLoading.value = false
  }
}

const requestSellerRole = async () => {
  try {
    await axios.post('/v1/user/seller/request')
    alert('판매자 권한 요청이 접수되었습니다.')
    fetchSellerStatus()
  } catch (err) {
    console.error(err)
    alert(err.response?.data || '요청에 실패했습니다.')
  }
}
</script>