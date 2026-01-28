package com.example.pr.domain.specification;

import com.example.pr.domain.impl.Party;

/**
 * Фабрика специфікацій для пошуку партій (Party).
 * <p>
 * Поля Party: - name - abbreviation - logoPath - program
 */
public final class PartySpecifications {

  private PartySpecifications() {
  }

  /**
   * Назва містить текст (без урахування регістру).
   */
  public static Specification<Party> nameContains(String text) {
    return party -> party.getName() != null
        && party.getName().toLowerCase().contains(text.toLowerCase());
  }

  /**
   * Точна назва.
   */
  public static Specification<Party> byName(String name) {
    return party -> party.getName() != null
        && party.getName().equals(name);
  }

  /**
   * Точна абревіатура.
   */
  public static Specification<Party> byAbbreviation(String abbreviation) {
    return party -> party.getAbbreviation() != null
        && party.getAbbreviation().equals(abbreviation);
  }

  /**
   * Абревіатура містить текст.
   */
  public static Specification<Party> abbreviationContains(String text) {
    return party -> party.getAbbreviation() != null
        && party.getAbbreviation().toLowerCase().contains(text.toLowerCase());
  }

  /**
   * Має логотип.
   */
  public static Specification<Party> hasLogo() {
    return party -> party.getLogoPath() != null
        && !party.getLogoPath().trim().isEmpty();
  }

  /**
   * Програма містить текст.
   */
  public static Specification<Party> programContains(String text) {
    return party -> party.getProgram() != null
        && party.getProgram().toLowerCase().contains(text.toLowerCase());
  }

  /**
   * Має програму.
   */
  public static Specification<Party> hasProgram() {
    return party -> party.getProgram() != null
        && !party.getProgram().trim().isEmpty();
  }

  /**
   * Всі партії.
   */
  public static Specification<Party> all() {
    return party -> true;
  }

  /**
   * Жодної партії.
   */
  public static Specification<Party> none() {
    return party -> false;
  }
}
