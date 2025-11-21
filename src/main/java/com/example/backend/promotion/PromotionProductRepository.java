package com.example.backend.promotion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.product.Product;

public interface PromotionProductRepository
        extends JpaRepository<PromotionProduct, PromotionProductId> {

    @Query("select pp.product from PromotionProduct pp where pp.promotion.id = :promotionId")
    List<Product> findProductsByPromotionId(@Param("promotionId") Long promotionId);

    @Transactional
    @Modifying
    @Query("delete from PromotionProduct pp " +
           "where pp.promotion.id = :promotionId and pp.product.id = :productId")
    void deleteByPromotionIdAndProductId(@Param("promotionId") Long promotionId,
                                         @Param("productId") Long productId);
}
