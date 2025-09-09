package com.example.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockBean; // << ใหม่

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DbHealthController.class)
class DbHealthControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    JdbcTemplate jdbcTemplate;

    @Test
    void ok() throws Exception {
        when(jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE()", Integer.class))
                .thenReturn(7);
        when(jdbcTemplate.queryForObject("SELECT DATABASE()", String.class))
                .thenReturn("ecommerce");

        mvc.perform(get("/api/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.schema").value("ecommerce"))
                .andExpect(jsonPath("$.tables").value(7));
    }
}
