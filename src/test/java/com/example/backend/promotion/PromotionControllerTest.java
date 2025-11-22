package com.example.backend.promotion;

import com.example.backend.product.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * Unit tests for PromotionController (30 test cases)
 */
@ExtendWith(MockitoExtension.class)
public class PromotionControllerTest {

    @Mock
    private PromotionService promotionService;

    @InjectMocks
    private PromotionController promotionController;

    // ===== Helper create dummy objects =====

    private Promotion newPromo() {
        // ไม่แตะ field ด้านในเลย ปลอดภัยสุด
        return new Promotion();
    }

    private Product newProduct() {
        return new Product();
    }

    private PromotionValidateRequest newValidateReq() {
        return new PromotionValidateRequest();
    }

    private PromotionValidateResponse newValidateResp() {
        return new PromotionValidateResponse();
    }

    // =============== 1. list: q=null, status=null =================
    @Test
    void list_withoutFilters_callsSearchWithNulls() {
        List<Promotion> promos = List.of(newPromo());
        when(promotionService.search(null, null)).thenReturn(promos);

        List<Promotion> result = promotionController.list(null, null);

        assertSame(promos, result);
        verify(promotionService, times(1)).search(null, null);
    }

    // =============== 2. list: q only =================
    @Test
    void list_withQOnly_callsSearchWithQAndNullStatus() {
        List<Promotion> promos = List.of(newPromo(), newPromo());
        when(promotionService.search("drink", null)).thenReturn(promos);

        List<Promotion> result = promotionController.list("drink", null);

        assertSame(promos, result);
        verify(promotionService).search("drink", null);
    }

    // =============== 3. list: status only =================
    @Test
    void list_withStatusOnly_callsSearchWithNullQAndStatus() {
        List<Promotion> promos = List.of(newPromo());
        when(promotionService.search(null, "ACTIVE")).thenReturn(promos);

        List<Promotion> result = promotionController.list(null, "ACTIVE");

        assertSame(promos, result);
        verify(promotionService).search(null, "ACTIVE");
    }

    // =============== 4. list: both q and status =================
    @Test
    void list_withQAndStatus_callsSearchWithBoth() {
        List<Promotion> promos = List.of(newPromo());
        when(promotionService.search("beverage", "INACTIVE")).thenReturn(promos);

        List<Promotion> result = promotionController.list("beverage", "INACTIVE");

        assertSame(promos, result);
        verify(promotionService).search("beverage", "INACTIVE");
    }

