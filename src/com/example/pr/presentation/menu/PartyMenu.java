package com.example.pr.presentation.menu;

import com.example.pr.domain.dto.party.PartyCreateDto;
import com.example.pr.domain.dto.party.PartyResponseDto;
import com.example.pr.domain.service.AuthService;
import com.example.pr.domain.service.PartyService;
import com.example.pr.domain.service.exception.ServiceException;
import com.example.pr.presentation.ConsoleUI;
import com.example.pr.presentation.util.TablePrinter;

import java.util.*;

import static com.example.pr.presentation.util.ConsoleColors.*;

/**
 * Меню партій.
 */
public class PartyMenu extends ConsoleUI implements Menu {

  private final PartyService partyService;
  private final AuthService authService;

  public PartyMenu(Scanner scanner, PartyService partyService, AuthService authService) {
    super(scanner);
    this.partyService = partyService;
    this.authService = authService;
  }

  @Override
  public String getTitle() {
    return "Партії";
  }

  @Override
  public boolean show() {
    TablePrinter.printSection("🏛️ " + getTitle());

    printMenuItem(1, "Всі партії");
    printMenuItem(2, "Пошук за назвою");
    printMenuItem(3, "Детальна інформація");

    if (authService.isAdmin()) {
      System.out.println();
      System.out.println(PURPLE + "  ── Адміністрування ──" + RESET);
      printMenuItem(4, "Створити партію");
      printMenuItem(5, "Видалити партію");
    }

    printBackItem();

    int maxChoice = authService.isAdmin() ? 5 : 3;
    int choice = input.readMenuChoice(maxChoice);

    switch (choice) {
      case 1 -> showAll();
      case 2 -> search();
      case 3 -> showDetails();
      case 4 -> { if (authService.isAdmin()) create(); }
      case 5 -> { if (authService.isAdmin()) delete(); }
      case 0 -> { return false; }
    }

    return true;
  }

  public void showAll() {
    System.out.println("\n" + header("── Всі партії ──"));
    displayList(partyService.findAll());
    input.pressEnterToContinue();
  }

  private void displayList(List<PartyResponseDto> parties) {
    if (parties.isEmpty()) {
      printInfo("Список порожній.");
      return;
    }

    String[] headers = {"Код", "Назва", "Абревіатура", "Кандидатів", "Програма"};
    List<String[]> rows = new ArrayList<>();

    for (PartyResponseDto p : parties) {
      long count = partyService.getCandidateCount(p.partyCode());
      String program = p.program() != null
          ? (p.program().length() > 25 ? p.program().substring(0, 22) + "..." : p.program())
          : "-";
      rows.add(new String[]{
          p.partyCode(), p.name(),
          p.abbreviation() != null ? p.abbreviation() : "-",
          String.valueOf(count), program
      });
    }
    TablePrinter.print(headers, rows);
  }

  private void search() {
    String query = input.readRequiredString("Введіть назву");
    displayList(partyService.searchByName(query));
    input.pressEnterToContinue();
  }

  private void showDetails() {
    String code = input.readRequiredString("Введіть код партії");

    try {
      PartyResponseDto p = partyService.findByCode(code.toUpperCase());

      System.out.println();
      System.out.println(WHITE_BOLD + "🏛️ " + p.name() + RESET);
      printDivider();
      System.out.println(info("Код: ") + p.partyCode());
      System.out.println(info("Абревіатура: ") + (p.abbreviation() != null ? p.abbreviation() : "-"));
      System.out.println(info("Кандидатів: ") + partyService.getCandidateCount(p.partyCode()));
      System.out.println(info("Програма: ") + (p.program() != null ? p.program() : "(не вказано)"));

    } catch (ServiceException e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }

  private void create() {
    System.out.println("\n" + header("── Створення партії ──"));

    try {
      String partyCode = input.readRequiredString("Код партії (унікальний)").toUpperCase();
      String name = input.readRequiredString("Назва");
      String abbreviation = input.readString("Абревіатура (опціонально)");
      String program = input.readString("Програма (опціонально)");

      PartyResponseDto party = partyService.create(new PartyCreateDto(
          partyCode, name,
          abbreviation.isEmpty() ? null : abbreviation.toUpperCase(),
          null, program.isEmpty() ? null : program
      ));

      printSuccess("Партію створено!");
      printInfo("Код: " + party.partyCode());

    } catch (ServiceException e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }

  private void delete() {
    System.out.println("\n" + header("── Видалення партії ──"));

    try {
      String code = input.readRequiredString("Код партії").toUpperCase();
      PartyResponseDto p = partyService.findByCode(code);

      printWarning("Видалити: " + p.name() + "?");

      if (input.confirm("Підтвердити?")) {
        partyService.delete(code);
        printSuccess("Партію видалено!");
      }
    } catch (ServiceException e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }
}
