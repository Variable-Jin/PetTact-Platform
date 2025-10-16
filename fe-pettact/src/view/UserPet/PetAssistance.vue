<template>
  <div class="pet-chat-page">
    <!-- Chat Container -->
    <div class="pet-chat-container">
      <!-- 헤더 -->
      <div class="chat-header">
        <div class="header-content">
          <h1 class="chat-title">🐕 반려 어시스턴스🐱</h1>
          <p class="chat-subtitle">우리 아이의 눈으로 바라본 하루를 대화로 남겨보세요.</p>
        </div>
      </div>

      <!-- 채팅 영역 -->
      <div class="chat-area" ref="chatArea">
        <!-- 웰컴 메시지 -->
        <div class="welcome-message">
          <div class="welcome-content">
            <span class="welcome-greeting">안녕하세요! 소중한 보호자님!</span>
            <p class="welcome-text">
              저는 보호자님과 우리 아이의 따뜻한 일상을 함께 기록하고,
              필요한 순간 가장 유용할 정보와 도움을 줄 보호자님의 동반자에요.
            </p>
            <span class="welcome-question">오늘 우리 아이와 어떤 하루를 보내셨나요?</span>
          </div>
        </div>

        <!-- 채팅 메시지 목록 -->
        <div v-for="message in chatMessages" :key="message.id" 
             :class="['message-group', message.isBot ? 'bot-message' : 'user-message']">
          <div class="message-avatar" v-if="message.isBot">
            <div class="avatar-icon">🐾</div>
          </div>
          <div :class="['message-bubble', message.isBot ? 'bot-bubble' : 'user-bubble']">
            <p class="message-text">{{ message.text }}</p>
          </div>
          <div class="message-avatar" v-if="!message.isBot">
            <div class="avatar-icon">🐾</div>
          </div>
        </div>

        <!-- 타이핑 인디케이터 -->
        <div v-if="isTyping" class="message-group bot-message">
          <div class="message-avatar">
            <div class="avatar-icon">🐾</div>
          </div>
          <div class="message-bubble bot-bubble">
            <div class="typing-indicator">
              <div class="typing-dots">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 입력 영역 -->
      <div class="input-area">
        <div class="input-container">
          <input 
            v-model="currentMessage" 
            @keyup.enter="sendMessage"
            type="text" 
            placeholder="오늘은 무슨 일이 있으셨나요?"
            class="message-input"
          />
          <button @click="sendMessage" class="send-button">
            <div class="send-icon">➤</div>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

// Router 사용
const router = useRouter()

// Reactive 상태들
const currentMessage = ref('')
const isTyping = ref(false)
const chatMessages = ref([])
const chatArea = ref(null)

// 사용자 정보
const currentUserNo = ref(1) // 실제 로그인된 사용자 번호로 변경 필요
const currentSessionNo = ref(Date.now()) // 세션 번호

// 스크롤을 맨 아래로
const scrollToBottom = () => {
  if (chatArea.value) {
    chatArea.value.scrollTop = chatArea.value.scrollHeight
  }
}

