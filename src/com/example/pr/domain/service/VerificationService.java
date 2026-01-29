package com.example.pr.domain.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Сервіс для верифікації email адрес.
 */
public class VerificationService {

  private static final int CODE_EXPIRY_MINUTES = 10;

  private final EmailService emailService;

  // Зберігаємо коди в пам'яті: email -> (code, expiryTime)
  private final Map<String, VerificationData> pendingVerifications = new ConcurrentHashMap<>();

  public VerificationService(EmailService emailService) {
    this.emailService = emailService;
  }

  /**
   * Надсилає код верифікації на email.
   */
  public void sendVerificationCode(String email) throws Exception {
    String code = EmailService.generateVerificationCode();
    LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(CODE_EXPIRY_MINUTES);

    // Зберігаємо код
    pendingVerifications.put(email.toLowerCase(), new VerificationData(code, expiryTime));

    // Надсилаємо email
    emailService.sendVerificationCode(email, code);
  }

  /**
   * Перевіряє введений код.
   */
  public boolean verifyCode(String email, String inputCode) {
    VerificationData data = pendingVerifications.get(email.toLowerCase());

    if (data == null) {
      return false;
    }

    // Перевіряємо термін дії
    if (LocalDateTime.now().isAfter(data.expiryTime())) {
      pendingVerifications.remove(email.toLowerCase());
      return false;
    }

    // Перевіряємо код
    if (data.code().equals(inputCode)) {
      pendingVerifications.remove(email.toLowerCase());
      return true;
    }

    return false;
  }

  /**
   * Перевіряє чи код ще дійсний (не закінчився термін).
   */
  public boolean isCodeValid(String email) {
    VerificationData data = pendingVerifications.get(email.toLowerCase());
    return data != null && LocalDateTime.now().isBefore(data.expiryTime());
  }

  /**
   * Внутрішній record для зберігання даних верифікації.
   */
  private record VerificationData(String code, LocalDateTime expiryTime) {}
}
