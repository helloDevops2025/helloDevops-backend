// PromotionValidateResponse.java
package com.example.backend.promotion;

import java.math.BigDecimal;

public class PromotionValidateResponse {
    private boolean valid;
    private String discountType;   // PERCENT_OFF / AMOUNT_OFF / ...
    private BigDecimal discountValue;  // เช่น 10 (10%) หรือ 50 (50 บาท)
    private BigDecimal discountAmount; // มูลค่าที่ลดจริง ๆ
    private BigDecimal finalAmount;    // ยอดหลังหักส่วนลด
    private String message;

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getFinalAmount() { return finalAmount; }
    public void setFinalAmount(BigDecimal finalAmount) { this.finalAmount = finalAmount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}