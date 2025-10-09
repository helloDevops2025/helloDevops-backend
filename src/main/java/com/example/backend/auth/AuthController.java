package com.example.backend.auth;

import com.example.backend.auth.dto.AuthResponse;
import com.example.backend.auth.dto.LoginRequest;
import com.example.backend.auth.dto.SignupRequest;
import com.example.backend.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    // 🔹 LOGIN
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        User u = auth.login(req.email, req.password);
        if (u == null) {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
        return ResponseEntity.ok(new AuthResponse(u.getEmail(), u.getRole().name()));
    }

    // 🔹 SIGNUP
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest req) {
        if (req.email == null || req.password == null) {
            return ResponseEntity.badRequest().body("email/password required");
        }
        if (req.confirmPassword != null && !req.password.equals(req.confirmPassword)) {
            return ResponseEntity.badRequest().body("password not match");
        }
        User u = auth.signup(req.email, req.phone, req.password);
        return ResponseEntity.ok(new AuthResponse(u.getEmail(), u.getRole().name()));
    }
}
