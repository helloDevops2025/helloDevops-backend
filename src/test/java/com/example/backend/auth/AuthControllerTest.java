package com.example.backend.auth;

import com.example.backend.auth.dto.LoginRequest;
import com.example.backend.auth.dto.SignupRequest;
import com.example.backend.user.Role;
import com.example.backend.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // ✅ ถูกต้อง

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // ปิด Security filters ชั่วคราว
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService auth;   // mock service ตามคอนโทรลเลอร์

    @Autowired
    private ObjectMapper mapper; // แปลง object <-> JSON

    // ✅ signup สำเร็จ → 200 + {"email","role"}
    @Test
    void signup_shouldReturnOk() throws Exception {
        User mockUser = new User();
        mockUser.setEmail("test@example.com");
        mockUser.setRole(Role.USER);

        when(auth.signup(eq("test@example.com"), eq("0800000000"), eq("123456")))
                .thenReturn(mockUser);

        SignupRequest req = new SignupRequest();
        req.email = "test@example.com";
        req.phone = "0800000000";
        req.password = "123456";
        // ไม่ส่ง confirmPassword -> ผ่านได้ตามคอนโทรลเลอร์

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    // ✅ login สำเร็จ → 200 + {"email","role"}
    @Test
    void login_shouldReturnUserInfo() throws Exception {
        User mockUser = new User();
        mockUser.setEmail("user@example.com");
        mockUser.setRole(Role.USER);

        when(auth.login(eq("user@example.com"), eq("pass123")))
                .thenReturn(mockUser);

        LoginRequest req = new LoginRequest();
        req.email = "user@example.com";
        req.password = "pass123";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    // ✅ login ผิด → 401 + "Invalid credentials"
    @Test
    void login_shouldReturnUnauthorizedWhenInvalid() throws Exception {
        when(auth.login(eq("wrong@example.com"), eq("badpass")))
                .thenReturn(null); // คอนโทรลเลอร์เช็ค null แล้วคืน 401

        LoginRequest req = new LoginRequest();
        req.email = "wrong@example.com";
        req.password = "badpass";

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }
}
