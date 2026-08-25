package com.lab10.advanced;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transfers")
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idempotency_key", nullable = false)
    private String idempotencyKey;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private String status;

    protected Transfer() {
    }

    public Transfer(String idempotencyKey, String userId, BigDecimal amount, Instant createdAt) {
        this.idempotencyKey = idempotencyKey;
        this.userId = userId;
        this.amount = amount;
        this.createdAt = createdAt;
        this.status = "PENDING";
    }

    public Long getId() { return id; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public String getUserId() { return userId; }
    public BigDecimal getAmount() { return amount; }
    public Instant getCreatedAt() { return createdAt; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
