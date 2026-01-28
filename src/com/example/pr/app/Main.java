package com.example.pr.app;

import com.example.pr.domain.enums.ElectionStatus;
import com.example.pr.domain.enums.VoterRole;
import com.example.pr.domain.impl.Candidate;
import com.example.pr.domain.impl.Election;
import com.example.pr.domain.impl.Party;
import com.example.pr.domain.impl.Region;
import com.example.pr.domain.impl.Vote;
import com.example.pr.domain.impl.Voter;
import com.example.pr.domain.specification.CandidateSpecifications;
import com.example.pr.domain.specification.VoteSpecifications;
import com.example.pr.infrastructure.storage.impl.DataContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class Main {

  public static void main(String[] args) {
    DataContext context = DataContext.getInstance();

    // Створюємо регіон
    Region kyiv = new Region("Київська область", "KYIV", "Столичний регіон");
    context.regions().save(kyiv);

    // Створюємо партію
    Party party = new Party("Демократична партія", "ДП", null, "Програма партії...");
    context.parties().save(party);

    // Створюємо вибори
    Election election = new Election(
        "Президентські вибори 2026",
        "Вибори Президента України",
        LocalDateTime.of(2026, 3, 31, 8, 0),
        LocalDateTime.of(2026, 3, 31, 20, 0),
        ElectionStatus.PENDING
    );
    context.elections().save(election);

    // Створюємо кандидата
    Candidate candidate = new Candidate(
        "Іван",
        "Петренко",
        party.getId(),
        election.getId(),
        "Моя передвиборча програма...",
        null,
        "Біографія кандидата"
    );
    context.candidates().save(candidate);

    // Створюємо виборця
    Voter voter = new Voter(
        "Марія",
        "Коваленко",
        "maria@example.com",
        "hashedPassword123",
        "AB123456",
        LocalDate.of(1990, 5, 15),
        VoterRole.VOTER,
        kyiv.getId()
    );
    context.voters().save(voter);

    // Голосуємо
    if (!context.votes().hasVoted(voter.getId(), election.getId())) {
      Vote vote = new Vote(
          voter.getId(),
          candidate.getId(),
          election.getId(),
          LocalDateTime.now()
      );
      context.votes().save(vote);
      System.out.println("Голос успішно зареєстровано!");
    }

    // Підраховуємо голоси
    long voteCount = context.votes().countByCandidate(candidate.getId());
    System.out.println("Кількість голосів за " + candidate.getFullName() + ": " + voteCount);

    // Пошук кандидатів за виборами
    List<Candidate> candidates = context.candidates()
        .findAll(CandidateSpecifications.byElectionId(election.getId()));
    System.out.println("Кандидати на виборах: " + candidates.size());

    // Перевірка голосів
    List<Vote> allVotes = context.votes()
        .findAll(VoteSpecifications.byElectionId(election.getId()));
    System.out.println("Всього голосів: " + allVotes.size());
  }
}