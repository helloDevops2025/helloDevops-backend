package com.example.backend.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductRepository repo;

  public ProductController(ProductRepository repo) {
    this.repo = repo;
  }

  // สำหรับทดสอบระบบ
  @GetMapping("/test")
  public ResponseEntity<?> test() {
    return ResponseEntity.ok(java.util.Map.of("status", "ok"));
  }

  // ------ READ (list) ------
  @GetMapping
  public List<Product> list() {
    return repo.findAll();
  }

  // ------ READ (by id) ------
  @GetMapping("/{id}")
  public ResponseEntity<Product> getById(@PathVariable Long id) {
    return repo.findById(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  // ------ CREATE ------
  @PostMapping
  public ResponseEntity<Product> create(@RequestBody Product p) {
    // ตั้งค่า default เบื้องต้น (ตามต้องการ)
    if (p.getInStock() == null) {
      // ถ้ายังไม่ได้คำนวณสต็อก ให้ตั้งจาก quantity > 0
      p.setInStock(p.getQuantity() != null && p.getQuantity() > 0);
    }
    Product saved = repo.save(p);
    return ResponseEntity.created(URI.create("/api/products/" + saved.getId())).body(saved);
  }

  // ------ UPDATE (by id) ------
  @PutMapping("/{id}")
  public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product req) {
    return repo.findById(id).map(p -> {
      // อัปเดตเฉพาะฟิลด์ที่ต้องการ
      p.setProductId(req.getProductId());
      p.setName(req.getName());
      p.setPrice(req.getPrice());
      p.setCategory(req.getCategory());
      p.setBrand(req.getBrand());
      p.setQuantity(req.getQuantity());
      p.setInStock(req.getInStock());
      return ResponseEntity.ok(repo.save(p));
    }).orElseGet(() -> ResponseEntity.notFound().build());
  }

  // ------ DELETE (by id) ------
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteById(@PathVariable Long id) {
    if (!repo.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    repo.deleteById(id);
    return ResponseEntity.noContent().build(); // 204
  }

  // ------ (ออปชัน) DELETE (by productId) ------
  // ใช้เมื่ออยากลบด้วย "#00001" แทน id
  @DeleteMapping("/by-product-id/{productId}")
  public ResponseEntity<Void> deleteByProductId(@PathVariable String productId) {
    return repo.findByProductId(productId)
        .map(p -> { repo.delete(p); return ResponseEntity.noContent().build(); })
        .orElseGet(() -> ResponseEntity.notFound().build());
  }
}
