// src/main/java/com/example/backend/config/CorsConfig.java
package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
          .allowedOriginPatterns(
            "http://localhost:*",     // รองรับทุกพอร์ต dev (5173, 5174, ฯลฯ)
            "http://127.0.0.1:*",
            "http://192.168.*.*:*"    // ใช้ภายใน network บ้าน/ออฟฟิศ
          )
          .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
          .allowedHeaders("*")
          .allowCredentials(true)
          .maxAge(3600);
      }
    };
  }
}
