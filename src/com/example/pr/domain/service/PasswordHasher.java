package com.example.pr.domain.service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

/**
 * Утиліта для хешування паролів.
 * Використовує Argon2.
 */
public final class PasswordHasher {

  private PasswordHasher() {}

  /**
   * Хешує пароль.
   * Формат повернення — Argon2-хеш (усі параметри, включно з сіллю, у рядку).
   */
  public static String hash(String password) {
    Argon2 argon2 = Argon2Factory.create();
    int iterations = 3;
    int memory = 65536;
    int parallelism = 1;
    try {
      return argon2.hash(iterations, memory, parallelism, password.toCharArray());
    } finally {
      argon2.wipeArray(password.toCharArray());
    }
  }

  /**
   * Перевіряє пароль проти Argon2-хешу.
   */
  public static boolean verify(String password, String storedHash) {
    if (password == null || storedHash == null) {
      return false;
    }
    Argon2 argon2 = Argon2Factory.create();
    try {
      return argon2.verify(storedHash, password.toCharArray());
    } catch (Exception e) {
      return false;
    }
  }
}
