package com.microfinance.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microfinance.code.dto.CIFDTO;
import com.microfinance.code.dto.ProductDTO;
import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.model.Dealer;
import com.microfinance.code.model.User;
import com.microfinance.code.repository.DealerRepo;
import com.microfinance.code.repository.UserRepo;
import com.microfinance.code.service.interFace.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(consumes = "multipart/form-data")
    public ApiResponse<ProductDTO> createProduct(
            @RequestParam("product") String productJson,
            @RequestPart("userPhoto") MultipartFile userPhoto,
            Principal principal) { // Get authenticated user

        try {
            // Get dealer from authenticated email
            String email = principal.getName();
            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Parse product DTO
            ProductDTO productDTO = objectMapper.readValue(productJson, ProductDTO.class);

            // Set dealer ID from authenticated user
            if (user.getDealer() == null) {
                throw new RuntimeException("Authenticated user is not a dealer");
            }
            productDTO.setDealerId(user.getDealer().getId());

            ProductDTO createdProduct = productService.createProduct(productDTO, userPhoto);
            return ApiResponse.success(HttpStatus.CREATED, 201, "Product created successfully", createdProduct);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 500, "Error creating product: " + e.getMessage());
        }
    }

    @GetMapping("/dealer/{dealerId}")
    public ApiResponse<List<ProductDTO>> getProductsByDealer(@PathVariable Integer dealerId) {
        List<ProductDTO> products = productService.getProductsByDealerId(dealerId);
        return ApiResponse.success(HttpStatus.OK, 200, "Products fetched successfully", products);
    }

    @GetMapping("/list")
    public ApiResponse<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ApiResponse.success(HttpStatus.OK, 200, "All products fetched successfully", products);
    }

    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable Integer id,
            @RequestParam("product") String productJson,
            @RequestPart(value = "photo", required = false) MultipartFile photo) {

        try {
            // Convert JSON string to a Map
            Map<String, Object> updates = objectMapper.readValue(productJson, new TypeReference<Map<String, Object>>() {});

            ProductDTO updatedProduct = productService.updateProduct(id, updates, photo);
            return ResponseEntity.ok(updatedProduct);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteProduct(@PathVariable Integer id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/branch/{branchId}")
    public ApiResponse<List<ProductDTO>> getProductByBranchId(@PathVariable Integer branchId) {
        List<ProductDTO> products = productService.getProductByBranchId(branchId);
        return ApiResponse.success(HttpStatus.OK, 200, "Products fetched successfully", products);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDTO> getProductById(@PathVariable Integer id) {
        try {
            ProductDTO product = productService.getProductById(id);
            return ApiResponse.success(HttpStatus.OK, 200, "Product fetched successfully", product);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, 404, "Product not found with ID: " + id);
        }
    }

}
