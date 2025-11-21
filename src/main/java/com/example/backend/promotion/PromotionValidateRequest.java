// PromotionValidateRequest.java
package com.example.backend.promotion;

import java.math.BigDecimal;

public class PromotionValidateRequest {
    private String code;
    private BigDecimal cartSubtotal;
    private Long userId;

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public BigDecimal getCartSubtotal() { return cartSubtotal; }
    public void setCartSubtotal(BigDecimal cartSubtotal) { this.cartSubtotal = cartSubtotal; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
