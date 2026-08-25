package com.lab10.advanced;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Ticket A1 — Money transfer service.
 *
 * Creates a transfer record for a user. The client is expected to send an
 * Idempotency-Key header so a retried or double-submitted request does not
 * create a second transfer.
 */
@Service
public class TransferService {

    private static final Set<String> STATUSES = Set.of("PENDING", "COMPLETED", "FAILED");

    private final TransferRepository repository;
    private final boolean referenceMode;

    public TransferService(TransferRepository repository, @Value("${lab.reference-mode:false}") boolean referenceMode) {
        this.repository = repository;
        this.referenceMode = referenceMode;
    }

    public synchronized TransferCreation createTransfer(String userId, BigDecimal amount, String idempotencyKey) {
        if (referenceMode) {
            var existing = repository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) return new TransferCreation(existing.get(), false);
        }
        Transfer transfer = new Transfer(idempotencyKey, userId, amount, Instant.now());
        return new TransferCreation(repository.save(transfer), true);
    }

    public Transfer getTransfer(long id) {
        return repository.findById(id).orElseThrow(() -> new TransferNotFoundException(id));
    }

    /** Ticket A2 baseline: validates names but not the state transition graph. */
    public Transfer updateStatus(long id, String nextStatus) {
        if (!STATUSES.contains(nextStatus)) {
            throw new InvalidTransferStateException("Unknown transfer status: " + nextStatus);
        }
        Transfer transfer = getTransfer(id);
        if (referenceMode && !"PENDING".equals(transfer.getStatus())) {
            throw new InvalidTransferStateException("A terminal transfer cannot change status");
        }
        transfer.setStatus(nextStatus);
        return repository.save(transfer);
    }

    public List<Transfer> listTransfers() {
        return repository.findAll();
    }
}
