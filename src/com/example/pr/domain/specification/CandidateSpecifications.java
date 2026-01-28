package com.example.pr.domain.specification;

import com.example.pr.domain.impl.Candidate;
import java.util.UUID;

/**
 * Фабрика специфікацій для пошуку кандидатів (Candidate).
 * <p>
 * Поля Candidate: - firstName, lastName - passportNumber - partyId (UUID) - electionId (UUID) -
 * program - photoPath - biography
 */
public final class CandidateSpecifications {

  private CandidateSpecifications() {
  }

  /**
   * Пошук за ім'ям.
   */
  public static Specification<Candidate> firstNameContains(String text) {
    return candidate -> candidate.getFirstName() != null
        && candidate.getFirstName().toLowerCase().contains(text.toLowerCase());
  }

  /**
   * Пошук за прізвищем.
   */
  public static Specification<Candidate> lastNameContains(String text) {
    return candidate -> candidate.getLastName() != null
        && candidate.getLastName().toLowerCase().contains(text.toLowerCase());
  }

  /**
   * Пошук за повним ім'ям.
   */
  public static Specification<Candidate> fullNameContains(String text) {
    return candidate -> {
      String fullName = (candidate.getFirstName() + " " + candidate.getLastName()).toLowerCase();
      return fullName.contains(text.toLowerCase());
    };
  }

  /**
   * Точне співпадіння прізвища.
   */
  public static Specification<Candidate> byLastName(String lastName) {
    return candidate -> candidate.getLastName() != null
        && candidate.getLastName().equals(lastName);
  }

  /**
   * Точний пошук за номером паспорта.
   */
  public static Specification<Candidate> byPassportNumber(String passportNumber) {
    return candidate -> candidate.getPassportNumber() != null
        && candidate.getPassportNumber().equals(passportNumber);
  }

  /**
   * Кандидати від конкретної партії.
   */
  public static Specification<Candidate> byPartyId(UUID partyId) {
    return candidate -> candidate.getPartyId() != null
        && candidate.getPartyId().equals(partyId);
  }

  /**
   * Незалежні кандидати.
   */
  public static Specification<Candidate> independent() {
    return candidate -> candidate.getPartyId() == null;
  }

  /**
   * Кандидати з партією.
   */
  public static Specification<Candidate> hasParty() {
    return candidate -> candidate.getPartyId() != null;
  }

  /**
   * Кандидати на конкретних виборах.
   */
  public static Specification<Candidate> byElectionId(UUID electionId) {
    return candidate -> candidate.getElectionId() != null
        && candidate.getElectionId().equals(electionId);
  }

  /**
   * Кандидати на конкретних виборах від конкретної партії.
   */
  public static Specification<Candidate> byElectionAndParty(UUID electionId, UUID partyId) {
    return byElectionId(electionId).and(byPartyId(partyId));
  }

  /**
   * Програма містить текст.
   */
  public static Specification<Candidate> programContains(String text) {
    return candidate -> candidate.getProgram() != null
        && candidate.getProgram().toLowerCase().contains(text.toLowerCase());
  }

  /**
   * Має програму.
   */
  public static Specification<Candidate> hasProgram() {
    return candidate -> candidate.getProgram() != null
        && !candidate.getProgram().trim().isEmpty();
  }

  /**
   * Біографія містить текст.
   */
  public static Specification<Candidate> biographyContains(String text) {
    return candidate -> candidate.getBiography() != null
        && candidate.getBiography().toLowerCase().contains(text.toLowerCase());
  }

  /**
   * Має біографію.
   */
  public static Specification<Candidate> hasBiography() {
    return candidate -> candidate.getBiography() != null
        && !candidate.getBiography().trim().isEmpty();
  }

  /**
   * Має фото.
   */
  public static Specification<Candidate> hasPhoto() {
    return candidate -> candidate.getPhotoPath() != null
        && !candidate.getPhotoPath().trim().isEmpty();
  }

  /**
   * Всі кандидати.
   */
  public static Specification<Candidate> all() {
    return candidate -> true;
  }

  /**
   * Жодного кандидата.
   */
  public static Specification<Candidate> none() {
    return candidate -> false;
  }
}
