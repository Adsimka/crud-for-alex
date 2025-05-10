package com.productservice.controller;

import com.productservice.model.dto.PageDto;
import com.productservice.model.dto.ProductCreateDto;
import com.productservice.model.filter.ProductSearchFilter;
import com.productservice.model.dto.ProductReadDto;
import com.productservice.model.dto.ProductUpdateDto;
import com.productservice.model.filter.ProductDeleteFilter;
import com.productservice.service.ProductService;
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
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

import static com.productservice.util.http.UriBuilder.buildCreateProductUri;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products")
@Tag(name = "Product", description = "The product API")
public class ProductController {

    private final ProductService productService;

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

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search products", description = "Search products by filter", responses = {
            @ApiResponse(responseCode = "200", description = "Products found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProductReadDto.class))
            }),
            @ApiResponse(responseCode = "404", description = "No products found", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))
            })
    })
    public ResponseEntity<PageDto<ProductReadDto>> findBy(
            ProductSearchFilter filter,
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
        return ResponseEntity.ok().body(
                productService.update(id, dto)
        );
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
    @Operation(summary = "Delete product by filter", description = "Remove product by filter", responses = {
            @ApiResponse(responseCode = "204", description = "Successful product removal", content = @Content)
    })
    public ResponseEntity<Void> deleteBy(ProductDeleteFilter filter) {
        productService.deleteBy(filter);
        return ResponseEntity.noContent().build();
    }
}