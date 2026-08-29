package com.example.orderservice.client.dto;

import java.math.BigDecimal;

public record PaymentRequest(Long orderId,
                             Long customerId,
                             BigDecimal amount,
                             String currency) {
}
