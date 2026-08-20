package com.lab3.repository;
import com.lab3.domain.PaymentAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PaymentAuditRepository extends JpaRepository<PaymentAudit,UUID> { List<PaymentAudit> findByPaymentIdOrderByCreatedAtDesc(UUID paymentId); }
