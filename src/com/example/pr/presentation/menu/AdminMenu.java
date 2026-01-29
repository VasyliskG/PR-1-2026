package com.example.pr.presentation.menu;

import com.example.pr.domain.dto.voter.VoterResponseDto;
import com.example.pr.domain.enums.VoterRole;
import com.example.pr.domain.service.AuthService;
import com.example.pr.domain.service.ElectionService;
import com.example.pr.domain.service.VoterService;
import com.example.pr.domain.service.exception.ServiceException;
import com.example.pr.presentation.ConsoleUI;
import com.example.pr.presentation.util.TablePrinter;

import java.util.*;

import static com.example.pr.presentation.util.ConsoleColors.*;

/**
 * Адміністративна панель.
 */
public class AdminMenu extends ConsoleUI implements Menu {

  private final VoterService voterService;
  private final ElectionService electionService;
  private final AuthService authService;

  public AdminMenu(Scanner scanner, VoterService voterService,
      ElectionService electionService, AuthService authService) {
    super(scanner);
    this.voterService = voterService;
    this.electionService = electionService;
    this.authService = authService;
  }

  @Override
  public String getTitle() {
    return "Адмін-панель";
  }

  @Override
  public boolean show() {
    if (!authService.isAdmin()) {
      printError("Доступ заборонено! Потрібні права адміністратора.");
      input.pressEnterToContinue();
      return false;
    }

    TablePrinter.printSection("⚙️ " + getTitle());

    printMenuItem(1, "👥 Управління виборцями");
    printMenuItem(2, "🔑 Зміна ролей користувачів");
    printMenuItem(3, "📊 Статистика системи");
    printMenuItem(4, "🗑️ Видалити виборця");
    printBackItem();

    int choice = input.readMenuChoice(4);

    switch (choice) {
      case 1 -> manageVoters();
      case 2 -> changeUserRole();
      case 3 -> showStatistics();
      case 4 -> deleteVoter();
      case 0 -> { return false; }
    }

    return true;
  }

  private void manageVoters() {
    System.out.println("\n" + header("── Управління виборцями ──"));

    printMenuItem(1, "Всі виборці");
    printMenuItem(2, "Виборці за роллю");
    printMenuItem(3, "Пошук за ім'ям");
    printBackItem();

    int choice = input.readMenuChoice(3);

    switch (choice) {
      case 1 -> showAllVoters();
      case 2 -> showByRole();
      case 3 -> searchVoters();
    }
  }

  private void showAllVoters() {
    System.out.println("\n" + header("── Всі виборці ──"));
    displayVotersList(voterService.findAll());
    input.pressEnterToContinue();
  }

  private void showByRole() {
    System.out.println("\n" + info("Оберіть роль:"));
    System.out.println(YELLOW + "  [1] " + RESET + "VOTER - Виборці");
    System.out.println(YELLOW + "  [2] " + RESET + "ADMIN - Адміністратори");
    System.out.println(YELLOW + "  [3] " + RESET + "OBSERVER - Спостерігачі");

    int choice = input.readIntInRange("Ваш вибір", 1, 3);
    VoterRole role = switch (choice) {
      case 1 -> VoterRole.VOTER;
      case 2 -> VoterRole.ADMIN;
      case 3 -> VoterRole.OBSERVER;
      default -> VoterRole.VOTER;
    };

    displayVotersList(voterService.findByRole(role));
    input.pressEnterToContinue();
  }

  private void searchVoters() {
    String query = input.readRequiredString("Введіть ім'я або прізвище");
    displayVotersList(voterService.searchByName(query));
    input.pressEnterToContinue();
  }

  private void displayVotersList(List<VoterResponseDto> voters) {
    if (voters.isEmpty()) {
      printInfo("Список порожній.");
      return;
    }

    String[] headers = {"ID", "Ім'я", "Прізвище", "Email", "Роль"};
    List<String[]> rows = new ArrayList<>();

    for (VoterResponseDto v : voters) {
      rows.add(new String[]{
          v.id().toString().substring(0, 8) + "...",
          v.firstName(), v.lastName(), v.email(), v.role().toString()
      });
    }
    TablePrinter.print(headers, rows);
  }

