package com.microfinance.code.config;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", System.getProperty("cloudinary.cloud_name", "dx1ifcrrj"),
                "api_key", System.getProperty("cloudinary.api_key", "562842999816968"),
                "api_secret", System.getProperty("cloudinary.api_secret", "dyRH75fbJuZxXChXryMn05cqhXU")
        ));
    }
}
