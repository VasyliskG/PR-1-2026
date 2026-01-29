package com.example.pr.presentation.menu;

/**
 * Інтерфейс меню.
 */
public interface Menu {
  /**
   * Відображає меню та обробляє вибір.
   * @return true - продовжити, false - вийти
   */
  boolean show();

  /**
   * Назва меню.
   */
  String getTitle();
}
