<template>
  <div class="container-fluid" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); min-height: 100vh; padding: 0;">
    <!-- 상단 헤더 -->
    <div class="text-center py-5 text-white">
      <h1 class="display-4 fw-bold">🐕 AI 일기장 🐾</h1>
      <p class="lead">오늘 반려동물이 무엇을 했는지 알려주세요!</p>
    </div>

    <!-- 메인 컨테이너 -->
    <div class="container" style="max-width: 1200px;">
      <!-- 일기 생성 폼 (결과가 없을 때만 표시) -->
      <div v-if="!showResult" class="row justify-content-center mb-5">
        <div class="col-lg-8">
          <div class="card shadow-lg border-0" style="border-radius: 20px;">
            <div class="card-body p-5">
              <!-- 반려동물 선택 -->
              <div class="mb-4">
                <label class="form-label fw-bold fs-5">반려동물 선택</label>
                <select
                  v-model="selectedPetId"
                  @change="onPetSelect"
                  :disabled="loading"
                  class="form-select form-select-lg"
                  style="border-radius: 15px;"
                >
                  <option value="">반려동물을 선택해주세요</option>
                  <option v-for="pet in petList" :key="pet.petId" :value="pet.petId">
                    {{ pet.petName }} ({{ pet.petSpecies }})
                  </option>
                </select>
              </div>

              <!-- 프롬프트 입력 -->
              <div class="mb-4">
                <label class="form-label fw-bold fs-5">
                  오늘의 이야기를 들려주세요
                  <span v-if="selectedPetName" class="text-primary">({{ selectedPetName }})</span>
                </label>
                <textarea
                  v-model="prompt"
                  :disabled="loading"
                  class="form-control form-control-lg"
                  rows="8"
                  :placeholder="selectedPetName ? `예: 오늘 ${selectedPetName}는 아침에 산책을 하다가 다른 강아지를 만났어요. 꼬리를 흔들며 인사를 했고, 공원에서 뛰어다니며 즐거워했어요...` : '먼저 반려동물을 선택해주세요'"
                  style="border-radius: 15px; resize: none; font-size: 16px; line-height: 1.6;"
                ></textarea>
                <div class="form-text mt-2">
                  구체적으로 적을수록 더 생생한 일기가 만들어져요!
                </div>
              </div>

              <!-- 생성 버튼 -->
              <button
                @click="createDiary"
                :disabled="loading || !selectedPetId || !prompt.trim()"
                class="btn btn-lg w-100 py-3 fw-bold"
                style="background: linear-gradient(45deg, #667eea, #764ba2); border: none; border-radius: 15px; color: white; font-size: 18px;"
              >
                <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                <span v-if="loading">{{ getSelectedPetName() }}의 일기 생성 중...</span>
                <span v-else>✨ {{ getSelectedPetName() }}의 일기 생성하기</span>
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 생성된 일기 결과 -->
      <div v-if="showResult" id="diary-result" class="row">
        <!-- 왼쪽: 일기 내용 -->
        <div class="col-lg-7 mb-4">
          <!-- 반려동물 정보 헤더 -->
          <div class="card shadow border-0 mb-4" style="border-radius: 20px; background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <div class="card-body p-4 text-white">
              <div class="d-flex align-items-center">
                <div class="me-3" style="font-size: 3rem;">🐕</div>
                <div>
                  <h3 class="mb-1 fw-bold">{{ getSelectedPetName() }}의 일기</h3>
                  <p class="mb-0 opacity-75">{{ getCurrentDate() }}</p>
                </div>
              </div>
            </div>
          </div>

          <!-- 입력한 프롬프트 -->
          <div class="card shadow border-0 mb-4" style="border-radius: 20px;">
            <div class="card-header border-0 py-3" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%); border-radius: 20px 20px 0 0;">
              <h6 class="mb-0 text-white fw-bold">📝 입력한 내용</h6>
            </div>
            <div class="card-body p-4">
              <p class="mb-0" style="line-height: 1.6; color: #666;">{{ originalPrompt }}</p>
            </div>
          </div>

          <!-- 생성된 일기 내용 -->
          <div class="card shadow border-0" style="border-radius: 20px;">
            <div class="card-header border-0 py-3 d-flex justify-content-between align-items-center" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%); border-radius: 20px 20px 0 0;">
              <h4 class="mb-0 text-white fw-bold">📓 {{ getSelectedPetName() }}의 일기</h4>
              <small class="text-white opacity-75">AI가 작성함</small>
            </div>
            <div class="card-body p-4">
              <div class="diary-content" style="white-space: pre-wrap; line-height: 1.8; font-size: 16px; color: #333;">
                {{ generated.diaryContent }}
              </div>
            </div>
          </div>
        </div>

        <!-- 오른쪽: 생성된 이미지와 액션들 -->
        <div class="col-lg-5">
          <!-- 생성된 이미지 영역 -->
          <div class="card shadow border-0 mb-4" style="border-radius: 20px;">
            <div class="card-header border-0 py-3" style="background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%); border-radius: 20px 20px 0 0;">
              <h5 class="mb-0 fw-bold" style="color: #8b4513;">🎨 생성된 이미지</h5>
            </div>
            <div class="card-body p-4">
              <div class="text-center" style="min-height: 300px; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%); border-radius: 15px;">
                <div v-if="!imageUrl">
                  <div style="font-size: 4rem; margin-bottom: 1rem;">🐕💕</div>
                  <p class="text-muted fw-bold">AI 이미지 생성 기능 준비 중입니다</p>
                </div>
                <img v-else :src="imageUrl" alt="생성된 일기 이미지" 
                     style="max-height: 280px; max-width: 100%; border-radius: 15px; box-shadow: 0 8px 25px rgba(0,0,0,0.15);" 
                     @error="imageUrl = null" />
              </div>
            </div>
          </div>

          <!-- 만족도 조사 -->
          <div class="card shadow border-0 mb-4" style="border-radius: 20px;">
            <div class="card-body p-4 text-center">
              <h6 class="mb-3 fw-bold">생성된 일기가 마음에 드시나요?</h6>
              <div class="d-grid gap-2">
                <button type="button" class="btn btn-outline-success btn-lg" style="border-radius: 15px;" @click="rateDiary('good')">
                  👍 좋아요!
                </button>
                <button type="button" class="btn btn-outline-warning btn-lg" style="border-radius: 15px;" @click="rateDiary('normal')">
                  😊 괜찮아요
                </button>
                <button type="button" class="btn btn-outline-secondary btn-lg" style="border-radius: 15px;" @click="rateDiary('bad')">
                  🤔 다시 만들어주세요
                </button>
              </div>
            </div>
          </div>

          <!-- 액션 버튼들 -->
          <div class="card shadow border-0" style="border-radius: 20px;">
            <div class="card-body p-4">
              <div class="d-grid gap-2">
                <button @click="regenerateDiary" class="btn btn-warning btn-lg" :disabled="loading" style="border-radius: 15px;">
                  <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
                  🔄 다시 생성하기
                </button>
                <button @click="writeNewDiary" class="btn btn-primary btn-lg" style="border-radius: 15px;">
                  ✏️ 새로운 일기 작성
                </button>
                <button @click="goToDiaryList" class="btn btn-success btn-lg" style="border-radius: 15px;">
                  📚 일기 목록 보기
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 하단 네비게이션 -->
      <div class="text-center py-4">
        <button @click="goToPetList" class="btn btn-link text-white-50 fs-5">
          🏠 반려동물 목록으로 돌아가기
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import axios from "axios";
import { useRouter } from "vue-router";

