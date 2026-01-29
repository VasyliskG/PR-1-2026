package com.example.pr.presentation.menu;

import com.example.pr.domain.dto.candidate.CandidateCreateDto;
import com.example.pr.domain.dto.candidate.CandidateResponseDto;
import com.example.pr.domain.dto.election.ElectionResponseDto;
import com.example.pr.domain.dto.party.PartyResponseDto;
import com.example.pr.domain.service.*;
import com.example.pr.domain.service.exception.ServiceException;
import com.example.pr.presentation.ConsoleUI;
import com.example.pr.presentation.util.TablePrinter;

import java.util.*;

import static com.example.pr.presentation.util.ConsoleColors.*;

/**
 * Меню кандидатів.
 */
public class CandidateMenu extends ConsoleUI implements Menu {

  private final CandidateService candidateService;
  private final ElectionService electionService;
  private final PartyService partyService;
  private final AuthService authService;

  public CandidateMenu(Scanner scanner, CandidateService candidateService,
      ElectionService electionService, PartyService partyService,
      AuthService authService) {
    super(scanner);
    this.candidateService = candidateService;
    this.electionService = electionService;
    this.partyService = partyService;
    this.authService = authService;
  }

  @Override
  public String getTitle() {
    return "Кандидати";
  }

  @Override
  public boolean show() {
    TablePrinter.printSection("👥 " + getTitle());

    printMenuItem(1, "Всі кандидати");
    printMenuItem(2, "Кандидати на виборах");
    printMenuItem(3, "Кандидати партії");
    printMenuItem(4, "Пошук за ім'ям");
    printMenuItem(5, "Детальна інформація");

    if (authService.isAdmin()) {
      System.out.println();
      System.out.println(PURPLE + "  ── Адміністрування ──" + RESET);
      printMenuItem(6, "Зареєструвати кандидата");
      printMenuItem(7, "Видалити кандидата");
    }

    printBackItem();

    int maxChoice = authService.isAdmin() ? 7 : 5;
    int choice = input.readMenuChoice(maxChoice);

    switch (choice) {
      case 1 -> showAll();
      case 2 -> showByElection();
      case 3 -> showByParty();
      case 4 -> search();
      case 5 -> showDetails();
      case 6 -> { if (authService.isAdmin()) create(); }
      case 7 -> { if (authService.isAdmin()) delete(); }
      case 0 -> { return false; }
    }

    return true;
  }

  public void showAll() {
    System.out.println("\n" + header("── Всі кандидати ──"));
    displayList(candidateService.findAll());
    input.pressEnterToContinue();
  }

  private void showByElection() {
    System.out.println("\n" + header("── Кандидати на виборах ──"));

    List<ElectionResponseDto> elections = electionService.findAll();
    if (elections.isEmpty()) {
      printInfo("Немає виборів у системі.");
      input.pressEnterToContinue();
      return;
    }

    System.out.println(info("Оберіть вибори:"));
    for (int i = 0; i < elections.size(); i++) {
      System.out.println(YELLOW + "  [" + (i + 1) + "] " + RESET + elections.get(i).name());
    }

    int choice = input.readIntInRange("Ваш вибір", 1, elections.size());
    ElectionResponseDto election = elections.get(choice - 1);

    displayList(candidateService.findByElection(election.id()));
    input.pressEnterToContinue();
  }

  private void showByParty() {
    System.out.println("\n" + header("── Кандидати партії ──"));

    List<PartyResponseDto> parties = partyService.findAll();
    if (parties.isEmpty()) {
      printInfo("Немає партій у системі.");
      input.pressEnterToContinue();
      return;
    }

    System.out.println(info("Введіть код партії:"));
    for (PartyResponseDto p : parties) {
      System.out.println("  " + YELLOW + p.partyCode() + RESET + " - " + p.name());
    }

    String partyCode = input.readString("Код партії (Enter - незалежні)");

    if (partyCode.isEmpty()) {
      System.out.println("\n" + info("Незалежні кандидати:"));
      displayList(candidateService.findIndependent());
    } else {
      displayList(candidateService.findByParty(partyCode.toUpperCase()));
    }

    input.pressEnterToContinue();
  }

