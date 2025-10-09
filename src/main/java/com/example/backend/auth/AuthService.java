package com.example.backend.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.backend.user.Role;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;

@Service
public class AuthService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Autowired
    public AuthService(UserRepository repo) {
        this.repo = repo;
    }

    // สมัครสมาชิกใหม่ (เก็บ hash)
    public User signup(String email, String phone, String rawPassword) {
        if (email == null || rawPassword == null) {
            throw new IllegalArgumentException("email/password must not be null");
        }
        String normalized = email.trim().toLowerCase();
        if (repo.existsByEmail(normalized)) {
            throw new IllegalStateException("Email already exists");
        }
        User u = new User();
        u.setEmail(normalized);
        u.setPhone(phone);
        u.setPasswordHash(encoder.encode(rawPassword));
        u.setRole(Role.USER);
        return repo.save(u);
    }

    // ล็อกอิน
    public User login(String email, String rawPassword) {
        if (email == null || rawPassword == null) return null;
        return repo.findByEmail(email.trim().toLowerCase())
                .filter(u -> u.isActive() && encoder.matches(rawPassword, u.getPasswordHash()))
                .orElse(null);
    }
}
