package com.example.backend.product;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
    List<ProductImage> findByProductIdFkOrderBySortOrderAscIdAsc(Long productIdFk);
    Optional<ProductImage> findFirstByProductIdFkAndIsCoverTrueOrderBySortOrderAscIdAsc(Long productIdFk);
    void deleteByProductIdFk(Long productIdFk);
}
 