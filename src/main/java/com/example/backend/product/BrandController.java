package com.example.backend.product;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/brands")
// @CrossOrigin(origins = "http://localhost:5173")
public class BrandController {
  private final BrandRepository repo;
  public BrandController(BrandRepository repo){ this.repo = repo; }

  @GetMapping
  public List<BrandDTO> list() {
    return repo.findAll().stream().map(b -> new BrandDTO(b.getId(), b.getName())).toList();
  }
  record BrandDTO(Long id, String name) {}
}
