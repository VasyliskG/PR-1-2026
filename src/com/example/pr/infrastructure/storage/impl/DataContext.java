package com.example.pr.infrastructure.storage.impl;

import com.example.pr.domain.Entity;
import com.example.pr.infrastructure.storage.Repository;
import com.example.pr.infrastructure.storage.contract.CandidateRepository;
import com.example.pr.infrastructure.storage.contract.ElectionRepository;
import com.example.pr.infrastructure.storage.contract.PartyRepository;
import com.example.pr.infrastructure.storage.contract.RegionRepository;
import com.example.pr.infrastructure.storage.contract.VoteRepository;
import com.example.pr.infrastructure.storage.contract.VoterRepository;
import java.util.*;

/**
 * DataContext - єдина точка доступу до всіх репозиторіїв системи. Реалізує паттерн Singleton та
 * Unit of Work.
 * <p>
 * Використання:
 * <pre>{@code
 * DataContext context = DataContext.getInstance();
 *
 * // Отримання репозиторіїв
 * VoterRepository voters = context.voters();
 *
 * // Unit of Work
 * Voter voter = new Voter(...);
 * context.registerNew(voter);
 * context.commit();
 * }</pre>
 */
public class DataContext {

  // Singleton instance (Bill Pugh pattern)
  private static class Holder {

    private static final DataContext INSTANCE = new DataContext();
  }

  // Репозиторії
  private final VoterRepository voterRepository;
  private final CandidateRepository candidateRepository;
  private final ElectionRepository electionRepository;
  private final VoteRepository voteRepository;
  private final RegionRepository regionRepository;
  private final PartyRepository partyRepository;

  // Unit of Work - відстеження змін
  private final Set<Entity> newEntities = new LinkedHashSet<>();
  private final Set<Entity> dirtyEntities = new LinkedHashSet<>();
  private final Map<Repository<? extends Entity>, Set<UUID>> deletedIdsMap = new HashMap<>();

  private DataContext() {
    this.voterRepository = new BinaryVoterRepository();
    this.candidateRepository = new BinaryCandidateRepository();
    this.electionRepository = new BinaryElectionRepository();
    this.voteRepository = new BinaryVoteRepository();
    this.regionRepository = new BinaryRegionRepository();
    this.partyRepository = new BinaryPartyRepository();
  }

  public static DataContext getInstance() {
    return Holder.INSTANCE;
  }

  // ==================== Repository Getters ====================

  public VoterRepository voters() {
    return voterRepository;
  }

  public CandidateRepository candidates() {
    return candidateRepository;
  }

  public ElectionRepository elections() {
    return electionRepository;
  }

  public VoteRepository votes() {
    return voteRepository;
  }

  public RegionRepository regions() {
    return regionRepository;
  }

  public PartyRepository parties() {
    return partyRepository;
  }

  // ==================== Unit of Work ====================

  public <T extends Entity> void registerNew(T entity) {
    removeFromDeleted(entity);
    dirtyEntities.remove(entity);
    newEntities.add(entity);
  }

  public <T extends Entity> void registerDirty(T entity) {
    if (!newEntities.contains(entity) && !isDeleted(entity)) {
      dirtyEntities.add(entity);
    }
  }

  public <T extends Entity> void registerDeleted(T entity) {
    if (newEntities.remove(entity)) {
      return;
    }
    dirtyEntities.remove(entity);
    addToDeleted(entity);
  }

  @SuppressWarnings("unchecked")
  public void commit() {
    // Вставляємо нові
    for (Entity entity : newEntities) {
      Repository<Entity> repo = getRepositoryForEntity(entity);
      if (repo != null) {
        repo.save(entity);
      }
    }

    // Оновлюємо змінені
    for (Entity entity : dirtyEntities) {
      Repository<Entity> repo = getRepositoryForEntity(entity);
      if (repo != null) {
        repo.save(entity);
      }
    }

    // Видаляємо
    for (Map.Entry<Repository<? extends Entity>, Set<UUID>> entry : deletedIdsMap.entrySet()) {
      Repository<Entity> repo = (Repository<Entity>) entry.getKey();
      for (UUID id : entry.getValue()) {
        repo.deleteById(id);
      }
    }

    clear();
  }

  public void rollback() {
    clear();
  }

  public void clear() {
    newEntities.clear();
    dirtyEntities.clear();
    deletedIdsMap.clear();
  }

  public boolean hasChanges() {
    return !newEntities.isEmpty()
        || !dirtyEntities.isEmpty()
        || !deletedIdsMap.isEmpty();
  }

  public String getChangesSummary() {
    int deletedCount = deletedIdsMap.values().stream()
        .mapToInt(Set::size)
        .sum();

    return String.format("New: %d, Dirty: %d, Deleted: %d",
        newEntities.size(), dirtyEntities.size(), deletedCount);
  }

  // ==================== Helper Methods ====================

  @SuppressWarnings("unchecked")
  private <T extends Entity> Repository<T> getRepositoryForEntity(T entity) {
    String className = entity.getClass().getSimpleName();

    return switch (className) {
      case "Voter" -> (Repository<T>) voterRepository;
      case "Candidate" -> (Repository<T>) candidateRepository;
      case "Election" -> (Repository<T>) electionRepository;
      case "Vote" -> (Repository<T>) voteRepository;
      case "Region" -> (Repository<T>) regionRepository;
      case "Party" -> (Repository<T>) partyRepository;
      default -> null;
    };
  }

  private void addToDeleted(Entity entity) {
    Repository<? extends Entity> repo = getRepositoryForEntity(entity);
    if (repo != null) {
      deletedIdsMap.computeIfAbsent(repo, k -> new LinkedHashSet<>())
          .add(entity.getId());
    }
  }

  private void removeFromDeleted(Entity entity) {
    Repository<? extends Entity> repo = getRepositoryForEntity(entity);
    if (repo != null) {
      Set<UUID> ids = deletedIdsMap.get(repo);
      if (ids != null) {
        ids.remove(entity.getId());
      }
    }
  }

  private boolean isDeleted(Entity entity) {
    Repository<? extends Entity> repo = getRepositoryForEntity(entity);
    if (repo != null) {
      Set<UUID> ids = deletedIdsMap.get(repo);
      return ids != null && ids.contains(entity.getId());
    }
    return false;
  }
}
