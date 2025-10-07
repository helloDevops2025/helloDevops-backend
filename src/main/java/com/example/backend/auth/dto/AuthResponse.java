package com.example.backend.auth.dto;

public class AuthResponse {
    public String token;  // ตอนนี้ยัง dummy ไว้ก่อน
    public String role;   // 'USER' | 'ADMIN'
    public Object user;   // public user info

    public AuthResponse(String token, String role, Object user) {
        this.token = token;
        this.role = role;
        this.user = user;
    }
}
