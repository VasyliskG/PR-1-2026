package com.example.pr.infrastructure.storage;

import com.example.pr.domain.Entity;
import com.example.pr.domain.specification.Specification;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface Repository<T extends Entity> {

  T save(T entity);

  Optional<T> findById(UUID id);

  // Default-реалізація - не потрібно імплементувати в кожному класі
  default Optional<T> findByCode(String code) {
    throw new UnsupportedOperationException(
        "findByCode is not supported for this entity"
    );
  }

  Optional<T> findOne(Specification<T> spec);

  List<T> findAll(Specification<T> spec);

  boolean deleteById(UUID id);

  // Default-реалізація
  default boolean deleteByCode(String code) {
    throw new UnsupportedOperationException(
        "deleteByCode is not supported for this entity"
    );
  }

  boolean delete(T entity);

  boolean existsById(UUID id);

  // Default-реалізація
  default boolean existsByCode(String code) {
    throw new UnsupportedOperationException(
        "existsByCode is not supported for this entity"
    );
  }

  long count(Specification<T> spec);

  boolean exists(Specification<T> spec);
}
