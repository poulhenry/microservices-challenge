package com.example.productservice.controller.dto;

import java.math.BigDecimal;

public record CreateProductRequest(String productName,
                                   Integer quantity,
                                   BigDecimal unitPrice) {
}