package com.example.backend.product;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {
  private final CategoryRepository repo;
  public CategoryController(CategoryRepository repo){ this.repo = repo; }

  @GetMapping
  public List<CategoryDTO> list() {
    return repo.findAll().stream().map(c -> new CategoryDTO(c.getId(), c.getName())).toList();
  }
  record CategoryDTO(Long id, String name) {}
}

