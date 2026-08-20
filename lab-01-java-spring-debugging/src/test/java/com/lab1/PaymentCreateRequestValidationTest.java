package com.lab1;

import com.lab1.dto.PaymentCreateRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentCreateRequestValidationTest {
    @Test void validationRequiresMerchantAndStatus() {
        PaymentCreateRequest request = new PaymentCreateRequest();
        request.setMerchantId("M1");
        request.setAmount(new BigDecimal("10.00"));
        request.setStatus(com.lab1.domain.PaymentStatus.CAPTURED);

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        assertEquals(0, validator.validate(request).size());
    }
}
