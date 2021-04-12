package com.kbui.ceres.config;

import com.cloudinary.Cloudinary;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CloudinaryApiConfiguration {
  @Bean
  public Cloudinary cloudinary() {
    Map<String, String> config = new HashMap<String, String>();
    config.put("cloud_name", System.getenv("CLOUDINARY_NAME"));
    config.put("api_key", System.getenv("CLOUDINARY_KEY"));
    config.put("api_secret", System.getenv("CLOUDINARY_SECRET"));

    return new Cloudinary(config);
  }
}
