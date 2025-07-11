<template>
  <div class="row">
    <div class="col-6 mb-3" v-for="card in cards" :key="card.label">
      <div class="card text-center h-100">
        <div class="card-body">
          <h5 class="card-title">{{ card.label }}</h5>
          <p class="card-text fs-4">{{ card.value }}</p>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
// import { connectNotificationWS } from '@/utils/websocket';
import { useUserStore } from '@/stores/user';

const userStore = useUserStore();

const cards = ref([
  { label: '전체 회원', value: 0 },
  { label: '오늘 가입', value: 0 },
  { label: '가입자 증가율 (%)', value: 0 },
  // { label: '실시간 접속자', value: 0 },
  { label: '판매자 승인 대기', value: 0 }
]);

onMounted(async () => {
  try {
    const res = await axios.get('/v1/admin/dashboard/summary');
    const data = res.data;

    cards.value.forEach(card => {
      if (card.label === '전체 회원') card.value = data.totalUsers;
      if (card.label === '오늘 가입') card.value = data.dailyNewUsers;
      if (card.label === '가입자 증가율 (%)') card.value = `${data.userGrowthRate.toFixed(2)}%`;
    });
  } catch (e) {
    console.error('[대시보드 요약 API 오류]', e);
  }

  try {
    const res = await axios.get('/v1/admin/dashboard/seller/pending-count');
    cards.value.find(c => c.label === '판매자 승인 대기').value = res.data;
  } catch (e) {
    console.error('[판매자 승인 대기자 수 API 오류]', e);
  }
  // connectNotificationWS(
  //     userStore.accessToken,
  //     userStore.user.userNo,
  //     (notification) => {
  //         console.log('알림 수신', notification);
  //     },
  //     (activeUsers) => {
  //         console.log('실시간 접속자 수 수신', activeUsers);
  //         cards.value = cards.value.map(card => {
  //             if (card.label === '실시간 접속자') {
  //                 return { ...card, value: activeUsers };
  //             }
  //             return card;
  //         });
  //     }
  // );

});

</script>
