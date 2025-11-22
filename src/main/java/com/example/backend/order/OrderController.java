package com.example.backend.order;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok(Map.of("status", "ok  test"));
    }

    // ✅ GET: All orders
    @GetMapping
    public List<Order> listOrders() {
        return orderService.listAll();
    }

    // ✅ GET: Order by ID (with full details)
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        Order order = orderService.getById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ ดึง orderItems ทั้งหมดจาก repository เพื่อให้แน่ใจว่าโหลดครบ
        List<OrderItem> orderItems = orderService.getOrderItemsByOrderId(id);

        // ✅ ดึงประวัติสถานะ (ยังไม่ใช้ฝั่ง FE แต่อาจใช้ต่อได้)
        List<OrderStatusHistory> history = orderService.getStatusHistoryByOrderId(id);

        // ✅ เติมชื่อแบรนด์จาก BrandRepository
        for (OrderItem item : orderItems) {
            if (item.getProduct() != null && item.getProduct().getBrandId() != null) {
                orderService.getBrandNameById(item.getProduct().getBrandId())
                        .ifPresent(item::setBrandName);
            }
        }

        // ====== totals ที่อ่านจาก entity + กัน null ======
        BigDecimal subtotal    = order.getSubtotal()     != null ? order.getSubtotal()     : BigDecimal.ZERO;
        BigDecimal discountTot = order.getDiscountTotal()!= null ? order.getDiscountTotal(): BigDecimal.ZERO;
        BigDecimal shippingFee = order.getShippingFee()  != null ? order.getShippingFee()  : BigDecimal.ZERO;
        BigDecimal taxTotal    = order.getTaxTotal()     != null ? order.getTaxTotal()     : BigDecimal.ZERO;
        BigDecimal grandTotal  = order.getGrandTotal()   != null ? order.getGrandTotal()   : BigDecimal.ZERO;

        // ถ้า grandTotal > 0 ให้ใช้เป็น totalAmount ถ้าไม่ → ใช้ getTotalAmount() (คำนวณ fallback)
        BigDecimal totalAmount = (grandTotal.compareTo(BigDecimal.ZERO) > 0)
                ? grandTotal
                : order.getTotalAmount();
        // ==================================================

        // ✅ รวมข้อมูลทั้งหมดเป็น Map (ให้ FE ใช้ง่าย)
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

        // 🔥 สำคัญ: ตัวที่หน้า Tracking ใช้
        response.put("subtotal", subtotal);
        response.put("discountTotal", discountTot);
        response.put("shippingFee", shippingFee);
        response.put("taxTotal", taxTotal);
        response.put("grandTotal", grandTotal);
        response.put("totalAmount", totalAmount);

        // จะส่ง history ไปด้วยหรือไม่ก็ได้ (ตอนนี้ FE ยังไม่ได้ใช้)
        response.put("statusHistory", history);

        return ResponseEntity.ok(response);
    }

    // ✅ POST: Create order
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order created = orderService.createOrder(order);
        return ResponseEntity.ok(created);
    }

    // ✅ PUT: Update order status + record in history
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        String note = body.getOrDefault("note", "");

        return orderService.getById(id).map(order -> {
            order.setOrderStatus(newStatus);
            orderService.updateOrder(order);
            // ถ้าจะบันทึก history ด้วย ก็เรียก addStatusHistory ตรงนี้ได้
            // orderService.addStatusHistory(order, newStatus, note);
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