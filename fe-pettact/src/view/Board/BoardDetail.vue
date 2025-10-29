<template>
  <div class="board-detail">
    <!-- 로딩 상태 -->
    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
      <p>게시글을 불러오는 중...</p>
    </div>

    <!-- 게시글 내용 -->
    <div v-else-if="board" class="detail-container">
      <!-- 상단 네비게이션 -->
      <div class="breadcrumb-nav">
        <span @click="goBack" class="nav-item">커뮤니티</span>
        <span class="nav-divider">›</span>
        <span @click="goToBoard" class="nav-item">{{ categoryInfo.title }}</span>
        <span class="nav-divider">›</span>
        <span class="nav-current">게시글 보기</span>
      </div>

      <!-- 메인 컨텐츠 영역 -->
      <div class="content-wrapper">
        <!-- 게시글 헤더 -->
        <div class="post-header">
          <h1 class="post-title">{{ board.boardTitle }}</h1>
          
          <div class="post-info">
            <div class="info-left">
              <span class="author">{{ board.userNickname || "익명" }}</span>
              <span class="divider">·</span>
              <span class="date">{{ formatDate(board.createdAt) }}</span>
              <span class="divider">·</span>
              <span class="views">조회 {{ board.boardViewCnt }}</span>
            </div>
            
            <!-- 작성자 액션 버튼 -->
            <div v-if="isAuthor" class="author-actions">
              <button @click="editPost" class="action-btn edit">수정</button>
              <button @click="deletePost" class="action-btn delete">삭제</button>
              <button @click="openReportModal" class="action-btn report">신고</button>
            </div>
          </div>
        </div>

       
          <!-- 첨부 이미지 -->
          <div v-if="attachedImages.length > 0" class="attachments images">
            <div class="image-grid">
              <div
                v-for="(image, index) in attachedImages"
                :key="image.fileNo"
                @click="openImageModal(index)"
                class="image-wrapper"
              >
                <img
                  :src="`/v1/multifile/image/${image.fileNo}`"
                  :alt="image.fileName"
                  class="attached-image"
                />
              </div>
            </div>
          </div>
           <!-- 게시글 본문 -->
        <div class="post-body">
          <div class="post-content">
            {{ board.boardContent }}
          </div>

          <!-- 첨부 파일 -->
          <div v-if="attachedFiles.length > 0" class="attachments files">
            <div class="file-header">첨부파일</div>
            <div
              v-for="file in attachedFiles"
              :key="file.fileNo"
              class="file-row"
            >
              <div class="file-details">
                <span class="file-icon">📎</span>
                <span class="file-name">{{ file.fileName }}</span>
                <span class="file-size">{{ formatFileSize(file.fileSize) }}</span>
              </div>
              <button
                @click="downloadFile(file.fileNo, file.fileName)"
                class="file-download"
              >
                다운로드
              </button>
            </div>
          </div>
        </div>

        <!-- 게시글 하단 액션 -->
        <div class="post-footer">
          <!-- 추천 버튼 -->
          <div v-if="categoryInfo.allowRecommend" class="recommend-area">
            <button
              @click="toggleRecommend"
              :class="['recommend-button', { recommended: isRecommended }]"
              :disabled="isRecommending"
            >
              <span class="icon">👍</span>
              <span class="text">{{ isRecommended ? "추천 취소" : "추천" }}</span>
              <span class="count">{{ recommendCount }}</span>
            </button>
          </div>
          
          <!-- 추천 비활성화 메시지 -->
          <div v-else class="recommend-disabled">
            <span class="icon">👍</span>
            <span class="count">{{ board?.boardRecommendCount || 0 }}</span>
          </div>
        </div>
      </div>

      <!-- 댓글 영역 -->
      <div class="comments-wrapper">
        <div class="comments-header">
          <h2 class="comments-title">
            댓글 <span class="count">{{ replyCount }}</span>
          </h2>
        </div>

        <ReplyItem
          :boardNo="board.boardNo"
          :allowReply="board.responseDto.boardAllowReply"
          @update:replyCount="replyCount = $event"
        />
      </div>
    </div>

    <!-- 오류 상태 -->
    <div v-else class="error-state">
      <div class="error-icon">⚠️</div>
      <h2>게시글을 찾을 수 없습니다</h2>
      <p>삭제되었거나 존재하지 않는 게시글입니다.</p>
      <button @click="goToBoard" class="btn-back">목록으로 돌아가기</button>
    </div>

    <!-- 이미지 모달 -->
    <div v-if="showImageModal" @click="closeImageModal" class="image-modal">
      <button @click="closeImageModal" class="modal-close">×</button>
      <div class="modal-content" @click.stop>
        <img
          v-if="currentImageIndex >= 0"
          :src="`/v1/multifile/image/${attachedImages[currentImageIndex].fileNo}`"
          :alt="attachedImages[currentImageIndex].fileName"
          class="modal-image"
        />
      </div>
      <div class="modal-nav">
        <button
          @click.stop="prevImage"
          :disabled="currentImageIndex <= 0"
          class="nav-button prev"
        >
          ‹
        </button>
        <span class="page-indicator">
          {{ currentImageIndex + 1 }} / {{ attachedImages.length }}
        </span>
        <button
          @click.stop="nextImage"
          :disabled="currentImageIndex >= attachedImages.length - 1"
          class="nav-button next"
        >
          ›
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useUserStore } from "@/stores/user";
import axios from "axios";
import ReplyItem from "@/view/reply/ReplyItem.vue";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

