package com.microfinance.code.service.interFace;

import com.microfinance.code.dto.CIFDTO;
import com.microfinance.code.dto.ProductDTO;
import com.microfinance.code.etc.ApiResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProductService {
    public void hello();
    ProductDTO createProduct(ProductDTO productDTO,  MultipartFile Photo) throws IOException;
    List<ProductDTO> getProductsByDealerId(Integer dealerId);

    ProductDTO updateProduct(Integer id, Map<String, Object> updates);


    ApiResponse<String> deleteProduct(Integer id);
}
