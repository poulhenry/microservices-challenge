package com.example.productservice.controller.dto;

import java.util.List;

public record ProductAvailabilityResponse(boolean available,
                                          List<ProductAvailabilityItemResponse> items) {
}