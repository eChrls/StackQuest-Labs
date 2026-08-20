package com.lab3.controller;
import com.lab3.dto.MerchantDto;
import com.lab3.dto.PaymentPageDto;
import com.lab3.service.MerchantService;
import com.lab3.service.PaymentService;
import com.lab3.domain.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/merchants") public class MerchantController {
 private final MerchantService merchants; private final PaymentService payments;
 public MerchantController(MerchantService merchants,PaymentService payments){this.merchants=merchants;this.payments=payments;}
 @GetMapping public List<MerchantDto> list(){return merchants.list();}
 @GetMapping("/{merchantId}/payments") public PaymentPageDto payments(@PathVariable String merchantId,@RequestParam(required=false) PaymentStatus status,@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="5") int size){Page<?> result=payments.list(merchantId,status,page,size);return new PaymentPageDto((List)result.getContent(),result.getNumber(),result.getSize(),result.getTotalElements(),result.getTotalPages());}
}