const router = useRouter();

// 🔥 기본 데이터들
const petList = ref([]);
const selectedPetId = ref("");
const prompt = ref("");
const loading = ref(false);
const generated = ref({
  petId: null,
  prompt: "",
  diaryContent: "",
});

// 🚀 UI 관련 데이터들
const showResult = ref(false);
const originalPrompt = ref("");
const selectedPetName = ref("");

// 🚀 이미지 URL (imageUrl로 통일)
const imageUrl = ref(null);

// 1. 사용자 펫 목록 가져오기
const fetchUserPets = () => {
  axios
    .get("/v1/pet/list")
    .then((res) => {
      petList.value = res.data.content || res.data;
      console.log("펫 목록 로드 성공:", petList.value);
    })
    .catch((err) => {
      console.error("펫 목록 로드 실패:", err);
      alert("반려동물 목록을 불러오는 데 실패했습니다.");
    });
};

// 🔥 반려동물 선택 시 이름 저장
const onPetSelect = () => {
  const selectedPet = petList.value.find(
    (pet) => pet.petId == selectedPetId.value
  );
  selectedPetName.value = selectedPet ? selectedPet.petName : "";
  console.log("선택된 반려동물:", selectedPetName.value);
};

// 2. 일기 생성 및 저장 요청
const createDiary = () => {
  if (!selectedPetId.value || !prompt.value.trim()) {
    alert("반려동물과 프롬프트를 모두 입력해주세요.");
    return;
  }

  loading.value = true;
  showResult.value = false;
  originalPrompt.value = prompt.value.trim(); // 원본 프롬프트 저장

  console.log("일기 생성 요청:", {
    petId: selectedPetId.value,
    prompt: prompt.value.trim(),
  });

  axios
    .post("/v1/diary/create", {
      petId: selectedPetId.value,
      prompt: prompt.value.trim(),
    })
    .then((res) => {
      console.log("일기 생성 성공:", res.data);
      console.log('=== 일기 생성 API 응답 전체 ===');
      console.log(res);
      console.log('=== 응답 데이터 ===');
      console.log(res.data);
      console.log('=== 이미지 URL 확인 ===');
      console.log('imageUrl:', res.data.imageUrl);
      console.log('imageUrl 타입:', typeof res.data.imageUrl);
      console.log('imageUrl 존재 여부:', !!res.data.imageUrl);

      // 🔥 기존 generated 업데이트 (기존 로직 유지)
      generated.value = {
        petId: selectedPetId.value,
        prompt: prompt.value.trim(),
        diaryContent:
          res.data.diaryContent || res.data.diary || res.data.message,
      };

      // 🚀 이미지 URL 저장
      imageUrl.value = res.data.imageUrl;
      console.log('Vue 상태 업데이트 후 imageUrl:', imageUrl.value);

      // 🚀 결과 화면 표시
      showResult.value = true;

      // 결과 영역으로 스크롤
      setTimeout(() => {
        const resultElement = document.getElementById("diary-result");
        if (resultElement) {
          resultElement.scrollIntoView({ behavior: "smooth" });
        }
      }, 100);
    })
    .catch((err) => {
      console.error("일기 생성 실패:", err);
      console.error("에러 상세:", err.response?.data);

      let errorMessage = "일기 생성에 실패했습니다.";
      if (err.response?.status === 401) {
        errorMessage = "로그인이 필요합니다.";
        router.push("/login");
        return;
      } else if (err.response?.status === 400) {
        errorMessage = "잘못된 요청입니다. 입력을 확인해주세요.";
      }

      alert(errorMessage);
    })
    .finally(() => {
      loading.value = false;
    });
};

