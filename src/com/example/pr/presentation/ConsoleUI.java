package com.example.pr.presentation;

import com.example.pr.presentation.util.InputHelper;
import java.util.Scanner;

import static com.example.pr.presentation.util.ConsoleColors.*;

/**
 * Базовий клас консольного інтерфейсу.
 */
public class ConsoleUI {

  protected final Scanner scanner;
  protected final InputHelper input;

  public ConsoleUI() {
    this.scanner = new Scanner(System.in);
    this.input = new InputHelper(scanner);
  }

  public ConsoleUI(Scanner scanner) {
    this.scanner = scanner;
    this.input = new InputHelper(scanner);
  }

  public void printHeader() {
    System.out.println();
    System.out.println(BLUE_BOLD + "╔════════════════════════════════════════════════════════════════╗" + RESET);
    System.out.println(BLUE_BOLD + "║" + WHITE_BOLD + "          🗳️  СИСТЕМА ЕЛЕКТРОННОГО ГОЛОСУВАННЯ  🗳️              " + BLUE_BOLD + "║" + RESET);
    System.out.println(BLUE_BOLD + "╚════════════════════════════════════════════════════════════════╝" + RESET);
  }

  public void printDivider() {
    System.out.println(CYAN + "────────────────────────────────────────────────────────────────" + RESET);
  }

  public void printSuccess(String message) {
    System.out.println(GREEN + "✓ " + message + RESET);
  }

  public void printError(String message) {
    System.out.println(RED + "✗ " + message + RESET);
  }

  public void printWarning(String message) {
    System.out.println(YELLOW + "⚠ " + message + RESET);
  }

  public void printInfo(String message) {
    System.out.println(CYAN + "ℹ " + message + RESET);
  }

  protected void printMenuItem(int number, String text) {
    System.out.println(YELLOW + "  [" + number + "] " + RESET + text);
  }

  protected void printExitItem() {
    System.out.println(RED + "  [0] " + RESET + "Вийти");
  }

  protected void printBackItem() {
    System.out.println(PURPLE + "  [0] " + RESET + "Назад");
  }

  public void close() {
    scanner.close();
  }
}
