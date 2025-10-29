<template>
  <div class="replies-container" v-if="allowReply && board">
    <!-- 게시글 헤더 -->
    <div class="board-header">
      <h2>{{ board.title }}</h2>
    </div>

    <!-- 새 댓글 작성 -->
    <div class="new-reply-form">
      <textarea v-model="newMainReplyContent" placeholder="댓글을 입력하세요..." rows="3" />
      <div class="form-actions">
        <button @click="submitNewReply" :disabled="!newMainReplyContent.trim()">댓글 작성</button>
      </div>
    </div>

    <!-- 댓글 리스트 -->
    <div class="replies-list">
      <div v-for="reply in replies" :key="reply.replyNo" class="reply-item">
        <div :class="['reply-content', `depth-${reply.depth}`]" :style="{ marginLeft: reply.depth * 20 + 'px' }">

          <!-- 댓글 헤더 -->
          <div class="reply-header">
            <span class="nickname">{{ reply.userNickname }}</span>
            <span class="date">{{ formatDate(reply.createdAt) }}</span>
            <span class="recommend-count" v-if="reply.recommendCount > 0">👍 {{ reply.recommendCount }}</span>
          </div>

          <!-- 댓글 내용 / 수정 폼 -->
          <div v-if="editingReply !== reply.replyNo" class="reply-text">{{ reply.content }}</div>
          <div v-else class="reply-edit-form">
            <textarea v-model="editReplyContent" placeholder="댓글을 수정하세요..." rows="3" class="edit-textarea" />
            <div class="form-actions">
              <button @click="submitEditReply(reply.replyNo)" :disabled="!editReplyContent.trim()">수정 완료</button>
              <button @click="cancelEditReply">취소</button>
            </div>
          </div>

          <!-- 댓글 액션 -->
          <div class="reply-actions">
            <button @click="toggleReplyForm(reply.replyNo)" class="action-btn reply-btn">답글</button>

            <!-- 추천 버튼: 클릭 시 바로 +1 -->
            <button
              class="action-btn recommend-btn"
              @click="recommendReply(reply.replyNo)"
            >
              👍 {{ reply.recommendCount || 0 }}
            </button>

            <button v-if="canEdit(reply)" @click="startEditReply(reply)" class="action-btn edit-btn">수정</button>
            <button v-if="canDelete(reply)" @click="deleteReply(reply.replyNo)" class="action-btn delete-btn">삭제</button>
          </div>

          <!-- 답글 폼 -->
          <div v-if="showReplyForm === reply.replyNo" class="reply-form">
            <textarea v-model="newReplyContent" placeholder="답글 입력..." rows="3" class="reply-textarea" />
            <div class="form-actions">
              <button @click="submitReply(reply.replyNo)" :disabled="!newReplyContent.trim()">답글 작성</button>
              <button @click="cancelReply">취소</button>
            </div>
          </div>

        </div>
      </div>
    </div>

    <!-- 페이징 -->
    <div class="pagination" v-if="totalPages > 1">
      <button v-for="page in totalPages" :key="page" @click="changePage(page)" :class="{ active: currentPage === page }">
        {{ page }}
      </button>
    </div>
  </div>

  <div v-else class="no-replies-message">
    댓글 기능이 비활성화되어 있거나 게시글을 불러오지 못했습니다.
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'
import { useUserStore } from '@/stores/user'

// === props ===
const props = defineProps({
  boardNo: { type: Number, required: true },
  allowReply: { type: Boolean, default: true }
})

// === emit (부모로 댓글 개수 전달) ===
const emit = defineEmits(['update:replyCount'])

// === 상태 ===
const userStore = useUserStore()
const board = ref(null)
const replies = ref([])

const newMainReplyContent = ref('')
const newReplyContent = ref('')
const showReplyForm = ref(null)

const editingReply = ref(null)
const editReplyContent = ref('')

const currentPage = ref(1)
const pageSize = ref(20)
const totalPages = ref(0)
const totalElements = ref(0)

// === 게시글 로드 ===
const loadBoard = async () => {
  try {
    const res = await axios.get(`/v1/board/${props.boardNo}`)
    board.value = res.data
  } catch (err) {
    console.error('게시글 로드 실패:', err)
  }
}

