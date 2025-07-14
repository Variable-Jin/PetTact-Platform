<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue';
import { useModalStore } from '@/js/modalStore';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import axios from 'axios';

const props = defineProps({
  roomNo: {
    type: Number,
    required: true
  }
});

const modalStore = useModalStore();
const message = ref('');
const chatBox = ref(null);

// 토큰 가져오기
function getBearerToken() {
  const token = localStorage.getItem('accessToken');
  return token ? `Bearer ${token}` : null;
}

// 메시지 불러오기
function fetchMessages(roomNo) {
  console.log('[fetchMessages] roomNo:', roomNo);
  axios.get(`/v1/chat/message/${roomNo}`, {
    headers: {
      Authorization: getBearerToken()
    }
  })
  .then(res => {
    console.log('[fetchMessages] 메시지 수신:', res.data);
    modalStore.messages = res.data;
  })
  .catch(err => {
    console.error('메시지 불러오기 실패:', err);
  });
}

// WebSocket 연결
function connect(roomNo) {
  console.log('[connect] STOMP 연결 시도');
  modalStore.roomNo = roomNo;

  const socket = new SockJS('/ws-stomp');
  const stompClient = new Client({
    webSocketFactory: () => socket,
    connectHeaders: {
      Authorization: getBearerToken(),
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    onConnect: () => {
      console.log('✅ STOMP 연결 성공');
      modalStore.connected = true;
      modalStore.setClient(stompClient);

      stompClient.subscribe(`/topic/chat/room/${roomNo}`, (msg) => {
        const data = JSON.parse(msg.body);
        console.log('[subscribe] 새 메시지 수신:', data);
        modalStore.addMessage(data);
      });
    },
    onStompError: (frame) => {
      console.error('❌ STOMP 에러:', frame);
    },
    onWebSocketError: (event) => {
      console.error('❌ WebSocket 연결 실패:', event);
    }
  });

  stompClient.activate();
}

// 메시지 전송
function sendMessage() {
  console.log('[sendMessage] 전송 시도:', message.value, modalStore.connected);

  if (!modalStore.connected || !message.value.trim()) {
    console.warn('⚠️ 전송 조건 불충족');
    return;
  }

  modalStore.stompClient?.publish({
    destination: '/app/chat/message', // 💡 @MessageMapping("/chat/message") 기준
    body: JSON.stringify({
      roomNo: modalStore.roomNo,
      message: message.value,
    }),
    headers: {
      Authorization: getBearerToken()
    }
  });

  console.log('[sendMessage] 메시지 전송 완료');
  message.value = '';
}

// 메시지 변경 시 자동 스크롤
watch(() => modalStore.messages.length, async () => {
  await nextTick();
  if (chatBox.value) {
    chatBox.value.scrollTop = chatBox.value.scrollHeight;
  }
});

onMounted(async () => {
  console.log('[onMounted] 컴포넌트 마운트됨');
  await fetchMessages(props.roomNo);
  connect(props.roomNo);
});

onBeforeUnmount(() => {
  console.log('[onBeforeUnmount] 연결 종료');
  modalStore.stompClient?.deactivate();
});
</script>


<template>
  <div class="chat-room">
    <div ref="chatBox" class="chat-messages">
      <div
        v-for="(msg, index) in modalStore.messages"
        :key="index"
        :class="['chat-message', msg.isMine ? 'me' : 'other']"
      >
        <span class="sender">{{ msg.senderNickname }}</span>
        <div class="bubble">{{ msg.message }}</div>
      </div>
    </div>

    <div class="chat-input">
      <input
        v-model="message"
        @keyup.enter="sendMessage"
        type="text"
        placeholder="메시지를 입력하세요"
      />
      <button @click="sendMessage">전송</button>
    </div>
  </div>
</template>

<style scoped>
.chat-room {
  display: flex;
  flex-direction: column;
  height: 400px;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 10px;
  background-color: #f7f7f7;
}

.chat-message {
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
}

.chat-message.me {
  align-items: flex-end;
}

.chat-message.other {
  align-items: flex-start;
}

.sender {
  font-size: 12px;
  color: #555;
}

.bubble {
  padding: 8px 12px;
  background-color: #dcf8c6;
  border-radius: 12px;
  max-width: 70%;
  word-break: break-word;
}

.chat-message.other .bubble {
  background-color: #eee;
}

.chat-input {
  display: flex;
  border-top: 1px solid #ccc;
  padding: 8px;
}

.chat-input input {
  flex: 1;
  padding: 8px;
  border-radius: 6px;
  border: 1px solid #ccc;
  margin-right: 8px;
}

.chat-input button {
  padding: 8px 16px;
  background-color: #4caf50;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.chat-input button:hover {
  background-color: #43a047;
}
</style>
