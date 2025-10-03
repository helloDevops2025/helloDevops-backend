// package com.example.backend.product;

// import org.springframework.data.jpa.repository.JpaRepository;
// import java.util.Optional;

// public interface ProductRepository extends JpaRepository<Product, Long> {
//     // เผื่อกรณีต้องการลบ/หาโดย productId เช่น "#00001"
//     Optional<Product> findByProductId(String productId);
// }
package com.example.backend.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
  Optional<Product> findByProductId(String productId);

  @Query(value = """
    SELECT
      p.id                 AS id,
      p.product_id         AS productId,
      p.name               AS name,
      p.price              AS price,
      p.quantity           AS quantity,
      p.in_stock           AS inStock,
      COALESCE(c.name,'-') AS category,
      COALESCE(b.name,'-') AS brand,
      (
        SELECT pi.image_url
        FROM product_images pi
        WHERE pi.product_id_fk = p.id AND pi.is_cover = TRUE
        ORDER BY pi.sort_order ASC, pi.id ASC
        LIMIT 1
      )                    AS imageUrl
    FROM products p
    LEFT JOIN categories c ON p.category_id = c.id
    LEFT JOIN brands b     ON p.brand_id    = b.id
    ORDER BY p.id ASC
  """, nativeQuery = true)
  List<ProductListRow> findAllListRows();
}
