package com.pettact.api.mongo;

import java.io.IOException;
import java.util.Date;

import org.bson.Document;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.pettact.api.security.vo.CustomUserDetails;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component 
@RequiredArgsConstructor 
public class MongoLoggingFilter implements Filter { 

    private final MongoLogRepository repo; 
       
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
    	
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        
        
        // 요청 시작 시각 기록
        long startTime = System.currentTimeMillis();

        chain.doFilter(req, res);

        // 응답까지 걸린 시간 계산
        long duration = System.currentTimeMillis() - startTime;

        // MongoDB에 저장할 로그 문서 생성
        Document logDoc = new Document()
                .append("timestamp", new Date())         // 로그 생성 시각
                .append("method", request.getMethod())  // HTTP 메서드 (GET/POST 등)
                .append("uri", request.getRequestURI()) // 요청 URL 경로
                .append("ip", getClientIp(request))  // 클라이언트 IP
                .append("userId", getUserId())    // 로그인한 사용자 ID (없으면 null)
                .append("status", response.getStatus())// HTTP 응답 상태코드 (200, 401 등)
                .append("duration", duration); // 처리 시간 (ms)
                
        // 로그 MongoDB에 저장
        repo.save(logDoc);
    }
    //유저 번호 가져오는 메소드입니다.
    private Long getUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated() &&
                auth.getPrincipal() instanceof CustomUserDetails userDetails) {
                return userDetails.getUserEntity().getUserNo(); // 유저 번호 (Long)
            }
        } catch (Exception e) {
        	
        }

        return null;
    }

    
    public String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            // X-Forwarded-For에 여러 IP가 있을 경우 첫 번째 것이 실제 클라이언트 IP
            return ip.split(",")[0].trim();
        }

        ip = request.getHeader("Proxy-Client-IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("WL-Proxy-Client-IP");  // WebLogic
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("HTTP_CLIENT_IP");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }

        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            return ip;
        }
        // 그 외에는 remote address 사용
        return request.getRemoteAddr();
    }

}
