package com.example.pr.app;

import com.example.pr.domain.enums.ElectionStatus;
import com.example.pr.domain.enums.VoterRole;
import com.example.pr.domain.impl.*;
import com.example.pr.domain.specification.*;
import com.example.pr.infrastructure.storage.impl.DataContext;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class Main {

  public static void main(String[] args) {
    DataContext context = DataContext.getInstance();

    System.out.println("╔══════════════════════════════════════════════════════════╗");
    System.out.println("║     СИСТЕМА ЕЛЕКТРОННОГО ГОЛОСУВАННЯ - ДЕМОНСТРАЦІЯ      ║");
    System.out.println("╚══════════════════════════════════════════════════════════╝\n");

    // ==================== CREATE ====================
    System.out.println("═══════════════════ CREATE ═══════════════════\n");

    // Регіони
    Region kyiv = new Region("Київська область", "KYIV", "Столичний регіон");
    Region lviv = new Region("Львівська область", "LVIV", "Західний регіон");
    context.regions().save(kyiv);
    context.regions().save(lviv);
    System.out.println("✓ Створено регіони: " + kyiv.getName() + ", " + lviv.getName());

    // Партії
    Party party1 = new Party("Демократична партія", "ДП", null, "Програма демократичної партії");
    Party party2 = new Party("Прогресивна партія", "ПП", null, "Програма прогресивної партії");
    context.parties().save(party1);
    context.parties().save(party2);
    System.out.println(
        "✓ Створено партії: " + party1.getAbbreviation() + ", " + party2.getAbbreviation());

    // Вибори
    Election election = new Election(
        "Президентські вибори 2026",
        "Вибори Президента України",
        LocalDateTime.of(2026, 3, 31, 8, 0),
        LocalDateTime.of(2026, 3, 31, 20, 0),
        ElectionStatus.PENDING
    );
    context.elections().save(election);
    System.out.println("✓ Створено вибори: " + election.getName());

    // Кандидати
    Candidate candidate1 = new Candidate(
        "Іван", "Петренко", "AA111111",
        party1.getId(), election.getId(),
        "Моя програма: розвиток економіки", null, "Досвідчений політик"
    );
    Candidate candidate2 = new Candidate(
        "Марія", "Шевченко", "BB222222",
        party2.getId(), election.getId(),
        "Моя програма: соціальні реформи", null, "Громадська активістка"
    );
    context.candidates().save(candidate1);
    context.candidates().save(candidate2);
    System.out.println(
        "✓ Створено кандидатів: " + candidate1.getFullName() + ", " + candidate2.getFullName());

    // Виборці
    Voter voter1 = new Voter("Олексій", "Коваленко", "alex@example.com", "hash1",
        "CC333333", LocalDate.of(1990, 5, 15), VoterRole.VOTER, kyiv.getId());
    Voter voter2 = new Voter("Наталія", "Мельник", "natalia@example.com", "hash2",
        "DD444444", LocalDate.of(1985, 8, 20), VoterRole.VOTER, kyiv.getId());
    Voter voter3 = new Voter("Петро", "Іваненко", "petro@example.com", "hash3",
        "EE555555", LocalDate.of(1975, 3, 10), VoterRole.ADMIN, lviv.getId());
    context.voters().save(voter1);
    context.voters().save(voter2);
    context.voters().save(voter3);
    System.out.println("✓ Створено виборців: " + voter1.getFirstName() + ", " +
        voter2.getFirstName() + ", " + voter3.getFirstName());

    // ==================== READ ====================
    System.out.println("\n═══════════════════ READ ═══════════════════\n");

    // Пошук за ID
    Optional<Voter> foundById = context.voters().findById(voter1.getId());
    System.out.println("Пошук за ID: " + foundById.map(Voter::getEmail).orElse("не знайдено"));

    // Пошук за email
    Optional<Voter> foundByEmail = context.voters().findByEmail("alex@example.com");
    System.out.println(
        "Пошук за email: " + foundByEmail.map(Voter::getFirstName).orElse("не знайдено"));

    // Пошук за паспортом
    Optional<Voter> foundByPassport = context.voters().findByPassportNumber("CC333333");
    System.out.println(
        "Пошук за паспортом: " + foundByPassport.map(Voter::getLastName).orElse("не знайдено"));

    // Пошук виборців за регіоном
    List<Voter> kyivVoters = context.voters().findAll(VoterSpecifications.byRegionId(kyiv.getId()));
    System.out.println("Виборці Київської області: " + kyivVoters.size());

    // Пошук адміністраторів
    List<Voter> admins = context.voters().findAll(VoterSpecifications.isAdmin());
    System.out.println("Адміністратори: " + admins.stream().map(Voter::getFirstName).toList());

    // Пошук виборців старших 40 років
    List<Voter> olderVoters = context.voters().findAll(VoterSpecifications.olderThan(40));
    System.out.println(
        "Вибор��і старші 40 років: " + olderVoters.stream().map(Voter::getFirstName).toList());

    // Пошук кандидатів на виборах
    List<Candidate> candidates = context.candidates().findByElectionId(election.getId());
    System.out.println(
        "Кандидати на виборах: " + candidates.stream().map(Candidate::getFullName).toList());

    // Пошук кандидатів партії
    List<Candidate> partyCandidates = context.candidates().findByPartyId(party1.getId());
    System.out.println("Кандидати " + party1.getAbbreviation() + ": " +
        partyCandidates.stream().map(Candidate::getLastName).toList());

    // Пошук активних виборів
    List<Election> activeElections = context.elections().findAll(ElectionSpecifications.active());
    System.out.println("Активних виборів: " + activeElections.size());

    // Комбінований пошук: адміни з Львова
    List<Voter> lvivAdmins = context.voters().findAll(
        VoterSpecifications.isAdmin().and(VoterSpecifications.byRegionId(lviv.getId()))
    );
    System.out.println("Адміни з Львова: " + lvivAdmins.stream().map(Voter::getFirstName).toList());

    // ==================== UPDATE ====================
    System.out.println("\n═══════════════════ UPDATE ═══════════════════\n");

    // Оновлення email виборця
    String oldEmail = voter1.getEmail();
    voter1.setEmail("alex.new@example.com");
    context.voters().save(voter1);
    System.out.println("Оновлено email: " + oldEmail + " → " + voter1.getEmail());

    // Активація виборів
    election.setStatus(ElectionStatus.ACTIVE);
    context.elections().save(election);
    System.out.println("Оновлено статус виборів: " + election.getStatus());

    // Оновлення програми кандидата
    candidate1.setProgram("Оновлена програма: економічний розвиток та інновації");
    context.candidates().save(candidate1);
    System.out.println("Оновлено програму кандидата: " + candidate1.getLastName());

    // ==================== ГОЛОСУВАННЯ ====================
    System.out.println("\n═══════════════════ ГОЛОСУВАННЯ ═══════════════════\n");

    // Голосування voter1 за candidate1
    if (!context.votes().hasVoted(voter1.getId(), election.getId())) {
      Vote vote1 = new Vote(voter1.getId(), candidate1.getId(), election.getId(),
          LocalDateTime.now());
      context.votes().save(vote1);
      System.out.println(
          "✓ " + voter1.getFirstName() + " проголосував за " + candidate1.getLastName());
    }

    // Голосування voter2 за candidate2
    if (!context.votes().hasVoted(voter2.getId(), election.getId())) {
      Vote vote2 = new Vote(voter2.getId(), candidate2.getId(), election.getId(),
          LocalDateTime.now());
      context.votes().save(vote2);
      System.out.println(
          "✓ " + voter2.getFirstName() + " проголосував за " + candidate2.getLastName());
    }

    // Голосування voter3 за candidate1
    if (!context.votes().hasVoted(voter3.getId(), election.getId())) {
      Vote vote3 = new Vote(voter3.getId(), candidate1.getId(), election.getId(),
          LocalDateTime.now());
      context.votes().save(vote3);
      System.out.println(
          "✓ " + voter3.getFirstName() + " проголосував за " + candidate1.getLastName());
    }

    // Спроба повторного голосування
    if (context.votes().hasVoted(voter1.getId(), election.getId())) {
      System.out.println("✗ " + voter1.getFirstName() + " вже голосував на цих виборах!");
    }

    // ==================== ПІДРАХУНОК ГОЛОСІВ ====================
    System.out.println("\n═══════════════════ РЕЗУЛЬТАТИ ═══════════════════\n");

    long totalVotes = context.votes().countByElection(election.getId());
    System.out.println("Всього голосів на виборах: " + totalVotes);

    for (Candidate c : candidates) {
      long votes = context.votes().countByCandidate(c.getId());
      double percentage = totalVotes > 0 ? (votes * 100.0 / totalVotes) : 0;
      System.out.printf("  %s %s: %d голосів (%.1f%%)\n",
          c.getFirstName(), c.getLastName(), votes, percentage);
    }

    // ==================== DELETE ====================
    System.out.println("\n═══════════════════ DELETE ═══════════════════\n");

    // Створюємо тестового виборця для видалення
    Voter tempVoter = new Voter("Тест", "Тестович", "test@example.com", "hash",
        "FF666666", LocalDate.of(2000, 1, 1), VoterRole.VOTER, kyiv.getId());
    context.voters().save(tempVoter);
    System.out.println("Створено тестового виборця: " + tempVoter.getEmail());

    boolean deleted = context.voters().deleteById(tempVoter.getId());
    System.out.println("Видалено: " + deleted);
    System.out.println("Існує після видалення: " + context.voters().existsById(tempVoter.getId()));

    // ==================== СТАТИСТИКА ====================
    System.out.println("\n═══════════════════ СТАТИСТИКА ═══════════════════\n");

    System.out.println("┌────────────────────┬───────────┐");
    System.out.println("│ Сутність           │ Кількість │");
    System.out.println("├────────────────────┼───────────┤");
    System.out.printf("│ Регіони            │ %9d │\n",
        context.regions().count(RegionSpecifications.all()));
    System.out.printf("│ Партії             │ %9d │\n",
        context.parties().count(PartySpecifications.all()));
    System.out.printf("│ Вибори             │ %9d │\n",
        context.elections().count(ElectionSpecifications.all()));
    System.out.printf("│ Кандидати          │ %9d │\n",
        context.candidates().count(CandidateSpecifications.all()));
    System.out.printf("│ Виборці            │ %9d │\n",
        context.voters().count(VoterSpecifications.all()));
    System.out.printf("│ Голоси             │ %9d │\n",
        context.votes().count(VoteSpecifications.all()));
    System.out.println("└────────────────────┴───────────┘");

    System.out.println("\n✓ Демонстрація завершена успішно!");
  }
}
