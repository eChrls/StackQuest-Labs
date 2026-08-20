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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class PaymentBugStatusFilterTest {
    @Autowired private MerchantRepository merchantRepository;
    @Autowired private PaymentRepository paymentRepository;

    @Test void shouldReturnOnlyCapturedPayments() {
        Merchant merchant = merchantRepository.save(new Merchant("M-FILTER", "Filter Merchant"));
        paymentRepository.save(new Payment(UUID.randomUUID(), merchant, new BigDecimal("10.00"), PaymentStatus.CAPTURED, "C1", Instant.now()));
        paymentRepository.save(new Payment(UUID.randomUUID(), merchant, new BigDecimal("20.00"), PaymentStatus.FAILED, "F1", Instant.now()));
        List<Payment> payments = paymentRepository.findByMerchant_IdAndStatusOrderByCreatedAtDesc(merchant.getId(), PaymentStatus.CAPTURED);
        assertEquals(1, payments.size());
    }
}
