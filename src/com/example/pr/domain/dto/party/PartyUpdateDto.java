package com.example.pr.domain.dto.party;

import java.util.Optional;

/**
 * DTO для оновлення партії.
 */
public record PartyUpdateDto(
    Optional<String> name,
    Optional<String> abbreviation,
    Optional<String> logoPath,
    Optional<String> program
) {
  public PartyUpdateDto {
    name.ifPresent(n -> {
      if (n.trim().isEmpty()) {
        throw new IllegalArgumentException("Назва партії не може бути порожньою");
      }
      if (n.length() < 2 || n.length() > 100) {
        throw new IllegalArgumentException("Назва партії повинна містити від 2 до 100 символів");
      }
    });

    abbreviation.ifPresent(a -> {
      if (a.length() < 2 || a.length() > 10) {
        throw new IllegalArgumentException("Абревіатура повинна містити від 2 до 10 символів");
      }
    });
  }

  public static PartyUpdateDto empty() {
    return new PartyUpdateDto(
        Optional.empty(), Optional.empty(),
        Optional.empty(), Optional.empty()
    );
  }

  public PartyUpdateDto withName(String name) {
    return new PartyUpdateDto(Optional.ofNullable(name), abbreviation, logoPath, program);
  }

  public PartyUpdateDto withAbbreviation(String abbreviation) {
    return new PartyUpdateDto(name, Optional.ofNullable(abbreviation), logoPath, program);
  }

  public PartyUpdateDto withProgram(String program) {
    return new PartyUpdateDto(name, abbreviation, logoPath, Optional.ofNullable(program));
  }
}
