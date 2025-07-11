// src/stores/order.js
import { defineStore } from 'pinia'
import { ref } from 'vue'
import api from '@/api'

export const useOrderStore = defineStore('order', {
  state: () => ({
    orderList: [],
    orderDetail: null,
    orderDraft: [],
    loading: false,
    error: null,
    page: 0,      // 현재 페이지 번호
    pageSize: 5,  // 페이지당 항목 수
    totalPages: 0, // 총 페이지 수
  }),
  actions: {

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
        const res = await api.get('/v1/order',{
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
        const res = await api.get(`/v1/order/detail/${orderNo}`)
        this.orderDetail = res.data
      } catch (err) {
        this.error = err
      } finally {
        this.loading = false
      }
    },

    async createOrder(orderDetails) {
      this.loading = true
      try {
        const res = await api.post('/v1/order', orderDetails)
        return res.data
      } catch (err) {
        this.error = err
        throw err
      } finally {
        this.loading = false
      }
    },

    async cancelOrder(orderNo) {
      this.loading = true
      try {
        await api.patch(`/v1/order/cancel/${orderNo}`)
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
