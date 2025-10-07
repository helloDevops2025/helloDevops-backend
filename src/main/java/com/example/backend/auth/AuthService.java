package com.example.backend.auth;

import com.example.backend.user.User;
import com.example.backend.user.Role;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AuthService {
    // ใช้ ConcurrentHashMap เผื่อการเรียกพร้อมกันหลายเธรด
    private final Map<String, User> byEmail = new ConcurrentHashMap<>(); // key: email (lowercase)
    private final AtomicLong seq = new AtomicLong(1);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService() {
        // seed ตัวอย่าง (ลบได้)
        createUser("Admin", "admin@local", "admin123", Role.ADMIN);
        createUser("User",  "user@local",  "user123",  Role.USER);
    }

    public User createUser(String name, String email, String rawPassword, Role role) {
        if (email == null || rawPassword == null) {
            throw new IllegalArgumentException("email/password must not be null");
        }
        String e = email.toLowerCase(Locale.ROOT);
        if (byEmail.containsKey(e)) throw new RuntimeException("email already used");
        String hash = encoder.encode(rawPassword);

        // ต้องตรงกับคอนสตรัคเตอร์ของ User (Long, String, String, String, Role)
        User u = new User(seq.getAndIncrement(), name, e, hash, role);
        byEmail.put(e, u);
        return u;
    }

    public User validateLogin(String email, String rawPassword) {
        if (email == null || rawPassword == null) return null;

        User u = byEmail.get(email.toLowerCase(Locale.ROOT));
        if (u == null || !u.isActive()) return null;

        if (!encoder.matches(rawPassword, u.getPasswordHash())) return null;
        return u;
    }

    // สำหรับส่งออกแบบปลอดภัย
    public static class PublicUser {
        public Long id; public String name; public String email; public String role;
        public PublicUser(User u) {
            this.id = u.getId();
            this.name = u.getName();
            this.email = u.getEmail();
            this.role = u.getRole() != null ? u.getRole().name() : null;
        }
    }
}
