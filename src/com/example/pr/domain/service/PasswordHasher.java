package com.example.pr.domain.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Утиліта для хешування паролів.
 * Використовує SHA-256 з сіллю.
 */
public final class PasswordHasher {

  private static final SecureRandom RANDOM = new SecureRandom();
  private static final int SALT_LENGTH = 16;

  private PasswordHasher() {}

  /**
   * Хешує пароль з генерованою сіллю.
   * Формат: salt$hash
   */
  public static String hash(String password) {
    byte[] salt = new byte[SALT_LENGTH];
    RANDOM.nextBytes(salt);
    String saltBase64 = Base64.getEncoder().encodeToString(salt);
    String hash = hashWithSalt(password, salt);
    return saltBase64 + "$" + hash;
  }

  /**
   * Перевіряє пароль проти хешу.
   */
  public static boolean verify(String password, String storedHash) {
    if (password == null || storedHash == null) {
      return false;
    }

    String[] parts = storedHash.split("\\$");
    if (parts.length != 2) {
      return false;
    }

    try {
      byte[] salt = Base64.getDecoder().decode(parts[0]);
      String expectedHash = parts[1];
      String actualHash = hashWithSalt(password, salt);
      return expectedHash.equals(actualHash);
    } catch (Exception e) {
      return false;
    }
  }

  private static String hashWithSalt(String password, byte[] salt) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      md.update(salt);
      byte[] hashedBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));
      return Base64.getEncoder().encodeToString(hashedBytes);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("SHA-256 not available", e);
    }
  }
}
