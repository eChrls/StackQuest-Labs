package com.example.lab2;

import com.example.lab2.domain.Merchant;
import com.example.lab2.domain.Payment;
import com.example.lab2.domain.ProcessingAudit;
import com.example.lab2.domain.repository.MerchantRepository;
import com.example.lab2.domain.repository.PaymentRepository;
import com.example.lab2.domain.repository.ProcessingAuditRepository;
import com.example.lab2.exception.DomainException;
import com.example.lab2.notification.MerchantNotificationService;
import com.example.lab2.service.LegacyPaymentProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LegacyPaymentProcessorTest {
    @Mock PaymentRepository payments;
    @Mock MerchantRepository merchants;
    @Mock ProcessingAuditRepository audits;
    @Mock MerchantNotificationService notifications;
    LegacyPaymentProcessor processor;
    Merchant active;
    UUID id;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        processor = new LegacyPaymentProcessor(payments, merchants, audits, notifications);
        active = new Merchant("M1", "Acme", true);
        id = UUID.randomUUID();
    }

    private Payment payment(String status, String providerReference) {
        return new Payment(id, active, new BigDecimal("10.00"), status, providerReference, false, Instant.now());
    }

    private void found(Payment payment) {
        when(payments.findById(id)).thenReturn(Optional.of(payment));
        when(merchants.findById("M1")).thenReturn(Optional.of(active));
        when(payments.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test void pendingPaymentsAreRejected() { found(payment("PENDING", null)); assertThrows(DomainException.class, () -> processor.processPayment(id)); }
    @Test void failedPaymentsAreRejected() { found(payment("FAILED", null)); assertThrows(DomainException.class, () -> processor.processPayment(id)); }
    @Test void refundedPaymentsAreRejected() { found(payment("REFUNDED", "ref")); assertThrows(DomainException.class, () -> processor.processPayment(id)); }
    @Test void missingPaymentsAreRejected() { when(payments.findById(id)).thenReturn(Optional.empty()); assertThrows(DomainException.class, () -> processor.processPayment(id)); }
    @Test void missingMerchantsAreRejected() { Payment payment = payment("CAPTURED", null); when(payments.findById(id)).thenReturn(Optional.of(payment)); when(merchants.findById("M1")).thenReturn(Optional.empty()); assertThrows(DomainException.class, () -> processor.processPayment(id)); }
    @Test void auditIsWrittenForAValidPayment() { Payment payment = payment("CAPTURED", "ref"); found(payment); processor.processPayment(id); verify(audits).save(any(ProcessingAudit.class)); verify(notifications, times(1)).notifyProcessed(payment); }
    @Test void auditFailureDoesNotCompletePayment() { Payment payment = payment("CAPTURED", null); found(payment); doThrow(new RuntimeException("audit unavailable")).when(audits).save(any()); assertFalse(processor.processPayment(id).isProcessed()); }
    @Test void inactiveMerchantIsMarkedInactive() { assertFalse(new Merchant("M1", "Old", false).isActive()); }
}
