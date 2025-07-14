package com.pettact.api.payment.client;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.pettact.api.payment.dto.TossConfirmResponseDTO;

import reactor.core.publisher.Mono;

@Component
public class TossPaymentClient {

    private final WebClient webClient;

    public TossPaymentClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("https://api.tosspayments.com/v1/payments")
                .defaultHeaders(headers -> {
                    headers.setBasicAuth("test_sk_Z1aOwX7K8mBZbWGnJeGW3yQxzvNP", ""); // 🔒 테스트 시크릿 키 설정
                    headers.add("Content-Type", "application/json");
                })
                .build();
    }

    /**
     * 토스에 결제 승인 요청을 보내는 함수
     */
    public TossConfirmResponseDTO confirmPayment(String paymentKey, String orderId, Long amount) {
        ConfirmRequest request = new ConfirmRequest(paymentKey, orderId, amount);
        System.out.println("🛰️ [Toss 요청] paymentKey=" + paymentKey + ", orderId=" + orderId + ", amount=" + amount);

        // ✅ JSON 응답 먼저 String으로 받기
        String jsonResponse = webClient.post()
                .uri("/confirm")
                .bodyValue(request)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class).flatMap(body -> {
                            System.err.println("❌ [Toss 오류] status=" + clientResponse.statusCode().value() + ", body=" + body);
                            return Mono.error(new RuntimeException("Toss 오류 응답: " + body));
                        }))
                .bodyToMono(String.class)
                .block();

        // ✅ JSON 로그 출력
        System.out.println("📦 [Toss 원본 JSON 응답]: " + jsonResponse);

        // ✅ JSON → DTO 수동 매핑 (ObjectMapper 사용)
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            TossConfirmResponseDTO dto = mapper.readValue(jsonResponse, TossConfirmResponseDTO.class);
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("❌ Toss 응답 파싱 실패", e);
        }
    }
    record ConfirmRequest(String paymentKey, String orderId, Long amount) {}
}
