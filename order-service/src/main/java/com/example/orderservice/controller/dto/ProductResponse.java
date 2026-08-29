package com.example.orderservice.controller.dto;

import java.math.BigDecimal;

public record ProductResponse(Long productId,
                              String productName,
                              Integer quantity,
                              BigDecimal unitPrice) {
}
