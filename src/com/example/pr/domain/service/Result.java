package com.example.pr.domain.service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Результат операції сервісу.
 * Використовується замість виключень для обробки помилок.
 *
 * @param <T> тип даних при успіху
 */
public sealed interface Result<T> {

  boolean isSuccess();
  boolean isFailure();

  record Success<T>(T data) implements Result<T> {
    @Override public boolean isSuccess() { return true; }
    @Override public boolean isFailure() { return false; }
  }

  record Failure<T>(String error, ErrorCode code) implements Result<T> {
    public Failure(String error) {
      this(error, ErrorCode.GENERAL_ERROR);
    }
    @Override public boolean isSuccess() { return false; }
    @Override public boolean isFailure() { return true; }
  }

  // Factory methods
  static <T> Result<T> success(T data) {
    return new Success<>(data);
  }

  static <T> Result<T> failure(String error) {
    return new Failure<>(error);
  }

  static <T> Result<T> failure(String error, ErrorCode code) {
    return new Failure<>(error, code);
  }

  // Utility methods
  default Optional<T> getData() {
    return this instanceof Success<T> s ? Optional.of(s.data()) : Optional.empty();
  }

  default Optional<String> getError() {
    return this instanceof Failure<T> f ? Optional.of(f.error()) : Optional.empty();
  }

  default <R> Result<R> map(Function<T, R> mapper) {
    return switch (this) {
      case Success<T> s -> Result.success(mapper.apply(s.data()));
      case Failure<T> f -> Result.failure(f.error(), f.code());
    };
  }

  default Result<T> onSuccess(Consumer<T> action) {
    if (this instanceof Success<T> s) {
      action.accept(s.data());
    }
    return this;
  }

  default Result<T> onFailure(Consumer<String> action) {
    if (this instanceof Failure<T> f) {
      action.accept(f.error());
    }
    return this;
  }

  /**
   * Коди помилок.
   */
  enum ErrorCode {
    GENERAL_ERROR,
    NOT_FOUND,
    ALREADY_EXISTS,
    VALIDATION_ERROR,
    UNAUTHORIZED,
    FORBIDDEN,
    INVALID_CREDENTIALS,
    ELECTION_NOT_ACTIVE,
    ALREADY_VOTED
  }
}
