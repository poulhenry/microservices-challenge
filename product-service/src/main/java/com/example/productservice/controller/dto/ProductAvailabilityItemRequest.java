package com.example.productservice.controller.dto;

public record ProductAvailabilityItemRequest(Long productId,
                                             Integer quantity) {
}