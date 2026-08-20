package com.lab3.service;

import com.lab3.domain.*;
import com.lab3.dto.PaymentDto;
import com.lab3.exception.NotFoundException;
import com.lab3.exception.InvalidTransitionException;
import com.lab3.mapper.PaymentMapper;
import com.lab3.repository.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.*;

@Service
public class PaymentService {
    private final PaymentRepository payments; private final PaymentAuditRepository audits; private final MerchantRepository merchants; private final PaymentMapper mapper;
    public PaymentService(PaymentRepository payments, PaymentAuditRepository audits, MerchantRepository merchants, PaymentMapper mapper){this.payments=payments;this.audits=audits;this.merchants=merchants;this.mapper=mapper;}
    public Page<PaymentDto> list(String merchantId, PaymentStatus status, int page, int size){
        if(!merchants.existsById(merchantId)) throw new NotFoundException("Merchant not found");
        Pageable pageable=PageRequest.of(page,size,Sort.by(Sort.Direction.DESC,"createdAt"));
        Page<Payment> result=status==null?payments.findByMerchantId(merchantId,pageable):payments.findByMerchantIdAndStatus(merchantId,status,pageable);
        return result.map(mapper::toDto);
    }
    public PaymentDto find(UUID id){return mapper.toDto(payments.findById(id).orElseThrow(()->new NotFoundException("Payment not found")));}
    public void updateStatus(UUID id, PaymentStatus requested){
        Payment payment=payments.findById(id).orElseThrow(()->new NotFoundException("Payment not found"));
        if(payment.getStatus()!=PaymentStatus.PENDING && requested!=PaymentStatus.CAPTURED) throw new InvalidTransitionException();
        PaymentStatus previous=payment.getStatus(); payment.setStatus(requested); payments.save(payment);
        audits.save(new PaymentAudit(UUID.randomUUID(),id,previous,requested,Instant.now()));
    }
    public List<PaymentAudit> audit(UUID id){if(!payments.existsById(id)) throw new NotFoundException("Payment not found"); return audits.findByPaymentIdOrderByCreatedAtDesc(id);}
}
