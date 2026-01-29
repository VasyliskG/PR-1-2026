package com.example.pr.presentation.menu;

import com.example.pr.domain.dto.candidate.CandidateResponseDto;
import com.example.pr.domain.dto.election.ElectionResponseDto;
import com.example.pr.domain.dto.vote.VoteCreateDto;
import com.example.pr.domain.dto.vote.VoteResultDto;
import com.example.pr.domain.dto.voter.VoterResponseDto;
import com.example.pr.domain.service.*;
import com.example.pr.domain.service.exception.ServiceException;
import com.example.pr.domain.service.exception.VotingException;
import com.example.pr.presentation.ConsoleUI;
import com.example.pr.presentation.util.TablePrinter;

import java.util.*;

import static com.example.pr.presentation.util.ConsoleColors.*;

/**
 * Меню голосування.
 */
public class VoteMenu extends ConsoleUI implements Menu {

  private final VoteService voteService;
  private final ElectionService electionService;
  private final CandidateService candidateService;
  private final AuthService authService;

  public VoteMenu(Scanner scanner, VoteService voteService, ElectionService electionService,
      CandidateService candidateService, AuthService authService) {
    super(scanner);
    this.voteService = voteService;
    this.electionService = electionService;
    this.candidateService = candidateService;
    this.authService = authService;
  }

  @Override
  public String getTitle() {
    return "Голосування";
  }

  @Override
  public boolean show() {
    if (!authService.isAuthenticated()) {
      printError("Для голосування потрібно увійти в систему!");
      input.pressEnterToContinue();
      return false;
    }

    TablePrinter.printSection("🗳️ " + getTitle());

    printMenuItem(1, "Проголосувати");
    printMenuItem(2, "Мої голоси");
    printMenuItem(3, "Результати виборів");
    printBackItem();

    int choice = input.readMenuChoice(3);

    switch (choice) {
      case 1 -> vote();
      case 2 -> showMyVotes();
      case 3 -> showResults();
      case 0 -> { return false; }
    }

    return true;
  }

  private void vote() {
    System.out.println("\n" + header("── Голосування ──"));

    try {
      VoterResponseDto voter = authService.getCurrentUser().orElseThrow();

      List<ElectionResponseDto> activeElections = electionService.findActive();

      if (activeElections.isEmpty()) {
        printWarning("Немає активних виборів.");
        input.pressEnterToContinue();
        return;
      }

      System.out.println("\n" + info("Активні вибори:"));
      for (int i = 0; i < activeElections.size(); i++) {
        ElectionResponseDto e = activeElections.get(i);
        boolean hasVoted = voteService.hasVoted(voter.id(), e.id());
        String status = hasVoted ? RED + " [Вже проголосували]" + RESET : GREEN + " [Доступно]" + RESET;
        System.out.println(YELLOW + "  [" + (i + 1) + "] " + RESET + e.name() + status);
      }
      System.out.println(PURPLE + "  [0] " + RESET + "Скасувати");

      int electionChoice = input.readIntInRange("Оберіть вибори", 0, activeElections.size());
      if (electionChoice == 0) return;

      ElectionResponseDto election = activeElections.get(electionChoice - 1);

      if (voteService.hasVoted(voter.id(), election.id())) {
        printError("Ви вже проголосували на цих виборах!");
        input.pressEnterToContinue();
        return;
      }

      List<CandidateResponseDto> candidates = candidateService.findByElection(election.id());

      System.out.println("\n" + info("Кандидати:"));
      String[] headers = {"№", "Ім'я", "Партія", "Програма"};
      List<String[]> rows = new ArrayList<>();

      for (int i = 0; i < candidates.size(); i++) {
        CandidateResponseDto c = candidates.get(i);
        String program = c.program() != null
            ? (c.program().length() > 35 ? c.program().substring(0, 32) + "..." : c.program())
            : "-";
        String party = c.partyCode() != null ? c.partyCode() : "Незалежний";
        rows.add(new String[]{String.valueOf(i + 1), c.fullName(), party, program});
      }
      TablePrinter.print(headers, rows);

      System.out.println(PURPLE + "  [0] " + RESET + "Скасувати");

      int candidateChoice = input.readIntInRange("Оберіть кандидата", 0, candidates.size());
      if (candidateChoice == 0) {
        printInfo("Голосування скасовано.");
        input.pressEnterToContinue();
        return;
      }

      CandidateResponseDto candidate = candidates.get(candidateChoice - 1);

      printWarning("Ви збираєтесь проголосувати за: " + WHITE_BOLD + candidate.fullName() + RESET);

      if (!input.confirm("Підтвердити голос?")) {
        printInfo("Голосування скасовано.");
        input.pressEnterToContinue();
        return;
      }

      voteService.vote(new VoteCreateDto(voter.id(), candidate.id(), election.id()));

      System.out.println();
      System.out.println(GREEN_BOLD + "╔══════════════════════════════════════════╗" + RESET);
      System.out.println(GREEN_BOLD + "║    ✓ ВАШ ГОЛОС УСПІШНО ЗАРЕЄСТРОВАНО!   ║" + RESET);
      System.out.println(GREEN_BOLD + "╚══════════════════════════════════════════╝" + RESET);

    } catch (VotingException e) {
      printError(e.getMessage());
    } catch (ServiceException e) {
      printError("Помилка: " + e.getMessage());
    }

    input.pressEnterToContinue();
  }

