<template>
  <div class="board-detail">
    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading">
      <h2>게시글을 불러오는 중...</h2>
    </div>

    <!-- 게시글 내용 -->
    <div v-else-if="board" class="detail-container">
      <!-- 헤더 -->
      <div class="detail-header">
        <div class="header-content">
          <div class="breadcrumb">
            <span @click="goBack" class="breadcrumb-link">커뮤니티</span>
            <span class="breadcrumb-separator">></span>
            <span @click="goToBoard" class="breadcrumb-link">{{ categoryInfo.title }}</span>
            <span class="breadcrumb-separator">></span>
            <span class="breadcrumb-current">게시글 보기</span>
          </div>
        </div>
      </div>

      <!-- 게시글 정보 -->
      <div class="board-content">
        <!-- 제목 및 메타 정보 -->
        <div class="board-header">
          <h1 class="board-title">{{ board.boardTitle }}</h1>
          <div class="board-meta">
            <div class="meta-left">
              <span class="author">작성자: {{ board.userNickname || '익명' }}</span>
              <span class="date">{{ formatDate(board.createdAt) }}</span>
              <span class="views">조회수: {{ board.viewCount || 0 }}</span>
            </div>
            <!-- 작성자인 경우 수정/삭제 버튼 -->
            <div v-if="isAuthor" class="action-buttons">
              <button @click="editPost" class="btn btn-edit">
                ✏️ 수정하기
              </button>
              <button @click="deletePost" class="btn btn-delete">
                🗑️ 삭제하기
              </button>
            </div>
          </div>
        </div>

        <!-- 게시글 내용 -->
        <div class="board-body">
          <div class="content-text">
            {{ board.boardContent }}
          </div>

          <!-- 첨부 이미지들 -->
          <div v-if="attachedImages.length > 0" class="attached-images">
            <h3>첨부 이미지</h3>
            <div class="image-gallery">
              <div 
                v-for="(image, index) in attachedImages" 
                :key="image.fileNo"
                @click="openImageModal(index)"
                class="image-item"
              >
                <img 
                  :src="`/v1/multifile/image/${image.fileNo}`" 
                  :alt="image.fileName"
                  class="board-image"
                />
              </div>
            </div>
          </div>

          <!-- 첨부 파일들 -->
          <div v-if="attachedFiles.length > 0" class="attached-files">
            <h3>첨부 파일</h3>
            <div class="file-list">
              <div 
                v-for="file in attachedFiles" 
                :key="file.fileNo"
                class="file-item"
              >
                <div class="file-info">
                  <span class="file-icon">📎</span>
                  <span class="file-name">{{ file.fileName }}</span>
                  <span class="file-size">({{ formatFileSize(file.fileSize) }})</span>
                </div>
                <a 
                  :href="`/v1/multifile/download/${file.fileNo}`" 
                  class="download-btn"
                  download
                >
                  다운로드
                </a>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 댓글 섹션 -->
      <div class="comments-section">
        <!-- 댓글 작성 -->
        <div class="comment-form-container">
          <h3>댓글 작성</h3>
          <div v-if="userStore.user" class="comment-form">
            <div class="comment-input-wrapper">
              <textarea
                v-model="newComment"
                placeholder="댓글을 입력하세요..."
                class="comment-input"
                rows="3"
                maxlength="1000"
              ></textarea>
              <div class="comment-actions">
                <span class="char-count">{{ newComment.length }}/1000</span>
                <button 
                  @click="submitComment" 
                  :disabled="!newComment.trim() || isSubmittingComment"
                  class="btn btn-primary btn-sm"
                >
                  <span v-if="isSubmittingComment">등록 중...</span>
                  <span v-else>댓글 등록</span>
                </button>
              </div>
            </div>
          </div>
          <div v-else class="login-required">
            <p>댓글을 작성하려면 <router-link to="/login" class="login-link">로그인</router-link>이 필요합니다.</p>
          </div>
        </div>

        <!-- 댓글 목록 -->
        <div class="comments-container">
          <div class="comments-header">
            <h3>댓글 {{ replies.length }}개</h3>
            <div class="sort-options">
              <button 
                @click="sortType = 'latest'" 
                :class="{ active: sortType === 'latest' }"
                class="sort-btn"
              >
                최신순
              </button>
              <button 
                @click="sortType = 'oldest'" 
                :class="{ active: sortType === 'oldest' }"
                class="sort-btn"
              >
                등록순
              </button>
            </div>
          </div>

          <!-- 댓글 로딩 -->
          <div v-if="loadingComments" class="comments-loading">
            <p>댓글을 불러오는 중...</p>
          </div>

          <!-- 댓글 없음 -->
          <div v-else-if="sortedReplies.length === 0" class="no-comments">
            <p>아직 댓글이 없습니다. 첫 번째 댓글을 작성해보세요!</p>
          </div>

          <!-- 댓글 리스트 -->
          <div v-else class="comments-list">
            <div 
              v-for="reply in sortedReplies" 
              :key="reply.replyNo"
              class="comment-item"
            >
              <div class="comment-content">
                <div class="comment-header">
                  <div class="comment-author">
                    <span class="author-name">{{ reply.userNickname }}</span>
                    <span class="comment-date">{{ formatDate(reply.createdAt) }}</span>
                  </div>
                  <div class="comment-actions">
                    <button 
                      v-if="userStore.user"
                      @click="startReplyComment(reply)"
                      class="action-btn reply-btn"
                    >
                      답글
                    </button>
                    <button 
                      v-if="isCommentAuthor(reply)"
                      @click="startEditComment(reply)"
                      class="action-btn edit-btn"
                    >
                      수정
                    </button>
                    <button 
                      v-if="isCommentAuthor(reply)"
                      @click="deleteComment(reply.replyNo)"
                      class="action-btn delete-btn"
                    >
                      삭제
                    </button>
                  </div>
                </div>

                <!-- 댓글 내용 (읽기 모드) -->
                <div v-if="editingCommentId !== reply.replyNo" class="comment-text">
                  {{ reply.replyContent }}
                </div>

                <!-- 댓글 수정 폼 -->
                <div v-else class="comment-edit-form">
                  <textarea
                    v-model="editingCommentContent"
                    class="edit-input"
                    rows="3"
                    maxlength="1000"
                  ></textarea>
                  <div class="edit-actions">
                    <button 
                      @click="cancelEditComment"
                      class="btn btn-secondary btn-sm"
                    >
                      취소
                    </button>
                    <button 
                      @click="updateComment(reply.replyNo)"
                      :disabled="!editingCommentContent.trim() || isUpdatingComment"
                      class="btn btn-primary btn-sm"
                    >
                      <span v-if="isUpdatingComment">수정 중...</span>
                      <span v-else>수정 완료</span>
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 오류 상태 -->
    <div v-else class="error-state">
      <h2>게시글을 찾을 수 없습니다</h2>
      <p>삭제되었거나 존재하지 않는 게시글입니다.</p>
      <button @click="goToBoard" class="btn btn-primary">목록으로 돌아가기</button>
    </div>

    <!-- 이미지 모달 -->
    <div v-if="showImageModal" @click="closeImageModal" class="image-modal">
      <div class="modal-content" @click.stop>
        <button @click="closeImageModal" class="modal-close">×</button>
        <img 
          v-if="currentImageIndex >= 0"
          :src="`/v1/multifile/image/${attachedImages[currentImageIndex].fileNo}`" 
          :alt="attachedImages[currentImageIndex].fileName"
          class="modal-image"
        />
        <div class="modal-controls">
          <button 
            @click="prevImage" 
            :disabled="currentImageIndex <= 0"
            class="nav-btn"
          >
            ‹ 이전
          </button>
          <span class="image-counter">
            {{ currentImageIndex + 1 }} / {{ attachedImages.length }}
          </span>
          <button 
            @click="nextImage" 
            :disabled="currentImageIndex >= attachedImages.length - 1"
            class="nav-btn"
          >
            다음 ›
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import axios from 'axios'

