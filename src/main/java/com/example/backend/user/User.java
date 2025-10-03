// package com.example.backend;

// public class User {
//     private Long id;
//     private String name;
//     private String email;

//     public User() {}

//     public User(Long id, String name, String email) {
//         this.id = id;
//         this.name = name;
//         this.email = email;
//     }

//     public Long getId() { return id; }
//     public void setId(Long id) { this.id = id; }

//     public String getName() { return name; }
//     public void setName(String name) { this.name = name; }

//     public String getEmail() { return email; }
//     public void setEmail(String email) { this.email = email; }
// }
package com.example.backend.user;

import java.time.Instant;

public class User {
    private Long id;
    private String name;
    private String email;         // unique, lowercase
    private String passwordHash;  // เก็บ hash ของ password
    private Role role;            // USER / ADMIN
    private boolean active = true;
    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public User() {}

    public User(Long id, String name, String email, String passwordHash, Role role) {
        this.id = id;
        this.name = name;
        this.email = email == null ? null : email.toLowerCase();
        this.passwordHash = passwordHash;
        this.role = role;
    }

    // ===== Getters/Setters =====
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email == null ? null : email.toLowerCase(); }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