// 메시지 전송 (스프링 백엔드로)
const sendMessage = async () => {
  if (currentMessage.value.trim() === '') return
  
  console.log('=== 채팅 요청 시작 ===');
  
  const userMessage = {
    id: Date.now(),
    text: currentMessage.value,
    isBot: false,
    timestamp: new Date()
  }
  
  chatMessages.value.push(userMessage)
  
  const messageText = currentMessage.value
  currentMessage.value = ''
  isTyping.value = true
  
  try {
    // ✅ 요청 데이터 구성 및 로깅
    const requestData = {
      message: messageText,                    // String message
      userNo: currentUserNo.value,            // Long userNo
      sessionNo: currentSessionNo.value,      // Long sessionNo
      petType: null,                          // String petType (선택사항)
      petId: null                             // Long petId (선택사항)
    };
    
    console.log('전송할 요청 데이터:', requestData);
    console.log('요청 URL:', '/v1/diary/chat/send');
    console.log('토큰 확인:', localStorage.getItem('token') || sessionStorage.getItem('token'));
    
    // ✅ 요청 전송
    console.log('서버로 요청 전송 중...');
    const response = await axios.post('/v1/diary/chat/send', requestData);
    
    console.log('✅ 서버 응답 성공!');
    console.log('응답 상태:', response.status);
    console.log('응답 헤더:', response.headers);
    console.log('응답 데이터:', response.data);
    
    const botResponse = {
      id: Date.now() + 1,
      text: response.data.response,
      isBot: true,
      timestamp: new Date()
    }
    
    isTyping.value = false
    chatMessages.value.push(botResponse)
    
    await nextTick()
    scrollToBottom()
    
    console.log('채팅 처리 완료');
    
  } catch (error) {
    console.error('=== 채팅 요청 실패 ===');

    alert('백엔드 응답: ' + JSON.stringify(error.response?.data));
    console.error('에러 객체:', error);
    console.error('에러 메시지:', error.message);
    
    if (error.response) {
      // 서버가 응답했지만 에러 상태 코드
      console.error('응답 상태 코드:', error.response.status);
      console.error('응답 헤더:', error.response.headers);
      console.error('응답 데이터:', error.response.data);
      console.error('응답 상태 텍스트:', error.response.statusText);
    } else if (error.request) {
      // 요청이 만들어졌지만 응답이 없음
      console.error('요청이 전송되었지만 응답 없음:', error.request);
    } else {
      // 요청 설정 중 에러
      console.error('요청 설정 에러:', error.message);
    }
    
    console.error('에러 설정:', error.config);
    
    isTyping.value = false
    
    let errorText = '죄송해요. 일시적인 오류가 발생했습니다.';
    
    // ✅ 상태 코드별 에러 메시지
    if (error.response) {
      switch (error.response.status) {
        case 401:
          errorText = '로그인이 필요합니다. 다시 로그인해주세요.';
          setTimeout(() => router.push('/login'), 2000);
          break;
        case 404:
          errorText = 'API 경로를 찾을 수 없습니다.';
          break;
        case 500:
          errorText = '서버에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요.';
          break;
        default:
          errorText = `서버 오류 (${error.response.status}): ${error.response.statusText}`;
      }
    }
    
    const errorMessage = {
      id: Date.now() + 1,
      text: errorText,
      isBot: true,
      timestamp: new Date()
    }
    chatMessages.value.push(errorMessage)
    
    await nextTick()
    scrollToBottom()
  }
}

// 컴포넌트 마운트 시
onMounted(async () => {
  console.log('채팅 컴포넌트 마운트됨');
  console.log('현재 사용자 번호:', currentUserNo.value);
  console.log('현재 세션 번호:', currentSessionNo.value);
  
  // 자동 스크롤
  await nextTick()
  scrollToBottom()
})
</script>
<style scoped>
.sub-nav-section {
  background: white;
  padding: 40px 0;
  border-bottom: 1px solid #e0e0e0;
}

.sub-nav-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.sub-nav-title {
  font-size: 15px;
  font-weight: 400;
  color: #333;
  font-family: 'Pretendard', sans-serif;
  margin: 0;
  text-align: center;
}

.sub-nav-menu {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px 83px;
  width: 1049px;
  margin: 28px auto 0;
  padding: 30px 40px;
  border: 1px solid #e2e2e2;
  border-radius: 8px;
  background: white;
}

.sub-nav-item {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: transparent;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 400;
  color: #333;
  position: relative;
  text-align: center;
}

.sub-nav-item:hover {
  color: #008BE6;
  font-weight: bold;
  transform: translateY(-2px);
}

.sub-nav-item.active {
  background: #008BE6;
  color: white;
}

