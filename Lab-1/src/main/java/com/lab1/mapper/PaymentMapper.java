package com.lab1.mapper;

import com.lab1.domain.Payment;
import com.lab1.dto.PaymentResponse;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {
    public PaymentResponse toResponse(Payment payment) {
        PaymentResponse response = new PaymentResponse();
        response.setId(payment.getId());
        response.setMerchantId(payment.getMerchant().getId());
        response.setAmount(payment.getAmount());
        response.setStatus(payment.getStatus());
        response.setProviderReference(payment.getProviderReference().trim());
        response.setCreatedAt(payment.getCreatedAt());
        return response;
    }
}
