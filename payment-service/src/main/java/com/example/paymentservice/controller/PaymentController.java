package com.example.paymentservice.controller;

import com.example.paymentservice.controller.dto.PaymentRequest;
import com.example.paymentservice.controller.dto.PaymentResponse;
import com.example.paymentservice.service.PaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@RequestHeader("Idempotency-Key") UUID key,
                                                         @RequestBody PaymentRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(PaymentResponse.from(paymentService.create(key, request)));
    }
}