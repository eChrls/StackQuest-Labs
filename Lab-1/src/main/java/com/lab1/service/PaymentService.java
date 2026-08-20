package com.lab1.service;

import com.lab1.domain.Merchant;
import com.lab1.domain.Payment;
import com.lab1.domain.PaymentStatus;
import com.lab1.exception.ResourceNotFoundException;
import com.lab1.repository.MerchantRepository;
import com.lab1.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final MerchantRepository merchantRepository;

    public PaymentService(PaymentRepository paymentRepository, MerchantRepository merchantRepository) {
        this.paymentRepository = paymentRepository;
        this.merchantRepository = merchantRepository;
    }

    @Transactional
    public Payment createPayment(String merchantId, BigDecimal amount, PaymentStatus status, String providerReference) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("Merchant not found: " + merchantId));
        return paymentRepository.save(new Payment(UUID.randomUUID(), merchant, amount, status, providerReference, Instant.now()));
    }

    @Transactional(readOnly = true)
    public Payment getPayment(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByMerchant(String merchantId, PaymentStatus status) {
        if (status == null) {
            return paymentRepository.findByMerchant_IdOrderByCreatedAtDesc(merchantId);
        }
        return paymentRepository.findByMerchant_IdOrderByCreatedAtDesc(merchantId);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateCapturedTotal(String merchantId) {
        BigDecimal total = BigDecimal.ZERO;
        List<Payment> payments = paymentRepository.findByMerchant_IdOrderByCreatedAtDesc(merchantId);
        for (Payment payment : payments) {
            if (payment.getStatus() == PaymentStatus.CAPTURED && payment.getMerchant().getId().equals(merchantId)) {
                total.add(payment.getAmount());
            }
        }
        return total;
    }
}
