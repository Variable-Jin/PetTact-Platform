package com.pettact.api.payment.dto;

import com.pettact.api.payment.entity.PaymentEntity;

import lombok.Builder;
import lombok.Data;

/**
 * 결제 처리 후 클라이언트에 반환할 응답 DTO
 */
@Data
@Builder
public class PaymentResponseDTO {
	
    private String paymentKey;
    private Long orderNo;
    private Long amount;
    private String method;
    private String status;
    private String approvedAt;
    private String cardCompany;

    // Entity → DTO 변환 메서드
    public static PaymentResponseDTO fromEntity(PaymentEntity payment) {
        return PaymentResponseDTO.builder()
                .paymentKey(payment.getPaymentKey())
                .orderNo(payment.getOrder().getOrderNo())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .approvedAt(payment.getApprovedAt())
                .cardCompany(payment.getCardCompany())
                .build();
    }
}
