package com.example.pr.presentation.menu;

import com.example.pr.domain.dto.voter.VoterResponseDto;
import com.example.pr.domain.enums.VoterRole;
import com.example.pr.domain.service.*;
import com.example.pr.presentation.ConsoleUI;

import java.util.Optional;
import java.util.Scanner;

import static com.example.pr.presentation.util.ConsoleColors.*;

/**
 * Головне меню застосунку.
 */
public class MainMenu extends ConsoleUI implements Menu {

  private final AuthService authService;
  private final VoterService voterService;
  private final ElectionService electionService;
  private final CandidateService candidateService;
  private final VoteService voteService;
  private final PartyService partyService;
  private final RegionService regionService;

  // Підменю
  private final AuthMenu authMenu;
  private final ElectionMenu electionMenu;
  private final VoteMenu voteMenu;
  private final CandidateMenu candidateMenu;
  private final PartyMenu partyMenu;
  private final RegionMenu regionMenu;
  private final AdminMenu adminMenu;

  public MainMenu(Scanner scanner) {
    super(scanner);

    // Ініціалізація сервісів
    this.authService = new AuthService();
    this.voterService = new VoterService();
    this.electionService = new ElectionService();
    this.candidateService = new CandidateService();
    this.voteService = new VoteService();
    this.partyService = new PartyService();
    this.regionService = new RegionService();

    // Ініціалізація підменю
    this.authMenu = new AuthMenu(scanner, authService, regionService);
    this.electionMenu = new ElectionMenu(scanner, electionService, candidateService, authService);
    this.voteMenu = new VoteMenu(scanner, voteService, electionService, candidateService, authService);
    this.candidateMenu = new CandidateMenu(scanner, candidateService, electionService, partyService, authService);
    this.partyMenu = new PartyMenu(scanner, partyService, authService);
    this.regionMenu = new RegionMenu(scanner, regionService, authService);
    this.adminMenu = new AdminMenu(scanner, voterService, electionService, authService);
  }

  @Override
  public String getTitle() {
    return "Головне меню";
  }

  @Override
  public boolean show() {
    printHeader();
    printUserStatus();
    printDivider();

    if (authService.isAuthenticated()) {
      showAuthenticatedMenu();
    } else {
      showGuestMenu();
    }

    return true;
  }

  private void printUserStatus() {
    Optional<VoterResponseDto> user = authService.getCurrentUser();
    if (user.isPresent()) {
      VoterResponseDto voter = user.get();
      System.out.println(GREEN + "👤 Користувач: " + WHITE_BOLD + voter.fullName() +
          GREEN + " | Роль: " + WHITE_BOLD + voter.role() + RESET);
    } else {
      System.out.println(YELLOW + "👤 Ви не авторизовані" + RESET);
    }
  }

  private void showGuestMenu() {
    System.out.println("\n" + header("── Меню гостя ──\n"));

    printMenuItem(1, "🔐 Увійти / Зареєструватися");
    printMenuItem(2, "📋 Переглянути активні вибори");
    printMenuItem(3, "👥 Переглянути кандидатів");
    printMenuItem(4, "🏛️ Переглянути партії");
    printMenuItem(5, "🗺️ Переглянути регіони");
    printExitItem();

    int choice = input.readMenuChoice(5);

    switch (choice) {
      case 1 -> runSubMenu(authMenu);
      case 2 -> electionMenu.showActive();
      case 3 -> candidateMenu.showAll();
      case 4 -> partyMenu.showAll();
      case 5 -> regionMenu.showAll();
      case 0 -> exitApplication();
    }
  }

  private void showAuthenticatedMenu() {
    VoterResponseDto user = authService.getCurrentUser().orElseThrow();
    boolean isAdmin = user.role() == VoterRole.ADMIN;

    System.out.println("\n" + header("── Головне меню ──\n"));

    printMenuItem(1, "🗳️ Голосування");
    printMenuItem(2, "📋 Вибори");
    printMenuItem(3, "👥 Кандидати");
    printMenuItem(4, "🏛️ Партії");
    printMenuItem(5, "🗺️ Регіони");
    printMenuItem(6, "📊 Результати виборів");
    printMenuItem(7, "👤 Мій профіль");

    if (isAdmin) {
      System.out.println();
      System.out.println(PURPLE_BOLD + "  ── Адміністрування ──" + RESET);
      printMenuItem(8, "⚙️ Адмін-панель");
    }

    System.out.println();
    printMenuItem(9, "🚪 Вийти з акаунту");
    printExitItem();

    int maxChoice = 9;
    int choice = input.readMenuChoice(maxChoice);

    switch (choice) {
      case 1 -> runSubMenu(voteMenu);
      case 2 -> runSubMenu(electionMenu);
      case 3 -> runSubMenu(candidateMenu);
      case 4 -> runSubMenu(partyMenu);
      case 5 -> runSubMenu(regionMenu);
      case 6 -> showResults();
      case 7 -> showProfile();
      case 8 -> { if (isAdmin) runSubMenu(adminMenu); }
      case 9 -> logout();
      case 0 -> exitApplication();
    }
  }

  private void runSubMenu(Menu menu) {
    while (menu.show()) {
      // Продовжуємо поки меню повертає true
    }
  }

  private void showResults() {
    voteMenu.showResults();
  }

  private void showProfile() {
    VoterResponseDto user = authService.getCurrentUser().orElseThrow();

    System.out.println("\n" + header("── Мій профіль ──"));
    System.out.println();
    System.out.println(WHITE_BOLD + "👤 " + user.fullName() + RESET);
    printDivider();
    System.out.println(info("ID: ") + user.id());
    System.out.println(info("Email: ") + user.email());
    System.out.println(info("Паспорт: ") + user.passportNumber());
    System.out.println(info("Дата народження: ") + user.birthDate());
    System.out.println(info("Роль: ") + user.role());
    System.out.println(info("Регіон ID: ") + user.regionId());
    System.out.println(info("Зареєстрований: ") + user.createdAt());

    System.out.println();
    printMenuItem(1, "Змінити пароль");
    printBackItem();

    int choice = input.readMenuChoice(1);
    if (choice == 1) {
      changePassword();
    }
  }

  private void changePassword() {
    System.out.println("\n" + header("── Зміна пароля ──"));

    try {
      String oldPassword = input.readPassword("Поточний пароль");
      String newPassword = input.readPassword("Новий пароль");
      String confirmPassword = input.readPassword("Підтвердіть новий пароль");

      if (!newPassword.equals(confirmPassword)) {
        printError("Паролі не співпадають!");
        input.pressEnterToContinue();
        return;
      }

      authService.changePassword(oldPassword, newPassword);
      printSuccess("Пароль успішно змінено!");

    } catch (Exception e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }

  private void logout() {
    authService.logout();
    printSuccess("Ви вийшли з системи.");
    input.pressEnterToContinue();
  }

  private void exitApplication() {
    System.out.println();
    printInfo("Дякуємо за використання системи!");
    printInfo("До побачення! 👋");
    System.exit(0);
  }
}
