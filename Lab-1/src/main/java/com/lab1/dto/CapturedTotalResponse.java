package com.lab1.dto;

import java.math.BigDecimal;

public class CapturedTotalResponse {
    private String merchantId;
    private BigDecimal capturedTotal;

    public CapturedTotalResponse() {}
    public CapturedTotalResponse(String merchantId, BigDecimal capturedTotal) {
        this.merchantId = merchantId;
        this.capturedTotal = capturedTotal;
    }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public BigDecimal getCapturedTotal() { return capturedTotal; }
    public void setCapturedTotal(BigDecimal capturedTotal) { this.capturedTotal = capturedTotal; }
}
