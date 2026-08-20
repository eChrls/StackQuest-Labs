package com.lab3;

import com.lab3.domain.*;
import com.lab3.dto.PaymentDto;
import com.lab3.mapper.PaymentMapper;
import com.lab3.repository.*;
import com.lab3.service.PaymentService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest @AutoConfigureMockMvc @Transactional
class PaymentChallengeTest {
 @Autowired MockMvc mvc; @Autowired PaymentRepository payments; @Autowired MerchantRepository merchants; @Autowired PaymentAuditRepository audits;
 static final UUID P1=UUID.fromString("00000000-0000-0000-0000-000000000001");
 @Test void merchantsEndpointListsSeedData() throws Exception { mvc.perform(get("/api/merchants")).andExpect(status().isOk()).andExpect(jsonPath("$[0].id").value("M1")); }
 @Test void merchantHasTwelvePayments() throws Exception { mvc.perform(get("/api/merchants/M1/payments?size=5")).andExpect(status().isOk()).andExpect(jsonPath("$.totalElements").value(12)); }
 @Test void pageMetadataIsZeroBased() throws Exception { mvc.perform(get("/api/merchants/M1/payments?page=1&size=5")).andExpect(status().isOk()).andExpect(jsonPath("$.page").value(1)); }
 @Test void defaultPageSizeIsFive() throws Exception { mvc.perform(get("/api/merchants/M1/payments")).andExpect(jsonPath("$.size").value(5)); }
 @Test void statusFilterWorks() throws Exception { mvc.perform(get("/api/merchants/M1/payments?status=PENDING&size=20")).andExpect(jsonPath("$.totalElements").value(3)); }
 @Test void individualPaymentContainsId() throws Exception { mvc.perform(get("/api/payments/"+P1)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(P1.toString())); }
 @Test void individualPaymentContainsAmount() throws Exception { mvc.perform(get("/api/payments/"+P1)).andExpect(jsonPath("$.amount").value(100.0)); }
 @Test void individualPaymentContainsStatus() throws Exception { mvc.perform(get("/api/payments/"+P1)).andExpect(jsonPath("$.status").value("CAPTURED")); }
 @Test void missingPaymentIsNotFound() throws Exception { mvc.perform(get("/api/payments/00000000-0000-0000-0000-999999999999")).andExpect(status().isNotFound()); }
 @Test void pendingCanBeCaptured() throws Exception { mvc.perform(patch("/api/payments/00000000-0000-0000-0000-000000000003/status").contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"CAPTURED\"}")).andExpect(status().isNoContent()); }
 @Test void pendingCanBeFailed() throws Exception { mvc.perform(patch("/api/payments/00000000-0000-0000-0000-000000000007/status").contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"FAILED\"}")).andExpect(status().isNoContent()); }
 @Test void auditEndpointReturnsList() throws Exception { mvc.perform(get("/api/payments/"+P1+"/audit")).andExpect(status().isOk()).andExpect(jsonPath("$").isArray()); }
 @Test void updateCreatesAudit() throws Exception { UUID id=UUID.fromString("00000000-0000-0000-0000-000000000010"); mvc.perform(patch("/api/payments/"+id+"/status").contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"CAPTURED\"}")); assertTrue(audits.findByPaymentIdOrderByCreatedAtDesc(id).size()>=1); }
 @Test void invalidStatusPayloadIsRejected() throws Exception { mvc.perform(patch("/api/payments/"+P1+"/status").contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"NOPE\"}")).andExpect(status().isBadRequest()); }
 @Test void merchantProjectionIsCorrect() { assertEquals("Northstar Market", merchants.findById("M1").orElseThrow().getName()); }
 @Test void mapperUsesMerchantName() { Payment payment=payments.findById(P1).orElseThrow(); PaymentDto dto=new PaymentMapper().toDto(payment); assertEquals("Northstar Market",dto.merchantName()); }
 @Test void auditFailureDoesNotCommitPayment() {
  Payment payment=new Payment(P1,new Merchant("M1","Northstar Market",true),BigDecimal.TEN,PaymentStatus.PENDING,Instant.now(),null);
  PaymentRepository paymentRepo=mock(PaymentRepository.class); PaymentAuditRepository auditRepo=mock(PaymentAuditRepository.class); MerchantRepository merchantRepo=mock(MerchantRepository.class);
  when(paymentRepo.findById(P1)).thenReturn(Optional.of(payment)); when(auditRepo.save(any())).thenThrow(new IllegalStateException("audit unavailable"));
  PaymentService service=new PaymentService(paymentRepo,auditRepo,merchantRepo,new PaymentMapper());
  assertThrows(IllegalStateException.class,()->service.updateStatus(P1,PaymentStatus.CAPTURED)); assertEquals(PaymentStatus.PENDING,payment.getStatus());
 }
 @Test void failedPaymentCannotBeCaptured() throws Exception { UUID id=UUID.fromString("00000000-0000-0000-0000-000000000005"); mvc.perform(patch("/api/payments/"+id+"/status").contentType(MediaType.APPLICATION_JSON).content("{\"status\":\"CAPTURED\"}")).andExpect(status().isConflict()); }
}
