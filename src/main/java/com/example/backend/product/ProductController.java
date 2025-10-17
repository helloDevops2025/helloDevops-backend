package com.example.backend.product;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductRepository repo;
    private final ProductImageRepository imageRepo; // ต้องฉีดตัวนี้ด้วย

    // constructor
    public ProductController(ProductRepository repo, ProductImageRepository imageRepo) {
        this.repo = repo;
        this.imageRepo = imageRepo;
    }

    // สำหรับทดสอบระบบ
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(java.util.Map.of("status", "ok"));
    }

    // ------ READ (list) ------
    // ✅ ใช้ Projection ที่ join category/brand + รูป cover ให้พร้อม
    @GetMapping
    public List<ProductListRow> list() {
        return repo.findAllListRows();
    }

    // ------ READ (by id) ------
    // จะยังคืนเป็น Entity ได้ เพราะเราเก็บ FK เป็น Long (ไม่มี relation)
    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ------ CREATE ------
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product p) {
        // เติมค่า inStock อัตโนมัติถ้าไม่ได้ส่งมา
        if (p.getInStock() == null) {
            Integer q = p.getQuantity();
            p.setInStock(q != null && q > 0);
        }
        Product saved = repo.save(p);
        return ResponseEntity.created(URI.create("/api/products/" + saved.getId())).body(saved);
    }

    // ========== อัปโหลดรูป cover เก็บใน DB ==========
    @PostMapping("/{id}/cover")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<?> uploadCoverToBLOB(
            @PathVariable Long id,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file
    ) {
        try {
            var opt = repo.findById(id);
            if (opt.isEmpty()) return ResponseEntity.notFound().build();

            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("file is missing");
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("only image files are allowed");
            }

            // ปิด cover เดิม
            imageRepo.findFirstByProductIdFkAndIsCoverTrueOrderBySortOrderAscIdAsc(id)
                    .ifPresent(old -> { old.setIsCover(false); imageRepo.save(old); });

            // สร้างชื่อไฟล์แบบปลอดภัย
            String ext = switch (contentType) {
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                case "image/gif" -> ".gif";
                default -> ".jpg";
            };
            String filename = "cover-" + System.currentTimeMillis() + ext;

            // เขียนลง DB (เก็บเป็น BLOB)
            ProductImage img = new ProductImage();
            img.setProductIdFk(id);
            img.setIsCover(true);
            img.setSortOrder(0);
            img.setFilename(filename);
            img.setContentType(contentType);
            img.setContent(file.getBytes());
            img.setImageUrl(null); // ไม่ใช้ไฟล์บนดิสก์

            ProductImage saved = imageRepo.save(img);

            // สร้าง URL สำหรับเรียกรูป (absolute)
            String rawPath  = "/api/products/" + id + "/images/" + saved.getId() + "/raw";
            String imageUrl = org.springframework.web.servlet.support.ServletUriComponentsBuilder
                    .fromCurrentContextPath()    // เช่น http://127.0.0.1:8080
                    .path(rawPath)
                    .toUriString();              // => http://127.0.0.1:8080/api/products/.../raw

            return ResponseEntity.ok(java.util.Map.of(
                    "status",   "ok",
                    "imageId",  saved.getId(),
                    "imageUrl", imageUrl,  // 👈 FE ใช้อันนี้เสมอ
                    "rawUrl",   rawPath    // คงไว้ชั่วคราวเพื่อ backward-compat
            ));

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("upload failed: " + ex.getMessage());
        }
    }

    @GetMapping("/{id}/images/__ping")
    public java.util.Map<String,String> ping(@PathVariable Long id) {
        return java.util.Map.of("ok","true","id", String.valueOf(id));
    }

    // @GetMapping("/{id}/images")
    // public java.util.List<java.util.Map<String,Object>> listImages(@PathVariable Long id) {
    //     return imageRepo.findByProductIdFkOrderBySortOrderAscIdAsc(id).stream()
    //             .map(pi -> java.util.Map.<String,Object>of(
    //                     "id", pi.getId(),
    //                     "filename", pi.getFilename(),
    //                     "isCover", java.lang.Boolean.TRUE.equals(pi.getIsCover()),
    //                     "sortOrder", pi.getSortOrder(),
    //                     "contentType", pi.getContentType(),
    //                     "rawUrl", "/api/products/" + id + "/images/" + pi.getId() + "/raw"
    //             ))
    //             .collect(java.util.stream.Collectors.toList()); // << แทน .toList()
    // }

    @GetMapping("/{id}/images")
    public java.util.List<java.util.Map<String,Object>> listImages(@PathVariable Long id) {
    return imageRepo.findByProductIdFkOrderBySortOrderAscIdAsc(id).stream()
        .map(pi -> {
            String rawPath = "/api/products/" + id + "/images/" + pi.getId() + "/raw";
            boolean hasBlob = pi.getContent() != null && pi.getContent().length > 0;

            // ถ้าเป็น BLOB → ใช้ /raw, ถ้าเป็นรูป seed ที่เป็น path ใน DB → ใช้ imageUrl เดิม
            String imageUrl = hasBlob
                ? ServletUriComponentsBuilder.fromCurrentContextPath().path(rawPath).toUriString()
                : pi.getImageUrl();

            // เผื่อ imageUrl เดิมเป็น relative, แปลงเป็น absolute
            if (imageUrl != null && !imageUrl.startsWith("http")) {
            imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath().path(imageUrl).toUriString();
            }

            return java.util.Map.<String,Object>of(
                "id",        pi.getId(),
                "filename",  pi.getFilename(),
                "isCover",   java.lang.Boolean.TRUE.equals(pi.getIsCover()),
                "sortOrder", pi.getSortOrder(),
                "contentType", pi.getContentType(),
                "imageUrl",  imageUrl
            );
        })
        .collect(java.util.stream.Collectors.toList());
    }


    @GetMapping("/{id}/images/{imageId}/raw")
    public ResponseEntity<byte[]> getImageRaw(@PathVariable Long id, @PathVariable Long imageId) {
        var opt = imageRepo.findById(imageId);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var img = opt.get();
        if (!img.getProductIdFk().equals(id) || img.getContent() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header("Content-Type", img.getContentType() != null ? img.getContentType() : "application/octet-stream")
                .header("Cache-Control", "public, max-age=86400")
                .eTag("\"" + (img.getFilename() == null ? "x" : img.getFilename()) + "-" + img.getContent().length + "\"")
                .body(img.getContent());
    }

    // ========== UPDATE image ==========
    @PutMapping("/{id}/images/{imageId}")
    @CrossOrigin(origins = "http://localhost:5173")
    public ResponseEntity<?> replaceImage(
            @PathVariable Long id,
            @PathVariable Long imageId,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
            @RequestParam(value = "isCover", required = false) Boolean isCover,
            @RequestParam(value = "sortOrder", required = false) Integer sortOrder
    ) {
        try {
            var opt = imageRepo.findById(imageId);
            if (opt.isEmpty()) return ResponseEntity.notFound().build();

            var img = opt.get();
            if (!img.getProductIdFk().equals(id)) {
                return ResponseEntity.badRequest().body("productId mismatch");
            }

            if (file == null || file.isEmpty()) {
                return ResponseEntity.badRequest().body("file is missing");
            }
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("only image files are allowed");
            }

            // ถ้า set cover = true → ปิด cover เดิมก่อน
            if (Boolean.TRUE.equals(isCover)) {
                imageRepo.findFirstByProductIdFkAndIsCoverTrueOrderBySortOrderAscIdAsc(id)
                        .ifPresent(old -> {
                            if (!old.getId().equals(imageId)) {
                                old.setIsCover(false);
                                imageRepo.save(old);
                            }
                        });
            }

            // สร้างชื่อไฟล์ใหม่
            String ext = switch (contentType) {
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                case "image/gif" -> ".gif";
                default -> ".jpg";
            };
            String filename = "img-" + System.currentTimeMillis() + ext;

            // อัปเดตข้อมูล
            img.setFilename(filename);
            img.setContentType(contentType);
            img.setContent(file.getBytes());
            img.setIsCover(isCover != null ? isCover : img.getIsCover());
            img.setSortOrder(sortOrder != null ? sortOrder : img.getSortOrder());

            ProductImage updated = imageRepo.save(img);

            String rawUrl = "/api/products/" + id + "/images/" + updated.getId() + "/raw";
            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(rawUrl)
                    .toUriString();
            return ResponseEntity.ok(java.util.Map.of(
                    "status", "updated",
                    "id", updated.getId(),
                    "filename", updated.getFilename(),
                    "isCover", updated.getIsCover(),
                    "sortOrder", updated.getSortOrder(),
                    "imageUrl", imageUrl,   // 👈 ให้ FE ใช้ตัวนี้เสมอ
                    "rawUrl", rawUrl
            ));

        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("update failed: " + ex.getMessage());
        }
    }


    // ========== ลบรูป ==========
   @DeleteMapping("/{id}/images/{imageId}")
   @CrossOrigin(origins = "http://localhost:5173")
   public ResponseEntity<Void> deleteImage(@PathVariable Long id,
                                           @PathVariable Long imageId) {
       System.out.println(">>> DELETE /api/products/" + id + "/images/" + imageId);
       return imageRepo.findById(imageId)
               .filter(img -> img.getProductIdFk().equals(id))
               .map(img -> {
                   System.out.println(">>> Found image, deleting...");
                   imageRepo.delete(img);
                   return ResponseEntity.noContent().<Void>build();
               })
               .orElseGet(() -> {
                   System.out.println(">>> Not found (image or product mismatch).");
                   return ResponseEntity.notFound().build();
               });
   }


    // ------ UPDATE (by id) ------
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product req) {
        return repo.findById(id).map(p -> {
            p.setProductId(req.getProductId());
            p.setName(req.getName());
            p.setDescription(req.getDescription());   // ✅ ใหม่
            p.setPrice(req.getPrice());
            p.setQuantity(req.getQuantity());
            // ถ้า inStock ไม่ส่งมา คำนวณจาก quantity ให้
            p.setInStock(req.getInStock() != null ? req.getInStock()
                    : (req.getQuantity() != null && req.getQuantity() > 0));
            p.setCategoryId(req.getCategoryId());     // ✅ ใช้ FK id
            p.setBrandId(req.getBrandId());           // ✅ ใช้ FK id
            return ResponseEntity.ok(repo.save(p));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // ------ DELETE (by id) ------
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ------ (ออปชัน) DELETE (by productId) ------
    @DeleteMapping("/by-product-id/{productId}")
    public ResponseEntity<Void> deleteByProductId(@PathVariable String productId) {
        ResponseEntity<Void> notFound = ResponseEntity.<Void>notFound().build();
        return repo.findByProductId(productId)
                .map(p -> { repo.delete(p); return ResponseEntity.noContent().<Void>build(); })
                .orElse(notFound);
    }

    @DeleteMapping("/{id}/cover")
    public ResponseEntity<?> deleteCurrentCover(@PathVariable Long id,
                                                @RequestParam(defaultValue = "unset") String mode) {
        var coverOpt = imageRepo.findFirstByProductIdFkAndIsCoverTrueOrderBySortOrderAscIdAsc(id);
        if (coverOpt.isEmpty()) return ResponseEntity.notFound().build();

        var img = coverOpt.get();
        if ("delete".equalsIgnoreCase(mode)) {
            imageRepo.delete(img);
        } else { // unset
            img.setIsCover(false);
            imageRepo.save(img);
        }
        return ResponseEntity.noContent().build();
    }



    // ========== ดึงรูป cover (สำหรับฝั่ง User) ==========
    @GetMapping("/{id}/cover")
    public ResponseEntity<?> getCurrentCover(@PathVariable Long id) {
        // หา cover image ล่าสุด
        var coverOpt = imageRepo.findFirstByProductIdFkAndIsCoverTrueOrderBySortOrderAscIdAsc(id);
        if (coverOpt.isEmpty()) {
            // ถ้าไม่มี → คืน placeholder หรือ 404
            return ResponseEntity.notFound().build();
        }

        var img = coverOpt.get();

        // ถ้าเป็นรูปใน DB (BLOB)
        if (img.getContent() != null && img.getContent().length > 0) {
            return ResponseEntity.ok()
                    .header("Content-Type", img.getContentType() != null ? img.getContentType() : "image/jpeg")
                    .header("Cache-Control", "public, max-age=86400")
                    .body(img.getContent());
        }

        // ถ้าเป็นรูปที่เก็บเป็น URL (บนเครื่องหรือ S3)
        if (img.getImageUrl() != null) {
            // redirect ไปที่ไฟล์จริง
            return ResponseEntity.status(302)
                    .header("Location", img.getImageUrl())
                    .build();
        }

        // ถ้าไม่มีทั้งคู่
        return ResponseEntity.notFound().build();
    }


}