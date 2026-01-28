package com.example.pr.domain.service;

import com.example.pr.domain.dto.auth.AuthResponseDto;
import com.example.pr.domain.dto.auth.LoginDto;
import com.example.pr.domain.dto.auth.RegisterDto;
import com.example.pr.domain.dto.voter.VoterResponseDto;
import com.example.pr.domain.enums.VoterRole;
import com.example.pr.domain.impl.Voter;
import com.example.pr.domain.service.PasswordHasher;
import com.example.pr.domain.service.exception.AuthenticationException;
import com.example.pr.domain.service.exception.DuplicateEntityException;
import com.example.pr.domain.service.exception.EntityNotFoundException;
import com.example.pr.infrastructure.storage.impl.DataContext;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервіс аутентифікації та авторизації.
 */
public class AuthService {

  private static final int MIN_AGE = 18;

  private final DataContext context;

  // Поточний авторизований користувач (для простоти - в пам'яті)
  private Voter currentUser;
  private String sessionToken;

  public AuthService() {
    this.context = DataContext.getInstance();
  }

  /**
   * Реєстрація нового користувача.
   */
  public VoterResponseDto register(RegisterDto dto) {
    // Перевірка унікальності email
    if (context.voters().existsByEmail(dto.email())) {
      throw new DuplicateEntityException("Виборець", "email", dto.email());
    }

    // Перевірка унікальності паспорта
    if (context.voters().existsByPassportNumber(dto.passportNumber())) {
      throw new DuplicateEntityException("Виборець", "passportNumber", dto.passportNumber());
    }

    // Перевірка віку
    int age = Period.between(dto.birthDate(), LocalDate.now()).getYears();
    if (age < MIN_AGE) {
      throw new AuthenticationException("Виборець повинен бути старше " + MIN_AGE + " років");
    }

    // Перевірка існування регіону
    if (!context.regions().existsById(dto.regionId())) {
      throw new EntityNotFoundException("Регіон", dto.regionId());
    }

    // Хешування пароля
    String passwordHash = PasswordHasher.hash(dto.password());

    // Створення виборця
    Voter voter = new Voter(
        dto.firstName(),
        dto.lastName(),
        dto.email(),
        passwordHash,
        dto.passportNumber(),
        dto.birthDate(),
        VoterRole.VOTER,  // За замовчуванням - звичайний виборець
        dto.regionId()
    );

    context.voters().save(voter);

    return VoterResponseDto.fromEntity(voter);
  }

  /**
   * Вхід в систему.
   */
  public AuthResponseDto login(LoginDto dto) {
    Optional<Voter> voterOpt = context.voters().findByEmail(dto.email());

    if (voterOpt.isEmpty()) {
      throw AuthenticationException.invalidCredentials();
    }

    Voter voter = voterOpt.get();

    // Перевірка пароля
    if (!PasswordHasher.verify(dto.password(), voter.getPasswordHash())) {
      throw AuthenticationException.invalidCredentials();
    }

    // Генерація токена сесії
    sessionToken = generateSessionToken();
    currentUser = voter;

    return AuthResponseDto.success(VoterResponseDto.fromEntity(voter), sessionToken);
  }

  /**
   * Вихід з системи.
   */
  public void logout() {
    currentUser = null;
    sessionToken = null;
  }

  /**
   * Отримує поточного авторизованого користувача.
   */
  public Optional<VoterResponseDto> getCurrentUser() {
    if (currentUser == null) {
      return Optional.empty();
    }
    return Optional.of(VoterResponseDto.fromEntity(currentUser));
  }

  /**
   * Перевіряє, чи користувач авторизований.
   */
  public boolean isAuthenticated() {
    return currentUser != null && sessionToken != null;
  }

  /**
   * Перевіряє, чи поточний користувач є адміном.
   */
  public boolean isAdmin() {
    return currentUser != null && currentUser.getRole() == VoterRole.ADMIN;
  }

  /**
   * Перевіряє авторизацію та повертає поточного користувача.
   * @throws AuthenticationException якщо не авторизований
   */
  public Voter requireAuthentication() {
    if (currentUser == null) {
      throw AuthenticationException.notAuthenticated();
    }
    return currentUser;
  }

  /**
   * Перевіряє права адміна.
   * @throws AuthenticationException якщо не адмін
   */
  public void requireAdmin() {
    requireAuthentication();
    if (currentUser.getRole() != VoterRole.ADMIN) {
      throw new AuthenticationException("Потрібні права адміністратора");
    }
  }

  /**
   * Зміна пароля.
   */
  public void changePassword(String oldPassword, String newPassword) {
    Voter voter = requireAuthentication();

    // Перевірка старого пароля
    if (!PasswordHasher.verify(oldPassword, voter.getPasswordHash())) {
      throw new AuthenticationException("Невірний поточний пароль");
    }

    // Валідація нового пароля
    if (newPassword == null || newPassword.length() < 6) {
      throw new AuthenticationException("Новий пароль повинен містити мінімум 6 символів");
    }

    // Оновлення пароля
    voter.setPasswordHash(PasswordHasher.hash(newPassword));
    context.voters().save(voter);
  }

  private String generateSessionToken() {
    return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
  }
}
