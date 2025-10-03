package com.example.backend.auth;

import com.example.backend.auth.dto.AuthResponse;
import com.example.backend.auth.dto.LoginRequest;
import com.example.backend.user.Role;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest req) {
        // เดโม่: ให้ผ่านตาม email/password ตัวอย่าง
        Role role = null;
        if ("user@local".equalsIgnoreCase(req.email) && "user123".equals(req.password)) role = Role.USER;
        if ("admin@local".equalsIgnoreCase(req.email) && "admin123".equals(req.password)) role = Role.ADMIN;

        if (role == null) throw new RuntimeException("Invalid credentials");

        var user = new Object() { public Long id=1L; public String email=req.email; public String name="Demo"; };
        return new AuthResponse("dummy-token", role.name(), user);
    }
}
