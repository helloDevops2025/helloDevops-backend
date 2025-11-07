package com.example.backend;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")   // ✅ เพิ่มตรงนี้ให้เหมือน controller ตัวอื่น
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Healthy");
    }

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("OK");
    }
}
