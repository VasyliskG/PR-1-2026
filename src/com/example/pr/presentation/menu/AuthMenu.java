package com.example.pr.presentation.menu;

import com.example.pr.domain.dto.auth.AuthResponseDto;
import com.example.pr.domain.dto.auth.LoginDto;
import com.example.pr.domain.dto.auth.RegisterDto;
import com.example.pr.domain.dto.region.RegionResponseDto;
import com.example.pr.domain.dto.voter.VoterResponseDto;
import com.example.pr.domain.service.AuthService;
import com.example.pr.domain.service.RegionService;
import com.example.pr.domain.service.exception.ServiceException;
import com.example.pr.presentation.ConsoleUI;
import com.example.pr.presentation.util.TablePrinter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

import static com.example.pr.presentation.util.ConsoleColors.*;

/**
 * Меню аутентифікації.
 */
public class AuthMenu extends ConsoleUI implements Menu {

  private final AuthService authService;
  private final RegionService regionService;

  public AuthMenu(Scanner scanner, AuthService authService, RegionService regionService) {
    super(scanner);
    this.authService = authService;
    this.regionService = regionService;
  }

  @Override
  public String getTitle() {
    return "Аутентифікація";
  }

  @Override
  public boolean show() {
    TablePrinter.printSection("🔐 " + getTitle());

    printMenuItem(1, "Увійти");
    printMenuItem(2, "Зареєструватися");
    printBackItem();

    int choice = input.readMenuChoice(2);

    switch (choice) {
      case 1 -> login();
      case 2 -> register();
      case 0 -> { return false; }
    }

    return true;
  }

  private void login() {
    System.out.println("\n" + header("── Вхід в систему ──"));

    try {
      String email = input.readEmail("Email");
      String password = input.readPassword("Пароль");

      AuthResponseDto result = authService.login(new LoginDto(email, password));

      printSuccess("Вхід успішний!");
      printInfo("Користувач: " + result.user().fullName());
      printInfo("Роль: " + result.user().role());

    } catch (ServiceException e) {
      printError(e.getMessage());
    }

    input.pressEnterToContinue();
  }

  private void register() {
    System.out.println("\n" + header("── Реєстрація ──"));

    try {
      showRegions();

      String firstName = input.readRequiredString("Ім'я");
      String lastName = input.readRequiredString("Прізвище");
      String email = input.readEmail("Email");
      String password = input.readPassword("Пароль");
      String confirmPassword = input.readPassword("Підтвердіть пароль");
      String passportNumber = input.readRequiredString("Номер паспорта");
      LocalDate birthDate = input.readDate("Дата народження");
      UUID regionId = input.readUUID("ID регіону");

      VoterResponseDto voter = authService.register(new RegisterDto(
          firstName, lastName, email, password, confirmPassword,
          passportNumber, birthDate, regionId
      ));

      printSuccess("Реєстрація успішна!");
      printInfo("Ваш ID: " + voter.id());

    } catch (ServiceException e) {
      printError(e.getMessage());
    } catch (IllegalArgumentException e) {
      printError("Помилка валідації: " + e.getMessage());
    }

    input.pressEnterToContinue();
  }

  private void showRegions() {
    System.out.println("\n" + info("Доступні регіони:"));
    List<RegionResponseDto> regions = regionService.findAll();

    String[] headers = {"ID", "Назва", "Код"};
    List<String[]> rows = new ArrayList<>();
    for (RegionResponseDto region : regions) {
      rows.add(new String[]{
          region.id().toString(),
          region.name(),
          region.code()
      });
    }
    TablePrinter.print(headers, rows);
  }
}
