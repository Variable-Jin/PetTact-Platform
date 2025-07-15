<template>
  <div class="notification-item dropdown" ref="notiDropdownRef" @click="toggle">
    🔔
    <span v-if="unreadCount > 0" class="badge">{{ unreadCount }}</span>

    <div v-if="isOpen" class="dropdown-menu show" style="min-width: 300px; right: 0;">
      <div v-if="notifications.length === 0" class="dropdown-item text-muted">알림 없음</div>
      <div
        v-for="noti in notifications.slice(0, 5)"
        :key="noti.notificationNo"
        class="dropdown-item"
        :class="{ 'fw-bold': !noti.isRead }"
        @click="handleNotiClick(noti)"
      >
        <div>{{ noti.notificationTitle }} · {{ formatDate(noti.createdAt) }}</div>
        <small class="text-muted">{{ noti.notificationContent }}</small>
      </div>
      <div class="dropdown-divider"></div>
      <div class="dropdown-item text-primary" @click="goToAllNotifications">전체 알림 보기</div>
      <div class="dropdown-item text-primary" @click="markAllAsRead">모두 읽음</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter } from 'vue-router'
import { useNotificationStore } from '@/stores/notification'
import dayjs from 'dayjs'

const router = useRouter()
const notiStore = useNotificationStore()

const isOpen = ref(false)
const notiDropdownRef = ref(null)

const notifications = computed(() => notiStore.notifications)
const unreadCount = computed(() => notiStore.unreadCount)

const toggle = () => {
  isOpen.value = !isOpen.value
}

const formatDate = (isoDateStr) => {
  return dayjs(isoDateStr).fromNow()
}

const markAllAsRead = async () => {
  await notiStore.markAllAsRead()
}

const goToAllNotifications = () => {
  router.push({ name: 'notifications' })
  isOpen.value = false
}

const handleNotiClick = async (noti) => {
  isOpen.value = false

  if (!noti.isRead) {
    await notiStore.markAsRead(noti.notificationNo)
  }

  const { targetType, targetId } = noti

  switch (targetType) {
    case 'POST':
      try {
        const res = await fetch(`/v1/board/${targetId}`)
        const data = await res.json()
        const categoryNo = data.responseDto.boardCategoryNo
        router.push(`/board/${categoryNo}/${targetId}`)
      } catch (err) {
        console.error('게시글 정보 조회 실패', err)
      }
      break
    case 'REPLY':
      router.push(`/board/post/${targetId}`)
      break
    case 'REPORT':
      router.push({ name: 'myReportDetail', params: { reportNo: targetId } })
      break
    case 'USER':
      router.push({ name: 'myMarket' })
      break
    default:
      console.warn('알 수 없는 알림 타입:', targetType)
  }
}

// 외부 클릭 시 드롭다운 닫기
const handleClickOutside = (e) => {
  if (
    isOpen.value &&
    notiDropdownRef.value &&
    !notiDropdownRef.value.contains(e.target)
  ) {
    isOpen.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onBeforeUnmount(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped>
.notification-item {
  position: relative;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  transition: background-color 0.2s ease;
}

.notification-item:hover {
  background: #f8f9fa;
}

.badge {
  position: absolute;
  top: -2px;
  right: -2px;
  background: #ff4757;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 11px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid white;
  font-family: 'Pretendard', sans-serif;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border: 1px solid #e1e5e9;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  min-width: 300px;
  z-index: 1000;
  margin-top: 8px;
  padding: 8px 0;
}
</style>
