package com.example.pr.domain.dto.auth;

import com.example.pr.domain.dto.voter.VoterResponseDto;

/**
 * DTO з результатом аутентифікації.
 */
public record AuthResultDto(
    boolean success,
    String message,
    VoterResponseDto voter,
    String sessionToken
) {
  public static AuthResultDto success(VoterResponseDto voter, String token) {
    return new AuthResultDto(true, "Успішна аутентифікація", voter, token);
  }

  public static AuthResultDto failure(String message) {
    return new AuthResultDto(false, message, null, null);
  }
}
