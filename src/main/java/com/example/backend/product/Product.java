// package com.example.backend.product;

// import jakarta.persistence.*;
// import java.math.BigDecimal;

// @Entity
// @Table(name = "products")
// public class Product {
//   @Id
//   @GeneratedValue(strategy = GenerationType.IDENTITY)
//   private Long id;

//   @Column(name="product_id", unique = true)
//   private String productId;

//   private String name;
//   private BigDecimal price;
//   private String category;
//   private String brand;
//   private Integer quantity;
//   private Boolean inStock;

//   // getters/setters
//   public Long getId(){return id;}
//   public String getProductId(){return productId;}
//   public void setProductId(String v){productId=v;}
//   public String getName(){return name;}
//   public void setName(String v){name=v;}
//   public BigDecimal getPrice(){return price;}
//   public void setPrice(BigDecimal v){price=v;}
//   public String getCategory(){return category;}
//   public void setCategory(String v){category=v;}
//   public String getBrand(){return brand;}
//   public void setBrand(String v){brand=v;}
//   public Integer getQuantity(){return quantity;}
//   public void setQuantity(Integer v){quantity=v;}
//   public Boolean getInStock(){return inStock;}
//   public void setInStock(Boolean v){inStock=v;}
// }
package com.example.backend.product;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class Product {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "product_id", unique = true, nullable = false)
  private String productId;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;   // ใหม่

  @Column(nullable = false)
  private BigDecimal price = BigDecimal.ZERO;

  @Column(nullable = false)
  private Integer quantity = 0;

  @Column(name = "in_stock", nullable = false)
  private Boolean inStock = true;

  // FK เก็บเป็นเลขตรงกับคอลัมน์ในตาราง (ไม่ทำ relation เพื่อลดการเปลี่ยนโค้ด)
  @Column(name = "category_id")
  private Long categoryId;      // ใหม่

  @Column(name = "brand_id")
  private Long brandId;         // ใหม่

  // -------- getters/setters --------
  public Long getId() { return id; }

  public String getProductId() { return productId; }
  public void setProductId(String v) { this.productId = v; }

  public String getName() { return name; }
  public void setName(String v) { this.name = v; }

  public String getDescription() { return description; }
  public void setDescription(String v) { this.description = v; }

  public BigDecimal getPrice() { return price; }
  public void setPrice(BigDecimal v) { this.price = v; }

  public Integer getQuantity() { return quantity; }
  public void setQuantity(Integer v) { this.quantity = v; }

  public Boolean getInStock() { return inStock; }
  public void setInStock(Boolean v) { this.inStock = v; }

  public Long getCategoryId() { return categoryId; }
  public void setCategoryId(Long v) { this.categoryId = v; }

  public Long getBrandId() { return brandId; }
  public void setBrandId(Long v) { this.brandId = v; }
}
