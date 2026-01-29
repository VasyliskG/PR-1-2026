package com.example.pr.presentation.util;

import java.io.Console;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Scanner;
import java.util.UUID;

import static com.example.pr.presentation.util.ConsoleColors.*;

/**
 * Допоміжний клас для вводу даних з консолі.
 */
public class InputHelper {

  private final Scanner scanner;
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

  // Патерни валідації
  private static final String EMAIL_PATTERN = "^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$";
  private static final String PASSPORT_PATTERN = "^[A-Z]{2}\\d{6}$";
  private static final String NAME_PATTERN = "^[A-Za-zА-Яа-яІіЇїЄєҐґ'\\-\\s]{2,50}$";

  public InputHelper(Scanner scanner) {
    this.scanner = scanner;
  }

  public String readString(String prompt) {
    System.out.print(CYAN + prompt + ": " + RESET);
    return scanner.nextLine().trim();
  }

  public String readRequiredString(String prompt) {
    while (true) {
      String value = readString(prompt);
      if (!value.isEmpty()) {
        return value;
      }
      System.out.println(error("  ✗ Це поле є обов'язковим!"));
    }
  }

  public Optional<String> readOptionalString(String prompt) {
    String value = readString(prompt + " (Enter - пропустити)");
    return value.isEmpty() ? Optional.empty() : Optional.of(value);
  }

  public int readInt(String prompt) {
    while (true) {
      try {
        System.out.print(CYAN + prompt + ": " + RESET);
        String input = scanner.nextLine().trim();
        return Integer.parseInt(input);
      } catch (NumberFormatException e) {
        System.out.println(error("  ✗ Введіть ціле число!"));
      }
    }
  }

  public int readIntInRange(String prompt, int min, int max) {
    while (true) {
      int value = readInt(prompt + " (" + min + "-" + max + ")");
      if (value >= min && value <= max) {
        return value;
      }
      System.out.println(error("  ✗ Значення повинно бути від " + min + " до " + max));
    }
  }

  public int readMenuChoice(int maxOption) {
    return readIntInRange("Ваш вибір", 0, maxOption);
  }

  public LocalDate readDate(String prompt) {
    while (true) {
      try {
        String input = readString(prompt + " (дд.мм.рррр)");
        return LocalDate.parse(input, DATE_FORMAT);
      } catch (DateTimeParseException e) {
        System.out.println(error("  ✗ Невірний формат! Використовуйте: дд.мм.рррр"));
      }
    }
  }

  /**
   * Читає дату народження з перевіркою мінімального віку.
   */
  public LocalDate readBirthDate(String prompt, int minAge) {
    while (true) {
      try {
        String input = readString(prompt + " (дд.мм.рррр)");
        LocalDate birthDate = LocalDate.parse(input, DATE_FORMAT);

        // Перевірка на майбутню дату
        if (birthDate.isAfter(LocalDate.now())) {
          System.out.println(error("  ✗ Дата народження не може бути в майбутньому!"));
          continue;
        }

        // Перевірка мінімального віку
        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age < minAge) {
          System.out.println(error("  ✗ Мінімальний вік для реєстрації: " + minAge + " років. Ваш вік: " + age));
          continue;
        }

        return birthDate;
      } catch (DateTimeParseException e) {
        System.out.println(error("  ✗ Невірний формат! Використовуйте: дд.мм.рррр"));
      }
    }
  }

  public LocalDateTime readDateTime(String prompt) {
    while (true) {
      try {
        String input = readString(prompt + " (дд.мм.рррр гг:хх)");
        return LocalDateTime.parse(input, DATETIME_FORMAT);
      } catch (DateTimeParseException e) {
        System.out.println(error("  ✗ Невірний формат! Використовуйте: дд.мм.рррр гг:хх"));
      }
    }
  }

  public UUID readUUID(String prompt) {
    while (true) {
      try {
        String input = readRequiredString(prompt);
        return UUID.fromString(input);
      } catch (IllegalArgumentException e) {
        System.out.println(error("  ✗ Невірний формат UUID!"));
      }
    }
  }

  public boolean confirm(String prompt) {
    while (true) {
      String input = readString(prompt + " (y/n)").toLowerCase();
      if (input.equals("y") || input.equals("yes") || input.equals("так")) {
        return true;
      }
      if (input.equals("n") || input.equals("no") || input.equals("ні")) {
        return false;
      }
      System.out.println(error("  ✗ Введіть 'y' або 'n'"));
    }
  }

  /**
   * Читає пароль з консолі (символи не відображаються).
   */
  public String readPassword(String prompt) {
    Console console = System.console();

    if (console != null) {
      // Працює в терміналі - пароль повністю прихований
      System.out.print(CYAN + prompt + ": " + RESET);
      char[] passwordChars = console.readPassword();
      return passwordChars != null ? new String(passwordChars) : "";
    } else {
      // Fallback для IDE (IntelliJ, Eclipse) - console == null
      System.out.print(CYAN + prompt + " (ввід видимий в IDE): " + RESET);
      return scanner.nextLine();
    }
  }

  /**
   * Читає пароль з підтвердженням та валідацією.
   */
  public String readPasswordWithConfirmation(String prompt, String confirmPrompt, int minLength) {
    while (true) {
      String password = readPassword(prompt);

      // Перевірка мінімальної довжини
      if (password.length() < minLength) {
        System.out.println(error("  ✗ Пароль повинен містити мінімум " + minLength + " символів!"));
        continue;
      }

      // Підтвердження пароля
      String confirmPassword = readPassword(confirmPrompt);

      if (!password.equals(confirmPassword)) {
        System.out.println(error("  ✗ Паролі не співпадають! Спробуйте ще раз."));
        continue;
      }

      return password;
    }
  }

  public String readEmail(String prompt) {
    while (true) {
      String email = readRequiredString(prompt);
      if (email.matches(EMAIL_PATTERN)) {
        return email.toLowerCase();
      }
      System.out.println(error("  ✗ Невірний формат email! Приклад: user@example.com"));
    }
  }

  /**
   * Читає номер паспорта з валідацією формату.
   * Формат: 2 великі латинські літери + 6 цифр (наприклад, AA123456)
   */
  public String readPassportNumber(String prompt) {
    while (true) {
      String passport = readRequiredString(prompt + " (формат: AA123456)");

      // Автоматично переводимо в верхній регістр
      passport = passport.toUpperCase();

      if (passport.matches(PASSPORT_PATTERN)) {
        return passport;
      }
      System.out.println(error("  ✗ Невірний формат! Очікується: 2 великі латинські літери + 6 цифр"));
      System.out.println(info("    Приклад: AA123456, BC789012"));
    }
  }

  /**
   * Читає ім'я або прізвище з валідацією.
   */
  public String readName(String prompt) {
    while (true) {
      String name = readRequiredString(prompt);

      if (name.length() < 2) {
        System.out.println(error("  ✗ Мінімальна довжина: 2 символи!"));
        continue;
      }

      if (name.length() > 50) {
        System.out.println(error("  ✗ Максимальна довжина: 50 символів!"));
        continue;
      }

      if (!name.matches(NAME_PATTERN)) {
        System.out.println(error("  ✗ Ім'я може містити лише літери, апостроф та дефіс!"));
        continue;
      }

      return name;
    }
  }

  public void pressEnterToContinue() {
    System.out.print(YELLOW + "\nНатисніть Enter для продовження..." + RESET);
    scanner.nextLine();
  }
}
