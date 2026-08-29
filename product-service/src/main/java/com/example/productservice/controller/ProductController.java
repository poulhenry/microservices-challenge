package com.example.productservice.controller;

import com.example.productservice.controller.dto.CreateProductRequest;
import com.example.productservice.controller.dto.ProductAvailabilityRequest;
import com.example.productservice.controller.dto.ProductAvailabilityResponse;
import com.example.productservice.domain.Product;
import com.example.productservice.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping(path = "/availability")
    public ResponseEntity<ProductAvailabilityResponse> checkAvailability(
            @RequestBody ProductAvailabilityRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.checkAvailability(request));
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }
}