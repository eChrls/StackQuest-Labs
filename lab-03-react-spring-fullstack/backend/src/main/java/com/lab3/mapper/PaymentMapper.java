package com.lab3.mapper;
import com.lab3.domain.Payment;
import com.lab3.dto.PaymentDto;
import org.springframework.stereotype.Component;
@Component
public class PaymentMapper {
    public PaymentDto toDto(Payment payment) {
        return new PaymentDto(payment.getId(), payment.getMerchant().getId(), payment.getMerchant().getId(), payment.getAmount(), payment.getStatus(), payment.getCreatedAt(), payment.getDescription());
    }
}
