package com.example.backend;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class DbHealthController {
    private final JdbcTemplate jdbc;
    public DbHealthController(JdbcTemplate jdbc){ this.jdbc = jdbc; }

    @GetMapping("/api/health")
    public Map<String,Object> health() {
        Integer tables = jdbc.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE()", Integer.class);
        String schema = jdbc.queryForObject("SELECT DATABASE()", String.class);
        return Map.of("status","UP","schema",schema,"tables",tables);
    }
}
