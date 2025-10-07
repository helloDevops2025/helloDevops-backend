package com.example.backend.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;        // ✅ เพิ่ม import นี้
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> store = new HashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    @GetMapping
    public List<User> getAllUsers() {
        return new ArrayList<>(store.values());
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return store.get(id);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        Long id = sequence.getAndIncrement();
        user.setId(id);
        store.put(id, user);
        return user;
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        User existing = store.get(id);
        if (existing == null) return null;
        existing.setName(user.getName());
        existing.setEmail(user.getEmail());
        return existing;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {   // ✅ ใช้ ResponseEntity<Void>
        if (!store.containsKey(id)) {
            return ResponseEntity.notFound().build();                  // 404
        }
        store.remove(id);
        return ResponseEntity.noContent().build();                     // 204
    }
}
