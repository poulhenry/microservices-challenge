package com.example.productservice.controller.dto;

import java.util.List;

public record ProductAvailabilityRequest(List<ProductAvailabilityItemRequest> items) {
}