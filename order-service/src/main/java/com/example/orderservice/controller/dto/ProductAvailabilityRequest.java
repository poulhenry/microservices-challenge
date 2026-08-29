package com.example.orderservice.controller.dto;

import java.util.List;

public record ProductAvailabilityRequest(List<OrderItemRequest> items) {
}
