package com.example.pr.domain.dto.region;

import com.example.pr.domain.impl.Region;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO для відповіді з даними регіону.
 */
public record RegionResponseDto(
    UUID id,
    String name,
    String code,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
  public static RegionResponseDto fromEntity(Region region) {
    return new RegionResponseDto(
        region.getId(),
        region.getName(),
        region.getCode(),
        region.getDescription(),
        region.getCreatedAt(),
        region.getUpdatedAt()
    );
  }
}
