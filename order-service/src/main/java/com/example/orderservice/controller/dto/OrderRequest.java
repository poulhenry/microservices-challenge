package com.example.orderservice.controller.dto;

import java.util.List;

public record OrderRequest(Long customerId,
                           List<OrderItemRequest> items) {
}
