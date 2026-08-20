package com.example.lab2.controller;
import com.example.lab2.domain.*; import com.example.lab2.domain.repository.*; import com.example.lab2.service.LegacyPaymentProcessor; import java.util.*; import org.springframework.http.*; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api") public class PaymentController {
 private final PaymentRepository payments; private final ProcessingAuditRepository audits; private final LegacyPaymentProcessor processor;
 public PaymentController(PaymentRepository p,ProcessingAuditRepository a,LegacyPaymentProcessor x){payments=p;audits=a;processor=x;}
 @GetMapping("/payments/{id}") public Payment payment(@PathVariable UUID id){return payments.findById(id).orElseThrow();}
 @PostMapping("/payments/{id}/process") public Payment process(@PathVariable UUID id){return processor.processPayment(id);}
 @GetMapping("/merchants/{id}/payments") public List<Payment> merchant(@PathVariable String id){return payments.findByMerchantId(id);}
 @GetMapping("/payments/{id}/audit") public List<ProcessingAudit> audit(@PathVariable UUID id){return audits.findByPaymentId(id);}
}
