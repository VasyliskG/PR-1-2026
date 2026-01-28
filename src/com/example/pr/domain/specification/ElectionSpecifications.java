package com.example.pr.domain.specification;

import com.example.pr.domain.enums.ElectionStatus;
import com.example.pr.domain.impl.Election;
import java.time.LocalDateTime;

/**
 * Фабрика специфікацій для пошуку виборів (Election).
 * <p>
 * Поля Election: - name - description - startDate (LocalDateTime) - endDate (LocalDateTime) -
 * status (ElectionStatus)
 */
public final class ElectionSpecifications {

  private ElectionSpecifications() {
  }

  /**
   * Назва містить текст (без урахування регістру).
   */
  public static Specification<Election> nameContains(String text) {
    return election -> election.getName() != null
        && election.getName().toLowerCase().contains(text.toLowerCase());
  }

  /**
   * Точна назва.
   */
  public static Specification<Election> byName(String name) {
    return election -> election.getName() != null
        && election.getName().equals(name);
  }

  /**
   * Опис містить текст.
   */
  public static Specification<Election> descriptionContains(String text) {
    return election -> election.getDescription() != null
        && election.getDescription().toLowerCase().contains(text.toLowerCase());
  }

  /**
   * Має опис.
   */
  public static Specification<Election> hasDescription() {
    return election -> election.getDescription() != null
        && !election.getDescription().trim().isEmpty();
  }

  /**
   * Вибори за статусом.
   */
  public static Specification<Election> byStatus(ElectionStatus status) {
    return election -> election.getStatus() == status;
  }

  /**
   * Очікують початку.
   */
  public static Specification<Election> pending() {
    return election -> election.getStatus() == ElectionStatus.PENDING;
  }

  /**
   * Активні вибори.
   */
  public static Specification<Election> active() {
    return election -> election.getStatus() == ElectionStatus.ACTIVE;
  }

  /**
   * Завершені вибори.
   */
  public static Specification<Election> closed() {
    return election -> election.getStatus() == ElectionStatus.CLOSED;
  }

  /**
   * Скасовані вибори.
   */
  public static Specification<Election> cancelled() {
    return election -> election.getStatus() == ElectionStatus.CANCELLED;
  }

  /**
   * Починаються після вказаної дати.
   */
  public static Specification<Election> startingAfter(LocalDateTime dateTime) {
    return election -> election.getStartDate() != null
        && election.getStartDate().isAfter(dateTime);
  }

  /**
   * Починаються до вказаної дати.
   */
  public static Specification<Election> startingBefore(LocalDateTime dateTime) {
    return election -> election.getStartDate() != null
        && election.getStartDate().isBefore(dateTime);
  }

  /**
   * Починаються у вказану дату.
   */
  public static Specification<Election> startingOn(LocalDateTime dateTime) {
    return election -> election.getStartDate() != null
        && election.getStartDate().toLocalDate().equals(dateTime.toLocalDate());
  }

  /**
   * Завершуються після вказаної дати.
   */
  public static Specification<Election> endingAfter(LocalDateTime dateTime) {
    return election -> election.getEndDate() != null
        && election.getEndDate().isAfter(dateTime);
  }

  /**
   * Завершуються до вказаної дати.
   */
  public static Specification<Election> endingBefore(LocalDateTime dateTime) {
    return election -> election.getEndDate() != null
        && election.getEndDate().isBefore(dateTime);
  }

  /**
   * Вибори, що відбуваються зараз.
   */
  public static Specification<Election> currentlyRunning() {
    return election -> {
      LocalDateTime now = LocalDateTime.now();
      return election.getStartDate() != null
          && election.getEndDate() != null
          && now.isAfter(election.getStartDate())
          && now.isBefore(election.getEndDate());
    };
  }

  /**
   * Вибори, що ще не почались.
   */
  public static Specification<Election> notStartedYet() {
    return election -> election.getStartDate() != null
        && election.getStartDate().isAfter(LocalDateTime.now());
  }

  /**
   * Вибори, що вже завершились.
   */
  public static Specification<Election> alreadyEnded() {
    return election -> election.getEndDate() != null
        && election.getEndDate().isBefore(LocalDateTime.now());
  }

  /**
   * Вибори у вказаному діапазоні дат.
   */
  public static Specification<Election> inDateRange(LocalDateTime from, LocalDateTime to) {
    return election -> election.getStartDate() != null
        && election.getEndDate() != null
        && !election.getEndDate().isBefore(from)
        && !election.getStartDate().isAfter(to);
  }

  /**
   * Всі вибори.
   */
  public static Specification<Election> all() {
    return election -> true;
  }

  /**
   * Жодних виборів.
   */
  public static Specification<Election> none() {
    return election -> false;
  }
}
