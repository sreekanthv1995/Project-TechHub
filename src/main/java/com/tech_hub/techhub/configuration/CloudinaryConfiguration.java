package com.tech_hub.techhub.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfiguration {
    @Value("${spring.cloudinary.cloud_name}")
    private String cloudName;
    @Value("${spring.cloudinary.api_key}")
    private String apiKey;
    @Value("${spring.cloudinary.api_secret}")
    private String apiSecret;


    @Bean
    public Cloudinary cloudinary(){
        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name",cloudName,
                "api_key",apiKey,
                "api_secret",apiSecret
        ));
    }

}
