package com.example.paymentservice.service;

import com.example.paymentservice.controller.dto.PaymentRequest;
import com.example.paymentservice.domain.Payment;
import com.example.paymentservice.domain.PaymentRepository;
import com.example.paymentservice.domain.PaymentStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment create(UUID key, PaymentRequest request) {
        var status = ThreadLocalRandom.current().nextBoolean() ? PaymentStatus.APPROVED : PaymentStatus.DECLINED;

        var payment = new Payment(
                request.orderId(),
                request.customerId(),
                request.amount(),
                status,
                key
        );

        return paymentRepository.save(payment);
    }
}