// CommentItem 컴포넌트 정의
const CommentItem = {
  name: 'CommentItem',
  props: {
    reply: Object,
    userStore: Object,
    editingCommentId: Number,
    editingCommentContent: String,
    isUpdatingComment: Boolean,
    replyingToId: Number,
    replyContent: String,
    isSubmittingReply: Boolean,
    depth: {
      type: Number,
      default: 0
    }
  },
  emits: ['edit', 'delete', 'reply', 'update', 'cancel-edit', 'submit-reply', 'cancel-reply'],
  setup(props, { emit }) {
    const isCommentAuthor = computed(() => {
      if (!props.userStore.user) return false
      return props.reply.userNo === props.userStore.user.userNo
    })

    const formatDate = (dateString) => {
      if (!dateString) return ''
      
      const date = new Date(dateString)
      const now = new Date()
      const diffMs = now - date
      const diffMins = Math.floor(diffMs / 60000)
      const diffHours = Math.floor(diffMs / 3600000)
      const diffDays = Math.floor(diffMs / 86400000)
      
      if (diffMins < 1) return '방금 전'
      if (diffMins < 60) return `${diffMins}분 전`
      if (diffHours < 24) return `${diffHours}시간 전`
      if (diffDays < 7) return `${diffDays}일 전`
      
      return date.toLocaleString('ko-KR', {
        year: 'numeric',
        month: 'long',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit'
      })
    }

    return {
      isCommentAuthor,
      formatDate
    }
  },
}

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 반응형 데이터
const board = ref(null)
const categoryInfo = ref({ title: '게시판' })
const attachedImages = ref([])
const attachedFiles = ref([])
const replies = ref([])
const loading = ref(true)
const loadingComments = ref(true)
const showImageModal = ref(false)
const currentImageIndex = ref(0)

