<template>
  <div class="card mt-4">
    <div class="card-body">
      <h5 class="card-title">최근 7일간 가입자 추이</h5>
      <Line :data="chartData" :options="chartOptions" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { Line } from 'vue-chartjs';
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  LineElement,
  PointElement,
  CategoryScale,
  LinearScale
} from 'chart.js';

ChartJS.register(Title, Tooltip, Legend, LineElement, PointElement, CategoryScale, LinearScale);

const chartData = ref({
  labels: [],
  datasets: []
});

const chartOptions = ref({
  responsive: true,
  plugins: {
    legend: { position: 'top' },
    title: { display: false }
  }
});

onMounted(async () => {
  const res = await axios.get('/v1/admin/dashboard/user/daily?days=7');
  const data = res.data;

  chartData.value = {
    labels: data.map(d => d.date),
    datasets: [{
      label: '신규 가입자 수',
      data: data.map(d => d.newUsers),
      borderWidth: 2,
      borderColor: 'rgba(75, 192, 192, 1)',
      backgroundColor: 'rgba(75, 192, 192, 0.2)',
      fill: false,
      tension: 0.1
    }]
  };
});
</script>
