package com.lab3.dto;
import java.util.List;
public record PaymentPageDto(List<PaymentDto> content, int page, int size, long totalElements, int totalPages) {}
