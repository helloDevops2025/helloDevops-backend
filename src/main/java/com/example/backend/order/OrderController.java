package com.example.backend.order;

import com.example.backend.order.dto.OrderItemResponse;
import com.example.backend.order.dto.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

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
    public List<OrderResponse> listOrders() {
        return orderService.listAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // ✅ GET: Order by ID (with DTO)
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        Order order = orderService.getById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return ResponseEntity.ok(convertToResponse(order));
    }

    // ✅ POST: Create order
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody Order order) {
        Order created = orderService.createOrder(order);
        return ResponseEntity.ok(convertToResponse(created));
    }

    // ✅ PUT: Update order status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return orderService.getById(id).map(order -> {
            String status = body.get("status");
            order.setOrderStatus(status);

            // ✅ ตั้งเวลาอัตโนมัติตามสถานะ
            switch (status.toUpperCase()) {
                case "PREPARING" -> order.setPreparingAt(LocalDateTime.now());
                case "READY_TO_SHIP" -> order.setReadyAt(LocalDateTime.now());
                case "SHIPPING" -> order.setShippingAt(LocalDateTime.now());
                case "DELIVERED" -> order.setDeliveredAt(LocalDateTime.now());
                case "CANCELLED" -> order.setCancelledAt(LocalDateTime.now());
            }

            orderService.updateOrder(order);
            return ResponseEntity.ok(convertToResponse(order));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Helper method: แปลง Order → OrderResponse
    private OrderResponse convertToResponse(Order order) {
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

        return new OrderResponse(
                order.getId(),
                order.getOrderCode(),
                order.getCustomerName(),
                order.getCustomerPhone(),
                order.getShippingAddress(),
                order.getPaymentMethod(),
                order.getShippingMethod(),
                order.getOrderStatus(),
                total,
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getPreparingAt(),
                order.getReadyAt(),
                order.getShippingAt(),
                order.getDeliveredAt(),
                order.getCancelledAt(),
                items
        );
    }
}
