package com.example.backend.order.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderResponse {
    private Long id;
    private String customerName;
    private String shippingMethod;
    private BigDecimal totalAmount;
    private List<OrderItemResponse> items;

    public OrderResponse() {}

    public OrderResponse(Long id, String customerName, String shippingMethod, BigDecimal totalAmount, List<OrderItemResponse> items) {
        this.id = id;
        this.customerName = customerName;
        this.shippingMethod = shippingMethod;
        this.totalAmount = totalAmount;
        this.items = items;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }
}
