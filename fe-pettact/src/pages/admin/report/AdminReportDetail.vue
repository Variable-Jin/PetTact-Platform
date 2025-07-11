<template>
  <div class="container mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
      <h2>신고 상세 정보</h2>
      <button @click="goBack" class="btn btn-secondary">목록으로 돌아가기</button>
    </div>

    <div v-if="reportDetail">
      <div class="card mb-4">
        <div class="card-header">
          신고 정보
        </div>
        <div class="card-body">
          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">신고번호:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ reportDetail.reportNo }}</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">대상번호:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ reportDetail.reportTargetNo }}</p>
            </div>
          </div>

          <div class="mb-3 row align-items-center">
            <label class="col-sm-2 col-form-label">대상종류:</label>
            <div class="col-sm-7">
              <p class="form-control-plaintext">{{ reportDetail.reportTargetLocation }}</p>
            </div>
            <div class="col-sm-3">
              <button @click="goToTarget" class="btn btn-outline-primary btn-sm">대상 페이지로 이동</button>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">신고자:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ reportDetail.userNo }}</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">사유:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ reportDetail.reportReason }}</p>
            </div>
          </div>
          
          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">신고자 IP:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ reportDetail.reportIP }}</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">상태:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ statusLabel(reportDetail.reportStatus) }}</p>
            </div>
          </div>

          <div class="mb-3 row">
            <label class="col-sm-2 col-form-label">신고일자:</label>
            <div class="col-sm-10">
              <p class="form-control-plaintext">{{ formatDateTime(reportDetail.createdAt) }}</p>
            </div>
          </div>
        </div>
      </div>

      <div class="d-flex gap-2">
        <button @click="updateStatus(1)" class="btn btn-success">승인</button>
        <button @click="updateStatus(2)" class="btn btn-warning">반려</button>
        <button @click="deleteReport" class="btn btn-danger">삭제</button>
      </div>
    </div>

    <div v-else class="alert alert-danger">
      신고 정보를 불러올 수 없습니다.
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()

const reportDetail = ref(null)

const getReportDetail = async () => {
  try {
    const reportNo = route.params.reportNo
    const res = await axios.get(`/v1/admin/report/${reportNo}`)
    reportDetail.value = res.data
    console.log("res : ", res)
  } catch (err) {
    console.error('신고 상세 조회 실패:', err)
    alert(err.response?.data?.message || '신고 상세 조회 중 오류가 발생했습니다.')
  }
}

const updateStatus = async (status) => {
  if (!confirm(`해당 신고를 ${statusLabel(status)} 처리하시겠습니까?`)) return
  console.log("status", status)

  try {
    await axios.patch(`/v1/admin/report/${reportDetail.value.reportNo}/status`, null, {
        params: { status }
    })
    alert('상태 변경 완료')
    await getReportDetail()
  } catch (err) {
    console.error('상태 변경 실패:', err)
    alert(err.response?.data?.message || '상태 변경 중 오류가 발생했습니다.')
  }
}

const deleteReport = async () => {
  if (!confirm('정말로 이 신고를 삭제하시겠습니까?')) return

  try {
    await axios.delete(`/v1/admin/report/${reportDetail.value.reportNo}`)
    alert('삭제 완료')
    goBack()
  } catch (err) {
    console.error('삭제 실패:', err)
    alert(err.response?.data?.message || '삭제 중 오류가 발생했습니다.')
  }
}

const goBack = () => {
  router.push({ name: 'adminReportList' })
}

const goToTarget = () => {
  const { reportTargetLocation, reportTargetNo } = reportDetail.value

  switch (reportTargetLocation) {
    case 'BOARD':
      router.push({ name: 'adminBoardDetail', params: { boardNo: reportTargetNo } })
      break
    // case 'REPLY':
    //   // 댓글 상세가 없다면 게시물 상세 + anchor 이동
    //   const boardNo = reportDetail.value.boardNo // 백엔드에서 댓글 신고 시 게시물 번호도 내려줘야 함
    //   router.push({ path: `/admin/board/${boardNo}#reply-${reportTargetNo}` })
    //   break
    case 'PRODUCT':
      router.push({ name: 'adminProductDetail', params: { productNo: reportTargetNo } })
      break
    case 'USER':
      router.push({ name: 'adminUserDetail', params: { userNo: reportTargetNo } })
      break
    default:
      alert('지원되지 않는 대상 유형입니다.')
  }
}


const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '-'
  return dateTimeStr.replace('T', ' ').split('.')[0]
}

const statusLabel = (status) => {
  switch (status) {
    case 0: return '대기'
    case 1: return '승인'
    case 2: return '반려'
    default: return 'Unknown'
  }
}

onMounted(() => {
  getReportDetail()
})
</script>