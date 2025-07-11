<template>
  <div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2>게시물 상세 정보</h2>
      <button @click="goBack" class="btn btn-secondary">목록으로 돌아가기</button>
    </div>

    <div v-if="boardDetail">
      <div class="card mb-4">
        <div class="card-header">
          게시물 정보
        </div>
        <div class="card-body">
          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">게시물번호:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ boardDetail.boardNo }}</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">제목:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ boardDetail.boardTitle }}</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">내용:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ boardDetail.boardContent }}</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">카테고리:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ boardDetail.categoryName }}</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">작성자:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ boardDetail.writer }}</p>
            </div>
          </div>
        </div>
      </div>

      <div v-if="boardDetail.isDeleted" class="card mb-4">
        <div class="card-header">
          삭제 정보
        </div>
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
              <p class="form-control-plaintext">{{ formatDateTime(boardDetail.deletedAt) }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- TODO: 액션 버튼 (삭제?숨김) -->
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

const boardDetail = ref(null)
// const processing = ref(false)

const getBoardDetail = async () => {
  try {
    const boardNo = route.params.boardNo
    const res = await axios.get(`/v1/admin/board/${boardNo}`)
    boardDetail.value = res.data
  } catch (err) {
    console.error('회원 정보 조회 실패:', err)
    alert('회원 정보를 불러오는 중 오류가 발생했습니다.')
  }
}

// TODO: 관리자 게시물 숨김처리

const goBack = () => {
  router.push({ name: 'adminBoardList' })
}

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '-'
  return dateTimeStr.replace('T', ' ').split('.')[0]
}

onMounted(() => {
  getBoardDetail()
})
</script>