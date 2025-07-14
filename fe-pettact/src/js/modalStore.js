import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useModalStore = defineStore('modalStore', () => {
  const isMessageModalOpen = ref(false);

  function openMessageModal() {
    isMessageModalOpen.value = true;
  }

  function closeMessageModal() {
    isMessageModalOpen.value = false;
  }

  return {
    isMessageModalOpen,
    openMessageModal,
    closeMessageModal
  };
});
