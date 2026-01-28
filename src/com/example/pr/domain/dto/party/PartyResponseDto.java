package com.example.pr.domain.dto.party;

import com.example.pr.domain.impl.Party;
import java.time.LocalDateTime;

/**
 * DTO для відповіді з даними партії.
 */
public record PartyResponseDto(
    String partyCode,
    String name,
    String abbreviation,
    String logoPath,
    String program,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
  public static PartyResponseDto fromEntity(Party party) {
    return new PartyResponseDto(
        party.getPartyCode(),
        party.getName(),
        party.getAbbreviation(),
        party.getLogoPath(),
        party.getProgram(),
        party.getCreatedAt(),
        party.getUpdatedAt()
    );
  }

  /**
   * Повертає відображувану назву (абревіатура або повна назва).
   */
  public String displayName() {
    return abbreviation != null ? abbreviation : name;
  }
}
