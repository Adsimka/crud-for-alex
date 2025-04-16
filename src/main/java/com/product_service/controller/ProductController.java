package com.product_service.controller;

import com.product_service.model.dto.*;
import com.product_service.service.impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.product_service.util.http.UriBuilder.buildCreateProductUri;

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
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
            }),
            @ApiResponse(responseCode = "400", description = "Parameter validation error", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
            })
    })
    public ResponseEntity<Void> create(@Valid @RequestBody ProductCreateDto dto) {
        UUID id = productService.create(dto);
        return ResponseEntity.created(
                buildCreateProductUri(id)
        ).build();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by id", description = "Gets a product by its ID", responses = {
            @ApiResponse(responseCode = "200", description = "The product has been found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductReadDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "The product does not exists", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
            })
    })
    public ResponseEntity<ProductReadDto> findById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping
    @Operation(summary = "Search products", description = "Search products by filter", responses = {
            @ApiResponse(responseCode = "200", description = "Products found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductReadDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "No products found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
            })
    })
    @Parameter(name = "name", description = "Filter by product name")
    @Parameter(name = "description", description = "Filter by product description")
    public ResponseEntity<PageDto<ProductReadDto>> findBy(
            ProductFilter filter,
            @PageableDefault(page = 0, size = 10)
            @Parameter(name = "sort", schema = @Schema(defaultValue = "name,desc"))
            @SortDefault(value = "name", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(productService.findBy(pageable, filter));
    }

    @PatchMapping(value = "/{id}")
    @Operation(summary = "Edit product", description = "Change product by JsonPatch operation", responses = {
            @ApiResponse(responseCode = "202", description = "The product has been updated", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductReadDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "No products found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
            }),
            @ApiResponse(responseCode = "422", description = "Unprocessable product", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
            }),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
            })
    })
    public ResponseEntity<ProductReadDto> update(
            @PathVariable("id") UUID id,
            @Valid @RequestBody ProductUpdateDto dto
    ) {
        return ResponseEntity.accepted().body(
                productService.update(id, dto));
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
    @Parameter(name = "name", description = "Product name")
    @Operation(summary = "Delete product by name", description = "Remove product by name", responses = {
            @ApiResponse(responseCode = "204", description = "Successful product removal", content = @Content)
    })
    public ResponseEntity<Void> deleteByName(@RequestParam String name) {
        productService.deleteByName(name);
        return ResponseEntity.noContent().build();
    }
}