package com.example.pr.domain.specification;

import com.example.pr.domain.enums.VoterRole;
import com.example.pr.domain.impl.Voter;
import java.time.LocalDate;
import java.time.Period;
import java.util.UUID;

/**
 * Фабрика специфікацій для пошуку виборців (Voter).
 * <p>
 * Поля Voter: - firstName, lastName - email, passwordHash - passportNumber - birthDate - role
 * (VoterRole) - regionId (UUID)
 */
public final class VoterSpecifications {

  private VoterSpecifications() {
  }

  /**
   * Пошук за ім'ям.
   */
  public static Specification<Voter> firstNameContains(String text) {
    return voter -> voter.getFirstName() != null
        && voter.getFirstName().toLowerCase().contains(text.toLowerCase());
  }

  /**
   * Пошук за прізвищем.
   */
  public static Specification<Voter> lastNameContains(String text) {
    return voter -> voter.getLastName() != null
        && voter.getLastName().toLowerCase().contains(text.toLowerCase());
  }

  /**
   * Пошук за повним ім'ям (ім'я + прізвище).
   */
  public static Specification<Voter> fullNameContains(String text) {
    return voter -> {
      String fullName = (voter.getFirstName() + " " + voter.getLastName()).toLowerCase();
      return fullName.contains(text.toLowerCase());
    };
  }

  /**
   * Точне співпадіння імені.
   */
  public static Specification<Voter> byFirstName(String firstName) {
    return voter -> voter.getFirstName() != null
        && voter.getFirstName().equals(firstName);
  }

  /**
   * Точне співпадіння прізвища.
   */
  public static Specification<Voter> byLastName(String lastName) {
    return voter -> voter.getLastName() != null
        && voter.getLastName().equals(lastName);
  }

  /**
   * Точний пошук за email.
   */
  public static Specification<Voter> byEmail(String email) {
    return voter -> voter.getEmail() != null
        && voter.getEmail().equalsIgnoreCase(email);
  }

  /**
   * Email містить текст.
   */
  public static Specification<Voter> emailContains(String text) {
    return voter -> voter.getEmail() != null
        && voter.getEmail().toLowerCase().contains(text.toLowerCase());
  }

  /**
   * Email з певного домену.
   */
  public static Specification<Voter> emailDomain(String domain) {
    return voter -> voter.getEmail() != null
        && voter.getEmail().toLowerCase().endsWith("@" + domain.toLowerCase());
  }

  /**
   * Точний пошук за номером паспорта.
   */
  public static Specification<Voter> byPassportNumber(String passportNumber) {
    return voter -> voter.getPassportNumber() != null
        && voter.getPassportNumber().equals(passportNumber);
  }

  /**
   * Паспорт починається з (серія).
   */
  public static Specification<Voter> passportStartsWith(String prefix) {
    return voter -> voter.getPassportNumber() != null
        && voter.getPassportNumber().startsWith(prefix);
  }

  /**
   * Народжені у вказану дату.
   */
  public static Specification<Voter> byBirthDate(LocalDate date) {
    return voter -> voter.getBirthDate() != null
        && voter.getBirthDate().equals(date);
  }

  /**
   * Народжені після вказаної дати.
   */
  public static Specification<Voter> bornAfter(LocalDate date) {
    return voter -> voter.getBirthDate() != null
        && voter.getBirthDate().isAfter(date);
  }

  /**
   * Народжені до вказаної дати.
   */
  public static Specification<Voter> bornBefore(LocalDate date) {
    return voter -> voter.getBirthDate() != null
        && voter.getBirthDate().isBefore(date);
  }

  /**
   * Народжені в діапазоні дат.
   */
  public static Specification<Voter> bornBetween(LocalDate from, LocalDate to) {
    return bornAfter(from.minusDays(1)).and(bornBefore(to.plusDays(1)));
  }

  /**
   * Виборці старші за вказаний вік.
   */
  public static Specification<Voter> olderThan(int years) {
    return voter -> {
      if (voter.getBirthDate() == null) {
        return false;
      }
      int age = Period.between(voter.getBirthDate(), LocalDate.now()).getYears();
      return age > years;
    };
  }

  /**
   * Виборці молодші за вказаний вік.
   */
  public static Specification<Voter> youngerThan(int years) {
    return voter -> {
      if (voter.getBirthDate() == null) {
        return false;
      }
      int age = Period.between(voter.getBirthDate(), LocalDate.now()).getYears();
      return age < years;
    };
  }

  /**
   * Виборці у вказаному віковому діапазоні.
   */
  public static Specification<Voter> ageBetween(int minAge, int maxAge) {
    return voter -> {
      if (voter.getBirthDate() == null) {
        return false;
      }
      int age = Period.between(voter.getBirthDate(), LocalDate.now()).getYears();
      return age >= minAge && age <= maxAge;
    };
  }

  /**
   * Пошук за роллю.
   */
  public static Specification<Voter> byRole(VoterRole role) {
    return voter -> voter.getRole() == role;
  }

  /**
   * Є адміністратором.
   */
  public static Specification<Voter> isAdmin() {
    return voter -> voter.getRole() == VoterRole.ADMIN;
  }

  /**
   * Є звичайним виборцем.
   */
  public static Specification<Voter> isVoter() {
    return voter -> voter.getRole() == VoterRole.VOTER;
  }

  /**
   * Є спостерігачем.
   */
  public static Specification<Voter> isObserver() {
    return voter -> voter.getRole() == VoterRole.OBSERVER;
  }

  /**
   * Виборці з конкретного регіону.
   */
  public static Specification<Voter> byRegionId(UUID regionId) {
    return voter -> voter.getRegionId() != null
        && voter.getRegionId().equals(regionId);
  }

  /**
   * Всі виборці.
   */
  public static Specification<Voter> all() {
    return voter -> true;
  }

  /**
   * Жодного виборця.
   */
  public static Specification<Voter> none() {
    return voter -> false;
  }
}
