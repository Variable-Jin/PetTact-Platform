// src/stores/order.js
import { defineStore } from 'pinia'
import axios from 'axios'

export const useOrderStore = defineStore('order', {
  state: () => ({
    orderList: [],
    orderDetail: null,
    orderDraft: [],
    paymentResult: null,
    loading: false,
    error: null,
    page: 0,      // 현재 페이지 번호
    pageSize: 5,  // 페이지당 항목 수
    totalPages: 0, // 총 페이지 수
  }),
  actions: {

    // ✅ 결제 승인 요청
    async confirmPayment({ paymentKey, orderId, amount }) {
      this.loading = true
      try {
        const res = await axios.post('/v1/payments/confirm', {
          paymentKey,
          orderId,
          amount,
        })
        this.paymentResult = res.data
        return res.data
      } catch (err) {
        console.error('❌ 결제 실패 전체 응답:', err.response)
        this.error = err.response?.data?.message || '결제 실패'
        throw err
      } finally {
        this.loading = false
      }
    },

        // ✅ 주문 임시 데이터 설정
    setOrderDraft(items) {
      this.orderDraft = items
    },

    // ✅ 주문 임시 데이터 초기화
    clearOrderDraft() {
      this.orderDraft = []
    },
    
    async fetchOrders(page = 0) {
      this.loading = true
      try {
        this.page = page
        const res = await axios.get('/v1/order',{
          params:{
            page: this.page,
            size: this.pageSize,
          },
        })
        this.orderList = res.data.content
        this.totalPages = res.data.totalPages
      } catch (err) {
        this.error = err
      } finally {
        this.loading = false
      }
    },

    async fetchOrderDetail(orderNo) {
      this.loading = true
      try {
        const res = await axios.get(`/v1/order/detail/${orderNo}`)
        this.orderDetail = res.data
      } catch (err) {
        this.error = err
      } finally {
        this.loading = false
      }
    },

    async createOrder(orderRequest) {
      this.loading = true
      try {
        const res = await axios.post('/v1/order', orderRequest)  // orderRequest는 CreateRequest DTO 의미
        return res.data
      } catch (err) {
        this.error = err
        throw err
      } finally {
        this.loading = false
      }
    },
    // async createOrder(orderDetails) {
    //   this.loading = true
    //   try {
    //     const res = await api.post('/v1/order', orderDetails)
    //     return res.data
    //   } catch (err) {
    //     this.error = err
    //     throw err
    //   } finally {
    //     this.loading = false
    //   }
    // },

    async cancelOrder(orderNo) {
      this.loading = true
      try {
        await axios.patch(`/v1/order/cancel/${orderNo}`)
        await this.fetchOrders()
      } catch (err) {
        this.error = err
        throw err
      } finally {
        this.loading = false
      }
    },
  }
})
