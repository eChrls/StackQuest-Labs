package com.lab1;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentAmountCalculationTest {
    @Test void addsNormally() {
        BigDecimal total = BigDecimal.ZERO;
        total = total.add(new BigDecimal("100.00"));
        total = total.add(new BigDecimal("50.00"));
        assertEquals(new BigDecimal("150.00"), total);
    }
}
