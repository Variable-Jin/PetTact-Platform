<template>
  <div class="container mt-4" style="max-width: 800px;">
    <!-- 헤더 -->
    <div class="text-center mb-4">
      <h2 class="text-success">🐕 AI 일기장 🐾</h2>
      <p class="text-muted">오늘 반려동물이 무엇을 했는지 알려주세요!</p>
    </div>

    <!-- 일기 생성 폼 (결과가 없을 때만 표시) -->
    <div v-if="!showResult" class="card mb-4">
      <div class="card-body">
        <!-- 반려동물 선택 -->
        <div class="mb-3">
          <label class="form-label fw-bold">반려동물 선택</label>
          <select 
            v-model="selectedPetId" 
            @change="onPetSelect"
            :disabled="loading"
            class="form-select"
          >
            <option value="">반려동물을 선택해주세요</option>
            <option 
              v-for="pet in petList" 
              :key="pet.petId" 
              :value="pet.petId"
            >
              {{ pet.petName }} ({{ pet.petSpecies }})
            </option>
          </select>
        </div>

        <!-- 프롬프트 입력 -->
        <div class="mb-3">
          <label class="form-label fw-bold">
            오늘의 이야기를 들려주세요
            <span v-if="selectedPetName" class="text-success">({{ selectedPetName }})</span>
          </label>
          <textarea
            v-model="prompt"
            :disabled="loading"
            class="form-control"
            rows="5"
            :placeholder="selectedPetName ? 
              `예: 오늘 ${selectedPetName}는 아침에 산책을 하다가 다른 강아지를 만났어요. 꼬리를 흔들며 인사를 했고, 공원에서 뛰어다니며 즐거워했어요...` : 
              '먼저 반려동물을 선택해주세요'"
            style="resize: none;"
          ></textarea>
          <div class="form-text">구체적으로 적을수록 더 생생한 일기가 만들어져요!</div>
        </div>

        <!-- 생성 버튼 -->
        <button
          @click="createDiary"
          :disabled="loading || !selectedPetId || !prompt.trim()"
          class="btn btn-success btn-lg w-100"
        >
          <span v-if="loading" class="spinner-border spinner-border-sm me-2" role="status"></span>
          <span v-if="loading">{{ getSelectedPetName() }}의 일기 생성 중...</span>
          <span v-else>✨ {{ getSelectedPetName() }}의 일기 생성하기</span>
        </button>
      </div>
    </div>

    <!-- 생성된 일기 결과 -->
    <div v-if="showResult" id="diary-result">
      <!-- 선택한 반려동물 정보 -->
      <div class="card mb-3">
        <div class="card-body bg-light">
          <div class="d-flex align-items-center">
            <div class="me-3" style="font-size: 2rem;">🐕</div>
            <div>
              <h5 class="mb-1">{{ getSelectedPetName() }}의 일기</h5>
              <small class="text-muted">{{ getCurrentDate() }}</small>
            </div>
          </div>
        </div>
      </div>

      <!-- 입력한 프롬프트 표시 -->
      <div class="card mb-3">
        <div class="card-header bg-info text-white">
          <h6 class="mb-0">📝 입력한 내용</h6>
        </div>
        <div class="card-body">
          <p class="mb-0 text-muted">{{ originalPrompt }}</p>
        </div>
      </div>

      <!-- 생성된 일기 내용 -->
      <div class="card mb-4">
        <div class="card-header bg-success text-white d-flex justify-content-between align-items-center">
          <h4 class="mb-0">📓 {{ getSelectedPetName() }}의 일기</h4>
          <small>AI가 작성함</small>
        </div>
        <div class="card-body">
          <div class="diary-content" style="white-space: pre-wrap; line-height: 1.8; font-size: 1.1rem;">
            {{ generated.diaryContent }}
          </div>
        </div>
      </div>

      <!-- 생성된 이미지 영역 (향후 구현) -->
      <div class="card mb-4">
        <div class="card-header bg-warning text-dark">
          <h5 class="mb-0">🎨 일기 속 한 장면</h5>
        </div>
        <div class="card-body text-center">
          <div 
            class="bg-gradient rounded p-4"
            style="height: 250px; display: flex; align-items: center; justify-content: center; background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);"
          >
            <div>
              <div style="font-size: 3rem; margin-bottom: 1rem;">🐕💕</div>
              <p class="text-muted">AI 이미지 생성 기능 준비 중입니다</p>
            </div>
          </div>
        </div>
      </div>

      <!-- 만족도 조사 -->
      <div class="card mb-4">
        <div class="card-body text-center">
          <p class="mb-3">생성된 일기가 마음에 드시나요?</p>
          <div class="btn-group" role="group">
            <button type="button" class="btn btn-outline-success" @click="rateDiary('good')">
              👍 좋아요!
            </button>
            <button type="button" class="btn btn-outline-warning" @click="rateDiary('normal')">
              😊 괜찮아요
            </button>
            <button type="button" class="btn btn-outline-secondary" @click="rateDiary('bad')">
              🤔 다시 만들어주세요
            </button>
          </div>
        </div>
      </div>

      <!-- 액션 버튼들 -->
      <div class="row mb-4">
        <div class="col-md-4 mb-2">
          <button @click="regenerateDiary" class="btn btn-warning w-100" :disabled="loading">
            <span v-if="loading" class="spinner-border spinner-border-sm me-2"></span>
            🔄 다시 생성하기
          </button>
        </div>
        <div class="col-md-4 mb-2">
          <button @click="writeNewDiary" class="btn btn-primary w-100">
            ✏️ 새로운 일기 작성
          </button>
        </div>
        <div class="col-md-4 mb-2">
          <button @click="goToDiaryList" class="btn btn-success w-100">
            📚 일기 목록 보기
          </button>
        </div>
      </div>
    </div>

    <!-- 하단 네비게이션 -->
    <div class="text-center">
      <button @click="goToPetList" class="btn btn-link text-muted">
        🏠 반려동물 목록으로 돌아가기
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import { useRouter } from 'vue-router';