// 게시글 관련 데이터
const board = ref(null);
const categoryInfo = ref({ title: "게시판" });
const attachedImages = ref([]);
const attachedFiles = ref([]);
const loading = ref(true);

const replyCount = ref(0);

// 이미지 모달 관련
const showImageModal = ref(false);
const currentImageIndex = ref(0);

// 게시글 추천 관련
const isRecommended = ref(false);
const recommendCount = ref(0);
const isRecommending = ref(false);

const isUserLoggedIn = computed(() => {
  return userStore.isLoggedIn && userStore.accessToken;
});

const isAuthor = computed(() => {
  if (!userStore.isLoggedIn || !board.value) return false
  return userStore.user?.userNo === board.value.userNo
})

// 추천 상태 확인
const checkRecommendStatus = async () => {
  if (!categoryInfo.value.allowRecommend) {
    isRecommended.value = false;
    recommendCount.value = 0;
    return;
  }

  if (!userStore.isLoggedIn) {
    isRecommended.value = false;
    recommendCount.value = board.value?.boardRecommendCount || 0;
    return;
  }

  try {
    const headers = userStore.accessToken
      ? { Authorization: `Bearer ${userStore.accessToken}` }
      : {};

    const response = await axios.get(`/v1/board/${boardNo.value}/recommend`, {
      headers,
    });
    recommendCount.value = response.data.count;
    isRecommended.value = response.data.recommended;
  } catch (error) {
    console.error("추천 상태 확인 실패:", error);
    isRecommended.value = false;
    recommendCount.value = board.value?.boardRecommendCount || 0;
  }
};

// 추천 토글
const toggleRecommend = async () => {
  // 디버깅 코드 추가
  console.log("=== 로그인 상태 확인 ===");
  console.log("userStore.isLoggedIn:", userStore.isLoggedIn);
  console.log("userStore.accessToken:", userStore.accessToken);
  console.log("userStore.user:", userStore.user);
  console.log("isUserLoggedIn:", isUserLoggedIn.value);

  if (!categoryInfo.value.allowRecommend) {
    alert("이 게시판은 추천 기능이 비활성화되어 있습니다.");
    return;
  }

  if (!userStore.isLoggedIn) {
    alert("로그인이 필요한 기능입니다.");
    return;
  }

  isRecommending.value = true;

  try {
    const headers = userStore.accessToken
      ? { Authorization: `Bearer ${userStore.accessToken}` }
      : {};

    if (isRecommended.value) {
      await axios.delete(`/v1/board/${boardNo.value}/recommend`, { headers });
      isRecommended.value = false;
      recommendCount.value = Math.max(recommendCount.value - 1, 0);
    } else {
      await axios.post(`/v1/board/${boardNo.value}/recommend`, {}, { headers });
      isRecommended.value = true;
      recommendCount.value++;
    }
  } catch (err) {
    console.error("추천 토글 실패:", err);
    alert("추천 처리 중 오류가 발생했습니다.");
  } finally {
    isRecommending.value = false;
  }
};

