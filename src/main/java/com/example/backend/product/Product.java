package com.example.backend.product;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="product_id", unique = true)
  private String productId;

  private String name;
  private BigDecimal price;
  private String category;
  private String brand;
  private Integer quantity;
  private Boolean inStock;

  // getters/setters
  public Long getId(){return id;}
  public String getProductId(){return productId;}
  public void setProductId(String v){productId=v;}
  public String getName(){return name;}
  public void setName(String v){name=v;}
  public BigDecimal getPrice(){return price;}
  public void setPrice(BigDecimal v){price=v;}
  public String getCategory(){return category;}
  public void setCategory(String v){category=v;}
  public String getBrand(){return brand;}
  public void setBrand(String v){brand=v;}
  public Integer getQuantity(){return quantity;}
  public void setQuantity(Integer v){quantity=v;}
  public Boolean getInStock(){return inStock;}
  public void setInStock(Boolean v){inStock=v;}
}
