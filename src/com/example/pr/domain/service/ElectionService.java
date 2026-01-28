package com.example.pr.domain.service;

import com.example.pr.domain.dto.election.ElectionCreateDto;
import com.example.pr.domain.dto.election.ElectionResponseDto;
import com.example.pr.domain.dto.election.ElectionUpdateDto;
import com.example.pr.domain.enums.ElectionStatus;
import com.example.pr.domain.impl.Election;
import com.example.pr.domain.service.exception.EntityNotFoundException;
import com.example.pr.domain.service.exception.ServiceException;
import com.example.pr.domain.specification.ElectionSpecifications;
import com.example.pr.infrastructure.storage.contract.ElectionRepository;
import com.example.pr.infrastructure.storage.impl.DataContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Сервіс для роботи з виборами.
 */
public class ElectionService {

  private final ElectionRepository electionRepository;
  private final DataContext context;

  public ElectionService() {
    this.context = DataContext.getInstance();
    this.electionRepository = context.elections();
  }

  /**
   * Створює нові вибори.
   */
  public ElectionResponseDto create(ElectionCreateDto dto) {
    Election election = new Election(
        dto.name(),
        dto.description(),
        dto.startDate(),
        dto.endDate(),
        ElectionStatus.PENDING  // Нові вибори завжди в статусі PENDING
    );

    electionRepository.save(election);
    return ElectionResponseDto.fromEntity(election);
  }

  /**
   * Знаходить вибори за ID.
   */
  public ElectionResponseDto findById(UUID id) {
    Election election = electionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Вибори", id));
    return ElectionResponseDto.fromEntity(election);
  }

  /**
   * Оновлює вибори.
   */
  public ElectionResponseDto update(UUID id, ElectionUpdateDto dto) {
    Election election = electionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Вибори", id));

    // Не можна змінювати активні або завершені вибори
    if (election.getStatus() == ElectionStatus.ACTIVE) {
      throw new ServiceException("Не можна змінювати активні вибори");
    }
    if (election.getStatus() == ElectionStatus.CLOSED) {
      throw new ServiceException("Не можна змінювати завершені вибори");
    }

    dto.name().ifPresent(election::setName);
    dto.description().ifPresent(election::setDescription);
    dto.status().ifPresent(election::setStatus);

    electionRepository.save(election);
    return ElectionResponseDto.fromEntity(election);
  }

  /**
   * Активує вибори.
   */
  public ElectionResponseDto activate(UUID id) {
    Election election = electionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Вибори", id));

    if (election.getStatus() != ElectionStatus.PENDING) {
      throw new ServiceException("Можна активувати тільки вибори в статусі PENDING");
    }

    // Перевіряємо наявність кандидатів
    long candidateCount = context.candidates().countByElectionId(id);
    if (candidateCount == 0) {
      throw new ServiceException("Не можна активувати вибори без кандидатів");
    }

    election.setStatus(ElectionStatus.ACTIVE);
    electionRepository.save(election);

    return ElectionResponseDto.fromEntity(election);
  }

  /**
   * Завершує вибори.
   */
  public ElectionResponseDto close(UUID id) {
    Election election = electionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Вибори", id));

    if (election.getStatus() != ElectionStatus.ACTIVE) {
      throw new ServiceException("Можна завершити тільки активні вибори");
    }

    election.setStatus(ElectionStatus.CLOSED);
    electionRepository.save(election);

    return ElectionResponseDto.fromEntity(election);
  }

  /**
   * Скасовує вибори.
   */
  public ElectionResponseDto cancel(UUID id) {
    Election election = electionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Вибори", id));

    if (election.getStatus() == ElectionStatus.CLOSED) {
      throw new ServiceException("Не можна скасувати завершені вибори");
    }

    election.setStatus(ElectionStatus.CANCELLED);
    electionRepository.save(election);

    return ElectionResponseDto.fromEntity(election);
  }

  /**
   * Видаляє вибори.
   */
  public void delete(UUID id) {
    Election election = electionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Вибори", id));

    if (election.getStatus() == ElectionStatus.ACTIVE) {
      throw new ServiceException("Не можна видалити активні вибори");
    }

    // Видаляємо пов'язані дані
    context.votes().deleteByElectionId(id);
    context.candidates().deleteByElectionId(id);

    electionRepository.deleteById(id);
  }

  /**
   * Знаходить всі вибори.
   */
  public List<ElectionResponseDto> findAll() {
    return electionRepository.findAll(ElectionSpecifications.all()).stream()
        .map(ElectionResponseDto::fromEntity)
        .toList();
  }

  /**
   * Знаходить активні вибори.
   */
  public List<ElectionResponseDto> findActive() {
    return electionRepository.findAll(ElectionSpecifications.active()).stream()
        .map(ElectionResponseDto::fromEntity)
        .toList();
  }

  /**
   * Знаходить вибори, що очікують.
   */
  public List<ElectionResponseDto> findPending() {
    return electionRepository.findAll(ElectionSpecifications.pending()).stream()
        .map(ElectionResponseDto::fromEntity)
        .toList();
  }

  /**
   * Знаходить завершені вибори.
   */
  public List<ElectionResponseDto> findClosed() {
    return electionRepository.findAll(ElectionSpecifications.closed()).stream()
        .map(ElectionResponseDto::fromEntity)
        .toList();
  }

  /**
   * Перевіряє, чи можна голосувати на виборах.
   */
  public boolean canVote(UUID electionId) {
    return electionRepository.findById(electionId)
        .map(e -> {
          if (e.getStatus() != ElectionStatus.ACTIVE) return false;
          LocalDateTime now = LocalDateTime.now();
          return now.isAfter(e.getStartDate()) && now.isBefore(e.getEndDate());
        })
        .orElse(false);
  }
}
