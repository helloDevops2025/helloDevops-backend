package com.example.backend.auth;

import com.example.backend.user.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AuthService {
    private final Map<String, User> byEmail = new HashMap<>(); // key: email (lowercase)
    private final AtomicLong seq = new AtomicLong(1);
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService() {
        // seed ตัวอย่าง (เอาออกได้): admin/admin123, user/user123
        createUser("Admin", "admin@local", "admin123", Role.ADMIN);
        createUser("User",  "user@local",  "user123",  Role.USER);
    }

    public User createUser(String name, String email, String rawPassword, Role role) {
        String e = email.toLowerCase();
        if (byEmail.containsKey(e)) throw new RuntimeException("email already used");
        String hash = encoder.encode(rawPassword);
        User u = new User(seq.getAndIncrement(), name, e, hash, role);
        byEmail.put(e, u);
        return u;
    }

    public User validateLogin(String email, String rawPassword) {
        if (email == null || rawPassword == null) return null;
        User u = byEmail.get(email.toLowerCase());
        if (u == null || !u.isActive()) return null;
        if (!encoder.matches(rawPassword, u.getPasswordHash())) return null;
        return u;
    }

    // สำหรับส่งออกแบบปลอดภัย
    public static class PublicUser {
        public Long id; public String name; public String email;
        public PublicUser(User u) { id=u.getId(); name=u.getName(); email=u.getEmail(); }
    }
}
