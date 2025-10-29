<template>
  <div class="community-page">
    <section class="admin-category-section">
      <div class="admin-container">
        <div class="header-area">
          <h2 class="page-title">커뮤니티 게시판 관리</h2>
          <button v-if="isAdmin" @click="goToCreateCategory" class="create-btn">
            + 새 게시판 만들기
          </button>
        </div>
        
        <!-- 로딩 상태 -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>게시판 목록을 불러오는 중...</p>
        </div>

        <!-- 에러 상태 -->
        <div v-else-if="error" class="error-state">
          <p>{{ error }}</p>
          <button @click="fetchBoardCategories" class="retry-btn">다시 시도</button>
        </div>

        <!-- 게시판 목록 -->
        <div v-else class="category-grid">
          <div 
            v-for="category in boardCategories" 
            :key="category.boardCategoryNo"
            class="category-card"
          >
            <!-- 게시판 정보 -->
            <div class="category-info" @click="goToBoard(category.boardCategoryNo)">
              <h3 class="category-title">{{ category.boardCategoryTitle }}</h3>
              <p v-if="category.boardCategoryDescription" class="category-desc">
                {{ category.boardCategoryDescription }}
              </p>
            </div>

            <!-- 관리 버튼들 -->
            <div v-if="isAdmin" class="category-actions">
              <button 
                @click.stop="viewCategoryDetail(category)" 
                class="action-btn detail"
                title="상세정보"
              >
                상세
              </button>
              <button 
                @click.stop="editCategory(category)" 
                class="action-btn edit"
                title="수정하기"
              >
                수정
              </button>
              <button 
                @click.stop="deleteCategory(category)" 
                class="action-btn delete"
                title="삭제하기"
              >
                삭제
              </button>
            </div>
          </div>

          <!-- 게시판이 없을 때 -->
          <div v-if="boardCategories.length === 0" class="empty-state">
            <p>등록된 게시판이 없습니다.</p>
            <button v-if="isAdmin" @click="goToCreateCategory" class="create-btn-empty">
              첫 게시판 만들기
            </button>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>


<script>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import axios from 'axios'

export default {
  setup() {
    const router = useRouter()
    const userStore = useUserStore()

    // Reactive data
    const boardCategories = ref([])
    const loading = ref(false)
    const error = ref(null)

    // Computed
    const isAdmin = computed(() => userStore.user?.userRole === 'ROLE_ADMIN')

    // Methods
    const fetchBoardCategories = async () => {
      loading.value = true
      error.value = null
      
      try {
        console.log('🔍 게시판 카테고리 목록 조회 시작...')
        const response = await axios.get('/v1/board-categories')
        boardCategories.value = response.data || []
        console.log('✅ 게시판 목록 로드 완료:', boardCategories.value)
      } catch (err) {
        console.error('❌ 게시판 목록 조회 실패:', err)
        error.value = '게시판 목록을 불러오는데 실패했습니다.'
        boardCategories.value = []
      } finally {
        loading.value = false
      }
    }

    const goToBoard = (categoryNo) => {
      console.log('게시판 이동:', categoryNo)
      router.push(`/board/${categoryNo}`)
    }

    const goToCreateCategory = () => {
      router.push('/boardCategoryForm')
    }

    const viewCategoryDetail = (category) => {
      console.log('상세정보:', category)
      router.push(`/boardCategoryDetail/${category.boardCategoryNo}`)
    }

    const editCategory = (category) => {
      console.log('수정:', category)
      router.push(`/boardCategory/${category.boardCategoryNo}/edit`)
    }

    const deleteCategory = async (category) => {
      if (confirm(`'${category.boardCategoryTitle}' 게시판을 정말 삭제하시겠습니까?`)) {
        try {
          await axios.delete(`/v1/board-categories/${category.boardCategoryNo}`)
          alert('게시판이 삭제되었습니다.')
          fetchBoardCategories() // 목록 새로고침
        } catch (error) {
          console.error('삭제 실패:', error)
          alert('게시판 삭제에 실패했습니다.')
        }
      }
    }

    // Lifecycle
    onMounted(() => {
      console.log('🚀 BoardCategoryList 컴포넌트 마운트')
      console.log('👤 현재 사용자 권한 - isAdmin:', isAdmin.value)
      fetchBoardCategories()
    })

    // Return
    return {
      boardCategories,
      loading,
      error,
      isAdmin,
      fetchBoardCategories,
      goToCreateCategory,
      goToBoard,
      viewCategoryDetail,
      editCategory,
      deleteCategory
    }
  }
}
</script>



<style scoped>
.community-page {
  min-height: 100vh;
  background: #f5f6f8;
  padding: 40px 0;
}

.admin-category-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.admin-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  padding: 40px;
}

/* 헤더 영역 */
.header-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}

.create-btn {
  padding: 12px 24px;
  background: #4a90e2;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.create-btn:hover {
  background: #357abd;
  transform: translateY(-1px);
}

/* 로딩/에러 상태 */
.loading-state,
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f0f0f0;
  border-top: 3px solid #4a90e2;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.retry-btn {
  padding: 10px 20px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.retry-btn:hover {
  background: #5a6268;
}

/* 카테고리 그리드 */
.category-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.category-card {
  background: #fafafa;
  border: 1px solid #e5e5e5;
  border-radius: 8px;
  padding: 24px;
  transition: all 0.2s;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.category-card:hover {
  border-color: #4a90e2;
  box-shadow: 0 4px 12px rgba(74, 144, 226, 0.1);
}

/* 카테고리 정보 */
.category-info {
  cursor: pointer;
  flex: 1;
}

.category-title {
  font-size: 18px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
}

.category-desc {
  font-size: 13px;
  color: #999;
  margin: 0;
  line-height: 1.5;
}

/* 액션 버튼들 */
.category-actions {
  display: flex;
  gap: 8px;
  padding-top: 16px;
  border-top: 1px solid #e5e5e5;
}

.action-btn {
  flex: 1;
  padding: 8px 12px;
  font-size: 13px;
  font-weight: 500;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  background: white;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn.detail {
  color: #4a90e2;
}

.action-btn.detail:hover {
  background: #f0f7ff;
  border-color: #4a90e2;
}

.action-btn.edit {
  color: #10b981;
}

.action-btn.edit:hover {
  background: #f0fdf4;
  border-color: #10b981;
}

.action-btn.delete {
  color: #ef4444;
}

.action-btn.delete:hover {
  background: #fef2f2;
  border-color: #ef4444;
}

/* 빈 상태 */
.empty-state {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 20px;
}

.empty-state p {
  color: #999;
  font-size: 14px;
}

.create-btn-empty {
  padding: 12px 24px;
  background: #4a90e2;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
}

.create-btn-empty:hover {
  background: #357abd;
}

/* 반응형 */
@media (max-width: 768px) {
  .admin-container {
    padding: 24px 20px;
  }

  .header-area {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .category-grid {
    grid-template-columns: 1fr;
  }
}
</style>