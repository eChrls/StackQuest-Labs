package com.lab10.advanced;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private final TransferService service;

    public TransferController(TransferService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody CreateTransferRequest request,
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Idempotency-Key header is required");
            return ResponseEntity.badRequest().body(error);
        }
        TransferCreation result = service.createTransfer(request.userId(), request.amount(), idempotencyKey);
        return ResponseEntity.status(result.created() ? HttpStatus.CREATED : HttpStatus.OK).body(result.transfer());
    }

    @GetMapping
    public List<Transfer> list() {
        return service.listTransfers();
    }

    @GetMapping("/{id}")
    public Transfer get(@PathVariable long id) {
        return service.getTransfer(id);
    }

    @PatchMapping("/{id}/status")
    public Transfer updateStatus(@PathVariable long id, @RequestBody UpdateTransferStatusRequest request) {
        return service.updateStatus(id, request.status());
    }
}