  private void changeUserRole() {
    System.out.println("\n" + header("── Зміна ролі користувача ──"));

    try {
      UUID id = input.readUUID("ID виборця");
      VoterResponseDto voter = voterService.findById(id);

      System.out.println("\n" + info("Поточні дані:"));
      System.out.println("  Користувач: " + voter.fullName());
      System.out.println("  Email: " + voter.email());
      System.out.println("  Поточна роль: " + voter.role());

      System.out.println("\n" + info("Оберіть нову роль:"));
      System.out.println(YELLOW + "  [1] " + RESET + "VOTER - Виборець");
      System.out.println(YELLOW + "  [2] " + RESET + "ADMIN - Адміністратор");
      System.out.println(YELLOW + "  [3] " + RESET + "OBSERVER - Спостерігач");
      System.out.println(PURPLE + "  [0] " + RESET + "Скасувати");

      int choice = input.readIntInRange("Ваш вибір", 0, 3);
      if (choice == 0) {
        printInfo("Скасовано.");
        input.pressEnterToContinue();
        return;
      }

      VoterRole newRole = switch (choice) {
        case 1 -> VoterRole.VOTER;
        case 2 -> VoterRole.ADMIN;
        case 3 -> VoterRole.OBSERVER;
        default -> voter.role();
      };

      if (newRole == voter.role()) {
        printWarning("Роль не змінено.");
      } else {
        voterService.changeRole(id, newRole);
        printSuccess("Роль змінено на " + newRole);
      }

    } catch (ServiceException e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }

  private void showStatistics() {
    System.out.println("\n" + header("── Статистика системи ──"));

    long totalVoters = voterService.count();
    long adminCount = voterService.findByRole(VoterRole.ADMIN).size();
    long observerCount = voterService.findByRole(VoterRole.OBSERVER).size();
    long voterCount = totalVoters - adminCount - observerCount;

    var activeElections = electionService.findActive();
    var pendingElections = electionService.findPending();
    var closedElections = electionService.findClosed();

    System.out.println();
    System.out.println(WHITE_BOLD + "📊 Загальна статистика" + RESET);
    printDivider();

    System.out.println("\n" + info("Користувачі:"));
    System.out.println("  Всього: " + WHITE_BOLD + totalVoters + RESET);
    System.out.println("  • Виборці: " + voterCount);
    System.out.println("  • Адміністратори: " + adminCount);
    System.out.println("  • Спостерігачі: " + observerCount);

    System.out.println("\n" + info("Вибори:"));
    System.out.println("  Активні: " + GREEN + activeElections.size() + RESET);
    System.out.println("  Очікують: " + YELLOW + pendingElections.size() + RESET);
    System.out.println("  Завершені: " + BLUE + closedElections.size() + RESET);

    if (!activeElections.isEmpty()) {
      System.out.println("\n" + info("Активні вибори:"));
      for (var e : activeElections) {
        System.out.println("  • " + e.name());
      }
    }

    input.pressEnterToContinue();
  }

  private void deleteVoter() {
    System.out.println("\n" + header("── Видалення виборця ──"));

    try {
      UUID id = input.readUUID("ID виборця для видалення");

      VoterResponseDto voter = voterService.findById(id);

      // Перевірка на самовидалення
      VoterResponseDto currentUser = authService.getCurrentUser().orElseThrow();
      if (voter.id().equals(currentUser.id())) {
        printError("Ви не можете видалити себе!");
        input.pressEnterToContinue();
        return;
      }

      printWarning("Ви збираєтесь видалити: " + voter.fullName() + " (" + voter.email() + ")");
      printWarning("Ця дія видалить всі голоси цього виборця!");

      if (input.confirm("Підтвердити видалення?")) {
        voterService.delete(id);
        printSuccess("Виборця видалено!");
      } else {
        printInfo("Видалення скасовано.");
      }

    } catch (ServiceException e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }
}
