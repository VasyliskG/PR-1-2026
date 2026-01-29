package com.example.pr.presentation.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

  public String readPassword(String prompt) {
    System.out.print(CYAN + prompt + ": " + RESET);
    return scanner.nextLine();
  }

  public String readEmail(String prompt) {
    while (true) {
      String email = readRequiredString(prompt);
      if (email.matches("^[\\w.-]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
        return email.toLowerCase();
      }
      System.out.println(error("  ✗ Невірний формат email!"));
    }
  }

  public void pressEnterToContinue() {
    System.out.print(YELLOW + "\nНатисніть Enter для продовження..." + RESET);
    scanner.nextLine();
  }
}
