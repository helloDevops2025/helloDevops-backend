package com.example.backend.promotion;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

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
        if (productIds == null || productIds.isEmpty()) return;

        Promotion promotion = getById(promoId);
        List<Product> products = productRepository.findAllById(productIds);

        for (Product product : products) {
            PromotionProductId key = new PromotionProductId();
            key.setPromotionId(promoId);
            key.setProductId(product.getId());

            if (promotionProductRepository.existsById(key)) continue;

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

    // ---------- ใช้ตรวจโค้ดส่วนลดจากหน้า Cart / Checkout ----------

    public PromotionValidateResponse validatePromotion(PromotionValidateRequest req) {
        PromotionValidateResponse resp = new PromotionValidateResponse();

        // 1) เช็คว่า user ใส่โค้ดมาหรือยัง
        if (req == null || req.getCode() == null || req.getCode().isBlank()) {
            resp.setValid(false);
            resp.setMessage("กรุณาระบุโค้ดส่วนลด");
            return resp;
        }

        // subtotal จาก request (ป้องกัน null)
        BigDecimal subtotal = req.getCartSubtotal() != null
                ? req.getCartSubtotal()
                : BigDecimal.ZERO;

        // 2) หาโปรโมชันจาก code
        Optional<Promotion> optPromo =
                promotionRepository.findFirstByCodeIgnoreCase(req.getCode().trim());

        if (optPromo.isEmpty()) {
            resp.setValid(false);
            resp.setMessage("ไม่พบโค้ดส่วนลดนี้");
            return resp;
        }

        Promotion promo = optPromo.get();

        // 3) เช็คสถานะ active
        if (promo.getStatus() != PromotionStatus.ACTIVE) {
            resp.setValid(false);
            resp.setMessage("โค้ดนี้ไม่สามารถใช้งานได้แล้ว");
            return resp;
        }

        // 4) เช็ควันที่เริ่มต้น / วันที่หมดอายุ (ใช้ OffsetDateTime ให้ตรง type)
        OffsetDateTime now = OffsetDateTime.now();
        if (promo.getStartAt() != null && now.isBefore(promo.getStartAt())) {
            resp.setValid(false);
            resp.setMessage("โค้ดยังไม่เริ่มใช้งาน");
            return resp;
        }
        if (promo.getEndAt() != null && now.isAfter(promo.getEndAt())) {
            resp.setValid(false);
            resp.setMessage("โค้ดหมดอายุแล้ว");
            return resp;
        }

        // 5) เช็คขั้นต่ำ (min_order_amount)
        BigDecimal minAmt = promo.getMinOrderAmount() != null
                ? promo.getMinOrderAmount()
                : BigDecimal.ZERO;

        if (minAmt.compareTo(BigDecimal.ZERO) > 0 && subtotal.compareTo(minAmt) < 0) {
            resp.setValid(false);
            resp.setMessage("ยอดสั่งซื้อยังไม่ถึงขั้นต่ำสำหรับโค้ดนี้");
            return resp;
        }

        // 6) เริ่มคำนวณส่วนลด
        BigDecimal discountAmount = BigDecimal.ZERO;
        PromoType promoType = promo.getPromoType();

        if (promoType == null) {
            resp.setValid(false);
            resp.setMessage("รูปแบบโปรโมชันไม่ถูกต้อง");
            return resp;
        }

        if (promoType == PromoType.PERCENT_OFF) {
            BigDecimal percent = promo.getPercentOff() != null
                    ? promo.getPercentOff()
                    : BigDecimal.ZERO;

            discountAmount = subtotal
                    .multiply(percent)
                    .divide(BigDecimal.valueOf(100));

        } else if (promoType == PromoType.AMOUNT_OFF) {
            BigDecimal off = promo.getAmountOff() != null
                    ? promo.getAmountOff()
                    : BigDecimal.ZERO;

            // กันไม่ให้ลดเกินยอดสินค้า
            discountAmount = off.min(subtotal);
        }
        // TODO: BUY_X_GET_Y, FIXED_PRICE, SHIPPING_DISCOUNT ถ้ามีค่อยเพิ่มทีหลัง

        // กันไม่ให้ติดลบ
        BigDecimal finalAmount = subtotal.subtract(discountAmount);
        if (finalAmount.compareTo(BigDecimal.ZERO) < 0) {
            finalAmount = BigDecimal.ZERO;
        }

        // 7) เซ็ตค่า response กลับไปให้ frontend
        String typeStr = promoType.name(); // ส่งเป็น String เช่น "PERCENT_OFF"

        resp.setValid(true);
        resp.setDiscountType(typeStr);
        resp.setDiscountValue(
                promoType == PromoType.PERCENT_OFF
                        ? (promo.getPercentOff() != null ? promo.getPercentOff() : BigDecimal.ZERO)
                        : (promo.getAmountOff() != null ? promo.getAmountOff() : BigDecimal.ZERO)
        );
        resp.setDiscountAmount(discountAmount);
        resp.setFinalAmount(finalAmount);
        resp.setMessage("ใช้โค้ด " + promo.getCode() + " สำเร็จ");

        return resp;
    }
}
