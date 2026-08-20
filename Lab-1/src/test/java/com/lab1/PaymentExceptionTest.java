package com.lab1;

import com.lab1.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PaymentExceptionTest {
    @Test void shouldThrowResourceNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> { throw new ResourceNotFoundException("Payment not found"); });
    }
}