  private void displayList(List<CandidateResponseDto> candidates) {
    if (candidates.isEmpty()) {
      printInfo("Список порожній.");
      return;
    }

    String[] headers = {"ID", "Ім'я", "Прізвище", "Партія", "Програма"};
    List<String[]> rows = new ArrayList<>();

    for (CandidateResponseDto c : candidates) {
      String party = c.partyCode() != null ? c.partyCode() : "Незалежний";
      String program = c.program() != null
          ? (c.program().length() > 30 ? c.program().substring(0, 27) + "..." : c.program())
          : "-";
      rows.add(new String[]{
          c.id().toString().substring(0, 8) + "...",
          c.firstName(), c.lastName(), party, program
      });
    }
    TablePrinter.print(headers, rows);
  }

  private void search() {
    System.out.println("\n" + header("── Пошук кандидатів ──"));

    String query = input.readRequiredString("Введіть ім'я або прізвище");
    List<CandidateResponseDto> results = candidateService.searchByName(query);

    System.out.println("\n" + info("Знайдено: " + results.size()));
    displayList(results);
    input.pressEnterToContinue();
  }

  private void showDetails() {
    System.out.println("\n" + header("── Детальна інформація ──"));

    UUID id = input.readUUID("Введіть ID кандидата");

    try {
      CandidateResponseDto c = candidateService.findById(id);

      System.out.println();
      System.out.println(WHITE_BOLD + "👤 " + c.fullName() + RESET);
      printDivider();
      System.out.println(info("ID: ") + c.id());
      System.out.println(info("Паспорт: ") + c.passportNumber());

      if (c.partyCode() != null) {
        try {
          PartyResponseDto party = partyService.findByCode(c.partyCode());
          System.out.println(info("Партія: ") + YELLOW + party.partyCode() + RESET + " - " + party.name());
        } catch (Exception e) {
          System.out.println(info("Партія: ") + c.partyCode());
        }
      } else {
        System.out.println(info("Партія: ") + CYAN + "Незалежний кандидат" + RESET);
      }

      ElectionResponseDto election = electionService.findById(c.electionId());
      System.out.println(info("Вибори: ") + election.name());

      System.out.println(info("Програма: ") + (c.program() != null ? c.program() : "(не вказано)"));
      System.out.println(info("Біографія: ") + (c.biography() != null ? c.biography() : "(не вказано)"));

    } catch (ServiceException e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }

  private void create() {
    System.out.println("\n" + header("── Реєстрація кандидата ──"));

    try {
      List<ElectionResponseDto> pending = electionService.findPending();
      if (pending.isEmpty()) {
        printWarning("Немає виборів у статусі PENDING.");
        input.pressEnterToContinue();
        return;
      }

      System.out.println(info("Оберіть вибори:"));
      for (int i = 0; i < pending.size(); i++) {
        System.out.println(YELLOW + "  [" + (i + 1) + "] " + RESET + pending.get(i).name());
      }

      int electionChoice = input.readIntInRange("Ваш вибір", 1, pending.size());
      ElectionResponseDto election = pending.get(electionChoice - 1);

      List<PartyResponseDto> parties = partyService.findAll();
      System.out.println("\n" + info("Партії (Enter - незалежний):"));
      for (PartyResponseDto p : parties) {
        System.out.println("  " + YELLOW + p.partyCode() + RESET + " - " + p.name());
      }

      String firstName = input.readRequiredString("Ім'я");
      String lastName = input.readRequiredString("Прізвище");
      String passportNumber = input.readRequiredString("Номер паспорта");
      String partyCode = input.readString("Код партії (Enter - незалежний)");
      String program = input.readString("Програма (опціонально)");
      String biography = input.readString("Біографія (опціонально)");

      partyCode = partyCode.isEmpty() ? null : partyCode.toUpperCase();

      CandidateResponseDto candidate = candidateService.create(new CandidateCreateDto(
          firstName, lastName, passportNumber, partyCode, election.id(),
          program.isEmpty() ? null : program, null,
          biography.isEmpty() ? null : biography
      ));

      printSuccess("Кандидата зареєстровано!");
      printInfo("ID: " + candidate.id());

    } catch (ServiceException e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }

  private void delete() {
    System.out.println("\n" + header("── Видалення кандидата ──"));

    try {
      UUID id = input.readUUID("ID кандидата");
      CandidateResponseDto c = candidateService.findById(id);

      printWarning("Видалити: " + c.fullName() + "?");

      if (input.confirm("Підтвердити?")) {
        candidateService.delete(id);
        printSuccess("Кандидата видалено!");
      }
    } catch (ServiceException e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }
}
