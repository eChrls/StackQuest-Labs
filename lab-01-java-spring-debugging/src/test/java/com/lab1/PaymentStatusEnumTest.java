package com.lab1;

import com.lab1.domain.PaymentStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentStatusEnumTest {
    @Test void hasFourStatuses() {
        assertEquals(4, PaymentStatus.values().length);
    }
}