// boardNo를 숫자로 변환
const boardNo = computed(() => route.params.boardNo);

// loadBoardDetail 함수 수정
const loadBoardDetail = async () => {
  try {
    loading.value = true;
    const boardNoParam = route.params.boardNo;

    // 게시글 정보 조회
    const boardResponse = await axios.get(`/v1/board/${boardNoParam}`);
    console.log("전체 게시글 데이터:", boardResponse.data);
    console.log("responseDto 내용:", boardResponse.data.responseDto);
    board.value = boardResponse.data;
    console.log("📊 조회수:", board.value.boardViewCnt);

    // responseDto에서 카테고리 정보 직접 사용
    if (boardResponse.data.responseDto) {
      const categoryData = boardResponse.data.responseDto;

      console.log("boardAllowRecommend:", categoryData.boardAllowRecommend);
      console.log("boardCategoryNo:", categoryData.boardCategoryNo);

      categoryInfo.value = {
        title: categoryData.boardCategoryTitle || "게시판",
        allowRecommend: categoryData.boardAllowRecommend || false,
      };

      console.log("설정된 categoryInfo:", categoryInfo.value);
    }

    // 첨부파일 조회
    await loadBoardFiles(boardNoParam);

    // 추천 상태 확인 추가
    await checkRecommendStatus();
  } catch (error) {
    console.error("게시글 로드 실패:", error);
    board.value = null;
  } finally {
    loading.value = false;
  }
};

// 게시글 파일 로드
const loadBoardFiles = async (boardNo) => {
  try {
    const filesResponse = await axios.get(`/v1/multifile`, {
      params: {
        referenceTable: "BOARD",
        referenceNo: boardNo,
      },
    });

    let files = [];
    const responseData = filesResponse.data;
    if (Array.isArray(responseData)) {
      files = responseData;
    } else if (responseData && Array.isArray(responseData.data)) {
      files = responseData.data;
    } else if (responseData && Array.isArray(responseData.content)) {
      files = responseData.content;
    } else {
      files = [];
    }

    // 파일 분리
    attachedImages.value = files.filter(
      (file) =>
        file && file.fileMimeType && file.fileMimeType.startsWith("image/")
    );
    attachedFiles.value = files.filter(
      (file) =>
        file && file.fileMimeType && !file.fileMimeType.startsWith("image/")
    );
  } catch (fileError) {
    console.log("파일 조회 실패 (정상 - 파일 없음):", fileError);
    attachedImages.value = [];
    attachedFiles.value = [];
  }
};

// 파일 다운로드 함수 추가
const downloadFile = async (fileNo, fileName) => {
  try {
    const response = await axios.get(`/v1/multifile/download/${fileNo}`, {
      responseType: "blob",
    });

    const url = window.URL.createObjectURL(new Blob([response.data]));
    const link = document.createElement("a");
    link.href = url;
    link.setAttribute("download", fileName);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error("다운로드 실패:", error);
    alert("파일 다운로드에 실패했습니다.");
  }
};

// 날짜 포맷
const formatDate = (dateString) => {
  if (!dateString) return "";

  const date = new Date(dateString);
  const now = new Date();
  const diffMs = now - date;
  const diffMins = Math.floor(diffMs / 60000);
  const diffHours = Math.floor(diffMs / 3600000);
  const diffDays = Math.floor(diffMs / 86400000);

  if (diffMins < 1) return "방금 전";
  if (diffMins < 60) return `${diffMins}분 전`;
  if (diffHours < 24) return `${diffHours}시간 전`;
  if (diffDays < 7) return `${diffDays}일 전`;

  return date.toLocaleString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

// 파일 크기 포맷
const formatFileSize = (bytes) => {
  if (!bytes) return "0 Bytes";
  const k = 1024;
  const sizes = ["Bytes", "KB", "MB", "GB"];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i];
};

// 이미지 모달 관련
const openImageModal = (index) => {
  currentImageIndex.value = index;
  showImageModal.value = true;
};

const closeImageModal = () => {
  showImageModal.value = false;
};

const prevImage = () => {
  if (currentImageIndex.value > 0) {
    currentImageIndex.value--;
  }
};

