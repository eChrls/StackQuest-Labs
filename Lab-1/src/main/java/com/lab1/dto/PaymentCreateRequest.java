package com.lab1.dto;

import com.lab1.domain.PaymentStatus;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PaymentCreateRequest {
    @NotNull
    private String merchantId;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private PaymentStatus status;

    private String providerReference;

    public PaymentCreateRequest() {}

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public PaymentStatus getStatus() { return status; }
    public void setStatus(PaymentStatus status) { this.status = status; }
    public String getProviderReference() { return providerReference; }
    public void setProviderReference(String providerReference) { this.providerReference = providerReference; }
}
