package com.example.pr.domain.dto.candidate;

import com.example.pr.domain.impl.Candidate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для відповіді з даними кандидата.
 */
public record CandidateResponseDto(
    UUID id,
    String firstName,
    String lastName,
    String passportNumber,
    String partyCode,
    UUID electionId,
    String program,
    String photoPath,
    String biography,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
  public static CandidateResponseDto fromEntity(Candidate candidate) {
    return new CandidateResponseDto(
        candidate.getId(),
        candidate.getFirstName(),
        candidate.getLastName(),
        candidate.getPassportNumber(),
        candidate.getPartyCode(),
        candidate.getElectionId(),
        candidate.getProgram(),
        candidate.getPhotoPath(),
        candidate.getBiography(),
        candidate.getCreatedAt(),
        candidate.getUpdatedAt()
    );
  }

  public String fullName() {
    return firstName + " " + lastName;
  }
}
