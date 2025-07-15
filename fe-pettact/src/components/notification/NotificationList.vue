<template>
  <div class="container mt-3">
    <div class="d-flex justify-content-between align-items-center mb-2">
      <h4>실시간 알림 목록</h4>
      <div>
        <button class="btn btn-sm btn-outline-secondary me-2" @click="markAll">모두 읽음</button>
        <button class="btn btn-sm btn-outline-danger" @click="deleteAll">전체 삭제</button>
      </div>
    </div>

    <ul class="list-group">
      <li
        v-for="noti in notificationStore.notifications"
        :key="noti.notificationNo"
        class="list-group-item d-flex justify-content-between align-items-center"
        :class="{ 'list-group-item-light': noti.isRead }"
        @click="handleClick(noti)"
        style="cursor: pointer"
      >
        <div>
          <strong>{{ noti.notificationTitle }} · {{ formatDate(noti.createdAt) }}</strong><br />
          <small>{{ noti.notificationContent }}</small>
        </div>
        <span v-if="!noti.isRead" class="badge bg-danger">NEW</span>
        <button class="btn btn-sm btn-outline-danger" @click.stop="deleteNoti(noti.notificationNo)">X</button>
      </li>
    </ul>
  </div>
</template>

<script setup>
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { useNotificationStore } from '@/stores/notification';
import { useUserStore } from '@/stores/user';
import dayjs from 'dayjs';

const router = useRouter();
const userStore = useUserStore();
const notificationStore = useNotificationStore();

onMounted(() => {
  if (userStore.accessToken) {
    notificationStore.fetchNotifications();
    notificationStore.fetchUnreadCount();
  }
});

const handleClick = async (noti) => {
  if (!noti.isRead) {
    await notificationStore.markAsRead(noti.notificationNo);
  }

  const { targetType, targetId } = noti;

  switch (targetType) {
    case 'POST':
      try {
        const res = await axios.get(`/v1/board/${targetId}`);
        const categoryNo = res.data.responseDto.boardCategoryNo;
        router.push(`/board/${categoryNo}/${targetId}`);
      } catch (err) {
        console.error('게시글 정보 조회 실패', err);
      }
      break;
    case 'REPLY':
      router.push({ path: `/board/post/${targetId}` });
      // router.push({ path: `/board/post/${targetId}`, query: { focus: 'reply' } });
      break;
    case 'REPORT':
      router.push({ name: 'myReportDetail', params: { reportNo: targetId } });
      break;
    default:
      console.warn('알 수 없는 알림 타입:', targetType);
  }
};

const formatDate = (isoDateStr) => {
  return dayjs(isoDateStr).fromNow();
};

const markAll = async () => {
  await notificationStore.markAllAsRead();
};

const deleteAll = async () => {
  await notificationStore.deleteAllNotifications();
};

const deleteNoti = async (id) => {
  await notificationStore.deleteNotification(id);
};
</script>

<style scoped>
.list-group-item-light {
  background-color: #f8f9fa;
}
</style>
