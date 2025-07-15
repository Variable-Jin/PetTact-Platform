<template>
  <div class="board-list-table">

    <div class="list-actions">
      <button @click="goToCreate" class="write-btn">
        ✏️ 작성하기
      </button>
    </div>
    <!-- 헤더 -->
    <div class="list-header">
      <div class="col-title">제목</div>
      <div class="col-author">작성자</div>
      <div class="col-date">작성일</div>
      <div class="col-views">조회수</div>
    </div>
    
    <!-- 게시글 목록 -->
    <div class="list-body">
      <div 
        v-for="board in boards" 
        :key="board.boardNo"
        @click="goToDetail(board.boardNo)"
        class="list-row"
      >
        <div class="col-title">
          <span class="title-text">{{ board.boardTitle }}</span>
        </div>
        <div class="col-author">{{ board.userNickname }}</div>
        <div class="col-date">{{ formatDate(board.createdAt) }}</div>
        <div class="col-views">{{ board.viewCount || 0 }}</div>
      </div>
    </div>
  </div>

  

</template>

<script setup> 
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/stores/user'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 반응형 데이터
const boards = ref([])
const totalBoards = ref(0)
const todayBoards = ref(0)
const totalPages = ref(0)
const currentPage = ref(1)
const loading = ref(false)
const searchType = ref('all')
const searchKeyword = ref('')
const boardConfig = ref({
  title: '게시판',
  description: '',
  icon: '',
  searchPlaceholder: '검색어를 입력하세요',
  listType: 'list'
})

// 글쓰기 버튼
const goToCreate = () => {
  const categoryNo = route.params.categoryNo
  router.push(`/category/${categoryNo}/create`)
}

// 게시글 상세
const goToDetail = (boardNo) => {
  router.push(`/board/detail/${boardNo}`)
}

// 날짜 포맷
const formatDate = (dateString) => {
  if (!dateString) return ''
  
  const date = new Date(dateString)
  const now = new Date()
  const diffTime = now - date
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))
  
  if (diffDays === 0) {
    return date.toLocaleTimeString('ko-KR', { hour: '2-digit', minute: '2-digit' })
  } else if (diffDays < 7) {
    return `${diffDays}일 전`
  } else {
    return date.toLocaleDateString('ko-KR', { month: 'short', day: 'numeric' })
  }
}

// 뒤로가기
const goBack = () => {
  router.push('/board')
}

// 페이지 이동
const goToPage = (page) => {
  currentPage.value = page
  loadBoards()
}

// 검색
const searchPosts = () => {
  currentPage.value = 1
  loadBoards()
}

// 게시글 목록 로드
const loadBoards = async () => {
  try {
    loading.value = true
    const categoryNo = route.params.categoryNo
    
    console.log('🔍 categoryNo:', categoryNo)
    
    const response = await axios.get('/v1/board', {
      params: {
        categoryNo: categoryNo,
        page: currentPage.value,
        size: 10
      }
    })
    
    console.log('📦 API 응답:', response.data)
    
    // 데이터 설정
    boards.value = Array.isArray(response.data) ? response.data : []
    totalBoards.value = boards.value.length
    totalPages.value = 1
    
    // 오늘 게시글 수 계산
    const today = new Date().toISOString().split('T')[0]
    todayBoards.value = boards.value.filter(board => 
      board.createdAt && board.createdAt.startsWith(today)
    ).length
    
  } catch (error) {
    console.error('❌ 데이터 로드 실패:', error)
    boards.value = []
    totalBoards.value = 0
    totalPages.value = 0
  } finally {
    loading.value = false
  }
}

// 카테고리 정보 로드
const loadCategoryInfo = async () => {
  try {
    const categoryNo = route.params.categoryNo
    const response = await axios.get(`/v1/board-categories/${categoryNo}`)
    
    boardConfig.value = {
      title: response.data.boardCategoryTitle || '게시판',
      description: response.data.boardCategoryDescription || '',
      icon: '📋',
      searchPlaceholder: '검색어를 입력하세요',
      listType: 'list'
    }
  } catch (error) {
    console.error('카테고리 로드 실패:', error)
  }
}

// 초기 로드
onMounted(async () => {
  await loadCategoryInfo()
  await loadBoards()
})
</script>

<style scoped>
.board-list-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e1e5e9;
}

.list-header {
  display: grid;
  grid-template-columns: 2fr 120px 120px 80px;
  background: #f8f9fa;
  padding: 16px;
  font-weight: 600;
  color: #495057;
  border-bottom: 1px solid #e1e5e9;
}

.list-body {
  max-height: 600px;
  overflow-y: auto;
}

.list-row {
  display: grid;
  grid-template-columns: 2fr 120px 120px 80px;
  padding: 16px;
  border-bottom: 1px solid #f1f3f4;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.list-row:hover {
  background: #f8f9fa;
}

.list-row:last-child {
  border-bottom: none;
}

.col-title {
  text-align: left;
}

.col-author, .col-date, .col-views {
  text-align: center;
  color: #6c757d;
  font-size: 14px;
}

.title-text {
  font-weight: 500;
  color: #212529;
  line-height: 1.4;
}
</style>