package com.example.pr.infrastructure.storage.impl;

import com.example.pr.domain.Entity;
import com.example.pr.infrastructure.storage.Repository;
import com.example.pr.infrastructure.storage.contract.*;
import java.util.*;

/**
 * DataContext - єдина точка доступу до всіх репозиторіїв системи.
 * <p>
 * Реалізує патерни: - Singleton (одна інстанція на все застосування) - Unit of Work (відстеження та
 * групове збереження змін)
 * <p>
 * Використання:
 * <pre>{@code
 * DataContext context = DataContext.getInstance();
 *
 * // === Пряме використання репозиторіїв (CRUD) ===
 *
 * // CREATE
 * Voter voter = new Voter("Іван", "Петренко", ...);
 * context.voters().save(voter);
 *
 * // READ
 * Optional<Voter> found = context.voters().findById(voterId);
 * List<Voter> admins = context.voters().findAll(VoterSpecifications.byRole(VoterRole.ADMIN));
 *
 * // UPDATE
 * voter.setEmail("new@email.com");
 * context.voters().save(voter);
 *
 * // DELETE
 * context.voters().deleteById(voterId);
 *
 * // === Unit of Work (батчеве збереження) ===
 *
 * Voter v1 = new Voter(...);
 * Voter v2 = new Voter(...);
 * context.registerNew(v1);
 * context.registerNew(v2);
 * context.commit(); // Зберігає всі зміни разом
 * }</pre>
 */
public class DataContext {

  // Singleton (Bill Pugh pattern - thread-safe)
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

  // Unit of Work - колекції для відстеження змін
  private final Set<Entity> newEntities = new LinkedHashSet<>();     // Нові сутності
  private final Set<Entity> dirtyEntities = new LinkedHashSet<>();   // Змінені сутності
  private final Map<Repository<? extends Entity>, Set<UUID>> deletedIdsMap = new HashMap<>(); // Видалені

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

  /**
   * Реєструє нову сутність для вставки при commit().
   */
  public <T extends Entity> void registerNew(T entity) {
    removeFromDeleted(entity);
    dirtyEntities.remove(entity);
    newEntities.add(entity);
  }

  /**
   * Реєструє змінену сутність для оновлення при commit().
   */
  public <T extends Entity> void registerDirty(T entity) {
    if (!newEntities.contains(entity) && !isDeleted(entity)) {
      dirtyEntities.add(entity);
    }
  }

  /**
   * Реєструє сутність для видалення при commit().
   */
  public <T extends Entity> void registerDeleted(T entity) {
    if (newEntities.remove(entity)) {
      return; // Якщо була новою - просто видаляємо з черги
    }
    dirtyEntities.remove(entity);
    addToDeleted(entity);
  }

  /**
   * Фіксує всі зміни.
   */
  @SuppressWarnings("unchecked")
  public void commit() {
    // 1. Зберігаємо нові
    for (Entity entity : newEntities) {
      Repository<Entity> repo = getRepositoryForEntity(entity);
      if (repo != null) {
        repo.save(entity);
      }
    }

    // 2. Оновлюємо змінені
    for (Entity entity : dirtyEntities) {
      Repository<Entity> repo = getRepositoryForEntity(entity);
      if (repo != null) {
        repo.save(entity);
      }
    }

    // 3. Видаляємо
    for (Map.Entry<Repository<? extends Entity>, Set<UUID>> entry : deletedIdsMap.entrySet()) {
      Repository<Entity> repo = (Repository<Entity>) entry.getKey();
      for (UUID id : entry.getValue()) {
        repo.deleteById(id);
      }
    }

    // 4. Очищаємо черги
    clear();
  }

  /**
   * Відкочує всі незбережені зміни.
   */
  public void rollback() {
    clear();
  }

  /**
   * Очищає всі черги змін.
   */
  public void clear() {
    newEntities.clear();
    dirtyEntities.clear();
    deletedIdsMap.clear();
  }

  /**
   * Перевіряє, чи є незбережені зміни.
   */
  public boolean hasChanges() {
    return !newEntities.isEmpty() || !dirtyEntities.isEmpty() || !deletedIdsMap.isEmpty();
  }

  /**
   * Повертає статистику змін.
   */
  public String getChangesSummary() {
    int deletedCount = deletedIdsMap.values().stream().mapToInt(Set::size).sum();
    return String.format("New: %d, Modified: %d, Deleted: %d",
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
      deletedIdsMap.computeIfAbsent(repo, k -> new LinkedHashSet<>()).add(entity.getId());
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
