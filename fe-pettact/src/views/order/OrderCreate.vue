<template>
  <div>
    <h2>주문</h2>
    <button @click="create">주문하기</button>
  </div>
</template>

<script setup>
import { useOrderStore } from '@/stores/order'

// ✅ store 인스턴스를 가져옵니다
const orderStore = useOrderStore()

const create = async () => {
  const orderDetailList = [
    {
      productNo: productNo,
      productStock: productStock,
      // productPrice: 10000  ← 백엔드에서 가격 계산하면 생략 가능
    }
  ]

  try {
    // ✅ store의 createOrder 액션을 호출
    const res = await orderStore.createOrder(orderDetailList)
    alert('주문 완료: ' + res.orderNo)
  } catch (err) {
    alert('주문 실패: ' + (err?.response?.data?.message || err.message || '알 수 없는 오류'))
  }
}
</script>

<style>
</style>
