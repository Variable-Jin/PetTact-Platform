package com.pettact.api.payment.service;

import org.springframework.stereotype.Service;

import com.pettact.api.payment.client.TossPaymentClient;
import com.pettact.api.payment.dto.PaymentConfirmRequestDTO;
import com.pettact.api.payment.dto.PaymentResponseDTO;
import com.pettact.api.payment.dto.TossConfirmResponseDTO;
import com.pettact.api.payment.entity.PaymentEntity;
import com.pettact.api.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final TossPaymentClient tossPaymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentResponseDTO confirmPayment(PaymentConfirmRequestDTO request) {
        try {
            // 1. 토스에 결제 승인 요청을 보냄
            TossConfirmResponseDTO tossResponse = tossPaymentClient.confirmPayment(
                    request.getPaymentKey(),
                    request.getOrderId(),
                    request.getAmount()
            );

            // 2. 응답으로 받은 데이터로 Entity 생성
            PaymentEntity payment = PaymentEntity.fromTossResponse(tossResponse);
            
            System.out.println("🛰️ Toss API 응답 amount: " + tossResponse.getAmount());

            // 3. DB에 저장
            paymentRepository.save(payment);

            // 4. DTO로 변환해서 클라이언트에 반환
            return PaymentResponseDTO.fromEntity(payment);

        } catch (Exception e) {
            System.err.println("❌ 결제 승인 처리 중 에러 발생: " + e.getMessage());
            e.printStackTrace(); // 스택 트레이스 출력

            // 필요하다면 예외 재던지기 or 커스텀 예외 반환 가능
            throw e; 
        }
    }
}
