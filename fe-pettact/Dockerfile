# Node.js 베이스 이미지
FROM node:20 AS build

# 작업 디렉토리 생성
WORKDIR /app

# package.json만 복사 (package-lock.json 제외)
COPY package.json ./

# package-lock.json 없이 새로 설치
RUN npm install

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