  private void showMyVotes() {
    System.out.println("\n" + header("── Мої голоси ──"));

    VoterResponseDto voter = authService.getCurrentUser().orElseThrow();
    var votes = voteService.findByVoter(voter.id());

    if (votes.isEmpty()) {
      printInfo("Ви ще не брали участі у голосуваннях.");
    } else {
      String[] headers = {"Вибори", "Кандидат", "Дата"};
      List<String[]> rows = new ArrayList<>();

      for (var vote : votes) {
        String electionName = electionService.findById(vote.electionId()).name();
        CandidateResponseDto candidate = candidateService.findById(vote.candidateId());
        rows.add(new String[]{electionName, candidate.fullName(), vote.timestamp().toString()});
      }
      TablePrinter.print(headers, rows);
    }

    input.pressEnterToContinue();
  }

  public void showResults() {
    System.out.println("\n" + header("── Результати виборів ──"));

    List<ElectionResponseDto> elections = electionService.findAll();

    if (elections.isEmpty()) {
      printInfo("Немає виборів у системі.");
      input.pressEnterToContinue();
      return;
    }

    System.out.println("\n" + info("Оберіть вибори:"));
    for (int i = 0; i < elections.size(); i++) {
      ElectionResponseDto e = elections.get(i);
      long voteCount = voteService.countByElection(e.id());
      System.out.println(YELLOW + "  [" + (i + 1) + "] " + RESET + e.name() +
          CYAN + " (" + voteCount + " голосів, " + e.status() + ")" + RESET);
    }
    System.out.println(PURPLE + "  [0] " + RESET + "Назад");

    int choice = input.readIntInRange("Ваш вибір", 0, elections.size());
    if (choice == 0) return;

    ElectionResponseDto election = elections.get(choice - 1);
    List<VoteResultDto> results = voteService.getResults(election.id());

    System.out.println("\n" + WHITE_BOLD + "📊 " + election.name() + RESET);
    System.out.println(info("Статус: " + election.status()));

    if (results.isEmpty()) {
      printInfo("Ще немає голосів.");
    } else {
      String[] headers = {"Місце", "Кандидат", "Партія", "Голоси", "%"};
      List<String[]> rows = new ArrayList<>();

      for (int i = 0; i < results.size(); i++) {
        VoteResultDto r = results.get(i);
        String place = switch (i) {
          case 0 -> "🥇 1";
          case 1 -> "🥈 2";
          case 2 -> "🥉 3";
          default -> "   " + (i + 1);
        };
        rows.add(new String[]{
            place, r.candidateFullName(), r.partyName(),
            String.valueOf(r.voteCount()), String.format("%.1f%%", r.percentage())
        });
      }
      TablePrinter.print(headers, rows);

      // Графік
      System.out.println("\n" + info("Графік:"));
      for (VoteResultDto r : results) {
        int barLength = (int) (r.percentage() / 2);
        String bar = "█".repeat(Math.max(1, barLength));
        String name = r.candidateFullName().length() > 20
            ? r.candidateFullName().substring(0, 17) + "..."
            : r.candidateFullName();
        System.out.printf("  %-20s %s%s%s %.1f%%\n", name, GREEN, bar, RESET, r.percentage());
      }
    }

    input.pressEnterToContinue();
  }
}
