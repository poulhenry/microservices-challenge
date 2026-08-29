package com.example.orderservice.client.dto;

import java.util.List;

public record ItemsAvailabilityResponse(boolean available,
                                        List<ProductAvailabilityItemResponse> items) {
}
