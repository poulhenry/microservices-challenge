package com.example.orderservice.client;

import com.example.orderservice.client.dto.PaymentRequest;
import com.example.orderservice.client.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(name = "PaymentClient", url = "${payment-service.url}")
public interface PaymentClient {

    @PostMapping
    ResponseEntity<PaymentResponse> createPayment(@RequestHeader("Idempotency-Key") UUID key,
                                                  @RequestBody PaymentRequest request);
}
