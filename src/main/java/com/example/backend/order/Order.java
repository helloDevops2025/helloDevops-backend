package com.example.backend.order;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {

    // ========== Basic fields ==========
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_code", unique = true)
    private String orderCode;

    @Column(name = "customer_name")
    private String customerName;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "shipping_method")
    private String shippingMethod;

    @Column(name = "order_status")
    private String orderStatus;

    // ========== Money fields (match init.sql) ==========

    /** ยอดรวมสินค้าก่อนลด (sum ของทุก item) */
    @Column(name = "subtotal", nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    /** ส่วนลดรวมทั้งหมด (order-level + item-level) */
    @Column(name = "discount_total", nullable = false)
    private BigDecimal discountTotal = BigDecimal.ZERO;

    /** ค่าส่ง */
    @Column(name = "shipping_fee")
    private BigDecimal shippingFee = BigDecimal.ZERO;

    /** ภาษี (ถ้าใช้) */
    @Column(name = "tax_total")
    private BigDecimal taxTotal = BigDecimal.ZERO;

    /** ยอดสุทธิหลังลด + ค่าส่ง + ภาษี */
    @Column(name = "grand_total")
    private BigDecimal grandTotal = BigDecimal.ZERO;

    // ========== Timestamps ==========

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", nullable = false)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt = new Date();

    // ========== Relations ==========

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderItems = new ArrayList<>();

    // ========== Convenience ==========

    /**
     * ใช้ให้ FE อ่านง่าย (เดิมมีแล้ว)
     * ถ้า grandTotal ถูกเซ็ตแล้ว ให้ใช้ grandTotal
     * ถ้าไม่ ก็ fallback ไปคำนวณจาก subtotal / discount / shipping / tax
     */
    @Transient
    public BigDecimal getTotalAmount() {
        if (grandTotal != null && grandTotal.compareTo(BigDecimal.ZERO) > 0) {
            return grandTotal;
        }
        BigDecimal sub = (subtotal != null) ? subtotal : BigDecimal.ZERO;
        BigDecimal disc = (discountTotal != null) ? discountTotal : BigDecimal.ZERO;
        BigDecimal ship = (shippingFee != null) ? shippingFee : BigDecimal.ZERO;
        BigDecimal tax = (taxTotal != null) ? taxTotal : BigDecimal.ZERO;
        return sub.subtract(disc).add(ship).add(tax);
    }

    /**
     * helper เวลา set orderItems : เซ็ตด้าน child ให้ชี้กลับมาที่ order ด้วย
     */
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems.clear();
        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                item.setOrder(this);
                this.orderItems.add(item);
            }
        }
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    // ========== Lifecycle hooks ==========

    @PrePersist
    public void onPrePersist() {
        // auto generate temp order code ถ้า frontend ไม่ส่งมา
        if (orderCode == null || orderCode.isBlank()) {
            this.orderCode = "TEMP-" + System.currentTimeMillis();
        }
        if (createdAt == null) {
            createdAt = new Date();
        }
        if (updatedAt == null) {
            updatedAt = new Date();
        }

        // ป้องกันไม่ให้ insert null เข้า column ที่ not null
        if (subtotal == null) subtotal = BigDecimal.ZERO;
        if (discountTotal == null) discountTotal = BigDecimal.ZERO;
        if (shippingFee == null) shippingFee = BigDecimal.ZERO;
        if (taxTotal == null) taxTotal = BigDecimal.ZERO;
        if (grandTotal == null) grandTotal = BigDecimal.ZERO;
    }

    @PreUpdate
    public void onPreUpdate() {
        updatedAt = new Date();
        if (subtotal == null) subtotal = BigDecimal.ZERO;
        if (discountTotal == null) discountTotal = BigDecimal.ZERO;
        if (shippingFee == null) shippingFee = BigDecimal.ZERO;
        if (taxTotal == null) taxTotal = BigDecimal.ZERO;
        if (grandTotal == null) grandTotal = BigDecimal.ZERO;
    }

    // ========== Getters / Setters ==========

    public Long getId() {
        return id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    // --- money fields ---

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountTotal() {
        return discountTotal;
    }

    public void setDiscountTotal(BigDecimal discountTotal) {
        this.discountTotal = discountTotal;
    }

    public BigDecimal getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(BigDecimal shippingFee) {
        this.shippingFee = shippingFee;
    }

    public BigDecimal getTaxTotal() {
        return taxTotal;
    }

    public void setTaxTotal(BigDecimal taxTotal) {
        this.taxTotal = taxTotal;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    // --- timestamps ---

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {  // เผื่ออยากเซ็ตเองจาก controller
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {  // optional
        this.updatedAt = updatedAt;
    }
}
