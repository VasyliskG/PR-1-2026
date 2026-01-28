package com.example.pr.domain.dto.region;

/**
 * DTO для створення регіону.
 */
public record RegionCreateDto(
    String name,
    String code,
    String description
) {
  public RegionCreateDto {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Назва регіону є обов'язковою");
    }
    if (name.length() < 2 || name.length() > 100) {
      throw new IllegalArgumentException("Назва регіону повинна містити від 2 до 100 символів");
    }
    if (code == null || code.trim().isEmpty()) {
      throw new IllegalArgumentException("Код регіону є обов'язковим");
    }
    if (!code.trim().matches("^[A-Za-z]{2,5}$")) {
      throw new IllegalArgumentException("Код регіону повинен містити від 2 до 5 латинських літер");
    }

    // Нормалізація даних
    name = name.trim();
    code = code.trim().toUpperCase();
    description = description != null ? description.trim() : null;
  }
}
