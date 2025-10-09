package com.example.backend.user;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  private String phone;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  private Role role = Role.USER;

  private boolean active = true;

  @Column(name = "created_at")
  private Instant createdAt = Instant.now();

  // ── getters/setters ─────────────────────
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getEmail() { return email; }
  public void setEmail(String email) {
    this.email = (email == null) ? null : email.toLowerCase();
  }

  public String getPhone() { return phone; }
  public void setPhone(String phone) { this.phone = phone; }

  public String getPasswordHash() { return passwordHash; }
  public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

  public Role getRole() { return role; }
  public void setRole(Role role) { this.role = role; }

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }

  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