// 댓글 관련 데이터
const newComment = ref('')
const isSubmittingComment = ref(false)
const sortType = ref('latest')
const editingCommentId = ref(null)
const editingCommentContent = ref('')
const isUpdatingComment = ref(false)
const replyingToId = ref(null)
const replyContent = ref('')
const isSubmittingReply = ref(false)

// 작성자 권한 체크
const isAuthor = computed(() => {
  if (!board.value || !userStore.user) return false
  return board.value.userNo === userStore.user.userNo
})

// 댓글 작성자 권한 체크
const isCommentAuthor = (reply) => {
  if (!userStore.user) return false
  return reply.userNo === userStore.user.userNo
}

// 정렬된 댓글 목록
const sortedReplies = computed(() => {
  const sorted = [...replies.value]
  if (sortType.value === 'latest') {
    return sorted.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
  } else {
    return sorted.sort((a, b) => new Date(a.createdAt) - new Date(b.createdAt))
  }
})

// 게시글 상세 정보 로드
const loadBoardDetail = async () => {
  try {
    loading.value = true
    const boardNo = route.params.boardNo

    // 1. 게시글 정보 조회
    const boardResponse = await axios.get(`/v1/board/${boardNo}`)
    board.value = boardResponse.data

    // 2. 카테고리 정보 조회
    if (board.value.categoryNo) {
      const categoryResponse = await axios.get(`/v1/board-categories/${board.value.categoryNo}`)
      categoryInfo.value = {
        title: categoryResponse.data.boardCategoryTitle || '게시판'
      }
    }

    // 3. 첨부파일 조회
    await loadBoardFiles(boardNo)

    // 4. 댓글 조회
    await loadComments(boardNo)

  } catch (error) {
    console.error('게시글 로드 실패:', error)
    board.value = null
  } finally {
    loading.value = false
  }
}

// 게시글 파일 로드
const loadBoardFiles = async (boardNo) => {
  try {
    const filesResponse = await axios.get(`/v1/multifile`, {
      params: {
        referenceTable: 'BOARD',
        referenceNo: boardNo
      }
    })

    let files = []
    const responseData = filesResponse.data
    if (Array.isArray(responseData)) {
      files = responseData
    } else if (responseData && Array.isArray(responseData.data)) {
      files = responseData.data
    } else if (responseData && Array.isArray(responseData.content)) {
      files = responseData.content
    } else {
      files = []
    }
    
    // 파일 분리
    attachedImages.value = files.filter(file => 
      file && file.fileMimeType && file.fileMimeType.startsWith('image/')
    )
    attachedFiles.value = files.filter(file => 
      file && file.fileMimeType && !file.fileMimeType.startsWith('image/')
    )

  } catch (fileError) {
    console.log('파일 조회 실패 (정상 - 파일 없음):', fileError)
    attachedImages.value = []
    attachedFiles.value = []
  }
}

