package com.lab3.dto;
import com.lab3.domain.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
public record PaymentDto(UUID id,String merchantId,String merchantName,BigDecimal amount,PaymentStatus status,Instant createdAt,String description) {}
