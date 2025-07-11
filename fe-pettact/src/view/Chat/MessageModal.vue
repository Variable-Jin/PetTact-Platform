<template>
<div class="modal-overlay" @click.self="closeChatModal">
    <div class="modal-box">
      <h4 v-if="!chatStore.roomNo">나의 채팅방</h4>

      <ul v-if="!chatStore.roomNo && chatRooms.length > 0">
        <li
          v-for="room in chatRooms"
          :key="room.roomNo"
          @click="openRoom(room.roomNo)"
          class="chat-room-item"
        >
          {{ room.name }}
        </li>
      </ul>
      <p v-else-if="!chatStore.roomNo">참여 중인 채팅방이 없습니다.</p>

      <!--  채팅창 -->
      <ChatWindow v-if="chatStore.roomNo" :roomNo="chatStore.roomNo" />

      <!--  닉네임 검색 -->
      <button v-if="!chatStore.roomNo" @click="showSearch = true" class="search-btn">
        닉네임으로 채팅 신청하기
      </button>
      <SearchUserModal v-if="showSearch" @roomOpen="openRoom" @close="showSearch = false" />

      <!--  닫기 또는 채팅 종료 -->
      <button class="close-btn" @click="closeModal">
        {{ chatStore.roomNo ? '채팅방 목록' : '닫기' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from '@/js/axios';
import { useChatStore } from '@/js/pinia';
import SearchUserModal from './SearchUserModal.vue';
import ChatWindow from './ChatRoom.vue';

const showSearch = ref(false);
const chatRooms = ref([]);
const chatStore = useChatStore();
const emit = defineEmits(['close']); 

onMounted(() => {
  axios.get('/chat/room/my').then(res => {
    chatRooms.value = res.data;
  });
});

function openRoom(roomNo) {
  chatStore.roomNo = roomNo;
  showSearch.value = false;
}

function closeModal() {
  chatStore.reset(); 

  axios.get('/chat/room/my')
    .then(res => {
      chatRooms.value = res.data;
    })
    .catch(err => {
      console.error('채팅방 목록 재조회 실패:', err);
    });

  emit('close'); 
}
</script>
<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-box {
  background-color: black;
  padding: 24px;
  width: 400px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.2);
}

.modal-box h4,
.modal-box h5 {
  margin-bottom: 16px;
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.chat-room-item {
  padding: 10px;
  margin-bottom: 8px;
  background-color: black;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s ease;
}
.chat-room-item:hover {
  background-color: #eaeaea;
}

.search-btn, .close-btn {
  margin-top: 12px;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  background-color: #4caf50;
  color: white;
  cursor: pointer;
  font-weight: bold;
}
.search-btn:hover,
.close-btn:hover {
  background-color: #43a047;
}
</style>
