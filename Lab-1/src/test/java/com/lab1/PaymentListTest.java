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
class PaymentListTest {
    @Autowired private MerchantRepository merchantRepository;
    @Autowired private PaymentRepository paymentRepository;

    @Test void savedPaymentsAreReturnedInDescendingOrder() {
        Merchant merchant = merchantRepository.save(new Merchant("M-LIST", "List Merchant"));
        paymentRepository.save(new Payment(UUID.randomUUID(), merchant, new BigDecimal("5.00"), PaymentStatus.CAPTURED, "A", Instant.now()));
        paymentRepository.save(new Payment(UUID.randomUUID(), merchant, new BigDecimal("7.00"), PaymentStatus.CAPTURED, "B", Instant.now().plusSeconds(60)));
        assertEquals(2, paymentRepository.findByMerchant_IdOrderByCreatedAtDesc(merchant.getId()).size());
    }
}
