package com.example.backend.order;

import com.example.backend.product.Product;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id_fk")
    @JsonBackReference
    private Order order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id_fk")
    private Product product;

    private Integer quantity;

    @Transient
    @JsonProperty("productIdFk")
    private Long productIdFk;

    public OrderItem() {}

    public Long getId() { return id; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Long getProductIdFk() { return productIdFk; }
    public void setProductIdFk(Long productIdFk) { this.productIdFk = productIdFk; }

    @Transient
    public String getProductName() {
        return (product != null) ? product.getName() : null;
    }

    @Transient
    public BigDecimal getPriceEach() {
        return (product != null && product.getPrice() != null)
                ? product.getPrice() : BigDecimal.ZERO;
    }

    @Transient
    public BigDecimal getTotalPrice() {
        return getPriceEach().multiply(BigDecimal.valueOf(quantity != null ? quantity : 0));
    }

    // ✅ ชื่อแบรนด์จะ lookup ผ่าน OrderService ภายหลัง
    @Transient
    private String brandName;
    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }
}
