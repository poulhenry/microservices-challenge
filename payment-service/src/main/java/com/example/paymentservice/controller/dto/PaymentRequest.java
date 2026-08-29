package com.example.paymentservice.controller.dto;

import java.math.BigDecimal;

public record PaymentRequest(Long orderId,
                             Long customerId,
                             BigDecimal amount,
                             String currency) {
}