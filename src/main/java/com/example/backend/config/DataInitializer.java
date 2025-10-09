// src/main/java/com/example/backend/config/DataInitializer.java
package com.example.backend.config;

import com.example.backend.user.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {
  @Bean
  CommandLineRunner initUsers(UserRepository repo) {
    return args -> {
      var enc = new BCryptPasswordEncoder();

      repo.findByEmail("admin@gmail.com").orElseGet(() -> {
        var u = new User();
        u.setEmail("admin@gmail.com");
        u.setPhone("0000000000");
        u.setPasswordHash(enc.encode("admin123"));
        u.setRole(Role.ADMIN);
        u.setActive(true);
        return repo.save(u);
      });

      repo.findByEmail("user@gmail.com").orElseGet(() -> {
        var u = new User();
        u.setEmail("user@gmail.com");
        u.setPhone("0999999999");
        u.setPasswordHash(enc.encode("user123"));
        u.setRole(Role.USER);
        u.setActive(true);
        return repo.save(u);
      });
    };
  }
}
