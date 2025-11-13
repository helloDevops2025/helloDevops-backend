package com.example.backend.promotion;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.backend.product.Product;
import com.example.backend.product.ProductRepository;

@Service
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionProductRepository promotionProductRepository;
    private final ProductRepository productRepository;

    public PromotionService(PromotionRepository promotionRepository,
                            PromotionProductRepository promotionProductRepository,
                            ProductRepository productRepository) {
        this.promotionRepository = promotionRepository;
        this.promotionProductRepository = promotionProductRepository;
        this.productRepository = productRepository;
    }

    // ---------- ค้นหา / ดึงโปร ----------

    public List<Promotion> search(String q, String status) {
        PromotionStatus st = null;
        if (status != null && !status.isBlank()) {
            st = PromotionStatus.valueOf(status);
        }
        return promotionRepository.search(q, st);
    }

    public Promotion getById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion not found"));
    }

    // ---------- สร้างโปรใหม่ ----------

    @Transactional
    public Promotion create(Promotion p) {
        Promotion row = new Promotion();

        row.setName(p.getName());
        row.setCode(p.getCode());
        row.setDescription(p.getDescription());
        row.setPromoType(p.getPromoType());
        row.setScope(p.getScope());
        row.setPercentOff(p.getPercentOff());
        row.setAmountOff(p.getAmountOff());
        row.setFixedPrice(p.getFixedPrice());
        row.setBuyQty(p.getBuyQty());
        row.setGetQty(p.getGetQty());
        row.setAppliesToShipping(p.getAppliesToShipping());
        row.setMinOrderAmount(p.getMinOrderAmount());
        row.setMinQuantity(p.getMinQuantity());
        row.setStackMode(p.getStackMode());
        row.setPriority(p.getPriority());
        row.setStatus(p.getStatus());
        row.setStartAt(p.getStartAt());
        row.setEndAt(p.getEndAt());
        row.setTimezone(p.getTimezone());

        return promotionRepository.save(row);
    }

    // ---------- แก้ไขโปรเดิม ----------

    @Transactional
    public Promotion update(Long id, Promotion patch) {
        Promotion p = getById(id);

        p.setName(patch.getName());
        p.setCode(patch.getCode());
        p.setDescription(patch.getDescription());
        p.setPromoType(patch.getPromoType());
        p.setScope(patch.getScope());
        p.setPercentOff(patch.getPercentOff());
        p.setAmountOff(patch.getAmountOff());
        p.setFixedPrice(patch.getFixedPrice());
        p.setBuyQty(patch.getBuyQty());
        p.setGetQty(patch.getGetQty());
        p.setAppliesToShipping(patch.getAppliesToShipping());
        p.setMinOrderAmount(patch.getMinOrderAmount());
        p.setMinQuantity(patch.getMinQuantity());
        p.setStackMode(patch.getStackMode());
        p.setPriority(patch.getPriority());
        p.setStatus(patch.getStatus());
        p.setStartAt(patch.getStartAt());
        p.setEndAt(patch.getEndAt());
        p.setTimezone(patch.getTimezone());

        return promotionRepository.save(p);
    }

    // ---------- จัดการความสัมพันธ์โปรโมชัน ↔ สินค้า ----------

    public List<Product> getProductsOfPromotion(Long promoId) {
        return promotionProductRepository.findProductsByPromotionId(promoId);
    }

    @Transactional
    public void attachProducts(Long promoId, List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return;
        }

        Promotion promotion = getById(promoId);
        List<Product> products = productRepository.findAllById(productIds);

        for (Product product : products) {
            PromotionProductId key = new PromotionProductId();
            key.setPromotionId(promoId);
            key.setProductId(product.getId());

            // กันไม่ให้ซ้ำ
            if (promotionProductRepository.existsById(key)) {
                continue;
            }

            PromotionProduct pp = new PromotionProduct();
            pp.setId(key);
            pp.setPromotion(promotion);
            pp.setProduct(product);

            promotionProductRepository.save(pp);
        }
    }

    @Transactional
    public void detachProduct(Long promoId, Long productId) {
        promotionProductRepository.deleteByPromotionIdAndProductId(promoId, productId);
    }
}
