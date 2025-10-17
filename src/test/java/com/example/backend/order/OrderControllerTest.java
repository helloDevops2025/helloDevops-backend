package com.example.backend.order;

import com.example.backend.product.Product;
import com.example.backend.order.dto.OrderItemResponse;
import com.example.backend.order.dto.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private Order mockOrder;
    private OrderItem mockItem;
    private Product mockProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // 🧱 Mock Product
        mockProduct = new Product();
        mockProduct.setName("ข้าวหอมมะลิใหม่ 5กก.");
        mockProduct.setPrice(BigDecimal.valueOf(150));

        // 🧱 Mock OrderItem
        mockItem = new OrderItem();
        mockItem.setProduct(mockProduct);
        mockItem.setQuantity(2);

        // 🧱 Mock Order
        mockOrder = new Order();
        mockOrder.setCustomerName("Pim Peace");
        mockOrder.setCustomerPhone("0800000000");
        mockOrder.setShippingAddress("Bangkok");
        mockOrder.setShippingMethod("STANDARD");
        mockOrder.setPaymentMethod("COD");
        mockOrder.setOrderStatus("Preparing");
        mockOrder.setOrderItems(List.of(mockItem));
    }

    // ✅ Test 1: /test endpoint
    @Test
    void testHealthCheck() {
        ResponseEntity<?> res = orderController.test();
        assertEquals(200, res.getStatusCodeValue());
        assertEquals("ok", ((Map<?, ?>) res.getBody()).get("status"));
    }

    // ✅ Test 2: List all orders
    @Test
    void testListOrders() {
        when(orderService.listAll()).thenReturn(List.of(mockOrder));
        List<Order> result = orderController.listOrders();
        assertEquals(1, result.size());
        verify(orderService, times(1)).listAll();
    }

    // ✅ Test 3: Get order by ID success
    @Test
    void testGetOrderByIdSuccess() {
        when(orderService.getById(1L)).thenReturn(Optional.of(mockOrder));
        ResponseEntity<OrderResponse> response = orderController.getOrderById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Pim Peace", response.getBody().getCustomerName());
        assertEquals(BigDecimal.valueOf(300), response.getBody().getTotalAmount());
    }

    // ✅ Test 4: Get order by ID not found
    @Test
    void testGetOrderByIdNotFound() {
        when(orderService.getById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> orderController.getOrderById(99L));
    }

    // ✅ Test 5: Create order
    @Test
    void testCreateOrder() {
        when(orderService.createOrder(any(Order.class))).thenReturn(mockOrder);
        ResponseEntity<Order> res = orderController.createOrder(mockOrder);
        assertEquals(200, res.getStatusCodeValue());
        assertEquals("Pim Peace", res.getBody().getCustomerName());
    }

    // ✅ Test 6: Update status success
    @Test
    void testUpdateStatusSuccess() {
        when(orderService.getById(1L)).thenReturn(Optional.of(mockOrder));
        Map<String, String> body = Map.of("status", "Delivered");

        ResponseEntity<?> res = orderController.updateStatus(1L, body);
        assertEquals(200, res.getStatusCodeValue());
        assertEquals("Delivered", mockOrder.getOrderStatus());
    }

    // ✅ Test 7: Update status not found
    @Test
    void testUpdateStatusNotFound() {
        when(orderService.getById(99L)).thenReturn(Optional.empty());
        ResponseEntity<?> res = orderController.updateStatus(99L, Map.of("status", "Delivered"));
        assertEquals(404, res.getStatusCodeValue());
    }

    // ✅ Test 8: Delete order success
    @Test
    void testDeleteOrderSuccess() {
        when(orderService.getById(1L)).thenReturn(Optional.of(mockOrder));
        ResponseEntity<?> res = orderController.deleteOrder(1L);
        verify(orderService, times(1)).deleteOrder(1L);
        assertEquals(200, res.getStatusCodeValue());
    }

    // ✅ Test 9: Delete order not found
    @Test
    void testDeleteOrderNotFound() {
        when(orderService.getById(99L)).thenReturn(Optional.empty());
        ResponseEntity<?> res = orderController.deleteOrder(99L);
        assertEquals(404, res.getStatusCodeValue());
    }

    // ✅ Test 10: Total amount should match quantity * price
    @Test
    void testTotalAmountCalculation() {
        BigDecimal total = mockOrder.getTotalAmount();
        assertEquals(BigDecimal.valueOf(300), total);
    }

    // ✅ Test 11: OrderItem total price works
    @Test
    void testOrderItemTotalPrice() {
        assertEquals(BigDecimal.valueOf(300), mockItem.getTotalPrice());
    }

    // ✅ Test 12: Product name is derived correctly
    @Test
    void testOrderItemProductName() {
        assertEquals("ข้าวหอมมะลิใหม่ 5กก.", mockItem.getProductName());
    }

    // ✅ Test 13: PriceEach is derived correctly
    @Test
    void testOrderItemPriceEach() {
        assertEquals(BigDecimal.valueOf(150), mockItem.getPriceEach());
    }

    // ✅ Test 14: Order with no items = 0 total
    @Test
    void testEmptyOrderItems() {
        mockOrder.setOrderItems(Collections.emptyList());
        assertEquals(BigDecimal.ZERO, mockOrder.getTotalAmount());
    }

    // ✅ Test 15: OrderService update called once
    @Test
    void testUpdateStatusCallsServiceUpdate() {
        when(orderService.getById(1L)).thenReturn(Optional.of(mockOrder));
        Map<String, String> body = Map.of("status", "Ready");
        orderController.updateStatus(1L, body);
        verify(orderService, times(1)).updateOrder(any(Order.class));
    }

    // ✅ Test 16: Order code auto-generated
    @Test
    void testOrderCodeAutoGenerated() {
        Order newOrder = new Order();
        newOrder.onPrePersist();
        assertTrue(newOrder.getOrderCode().startsWith("TEMP-"));
    }

    // ✅ Test 17: Product null = price zero
    @Test
    void testNullProductPriceEach() {
        OrderItem item = new OrderItem();
        item.setQuantity(2);
        item.setProduct(null);
        assertEquals(BigDecimal.ZERO, item.getPriceEach());
    }

    // ✅ Test 18: Null quantity = total 0
    @Test
    void testNullQuantityTotalPrice() {
        mockItem.setQuantity(null);
        assertEquals(BigDecimal.ZERO, mockItem.getTotalPrice());
    }

    // ✅ Test 19: Brand name set and get
    @Test
    void testBrandNameGetterSetter() {
        mockItem.setBrandName("Sombun");
        assertEquals("Sombun", mockItem.getBrandName());
    }

    // ✅ Test 20: Ensure CORS annotation present
    @Test
    void testCrossOriginAnnotationPresent() {
        assertTrue(orderController.getClass().isAnnotationPresent(CrossOrigin.class));
    }
}