.pet-chat-container {
  max-width: 600px;
  height: 80vh;
  margin: 20px auto;
  background: white;
  border-radius: 15px;
  border: 1px solid #CCCCCC;
  box-shadow: 4px 0px 4px rgba(0, 0, 0, 0.25);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 헤더 */
.chat-header {
  background: linear-gradient(270deg, #FFEDED 0%, #EDD8D8 100%);
  border: 1px solid #D9D9D9;
  border-radius: 15px 15px 0 0;
  padding: 30px 40px;
}

.chat-title {
  font-size: 24px;
  font-weight: 600;
  color: #111111;
  font-family: 'Pretendard', sans-serif;
  text-align: center;
  line-height: 1.4;
  margin: 0 0 8px 0;
}

.chat-subtitle {
  font-size: 18px;
  font-weight: 600;
  color: #111111;
  font-family: 'Pretendard', sans-serif;
  text-align: center;
  line-height: 1.4;
  margin: 0;
}

/* 채팅 영역 */
.chat-area {
  flex: 1;
  background: #F8F9FA;
  border: 1px solid rgba(204, 204, 204, 0.80);
  padding: 20px;
  overflow-y: auto;
}

/* 웰컴 메시지 */
.welcome-message {
  background: white;
  border-radius: 15px;
  border: 1px solid rgba(211, 211, 211, 0.50);
  box-shadow: 3px 3px 3px rgba(0, 0, 0, 0.10);
  padding: 30px 20px;
  margin: 20px;
  text-align: center;
}

.welcome-greeting {
  color: #008BE6;
  font-size: 20px;
  font-weight: 700;
  font-family: 'Pretendard', sans-serif;
  display: block;
  margin-bottom: 15px;
}

.welcome-text {
  color: black;
  font-size: 16px;
  font-weight: 400;
  font-family: 'Pretendard', sans-serif;
  line-height: 1.5;
  margin: 15px 0;
}

.welcome-question {
  color: #008BE6;
  font-size: 18px;
  font-weight: 500;
  font-family: 'Pretendard', sans-serif;
  line-height: 1.4;
  display: block;
}

/* 메시지 그룹 */
.message-group {
  display: flex;
  margin-bottom: 20px;
  gap: 13px;
}

.bot-message {
  justify-content: flex-start;
}

.user-message {
  justify-content: flex-end;
}

/* 아바타 */
.message-avatar {
  flex-shrink: 0;
}

.avatar-icon {
  width: 45px;
  height: 45px;
  background: rgba(0, 138.76, 230.43, 0.32);
  border: 1px solid #D3D3D3;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0px 4px 4px rgba(0, 0, 0, 0.25);
}

/* 메시지 버블 */
.message-bubble {
  max-width: 275px;
  min-height: 124px;
  padding: 17px 35px;
  box-shadow: 5px 5px 5px rgba(0, 0, 0, 0.25);
  border: 1px solid #D3D3D3;
  display: flex;
  align-items: center;
}

.bot-bubble {
  background: white;
  border-radius: 7px 15px 15px 15px;
}

.user-bubble {
  background: rgba(191, 230, 255, 0.19);
  border-radius: 15px 7px 15px 15px;
}

.message-text {
  font-size: 12px;
  font-weight: 400;
  color: black;
  font-family: 'Pretendard', sans-serif;
  line-height: 16.8px;
  margin: 0;
}

/* 타이핑 인디케이터 */
.typing-indicator {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
}

.typing-dots {
  display: flex;
  gap: 12px;
}

.typing-dots span {
  width: 10px;
  height: 10px;
  background: rgba(204, 204, 204, 0.50);
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-dots span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-dots span:nth-child(3) {
  animation-delay: 0.4s;
}

@keyframes typing {
  0%, 60%, 100% {
    opacity: 0.3;
  }
  30% {
    opacity: 1;
  }
}

/* 입력 영역 */
.input-area {
  background: white;
  padding: 20px;
  border-top: 1px solid #e0e0e0;
}

.input-container {
  display: flex;
  gap: 27px;
  align-items: center;
}

.message-input {
  flex: 1;
  height: 92px;
  padding: 13px 19px;
  border: 1px solid rgba(136, 136, 136, 0.70);
  border-radius: 60px;
  font-size: 15px;
  font-weight: 200;
  color: #333;
  font-family: 'Pretendard', sans-serif;
  line-height: 21px;
  box-shadow: 4px 4px 4px rgba(0, 0, 0, 0.10);
  outline: none;
}

.message-input::placeholder {
  color: #888888;
}

.send-button {
  width: 89px;
  height: 85px;
  background: rgba(0, 138.76, 230.43, 0.50);
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.send-button:hover {
  background: rgba(0, 138.76, 230.43, 0.70);
  transform: scale(1.05);
}

.send-icon {
  width: 25px;
  height: 27px;
  background: #008BE6;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  font-weight: bold;
  clip-path: polygon(0 0, 100% 50%, 0 100%);
}

/* 반응형 */
@media (max-width: 768px) {
  .pet-chat-container {
    height: 100vh;
    border-radius: 0;
  }
  
  .chat-header {
    padding: 30px 20px;
  }
  
  .chat-title {
    font-size: 24px;
  }
  
  .chat-subtitle {
    font-size: 18px;
  }
  
  .welcome-message {
    margin: 20px;
    padding: 30px 20px;
  }
  
  .welcome-greeting {
    font-size: 24px;
  }
  
  .welcome-text {
    font-size: 18px;
  }
  
  .welcome-question {
    font-size: 20px;
  }
  
  .message-bubble {
    max-width: 200px;
    padding: 15px 20px;
  }
  
  .input-container {
    gap: 15px;
  }
  
  .send-button {
    width: 60px;
    height: 60px;
  }
}
</style>
