package com.example.orderservice.service;


import com.example.orderservice.client.PaymentClient;
import com.example.orderservice.client.ProductClient;
import com.example.orderservice.client.dto.PaymentRequest;
import com.example.orderservice.client.dto.PaymentStatus;
import com.example.orderservice.controller.dto.OrderItemRequest;
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
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
        Map<Long, Integer> quantityByProductId = data.items()
                .stream()
                .collect(Collectors.toMap(
                        OrderItemRequest::productId,
                        OrderItemRequest::quantity,
                        Integer::sum
                ));

        var consolidatedItems = quantityByProductId.entrySet()
                .stream()
                .map(entry -> new OrderItemRequest(entry.getKey(), entry.getValue()))
                .toList();

        var response = productClient.checkQuantityAvailability(new ProductAvailabilityRequest(consolidatedItems));

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
                    var quantityRequest = quantityByProductId.get(item.id());

                    orderItem.setProductName(item.name());
                    orderItem.setQuantity(quantityRequest);
                    orderItem.setProductId(item.id());
                    orderItem.setUnitPrice(item.unitPrice());
                    orderItem.setOrder(order);

                    return orderItem;
                })
                .toList();

        order.setItems(orderItems);

        var total = response
                .items()
                .stream()
                .map(item -> {
                    var quantity = quantityByProductId.get(item.id());

                    return item.unitPrice().multiply(BigDecimal.valueOf(quantity));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setTotalAmount(total);

        orderRepository.save(order);

        getPaymentOrder(order);

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

        orderRepository.save(order);
    }
}