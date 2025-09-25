package com.example.backend.product;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // เผื่อกรณีต้องการลบ/หาโดย productId เช่น "#00001"
    Optional<Product> findByProductId(String productId);
}
