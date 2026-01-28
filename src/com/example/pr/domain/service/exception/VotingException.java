package com.example.pr.domain.service.exception;

/**
 * Виняток голосування.
 */
public class VotingException extends ServiceException {

  public VotingException(String message) {
    super(message);
  }

  public static VotingException alreadyVoted() {
    return new VotingException("Ви вже проголосували на цих виборах");
  }

  public static VotingException electionNotActive() {
    return new VotingException("Вибори не активні");
  }

  public static VotingException electionNotStarted() {
    return new VotingException("Вибори ще не почались");
  }

  public static VotingException electionEnded() {
    return new VotingException("Вибори вже завершились");
  }

  public static VotingException invalidCandidate() {
    return new VotingException("Кандидат не бере участь у цих виборах");
  }

  public static VotingException voterNotEligible() {
    return new VotingException("Виборець не має права голосувати");
  }
}
