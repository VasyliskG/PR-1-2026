package com.example.pr.app;

import com.example.pr.domain.dto.auth.AuthResponseDto;
import com.example.pr.domain.dto.auth.LoginDto;
import com.example.pr.domain.dto.auth.RegisterDto;
import com.example.pr.domain.dto.candidate.CandidateCreateDto;
import com.example.pr.domain.dto.candidate.CandidateResponseDto;
import com.example.pr.domain.dto.election.ElectionCreateDto;
import com.example.pr.domain.dto.election.ElectionResponseDto;
import com.example.pr.domain.dto.party.PartyCreateDto;
import com.example.pr.domain.dto.party.PartyResponseDto;
import com.example.pr.domain.dto.region.RegionCreateDto;
import com.example.pr.domain.dto.region.RegionResponseDto;
import com.example.pr.domain.dto.vote.VoteCreateDto;
import com.example.pr.domain.dto.vote.VoteResponseDto;
import com.example.pr.domain.dto.vote.VoteResultDto;
import com.example.pr.domain.dto.voter.VoterResponseDto;
import com.example.pr.domain.service.*;
import com.example.pr.domain.service.exception.ServiceException;
import com.example.pr.domain.service.exception.VotingException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    System.out.println("╔══════════════════════════════════════════════════════════════╗");
    System.out.println("║       СИСТЕМА ЕЛЕКТРОННОГО ГОЛОСУВАННЯ - BUSINESS LAYER       ║");
    System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

    // Ініціалізація сервісів
    RegionService regionService = new RegionService();
    PartyService partyService = new PartyService();
    VoterService voterService = new VoterService();
    ElectionService electionService = new ElectionService();
    CandidateService candidateService = new CandidateService();
    VoteService voteService = new VoteService();
    AuthService authService = new AuthService();

    try {
      // ==================== РЕГІОНИ ====================
      System.out.println("═══════════════════ РЕГІОНИ ═══════════════════\n");

      RegionResponseDto kyiv = regionService.create(
          new RegionCreateDto("Київська область", "KYIV", "Столичний регіон")
      );
      RegionResponseDto lviv = regionService.create(
          new RegionCreateDto("Львівська область", "LVIV", "Західний регіон")
      );
      RegionResponseDto odesa = regionService.create(
          new RegionCreateDto("Одеська область", "ODES", "Південний регіон")
      );

      System.out.println("✓ Створено регіони:");
      regionService.findAll().forEach(r ->
          System.out.println("  - " + r.name() + " (" + r.code() + ")"));

      // ==================== ПАРТІЇ ====================
      System.out.println("\n═══════════════════ ПАРТІЇ ═══════════════════\n");

      PartyResponseDto party1 = partyService.create(
          new PartyCreateDto("sdavw","Демократична партія", "ДП", null,
              "Програма: розвиток демократії та громадянського суспільства")
      );
      PartyResponseDto party2 = partyService.create(
          new PartyCreateDto("asdssd","Прогресивна партія", "ПП", null,
              "Програма: технологічний розвиток та інновації")
      );
      PartyResponseDto party3 = partyService.create(
          new PartyCreateDto("asd","Народна партія", "НП", null,
              "Програма: соціальний захист та добробут громадян")
      );

      System.out.println("✓ Створено партії:");
      partyService.findAll().forEach(p ->
          System.out.println("  - " + p.name() + " (" + p.abbreviation() + ")"));

      // ==================== РЕЄСТРАЦІЯ ВИБОРЦІВ ====================
      System.out.println("\n═══════════════════ РЕЄСТРАЦІЯ ВИБОРЦІВ ═══════════════════\n");

      // Реєстрація через AuthService
      VoterResponseDto voter1 = authService.register(new RegisterDto(
          "Олексій", "Коваленко",
          "alex@example.com", "password123", "password123",
          "AA123456", LocalDate.of(1990, 5, 15), kyiv.id()
      ));
      System.out.println("✓ Зареєстровано: " + voter1.fullName() + " (" + voter1.email() + ")");

      VoterResponseDto voter2 = authService.register(new RegisterDto(
          "Марія", "Шевченко",
          "maria@example.com", "password456", "password456",
          "BB654321", LocalDate.of(1985, 8, 20), lviv.id()
      ));
      System.out.println("✓ Зареєстровано: " + voter2.fullName() + " (" + voter2.email() + ")");

      VoterResponseDto voter3 = authService.register(new RegisterDto(
          "Петро", "Іваненко",
          "petro@example.com", "password789", "password789",
          "CC111222", LocalDate.of(1978, 3, 10), odesa.id()
      ));
      System.out.println("✓ Зареєстровано: " + voter3.fullName() + " (" + voter3.email() + ")");

      // ==================== АУТЕНТИФІКАЦІЯ ====================
      System.out.println("\n═══════════════════ АУТЕНТИФІКАЦІЯ ═══════════════════\n");

      // Спроба входу
      AuthResponseDto loginResult = authService.login(new LoginDto("alex@example.com", "password123"));
      System.out.println("✓ Успішний вхід: " + loginResult.message());
      System.out.println("  Користувач: " + loginResult.user().fullName());
      System.out.println("  Роль: " + loginResult.user().role());

      // Перевірка авторизації
      System.out.println("  Авторизований: " + authService.isAuthenticated());
      System.out.println("  Є адміном: " + authService.isAdmin());

      // Вихід
      authService.logout();
      System.out.println("✓ Вихід із системи");

      // ==================== ВИБОРИ ====================
      System.out.println("\n═══════════════════ ВИБОРИ ═══════════════════\n");

      ElectionResponseDto election = electionService.create(new ElectionCreateDto(
          "Президентські вибори 2026",
          "Вибори Президента України на 2026 рік",
          LocalDateTime.of(2025, 3, 31, 8, 0),
          LocalDateTime.of(2026, 3, 31, 20, 0)
      ));
      System.out.println("✓ Створено вибори: " + election.name());
      System.out.println("  Статус: " + election.status());
      System.out.println("  Період: " + election.startDate() + " - " + election.endDate());

      // ==================== КАНДИДАТИ ====================
      System.out.println("\n═══════════════════ КАНДИДАТИ ═══════════════════\n");

      CandidateResponseDto candidate1 = candidateService.create(new CandidateCreateDto(
          "Іван", "Петренко", "DD111111",
          party1.partyCode(), election.id(),
          "Моя програма: економічний розвиток, боротьба з корупцією",
          null, "Досвідчений державний діяч з 20-річним стажем"
      ));
      System.out.println("✓ Зареєстровано кандидата: " + candidate1.fullName() + " (" + party1.abbreviation() + ")");

      CandidateResponseDto candidate2 = candidateService.create(new CandidateCreateDto(
          "Олена", "Мель��ик", "EE222222",
          party2.partyCode(), election.id(),
          "Моя програма: цифровізація, освіта, наука",
          null, "IT-підприємець, громадська активістка"
      ));
      System.out.println("✓ Зареєстровано кандидата: " + candidate2.fullName() + " (" + party2.abbreviation() + ")");

      CandidateResponseDto candidate3 = candidateService.create(new CandidateCreateDto(
          "Василь", "Сидоренко", "FF333333",
          party3.partyCode(), election.id(),
          "Моя програма: соціальний захист, підвищення пенсій",
          null, "Колишній міністр соціальної політики"
      ));
      System.out.println("✓ Зареєстровано кандидата: " + candidate3.fullName() + " (" + party3.abbreviation() + ")");

      // Незалежний кандидат
      CandidateResponseDto candidate4 = candidateService.create(new CandidateCreateDto(
          "Анна", "Павленко", "GG444444",
          null, election.id(),  // без партії
          "Моя програма: незалежна політика, народовладдя",
          null, "Журналістка, правозахисниця"
      ));
      System.out.println("✓ Зареєстровано незалежного кандидата: " + candidate4.fullName());

      System.out.println("\nКандидати на виборах:");
      candidateService.findByElection(election.id()).forEach(c ->
          System.out.println("  - " + c.fullName()));

      // ==================== АКТИВАЦІЯ ВИБОРІВ ====================
      System.out.println("\n═══════════════════ АКТИВАЦІЯ ВИБОРІВ ═══════════════════\n");

      ElectionResponseDto activatedElection = electionService.activate(election.id());
      System.out.println("✓ Вибори активовано!");
      System.out.println("  Новий статус: " + activatedElection.status());

      // ==================== ГОЛОСУВАННЯ ====================
      System.out.println("\n═══════════════════ ГОЛОСУВАННЯ ═══════════════════\n");

      // Голосування виборців
      VoteResponseDto vote1 = voteService.vote(new VoteCreateDto(
          voter1.id(), candidate1.id(), election.id()
      ));
      System.out.println("✓ " + voter1.fullName() + " проголосував за " + candidate1.fullName());

      VoteResponseDto vote2 = voteService.vote(new VoteCreateDto(
          voter2.id(), candidate2.id(), election.id()
      ));
      System.out.println("✓ " + voter2.fullName() + " проголосував за " + candidate2.fullName());

      VoteResponseDto vote3 = voteService.vote(new VoteCreateDto(
          voter3.id(), candidate1.id(), election.id()
      ));
      System.out.println("✓ " + voter3.fullName() + " проголосував за " + candidate1.fullName());

      // Спроба повторного голосування
      System.out.println("\n--- Спроба повторного голосування ---");
      try {
        voteService.vote(new VoteCreateDto(voter1.id(), candidate2.id(), election.id()));
      } catch (VotingException e) {
        System.out.println("✗ Помилка: " + e.getMessage());
      }

      // ==================== РЕЗУЛЬТАТИ ====================
      System.out.println("\n═══════════════════ РЕЗУЛЬТАТИ ГОЛОСУВАННЯ ═══════════════════\n");

      System.out.println("Всього голосів: " + voteService.countByElection(election.id()));
      System.out.println();

      List<VoteResultDto> results = voteService.getResults(election.id());
      System.out.println("┌─────────────────────────────┬────────────┬───────────┬────────────┐");
      System.out.println("│ Кандидат                    │ Партія     │ Голоси    │ Відсоток   │");
      System.out.println("├─────────────────────────────┼────────────┼───────────┼────────────┤");
      for (VoteResultDto result : results) {
        System.out.printf("│ %-27s │ %-10s │ %9d │ %8.1f%% │\n",
            result.candidateFullName(),
            result.partyName().length() > 10
                ? result.partyName().substring(0, 10)
                : result.partyName(),
            result.voteCount(),
            result.percentage()
        );
      }
      System.out.println("└─────────────────────────────┴────────────┴───────────┴────────────┘");

      // ==================== ЗАВЕРШЕННЯ ВИБОРІВ ====================
      System.out.println("\n═══════════════════ ЗАВЕРШЕННЯ ВИБОРІВ ═══════════════════\n");

      ElectionResponseDto closedElection = electionService.close(election.id());
      System.out.println("✓ Вибори завершено!");
      System.out.println("  Статус: " + closedElection.status());

      // Спроба голосування після завершення
      System.out.println("\n--- Спроба голосування після завершення ---");
      try {
        // Реєструємо нового виборця для тесту
        VoterResponseDto newVoter = authService.register(new RegisterDto(
            "Тест", "Тестович",
            "test@example.com", "testpass", "testpass",
            "ZZ999999", LocalDate.of(1995, 1, 1), kyiv.id()
        ));
        voteService.vote(new VoteCreateDto(newVoter.id(), candidate1.id(), election.id()));
      } catch (VotingException e) {
        System.out.println("✗ Помилка: " + e.getMessage());
      }

      // ==================== СТАТИСТИКА ====================
      System.out.println("\n═══════════════════ ЗАГАЛЬНА СТАТИСТИКА ═══════════════════\n");

      System.out.println("┌────────────────────────────────┬───────────────┐");
      System.out.println("│ Показник                       │ Значення      │");
      System.out.println("├────────────────────────────────┼───────────────┤");
      System.out.printf("│ Регіонів                       │ %13d │\n", regionService.count());
      System.out.printf("│ Партій                         │ %13d │\n", partyService.count());
      System.out.printf("│ Виборців                       │ %13d │\n", voterService.count());
      System.out.printf("│ Кандидатів на виборах          │ %13d │\n", candidateService.countByElection(election.id()));
      System.out.printf("│ Голосів                        │ %13d │\n", voteService.countByElection(election.id()));
      System.out.println("└────────────────────────────────┴───────────────┘");

      // Статистика по регіонах
      System.out.println("\nВиборці по регіонах:");
      for (RegionResponseDto region : regionService.findAll()) {
        long voterCount = regionService.getVoterCount(region.id());
        System.out.printf("  %s: %d виборців\n", region.name(), voterCount);
      }

      System.out.println("\n✓ Демонстрація бізнес-логіки завершена успішно!");

    } catch (ServiceException e) {
      System.err.println("❌ Помилка сервісу: " + e.getMessage());
      e.printStackTrace();
    } catch (Exception e) {
      System.err.println("❌ Несподівана помилка: " + e.getMessage());
      e.printStackTrace();
    }
  }
}
