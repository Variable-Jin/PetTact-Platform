// src/api.js
import axios from 'axios';

const api = axios.create({
  baseURL: '/', // 필요 시 API 기본 경로 지정
});

// api.interceptors.request.use((config) => {
//   const token = localStorage.getItem('eyJ1c2VyTm8iOjEsInVzZXJFbWFpbCI6InBldHRhY3QyMDI1QGdtYWlsLmNvbSIsInVzZXJOaWNrbmFtZSI6Iu2Oq-2Fje2KuCIsInVzZXJSb2xlIjoiUk9MRV9OT1JNQUwiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyTmlja25hbWUiOiLtjqvthY3tirgiLCJ1c2VyRW1haWwiOiJwZXR0YWN0MjAyNUBnbWFpbC5jb20iLCJ1c2VyUm9sZSI6IlJPTEVfTk9STUFMIiwidXNlck5vIjoxLCJpYXQiOjE3NTE0Mzg2OTYsImV4cCI6MTc1MjA0MzQ5Nn0.pwAwo12pZPcBizklcY9dOs41UVb6rwlVLMR-NN_sBCc'); // 로컬 스토리지에서 토큰 읽기
//   if (token) {
//     config.headers.Authorization = `Bearer ${token}`;
//   }
//   return config;
// });


// 공통 Authorization 헤더 자동 주입
api.interceptors.request.use((config) => {
  const token = 'eyJ1c2VyRW1haWwiOiJwZXR0YWN0MjAyNUBnbWFpbC5jb20iLCJ1c2VyTm8iOjEsInVzZXJSb2xlIjoiUk9MRV9BRE1JTiIsInVzZXJOaWNrbmFtZSI6Iu2Oq-2Fje2KuCIsImFsZyI6IkhTMjU2In0.eyJ1c2VyTmlja25hbWUiOiLtjqvthY3tirgiLCJ1c2VyRW1haWwiOiJwZXR0YWN0MjAyNUBnbWFpbC5jb20iLCJ1c2VyUm9sZSI6IlJPTEVfQURNSU4iLCJ1c2VyTm8iOjEsImlhdCI6MTc1MTcxNjMwMiwiZXhwIjoxNzUyMzIxMTAyfQ.HFjpEdMNWv1qUaFYrjd6Y59wwi4gx4AwkrA-gSKIWzk'; // 추후 localStorage 등으로 대체 가능
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export default api;
