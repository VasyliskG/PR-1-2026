package com.example.pr.presentation.menu;

import com.example.pr.domain.dto.election.ElectionCreateDto;
import com.example.pr.domain.dto.election.ElectionResponseDto;
import com.example.pr.domain.service.AuthService;
import com.example.pr.domain.service.CandidateService;
import com.example.pr.domain.service.ElectionService;
import com.example.pr.domain.service.exception.ServiceException;
import com.example.pr.presentation.ConsoleUI;
import com.example.pr.presentation.util.TablePrinter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static com.example.pr.presentation.util.ConsoleColors.*;

/**
 * Меню виборів.
 */
public class ElectionMenu extends ConsoleUI implements Menu {

  private final ElectionService electionService;
  private final CandidateService candidateService;
  private final AuthService authService;

  public ElectionMenu(Scanner scanner, ElectionService electionService,
      CandidateService candidateService, AuthService authService) {
    super(scanner);
    this.electionService = electionService;
    this.candidateService = candidateService;
    this.authService = authService;
  }

  @Override
  public String getTitle() {
    return "Вибори";
  }

  @Override
  public boolean show() {
    TablePrinter.printSection("📋 " + getTitle());

    printMenuItem(1, "Всі вибори");
    printMenuItem(2, "Активні вибори");
    printMenuItem(3, "Детальна інформація");

    if (authService.isAdmin()) {
      System.out.println();
      System.out.println(PURPLE + "  ── Адміністрування ──" + RESET);
      printMenuItem(4, "Створити вибори");
      printMenuItem(5, "Активувати вибори");
      printMenuItem(6, "Завершити вибори");
      printMenuItem(7, "Скасувати вибори");
    }

    printBackItem();

    int maxChoice = authService.isAdmin() ? 7 : 3;
    int choice = input.readMenuChoice(maxChoice);

    switch (choice) {
      case 1 -> showAll();
      case 2 -> showActive();
      case 3 -> showDetails();
      case 4 -> { if (authService.isAdmin()) create(); }
      case 5 -> { if (authService.isAdmin()) activate(); }
      case 6 -> { if (authService.isAdmin()) close(); }
      case 7 -> { if (authService.isAdmin()) cancel(); }
      case 0 -> { return false; }
    }

    return true;
  }

  private void showAll() {
    System.out.println("\n" + header("── Всі вибори ──"));
    displayList(electionService.findAll());
    input.pressEnterToContinue();
  }

  public void showActive() {
    System.out.println("\n" + header("── Активні вибори ──"));
    List<ElectionResponseDto> active = electionService.findActive();
    if (active.isEmpty()) {
      printInfo("Немає активних виборів.");
    } else {
      displayList(active);
    }
    input.pressEnterToContinue();
  }

  private void displayList(List<ElectionResponseDto> elections) {
    if (elections.isEmpty()) {
      printInfo("Список порожній.");
      return;
    }

    String[] headers = {"ID", "Назва", "Статус", "Поча��ок", "Кінець", "Кандидатів"};
    List<String[]> rows = new ArrayList<>();

    for (ElectionResponseDto e : elections) {
      long count = candidateService.countByElection(e.id());
      rows.add(new String[]{
          e.id().toString().substring(0, 8) + "...",
          e.name().length() > 25 ? e.name().substring(0, 22) + "..." : e.name(),
          e.status().toString(), e.startDate().toString(),
          e.endDate().toString(), String.valueOf(count)
      });
    }
    TablePrinter.print(headers, rows);
  }

  private void showDetails() {
    System.out.println("\n" + header("── Детальна інформація ──"));

    UUID id = input.readUUID("Введіть ID виборів");

    try {
      ElectionResponseDto election = electionService.findById(id);

      System.out.println();
      System.out.println(WHITE_BOLD + election.name() + RESET);
      printDivider();
      System.out.println(info("ID: ") + election.id());
      System.out.println(info("Статус: ") + getStatusColored(election.status().toString()));
      System.out.println(info("Опис: ") + (election.description() != null ? election.description() : "-"));
      System.out.println(info("Початок: ") + election.startDate());
      System.out.println(info("Кінець: ") + election.endDate());

      var candidates = candidateService.findByElection(id);
      System.out.println("\n" + info("Кандидати (" + candidates.size() + "):"));
      for (var c : candidates) {
        String party = c.partyCode() != null ? " (" + c.partyCode() + ")" : " (Незалежний)";
        System.out.println("  • " + c.fullName() + party);
      }

    } catch (ServiceException e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }

  private String getStatusColored(String status) {
    return switch (status) {
      case "PENDING" -> YELLOW + status + RESET;
      case "ACTIVE" -> GREEN + status + RESET;
      case "CLOSED" -> BLUE + status + RESET;
      case "CANCELLED" -> RED + status + RESET;
      default -> status;
    };
  }

  private void create() {
    System.out.println("\n" + header("── Створення виборів ──"));

    try {
      String name = input.readRequiredString("Назва виборів");
      String description = input.readString("Опис (опціонально)");
      LocalDateTime startDate = input.readDateTime("Дата початку");
      LocalDateTime endDate = input.readDateTime("Дата завершення");

      ElectionResponseDto election = electionService.create(new ElectionCreateDto(
          name, description.isEmpty() ? null : description, startDate, endDate
      ));

      printSuccess("Вибори створено!");
      printInfo("ID: " + election.id());

    } catch (Exception e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }

  private void activate() {
    System.out.println("\n" + header("── Активація виборів ──"));

    try {
      showPending();
      UUID id = input.readUUID("ID виборів для активації");

      if (input.confirm("Активувати вибори?")) {
        ElectionResponseDto election = electionService.activate(id);
        printSuccess("Вибори '" + election.name() + "' активовано!");
      }
    } catch (ServiceException e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }

  private void showPending() {
    List<ElectionResponseDto> pending = electionService.findPending();
    if (pending.isEmpty()) {
      printInfo("Немає виборів, що очікують.");
      return;
    }
    System.out.println(info("Вибори, що очікують:"));
    for (var e : pending) {
      System.out.println("  • " + e.id() + " - " + e.name());
    }
  }

  public void close() {
    System.out.println("\n" + header("── Завершення виборів ──"));

    try {
      List<ElectionResponseDto> active = electionService.findActive();
      if (active.isEmpty()) {
        printInfo("Немає активних виборів.");
        input.pressEnterToContinue();
        return;
      }

      System.out.println(info("Активні вибори:"));
      for (var e : active) {
        System.out.println("  • " + e.id() + " - " + e.name());
      }

      UUID id = input.readUUID("ID виборів для завершення");

      if (input.confirm("Завершити вибори?")) {
        ElectionResponseDto election = electionService.close(id);
        printSuccess("Вибори '" + election.name() + "' завершено!");
      }
    } catch (ServiceException e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }

  private void cancel() {
    System.out.println("\n" + header("── Скасування виборів ──"));

    try {
      UUID id = input.readUUID("ID виборів для скасування");

      printWarning("Ця дія незворотна!");
      if (input.confirm("Скасувати вибори?")) {
        ElectionResponseDto election = electionService.cancel(id);
        printSuccess("Вибори '" + election.name() + "' скасовано!");
      }
    } catch (ServiceException e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }
}
