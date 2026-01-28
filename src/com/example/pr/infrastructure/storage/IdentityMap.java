package com.example.pr.infrastructure.storage;

import com.example.pr.domain.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Реалізація патерну Identity Map. Кешує завантажені сутності за їх ідентифікаторами.
 *
 * @param <T> тип сутності
 */
public class IdentityMap<T extends Entity> {

  private final Map<UUID, T> cache = new HashMap<>();

  public Optional<T> get(UUID id) {
    return Optional.ofNullable(cache.get(id));
  }

  public void put(UUID id, T entity) {
    cache.put(id, entity);
  }

  public void remove(UUID id) {
    cache.remove(id);
  }

  public void clear() {
    cache.clear();
  }

  public boolean contains(UUID id) {
    return cache.containsKey(id);
  }

  public int size() {
    return cache.size();
  }
}