package com.microfinance.code.service.impl;

import com.cloudinary.Cloudinary;

import com.microfinance.code.dto.ProductDTO;

import com.microfinance.code.etc.ApiResponse;
import com.microfinance.code.exception.NotFoundException;
import com.microfinance.code.mapper.ProductMapper;
import com.microfinance.code.model.Dealer;
import com.microfinance.code.model.Product;

import com.microfinance.code.model.User;
import com.microfinance.code.repository.DealerRepo;
import com.microfinance.code.repository.ProductRepo;
import com.microfinance.code.repository.UserRepo;
import com.microfinance.code.service.CloudinaryService;
import com.microfinance.code.service.interFace.ProductService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private DealerRepo dealerRepo;

    @Autowired
    private UserRepo userRepository;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Override
    public ProductDTO createProduct(ProductDTO dto, MultipartFile userPhoto) throws IOException {
        try {
            // Get dealer from DTO
            Dealer dealer = dealerRepo.findById(dto.getDealerId())
                    .orElseThrow(() -> new RuntimeException("Dealer not found"));

            // Upload image
            CompletableFuture<String> photoUrlFuture = cloudinaryService.uploadFileAsync(userPhoto);
            String photoUrl = photoUrlFuture.get();

            // Create product
            Product product = productMapper.toEntity(dto);
            product.setDealer(dealer); // Set dealer relationship
            product.setPhoto(photoUrl);

            product = productRepo.save(product);
            return productMapper.toDTO(product);
        } catch (Exception e) {
            throw new IOException("Error creating product: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ProductDTO> getProductsByDealerId(Integer dealerId) {
        List<Product> products = productRepo.findByDealerId(dealerId); // Fetch from DB
        return products.stream()
                .map(productMapper::toDTO) // Convert to DTOs
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> products = productRepo.findAll();
        return products.stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

//    @Override
//    public List<ProductDTO> getProductByBranchId(Integer branchId) {
//        return productRepo.findProductsByBranchId(branchId).stream()
//                .map(productMapper::toDTO)
//                .collect(Collectors.toList());
//    }

    @Override
    public ProductDTO updateProduct(Integer id, Map<String, Object> updates, MultipartFile photo) {
        Optional<Product> optionalProduct = productRepo.findById(id);
        if (!optionalProduct.isPresent()) {
            throw new RuntimeException("Product not found with ID: " + id);
        }

        Product product = optionalProduct.get();

        // Update fields dynamically
        updates.forEach((key, value) -> {
            switch (key) {
                case "productName":
                    product.setProductName((String) value);
                    break;
                case "value":
                    product.setValue(new BigDecimal(value.toString()));
                    break;
                case "status":
                    product.setStatus((Boolean) value);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid field: " + key);
            }
        });

        // Handle image upload asynchronously if a new file is provided
        if (photo != null && !photo.isEmpty()) {
            try {
                // Upload image asynchronously
                CompletableFuture<String> photoUrlFuture = cloudinaryService.uploadFileAsync(photo);

                // Wait for the upload to complete and get the URL
                String photoUrl = photoUrlFuture.get(); // Blocking call to wait for the result
                product.setPhoto(photoUrl);
            } catch (Exception e) {
                throw new RuntimeException("Failed to upload image: " + e.getMessage());
            }
        }

        // Save updated product
        Product updatedProduct = productRepo.save(product);
        return productMapper.toDTO(updatedProduct);
    }

    @Override
    public ApiResponse<String> deleteProduct(Integer id) {
        Optional<Product> optionalProduct = productRepo.findById(id);

        if (!optionalProduct.isPresent()) {
            return ApiResponse.error(HttpStatus.NOT_FOUND, 404, "Product not found with ID: " + id, "error");
        }

        productRepo.deleteById(id);
        return ApiResponse.success(HttpStatus.OK, 200, "Product deleted successfully", "Deleted product ID: " + id, "success");
    }
    @Override
    public List<ProductDTO> getProductByBranchId(Integer branchId) {
        // Step 1: Find all users in the given branch
        List<User> users = userRepository.findByBranchId(branchId);

        // Step 2: Filter out users with null dealers and find all dealers associated with those users
        Set<Dealer> dealers = users.stream()
                .filter(user -> user.getDealer() != null) // Filter out users with null dealers
                .map(User::getDealer)
                .collect(Collectors.toSet());

        // Step 3: Find all products associated with those dealers and map them to DTOs
        return dealers.stream()
                .flatMap(dealer -> productRepo.findByDealer(dealer).stream())
                .map(productMapper::toDTO) // Map each Product entity to ProductDTO
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(Integer id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with ID: " + id));
        return productMapper.toDTO(product);
    }
}