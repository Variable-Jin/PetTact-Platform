<template>
  <div class="card mt-4">
    <div class="card-body">
      <h5 class="card-title">최근 7일간 신고 접수 수</h5>
      <Bar :data="chartData" :options="chartOptions" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { Bar } from 'vue-chartjs';
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  BarElement,
  CategoryScale,
  LinearScale
} from 'chart.js';

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale);

const chartData = ref({
  labels: [],
  datasets: []
});

const chartOptions = ref({
  responsive: true,
  plugins: {
    legend: { display: false },
    title: { display: false }
  }
});

onMounted(async () => {
  const res = await axios.get('/v1/admin/dashboard/report/daily?days=7');
  const data = res.data;

  chartData.value = {
    labels: data.map(d => d.date),
    datasets: [{
      label: '신고 건수',
      data: data.map(d => d.reportCount),
      backgroundColor: 'rgba(255, 99, 132, 0.6)',
      borderColor: 'rgba(255, 99, 132, 1)',
      borderWidth: 1
    }]
  };
});
</script>
