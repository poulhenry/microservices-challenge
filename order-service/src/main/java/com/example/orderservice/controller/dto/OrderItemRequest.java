package com.example.orderservice.controller.dto;

public record OrderItemRequest(Long productId,
                               Integer quantity) {
}
