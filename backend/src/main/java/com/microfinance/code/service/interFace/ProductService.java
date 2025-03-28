package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.CIFDTO;
import com.microfinance.code.dto.ProductDTO;
import com.microfinance.code.etc.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProductService {
    ProductDTO createProduct(ProductDTO productDTO,  MultipartFile Photo) throws IOException;
    List<ProductDTO> getProductsByDealerId(Integer dealerId);

    ProductDTO updateProduct(Integer id, Map<String, Object> updates, MultipartFile photo);

    ApiResponse<String> deleteProduct(Integer id);
    List<ProductDTO> getAllProducts();

    List<ProductDTO> getProductByBranchId(Integer branchId);

    ProductDTO getProductById(Integer id);
}