const router = useRouter();

// 🔥 기존 필수 데이터들
const petList = ref([]);
const selectedPetId = ref('');
const prompt = ref('');
const loading = ref(false);
const generated = ref({
  petId: null,
  prompt: '',
  diaryContent: ''
});

// 🚀 추가된 UI 관련 데이터들
const showResult = ref(false);
const originalPrompt = ref('');
const selectedPetName = ref('');

// 1. 사용자 펫 목록 가져오기 (기존 코드 유지)
const fetchUserPets = () => {
  axios.get('/v1/pet/list')
    .then(res => {
      petList.value = res.data.content || res.data;
      console.log('펫 목록 로드 성공:', petList.value);
    })
    .catch(err => {
      console.error('펫 목록 로드 실패:', err);
      alert('반려동물 목록을 불러오는 데 실패했습니다.');
    });
};

// 🔥 반려동물 선택 시 이름 저장
const onPetSelect = () => {
  const selectedPet = petList.value.find(pet => pet.petId == selectedPetId.value);
  selectedPetName.value = selectedPet ? selectedPet.petName : '';
  console.log('선택된 반려동물:', selectedPetName.value);
};

// 2. 일기 생성 및 저장 요청 (기존 + 추가 기능)
const createDiary = () => {
  if (!selectedPetId.value || !prompt.value.trim()) {
    alert('반려동물과 프롬프트를 모두 입력해주세요.');
    return;
  }

  loading.value = true;
  showResult.value = false;
  originalPrompt.value = prompt.value.trim(); // 원본 프롬프트 저장

  console.log('일기 생성 요청:', {
    petId: selectedPetId.value,
    prompt: prompt.value.trim()
  });

  axios.post('/v1/diary/create', {
    petId: selectedPetId.value,
    prompt: prompt.value.trim()
  })
  .then(res => {
    console.log('일기 생성 성공:', res.data);
    
    // 🔥 기존 generated 업데이트 (기존 로직 유지)
    generated.value = {
      petId: selectedPetId.value,
      prompt: prompt.value.trim(),
      diaryContent: res.data.diaryContent || res.data.message
    };

    // 🚀 결과 화면 표시
    showResult.value = true;
    
    // 성공 메시지는 일단 제거 (결과 화면으로 대체)
    // alert('일기가 성공적으로 작성되었습니다.');
    
    // 결과 영역으로 스크롤
    setTimeout(() => {
      const resultElement = document.getElementById('diary-result');
      if (resultElement) {
        resultElement.scrollIntoView({ behavior: 'smooth' });
      }
    }, 100);
  })
  .catch(err => {
    console.error('일기 생성 실패:', err);
    console.error('에러 상세:', err.response?.data);
    
    let errorMessage = '일기 생성에 실패했습니다.';
    if (err.response?.status === 401) {
      errorMessage = '로그인이 필요합니다.';
      router.push('/login');
      return;
    } else if (err.response?.status === 400) {
      errorMessage = '잘못된 요청입니다. 입력을 확인해주세요.';
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
  prompt.value = '';
  originalPrompt.value = '';
  generated.value = {
    petId: null,
    prompt: '',
    diaryContent: ''
  };
  showResult.value = false;
  
  // 폼 영역으로 스크롤
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

// 🚀 만족도 평가
const rateDiary = (rating) => {
  console.log('사용자 평가:', rating);
  
  if (rating === 'bad') {
    if (confirm('같은 내용으로 일기를 다시 생성하시겠습니까?')) {
      regenerateDiary();
    }
  } else {
    alert('피드백 감사합니다! 더 좋은 일기를 만들어드릴게요 😊');
  }
};

// 🚀 일기 목록으로 이동
const goToDiaryList = () => {
  if (selectedPetId.value) {
    router.push(`/diary/${selectedPetId.value}`);
  } else {
    alert('반려동물을 먼저 선택해주세요.');
  }
};

// 🚀 반려동물 목록으로 이동
const goToPetList = () => {
  router.push('/userPet');
};

// 🚀 현재 날짜 반환
const getCurrentDate = () => {
  return new Date().toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  });
};

// 🚀 선택된 반려동물 이름 가져오기
const getSelectedPetName = () => {
  if (!selectedPetId.value) return '반려동물';
  const pet = petList.value.find(p => p.petId == selectedPetId.value);
  return pet ? pet.petName : '반려동물';
};

onMounted(() => {
  fetchUserPets();
});
</script>
