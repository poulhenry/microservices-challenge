package com.example.productservice.service;

import com.example.productservice.controller.dto.*;
import com.example.productservice.domain.Product;
import com.example.productservice.domain.ProductRepository;
import com.example.productservice.exception.ProductNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductAvailabilityResponse checkAvailability(ProductAvailabilityRequest request) {
        var productIds = request.items()
                .stream()
                .map(ProductAvailabilityItemRequest::productId)
                .toList();

        var products = productRepository.findAllById(productIds);
        Map<Long, Product> productsById = products.stream()
                .collect(Collectors.toMap(
                        Product::getId,
                        Function.identity()
                ));

        var responseItems = request.items()
                .stream()
                .map(item -> {
                    var product = productsById.get(item.productId());

                    if (product == null) {
                        throw new ProductNotFoundException(item.productId());
                    }

                    var available = product.getQuantity() >= item.quantity();

                    return new ProductAvailabilityItemResponse(
                            product.getId(),
                            product.getName(),
                            product.getQuantity(),
                            product.getUnitPrice(),
                            available
                    );
                })
                .toList();

        var allAvailable = responseItems.stream()
                .allMatch(ProductAvailabilityItemResponse::available);

        return new ProductAvailabilityResponse(allAvailable, responseItems);
    }

    public Product create(CreateProductRequest data) {
        var product = new Product(data.productName(), data.quantity(), data.unitPrice());

        return productRepository.save(product);
    }
}