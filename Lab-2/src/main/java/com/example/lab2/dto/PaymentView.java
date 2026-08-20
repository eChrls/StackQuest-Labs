package com.example.lab2.dto;

import com.example.lab2.domain.Payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/** Stable read shape kept separate from the persistence entity for future characterization work. */
public record PaymentView(UUID id, BigDecimal amount, String status, String providerReference,
                          boolean processed, Instant createdAt) {
    public static PaymentView from(Payment payment) {
        return new PaymentView(payment.getId(), payment.getAmount(), payment.getStatus(),
                payment.getProviderReference(), payment.isProcessed(), payment.getCreatedAt());
    }
}
