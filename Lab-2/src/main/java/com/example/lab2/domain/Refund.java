package com.example.lab2.domain;
import jakarta.persistence.*; import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
@Entity public class Refund { @Id private UUID id; @ManyToOne(fetch=FetchType.LAZY) private Payment payment; private BigDecimal amount; private Instant createdAt; protected Refund(){} public Refund(UUID id,Payment payment,BigDecimal amount,Instant createdAt){this.id=id;this.payment=payment;this.amount=amount;this.createdAt=createdAt;} }