// === 댓글 로드 ===
const loadReplies = async () => {
  try {
    const res = await axios.get(`/v1/board/${props.boardNo}/replies`, {
      params: { page: currentPage.value - 1, size: pageSize.value }
    })

    // 정렬 (추천순 → 최신순)
    replies.value = [...res.data].sort((a, b) => (b.recommendCount || 0) - (a.recommendCount || 0))
    totalPages.value = 1
    totalElements.value = res.data.length

    // ✅ 부모로 댓글 개수 전달
    emit('update:replyCount', replies.value.length)
  } catch (err) {
    console.error('댓글 로드 실패:', err)
  }
}

// === 댓글 작성 ===
const submitNewReply = async () => {
  if (!newMainReplyContent.value.trim()) return
  try {
    await axios.post(`/v1/board/${props.boardNo}/replies`, {
      content: newMainReplyContent.value,
      parentReplyNo: null
    })
    newMainReplyContent.value = ''
    await loadReplies()
  } catch (err) {
    console.error('댓글 작성 실패:', err)
    alert('댓글 작성 실패')
  }
}

// === 답글 작성 ===
const submitReply = async (parentReplyNo) => {
  if (!newReplyContent.value.trim()) return
  try {
    await axios.post(`/v1/board/${props.boardNo}/replies`, {
      content: newReplyContent.value,
      parentReplyNo
    })
    newReplyContent.value = ''
    showReplyForm.value = null
    await loadReplies()
  } catch (err) {
    console.error('답글 작성 실패:', err)
    alert('답글 작성 실패')
  }
}

// === 댓글 수정 ===
const startEditReply = (reply) => {
  editingReply.value = reply.replyNo
  editReplyContent.value = reply.content
  showReplyForm.value = null
  newReplyContent.value = ''
}
const submitEditReply = async (replyNo) => {
  if (!editReplyContent.value.trim()) return
  try {
    await axios.patch(`/v1/replies/${replyNo}`, {
      content: editReplyContent.value.trim()
    })
    editingReply.value = null
    editReplyContent.value = ''
    await loadReplies()
  } catch (err) {
    console.error('댓글 수정 실패:', err)
    alert('댓글 수정 실패')
  }
}
const cancelEditReply = () => {
  editingReply.value = null
  editReplyContent.value = ''
}

// === 댓글 삭제 ===
const deleteReply = async (replyNo) => {
  if (!confirm('정말 삭제하시겠습니까?')) return
  try {
    await axios.delete(`/v1/replies/${replyNo}`)
    await loadReplies()
  } catch (err) {
    console.error('댓글 삭제 실패:', err)
    alert('댓글 삭제 실패')
  }
}

// === 답글 폼 토글 ===
const toggleReplyForm = (replyNo) => {
  showReplyForm.value = showReplyForm.value === replyNo ? null : replyNo
  newReplyContent.value = ''
  editingReply.value = null
  editReplyContent.value = ''
}

// === 권한 확인 ===
const canDelete = (reply) => {
  const currentUserNickname = userStore.user?.nickname || userStore.user?.userNickname
  const isAdmin = userStore.user?.isAdmin || userStore.user?.role === 'ADMIN'
  return reply.userNickname === currentUserNickname || isAdmin
}
const canEdit = (reply) => canDelete(reply)

// === 추천 기능 ===
const recommendReply = async (replyNo) => {
  if (!userStore.isLoggedIn) {
    alert('로그인이 필요합니다.')
    return
  }

  const headers = userStore.accessToken
    ? { Authorization: `Bearer ${userStore.accessToken}` }
    : {}

  try {
    // 현재 추천 상태 가져오기
    const reply = replies.value.find(r => r.replyNo === replyNo)
    if (!reply) return

    if (reply.isRecommended) {
      // 추천 취소
      await axios.delete(`/v1/replies/${replyNo}/recommend`, { headers })
      reply.isRecommended = false
      reply.recommendCount = Math.max((reply.recommendCount || 0) - 1, 0)
    } else {
      // 추천 추가
      await axios.post(`/v1/replies/${replyNo}/recommend`, {}, { headers })
      reply.isRecommended = true
      reply.recommendCount = (reply.recommendCount || 0) + 1
    }
  } catch (err) {
    console.error('추천 토글 실패:', err)
    alert('추천 처리 중 오류가 발생했습니다.')
  }
}

