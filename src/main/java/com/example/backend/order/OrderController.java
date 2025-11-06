package com.example.backend.order;

import com.example.backend.order.dto.OrderItemResponse;
import com.example.backend.order.dto.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;
    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of("status", "ok  test"));
    }

    // ✅ GET: All orders
    @GetMapping
    public List<Order> listOrders() {
        return orderService.listAll();
    }

    // ✅ GET: Order by ID (with DTO)
// ✅ GET: Order by ID (with full details)
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Order order = orderService.getById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ ดึง orderItems ทั้งหมดจาก repository เพื่อให้แน่ใจว่าโหลดครบ
        List<OrderItem> orderItems = orderService.getOrderItemsByOrderId(id);

        // ✅ เติมชื่อแบรนด์จาก BrandRepository
        for (OrderItem item : orderItems) {
            if (item.getProduct() != null && item.getProduct().getBrandId() != null) {
                orderService.getBrandNameById(item.getProduct().getBrandId())
                        .ifPresent(item::setBrandName);
            }
        }

        // ✅ รวมข้อมูลทั้งหมดเป็น Map (ไม่ใช้ DTO ตัดข้อมูลออก)
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", order.getId());
        response.put("orderCode", order.getOrderCode());
        response.put("customerName", order.getCustomerName());
        response.put("customerPhone", order.getCustomerPhone());
        response.put("shippingAddress", order.getShippingAddress());
        response.put("paymentMethod", order.getPaymentMethod());
        response.put("shippingMethod", order.getShippingMethod());
        response.put("orderStatus", order.getOrderStatus());
        response.put("createdAt", order.getCreatedAt());
        response.put("updatedAt", order.getUpdatedAt());
        response.put("orderItems", orderItems);
        response.put("totalAmount", order.getTotalAmount());

        return ResponseEntity.ok(response);
    }


    // ✅ POST: Create order
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order created = orderService.createOrder(order);
        return ResponseEntity.ok(created);
    }

    // ✅ PUT: Update order status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return orderService.getById(id).map(order -> {
            String status = body.get("status");
            order.setOrderStatus(status);
            orderService.updateOrder(order);
            return ResponseEntity.ok(order);
        }).orElse(ResponseEntity.notFound().build());
    }
    // ✅ DELETE: Delete order by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        return orderService.getById(id).map(order -> {
            orderService.deleteOrder(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Order deleted successfully",
                    "orderId", id
            ));
        }).orElse(ResponseEntity.status(404).body(Map.of(
                "error", "Order not found"
        )));
    }

}
