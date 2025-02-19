package com.product.http.controller;

import com.product.dto.CreateProductDto;
import com.product.dto.EditProductDto;
import com.product.dto.ReadProductDto;
import com.product.service.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
@Tag(name = "Product Controller")
public class ProductController {

    private final ProductServiceImpl productService;

    @PostMapping
    @Operation(summary = "Create product")
    public ResponseEntity<ReadProductDto> create(@Valid @RequestBody CreateProductDto productDto) {
        return ResponseEntity.ok(productService.create(productDto));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by id")
    public ResponseEntity<ReadProductDto> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all products")
    public ResponseEntity<List<ReadProductDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit product")
    public ResponseEntity<ReadProductDto> update(
            @PathVariable Long id,
            @Valid @RequestBody EditProductDto product
    ) {
        return ResponseEntity.ok(productService.update(id, product));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product by id")
    public ResponseEntity<Boolean> delete(@PathVariable Long id) {
        return ResponseEntity.ok(productService.delete(id));
    }
}