// 댓글 목록 로드
const loadComments = async (boardNo) => {
  try {
    loadingComments.value = true
    const response = await axios.get(`/v1/board/${boardNo}/replies`)
    console.log('📝 댓글 API 응답:', response.data) // 디버깅용
    replies.value = response.data || []
    console.log('📝 저장된 댓글:', replies.value) // 디버깅용
  } catch (error) {
    console.error('댓글 로드 실패:', error)
    replies.value = []
  } finally {
    loadingComments.value = false
  }
}

// 댓글 작성
const submitComment = async () => {
  if (!newComment.value.trim() || isSubmittingComment.value) return

  try {
    isSubmittingComment.value = true
    const boardNo = route.params.boardNo

    const commentData = {
      content: newComment.value.trim(),
      parentReplyNo: null  // 일반 댓글은 부모가 없음
    }

    console.log('전송할 댓글 데이터:', commentData)

    const response = await axios.post(`/v1/board/${boardNo}/replies`, commentData)
    
    // 댓글 목록 새로고침 (계층구조를 위해)
    await loadComments(boardNo)
    newComment.value = ''
    
  } catch (error) {
    console.error('댓글 작성 실패:', error)
    console.log('에러 응답:', error.response?.data)
    alert('댓글 작성에 실패했습니다.')
  } finally {
    isSubmittingComment.value = false
  }
}

// 댓글 수정 시작
const startEditComment = (reply) => {
  editingCommentId.value = reply.replyNo
  editingCommentContent.value = reply.content || reply.replyContent || '' // 기본값 설정
}

// 댓글 수정 취소
const cancelEditComment = () => {
  editingCommentId.value = null
  editingCommentContent.value = ''
}

// 답글 시작
const startReplyComment = (reply) => {
  console.log('답글 시작:', reply) // 디버깅용
  replyingToId.value = reply.replyNo
  replyContent.value = ''
}

// 답글 취소
const cancelReplyComment = () => {
  replyingToId.value = null
  replyContent.value = ''
}

// 답글 작성
const submitReply = async (parentReplyNo, content) => {
  if (!content.trim() || isSubmittingReply.value) return

  try {
    isSubmittingReply.value = true
    const boardNo = route.params.boardNo

    const replyData = {
      content: content.trim(),
      parentReplyNo: parentReplyNo  // 부모 댓글 번호
    }

    const response = await axios.post(`/v1/board/${boardNo}/replies`, replyData)
    
    // 댓글 목록 새로고침
    await loadComments(boardNo)
    
    // 답글 작성 모드 종료
    replyingToId.value = null
    replyContent.value = ''
    
  } catch (error) {
    console.error('답글 작성 실패:', error)
    alert('답글 작성에 실패했습니다.')
  } finally {
    isSubmittingReply.value = false
  }
}

// 댓글 수정
const updateComment = async (replyNo) => {
  if (!editingCommentContent.value || !editingCommentContent.value.trim() || isUpdatingComment.value) return

  try {
    isUpdatingComment.value = true

    const updateData = {
      content: editingCommentContent.value.trim()
    }

    const response = await axios.patch(`/v1/replies/${replyNo}`, updateData)
    
    // 댓글 목록 새로고침 (계층구조 유지를 위해)
    await loadComments(route.params.boardNo)
    
    // 수정 모드 종료
    editingCommentId.value = null
    editingCommentContent.value = ''
    
  } catch (error) {
    console.error('댓글 수정 실패:', error)
    alert('댓글 수정에 실패했습니다.')
  } finally {
    isUpdatingComment.value = false
  }
}

// 댓글 삭제
const deleteComment = async (replyNo) => {
  if (!confirm('이 댓글을 삭제하시겠습니까?')) return

  try {
    await axios.delete(`/v1/replies/${replyNo}`)
    
    // 댓글 목록 새로고침 (계층구조 유지를 위해)
    await loadComments(route.params.boardNo)
    
  } catch (error) {
    console.error('댓글 삭제 실패:', error)
    alert('댓글 삭제에 실패했습니다.')
  }
}

// 날짜 포맷
const formatDate = (dateString) => {
  if (!dateString) return ''
  
  const date = new Date(dateString)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)
  
  if (diffMins < 1) return '방금 전'
  if (diffMins < 60) return `${diffMins}분 전`
  if (diffHours < 24) return `${diffHours}시간 전`
  if (diffDays < 7) return `${diffDays}일 전`
  
  return date.toLocaleString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 파일 크기 포맷
