<template>
  <div class="container mt-4">
    <h3>내 활동</h3>

    <ul class="nav nav-tabs mt-3">
      <li class="nav-item">
        <a
          class="nav-link"
          :class="{ active: activeTab === 'reports' }"
          @click="activeTab = 'reports'"
        >신고내역</a>
      </li>
    </ul>

    <div class="mt-3">
      <MyReports v-if="activeTab === 'reports'" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import MyReports from './components/myActivity/MyReports.vue';

const activeTab = ref("reports");
</script>
