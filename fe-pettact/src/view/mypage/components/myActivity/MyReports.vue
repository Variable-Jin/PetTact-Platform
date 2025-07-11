<template>
  <div>
    <h5 class="mt-3">신고 내역</h5>

    <div v-if="reports.length > 0" class="list-group mt-2">
      <a
        v-for="report in reports"
        :key="report.reportNo"
        class="list-group-item list-group-item-action"
        @click="goToDetail(report.reportNo)"
      >
        <div class="d-flex w-100 justify-content-between">
          <h6 class="mb-1">{{ report.reportReason }}</h6>
          <small>{{ formatDate(report.createdAt) }}</small>
        </div>
        <p class="mb-1">
          대상: {{ report.reportTargetLocation }} / 상태:
          <span :class="statusBadgeClass(report.reportStatus)">
            {{ statusText(report.reportStatus) }}
          </span>
        </p>
      </a>
    </div>

    <div v-else class="mt-2">
      <p>신고 내역이 없습니다.</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const reports = ref([]);
const router = useRouter();

const fetchReports = async () => {
  try {
    const res = await axios.get("/v1/user/mypage/my-reports");
    reports.value = res.data;
  } catch (e) {
    console.error(e);
  }
};

const goToDetail = (reportNo) => {
  router.push({ name: 'myReportDetail', params: { reportNo } });
};

const formatDate = (dateStr) => {
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

const statusBadgeClass = (status) => {
  return status === 1 ? "badge bg-success" : "badge bg-secondary";
};

onMounted(fetchReports);
</script>
