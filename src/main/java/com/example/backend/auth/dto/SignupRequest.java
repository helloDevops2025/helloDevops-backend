package com.example.backend.auth.dto;
public class SignupRequest {
    public String email;
    public String phone;          // optional
    public String password;
    public String confirmPassword; // optional (ไว้ตรวจฝั่ง server)
}