const nextImage = () => {
  if (currentImageIndex.value < attachedImages.value.length - 1) {
    currentImageIndex.value++;
  }
};

// 네비게이션
const goBack = () => {
  router.go(-1);
};

const goToBoard = () => {
  if (board.value && board.value.boardCategoryNo) {
    router.push(`/board/${board.value.boardCategoryNo}`);
  } else {
    console.error("boardCategoryNo가 없습니다:", board.value);
    router.go(-1); // 이전 페이지로
  }
};

// 게시글 수정
const editPost = () => {
  const boardNoParam = route.params.boardNo;
  router.push(`/board/${boardNoParam}/edit`);
};

// 게시글 삭제
const deletePost = async () => {
  if (!confirm("정말로 이 게시글을 삭제하시겠습니까?")) {
    return;
  }

  try {
    const boardNoParam = route.params.boardNo;
    await axios.delete(`/v1/board/${boardNoParam}`);

    alert("게시글이 삭제되었습니다.");
    goToBoard();
  } catch (error) {
    console.error("게시글 삭제 실패:", error);
    alert("게시글 삭제에 실패했습니다.");
  }
};

// 초기 로드 시 추천 상태 확인
onMounted(() => {
  loadBoardDetail().then(() => {
    if (categoryInfo.value.allowRecommend && userStore.isLoggedIn) {
      checkRecommendStatus();
    } else if (board.value) {
      recommendCount.value = board.value.boardRecommendCount || 0;
    }
  });
});
</script>

<style scoped>
/* 기본 레이아웃 */
.board-detail {
  min-height: 100vh;
  background: #f5f6f8;
  padding: 0;
}

/* 로딩 */
.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
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

.loading p {
  color: #666;
  font-size: 14px;
}

/* 컨테이너 */
.detail-container {
  max-width: 860px;
  margin: 0 auto;
  padding: 20px;
}

/* 브레드크럼 네비게이션 */
.breadcrumb-nav {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 16px 0;
  font-size: 13px;
  color: #666;
}

.nav-item {
  color: #4a90e2;
  cursor: pointer;
  transition: color 0.2s;
}

.nav-item:hover {
  color: #357abd;
  text-decoration: underline;
}

.nav-divider {
  color: #ccc;
  font-size: 12px;
}

.nav-current {
  color: #333;
  font-weight: 500;
}

/* 메인 컨텐츠 래퍼 */
.content-wrapper {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  margin-bottom: 16px;
}

/* 게시글 헤더 */
.post-header {
  padding: 32px 32px 24px;
  border-bottom: 1px solid #f0f0f0;
}

.post-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a1a;
  line-height: 1.4;
  margin: 0 0 20px 0;
  word-break: keep-all;
}

.post-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.info-left {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #666;
}

.author {
  font-weight: 600;
  color: #333;
}

.divider {
  color: #ddd;
}

.date, .views {
  color: #999;
}

.author-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  padding: 6px 12px;
  font-size: 13px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  background: white;
  color: #666;
  cursor: pointer;
  transition: all 0.2s;
}

.action-btn:hover {
  background: #f8f9fa;
  border-color: #ccc;
}

.action-btn.delete:hover {
  background: #fff5f5;
  border-color: #ff6b6b;
  color: #ff6b6b;
}

/* 게시글 본문 */
.post-body {
  padding: 32px;
}

.post-content {
  font-size: 15px;
  line-height: 1.7;
  color: #333;
  white-space: pre-wrap;
  word-break: break-word;
  min-height: 100px;
}

/* 첨부파일 공통 */
.attachments {
  margin-top: 32px;
  padding-top: 32px;
  border-top: 1px solid #f0f0f0;
}

/* 이미지 첨부 */
.image-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 0 5%;  /* 양쪽에 5%씩 */
}

.image-wrapper {
  position: relative;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  background: #f8f9fa;
  aspect-ratio: 4/3;
}

.image-wrapper:hover {
  opacity: 0.9;
}

.attached-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* 파일 첨부 */
.file-header {
  font-size: 13px;
  font-weight: 600;
  color: #666;
  margin-bottom: 12px;
}

.file-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 6px;
  margin-bottom: 8px;
}

