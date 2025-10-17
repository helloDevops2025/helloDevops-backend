package com.example.backend.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // ✅ ถูกต้อง
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.endsWith;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SuppressWarnings("removal") // ซ่อน warning @MockBean ชั่วคราว
@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired MockMvc mvc;

    @MockitoBean ProductRepository repo;
    @MockitoBean ProductImageRepository imageRepo;

    static class TestRow implements ProductListRow {
        private final Long id;
        private final String productId;
        private final String name;
        private final java.math.BigDecimal price;
        private final Integer quantity;
        private final Boolean inStock;
        private final String category;
        private final String brand;
        private final String imageUrl;

        TestRow(Long id, String productId, String name,
                java.math.BigDecimal price, Integer quantity, Boolean inStock,
                String category, String brand, String imageUrl) {
            this.id = id; this.productId = productId; this.name = name;
            this.price = price; this.quantity = quantity; this.inStock = inStock;
            this.category = category; this.brand = brand; this.imageUrl = imageUrl;
        }
        public Long getId() { return id; }
        public String getProductId() { return productId; }
        public String getName() { return name; }
        public java.math.BigDecimal getPrice() { return price; }
        public Integer getQuantity() { return quantity; }
        public Boolean getInStock() { return inStock; }
        public String getCategory() { return category; }
        public String getBrand() { return brand; }
        public String getImageUrl() { return imageUrl; }
    }



    @Test
    void test_endpoint_shouldReturnOk() throws Exception {
        mvc.perform(get("/api/products/test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"));
    }

    @Test
    void list_shouldReturnProjectionRows() throws Exception {
        TestRow row = new TestRow(
                1L, "P001", "Apple",
                new java.math.BigDecimal("9.99"), 3, true,
                "Fruit", "Pure", "http://x/cover.jpg"
        );
        when(repo.findAllListRows()).thenReturn(java.util.List.of(row));

        mvc.perform(get("/api/products").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].productId").value("P001"))
                .andExpect(jsonPath("$[0].name").value("Apple"))
                .andExpect(jsonPath("$[0].price").value(9.99))
                .andExpect(jsonPath("$[0].quantity").value(3))
                .andExpect(jsonPath("$[0].inStock").value(true))
                .andExpect(jsonPath("$[0].category").value("Fruit"))
                .andExpect(jsonPath("$[0].brand").value("Pure"))
                .andExpect(jsonPath("$[0].imageUrl").value("http://x/cover.jpg"));
    }


    @Test
    void getById_found_shouldReturn200() throws Exception {
        Product p = new Product();
        ReflectionTestUtils.setField(p, "id", 10L);
        p.setProductId("P010");
        p.setName("Mango");
        p.setPrice(new BigDecimal("15.50"));
        when(repo.findById(10L)).thenReturn(Optional.of(p));

        mvc.perform(get("/api/products/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Mango"));
    }

    @Test
    void getById_notFound_shouldReturn404() throws Exception {
        when(repo.findById(999L)).thenReturn(Optional.empty());
        mvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_shouldReturn201_andSetInStockFromQuantity() throws Exception {
        Product saved = new Product();
        ReflectionTestUtils.setField(saved, "id", 123L);
        saved.setName("Orange");
        saved.setPrice(new BigDecimal("20.00"));
        saved.setQuantity(5);
        saved.setInStock(true);
        when(repo.save(any(Product.class))).thenReturn(saved);

        String body = """
          { "name":"Orange", "price":20.00, "quantity":5 }
        """;

        mvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/api/products/123")))
                .andExpect(jsonPath("$.id").value(123))
                .andExpect(jsonPath("$.inStock").value(true));
    }

    @Test
    void uploadCover_png_shouldReturnOkWithImageMeta() throws Exception {
        when(repo.findById(1L)).thenReturn(Optional.of(new Product()));
        when(imageRepo.findFirstByProductIdFkAndIsCoverTrueOrderBySortOrderAscIdAsc(1L))
                .thenReturn(Optional.empty());

        ProductImage savedImg = new ProductImage();
        ReflectionTestUtils.setField(savedImg, "id", 77L);
        savedImg.setProductIdFk(1L);
        savedImg.setFilename("cover-xxx.png");
        savedImg.setContentType("image/png");
        savedImg.setContent(new byte[]{1,2,3});
        when(imageRepo.save(any(ProductImage.class))).thenReturn(savedImg);

        MockMultipartFile file = new MockMultipartFile("file", "x.png", "image/png", new byte[]{1,2,3});

        mvc.perform(multipart("/api/products/{id}/cover", 1L).file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ok"))
                .andExpect(jsonPath("$.imageId").value(77))
                .andExpect(jsonPath("$.imageUrl").exists());
    }

    @Test
    void uploadCover_nonImage_shouldReturn400() throws Exception {
        when(repo.findById(2L)).thenReturn(Optional.of(new Product()));

        MockMultipartFile file = new MockMultipartFile("file", "x.txt", "text/plain", "hello".getBytes());

        mvc.perform(multipart("/api/products/{id}/cover", 2L).file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("only image files")));
    }

    @Test
    void getImageRaw_ok_whenMatchedAndHasBlob() throws Exception {
        ProductImage img = new ProductImage();
        ReflectionTestUtils.setField(img, "id", 5L);
        img.setProductIdFk(3L);
        img.setContentType("image/jpeg");
        img.setFilename("f.jpg");
        img.setContent(new byte[]{10,20});
        when(imageRepo.findById(5L)).thenReturn(Optional.of(img));

        mvc.perform(get("/api/products/3/images/5/raw"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/jpeg"))
                .andExpect(header().string("Cache-Control", "public, max-age=86400"))
                .andExpect(content().bytes(new byte[]{10,20}));
    }

    @Test
    void getImageRaw_notFound_cases() throws Exception {
        when(imageRepo.findById(99L)).thenReturn(Optional.empty());
        mvc.perform(get("/api/products/9/images/99/raw"))
                .andExpect(status().isNotFound());

        ProductImage img = new ProductImage();
        ReflectionTestUtils.setField(img, "id", 7L);
        img.setProductIdFk(123L); // mismatch
        img.setContent(new byte[]{1});
        when(imageRepo.findById(7L)).thenReturn(Optional.of(img));

        mvc.perform(get("/api/products/1/images/7/raw"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteImage_shouldReturn204_whenFoundAndMatch() throws Exception {
        ProductImage img = new ProductImage();
        ReflectionTestUtils.setField(img, "id", 8L);
        img.setProductIdFk(4L);
        when(imageRepo.findById(8L)).thenReturn(Optional.of(img));
        doNothing().when(imageRepo).delete(img);

        mvc.perform(delete("/api/products/4/images/8"))
                .andExpect(status().isNoContent());

        verify(imageRepo).delete(img);
    }

    @Test
    void update_shouldReturn200_whenFound() throws Exception {
        Product existing = new Product();
        ReflectionTestUtils.setField(existing, "id", 50L);
        existing.setName("Old");
        existing.setPrice(new BigDecimal("1.00"));
        existing.setQuantity(0);
        existing.setInStock(false);
        when(repo.findById(50L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        String body = """
          {
            "productId": "P-50",
            "name": "NewName",
            "description": "desc",
            "price": 99.99,
            "quantity": 10,
            "categoryId": 1,
            "brandId": 2
          }
          """;

        mvc.perform(put("/api/products/50")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.inStock").value(true));
    }

    @Test
    void deleteById_shouldReturn204_whenExists() throws Exception {
        when(repo.existsById(7L)).thenReturn(true);
        doNothing().when(repo).deleteById(7L);

        mvc.perform(delete("/api/products/7"))
                .andExpect(status().isNoContent());

        verify(repo).deleteById(7L);
    }

    @Test
    void deleteById_shouldReturn404_whenNotExists() throws Exception {
        when(repo.existsById(700L)).thenReturn(false);
        mvc.perform(delete("/api/products/700"))
                .andExpect(status().isNotFound());
    }
}
