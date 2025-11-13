package com.example.backend.promotion;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PromotionProductId implements Serializable {

    @Column(name = "promotion_id")
    private Long promotionId;

    @Column(name = "product_id")
    private Long productId;

    // ===== getters / setters =====
    public Long getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(Long promotionId) {
        this.promotionId = promotionId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    // equals / hashCode (generate จาก IDE ก็ได้)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PromotionProductId that)) return false;
        return Objects.equals(promotionId, that.promotionId)
                && Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(promotionId, productId);
    }
}
