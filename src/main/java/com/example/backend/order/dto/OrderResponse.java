package com.example.backend.order.dto;

import java.math.BigDecimal;
import java.util.Date;
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
    private Date createdAt;
    private Date updatedAt;
    private BigDecimal total;
    private List<OrderItemResponse> items;

    public OrderResponse(Long id, String orderCode, String customerName, String customerPhone,
                         String shippingAddress, String paymentMethod, String shippingMethod,
                         String orderStatus, Date createdAt, Date updatedAt,
                         BigDecimal total, List<OrderItemResponse> items) {
        this.id = id;
        this.orderCode = orderCode;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.shippingMethod = shippingMethod;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.total = total;
        this.items = items;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCustomerName() { return customerName; }
    public String getcustomerPhone() { return customerPhone; }

    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getShippingMethod() { return shippingMethod; }
    public void setShippingMethod(String shippingMethod) { this.shippingMethod = shippingMethod; }
    public BigDecimal getTotalAmount() { return total; }
    public void setTotalAmount(BigDecimal totalAmount) { this.total = totalAmount; }
    public List<OrderItemResponse> getItems() { return items; }
    public void setItems(List<OrderItemResponse> items) { this.items = items; }
}
