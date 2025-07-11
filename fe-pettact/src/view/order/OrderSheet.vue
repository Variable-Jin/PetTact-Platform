<template>
  <div class="container py-4 text-white">
    <h2 class="mb-4">주문서</h2>

    <div v-if="!orderStore.orderDraft.length" class="alert alert-warning">
      주문할 상품이 없습니다. 다시 장바구니로 이동해주세요.
    </div>

    <div v-else>
      <!-- 상품 목록 테이블 -->
      <table class="table table-bordered text-white">
        <thead>
          <tr>
            <th>상품명</th>
            <th>수량</th>
            <th>가격</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in orderStore.orderDraft" :key="item.cartNo">
            <td>{{ item.productName }}</td>
            <td>{{ item.productStock }}</td>
            <td>{{ getFormattedPrice(item.productPrice * item.productStock) }}원</td>
          </tr>
        </tbody>
      </table>

      <!-- 배송 정보 -->
      <h5 class="mt-4">배송 정보 <span class="text-danger">*</span> 필수입력</h5>

      <!-- 기존 배송지 / 신규입력 선택 -->
      <div class="form-check form-check-inline mt-2">
        <input class="form-check-input" type="radio" id="existing" value="existing" v-model="addressType" />
        <label class="form-check-label" for="existing">기존 배송지</label>
      </div>
      <div class="form-check form-check-inline">
        <input class="form-check-input" type="radio" id="new" value="new" v-model="addressType" />
        <label class="form-check-label" for="new">신규입력</label>
      </div>

      <!-- 배송지명 -->
      <div class="mb-3 mt-3">
        <label class="form-label">배송지명</label>
        <input v-model="deliveryName" class="form-control" placeholder="예: 우리집, 회사 등" />
      </div>

      <!-- 수령인 -->
      <div class="mb-3">
        <label class="form-label">수령인 <span class="text-danger">*</span></label>
        <input v-model="receiver" class="form-control" required />
      </div>

      <!-- 배송지 주소 -->
      <div class="mb-3">
        <label class="form-label">배송지 <span class="text-danger">*</span></label>
        <div class="input-group mb-2">
          <input v-model="zipcode" class="form-control" placeholder="우편번호" readonly />
          <button class="btn btn-secondary" type="button" @click="searchZipcode">우편번호 검색</button>
        </div>
        <input v-model="address1" class="form-control mb-2" placeholder="기본 주소" />
        <input v-model="address2" class="form-control" placeholder="상세 주소" />
      </div>

      <!-- 연락처1 -->
      <div class="mb-3">
        <label class="form-label">연락처1 <span class="text-danger">*</span></label>
        <input v-model="phone" class="form-control" placeholder="예: 010-1234-5678" />
      </div>

      <!-- 결제 버튼 -->
      <button @click="openModal" class="btn btn-secondary">결제하기</button>

      <!-- 이동 버튼 -->
      <div class="d-flex gap-2 mt-3">
        <button @click="goToCart" class="btn btn-outline-light">장바구니로 돌아가기</button>
        <button @click="goToProductList" class="btn btn-outline-light">상품 목록으로 이동</button>
      </div>
    </div>
      <!-- ✅ 개선된 모달 영역 -->
      <div v-if="isModalOpen" class="modal-backdrop fade show"></div>
      <div v-if="isModalOpen" class="modal fade show d-block" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content bg-light text-dark shadow-lg border-0 rounded">
            <div class="modal-header bg-primary text-white">
              <h5 class="modal-title">가상 결제( API 연동X )</h5>
              <button type="button" class="btn-close btn-close-white" @click="closeModal"></button>
            </div>
            <div class="modal-body">
              <!-- 결제 요약 -->
              <div class="mb-3">
                <strong>총 결제금액:</strong>
                <span class="text-danger fs-5 fw-bold">{{ getFormattedPrice(totalPrice) }}원</span>
              </div>

              <!-- 카드 정보 입력 -->
              <div class="mb-3">
                <label class="form-label">카드번호</label>
                <input type="text" class="form-control" placeholder="1234-5678-9012-3456" v-model="cardNumber" />
              </div>

              <div class="row">
                <div class="col-md-6 mb-3">
                  <label class="form-label">유효기간</label>
                  <input type="text" class="form-control" placeholder="MM/YY" v-model="expiry" />
                </div>
                <div class="col-md-6 mb-3">
                  <label class="form-label">CVC</label>
                  <input type="password" class="form-control" placeholder="***" v-model="cvc" />
                </div>
              </div>

              <div class="mb-3">
                <label class="form-label">카드사 선택</label>
                <select class="form-select" v-model="cardCompany">
                  <option disabled value="">선택하세요</option>
                  <option>신한카드</option>
                  <option>국민카드</option>
                  <option>현대카드</option>
                  <option>삼성카드</option>
                </select>
              </div>
            </div>

            <div class="modal-footer">
              <button class="btn btn-outline-secondary" @click="closeModal">취소</button>
              <button class="btn btn-primary" @click="submitOrder">결제 완료</button>
            </div>
          </div>
        </div>
      </div>
  </div>
  
</template>

<script setup>
import { useOrderStore } from '@/stores/order';
import { useRouter } from 'vue-router';
import { ref,computed } from 'vue';

const orderStore = useOrderStore();
const router = useRouter();

//===================샘플데이터================
const addressType = ref('new'); // 기본값: 신규입력
const deliveryName = ref('김철수');
const receiver = ref('집');
const zipcode = ref('');
const address1 = ref('');
const address2 = ref('');
const phone = ref('010-1111-1111');

const isModalOpen = ref(false);
const cardNumber = ref('1234-5678-9012');
const expiry = ref('12/34');
const cvc = ref('123');
const cardCompany = ref('국민카드');
const totalPrice = computed(() =>
  orderStore.orderDraft.reduce((sum, item) => sum + item.productPrice * item.productStock, 0)
);

const openModal = () => {
  if (!receiver.value || !address1.value || !address2.value || !phone.value) {
    alert('필수 배송 정보를 모두 입력해주세요.');
    return;
  }
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
};

const searchZipcode = () => {
  alert('우편번호 검색 모듈과 연동하세요.');
  zipcode.value = '04000';
  address1.value = '서울시 마포구 예제로 123';
  address2.value = '1층 101호';
  const phone = ref('010-1111-1111');
};

const getFormattedPrice = (price) => new Intl.NumberFormat().format(price);

const goToCart = () => router.push('cart');
const goToProductList = () => router.push('/product');

const submitOrder = async () => {
  
    if (!cardNumber.value || !expiry.value || !cvc.value || !cardCompany.value) {
    alert('카드 정보를 모두 입력해주세요.');
    return;
  }

  try {
    const orderDetails = orderStore.orderDraft.map(item => ({
      productNo: item.productNo,
      productStock: item.productStock
    }));
      await orderStore.createOrder({
      deliveryName: deliveryName.value,
      receiver: receiver.value,
      zipcode: zipcode.value,
      address1: address1.value,
      address2: address2.value,
      phone: phone.value,
      orderDetails: orderDetails
    });
    //await orderStore.createOrder(orderDetails);
    alert('주문이 완료되었습니다!');
    orderStore.clearOrderDraft();
    isModalOpen.value = false;
    router.push('/order');
  } catch (err) {
    alert('주문 중 오류 발생');
    console.error(err);
  }
};
</script>