// === 날짜 포맷 ===
const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// === 페이지 이동 ===
const changePage = (page) => {
  currentPage.value = page
  loadReplies()
}

// === 마운트 시 실행 ===
onMounted(() => {
  loadBoard()
  loadReplies()
})
</script>

<style scoped>
.replies-container {
  margin-top: 20px;
}

.popular-replies {
  margin-bottom: 30px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
}

.popular-badge {
  background: #ff6b6b;
  color: white;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 10px;
  font-weight: bold;
}

.reply-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.action-btn {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background: #fff;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #f5f5f5;
}

.reply-btn:hover {
  border-color: #007bff;
  color: #007bff;
}

.recommend-btn {
  border-color: #28a745;
  color: #28a745;
}

.recommend-btn.active {
  background: #28a745;
  color: white;
}

.recommend-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.edit-btn {
  border-color: #ffc107;
  color: #ffc107;
}

.edit-btn:hover {
  background: #ffc107;
  color: white;
}

.delete-btn {
  border-color: #dc3545;
  color: #dc3545;
}

.delete-btn:hover {
  background: #dc3545;
  color: white;
}

.reply-edit-form {
  margin: 8px 0;
}

.edit-textarea,
.reply-textarea {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
  font-family: inherit;
}

.form-actions {
  display: flex;
  gap: 8px;
  margin-top: 8px;
}

.form-actions button {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
}

.form-actions button:first-child {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.form-actions button:first-child:hover {
  background: #0056b3;
}

.form-actions button:first-child:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.form-actions button:last-child {
  background: #6c757d;
  color: white;
  border-color: #6c757d;
}

.form-actions button:last-child:hover {
  background: #545b62;
}

.reply-item {
  border-bottom: 1px solid #eee;
  padding: 12px 0;
}

.reply-content {
  transition: margin-left 0.2s ease;
  padding: 12px;
  border-radius: 6px;
  margin-bottom: 8px;
  /* position: relative; */
}

.reply-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 8px;
}

.nickname {
  font-weight: bold;
  color: #333;
}

.date {
  color: #666;
  font-size: 12px;
}

/* 부모 댓글 (depth=0) */
.reply-content[style*="marginLeft: 0px"] {
  background-color: #ffffff;
  border: 1px solid #e9ecef;
}

.reply-content.depth-0 {
  margin-left: 0px !important;
  background-color: #ffffff !important;
  border: 1px solid #e9ecef !important;
}

.reply-content.depth-1 {
  margin-left: 40px !important;
  background-color: #f8f9fa !important;
  border-left: 4px solid #007bff !important;
  border: 1px solid #dee2e6 !important;
}

.reply-content.depth-2 {
  margin-left: 80px !important;
  background-color: #fff3cd !important;
  border-left: 4px solid #ffc107 !important;
  border: 1px solid #ffeaa7 !important;
}

.reply-content.depth-3 {
  margin-left: 120px !important;
  background-color: #d4edda !important;
  border-left: 4px solid #28a745 !important;
}

/* 호버 효과 개선 */
.reply-content:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

/* 대댓글 연결선 (선택사항) */
.reply-content[style*="marginLeft: 20px"] {
  position: relative;
}

.reply-content[style*="marginLeft: 20px"]::after {
  content: "";
  position: absolute;
  left: -10px;
  top: 0;
  bottom: 0;
  width: 2px;
  background-color: #007bff;
  opacity: 0.3;
}


.recommend-count {
  color: #28a745;
  font-size: 12px;
  font-weight: bold;
}

.reply-text {
  color: #333;
  line-height: 1.5;
  margin-bottom: 8px;
  white-space: pre-wrap;
}

.new-reply-form {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
}

.new-reply-form textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  resize: vertical;
}

.pagination {
  display: flex;
  justify-content: center;
  gap: 5px;
  margin-top: 20px;
}

.pagination button {
  padding: 8px 12px;
  border: 1px solid #ddd;
  background: white;
  cursor: pointer;
  border-radius: 4px;
}

.pagination button.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.no-replies-message {
  text-align: center;
  padding: 40px;
  color: #666;
  background: #f8f9fa;
  border-radius: 8px;
  margin-top: 20px;
}
</style>