const formatFileSize = (bytes) => {
  if (!bytes) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 이미지 모달 관련
const openImageModal = (index) => {
  currentImageIndex.value = index
  showImageModal.value = true
}

const closeImageModal = () => {
  showImageModal.value = false
}

const prevImage = () => {
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--
  }
}

const nextImage = () => {
  if (currentImageIndex.value < attachedImages.value.length - 1) {
    currentImageIndex.value++
  }
}

// 네비게이션
const goBack = () => {
  router.go(-1)
}

const goToBoard = () => {
  if (board.value && board.value.categoryNo) {
    router.push(`/category/${board.value.categoryNo}`)
  } else {
    router.push('/board')
  }
}

// 게시글 수정
const editPost = () => {
  const boardNo = route.params.boardNo
  router.push(`/board/${boardNo}/edit`)
}

// 게시글 삭제
const deletePost = async () => {
  if (!confirm('정말로 이 게시글을 삭제하시겠습니까?')) {
    return
  }

  try {
    const boardNo = route.params.boardNo
    await axios.delete(`/v1/board/${boardNo}`)
    
    alert('게시글이 삭제되었습니다.')
    goToBoard()
  } catch (error) {
    console.error('게시글 삭제 실패:', error)
    alert('게시글 삭제에 실패했습니다.')
  }
}

// 초기 로드
onMounted(() => {
  loadBoardDetail()
})
</script>

<style scoped>
.board-detail {
  min-height: 100vh;
  background: #f8f9fa;
}

.loading, .error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 50vh;
  text-align: center;
}

.detail-header {
  background: white;
  border-bottom: 1px solid #e9ecef;
  box-shadow: 0 2px 4px rgba(0,0,0,0.05);
}

.header-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.breadcrumb {
  font-size: 14px;
  color: #6c757d;
}

.breadcrumb-link {
  color: #007bff;
  cursor: pointer;
  transition: color 0.2s;
}

.breadcrumb-link:hover {
  color: #0056b3;
}

.breadcrumb-separator {
  margin: 0 8px;
  color: #dee2e6;
}

.detail-container {
  max-width: 800px;
  margin: 0 auto;
}

.board-content {
  background: white;
  margin: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.08);
  overflow: hidden;
}

.board-header {
  padding: 30px;
  border-bottom: 1px solid #e9ecef;
}

.board-title {
  font-size: 28px;
  font-weight: bold;
  color: #2c3e50;
  margin: 0 0 15px 0;
  line-height: 1.3;
}

.board-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
}

.meta-left {
  display: flex;
  gap: 20px;
  color: #6c757d;
  font-size: 14px;
  flex-wrap: wrap;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.board-body {
  padding: 30px;
}

.content-text {
  font-size: 16px;
  line-height: 1.7;
  color: #2c3e50;
  white-space: pre-wrap;
  margin-bottom: 30px;
}

.attached-images, .attached-files {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

.attached-images h3, .attached-files h3 {
  margin-bottom: 15px;
  color: #495057;
  font-size: 18px;
}

.image-gallery {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 15px;
}

.image-item {
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.2s;
}

.image-item:hover {
  transform: scale(1.02);
}

.board-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.file-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
}

.file-icon {
  font-size: 18px;
}

.file-name {
  font-weight: 500;
  color: #495057;
}

.file-size {
  color: #6c757d;
  font-size: 12px;
}

.download-btn {
  background: #007bff;
  color: white;
  padding: 6px 12px;
  border-radius: 4px;
  text-decoration: none;
  font-size: 12px;
  transition: background-color 0.2s;
}

.download-btn:hover {
  background: #0056b3;
}

/* 댓글 섹션 */
.comments-section {
  background: white;
  margin: 20px;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.08);
  overflow: hidden;
}

.comment-form-container {
  padding: 30px;
  border-bottom: 1px solid #e9ecef;
}

.comment-form-container h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 20px;
}

.comment-input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.comment-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  transition: border-color 0.2s;
  font-family: inherit;
  box-sizing: border-box;
}

.comment-input:focus {
  outline: none;
  border-color: #007bff;
}

.comment-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.char-count {
  font-size: 12px;
  color: #6c757d;
}

.login-required {
  text-align: center;
  padding: 20px;
  color: #6c757d;
}

.login-link {
  color: #007bff;
  text-decoration: none;
}