    // =============== 5. list: empty result =================
    @Test
    void list_returnsEmptyListWhenServiceReturnsEmpty() {
        when(promotionService.search(null, null)).thenReturn(Collections.emptyList());

        List<Promotion> result = promotionController.list(null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // =============== 6. getOne: basic =================
    @Test
    void getOne_returnsPromotionFromService() {
        Promotion promo = newPromo();
        when(promotionService.getById(1L)).thenReturn(promo);

        Promotion result = promotionController.getOne(1L);

        assertSame(promo, result);
        verify(promotionService).getById(1L);
    }

    // =============== 7. getOne: different id =================
    @Test
    void getOne_callsServiceWithCorrectId() {
        Promotion promo = newPromo();
        when(promotionService.getById(99L)).thenReturn(promo);

        Promotion result = promotionController.getOne(99L);

        assertSame(promo, result);
        verify(promotionService).getById(99L);
    }

    // =============== 8. create: basic =================
    @Test
    void create_callsServiceCreateAndReturnsResult() {
        Promotion body = newPromo();
        Promotion saved = newPromo();

        when(promotionService.create(body)).thenReturn(saved);

        Promotion result = promotionController.create(body);

        assertSame(saved, result);
        verify(promotionService).create(body);
    }

    // =============== 9. create: null body (service handle) =================
    @Test
    void create_allowsNullBodyIfServiceHandlesIt() {
        Promotion saved = newPromo();
        when(promotionService.create(null)).thenReturn(saved);

        Promotion result = promotionController.create(null);

        assertSame(saved, result);
        verify(promotionService).create(null);
    }

    // =============== 10. update: basic =================
    @Test
    void update_callsServiceUpdateWithIdAndBody() {
        Promotion body = newPromo();
        Promotion updated = newPromo();
        when(promotionService.update(10L, body)).thenReturn(updated);

        Promotion result = promotionController.update(10L, body);

        assertSame(updated, result);
        verify(promotionService).update(10L, body);
    }

    // =============== 11. update: different id =================
    @Test
    void update_callsServiceUpdateWithDifferentId() {
        Promotion body = newPromo();
        Promotion updated = newPromo();
        when(promotionService.update(20L, body)).thenReturn(updated);

        Promotion result = promotionController.update(20L, body);

        assertSame(updated, result);
        verify(promotionService).update(20L, body);
    }

    // =============== 12. update: null body =================
    @Test
    void update_allowsNullBodyIfServiceHandlesIt() {
        Promotion updated = newPromo();
        when(promotionService.update(5L, null)).thenReturn(updated);

        Promotion result = promotionController.update(5L, null);

        assertSame(updated, result);
        verify(promotionService).update(5L, null);
    }

    // =============== 13. getProducts: basic =================
    @Test
    void getProducts_returnsListFromService() {
        List<Product> products = List.of(newProduct(), newProduct());
        when(promotionService.getProductsOfPromotion(1L)).thenReturn(products);

        List<Product> result = promotionController.getProducts(1L);

        assertSame(products, result);
        verify(promotionService).getProductsOfPromotion(1L);
    }

    // =============== 14. getProducts: empty list =================
    @Test
    void getProducts_returnsEmptyListWhenNoProducts() {
        when(promotionService.getProductsOfPromotion(2L)).thenReturn(Collections.emptyList());

        List<Product> result = promotionController.getProducts(2L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(promotionService).getProductsOfPromotion(2L);
    }

    // =============== 15. attachProducts: basic =================
    @Test
    void attachProducts_callsServiceAttachProductsWithIds() {
        PromotionController.AttachProductsRequest req = new PromotionController.AttachProductsRequest();
        req.setProductIds(List.of(1L, 2L, 3L));

        ResponseEntity<?> response = promotionController.attachProducts(10L, req);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> map = (Map<?, ?>) response.getBody();
        assertEquals(Boolean.TRUE, map.get("ok"));

        verify(promotionService).attachProducts(10L, List.of(1L, 2L, 3L));
    }

    // =============== 16. attachProducts: null productIds =================
    @Test
    void attachProducts_allowsNullProductIds() {
        PromotionController.AttachProductsRequest req = new PromotionController.AttachProductsRequest();
        req.setProductIds(null);

        ResponseEntity<?> response = promotionController.attachProducts(5L, req);

        assertEquals(200, response.getStatusCodeValue());
        verify(promotionService).attachProducts(5L, null);
    }

    // =============== 17. attachProducts: empty list =================
    @Test
    void attachProducts_allowsEmptyProductIdsList() {
        PromotionController.AttachProductsRequest req = new PromotionController.AttachProductsRequest();
        req.setProductIds(Collections.emptyList());

        ResponseEntity<?> response = promotionController.attachProducts(7L, req);

        assertEquals(200, response.getStatusCodeValue());
        verify(promotionService).attachProducts(7L, Collections.emptyList());
    }

    // =============== 18. detachProduct: basic =================
    @Test
    void detachProduct_callsServiceDetachProductAndReturnsNoContent() {
        ResponseEntity<?> response = promotionController.detachProduct(10L, 99L);

        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(promotionService).detachProduct(10L, 99L);
    }

    // =============== 19. detachProduct: different ids =================
    @Test
    void detachProduct_worksWithDifferentIds() {
        ResponseEntity<?> response = promotionController.detachProduct(2L, 3L);

        assertEquals(204, response.getStatusCodeValue());
        verify(promotionService).detachProduct(2L, 3L);
    }

    // =============== 20. validatePromotion: basic =================
    @Test
    void validatePromotion_returnsResponseFromService() {
        PromotionValidateRequest req = newValidateReq();
        PromotionValidateResponse resp = newValidateResp();

        when(promotionService.validatePromotion(req)).thenReturn(resp);

        ResponseEntity<PromotionValidateResponse> result =
                promotionController.validatePromotion(req);

        assertEquals(200, result.getStatusCodeValue());
        assertSame(resp, result.getBody());
        verify(promotionService).validatePromotion(req);
    }

    // =============== 21. validatePromotion: null request =================
    @Test
    void validatePromotion_allowsNullRequestIfServiceHandlesIt() {
        PromotionValidateResponse resp = newValidateResp();
        when(promotionService.validatePromotion(null)).thenReturn(resp);

        ResponseEntity<PromotionValidateResponse> result =
                promotionController.validatePromotion(null);

        assertEquals(200, result.getStatusCodeValue());
        assertSame(resp, result.getBody());
        verify(promotionService).validatePromotion(null);
    }

    // =============== 22. debugValidate: returns fixed string =================
    @Test
    void debugValidate_returnsFixedString() {
        String result = promotionController.debugValidate();
        assertEquals("validate-endpoint-loaded", result);
    }

    // =============== 23. list: verify exact parameters with empty strings =================
    @Test
    void list_withEmptyStrings_passesEmptyStringsToService() {
        when(promotionService.search("", "")).thenReturn(Collections.emptyList());

        List<Promotion> result = promotionController.list("", "");

        assertNotNull(result);
        verify(promotionService).search("", "");
    }

    // =============== 24. list: capture arguments =================
    @Test
    void list_usesGivenQAndStatusArguments() {
        ArgumentCaptor<String> qCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> statusCaptor = ArgumentCaptor.forClass(String.class);

        when(promotionService.search(any(), any())).thenReturn(Collections.emptyList());

        promotionController.list("abc", "ACTIVE");

        verify(promotionService).search(qCaptor.capture(), statusCaptor.capture());
        assertEquals("abc", qCaptor.getValue());
        assertEquals("ACTIVE", statusCaptor.getValue());
    }

    // =============== 25. attachProducts: verify request object mapping =================
    @Test
    void attachProducts_passesExactlySameListToService() {
        PromotionController.AttachProductsRequest req = new PromotionController.AttachProductsRequest();
        List<Long> ids = List.of(10L, 20L);
        req.setProductIds(ids);

        promotionController.attachProducts(1L, req);

        ArgumentCaptor<List<Long>> captor = ArgumentCaptor.forClass(List.class);
        verify(promotionService).attachProducts(eq(1L), captor.capture());
        assertSame(ids, captor.getValue());
    }

    // =============== 26. getProducts: multiple calls with different ids =================
    @Test
    void getProducts_canBeCalledMultipleTimesWithDifferentIds() {
        List<Product> products1 = List.of(newProduct());
        List<Product> products2 = List.of(newProduct(), newProduct());

        when(promotionService.getProductsOfPromotion(1L)).thenReturn(products1);
        when(promotionService.getProductsOfPromotion(2L)).thenReturn(products2);

        List<Product> result1 = promotionController.getProducts(1L);
        List<Product> result2 = promotionController.getProducts(2L);

        assertSame(products1, result1);
        assertSame(products2, result2);
        verify(promotionService).getProductsOfPromotion(1L);
        verify(promotionService).getProductsOfPromotion(2L);
    }

    // =============== 27. create: ensure returned object is exactly the same as service output =================
    @Test
    void create_returnsExactlyServiceOutputInstance() {
        Promotion body = newPromo();
        Promotion serviceResult = newPromo();

        when(promotionService.create(body)).thenReturn(serviceResult);

        Promotion result = promotionController.create(body);

        assertSame(serviceResult, result);
    }

    // =============== 28. update: ensure returned object is exactly the same as service output =================
    @Test
    void update_returnsExactlyServiceOutputInstance() {
        Promotion body = newPromo();
        Promotion serviceResult = newPromo();

        when(promotionService.update(3L, body)).thenReturn(serviceResult);

        Promotion result = promotionController.update(3L, body);

        assertSame(serviceResult, result);
    }

    // =============== 29. list: multiple calls with different filters =================
    @Test
    void list_canBeCalledMultipleTimesWithDifferentFilters() {
        List<Promotion> promos1 = List.of(newPromo());
        List<Promotion> promos2 = List.of(newPromo(), newPromo());

        when(promotionService.search("A", "ACTIVE")).thenReturn(promos1);
        when(promotionService.search("B", "INACTIVE")).thenReturn(promos2);

        List<Promotion> r1 = promotionController.list("A", "ACTIVE");
        List<Promotion> r2 = promotionController.list("B", "INACTIVE");

        assertSame(promos1, r1);
        assertSame(promos2, r2);
        verify(promotionService).search("A", "ACTIVE");
        verify(promotionService).search("B", "INACTIVE");
    }

    // =============== 30. validatePromotion: multiple calls =================
    @Test
    void validatePromotion_canBeCalledMultipleTimes() {
        PromotionValidateRequest req1 = newValidateReq();
        PromotionValidateRequest req2 = newValidateReq();
        PromotionValidateResponse resp1 = newValidateResp();
        PromotionValidateResponse resp2 = newValidateResp();

        when(promotionService.validatePromotion(req1)).thenReturn(resp1);
        when(promotionService.validatePromotion(req2)).thenReturn(resp2);

        ResponseEntity<PromotionValidateResponse> r1 = promotionController.validatePromotion(req1);
        ResponseEntity<PromotionValidateResponse> r2 = promotionController.validatePromotion(req2);

        assertSame(resp1, r1.getBody());
        assertSame(resp2, r2.getBody());
        verify(promotionService).validatePromotion(req1);
        verify(promotionService).validatePromotion(req2);
    }
}
