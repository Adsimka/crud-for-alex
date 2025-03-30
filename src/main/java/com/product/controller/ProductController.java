package com.product.controller;

import com.product.model.dto.ProductCreateDto;
import com.product.model.dto.ProductEditDto;
import com.product.model.dto.ProductFilterDto;
import com.product.model.dto.ProductReadDto;
import com.product.service.impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products")
@Tag(name = "Product Controller")
public class ProductController {

    private final ProductServiceImpl productService;

    @PostMapping
    @Operation(summary = "Create product")
    public ResponseEntity<Void> create(@Valid @RequestBody ProductCreateDto dto) {
        return ResponseEntity.created(
                productService.create(dto)
        ).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by id")
    public ResponseEntity<ProductReadDto> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Search products by filter")
    @Parameter(name = "name", description = "Filter by product name")
    @Parameter(name = "description", description = "Filter by product description")
    public ResponseEntity<Page<ProductReadDto>> findByFilter(
            @ParameterObject ProductFilterDto filter,
            @Parameter(name = "sort", schema = @Schema(defaultValue = "name,desc"))
            @PageableDefault(page = 0, size = 10)
            @SortDefault(value = "name", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(productService.findBy(pageable, filter));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Edit product")
    public ResponseEntity<ProductReadDto> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody ProductEditDto dto
    ) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product by id")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Operation(summary = "Delete product by filter")
    public ResponseEntity<Void> deleteBy(@RequestBody ProductFilterDto filter) {
        productService.deleteBy(filter);
        return ResponseEntity.noContent().build();
    }
}