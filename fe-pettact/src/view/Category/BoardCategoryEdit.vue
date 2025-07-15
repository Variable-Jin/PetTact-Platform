<template>
  <div>
    <!-- 서브 네비게이션 -->
    <section class="sub-nav-section">
      <div class="sub-nav-container">
        <h2 class="sub-nav-title">커뮤니티</h2>
        <div class="sub-nav-menu">
          <div v-for="category in boardCategories" 
               :key="category.boardCategoryNo"
               class="sub-nav-item">
            {{ category.boardCategoryTitle }}
          </div>
        </div>
      </div>
    </section>

    <!-- 카테고리 수정 폼 -->
    <div v-if="category" class="board-category-form">
      <div class="form-header">
        <h2>게시판 카테고리 수정</h2>
        <p>기존 게시판 카테고리 정보를 수정합니다.</p>
      </div>

      <div class="form-container">
        <!-- 기본 정보 -->
        <div class="form-section">
          <h3>기본 정보</h3>
          
          <div class="form-group">
            <label>카테고리 번호</label>
            <input v-model="category.boardCategoryNo" 
                   class="form-input" 
                   readonly 
                   disabled>
          </div>

          <div class="form-group">
            <label>카테고리 제목 <span class="required">*</span></label>
            <input v-model="category.boardCategoryTitle" 
                   class="form-input" 
                   placeholder="카테고리 제목을 입력하세요"
                   required>
          </div>

          <div class="form-group">
            <label>카테고리 설명</label>
            <textarea v-model="category.boardCategoryDescription" 
                      class="form-textarea" 
                      placeholder="카테고리 설명을 입력하세요"
                      rows="4"></textarea>
          </div>

          <div class="form-group">
            <label>허용 권한</label>
            <select v-model="category.boardAllowedRole" class="form-select">
              <option value="ROLE_USER">일반 사용자</option>
              <option value="ROLE_ADMIN">관리자</option>
            </select>
          </div>
        </div>

        <!-- 파일 설정 -->
        <div class="form-section">
          <h3>파일 설정</h3>
          
          <div class="form-group">
            <div class="checkbox-group">
              <label class="checkbox-label">
                <input type="checkbox" 
                       v-model="category.boardAllowImage" 
                       class="form-checkbox">
                <span class="checkbox-text">이미지 업로드 허용</span>
              </label>
            </div>
          </div>

          <div v-if="category.boardAllowImage" class="form-group">
            <label>최대 이미지 개수</label>
            <input type="number" 
                   v-model="category.boardMaxImageCount" 
                   class="form-input"
                   min="1" 
                   max="10">
          </div>

          <div class="form-group">
            <div class="checkbox-group">
              <label class="checkbox-label">
                <input type="checkbox" 
                       v-model="category.boardAllowAttachment" 
                       class="form-checkbox">
                <span class="checkbox-text">첨부파일 업로드 허용</span>
              </label>
            </div>
          </div>

          <div class="form-group">
            <label>최대 파일 크기 (MB)</label>
            <input type="number" 
                   v-model="category.boardMaxFileSize" 
                   class="form-input"
                   min="1" 
                   max="100">
          </div>
        </div>

        <!-- 게시판 설정 -->
        <div class="form-section">
          <h3>게시판 설정</h3>
          
          <div class="form-group">
            <label>에디터 타입</label>
            <select v-model="category.editorType" class="form-select">
            <option value="RICH_TEXT">리치 텍스트 에디터</option>
            <option value="MARKDOWN">마크다운 에디터</option>
            <option value="PLAIN_TEXT">일반 텍스트</option>
            </select>
          </div>

          <div class="form-group">
            <div class="checkbox-group">
              <label class="checkbox-label">
                <input type="checkbox" 
                       v-model="category.boardAllowReply" 
                       class="form-checkbox">
                <span class="checkbox-text">댓글 기능 허용</span>
              </label>
            </div>
          </div>

          <div class="form-group">
            <div class="checkbox-group">
              <label class="checkbox-label">
                <input type="checkbox" 
                       v-model="category.boardAllowRecommend" 
                       class="form-checkbox">
                <span class="checkbox-text">추천 기능 허용</span>
              </label>
            </div>
          </div>
        </div>

        <!-- 통계 정보 (읽기 전용) -->
        <div class="form-section">
          <h3>통계 정보</h3>
          
          <div class="form-group">
            <label>총 게시글 수</label>
            <input v-model="category.totalBoards" 
                   class="form-input" 
                   readonly 
                   disabled>
          </div>

          <div class="form-group">
            <label>생성일</label>
            <input :value="formatDate(category.createdAt)" 
                   class="form-input" 
                   readonly 
                   disabled>
          </div>

          <div class="form-group">
            <label>수정일</label>
            <input :value="formatDate(category.updatedAt)" 
                   class="form-input" 
                   readonly 
                   disabled>
          </div>
        </div>
      </div>

      <!-- 결과 메시지 -->
      <div v-if="resultMessage" 
           class="result-message" 
           :class="resultType">
        {{ resultMessage }}
      </div>

      <!-- 액션 버튼 -->
      <div class="form-actions">
        <button @click="goBack" 
                class="btn btn-secondary"
                :disabled="loading">
          취소
        </button>
        <button @click="updateCategory" 
                class="btn btn-primary"
                :disabled="loading">
          {{ loading ? '수정 중...' : '수정하기' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const category = ref(null)
const boardCategories = ref([]) // 서브 네비게이션용
const loading = ref(false)
const resultMessage = ref('')
const resultType = ref('')

// 기존 카테고리 정보 로딩
const getCategoryDetail = async () => {
  try {
    const categoryNo = route.params.categoryNo
    const res = await axios.get(`/v1/board-categories/${categoryNo}`)
    category.value = res.data
    console.log('기존 카테고리 데이터 로딩:', category.value)
  } catch (err) {
    console.error('카테고리 정보 조회 실패:', err)
    alert('카테고리 정보를 불러오는 중 오류가 발생했습니다.')
  }
}

// 카테고리 수정
const updateCategory = async () => {
  if (!category.value.boardCategoryTitle) {
    alert('카테고리 제목을 입력해주세요.')
    return
  }

  loading.value = true
  resultMessage.value = ''

  try {
    const categoryNo = route.params.categoryNo
    const res = await axios.patch(`/v1/board-categories/${categoryNo}`, category.value)
    
    resultMessage.value = '카테고리가 성공적으로 수정되었습니다.'
    resultType.value = 'success'
    
    // 3초 후 상세 페이지로 이동
    setTimeout(() => {
      router.push({ name: 'BoardCategoryDetail', params: { categoryNo } })
    }, 2000)
    
  } catch (err) {
    console.error('카테고리 수정 실패:', err)
    resultMessage.value = '카테고리 수정 중 오류가 발생했습니다.'
    resultType.value = 'error'
  } finally {
    loading.value = false
  }
}

const goBack = () => {
  router.push({ name: 'BoardCategoryDetail', params: { categoryNo: route.params.categoryNo } })
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return dateStr.replace('T', ' ').split('.')[0]
}

onMounted(() => {
  getCategoryDetail()
})
</script>

<style scoped>
.board-category-form {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.form-header {
  text-align: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.form-header h2 {
  color: #333;
  font-size: 24px;
  margin: 0 0 10px 0;
}

.form-header p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.form-container {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.form-section {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 6px;
  border: 1px solid #e0e0e0;
}

.form-section h3 {
  color: #333;
  font-size: 18px;
  margin: 0 0 20px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid #ddd;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #333;
  font-size: 14px;
}

.required {
  color: #e74c3c;
}

.form-input,
.form-textarea,
.form-select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
  box-sizing: border-box;
}

.form-input:focus,
.form-textarea:focus,
.form-select:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}

.form-input:disabled {
  background-color: #f5f5f5;
  color: #666;
}

.form-textarea {
  resize: vertical;
  min-height: 80px;
}

.checkbox-group {
  display: flex;
  align-items: center;
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
  margin-bottom: 0;
}

.form-checkbox {
  width: auto;
  margin-right: 10px;
  transform: scale(1.2);
}

.checkbox-text {
  color: #333;
  font-size: 14px;
}

.result-message {
  padding: 15px;
  border-radius: 4px;
  text-align: center;
  font-weight: 500;
  margin: 20px 0;
}

.result-message.success {
  background-color: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.result-message.error {
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.form-actions {
  display: flex;
  gap: 15px;
  justify-content: center;
  padding: 20px 0;
  border-top: 1px solid #e0e0e0;
}

.btn {
  padding: 12px 24px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #3498db;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #2980b9;
}

.btn-secondary {
  background-color: #95a5a6;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #7f8c8d;
}

.sub-nav-section {
  background-color: #f8f9fa;
  padding: 15px 0;
  margin-bottom: 20px;
}

.sub-nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.sub-nav-title {
  font-size: 20px;
  margin-bottom: 10px;
}

.sub-nav-menu {
  display: flex;
  gap: 20px;
}

.sub-nav-item {
  padding: 8px 16px;
  background-color: white;
  border-radius: 4px;
  border: 1px solid #dee2e6;
}

@media (max-width: 600px) {
  .board-category-form {
    padding: 15px;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }
}
</style>