package com.example.backend.promotion;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "promotions")
// ให้ field camelCase ถูกแปลงเป็น JSON แบบ snake_case เช่น promoType -> promo_type
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Promotion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String code;

  @Column(columnDefinition = "TEXT")
  private String description;

  // PERCENT_OFF, AMOUNT_OFF, BUY_X_GET_Y, FIXED_PRICE, SHIPPING_DISCOUNT
  @Enumerated(EnumType.STRING)
  @Column(name = "promo_type", nullable = false)
  private PromoType promoType;

  // ORDER, PRODUCT, CATEGORY, BRAND
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private PromoScope scope;

  @Column(name = "percent_off")
  private BigDecimal percentOff;

  @Column(name = "amount_off")
  private BigDecimal amountOff;

  @Column(name = "fixed_price")
  private BigDecimal fixedPrice;

  @Column(name = "buy_qty")
  private Integer buyQty;

  @Column(name = "get_qty")
  private Integer getQty;

  @Column(name = "applies_to_shipping")
  private Boolean appliesToShipping = false;

  @Column(name = "min_order_amount")
  private BigDecimal minOrderAmount;

  @Column(name = "min_quantity")
  private Integer minQuantity;

  // EXCLUSIVE, STACKABLE, PRIORITY
  @Enumerated(EnumType.STRING)
  @Column(name = "stack_mode")
  private StackMode stackMode = StackMode.EXCLUSIVE;

  private Integer priority = 100;

  // DRAFT, ACTIVE, PAUSED, EXPIRED
  @Enumerated(EnumType.STRING)
  private PromotionStatus status = PromotionStatus.DRAFT;

  @Column(name = "start_at")
  private OffsetDateTime startAt;

  @Column(name = "end_at")
  private OffsetDateTime endAt;

  private String timezone;

  // ===== getters / setters =====
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public String getCode() { return code; }
  public void setCode(String code) { this.code = code; }

  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }

  public PromoType getPromoType() { return promoType; }
  public void setPromoType(PromoType promoType) { this.promoType = promoType; }

  public PromoScope getScope() { return scope; }
  public void setScope(PromoScope scope) { this.scope = scope; }

  public BigDecimal getPercentOff() { return percentOff; }
  public void setPercentOff(BigDecimal percentOff) { this.percentOff = percentOff; }

  public BigDecimal getAmountOff() { return amountOff; }
  public void setAmountOff(BigDecimal amountOff) { this.amountOff = amountOff; }

  public BigDecimal getFixedPrice() { return fixedPrice; }
  public void setFixedPrice(BigDecimal fixedPrice) { this.fixedPrice = fixedPrice; }

  public Integer getBuyQty() { return buyQty; }
  public void setBuyQty(Integer buyQty) { this.buyQty = buyQty; }

  public Integer getGetQty() { return getQty; }
  public void setGetQty(Integer getQty) { this.getQty = getQty; }

  public Boolean getAppliesToShipping() { return appliesToShipping; }
  public void setAppliesToShipping(Boolean appliesToShipping) { this.appliesToShipping = appliesToShipping; }

  public BigDecimal getMinOrderAmount() { return minOrderAmount; }
  public void setMinOrderAmount(BigDecimal minOrderAmount) { this.minOrderAmount = minOrderAmount; }

  public Integer getMinQuantity() { return minQuantity; }
  public void setMinQuantity(Integer minQuantity) { this.minQuantity = minQuantity; }

  public StackMode getStackMode() { return stackMode; }
  public void setStackMode(StackMode stackMode) { this.stackMode = stackMode; }

  public Integer getPriority() { return priority; }
  public void setPriority(Integer priority) { this.priority = priority; }

  public PromotionStatus getStatus() { return status; }
  public void setStatus(PromotionStatus status) { this.status = status; }

  public OffsetDateTime getStartAt() { return startAt; }
  public void setStartAt(OffsetDateTime startAt) { this.startAt = startAt; }

  public OffsetDateTime getEndAt() { return endAt; }
  public void setEndAt(OffsetDateTime endAt) { this.endAt = endAt; }

  public String getTimezone() { return timezone; }
  public void setTimezone(String timezone) { this.timezone = timezone; }
}
