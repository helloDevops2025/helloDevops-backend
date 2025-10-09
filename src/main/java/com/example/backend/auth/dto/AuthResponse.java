package com.example.backend.auth.dto;
public class AuthResponse {
    public String email;
    public String role;
    public AuthResponse(String email, String role) {
        this.email = email;
        this.role = role;
    }
}
