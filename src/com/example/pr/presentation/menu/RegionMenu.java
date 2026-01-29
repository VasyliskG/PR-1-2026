package com.example.pr.presentation.menu;

import com.example.pr.domain.dto.region.RegionCreateDto;
import com.example.pr.domain.dto.region.RegionResponseDto;
import com.example.pr.domain.service.AuthService;
import com.example.pr.domain.service.RegionService;
import com.example.pr.domain.service.exception.ServiceException;
import com.example.pr.presentation.ConsoleUI;
import com.example.pr.presentation.util.TablePrinter;

import java.util.*;

import static com.example.pr.presentation.util.ConsoleColors.*;

/**
 * Меню регіонів.
 */
public class RegionMenu extends ConsoleUI implements Menu {

  private final RegionService regionService;
  private final AuthService authService;

  public RegionMenu(Scanner scanner, RegionService regionService, AuthService authService) {
    super(scanner);
    this.regionService = regionService;
    this.authService = authService;
  }

  @Override
  public String getTitle() {
    return "Регіони";
  }

  @Override
  public boolean show() {
    TablePrinter.printSection("🗺️ " + getTitle());

    printMenuItem(1, "Всі регіони");
    printMenuItem(2, "Пошук за назвою");
    printMenuItem(3, "Детальна інформація");

    if (authService.isAdmin()) {
      System.out.println();
      System.out.println(PURPLE + "  ── Адміністрування ──" + RESET);
      printMenuItem(4, "Створити регіон");
      printMenuItem(5, "Видалити регіон");
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
    System.out.println("\n" + header("── Всі регіони ──"));
    displayList(regionService.findAll());
    input.pressEnterToContinue();
  }

  private void displayList(List<RegionResponseDto> regions) {
    if (regions.isEmpty()) {
      printInfo("Список порожній.");
      return;
    }

    String[] headers = {"ID", "Назва", "Код", "Виборців", "Опис"};
    List<String[]> rows = new ArrayList<>();

    for (RegionResponseDto r : regions) {
      long voterCount = regionService.getVoterCount(r.id());
      String description = r.description() != null
          ? (r.description().length() > 25 ? r.description().substring(0, 22) + "..." : r.description())
          : "-";
      rows.add(new String[]{
          r.id().toString().substring(0, 8) + "...",
          r.name(), r.code(), String.valueOf(voterCount), description
      });
    }
    TablePrinter.print(headers, rows);
  }

  private void search() {
    System.out.println("\n" + header("── Пошук регіонів ──"));

    String query = input.readRequiredString("Введіть назву або частину назви");
    List<RegionResponseDto> results = regionService.searchByName(query);

    System.out.println("\n" + info("Знайдено: " + results.size()));
    displayList(results);
    input.pressEnterToContinue();
  }

  private void showDetails() {
    System.out.println("\n" + header("── Детальна інформація ──"));

    UUID id = input.readUUID("Введіть ID регіону");

    try {
      RegionResponseDto region = regionService.findById(id);
      long voterCount = regionService.getVoterCount(id);

      System.out.println();
      System.out.println(WHITE_BOLD + "🗺️ " + region.name() + RESET);
      printDivider();
      System.out.println(info("ID: ") + region.id());
      System.out.println(info("Назва: ") + region.name());
      System.out.println(info("Код: ") + region.code());
      System.out.println(info("Виборців: ") + voterCount);
      System.out.println(info("Створено: ") + region.createdAt());
      System.out.println(info("Опис: ") + (region.description() != null ? region.description() : "(не вказано)"));

    } catch (ServiceException e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }

  private void create() {
    System.out.println("\n" + header("── Створення регіону ──"));

    try {
      String name = input.readRequiredString("Назва регіону");
      String code = input.readRequiredString("Код регіону (2-5 літер)").toUpperCase();
      String description = input.readString("Опис (опціонально)");

      RegionResponseDto region = regionService.create(new RegionCreateDto(
          name, code, description.isEmpty() ? null : description
      ));

      printSuccess("Регіон створено!");
      printInfo("ID: " + region.id());

    } catch (ServiceException e) {
      printError(e.getMessage());
    } catch (IllegalArgumentException e) {
      printError("Помилка валідації: " + e.getMessage());
    }

    input.pressEnterToContinue();
  }

  private void delete() {
    System.out.println("\n" + header("── Видалення регіону ──"));

    try {
      UUID id = input.readUUID("ID регіону для видалення");

      RegionResponseDto region = regionService.findById(id);
      long voterCount = regionService.getVoterCount(id);

      printWarning("Ви збираєтесь видалити: " + region.name());

      if (voterCount > 0) {
        printError("Неможливо видалити регіон з " + voterCount + " виборцями!");
        input.pressEnterToContinue();
        return;
      }

      if (input.confirm("Підтвердити видалення?")) {
        regionService.delete(id);
        printSuccess("Регіон видалено!");
      } else {
        printInfo("Видалення скасовано.");
      }

    } catch (ServiceException e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }
}
