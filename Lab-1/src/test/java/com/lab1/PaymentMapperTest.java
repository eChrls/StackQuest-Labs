package com.lab1;

import com.lab1.domain.Merchant;
import com.lab1.domain.Payment;
import com.lab1.domain.PaymentStatus;
import com.lab1.dto.PaymentResponse;
import com.lab1.mapper.PaymentMapper;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentMapperTest {
    @Test void mapsBasicFields() {
        PaymentMapper mapper = new PaymentMapper();
        Payment payment = new Payment(UUID.randomUUID(), new Merchant("M1", "Merchant One"), new BigDecimal("42.00"), PaymentStatus.CAPTURED, "REF-1", Instant.now());
        PaymentResponse response = mapper.toResponse(payment);
        assertEquals("M1", response.getMerchantId());
        assertEquals(PaymentStatus.CAPTURED, response.getStatus());
    }
}
