package com.lab10.advanced;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class InvalidStateTransitionTest {
    @Autowired TransferRepository repository;
    @Autowired TransferService service;

    @BeforeEach void clean() { repository.deleteAll(); }

    @Test @Tag("public")
    void completedTransferCannotReturnToPending() {
        Transfer transfer = service.createTransfer("user-1", new BigDecimal("10.00"), "state-1").transfer();
        service.updateStatus(transfer.getId(), "COMPLETED");
        assertThrows(InvalidTransferStateException.class,
                () -> service.updateStatus(transfer.getId(), "PENDING"));
    }
}
