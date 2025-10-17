package com.example.backend.order;

import com.example.backend.product.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final BrandRepository brandRepo;
    private final OrderItemRepository orderItemRepo;

    public OrderService(OrderRepository orderRepo, ProductRepository productRepo,
                        BrandRepository brandRepo, OrderItemRepository orderItemRepo) {
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.brandRepo = brandRepo;
        this.orderItemRepo = orderItemRepo;
    }

    // ✅ สร้างออเดอร์ใหม่
    @Transactional
    public Order createOrder(Order order) {
        Order savedOrder = orderRepo.save(order);
        savedOrder.setOrderCode(String.format("#ORD%05d", savedOrder.getId()));

        if (order.getOrderItems() != null) {
            for (OrderItem item : order.getOrderItems()) {
                item.setOrder(savedOrder);

                if (item.getProductIdFk() != null) {
                    productRepo.findById(item.getProductIdFk()).ifPresent(prod -> {
                        item.setProduct(prod);
                        // ✅ ดึงชื่อแบรนด์จาก brandRepo ผ่าน brandId
                        if (prod.getBrandId() != null) {
                            brandRepo.findById(prod.getBrandId())
                                    .ifPresent(brand -> item.setBrandName(brand.getName()));
                        }
                    });
                }
            }
            orderItemRepo.saveAll(order.getOrderItems());
        }

        if (savedOrder.getOrderStatus() == null) savedOrder.setOrderStatus("PENDING");
        if (savedOrder.getPaymentMethod() == null) savedOrder.setPaymentMethod("COD");
        if (savedOrder.getShippingMethod() == null) savedOrder.setShippingMethod("STANDARD");

        return orderRepo.save(savedOrder);
    }

    // ✅ อัปเดต order
    public Order updateOrder(Order order) {
        return orderRepo.save(order);
    }

    // ✅ ดึงทั้งหมด
    public List<Order> listAll() {
        return orderRepo.findAll();
    }

    // ✅ ดึงตาม id
    public Optional<Order> getById(Long id) {
        return orderRepo.findById(id);
    }

    // ✅ ดึงตาม code
    public Optional<Order> getByCode(String code) {
        return orderRepo.findByOrderCode(code);
    }
}
