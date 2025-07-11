<template>
  <div class="container mt-4">
    <h2 class="mb-4 border-bottom pb-2">판매자 권한 요청 대기중 목록</h2>

    <div v-if="isLoading" class="text-center">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">로딩 중...</span>
      </div>
    </div>

    <div v-else>
      <div v-if="error" class="alert alert-danger">{{ error }}</div>

      <div v-else>
        <div class="table-responsive">
          <table class="table table-striped table-hover align-middle">
            <thead class="table-dark">
              <tr>
                <th>#</th>
                <th>이메일</th>
                <th>닉네임</th>
                <th>상태</th>
                <th>처리</th>
                <th>회원 상세정보</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(seller, index) in sellerList" :key="seller.userNo">
                <td>{{ index + 1 }}</td>
                <td>{{ seller.userEmail }}</td>
                <td>{{ seller.userNickname }}</td>
                <td>{{ seller.statusCode }}</td>
                <td>
                  <button type="button" class="btn btn-sm btn-success" @click="approveSeller(seller.userNo)">승인</button>
                </td>
                <td>
                  <button type="button" class="btn btn-sm btn-outline-primary" @click="showDetail(seller.userNo)">상세정보</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>

        <div v-if="sellerList.length === 0" class="text-center mt-3">
          승인 대기중인 요청이 없습니다.
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useRouter } from 'vue-router'

const sellerList = ref([])
const isLoading = ref(true)
const error = ref('')

const router = useRouter()

onMounted(() => {
  fetchSellerList()
})

const fetchSellerList = async () => {
  try {
    const res = await axios.get('/v1/admin/seller/requests')
    sellerList.value = res.data
  } catch (err) {
    console.error(err)
    error.value = '판매자 권한 요청 목록을 불러오는 데 실패했습니다.'
  } finally {
    isLoading.value = false
  }
}

const approveSeller = async (userNo) => {
  if (!confirm('해당 사용자의 판매자 권한을 승인하시겠습니까?')) return

  try {
    await axios.patch(`/v1/admin/seller/${userNo}/approve`)
    alert('판매자 권한이 승인되었습니다.')
    fetchSellerList()
  } catch (err) {
    console.error(err)
    alert(err.response?.data || '승인 처리 중 오류가 발생했습니다.')
  }
}

const showDetail = (userNo) => {
  router.push({ name: 'adminUserDetail', params: { userNo } })
}
</script>