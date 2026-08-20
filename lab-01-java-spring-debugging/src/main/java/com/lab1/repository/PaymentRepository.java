package com.lab1.repository;

import com.lab1.domain.Payment;
import com.lab1.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByMerchant_IdOrderByCreatedAtDesc(String merchantId);
    List<Payment> findByMerchant_IdAndStatusOrderByCreatedAtDesc(String merchantId, PaymentStatus status);

    @Query("select coalesce(sum(p.amount), 0) from Payment p where p.merchant.id = :merchantId and p.status = :status")
    BigDecimal sumByMerchantIdAndStatus(@Param("merchantId") String merchantId, @Param("status") PaymentStatus status);
}
