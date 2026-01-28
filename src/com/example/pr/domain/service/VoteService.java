package com.example.pr.domain.service;

import com.example.pr.domain.dto.vote.VoteCreateDto;
import com.example.pr.domain.dto.vote.VoteResponseDto;
import com.example.pr.domain.dto.vote.VoteResultDto;
import com.example.pr.domain.enums.ElectionStatus;
import com.example.pr.domain.impl.Candidate;
import com.example.pr.domain.impl.Election;
import com.example.pr.domain.impl.Vote;
import com.example.pr.domain.impl.Voter;
import com.example.pr.domain.service.exception.EntityNotFoundException;
import com.example.pr.domain.service.exception.VotingException;
import com.example.pr.domain.specification.VoteSpecifications;
import com.example.pr.infrastructure.storage.contract.VoteRepository;
import com.example.pr.infrastructure.storage.impl.DataContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Сервіс для роботи з голосуванням.
 */
public class VoteService {

  private final VoteRepository voteRepository;
  private final DataContext context;

  public VoteService() {
    this.context = DataContext.getInstance();
    this.voteRepository = context.votes();
  }

  /**
   * Реєструє голос.
   */
  public VoteResponseDto vote(VoteCreateDto dto) {
    // Перевірка існування виборця
    Voter voter = context.voters().findById(dto.voterId())
        .orElseThrow(() -> new EntityNotFoundException("Виборець", dto.voterId()));

    // Перевірка існування виборів
    Election election = context.elections().findById(dto.electionId())
        .orElseThrow(() -> new EntityNotFoundException("Вибори", dto.electionId()));

    // Перевірка існування кандидата
    Candidate candidate = context.candidates().findById(dto.candidateId())
        .orElseThrow(() -> new EntityNotFoundException("Кандидат", dto.candidateId()));

    // Перевірка статусу виборів
    if (election.getStatus() != ElectionStatus.ACTIVE) {
      throw VotingException.electionNotActive();
    }

    // Перевірка часу виборів
    LocalDateTime now = LocalDateTime.now();
    if (now.isBefore(election.getStartDate())) {
      throw VotingException.electionNotStarted();
    }
    if (now.isAfter(election.getEndDate())) {
      throw VotingException.electionEnded();
    }

    // Перевірка, чи кандидат бере участь у цих виборах
    if (!candidate.getElectionId().equals(dto.electionId())) {
      throw VotingException.invalidCandidate();
    }

    // Перевірка, чи виборець вже голосував
    if (voteRepository.hasVoted(dto.voterId(), dto.electionId())) {
      throw VotingException.alreadyVoted();
    }

    // Створення голосу
    Vote vote = new Vote(
        dto.voterId(),
        dto.candidateId(),
        dto.electionId(),
        LocalDateTime.now()
    );

    voteRepository.save(vote);
    return VoteResponseDto.fromEntity(vote);
  }

  /**
   * Перевіряє, чи виборець голосував.
   */
  public boolean hasVoted(UUID voterId, UUID electionId) {
    return voteRepository.hasVoted(voterId, electionId);
  }

  /**
   * Отримує результати голосування.
   */
  public List<VoteResultDto> getResults(UUID electionId) {
    // Перевірка існування виборів
    Election election = context.elections().findById(electionId)
        .orElseThrow(() -> new EntityNotFoundException("Вибори", electionId));

    // Отримуємо всіх кандидатів
    List<Candidate> candidates = context.candidates().findByElectionId(electionId);

    // Загальна кількість голосів
    long totalVotes = voteRepository.countByElection(electionId);

    // Формуємо результати
    List<VoteResultDto> results = new ArrayList<>();

    for (Candidate candidate : candidates) {
      long voteCount = voteRepository.countByCandidate(candidate.getId());
      double percentage = totalVotes > 0 ? (voteCount * 100.0 / totalVotes) : 0;

      String partyName = candidate.getPartyCode() != null
          ? context.parties().findByCode(candidate.getPartyCode())
          .map(p -> p.getName())
          .orElse("Незалежний")
          : "Незалежний";

      results.add(new VoteResultDto(
          candidate.getId(),
          candidate.getFirstName() + " " + candidate.getLastName(),
          partyName,
          voteCount,
          percentage
      ));
    }

    // Сортуємо за кількістю голосів (від більшого до меншого)
    results.sort((a, b) -> Long.compare(b.voteCount(), a.voteCount()));

    return results;
  }

  /**
   * Отримує всі голоси виборця.
   */
  public List<VoteResponseDto> findByVoter(UUID voterId) {
    return voteRepository.findByVoterId(voterId).stream()
        .map(VoteResponseDto::fromEntity)
        .toList();
  }

  /**
   * Отримує всі голоси на виборах.
   */
  public List<VoteResponseDto> findByElection(UUID electionId) {
    return voteRepository.findByElectionId(electionId).stream()
        .map(VoteResponseDto::fromEntity)
        .toList();
  }

  /**
   * Підраховує голоси за кандидата.
   */
  public long countByCandidate(UUID candidateId) {
    return voteRepository.countByCandidate(candidateId);
  }

  /**
   * Підраховує всі голоси на виборах.
   */
  public long countByElection(UUID electionId) {
    return voteRepository.countByElection(electionId);
  }

  /**
   * Статистика голосування за сьогодні.
   */
  public long countVotesToday(UUID electionId) {
    return voteRepository.findAll(
        VoteSpecifications.byElectionId(electionId)
            .and(VoteSpecifications.votedToday())
    ).size();
  }
}
