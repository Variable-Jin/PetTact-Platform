<template>
  <div class="modal-overlay" @click.self="$emit('close')">
    <div class="modal-box">
      <h4 v-if="!chatStore.roomNo">나의 채팅방</h4>

      <!-- ✅ 채팅방 목록 -->
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

      <!-- ✅ 채팅창 -->
      <ChatWindow v-if="chatStore.roomNo" :roomNo="chatStore.roomNo" />

      <!-- ✅ 닉네임 검색 -->
      <button v-if="!chatStore.roomNo" @click="showSearch = true" class="search-btn">
        닉네임으로 채팅 신청하기
      </button>
      <SearchUserModal v-if="showSearch" @roomOpen="openRoom" @close="showSearch = false" />

      <!-- ✅ 닫기 또는 채팅 종료 -->
      <button class="close-btn" @click="closeModal">
        {{ chatStore.roomNo ? '채팅방 나가기' : '닫기' }}
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
  if (chatStore.roomNo) {
    chatStore.roomNo = null;
    chatStore.messages = [];
  } else {
    // 채팅방이 아니면 모달 닫기
    emit('close');
  }
}
</script>
