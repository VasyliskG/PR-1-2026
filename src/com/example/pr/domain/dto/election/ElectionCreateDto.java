package com.example.pr.domain.dto.election;

import java.time.LocalDateTime;

/**
 * DTO для створення виборів.
 */
public record ElectionCreateDto(
    String name,
    String description,
    LocalDateTime startDate,
    LocalDateTime endDate
) {
  public ElectionCreateDto {
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Назва виборів є обов'язковою");
    }
    if (name.length() < 5 || name.length() > 200) {
      throw new IllegalArgumentException("Назва повинна містити від 5 до 200 символів");
    }
    if (startDate == null) {
      throw new IllegalArgumentException("Дата початку є обов'язковою");
    }
    if (endDate == null) {
      throw new IllegalArgumentException("Дата завершення є обов'язковою");
    }
    if (!endDate.isAfter(startDate)) {
      throw new IllegalArgumentException("Дата завершення повинна бути пізніше дати початку");
    }

    name = name.trim();
  }
}
