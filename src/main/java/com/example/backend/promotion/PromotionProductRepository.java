package com.example.backend.promotion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.backend.product.Product;

public interface PromotionProductRepository
        extends JpaRepository<PromotionProduct, PromotionProductId> {

    @Query("select pp.product from PromotionProduct pp where pp.promotion.id = :promotionId")
    List<Product> findProductsByPromotionId(Long promotionId);

    void deleteByPromotionIdAndProductId(Long promotionId, Long productId);
}
