<template>
  <div>
    <h2>{{ chatStore.name }}</h2>
    <div class="chat-box">
      <div v-for="(msg, idx) in chatStore.messages" :key="idx">
        <strong>[{{ msg.sender }}]</strong>{{ msg.message }} 
      </div>
    </div>
    <input
      v-model="message"
      @keyup.enter="sendMessage"
      placeholder="메세지 입력"
    />
    <button @click="sendMessage">전송</button>
    <button class="close-btn" @click="$emit('close')">채팅 닫기</button>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useChatStore } from '@/js/pinia';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import axios from '@/js/axios'
const props = defineProps({
  roomNo: {
    type: Number,
    required: true
  }
});

const chatStore = useChatStore();
const message = ref('');


const jwtToken = 'Bearer eyJ1c2VyRW1haWwiOiJzdXl1bmdraW5nQG5hdmVyLmNvbSIsInVzZXJOaWNrbmFtZSI6IuyImOyYgSIsInVzZXJSb2xlIjoiUk9MRV9VU0VSIiwidXNlck5vIjozLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyTmlja25hbWUiOiLsiJjsmIEiLCJ1c2VyRW1haWwiOiJzdXl1bmdraW5nQG5hdmVyLmNvbSIsInVzZXJSb2xlIjoiUk9MRV9VU0VSIiwidXNlck5vIjozLCJpYXQiOjE3NTIxNDk2OTMsImV4cCI6MTc1MzAxMzY5M30.qXUdkEt3_E-fGphwp0mlK6jv9xttLk-_58as3fBkeqA';

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

onMounted(async () => {
  await fetchMessages(props.roomNo); 
  connect(props.roomNo);             
});

onBeforeUnmount(() => {
  chatStore.stompClient?.deactivate();
});
</script>
