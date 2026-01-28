package com.example.pr.infrastructure.storage;

import com.example.pr.domain.Entity;
import com.example.pr.domain.specification.Specification;
import com.example.pr.infrastructure.storage.exception.StorageException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Базовий репозиторій для роботи з бінарними файлами. Використовує Java Serialization для
 * збереження/завантаження даних.
 *
 * @param <T> тип сутності
 */
public abstract class BinaryRepository<T extends Entity> implements Repository<T> {

  protected final Path filePath;

  // Identity Map для кешування
  protected final IdentityMap<T> identityMap = new IdentityMap<>();

  // Кеш списку
  private boolean cacheValid = false;
  private List<T> cachedList = null;

  protected BinaryRepository(String filename) {
    this.filePath = Path.of(filename);
    ensureDirectoryExists();
  }

  @Override
  public T save(T entity) {
    UUID id = entity.getId();

    // Оновлюємо Identity Map
    identityMap.put(id, entity);

    // Інвалідуємо кеш
    invalidateCache();

    // Завантажуємо, оновлюємо, зберігаємо
    List<T> entities = loadFromFile();

    boolean exists = entities.stream()
        .anyMatch(e -> e.getId().equals(id));

    if (exists) {
      entities.replaceAll(e -> e.getId().equals(id) ? entity : e);
    } else {
      entities.add(entity);
    }

    writeToFile(entities);
    return entity;
  }

  @Override
  public Optional<T> findById(UUID id) {
    return identityMap.get(id)
        .or(() -> findAllInternal().stream()
            .filter(entity -> entity.getId().equals(id))
            .findFirst());
  }

  @Override
  public List<T> findAll(Specification<T> spec) {
    return findAllInternal().stream()
        .filter(spec::isSatisfiedBy)
        .toList();
  }

  @Override
  public Optional<T> findOne(Specification<T> spec) {
    return findAllInternal().stream()
        .filter(spec::isSatisfiedBy)
        .findFirst();
  }

  @Override
  public long count(Specification<T> spec) {
    return findAllInternal().stream()
        .filter(spec::isSatisfiedBy)
        .count();
  }

  @Override
  public boolean exists(Specification<T> spec) {
    return findAllInternal().stream()
        .anyMatch(spec::isSatisfiedBy);
  }

  @Override
  public boolean deleteById(UUID id) {
    identityMap.remove(id);
    invalidateCache();

    List<T> entities = loadFromFile();
    boolean removed = entities.removeIf(entity -> entity.getId().equals(id));

    if (removed) {
      writeToFile(entities);
    }
    return removed;
  }

  @Override
  public boolean delete(T entity) {
    return deleteById(entity.getId());
  }

  @Override
  public boolean existsById(UUID id) {
    return identityMap.contains(id) || findById(id).isPresent();
  }

  /**
   * Інвалідує кеш.
   */
  protected void invalidateCache() {
    cacheValid = false;
    cachedList = null;
  }

  /**
   * Повертає всі сутності з кешу або файлу.
   */
  protected List<T> findAllInternal() {
    if (cacheValid && cachedList != null) {
      return cachedList;
    }

    cachedList = loadFromFile();
    cacheValid = true;

    // Заповнюємо Identity Map
    cachedList.forEach(entity -> {
      if (!identityMap.contains(entity.getId())) {
        identityMap.put(entity.getId(), entity);
      }
    });

    return cachedList;
  }

  /**
   * Читає всі сутності з бінарного файлу.
   */
  @SuppressWarnings("unchecked")
  protected List<T> loadFromFile() {
    if (!Files.exists(filePath)) {
      return new ArrayList<>();
    }

    try (ObjectInputStream ois = new ObjectInputStream(
        new BufferedInputStream(new FileInputStream(filePath.toFile())))) {

      Object obj = ois.readObject();
      if (obj instanceof List<?>) {
        return new ArrayList<>((List<T>) obj);
      }
      return new ArrayList<>();

    } catch (EOFException e) {
      // Порожній файл
      return new ArrayList<>();
    } catch (IOException | ClassNotFoundException e) {
      throw new StorageException("Помилка читання з бінарного файлу: " + filePath, e);
    }
  }

  /**
   * Записує всі сутності у бінарний файл.
   */
  protected void writeToFile(List<T> entities) {
    try (ObjectOutputStream oos = new ObjectOutputStream(
        new BufferedOutputStream(new FileOutputStream(filePath.toFile())))) {

      oos.writeObject(entities);
      oos.flush();

    } catch (IOException e) {
      throw new StorageException("Помилка запису у бінарний файл: " + filePath, e);
    }
  }

  /**
   * Створює директорію, якщо вона не існує.
   */
  private void ensureDirectoryExists() {
    Path parent = filePath.getParent();
    if (parent != null && !Files.exists(parent)) {
      try {
        Files.createDirectories(parent);
      } catch (IOException e) {
        throw new StorageException("Не вдалося створити директорію: " + parent, e);
      }
    }
  }
}
