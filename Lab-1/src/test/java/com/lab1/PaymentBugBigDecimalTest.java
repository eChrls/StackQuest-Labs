package com.lab1;

import com.lab1.domain.Merchant;
import com.lab1.domain.Payment;
import com.lab1.domain.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentBugBigDecimalTest {
    @Test void capturedTotalShouldBe150() {
        BigDecimal total = BigDecimal.ZERO;
        total.add(new BigDecimal("100.00"));
        total.add(new BigDecimal("50.00"));
        assertEquals(new BigDecimal("150.00"), total);
    }
}
