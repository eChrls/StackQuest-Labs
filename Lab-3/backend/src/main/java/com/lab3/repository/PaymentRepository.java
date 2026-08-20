package com.lab3.repository;
import com.lab3.domain.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface PaymentRepository extends JpaRepository<Payment,UUID> {
    Page<Payment> findByMerchantId(String merchantId, Pageable pageable);
    Page<Payment> findByMerchantIdAndStatus(String merchantId, PaymentStatus status, Pageable pageable);
}
