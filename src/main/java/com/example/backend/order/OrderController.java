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
        return ResponseEntity.ok(Map.of("status", "ok"));
    }

    // ✅ GET: All orders
    @GetMapping
    public List<Order> listOrders() {
        return orderService.listAll();
    }

    // ✅ GET: Order by ID (with DTO)
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        Order order = orderService.getById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProductName(),
                        item.getBrandName(),
                        item.getQuantity(),
                        item.getPriceEach(),
                        item.getTotalPrice()
                ))
                .toList();

        BigDecimal total = items.stream()
                .map(OrderItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OrderResponse response = new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getShippingMethod(),
                total,
                items
        );

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
