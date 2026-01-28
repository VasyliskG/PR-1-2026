package com.example.pr.domain.specification;

import com.example.pr.domain.impl.Region;

/**
 * Фабрика специфікацій для пошуку регіонів (Region).
 * <p>
 * Поля Region: - name - code - description
 */
public final class RegionSpecifications {

  private RegionSpecifications() {
  }

  /**
   * Назва містить текст (без урахування регістру).
   */
  public static Specification<Region> nameContains(String text) {
    return region -> region.getName() != null
        && region.getName().toLowerCase().contains(text.toLowerCase());
  }

  /**
   * Точна назва.
   */
  public static Specification<Region> byName(String name) {
    return region -> region.getName() != null
        && region.getName().equals(name);
  }

  /**
   * Точний код регіону.
   */
  public static Specification<Region> byCode(String code) {
    return region -> region.getCode() != null
        && region.getCode().equals(code);
  }

  /**
   * Код починається з.
   */
  public static Specification<Region> codeStartsWith(String prefix) {
    return region -> region.getCode() != null
        && region.getCode().startsWith(prefix);
  }

  /**
   * Опис містить текст.
   */
  public static Specification<Region> descriptionContains(String text) {
    return region -> region.getDescription() != null
        && region.getDescription().toLowerCase().contains(text.toLowerCase());
  }

  /**
   * Має опис.
   */
  public static Specification<Region> hasDescription() {
    return region -> region.getDescription() != null
        && !region.getDescription().trim().isEmpty();
  }

  /**
   * Всі регіони.
   */
  public static Specification<Region> all() {
    return region -> true;
  }

  /**
   * Жодного регіону.
   */
  public static Specification<Region> none() {
    return region -> false;
  }
}
