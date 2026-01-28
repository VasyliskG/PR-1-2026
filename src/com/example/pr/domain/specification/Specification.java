package com.example.pr.domain.specification;

import java.util.function.Predicate;

@FunctionalInterface
public interface Specification<T> {

  boolean isSatisfiedBy(T entity);

  default Specification<T> and(Specification<T> other) {
    return entity -> this.isSatisfiedBy(entity) && other.isSatisfiedBy(entity);
  }

  default Specification<T> or(Specification<T> other) {
    return entity -> this.isSatisfiedBy(entity) || other.isSatisfiedBy(entity);
  }

  default Specification<T> not() {
    return entity -> !this.isSatisfiedBy(entity);
  }

  default Predicate<T> toPredicate() {
    return this::isSatisfiedBy;
  }
}