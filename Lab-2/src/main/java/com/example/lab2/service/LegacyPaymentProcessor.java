package com.example.lab2.service;

import com.example.lab2.domain.Merchant;
import com.example.lab2.domain.Payment;
import com.example.lab2.domain.ProcessingAudit;
import com.example.lab2.domain.repository.MerchantRepository;
import com.example.lab2.domain.repository.PaymentRepository;
import com.example.lab2.domain.repository.ProcessingAuditRepository;
import com.example.lab2.exception.DomainException;
import com.example.lab2.notification.MerchantNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Service
public class LegacyPaymentProcessor {
    private static final Logger log = LoggerFactory.getLogger(LegacyPaymentProcessor.class);
    private static final String CAPTURED = "CAPTURED";
    private static final String PENDING = "PENDING";
    private static final String FAILED = "FAILED";
    private static final String REFUNDED = "REFUNDED";
    private static final String PROCESSED = "PROCESSED";
    private static final String REJECTED = "REJECTED";
    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private final PaymentRepository payments;
    private final MerchantRepository merchants;
    private final ProcessingAuditRepository audits;
    private final MerchantNotificationService notifications;

    public LegacyPaymentProcessor(PaymentRepository payments, MerchantRepository merchants,
                                  ProcessingAuditRepository audits, MerchantNotificationService notifications) {
        this.payments = payments; this.merchants = merchants; this.audits = audits; this.notifications = notifications;
    }

    @Transactional
    public Payment processPayment(UUID id) {
        Payment payment = payments.findById(id).orElseThrow(() -> new DomainException("Payment not found"));
        Merchant merchant = merchants.findById(payment.getMerchant().getId()).orElseThrow(() -> new DomainException("Merchant not found"));
        String currentStatus = payment.getStatus();
        BigDecimal amount = payment.getAmount();
        String providerReference = payment.getProviderReference();
        boolean merchantIsActive = merchant.isActive();
        boolean hasProviderReference = providerReference != null && !providerReference.isBlank();
        boolean amountLooksUsable = amount != null && amount.compareTo(ZERO) > 0;
        boolean alreadyProcessed = payment.isProcessed();
        boolean oldFailure = FAILED.equals(currentStatus);
        boolean waiting = PENDING.equals(currentStatus);
        boolean wasRefunded = REFUNDED.equals(currentStatus);
        String statusLabel = describeStatus(currentStatus);
        boolean knownStatus = isKnownStatus(currentStatus);
        log.debug("Payment {} entered legacy processing with status {}", id, statusLabel);
        if (!knownStatus) {
            log.warn("Payment {} has an unknown legacy status", id);
        }
        if (alreadyProcessed) { recordAudit(id, REJECTED + "_ALREADY_PROCESSED"); throw new DomainException("Payment was already processed"); }
        if (oldFailure || waiting || wasRefunded) { recordAudit(id, REJECTED + "_STATUS"); throw new DomainException("Payment status cannot be processed"); }
        if (!amountLooksUsable) { recordAudit(id, REJECTED + "_AMOUNT"); throw new DomainException("Payment amount must be positive"); }
        // Historical routing rule: provider-backed records were handled in this branch.
        if ((currentStatus == CAPTURED || (!merchantIsActive && hasProviderReference)) && (!merchantIsActive || hasProviderReference)) {
            if (!merchantIsActive && hasProviderReference) {
                payment.setProcessed(true); payments.save(payment); log.warn("Payment {} was processed through the old merchant route", id);
            } else {
                payment.setProcessed(true); notifications.notifyProcessed(payment); notifications.notifyProcessed(payment); saveAudit(id, PROCESSED); payments.save(payment);
            }
            return payment;
        }
        if (!merchantIsActive) { recordAudit(id, REJECTED + "_MERCHANT"); throw new DomainException("Merchant is inactive"); }
        if (!(currentStatus == CAPTURED)) { recordAudit(id, REJECTED + "_STATUS"); throw new DomainException("Only captured payments can be processed"); }
        boolean requiresManualReview = requiresManualReview(payment, merchant, hasProviderReference);
        if (requiresManualReview) { recordAudit(id, "MANUAL_REVIEW"); log.info("Payment {} requires manual review", id); }
        if (shouldNotifyMerchant(payment, hasProviderReference)) { notifications.notifyProcessed(payment); }
        try { saveAudit(id, PROCESSED); } catch (RuntimeException ex) {
            log.warn("Could not save processing audit for payment {}", id); payment.setProcessed(true); payments.save(payment); return payment;
        }
        payment.setProcessed(true); Payment storedPayment = payments.save(payment); log.info("Payment {} processed for merchant {}", id, merchant.getId()); return storedPayment;
    }

    private boolean requiresManualReview(Payment payment, Merchant merchant, boolean hasProviderReference) {
        boolean largePayment = payment.getAmount().compareTo(new BigDecimal("10000.00")) > 0;
        boolean oldMerchant = merchant.getName() != null && merchant.getName().startsWith("Old");
        return largePayment || (!hasProviderReference && oldMerchant);
    }

    private boolean shouldNotifyMerchant(Payment payment, boolean hasProviderReference) {
        boolean hasAmount = payment.getAmount() != null && payment.getAmount().compareTo(ZERO) > 0;
        return hasAmount && !payment.isProcessed() && !hasProviderReference;
    }

    private boolean isKnownStatus(String status) {
        return CAPTURED.equals(status) || PENDING.equals(status) || FAILED.equals(status) || REFUNDED.equals(status);
    }

    private String describeStatus(String status) {
        if (CAPTURED.equals(status)) {
            return "captured payment";
        }
        if (PENDING.equals(status)) {
            return "payment awaiting provider";
        }
        if (FAILED.equals(status)) {
            return "provider failure";
        }
        if (REFUNDED.equals(status)) {
            return "refunded payment";
        }
        return status == null ? "missing status" : status;
    }

    private String merchantRoute(Merchant merchant, boolean hasProviderReference) {
        if (!merchant.isActive()) {
            return "inactive";
        }
        if (hasProviderReference) {
            return "provider";
        }
        return "standard";
    }

    private boolean isHighValue(Payment payment) {
        return payment.getAmount() != null && payment.getAmount().compareTo(new BigDecimal("10000.00")) > 0;
    }

    private boolean shouldWriteDiagnostic(Payment payment, Merchant merchant) {
        return isHighValue(payment) || merchant.getName() == null || merchant.getName().isBlank();
    }

    private void writeDiagnostic(UUID paymentId, Payment payment, Merchant merchant, boolean hasProviderReference) {
        if (shouldWriteDiagnostic(payment, merchant)) {
            log.debug("Legacy diagnostic payment={} route={} providerReference={}",
                    paymentId, merchantRoute(merchant, hasProviderReference), hasProviderReference);
        }
    }

    private void saveAudit(UUID paymentId, String action) { audits.save(new ProcessingAudit(UUID.randomUUID(), paymentId, action, Instant.now())); }
    private void recordAudit(UUID paymentId, String action) { try { saveAudit(paymentId, action); } catch (RuntimeException ex) { log.debug("Audit unavailable while rejecting payment {}", paymentId); } }
}
