package com.lab10.advanced;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferResponse(Long id, String userId, BigDecimal amount, String status, Instant createdAt) { }
