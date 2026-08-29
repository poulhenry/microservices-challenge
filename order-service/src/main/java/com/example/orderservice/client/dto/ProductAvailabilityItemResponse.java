package com.example.orderservice.client.dto;

import java.math.BigDecimal;

public record ProductAvailabilityItemResponse(Long id,
                                              String name,
                                              Integer quantity,
                                              BigDecimal unitPrice,
                                              boolean available) {
}
