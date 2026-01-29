package com.example.pr.presentation;

import com.example.pr.domain.dto.party.PartyCreateDto;
import com.example.pr.domain.dto.region.RegionCreateDto;
import com.example.pr.domain.enums.VoterRole;
import com.example.pr.domain.impl.Voter;
import com.example.pr.domain.service.PartyService;
import com.example.pr.domain.service.PasswordHasher;
import com.example.pr.domain.service.RegionService;
import com.example.pr.infrastructure.storage.impl.DataContext;
import com.example.pr.presentation.menu.MainMenu;

import java.time.LocalDate;
import java.util.Scanner;

import static com.example.pr.presentation.util.ConsoleColors.*;

/**
 * Головний клас застосунку.
 */
public class Application {

  private final Scanner scanner;
  private final MainMenu mainMenu;

  public Application() {
    this.scanner = new Scanner(System.in);
    this.mainMenu = new MainMenu(scanner);
  }

  /**
   * Запускає застосунок.
   */
  public void run() {
    printWelcome();
    initializeSampleData();

    try {
      while (true) {
        mainMenu.show();
      }
    } catch (Exception e) {
      System.err.println(RED + "Критична помилка: " + e.getMessage() + RESET);
      e.printStackTrace();
    } finally {
      scanner.close();
    }
  }

  private void printWelcome() {
    System.out.println();
    System.out.println(CYAN_BOLD + "╔══════════════════════════════════════════════════════════════════╗" + RESET);
    System.out.println(CYAN_BOLD + "║                                                                  ║" + RESET);
    System.out.println(CYAN_BOLD + "║" + WHITE_BOLD + "          🗳️  СИСТЕМА ЕЛЕКТРОННОГО ГОЛОСУВАННЯ  🗳️              " + CYAN_BOLD + "║" + RESET);
    System.out.println(CYAN_BOLD + "║" + CYAN + "                      Версія 1.0.0                               " + CYAN_BOLD + "║" + RESET);
    System.out.println(CYAN_BOLD + "║                                                                  ║" + RESET);
    System.out.println(CYAN_BOLD + "╚══════════════════════════════════════════════════════════════════╝" + RESET);
    System.out.println();
  }

  /**
   * Ініціалізує тестові дані, якщо база порожня.
   */
  private void initializeSampleData() {
    DataContext context = DataContext.getInstance();

    // Перевіряємо чи є дані
    if (context.regions().count(r -> true) > 0) {
      System.out.println(GREEN + "✓ Дані завантажено з файлів" + RESET);
      return;
    }

    System.out.println(YELLOW + "⚙ Ініціалізація тестових даних..." + RESET);

    try {
      RegionService regionService = new RegionService();
      PartyService partyService = new PartyService();

      // Регіони
      var kyiv = regionService.create(new RegionCreateDto("Київська область", "KYIV", "Столичний регіон"));
      regionService.create(new RegionCreateDto("Львівська область", "LVIV", "Західний регіон"));
      regionService.create(new RegionCreateDto("Одеська область", "ODES", "Південний регіон"));
      regionService.create(new RegionCreateDto("Харківська область", "KHRK", "Східний регіон"));
      regionService.create(new RegionCreateDto("Дніпропетровська область", "DNPR", "Центральний регіон"));

      // Партії
      partyService.create(new PartyCreateDto("DP", "Демократична партія", "ДП", null, "Програма розвитку демократії"));
      partyService.create(new PartyCreateDto("PP", "Прогресивна партія", "ПП", null, "Програма технологічного розвитку"));
      partyService.create(new PartyCreateDto("NP", "Народна партія", "НП", null, "Програма соціального захисту"));

      // Адміністратор за замовчуванням
      Voter admin = new Voter(
          "Адмін",
          "Системний",
          "admin@voting.ua",
          PasswordHasher.hash("admin123"),
          "AA000000",
          LocalDate.of(1980, 1, 1),
          VoterRole.ADMIN,
          kyiv.id()
      );
      context.voters().save(admin);

      System.out.println(GREEN + "✓ Тестові дані створено" + RESET);
      System.out.println();
      System.out.println(CYAN_BOLD + "╔══════════════════════════════════════════════╗" + RESET);
      System.out.println(CYAN_BOLD + "║" + WHITE_BOLD + "  Тестовий акаунт адміністратора:            " + CYAN_BOLD + "║" + RESET);
      System.out.println(CYAN_BOLD + "║" + YELLOW + "  Email: " + WHITE + "admin@voting.ua                    " + CYAN_BOLD + "║" + RESET);
      System.out.println(CYAN_BOLD + "║" + YELLOW + "  Пароль: " + WHITE + "admin123                          " + CYAN_BOLD + "║" + RESET);
      System.out.println(CYAN_BOLD + "╚══════════════════════════════════════════════╝" + RESET);

    } catch (Exception e) {
      System.out.println(RED + "✗ Помилка ініціалізації: " + e.getMessage() + RESET);
    }

    System.out.println();
  }

  /**
   * Точка входу в програму.
   */
  public static void main(String[] args) {
    Application app = new Application();
    app.run();
  }
}
