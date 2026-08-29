package com.example.orderservice.controller.dto;

import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(Long id,
                            Long customerId,
                            OrderStatus status,
                            BigDecimal totalAmount,
                            List<ProductResponse> items) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getTotalAmount(),
                order
                        .getItems()
                        .stream()
                        .map(item -> new ProductResponse(
                                item.getProductId(),
                                item.getProductName(),
                                item.getQuantity(),
                                item.getUnitPrice())
                        )
                        .toList()
        );
    }
}
