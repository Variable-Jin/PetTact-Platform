<template>
  <div>
    <h2>유기동물 검색</h2>

    <div class="filters">
      <!-- 축종 선택 -->
      <select v-model="selectedUpKindCd" @change="fetchKinds">
        <option value="">축종 선택</option>
        <option value="417000">개</option>
        <option value="422400">고양이</option>
        <option value="429900">기타</option>
      </select>

      <!-- 품종 -->
      <select v-model="selectedKindCd" required>
        <option value="">품종 선택</option>
        <option v-for="k in kindList" :key="k.kindCd" :value="k.kindCd">{{ k.kindNm }}</option>
      </select>

      <!-- 시도 -->
      <select v-model="selectedSido" @change="handleSidoChange">
        <option value="">시도 선택</option>
        <option v-for="s in sidoList" :key="s.orgCd" :value="s">{{ s.orgdownNm }}</option>
      </select>

      <!-- 시군구 -->
      <select v-model="selectedSigungu" @change="handleSigunguChange">
        <option value="">시군구 선택</option>
        <option v-for="g in sigunguList" :key="g.orgCd" :value="g">{{ g.orgdownNm }}</option>
      </select>

      <button @click="goPage(1)">조회</button>
    </div>

    <!-- 결과 -->
    <div v-if="abandonments.length > 0" class="list">
      <h3>조회 결과 (총 {{ totalElements.toLocaleString() }}건)</h3>
      <ul>
        <li v-for="a in abandonments" :key="a.desertionNo" class="animal-card">
          <img :src="a.popfile1" alt="사진" width="150" v-if="a.popfile1" />
          <div class="info">
            <p><strong>{{ a.kindCd }}</strong> - {{ a.sexCd }} / {{ a.age }}</p>
            <p>발견 장소: {{ a.happenPlace }}</p>
            <p>공고 기간: {{ a.noticeSdt }} ~ {{ a.noticeEdt }}</p>
            <button @click="DetailView(a.desertionNo)">상세보기</button>
          </div>
        </li>
      </ul>
      <Pagination :current-page="page" :total-pages="totalPages" @change="goPage" />
    </div>

    <p v-else-if="searched">조건에 맞는 유기동물이 없습니다.</p>
  </div>
</template>

<script>
// import axios from '@/js/axios'
// import Pagination from '@/components/Pagination.vue'

export default {
  // components: { Pagination },
  data() {
    return {
      selectedUpKindCd: '',
      selectedKindCd: '',
      selectedSido: '',
      selectedSigungu: '',
      selectedShelterCd: '',

      kindList: [],
      sidoList: [],
      sigunguList: [],
      shelterList: [],

      abandonments: [],
      page: 1,
      totalPages: 1,
      searched: false
    }
  },
  created() {
    this.fetchSido()
  },
  methods: {
    DetailView(desertionNo) {
      this.$router.push(`/abandonment/${desertionNo}`)
    },
    fetchSido() {
      axios.get('/pet/sido')
        .then(res => {
          this.sidoList = res.data.items
        })
    },
    fetchKinds() {
      this.selectedKindCd = ''
      this.kindList = []
      if (!this.selectedUpKindCd) return

      axios.get('/pet/kind', {
        params: { upKindCd: this.selectedUpKindCd }
      }).then(res => {
        this.kindList = res.data.items
      })
    },
    handleSidoChange() {
      this.selectedSigungu = ''
      this.sigunguList = []
      this.selectedShelterCd = ''
      this.shelterList = []

      if (this.selectedSido) this.fetchSigungu()
    },
    fetchSigungu() {
      axios.get('/pet/sigungu', {
        params: { uprCd: this.selectedSido.orgCd }
      }).then(res => {
        this.sigunguList = res.data.items
      })
    },
    handleSigunguChange() {
      this.selectedShelterCd = ''
      this.shelterList = []
      if (this.selectedSido && this.selectedSigungu) this.fetchShelters()
    },
    goPage(page) {
      if (!this.selectedKindCd) {
        alert('품종을 선택해주세요.')
        return
      }

      const params = {
        page: page,
        size: 10,
        upKindCd: this.selectedUpKindCd,
        kindCd: this.selectedKindCd
      }

      if (this.selectedSido?.orgdownNm) {
        params.orgNm = this.selectedSido.orgdownNm
        if (this.selectedSigungu?.orgdownNm) {
          params.orgNm += ' ' + this.selectedSigungu.orgdownNm
        }
      }

      if (this.selectedShelterCd) {
        params.careRegNo = this.selectedShelterCd
      }

      axios.get('/pet/abandonment', { params })
      .then(res => {
        this.abandonments = res.data.content
        this.totalPages = res.data.totalPages
        this.totalElements = res.data.totalElements
        this.page = page
        this.searched = true
      })
        .catch(err => {
          console.error('유기동물 조회 실패:', err)
        })
    }
  }
}
</script>

<style scoped>
.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}
.animal-card {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 10px;
  border: 1px solid #ccc;
  margin-bottom: 12px;
  border-radius: 8px;
}
.animal-card img {
  border-radius: 4px;
}
.animal-card .info {
  flex: 1;
}
</style>
