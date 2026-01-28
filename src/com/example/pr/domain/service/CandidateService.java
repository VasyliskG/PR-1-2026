package com.example.pr.domain.service;

import com.example.pr.domain.dto.candidate.CandidateCreateDto;
import com.example.pr.domain.dto.candidate.CandidateResponseDto;
import com.example.pr.domain.dto.candidate.CandidateUpdateDto;
import com.example.pr.domain.enums.ElectionStatus;
import com.example.pr.domain.impl.Candidate;
import com.example.pr.domain.impl.Election;
import com.example.pr.domain.service.exception.DuplicateEntityException;
import com.example.pr.domain.service.exception.EntityNotFoundException;
import com.example.pr.domain.service.exception.ServiceException;
import com.example.pr.domain.specification.CandidateSpecifications;
import com.example.pr.infrastructure.storage.contract.CandidateRepository;
import com.example.pr.infrastructure.storage.impl.DataContext;

import java.util.List;
import java.util.UUID;

/**
 * Сервіс для роботи з кандидатами.
 */
public class CandidateService {

  private final CandidateRepository candidateRepository;
  private final DataContext context;

  public CandidateService() {
    this.context = DataContext.getInstance();
    this.candidateRepository = context.candidates();
  }

  /**
   * Реєструє нового кандидата.
   */
  public CandidateResponseDto create(CandidateCreateDto dto) {
    // Перевірка існування виборів
    Election election = context.elections().findById(dto.electionId())
        .orElseThrow(() -> new EntityNotFoundException("Вибори", dto.electionId()));

    // Перевірка статусу виборів
    if (election.getStatus() != ElectionStatus.PENDING) {
      throw new ServiceException("Можна додавати кандидатів тільки до виборів у статусі PENDING");
    }

    // Перевірка унікальності паспорта
    candidateRepository.findByPassportNumber(dto.passportNumber())
        .ifPresent(c -> {
          throw new DuplicateEntityException("Кандидат", "passportNumber", dto.passportNumber());
        });

    // Перевірка існування партії (якщо вказана)
    if (dto.partyCode() != null && !context.parties().existsByCode(dto.partyCode())) {
      throw new EntityNotFoundException("Партія", dto.partyCode());
    }

    Candidate candidate = new Candidate(
        dto.firstName(),
        dto.lastName(),
        dto.passportNumber(),
        dto.partyCode(),
        dto.electionId(),
        dto.program(),
        dto.photoPath(),
        dto.biography()
    );

    candidateRepository.save(candidate);
    return CandidateResponseDto.fromEntity(candidate);
  }

  /**
   * Знаходить кандидата за ID.
   */
  public CandidateResponseDto findById(UUID id) {
    Candidate candidate = candidateRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Кандидат", id));
    return CandidateResponseDto.fromEntity(candidate);
  }

  /**
   * Оновлює кандидата.
   */
  public CandidateResponseDto update(UUID id, CandidateUpdateDto dto) {
    Candidate candidate = candidateRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Кандидат", id));

    // Перевірка статусу виборів
    Election election = context.elections().findById(candidate.getElectionId())
        .orElseThrow(() -> new EntityNotFoundException("Вибори", candidate.getElectionId()));

    if (election.getStatus() == ElectionStatus.CLOSED) {
      throw new ServiceException("Не можна оновлювати кандидатів на завершених виборах");
    }

    // Оновлення полів
    dto.firstName().ifPresent(candidate::setFirstName);
    dto.lastName().ifPresent(candidate::setLastName);

    dto.partyCode().ifPresent(partyCode -> {
      if (partyCode != null && !context.parties().existsByCode(partyCode)) {
        throw new EntityNotFoundException("Партія", partyCode);
      }
      candidate.setPartyCode(partyCode);
    });

    dto.program().ifPresent(candidate::setProgram);
    dto.photoPath().ifPresent(candidate::setPhotoPath);
    dto.biography().ifPresent(candidate::setBiography);

    candidateRepository.save(candidate);
    return CandidateResponseDto.fromEntity(candidate);
  }

  /**
   * Видаляє кандидата.
   */
  public void delete(UUID id) {
    Candidate candidate = candidateRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Кандидат", id));

    // Перевірка статусу виборів
    Election election = context.elections().findById(candidate.getElectionId())
        .orElseThrow(() -> new EntityNotFoundException("Вибори", candidate.getElectionId()));

    if (election.getStatus() == ElectionStatus.ACTIVE) {
      throw new ServiceException("Не можна видаляти кандидатів під час активних виборів");
    }

    if (election.getStatus() == ElectionStatus.CLOSED) {
      throw new ServiceException("Не можна видаляти кандидатів із завершених виборів");
    }

    // Видаляємо голоси за цього кандидата
    context.votes().deleteByCandidateId(id);

    candidateRepository.deleteById(id);
  }

  /**
   * Знаходить всіх кандидатів.
   */
  public List<CandidateResponseDto> findAll() {
    return candidateRepository.findAll(CandidateSpecifications.all()).stream()
        .map(CandidateResponseDto::fromEntity)
        .toList();
  }

  /**
   * Знаходить кандидатів на виборах.
   */
  public List<CandidateResponseDto> findByElection(UUID electionId) {
    return candidateRepository.findByElectionId(electionId).stream()
        .map(CandidateResponseDto::fromEntity)
        .toList();
  }

  /**
   * Знаходить кандидатів партії.
   */
  public List<CandidateResponseDto> findByParty(String partyCode) {
    return candidateRepository.findByPartyCode(partyCode).stream()
        .map(CandidateResponseDto::fromEntity)
        .toList();
  }

  /**
   * Пошук кандидатів за ім'ям.
   */
  public List<CandidateResponseDto> searchByName(String query) {
    return candidateRepository.findAll(CandidateSpecifications.fullNameContains(query)).stream()
        .map(CandidateResponseDto::fromEntity)
        .toList();
  }

  /**
   * Знаходить незалежних кандидатів (без партії).
   */
  public List<CandidateResponseDto> findIndependent() {
    return candidateRepository.findAll(CandidateSpecifications.independent()).stream()
        .map(CandidateResponseDto::fromEntity)
        .toList();
  }

  /**
   * Підраховує кандидатів на виборах.
   */
  public long countByElection(UUID electionId) {
    return candidateRepository.countByElectionId(electionId);
  }

  /**
   * Підраховує кандидатів партії.
   */
  public long countByParty(String partyCode) {
    return candidateRepository.countByPartyCode(partyCode);
  }
}
