package com.lab10.advanced;

import java.math.BigDecimal;

public record CreateTransferRequest(String userId, BigDecimal amount) {
}
