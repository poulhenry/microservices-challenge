package com.example.orderservice.service;


import com.example.orderservice.client.PaymentClient;
import com.example.orderservice.client.ProductClient;
import com.example.orderservice.client.dto.PaymentRequest;
import com.example.orderservice.client.dto.PaymentStatus;
import com.example.orderservice.client.dto.ProductAvailabilityItemResponse;
import com.example.orderservice.controller.dto.OrderRequest;
import com.example.orderservice.controller.dto.OrderResponse;
import com.example.orderservice.controller.dto.ProductAvailabilityRequest;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderItem;
import com.example.orderservice.domain.OrderRepository;
import com.example.orderservice.domain.OrderStatus;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final PaymentClient paymentClient;

    public OrderService(OrderRepository orderRepository, ProductClient productClient, PaymentClient paymentClient) {
        this.orderRepository = orderRepository;
        this.productClient = productClient;
        this.paymentClient = paymentClient;
    }

    public OrderResponse create(OrderRequest data) {
        var response = productClient.checkQuantityAvailability(new ProductAvailabilityRequest(data.items()));

        if (!response.available()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock");
        }

        var order = new Order();
        order.setCustomerId(data.customerId());
        order.setStatus(OrderStatus.PENDING);
        order.setIdempotencyKey(UUID.randomUUID());

        var orderItems = response
                .items()
                .stream()
                .map(item -> {
                    var orderItem = new OrderItem();

                    orderItem.setProductName(item.name());
                    orderItem.setQuantity(item.quantity());
                    orderItem.setProductId(item.id());
                    orderItem.setUnitPrice(item.unitPrice());

                    return orderItem;
                })
                .toList();

        order.setItems(orderItems);

        var total = response
                .items()
                .stream()
                .map(ProductAvailabilityItemResponse::unitPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);

        getPaymentOrder(order);

        orderRepository.save(order);

        return OrderResponse.from(order);
    }

    private void getPaymentOrder(Order order) {
        var response = paymentClient.createPayment(
                order.getIdempotencyKey(),
                new PaymentRequest(order.getId(), order.getCustomerId(), order.getTotalAmount(), "BRL")
        );

        if (response.getBody() != null) {
            if (PaymentStatus.DECLINED.equals(response.getBody().status())) {
                order.setStatus(OrderStatus.PAYMENT_FAILED);
            } else {
                order.setStatus(OrderStatus.PAID);
            }
        }
    }
}