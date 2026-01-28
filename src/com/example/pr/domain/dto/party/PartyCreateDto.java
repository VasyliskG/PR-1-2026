package com.example.pr.domain.dto.party;

/**
 * DTO для створення партії.
 */
public record PartyCreateDto(
    String partyCode,
    String name,
    String abbreviation,
    String logoPath,
    String program
) {
  public PartyCreateDto {
    if (partyCode == null) {
      throw new IllegalArgumentException("ID партії є обов'язковим");
    }
    if (name == null || name.trim().isEmpty()) {
      throw new IllegalArgumentException("Назва партії є обов'язковою");
    }
    if (name.length() < 2 || name.length() > 100) {
      throw new IllegalArgumentException("Назва партії повинна містити від 2 до 100 символів");
    }
    if (abbreviation != null && (abbreviation.length() < 2 || abbreviation.length() > 10)) {
      throw new IllegalArgumentException("Абревіатура повинна містити від 2 до 10 символів");
    }

    // Нормалізація
    name = name.trim();
    abbreviation = abbreviation != null ? abbreviation.trim().toUpperCase() : null;
    logoPath = logoPath != null ? logoPath.trim() : null;
    program = program != null ? program.trim() : null;
  }
}
