import { defineStore } from 'pinia';
import { useStorage } from '@vueuse/core';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

export const useUserStore = defineStore('user', {
  state: () => ({
    user: useStorage('user', null),
    accessToken: useStorage('accessToken', null),
  }),
  actions: {
    async login(email, password) {
      const res = await axios.post('/v1/user/login', { userEmail: email, userPassword: password });
      this.accessToken = res.data.accessToken;
      this.user = res.data.user;

      localStorage.setItem('accessToken', this.accessToken);
    },

    restoreUserFromToken() {
      if (this.accessToken) {
        try {
          const decoded = jwtDecode(this.accessToken);

          // 디버깅 로그 추가
          console.log('Decoded JWT:', decoded);
          console.log('UserNo from token:', decoded.userNo);

          this.user = {
            userEmail: decoded.userEmail,
            userNo: decoded.userNo,
            userNickname: decoded.userNickname,
            userRole: decoded.userRole,
          };

          // 설정 후 확인
          console.log('User after setting:', this.user);
        } catch (e) {
          console.error('Failed to decode JWT', e);
        }
      }
    },

    logout() {
      this.user = null;
      this.accessToken = null;
      localStorage.removeItem('accessToken');
    },
  },
});
