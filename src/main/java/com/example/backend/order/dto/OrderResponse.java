package com.example.backend.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponse {
    private Long id;
    private String orderCode;
    private String customerName;
    private String customerPhone;
    private String shippingAddress;
    private String paymentMethod;
    private String shippingMethod;
    private String orderStatus;
    private BigDecimal totalAmount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime preparingAt;
    private LocalDateTime readyAt;
    private LocalDateTime shippingAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime cancelledAt;

    private List<OrderItemResponse> items;

    public OrderResponse() {}

    public OrderResponse(Long id, String orderCode, String customerName, String customerPhone,
                         String shippingAddress, String paymentMethod, String shippingMethod,
                         String orderStatus, BigDecimal totalAmount, LocalDateTime createdAt,
                         LocalDateTime updatedAt, LocalDateTime preparingAt,
                         LocalDateTime readyAt, LocalDateTime shippingAt,
                         LocalDateTime deliveredAt, LocalDateTime cancelledAt,
                         List<OrderItemResponse> items) {
        this.id = id;
        this.orderCode = orderCode;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.shippingMethod = shippingMethod;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.preparingAt = preparingAt;
        this.readyAt = readyAt;
        this.shippingAt = shippingAt;
        this.deliveredAt = deliveredAt;
        this.cancelledAt = cancelledAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getPreparingAt() {
        return preparingAt;
    }

    public void setPreparingAt(LocalDateTime preparingAt) {
        this.preparingAt = preparingAt;
    }

    public LocalDateTime getReadyAt() {
        return readyAt;
    }

    public void setReadyAt(LocalDateTime readyAt) {
        this.readyAt = readyAt;
    }

    public LocalDateTime getShippingAt() {
        return shippingAt;
    }

    public void setShippingAt(LocalDateTime shippingAt) {
        this.shippingAt = shippingAt;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
