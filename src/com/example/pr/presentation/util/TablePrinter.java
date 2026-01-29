package com.example.pr.presentation.util;

import java.util.List;

import static com.example.pr.presentation.util.ConsoleColors.*;

/**
 * Утиліта для друку таблиць у консолі.
 */
public final class TablePrinter {

  private TablePrinter() {}

  /**
   * Друкує таблицю з заголовками та рядками.
   */
  public static void print(String[] headers, List<String[]> rows) {
    if (headers == null || headers.length == 0) return;

    int[] widths = calculateWidths(headers, rows);

    printSeparator(widths);
    printRow(headers, widths, true);
    printSeparator(widths);
    for (String[] row : rows) {
      printRow(row, widths, false);
    }
    printSeparator(widths);
  }

  private static int[] calculateWidths(String[] headers, List<String[]> rows) {
    int[] widths = new int[headers.length];
    for (int i = 0; i < headers.length; i++) {
      widths[i] = headers[i].length();
    }
    for (String[] row : rows) {
      for (int i = 0; i < Math.min(row.length, widths.length); i++) {
        if (row[i] != null) {
          widths[i] = Math.max(widths[i], row[i].length());
        }
      }
    }
    return widths;
  }

  private static void printSeparator(int[] widths) {
    StringBuilder sb = new StringBuilder(CYAN + "+" + RESET);
    for (int width : widths) {
      sb.append(CYAN).append("-".repeat(width + 2)).append("+").append(RESET);
    }
    System.out.println(sb);
  }

  private static void printRow(String[] row, int[] widths, boolean isHeader) {
    StringBuilder sb = new StringBuilder(CYAN + "|" + RESET);
    for (int i = 0; i < widths.length; i++) {
      String value = i < row.length && row[i] != null ? row[i] : "";
      String formatted = String.format(" %-" + widths[i] + "s ", value);
      if (isHeader) {
        sb.append(WHITE_BOLD).append(formatted).append(RESET);
      } else {
        sb.append(formatted);
      }
      sb.append(CYAN).append("|").append(RESET);
    }
    System.out.println(sb);
  }

  /**
   * Друкує заголовок секції.
   */
  public static void printSection(String title) {
    int width = 60;
    int padding = (width - title.length() - 2) / 2;
    System.out.println();
    System.out.println(BLUE_BOLD + "═".repeat(width) + RESET);
    System.out.println(BLUE_BOLD + "║" + " ".repeat(padding) + WHITE_BOLD + title +
        BLUE_BOLD + " ".repeat(width - padding - title.length() - 2) + "║" + RESET);
    System.out.println(BLUE_BOLD + "═".repeat(width) + RESET);
  }
}
