package com.example.paymentservice.controller.dto;

import com.example.paymentservice.domain.Payment;
import com.example.paymentservice.domain.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentResponse(UUID paymentId,
                              Long orderId,
                              PaymentStatus status,
                              BigDecimal amount,
                              String currency) {

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getCurrency()
        );
    }
}