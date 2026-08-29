package com.example.orderservice.client.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponse(UUID paymentId,
                              Long orderId,
                              PaymentStatus status,
                              BigDecimal amount,
                              String currency) {
}
