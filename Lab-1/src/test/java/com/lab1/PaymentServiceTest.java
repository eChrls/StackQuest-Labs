package com.lab1;

import com.lab1.domain.Merchant;
import com.lab1.domain.Payment;
import com.lab1.domain.PaymentStatus;
import com.lab1.repository.MerchantRepository;
import com.lab1.repository.PaymentRepository;
import com.lab1.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceTest {
    @Autowired private PaymentService paymentService;
    @Autowired private MerchantRepository merchantRepository;
    @Autowired private PaymentRepository paymentRepository;

    @Test void calculatesCapturedTotal() {
        Merchant merchant = merchantRepository.save(new Merchant("M-TEST-1", "Test Merchant 1"));
        paymentRepository.save(new Payment(UUID.randomUUID(), merchant, new BigDecimal("100.00"), PaymentStatus.CAPTURED, "P1", Instant.now()));
        paymentRepository.save(new Payment(UUID.randomUUID(), merchant, new BigDecimal("50.00"), PaymentStatus.CAPTURED, "P2", Instant.now()));
        paymentRepository.save(new Payment(UUID.randomUUID(), merchant, new BigDecimal("30.00"), PaymentStatus.FAILED, "P3", Instant.now()));
        assertEquals(new BigDecimal("150.00"), paymentService.calculateCapturedTotal(merchant.getId()));
    }
}
