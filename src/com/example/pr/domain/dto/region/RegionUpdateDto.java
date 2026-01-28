package com.example.pr.domain.dto.region;

import java.util.Optional;

/**
 * DTO для оновлення регіону.
 */
public record RegionUpdateDto(
    Optional<String> name,
    Optional<String> code,
    Optional<String> description
) {
  public RegionUpdateDto {
    name.ifPresent(n -> {
      if (n.trim().isEmpty()) {
        throw new IllegalArgumentException("Назва регіону не може бути порожньою");
      }
      if (n.length() < 2 || n.length() > 100) {
        throw new IllegalArgumentException("Назва регіону повинна містити від 2 до 100 символів");
      }
    });

    code.ifPresent(c -> {
      if (c.trim().isEmpty()) {
        throw new IllegalArgumentException("Код регіону не може бути порожнім");
      }
      if (!c.trim().matches("^[A-Za-z]{2,5}$")) {
        throw new IllegalArgumentException("Код регіону повинен містити від 2 до 5 латинських літер");
      }
    });
  }

  public static RegionUpdateDto empty() {
    return new RegionUpdateDto(Optional.empty(), Optional.empty(), Optional.empty());
  }

  public RegionUpdateDto withName(String name) {
    return new RegionUpdateDto(Optional.ofNullable(name), code, description);
  }

  public RegionUpdateDto withCode(String code) {
    return new RegionUpdateDto(name, Optional.ofNullable(code), description);
  }

  public RegionUpdateDto withDescription(String description) {
    return new RegionUpdateDto(name, code, Optional.ofNullable(description));
  }
}
