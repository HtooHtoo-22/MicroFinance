package com.microfinance.code.mapper;

import com.microfinance.code.dto.ProductDTO;
import com.microfinance.code.model.Dealer;
import com.microfinance.code.model.Product;

import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductDTO toDTO(Product product) {
        if (product == null) {
            return null;
        }
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setProductName(product.getProductName());
        dto.setValue(product.getValue());
        dto.setPhoto(product.getPhoto());
        dto.setDealerRegisterId(product.getDealer().getId()); // Fixed reference
        dto.setStatus(product.isStatus()); // Boolean mapping

        return dto;
    }

    public Product toEntity(ProductDTO dto) {
        if (dto == null) {
            return null;
        }
        Product product = new Product();
        product.setId(dto.getId());
        product.setProductName(dto.getProductName());
        product.setValue(dto.getValue());
        product.setPhoto(dto.getPhoto());

        // Fix: Corrected method name to match DTO field
        Dealer dealerRegister = new Dealer();
        dealerRegister.setId(dto.getDealerRegisterId()); // Fixed reference
        product.setDealer(dealerRegister);

        product.setStatus(dto.getStatus()); // Boolean mapping

        return product;
    }
}
