<template>
  <div>
    <h2>주문</h2>
    <button v-if="isLoggedIn" @click="create">주문하기</button>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useOrderStore } from '@/stores/order'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const orderStore = useOrderStore()
const userStore = useUserStore()

const isLoggedIn = computed(() => !!userStore.user)

const create = async () => {
  if (!isLoggedIn.value) {
    alert('로그인 후 주문 가능합니다.')
    return
  }

  const orderRequest = {
    deliveryName: '우리집',
    receiver: '홍길동',
    zipcode: '04000',
    address1: '서울시 마포구',
    address2: '101호',
    phone: '010-1234-5678',
    orderDetails: [
      {
        productNo: productNo,
        productStock: productStock
      }
    ]
  }

  try {
    const res = await orderStore.createOrder(orderRequest)
    alert('주문 완료: ' + res.orderNo)
    router.push(`/order/${res.orderNo}`)
  } catch (err) {
    alert('주문 실패: ' + (err?.response?.data?.message || err.message || '알 수 없는 오류'))
  }
}
</script>


<style>
</style>
