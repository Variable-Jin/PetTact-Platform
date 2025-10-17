# Node.js 일반 이미지
FROM node:20 AS build

# 작업 디렉토리 생성
WORKDIR /app

# package.json 복사
COPY package.json ./
COPY package-lock.json ./

# 의존성 설치
RUN npm ci

# 소스 코드 복사
COPY . .

# 프로젝트 빌드 (Vue + Vite)
RUN npm run build

# Nginx 웹서버 설정
FROM nginx:alpine
COPY --from=build /app/dist /usr/share/nginx/html

# 컨테이너가 사용할 포트 노출
EXPOSE 80

# Nginx 실행
CMD ["nginx", "-g", "daemon off;"]
