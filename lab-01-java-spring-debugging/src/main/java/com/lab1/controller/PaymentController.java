package com.lab1.controller;

import com.lab1.domain.Payment;
import com.lab1.domain.PaymentStatus;
import com.lab1.dto.CapturedTotalResponse;
import com.lab1.dto.PaymentCreateRequest;
import com.lab1.dto.PaymentResponse;
import com.lab1.mapper.PaymentMapper;
import com.lab1.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentMapper paymentMapper;

    public PaymentController(PaymentService paymentService, PaymentMapper paymentMapper) {
        this.paymentService = paymentService;
        this.paymentMapper = paymentMapper;
    }

    @PostMapping("/payments")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentCreateRequest request) {
        Payment payment = paymentService.createPayment(request.getMerchantId(), request.getAmount(), request.getStatus(), request.getProviderReference());
        return ResponseEntity.created(URI.create("/api/payments/" + payment.getId())).body(paymentMapper.toResponse(payment));
    }

    @GetMapping("/payments/{paymentId}")
    public PaymentResponse getPayment(@PathVariable UUID paymentId) {
        return paymentMapper.toResponse(paymentService.getPayment(paymentId));
    }

    @GetMapping("/merchants/{merchantId}/payments")
    public List<PaymentResponse> getPaymentsByMerchant(@PathVariable String merchantId, @RequestParam(required = false) String status) {
        PaymentStatus paymentStatus = status == null ? null : PaymentStatus.valueOf(status);
        return paymentService.getPaymentsByMerchant(merchantId, paymentStatus).stream().map(paymentMapper::toResponse).toList();
    }

    @GetMapping("/merchants/{merchantId}/captured-total")
    public CapturedTotalResponse getCapturedTotal(@PathVariable String merchantId) {
        return new CapturedTotalResponse(merchantId, paymentService.calculateCapturedTotal(merchantId));
    }
}
