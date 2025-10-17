package com.example.backend.order.dto;

import java.math.BigDecimal;

public class OrderItemResponse {
    private String productName;
    private String brandName;
    private Integer quantity;
    private BigDecimal priceEach;
    private BigDecimal totalPrice;

    public OrderItemResponse() {}

    public OrderItemResponse(String productName, String brandName,
                             Integer quantity, BigDecimal priceEach, BigDecimal totalPrice) {
        this.productName = productName;
        this.brandName = brandName;
        this.quantity = quantity;
        this.priceEach = priceEach;
        this.totalPrice = totalPrice;
    }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPriceEach() { return priceEach; }
    public void setPriceEach(BigDecimal priceEach) { this.priceEach = priceEach; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
}
