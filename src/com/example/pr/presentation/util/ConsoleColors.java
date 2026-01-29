package com.example.pr.presentation.util;

/**
 * ANSI коди кольорів для консолі.
 */
public final class ConsoleColors {

  // Reset
  public static final String RESET = "\033[0m";

  // Звичайні кольори
  public static final String BLACK = "\033[0;30m";
  public static final String RED = "\033[0;31m";
  public static final String GREEN = "\033[0;32m";
  public static final String YELLOW = "\033[0;33m";
  public static final String BLUE = "\033[0;34m";
  public static final String PURPLE = "\033[0;35m";
  public static final String CYAN = "\033[0;36m";
  public static final String WHITE = "\033[0;37m";

  // Жирні кольори
  public static final String RED_BOLD = "\033[1;31m";
  public static final String GREEN_BOLD = "\033[1;32m";
  public static final String YELLOW_BOLD = "\033[1;33m";
  public static final String BLUE_BOLD = "\033[1;34m";
  public static final String PURPLE_BOLD = "\033[1;35m";
  public static final String CYAN_BOLD = "\033[1;36m";
  public static final String WHITE_BOLD = "\033[1;37m";

  private ConsoleColors() {}

  public static String success(String text) {
    return GREEN + text + RESET;
  }

  public static String error(String text) {
    return RED + text + RESET;
  }

  public static String warning(String text) {
    return YELLOW + text + RESET;
  }

  public static String info(String text) {
    return CYAN + text + RESET;
  }

  public static String header(String text) {
    return BLUE_BOLD + text + RESET;
  }
}
