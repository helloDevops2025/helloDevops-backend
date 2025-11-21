package com.example.backend.promotion;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.backend.product.Product;

@RestController
@RequestMapping("/api/promotions")
@CrossOrigin
public class PromotionController {

    private final PromotionService promotionService;

    public PromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    // GET /api/promotions?q=&status=
    @GetMapping
    public List<Promotion> list(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status
    ) {
        return promotionService.search(q, status);
    }

    // GET /api/promotions/{id}
    @GetMapping("/{id}")
    public Promotion getOne(@PathVariable Long id) {
        return promotionService.getById(id);
    }

    // POST /api/promotions (สร้างโปรใหม่)
    @PostMapping
    public Promotion create(@RequestBody Promotion body) {
        return promotionService.create(body);
    }

    // PUT /api/promotions/{id} (แก้ไขโปรเดิม)
    @PutMapping("/{id}")
    public Promotion update(@PathVariable Long id, @RequestBody Promotion body) {
        return promotionService.update(id, body);
    }

    // GET /api/promotions/{id}/products
    @GetMapping("/{id}/products")
    public List<Product> getProducts(@PathVariable Long id) {
        return promotionService.getProductsOfPromotion(id);
    }

    // DTO สำหรับผูกสินค้าเข้ากับโปร
    public static class AttachProductsRequest {
        private List<Long> productIds;

        public List<Long> getProductIds() {
            return productIds;
        }

        public void setProductIds(List<Long> productIds) {
            this.productIds = productIds;
        }
    }

    // POST /api/promotions/{id}/products
    @PostMapping("/{id}/products")
    public ResponseEntity<?> attachProducts(
            @PathVariable Long id,
            @RequestBody AttachProductsRequest req
    ) {
        promotionService.attachProducts(id, req.getProductIds());
        return ResponseEntity.ok(Map.of("ok", true));
    }

    // DELETE /api/promotions/{id}/products/{productId}
    @DeleteMapping("/{id}/products/{productId}")
    public ResponseEntity<?> detachProduct(
            @PathVariable Long id,
            @PathVariable Long productId
    ) {
        promotionService.detachProduct(id, productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/validate")
    public ResponseEntity<PromotionValidateResponse> validatePromotion(
            @RequestBody PromotionValidateRequest req
    ) {
        PromotionValidateResponse resp = promotionService.validatePromotion(req);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/debug-validate")
    public String debugValidate() {
        return "validate-endpoint-loaded";
        }


}
