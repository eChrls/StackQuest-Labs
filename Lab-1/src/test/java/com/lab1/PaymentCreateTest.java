package com.lab1;

import com.lab1.domain.Merchant;
import com.lab1.domain.Payment;
import com.lab1.domain.PaymentStatus;
import com.lab1.repository.MerchantRepository;
import com.lab1.repository.PaymentRepository;
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
class PaymentCreateTest {
    @Autowired private MerchantRepository merchantRepository;
    @Autowired private PaymentRepository paymentRepository;

    @Test void shouldPersistCreatedPayment() {
        Merchant merchant = merchantRepository.save(new Merchant("M-CREATE", "Create Merchant"));
        Payment payment = new Payment(UUID.randomUUID(), merchant, new BigDecimal("15.75"), PaymentStatus.CAPTURED, "P-NEW", Instant.now());
        Payment saved = paymentRepository.save(payment);
        assertEquals("P-NEW", saved.getProviderReference());
    }
}
