package com.lab3.controller;
import com.lab3.domain.*; import com.lab3.dto.PaymentDto; import com.lab3.service.PaymentService;
import jakarta.validation.Valid; import jakarta.validation.constraints.NotNull; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/payments") public class PaymentController {
 private final PaymentService service; public PaymentController(PaymentService service){this.service=service;}
 @GetMapping("/{id}") public PaymentDto find(@PathVariable UUID id){return service.find(id);}
 public record StatusRequest(@NotNull PaymentStatus status){}
 @PatchMapping("/{id}/status") public ResponseEntity<Void> update(@PathVariable UUID id,@Valid @RequestBody StatusRequest request){service.updateStatus(id,request.status());return ResponseEntity.noContent().build();}
 @GetMapping("/{id}/audit") public List<PaymentAudit> audit(@PathVariable UUID id){return service.audit(id);}
}
