package com.example.backend.product;

import jakarta.persistence.*;

@Entity
@Table(name = "product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id_fk", nullable = false)
    private Long productIdFk;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_cover", nullable = false)
    private Boolean isCover = false;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    // ==== ใหม่: เก็บรูปใน DB ====
    @Lob
    @Column(name = "content", columnDefinition = "MEDIUMBLOB")
    private byte[] content;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "filename")
    private String filename;

    // ===== getters/setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductIdFk() { return productIdFk; }
    public void setProductIdFk(Long productIdFk) { this.productIdFk = productIdFk; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getIsCover() { return isCover; }
    public void setIsCover(Boolean isCover) { this.isCover = isCover; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public byte[] getContent() { return content; }
    public void setContent(byte[] content) { this.content = content; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
}
