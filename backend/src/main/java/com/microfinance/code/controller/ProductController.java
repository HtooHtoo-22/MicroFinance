package com.microfinance.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.code.dto.CIFDTO;
import com.microfinance.code.dto.ProductDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.service.interFace.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired 
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper; // Inject ObjectMapper properly

    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<ProductDTO> createProduct(
            @RequestParam("product") String productJson,
            @RequestPart("userPhoto") MultipartFile userPhoto) {
        try {
            System.out.println("Received JSON: " + productJson); // Debugging log

            // Convert JSON string to ProductDTO
            ProductDTO productDTO = objectMapper.readValue(productJson, ProductDTO.class);

            ProductDTO createdProduct = productService.createProduct(productDTO, userPhoto);
            return ApiResponse.success(HttpStatus.CREATED, 201, "Product created successfully", createdProduct);
        } catch (IOException e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 500, "Invalid JSON format: " + e.getMessage());
        }
    }
    @GetMapping("/dealer/{dealerId}")
    public ApiResponse<List<ProductDTO>> getProductsByDealer(@PathVariable Integer dealerId) {
        List<ProductDTO> products = productService.getProductsByDealerId(dealerId);
        return ApiResponse.success(HttpStatus.OK, 200, "Products fetched successfully", products);
    }
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> updates) {

        ProductDTO updatedProduct = productService.updateProduct(id, updates);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Integer id) {
        ApiResponse<String> response = productService.deleteProduct(id);
        return ResponseEntity.status(response.getHttpStatus()).body(response);
    }
}
