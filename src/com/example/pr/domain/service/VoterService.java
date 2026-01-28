package com.example.pr.domain.service;

import com.example.pr.domain.dto.voter.VoterCreateDto;
import com.example.pr.domain.dto.voter.VoterResponseDto;
import com.example.pr.domain.dto.voter.VoterUpdateDto;
import com.example.pr.domain.enums.VoterRole;
import com.example.pr.domain.impl.Voter;
import com.example.pr.domain.service.exception.DuplicateEntityException;
import com.example.pr.domain.service.exception.EntityNotFoundException;
import com.example.pr.domain.specification.VoterSpecifications;
import com.example.pr.infrastructure.storage.contract.VoterRepository;
import com.example.pr.infrastructure.storage.impl.DataContext;

import java.util.List;
import java.util.UUID;

/**
 * Сервіс для роботи з виборцями.
 */
public class VoterService {

  private final VoterRepository voterRepository;
  private final DataContext context;

  public VoterService() {
    this.context = DataContext.getInstance();
    this.voterRepository = context.voters();
  }

  /**
   * Створює нового виборця.
   */
  public VoterResponseDto create(VoterCreateDto dto) {
    // Перевірка унікальності email
    if (voterRepository.existsByEmail(dto.email())) {
      throw new DuplicateEntityException("Виборець", "email", dto.email());
    }

    // Перевірка унікальності паспорта
    if (voterRepository.existsByPassportNumber(dto.passportNumber())) {
      throw new DuplicateEntityException("Виборець", "passportNumber", dto.passportNumber());
    }

    // Перевірка існування регіону
    if (!context.regions().existsById(dto.regionId())) {
      throw new EntityNotFoundException("Регіон", dto.regionId());
    }

    String passwordHash = PasswordHasher.hash(dto.password());

    Voter voter = new Voter(
        dto.firstName(),
        dto.lastName(),
        dto.email(),
        passwordHash,
        dto.passportNumber(),
        dto.birthDate(),
        VoterRole.VOTER,
        dto.regionId()
    );

    voterRepository.save(voter);
    return VoterResponseDto.fromEntity(voter);
  }

  /**
   * Знаходить виборця за ID.
   */
  public VoterResponseDto findById(UUID id) {
    Voter voter = voterRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Виборець", id));
    return VoterResponseDto.fromEntity(voter);
  }

  /**
   * Знаходить виборця за email.
   */
  public VoterResponseDto findByEmail(String email) {
    Voter voter = voterRepository.findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException("Виборець", "email", email));
    return VoterResponseDto.fromEntity(voter);
  }

  /**
   * Оновлює виборця.
   */
  public VoterResponseDto update(UUID id, VoterUpdateDto dto) {
    Voter voter = voterRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Виборець", id));

    // Оновлюємо поля, якщо вони присутні
    dto.firstName().ifPresent(voter::setFirstName);
    dto.lastName().ifPresent(voter::setLastName);

    dto.email().ifPresent(email -> {
      // Перевіряємо унікальність нового email
      if (!email.equalsIgnoreCase(voter.getEmail()) && voterRepository.existsByEmail(email)) {
        throw new DuplicateEntityException("Виборець", "email", email);
      }
      voter.setEmail(email);
    });

    dto.regionId().ifPresent(regionId -> {
      if (!context.regions().existsById(regionId)) {
        throw new EntityNotFoundException("Регіон", regionId);
      }
      voter.setRegionId(regionId);
    });

    voterRepository.save(voter);
    return VoterResponseDto.fromEntity(voter);
  }

  /**
   * Видаляє виборця.
   */
  public void delete(UUID id) {
    if (!voterRepository.existsById(id)) {
      throw new EntityNotFoundException("Виборець", id);
    }

    // Видаляємо пов'язані голоси
    context.votes().deleteByVoterId(id);

    voterRepository.deleteById(id);
  }

  /**
   * Знаходить всіх виборців.
   */
  public List<VoterResponseDto> findAll() {
    return voterRepository.findAll(VoterSpecifications.all()).stream()
        .map(VoterResponseDto::fromEntity)
        .toList();
  }

  /**
   * Знаходить виборців за регіоном.
   */
  public List<VoterResponseDto> findByRegion(UUID regionId) {
    return voterRepository.findAll(VoterSpecifications.byRegionId(regionId)).stream()
        .map(VoterResponseDto::fromEntity)
        .toList();
  }

  /**
   * Знаходить виборців за роллю.
   */
  public List<VoterResponseDto> findByRole(VoterRole role) {
    return voterRepository.findAll(VoterSpecifications.byRole(role)).stream()
        .map(VoterResponseDto::fromEntity)
        .toList();
  }

  /**
   * Пошук виборців за ім'ям.
   */
  public List<VoterResponseDto> searchByName(String query) {
    return voterRepository.findAll(VoterSpecifications.fullNameContains(query)).stream()
        .map(VoterResponseDto::fromEntity)
        .toList();
  }

  /**
   * Змінює роль виборця (тільки для адмінів).
   */
  public VoterResponseDto changeRole(UUID id, VoterRole newRole) {
    Voter voter = voterRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Виборець", id));

    voter.setRole(newRole);
    voterRepository.save(voter);

    return VoterResponseDto.fromEntity(voter);
  }

  /**
   * Підраховує виборців.
   */
  public long count() {
    return voterRepository.count(VoterSpecifications.all());
  }

  /**
   * Підраховує виборців у регіоні.
   */
  public long countByRegion(UUID regionId) {
    return voterRepository.countByRegionId(regionId);
  }
}