.login-link:hover {
  text-decoration: underline;
}

.comments-container {
  padding: 30px;
}

.comments-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.comments-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 18px;
}

.sort-options {
  display: flex;
  gap: 10px;
}

.sort-btn {
  background: none;
  border: 1px solid #e9ecef;
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
}

.sort-btn.active,
.sort-btn:hover {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.comments-loading,
.no-comments {
  text-align: center;
  padding: 40px 20px;
  color: #6c757d;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.comment-item {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  overflow: hidden;
}

.comment-content {
  padding: 20px;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.comment-author {
  display: flex;
  align-items: center;
  gap: 10px;
}

.author-name {
  font-weight: 500;
  color: #2c3e50;
}

.comment-date {
  font-size: 12px;
  color: #6c757d;
}

.comment-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  background: none;
  border: none;
  font-size: 12px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.edit-btn {
  color: #28a745;
}

.edit-btn:hover {
  background: #e8f5e8;
}

.delete-btn {
  color: #dc3545;
}

.delete-btn:hover {
  background: #f8e8e8;
}

.comment-text {
  color: #495057;
  line-height: 1.5;
  white-space: pre-wrap;
}

.comment-edit-form {
  margin-top: 10px;
}

.edit-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  transition: border-color 0.2s;
  font-family: inherit;
  box-sizing: border-box;
  margin-bottom: 10px;
}

.edit-input:focus {
  outline: none;
  border-color: #007bff;
}

.child-comment {
  border-left: 3px solid #e9ecef;
  background: #f8f9fa;
}

.reply-indicator {
  color: #6c757d;
  font-size: 12px;
  margin-left: 5px;
}

.reply-btn {
  color: #007bff;
}

.reply-btn:hover {
  background: #e3f2fd;
}

.reply-form {
  margin-top: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.reply-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  transition: border-color 0.2s;
  font-family: inherit;
  box-sizing: border-box;
  margin-bottom: 10px;
}

.reply-input:focus {
  outline: none;
  border-color: #007bff;
}

.reply-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.reply-btn {
  color: #007bff;
}

.reply-btn:hover {
  background: #e3f2fd;
}

.reply-form {
  margin-top: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e9ecef;
}

.reply-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  transition: border-color 0.2s;
  font-family: inherit;
  box-sizing: border-box;
  margin-bottom: 10px;
}

.reply-input:focus {
  outline: none;
  border-color: #007bff;
}

.reply-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 5px;
}

.btn-primary {
  background: #007bff;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #0056b3;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background: #545b62;
}

.btn-edit {
  background: #28a745;
  color: white;
}

.btn-edit:hover {
  background: #218838;
}

.btn-delete {
  background: #dc3545;
  color: white;
}

.btn-delete:hover {
  background: #c82333;
}

.btn-sm {
  padding: 6px 12px;
  font-size: 12px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 이미지 모달 */
.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  position: relative;
  max-width: 90%;
  max-height: 90%;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.modal-close {
  position: absolute;
  top: -40px;
  right: 0;
  background: none;
  border: none;
  color: white;
  font-size: 30px;
  cursor: pointer;
  z-index: 1001;
}

.modal-image {
  max-width: 100%;
  max-height: 80vh;
  object-fit: contain;
}

.modal-controls {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-top: 20px;
  color: white;
}

.nav-btn {
  background: rgba(255,255,255,0.2);
  border: 1px solid rgba(255,255,255,0.3);
  color: white;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
}

.nav-btn:hover:not(:disabled) {
  background: rgba(255,255,255,0.3);
}

.nav-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.image-counter {
  font-size: 14px;
  color: rgba(255,255,255,0.8);
}

/* 반응형 */
@media (max-width: 768px) {
  .board-content, .comments-section {
    margin: 10px;
    border-radius: 8px;
  }
  
  .board-header, .board-body, .comment-form-container, .comments-container {
    padding: 20px;
  }
  
  .board-title {
    font-size: 24px;
  }
  
  .board-meta {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .meta-left {
    flex-direction: column;
    gap: 5px;
  }
  
  .image-gallery {
    grid-template-columns: 1fr;
  }
  
  .file-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .comments-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .comment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .comment-actions {
    align-self: flex-end;
  }
  
  .edit-actions {
    justify-content: flex-start;
  }
}
</style>