<template>
  <div>
    <h2>{{ chatStore.name }}</h2>

    <div ref="chatBox" class="chat-box">
      <div
        v-for="(msg, idx) in chatStore.messages"
        :key="idx"
        :class="['message', msg.isMine ? 'mine' : 'other']"
      >
        <strong>[{{ msg.senderNickname }}]</strong> {{ msg.message }}
      </div>
    </div>

    <input
      v-model="message"
      @keyup.enter="sendMessage"
      placeholder="메세지 입력"
    />
    <!-- <button class="send-btn"@click="sendMessage">전송</button> -->
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount, watch, nextTick } from 'vue';
// import { useChatStore } from '@/js/pinia';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
// import axios from '@/js/axios';

const props = defineProps({
  roomNo: {
    type: Number,
    required: true
  }
});

const chatStore = useChatStore();
const message = ref('');
const chatBox = ref(null);

const jwtToken = 'Bearer eyJ1c2VyTmlja25hbWUiOiLtjqvthY3tirgiLCJ1c2VyUm9sZSI6IlJPTEVfVVNFUiIsInVzZXJObyI6MSwidXNlckVtYWlsIjoicGV0dGFjdDIwMjVAZ21haWwuY29tIiwiYWxnIjoiSFMyNTYifQ.eyJ1c2VyTmlja25hbWUiOiLtjqvthY3tirgiLCJ1c2VyRW1haWwiOiJwZXR0YWN0MjAyNUBnbWFpbC5jb20iLCJ1c2VyUm9sZSI6IlJPTEVfVVNFUiIsInVzZXJObyI6MSwiaWF0IjoxNzUyMjAwNzM3LCJleHAiOjE3NTMwNjQ3Mzd9.shCmZ-OcjW6NYbmzPcNb_nHEw7R6jLCZdgV2VYRBjQM'; // 실제 JWT 토큰 사용

// 메시지 불러오기
function fetchMessages(roomNo) {
  axios.get(`/chat/message/${roomNo}`, {
    headers: {
      Authorization: jwtToken
    }
  })
  .then(res => {
    chatStore.messages = res.data;
  })
  .catch(err => {
    console.error('메시지 불러오기 실패:', err);
  });
}

// 웹소켓 연결
function connect(roomNo) {
  chatStore.roomNo = roomNo;

  const socket = new SockJS('http://localhost:8080/ws-stomp');
  const stompClient = new Client({
    webSocketFactory: () => socket,
    connectHeaders: {
      Authorization: jwtToken,
    },
    reconnectDelay: 5000,
    heartbeatIncoming: 10000,
    heartbeatOutgoing: 10000,
    onConnect: () => {
      chatStore.connected = true;
      chatStore.setClient(stompClient);

      stompClient.subscribe(`/topic/chat/room/${roomNo}`, (msg) => {
        const data = JSON.parse(msg.body);
        chatStore.addMessage(data);
      });
    },
    onStompError: (frame) => {
      console.error('STOMP 에러:', frame);
    }
  });

  stompClient.activate();
}

// 메시지 전송
function sendMessage() {
  if (!chatStore.connected || !message.value.trim()) return;

  chatStore.stompClient?.publish({
    destination: '/app/chat/message',
    body: JSON.stringify({
      type: 'TALK',
      roomNo: chatStore.roomNo,
      message: message.value
    }),
    headers: {
      Authorization: jwtToken
    }
  });

  message.value = '';
}

// 자동 스크롤
watch(() => chatStore.messages.length, async () => {
  await nextTick();
  if (chatBox.value) {
    chatBox.value.scrollTop = chatBox.value.scrollHeight;
  }
});

onMounted(async () => {
  await fetchMessages(props.roomNo);
  connect(props.roomNo);
});

onBeforeUnmount(() => {
  chatStore.stompClient?.deactivate();
});
</script>

<style scoped>
.chat-box {
  height: 300px;
  overflow-y: auto;
  padding: 10px;
  background: #1e1e1e;
  border-radius: 8px;
  margin-bottom: 10px;
  display: flex;
  flex-direction: column;
}

/* 메시지 공통 스타일 */
.message {
  margin: 5px 0;
  max-width: 70%;
  word-break: break-word;
}

/* 내 메시지: 오른쪽 */
.mine {
  align-self: flex-end;
  text-align: right;
  color: #fff;
}

/* 상대 메시지: 왼쪽 */
.other {
  align-self: flex-start;
  text-align: left;
  color: #aaa;
}

.send-btn {
  margin-top: 12px;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  background-color: #4caf50;
  color: white;
  cursor: pointer;
  font-weight: bold;
}
</style>
