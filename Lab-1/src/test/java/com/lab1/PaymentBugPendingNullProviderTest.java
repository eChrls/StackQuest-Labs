package com.lab1;

import com.lab1.domain.Merchant;
import com.lab1.domain.Payment;
import com.lab1.domain.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNull;

class PaymentBugPendingNullProviderTest {
    @Test void providerReferenceShouldBeNullForPending() {
        Payment payment = new Payment(UUID.randomUUID(), new Merchant("M1", "Merchant One"), new BigDecimal("25.00"), PaymentStatus.PENDING, null, Instant.now());
        assertNull(payment.getProviderReference());
    }
}
