package com.example.backend.product;
import java.math.BigDecimal;

public interface ProductListRow {
  Long getId();
  String getProductId();
  String getName();
  BigDecimal getPrice();
  Integer getQuantity();
  Boolean getInStock();
  String getCategory();   // ชื่อ category จาก JOIN
  String getBrand();      // ชื่อ brand จาก JOIN
  String getImageUrl();   // cover image (URL) จาก subquery
}
