package com.lab3.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
public class PaymentAudit {
    @Id private UUID id;
    @Column(nullable=false) private UUID paymentId;
    @Enumerated(EnumType.STRING) private PaymentStatus previousStatus;
    @Enumerated(EnumType.STRING) private PaymentStatus newStatus;
    @Column(nullable=false) private Instant createdAt;
    protected PaymentAudit() {}
    public PaymentAudit(UUID id, UUID paymentId, PaymentStatus previousStatus, PaymentStatus newStatus, Instant createdAt) { this.id=id; this.paymentId=paymentId; this.previousStatus=previousStatus; this.newStatus=newStatus; this.createdAt=createdAt; }
    public UUID getId(){return id;} public UUID getPaymentId(){return paymentId;} public PaymentStatus getPreviousStatus(){return previousStatus;} public PaymentStatus getNewStatus(){return newStatus;} public Instant getCreatedAt(){return createdAt;}
}
