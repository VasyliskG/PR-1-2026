package com.example.pr.domain.dto.auth;

import com.example.pr.domain.dto.voter.VoterResponseDto;

/**
 * DTO для відповіді аутентифікації.
 */
public record AuthResponseDto(
    boolean success,
    String message,
    VoterResponseDto user,
    String sessionToken
) {
  public static AuthResponseDto success(VoterResponseDto user, String token) {
    return new AuthResponseDto(true, "Успішний вхід", user, token);
  }

  public static AuthResponseDto failure(String message) {
    return new AuthResponseDto(false, message, null, null);
  }
}
