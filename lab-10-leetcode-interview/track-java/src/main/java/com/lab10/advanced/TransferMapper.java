package com.lab10.advanced;

import org.springframework.stereotype.Component;

@Component
public class TransferMapper {
    public TransferResponse toResponse(Transfer transfer) {
        return new TransferResponse(transfer.getId(), transfer.getUserId(), transfer.getAmount(),
                transfer.getStatus(), transfer.getCreatedAt());
    }
}
