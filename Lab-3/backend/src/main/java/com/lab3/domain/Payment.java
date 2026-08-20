package com.lab3.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Payment {
    @Id private UUID id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false) private Merchant merchant;
    @Column(nullable=false, precision=12, scale=2) private BigDecimal amount;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private PaymentStatus status;
    @Column(nullable=false) private Instant createdAt;
    private String description;
    protected Payment() {}
    public Payment(UUID id, Merchant merchant, BigDecimal amount, PaymentStatus status, Instant createdAt, String description) { this.id=id; this.merchant=merchant; this.amount=amount; this.status=status; this.createdAt=createdAt; this.description=description; }
    public UUID getId(){return id;} public Merchant getMerchant(){return merchant;} public BigDecimal getAmount(){return amount;} public PaymentStatus getStatus(){return status;} public Instant getCreatedAt(){return createdAt;} public String getDescription(){return description;}
    public void setStatus(PaymentStatus status){this.status=status;}
}
