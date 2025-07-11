<template>
  <div class="cart-view container py-4 text-white">
    <h2 class="mb-4">장바구니</h2>

    <div class="mb-3 d-flex gap-2">
      <button @click="goToList" class="btn btn-secondary">상품 목록</button>
      <button @click="goToOrderList" class="btn btn-secondary">주문 내역</button>
    </div>

    <div v-if="cartStore.loading" class="mb-3">불러오는 중...</div>
    <div v-else-if="cartStore.cartItems.length === 0" class="alert alert-info">
      장바구니가 비어 있습니다.
    </div>

    <table v-else class="table table-bordered align-middle text-white">
      <thead class="table-secondary text-dark text-center">
        <tr>
          <th style="width: 40px;"></th> <!-- 체크박스 -->
          <th>상품 정보</th>
          <th style="width: 100px;">수량</th>
          <th style="width: 120px;">주문 금액</th>
          <th style="width: 80px;"></th> <!-- 삭제 -->
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in cartStore.cartItems" :key="item.cartNo">
          <!-- 체크박스 -->
          <td class="text-center">
            <input
              type="checkbox"
              :value="item.cartNo"
              v-model="selectedItems"
              :disabled="!item.productName"
              class="form-check-input"
            />
          </td>

          <!-- 상품 정보 -->
          <td>
            <div class="d-flex gap-3 align-items-center">
              <!-- 이미지 -->
              <img
                v-if="item.imageUrl"
                :src="getImageUrl(item.imageUrl)"
                alt="상품 이미지"
                class="img-thumbnail"
                style="width: 80px; height: 80px; object-fit: cover;"
              />

              <!-- 텍스트 정보 -->
              <div>
                <div v-if="item.productName">
                  <router-link :to="`/product/${item.productNo}`">
                    {{ item.productName }}
                  </router-link>
                </div>
                <div v-else class="text-warning small">⚠️ 판매 중지 상품</div>
              </div>
            </div>
          </td>

          <!-- 수량 -->
          <td class="text-center">
            <div v-if="item.productName" class="input-group input-group-sm justify-content-center">
                <button
                  class="btn btn-light text-dark"
                  @click="changeQuantity(item, -1)"
                  :disabled="item.productStock <= 1"
                >−</button>

                <input
                  class="form-control text-center"
                  :value="item.productStock"
                  readonly
                  style="max-width: 50px;"
                />

                <button
                  class="btn btn-light text-dark"
                  @click="changeQuantity(item, 1)"
                >+</button>
              </div>
          </td>

          <!-- 주문 금액 -->
          <td class="text-end">
            <span v-if="item.productName">
              {{ getFormattedPrice(item.productPrice * item.productStock) }}원
            </span>
          </td>

          <!-- 삭제 -->
          <td class="text-center">
            <button @click="removeItem(item.cartNo)" class="btn btn-sm btn-outline-secondary">
              삭제
            </button>
          </td>
        </tr>
      </tbody>
    </table>

    <div class="mt-3">
      <button
        @click="orderSelectedItems"
        :disabled="selectedItems.length === 0"
        class="btn btn-secondary"
        type="button"
      >
        구매하기 ({{ selectedItems.length }}개 선택됨)
      </button>
    </div>
  </div>
  <div class="mt-4 text-center">
  <button
    class="btn btn-sm btn-outline-light me-2"
    :disabled="cartStore.currentPage === 0"
    @click="changePage(cartStore.currentPage - 1)"
  >
    ◀ 이전
  </button>

  <span class="fw-bold text-white">
    {{ cartStore.currentPage + 1 }} / {{ cartStore.totalPages }}
  </span>

  <button
    class="btn btn-sm btn-outline-light ms-2"
    :disabled="cartStore.currentPage + 1 >= cartStore.totalPages"
    @click="changePage(cartStore.currentPage + 1)"
  >
    다음 ▶
  </button>
</div>
</template>



<script setup>
import { ref, onMounted } from 'vue';
import { useCartStore } from '@/stores/cart';
import { useOrderStore } from '@/stores/order'  // order 스토어 임포트
import { useRouter } from 'vue-router'

const cartStore = useCartStore();
const orderStore = useOrderStore()  // order 스토어 인스턴스 생성
const router = useRouter()

// 선택된 cartNo들을 저장하는 배열
const selectedItems = ref([]);

const getFormattedPrice = (price) => {
  return new Intl.NumberFormat('ko-KR').format(price);
};

const goToOrderList = () => {
  router.push('/order')
}

const changePage = (pageNum) => {
  if (pageNum >= 0 && pageNum < cartStore.totalPages) {
    cartStore.fetchCart(pageNum);
    selectedItems.value = []; // 페이지 이동 시 선택 초기화
  }
};

const changeQuantity = (item, delta) => { // 수량 변경
  const newQty = item.productStock + delta;
  if (newQty < 1) return;

  item.productStock = newQty;
  cartStore.updateCartItem(item.productNo, item.productStock);
};

function getImageUrl(imageUrl) {
  if (!imageUrl) return '/default-product.png';
  const baseUrl = 'http://localhost:8080'; // 실제 백엔드 이미지 URL 경로
  return baseUrl + imageUrl;
}

const goToList = () => {
  router.push('/product') 
}

const removeItem = async (cartNo) => {
  if (confirm('삭제하시겠습니까?')) {
    try {
      // 선택 목록에서도 제거
      const index = selectedItems.value.indexOf(cartNo);
      if (index !== -1) selectedItems.value.splice(index, 1);

      // 삭제 요청 & 상태 반영
      await cartStore.deleteCartItem(cartNo);
    } catch (err) {
      alert('삭제 중 오류가 발생했습니다.');
      console.error(err);
    }
  }
};

onMounted(() => {
  cartStore.fetchCart().then(() => {
    console.log(cartStore.cartItems);
  });
});

const orderSelectedItems = async () => {
  if (selectedItems.value.length === 0) {
    alert('주문할 상품을 선택해주세요.')
    return
  }

  // 1️⃣ 주문 전에 cart 재조회해서 최신 수량 반영
  await cartStore.fetchCart()

  const itemsToOrder = cartStore.cartItems.filter(item =>
    selectedItems.value.includes(item.cartNo)
  )

  const orderDetails = itemsToOrder.map(item => ({
    productNo: item.productNo,
    productName: item.productName,      // ✅ 추가
    productPrice: item.productPrice,    // ✅ 추가
    productStock: item.productStock
  }))

  try {
    //const result = await orderStore.createOrder(orderDetails)
    alert('주문서가 성공적으로 생성되었습니다.')
    selectedItems.value = []
    await cartStore.fetchCart()
    orderStore.setOrderDraft(itemsToOrder);  
    router.push('/order-sheet')
  } catch (error) {
    console.error('주문 생성 실패:', error)
    alert('주문서 생성에 실패했습니다.')
  }
}
</script>

<style scoped>
.product-image {
  width: 200px;
  height: 200px;
  object-fit: cover;
  border-radius: 0px;
  border: 1px solid #ccc;
}

.custom-checkbox {
  width: 20px;
  height: 20px;
  background-color: #ccc; /* 연한 회색 배경 */
  border: 1.5px solid #888; /* 진한 회색 테두리 */
  appearance: none;
  -webkit-appearance: none;
  outline: none;
  cursor: pointer;
  border-radius: 3px;
  position: relative;
}

.input-group-sm .form-control {
  padding: 0.25rem 0.5rem;
  font-size: 0.875rem;
}
</style>