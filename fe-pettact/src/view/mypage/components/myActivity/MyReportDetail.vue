<template>
  <div class="container mt-4">
    <h4>신고 상세 내역</h4>

    <div v-if="report" class="mt-3">
      <p><strong>신고 사유:</strong> {{ report.reportReason }}</p>
      <p><strong>신고 내용:</strong> {{ report.reportDescription }}</p>
      <p><strong>대상 위치:</strong> {{ report.reportTargetLocation }}</p>
      <p><strong>신고 상태:</strong> {{ statusText(report.reportStatus) }}</p>
      <p><strong>신고 접수일:</strong> {{ formatDate(report.createdAt) }}</p>
      <p><strong>처리 완료일:</strong> {{ formatDate(report.updatedAt) }}</p>
    </div>

    <div v-else>
      <p>로딩 중...</p>
    </div>

    <button class="btn btn-secondary mt-3" @click="goBack">뒤로가기</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useRoute, useRouter } from "vue-router";

const route = useRoute();
const router = useRouter();
const report = ref(null);

const fetchReportDetail = async () => {
  try {
    const res = await axios.get(`/v1/user/mypage/my-report/${route.params.reportNo}`);
    report.value = res.data;
  } catch (e) {
    console.error(e);
  }
};

const formatDate = (dateStr) => {
  if (!dateStr) return "-";
  const date = new Date(dateStr);
  return date.toLocaleDateString() + " " + date.toLocaleTimeString();
};

const statusText = (status) => {
  switch (status) {
    case 0:
      return "처리중";
    case 1:
      return "승인";
    default:
      return "반려";
  }
};

const goBack = () => {
  router.back();
};

onMounted(fetchReportDetail);
</script>