// 🚀 같은 프롬프트로 다시 생성
const regenerateDiary = () => {
  prompt.value = originalPrompt.value;
  createDiary();
};

// 🚀 새로운 일기 작성
const writeNewDiary = () => {
  prompt.value = "";
  originalPrompt.value = "";
  generated.value = {
    petId: null,
    prompt: "",
    diaryContent: "",
  };
  imageUrl.value = null; // 🚀 이미지 URL 초기화
  showResult.value = false;

  // 폼 영역으로 스크롤
  window.scrollTo({ top: 0, behavior: "smooth" });
};

// 🚀 만족도 평가
const rateDiary = (rating) => {
  console.log("사용자 평가:", rating);

  if (rating === "bad") {
    if (confirm("같은 내용으로 일기를 다시 생성하시겠습니까?")) {
      regenerateDiary();
    }
  } else {
    alert("피드백 감사합니다! 더 좋은 일기를 만들어드릴게요 😊");
  }
};

// 🚀 일기 목록으로 이동
const goToDiaryList = () => {
  if (selectedPetId.value) {
    router.push(`/diary/${selectedPetId.value}`);
  } else {
    alert("반려동물을 먼저 선택해주세요.");
  }
};

// 🚀 반려동물 목록으로 이동
const goToPetList = () => {
  router.push("/userPet");
};

// 🚀 현재 날짜 반환
const getCurrentDate = () => {
  return new Date().toLocaleDateString("ko-KR", {
    year: "numeric",
    month: "long",
    day: "numeric",
    weekday: "long",
  });
};

// 🚀 선택된 반려동물 이름 가져오기
const getSelectedPetName = () => {
  if (!selectedPetId.value) return "반려동물";
  const pet = petList.value.find((p) => p.petId == selectedPetId.value);
  return pet ? pet.petName : "반려동물";
};

onMounted(() => {
  fetchUserPets();
});
</script>