.file-details {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.file-icon {
  font-size: 16px;
}

.file-name {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
}

.file-download {
  padding: 6px 14px;
  font-size: 13px;
  background: #4a90e2;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background 0.2s;
  white-space: nowrap;
}

.file-download:hover {
  background: #357abd;
}

/* 게시글 하단 */
.post-footer {
  padding: 24px 32px;
  border-top: 1px solid #f0f0f0;
  display: flex;
  justify-content: center;
}

/* 추천 영역 */
.recommend-area {
  display: flex;
  justify-content: center;
}

.recommend-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  font-size: 14px;
  font-weight: 600;
  background: white;
  border: 2px solid #e0e0e0;
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.2s;
  color: #666;
}

.recommend-button:hover:not(:disabled) {
  border-color: #4a90e2;
  color: #4a90e2;
  transform: translateY(-1px);
}

.recommend-button.recommended {
  background: #4a90e2;
  border-color: #4a90e2;
  color: white;
}

.recommend-button.recommended:hover {
  background: #357abd;
  border-color: #357abd;
}

.recommend-button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  transform: none;
}

.recommend-button .icon {
  font-size: 16px;
}

.recommend-button .count {
  font-weight: 700;
}

.recommend-disabled {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #999;
  font-size: 14px;
}

.recommend-disabled .icon {
  font-size: 16px;
}

.recommend-disabled .count {
  font-weight: 600;
}

/* 댓글 영역 */
.comments-wrapper {
  background: white;
  border-radius: 8px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.08);
  padding: 24px 32px;
}

.comments-header {
  margin-bottom: 24px;
}

.comments-title {
  font-size: 17px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
}

.comments-title .count {
  color: #4a90e2;
  margin-left: 4px;
}

/* 에러 상태 */
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 60vh;
  text-align: center;
  gap: 16px;
}

.error-icon {
  font-size: 48px;
}

.error-state h2 {
  font-size: 20px;
  color: #333;
  margin: 0;
}

.error-state p {
  color: #666;
  font-size: 14px;
  margin: 0;
}

.btn-back {
  margin-top: 16px;
  padding: 10px 24px;
  background: #4a90e2;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-back:hover {
  background: #357abd;
}

/* 이미지 모달 */
.image-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.95);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.modal-close {
  position: absolute;
  top: 20px;
  right: 20px;
  width: 44px;
  height: 44px;
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: white;
  font-size: 32px;
  cursor: pointer;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s;
  z-index: 10001;
}

.modal-close:hover {
  background: rgba(255, 255, 255, 0.2);
}

.modal-content {
  max-width: 90vw;
  max-height: 85vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.modal-image {
  max-width: 100%;
  max-height: 85vh;
  object-fit: contain;
  border-radius: 4px;
}

.modal-nav {
  position: absolute;
  bottom: 40px;
  display: flex;
  align-items: center;
  gap: 24px;
  background: rgba(0, 0, 0, 0.7);
  padding: 12px 24px;
  border-radius: 24px;
}

.nav-button {
  width: 36px;
  height: 36px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: white;
  font-size: 24px;
  cursor: pointer;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.nav-button:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.2);
}

.nav-button:disabled {
  opacity: 0.3;
  cursor: not-allowed;
}

.page-indicator {
  color: white;
  font-size: 14px;
  font-weight: 500;
  min-width: 60px;
  text-align: center;
}

/* 반응형 */
@media (max-width: 768px) {
  .detail-container {
    padding: 12px;
  }

  .content-wrapper,
  .comments-wrapper {
    border-radius: 0;
  }

  .post-header {
    padding: 24px 20px 20px;
  }

  .post-title {
    font-size: 20px;
    margin-bottom: 16px;
  }

  .post-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .info-left {
    flex-wrap: wrap;
  }

  .author-actions {
    width: 100%;
    justify-content: flex-end;
  }

  .post-body {
    padding: 24px 20px;
  }

  .post-content {
    font-size: 14px;
  }

  .image-grid {
    grid-template-columns: 1fr;
  }

  .file-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .file-download {
    width: 100%;
  }

  .post-footer {
    padding: 20px;
  }

  .comments-wrapper {
    padding: 20px;
  }

  .modal-nav {
    bottom: 20px;
    padding: 8px 16px;
  }

  .nav-button {
    width: 32px;
    height: 32px;
    font-size: 20px;
  }
}
</style>