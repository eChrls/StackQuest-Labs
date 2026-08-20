package com.example.lab2;

import com.example.lab2.domain.Merchant;
import com.example.lab2.domain.Payment;
import com.example.lab2.domain.ProcessingAudit;
import com.example.lab2.domain.repository.MerchantRepository;
import com.example.lab2.domain.repository.PaymentRepository;
import com.example.lab2.domain.repository.ProcessingAuditRepository;
import com.example.lab2.exception.DomainException;
import com.example.lab2.service.LegacyPaymentProcessor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PaymentIntegrationTest {
    private static final UUID CAPTURED = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID PENDING = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID FAILED = UUID.fromString("33333333-3333-3333-3333-333333333333");
    private static final UUID REFUNDED = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID INACTIVE = UUID.fromString("55555555-5555-5555-5555-555555555555");

    @Autowired PaymentRepository payments;
    @Autowired MerchantRepository merchants;
    @Autowired ProcessingAuditRepository audits;
    @Autowired LegacyPaymentProcessor processor;

    @Test void seedContainsThreeMerchants() { assertEquals(3, merchants.count()); }
    @Test void seedContainsFivePayments() { assertEquals(5, payments.count()); }
    @Test void capturedPaymentHasExpectedAmount() { assertEquals("100.00", payments.findById(CAPTURED).orElseThrow().getAmount().toPlainString()); }
    @Test void capturedPaymentHasProviderReference() { assertNotNull(payments.findById(CAPTURED).orElseThrow().getProviderReference()); }
    @Test void pendingPaymentIsNotProcessed() { assertFalse(payments.findById(PENDING).orElseThrow().isProcessed()); }
    @Test void failedPaymentIsNotProcessed() { assertFalse(payments.findById(FAILED).orElseThrow().isProcessed()); }
    @Test void refundedPaymentIsAlreadyProcessed() { assertTrue(payments.findById(REFUNDED).orElseThrow().isProcessed()); }
    @Test void activeMerchantCanBeLoaded() { assertTrue(merchants.findById("M1").orElseThrow().isActive()); }
    @Test void inactiveMerchantCannotBeProcessed() { assertFalse(merchants.findById("M3").orElseThrow().isActive()); assertThrows(DomainException.class, () -> processor.processPayment(INACTIVE)); }
    @Test void merchantPaymentQueryReturnsExpectedRows() { assertEquals(2, payments.findByMerchantId("M1").size()); }
    @Test void nonCapturedPaymentIsRejectedByProcessor() { assertThrows(DomainException.class, () -> processor.processPayment(PENDING)); }
    @Test void capturedSeedPaymentIsProcessed() { assertDoesNotThrow(() -> processor.processPayment(CAPTURED)); }
}
