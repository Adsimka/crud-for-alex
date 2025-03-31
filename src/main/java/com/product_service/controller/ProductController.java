package com.product_service.controller;

import com.github.fge.jsonpatch.JsonPatch;
import com.product_service.model.dto.ProductCreateDto;
import com.product_service.model.dto.ProductFilterDto;
import com.product_service.model.dto.ProductReadDto;
import com.product_service.service.impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
@Tag(name = "Product", description = "The product API")
public class ProductController {

    private final ProductServiceImpl productService;

    @PostMapping
    @Operation(summary = "Create product", description = "Creates a new product", responses = {
            @ApiResponse(responseCode = "201", description = "The product has been created", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = com.product_service.model.ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Parameter validation error", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = com.product_service.model.ErrorResponse.class))
            })
    })
    public ResponseEntity<Void> create(@Valid @RequestBody ProductCreateDto dto) {
        return ResponseEntity.created(
                productService.create(dto)
        ).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by id", description = "Gets a product by its ID", responses = {
            @ApiResponse(responseCode = "200", description = "The product has been found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = com.product_service.model.dto.ProductReadDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "The product does not exists", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = com.product_service.model.ErrorResponse.class))
            })
    })
    public ResponseEntity<ProductReadDto> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Search products", description = "Search products by filter", responses = {
            @ApiResponse(responseCode = "200", description = "Products found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = com.product_service.model.dto.ProductReadDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "No products found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = com.product_service.model.ErrorResponse.class))
            })
    })
    @Parameter(name = "name", description = "Filter by product name")
    @Parameter(name = "description", description = "Filter by product description")
    public ResponseEntity<Page<ProductReadDto>> findByFilter(
            @ParameterObject ProductFilterDto filter,
            @PageableDefault(page = 0, size = 10)
            @Parameter(name = "sort", schema = @Schema(defaultValue = "name,desc"))
            @SortDefault(value = "name", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(productService.findBy(pageable, filter));
    }

    @PatchMapping(value = "/{id}", consumes = "application/json-patch+json")
    @Operation(summary = "Edit product", description = "Change product by JsonPatch operation", responses = {
            @ApiResponse(responseCode = "202", description = "The product has been updated", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = com.product_service.model.dto.ProductReadDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "No products found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = com.product_service.model.ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "422", description = "Unprocessable product", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = com.product_service.model.ErrorResponse.class))
            })
    })
    public ResponseEntity<ProductReadDto> update(
            @PathVariable("id") UUID id,
            @RequestBody JsonPatch patch
            ) {
        return ResponseEntity.accepted()
                .body(productService.update(id, patch));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product by id", description = "Remove product by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Successful product removal", content = @Content)
    })
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    @Parameter(name = "name", description = "Filter by product name")
    @Parameter(name = "description", description = "Filter by product description")
    @Operation(summary = "Delete product by filter", description = "Remove product by filter", responses = {
            @ApiResponse(responseCode = "204", description = "Successful product removal", content = @Content)
    })
    public ResponseEntity<Void> deleteBy(@ParameterObject ProductFilterDto filter) {
        productService.deleteBy(filter);
        return ResponseEntity.noContent().build();
    }
}