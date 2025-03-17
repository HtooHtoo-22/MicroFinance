package com.microfinance.code.mapper;

import com.microfinance.code.dto.ProductDTO;
import com.microfinance.code.model.Dealer;
import com.microfinance.code.model.Product;

import com.microfinance.code.model.User;
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
        dto.setDealerId(product.getDealer().getId());
        dto.setStatus(product.isStatus());
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

        // Map dealer
        Dealer dealer = new Dealer();
        dealer.setId(dto.getDealerId());
        product.setDealer(dealer);

        product.setStatus(dto.getStatus());
        return product;
    }
}