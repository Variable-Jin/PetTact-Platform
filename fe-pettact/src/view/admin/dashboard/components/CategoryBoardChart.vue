<template>
  <div class="card mt-4">
    <div class="card-body">
      <h5 class="card-title">카테고리별 게시물 비율</h5>
      <Doughnut :data="chartData" :options="chartOptions" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { Doughnut } from 'vue-chartjs';
import {
  Chart as ChartJS,
  Title,
  Tooltip,
  Legend,
  ArcElement,
} from 'chart.js';

ChartJS.register(Title, Tooltip, Legend, ArcElement);

const chartData = ref({
  labels: [],
  datasets: []
});

const chartOptions = ref({
  responsive: true,
  plugins: {
    legend: { position: 'right' },
    title: {
      display: false,
      text: '카테고리별 게시물 비율'
    }
  }
});

onMounted(async () => {
  try {
    const res = await axios.get('/v1/admin/dashboard/board/category-ratio');
    const data = res.data;

    chartData.value = {
      labels: data.map(d => d.categoryName),
      datasets: [{
        label: '게시물 수',
        data: data.map(d => d.boardCount),
        backgroundColor: [
          'rgba(255, 99, 132, 0.6)',
          'rgba(54, 162, 235, 0.6)',
          'rgba(255, 206, 86, 0.6)',
          'rgba(75, 192, 192, 0.6)',
          'rgba(153, 102, 255, 0.6)',
          'rgba(255, 159, 64, 0.6)'
        ],
        borderWidth: 1
      }]
    };
  } catch (e) {
    console.error('[카테고리별 게시물 통계 API 오류]', e);
  }
});
</script>
