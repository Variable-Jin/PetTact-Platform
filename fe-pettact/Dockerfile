# Node.js 베이스 이미지
FROM node:20 AS build

# 작업 디렉토리 생성
WORKDIR /app

# package.json과 package-lock.json 복사
COPY package*.json ./

# 의존성 설치 (lock 파일 사용)
RUN npm ci

# 소스 코드 복사
COPY . .

# 프로덕션 환경 설정
ENV NODE_ENV=production

# 프로젝트 빌드 (Vue + Vite)
RUN npm run build

# Nginx 웹서버 설정
FROM nginx:alpine

# 빌드된 파일 복사
COPY --from=build /app/dist /usr/share/nginx/html

# Nginx 설정 파일 복사 (Vue Router용)
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 컨테이너가 사용할 포트 노출
EXPOSE 80

# Nginx 실행
CMD ["nginx", "-g", "daemon off;"]
