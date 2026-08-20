package com.example.lab2.domain;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
public class Payment {
  @Id private UUID id;
  @ManyToOne(fetch=FetchType.LAZY, optional=false) private Merchant merchant;
  @Column(precision=19, scale=2, nullable=false) private BigDecimal amount;
  private String status;
  private String providerReference;
  private boolean processed;
  private Instant createdAt;
  protected Payment() {}
  public Payment(UUID id, Merchant merchant, BigDecimal amount, String status, String providerReference, boolean processed, Instant createdAt) { this.id=id;this.merchant=merchant;this.amount=amount;this.status=status;this.providerReference=providerReference;this.processed=processed;this.createdAt=createdAt; }
  public UUID getId(){return id;} @JsonIgnore public Merchant getMerchant(){return merchant;} public BigDecimal getAmount(){return amount;} public String getStatus(){return status;} public String getProviderReference(){return providerReference;} public boolean isProcessed(){return processed;} public Instant getCreatedAt(){return createdAt;}
  public void setStatus(String status){this.status=status;} public void setProcessed(boolean processed){this.processed=processed;}
}
