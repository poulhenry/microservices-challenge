package com.example.orderservice.client;

import com.example.orderservice.client.dto.ItemsAvailabilityResponse;
import com.example.orderservice.controller.dto.ProductAvailabilityRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "ProductClient", url = "${product-service.url}")
public interface ProductClient {

    @PostMapping("/availability")
    ItemsAvailabilityResponse checkQuantityAvailability(ProductAvailabilityRequest request);
}
