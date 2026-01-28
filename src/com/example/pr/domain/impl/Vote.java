package com.example.pr.domain.impl;

import com.example.pr.domain.BaseEntity;
import com.example.pr.domain.exeption.EntityValidationException;
import com.example.pr.domain.util.ValidationError;
import java.time.LocalDateTime;
import java.util.UUID;

public class Vote extends BaseEntity {

  public static final String FIELD_VOTER_ID = "voterId";
  public static final String FIELD_CANDIDATE_ID = "candidateId";
  public static final String FIELD_ELECTION_ID = "electionId";
  public static final String FIELD_TIMESTAMP = "timestamp";

  private UUID voterId;
  private UUID candidateId;
  private UUID electionId;
  private LocalDateTime timestamp;

  public Vote() {
    super();
  }

  public Vote(UUID voterId, UUID candidateId, UUID electionId, LocalDateTime timestamp) {
    this();
    setVoterId(voterId);
    setCandidateId(candidateId);
    setElectionId(electionId);
    setTimestamp(timestamp);

    if (!isValid()) {
      throw new EntityValidationException(getErrors());
    }
  }

  public UUID getVoterId() {
    return voterId;
  }

  public void setVoterId(UUID voterId) {
    clearError(FIELD_VOTER_ID);
    if (voterId == null) {
      addError(FIELD_VOTER_ID, ValidationError.VOTE_VOTER_ID_REQUIRED.getMessage());
    }
    this.voterId = voterId;
    updateTimestamp();
  }

  public UUID getCandidateId() {
    return candidateId;
  }

  public void setCandidateId(UUID candidateId) {
    clearError(FIELD_CANDIDATE_ID);
    if (candidateId == null) {
      addError(FIELD_CANDIDATE_ID, ValidationError.VOTE_CANDIDATE_ID_REQUIRED.getMessage());
    }
    this.candidateId = candidateId;
    updateTimestamp();
  }

  public UUID getElectionId() {
    return electionId;
  }

  public void setElectionId(UUID electionId) {
    clearError(FIELD_ELECTION_ID);
    if (electionId == null) {
      addError(FIELD_ELECTION_ID, ValidationError.VOTE_ELECTION_ID_REQUIRED.getMessage());
    }
    this.electionId = electionId;
    updateTimestamp();
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    clearError(FIELD_TIMESTAMP);
    if (timestamp == null) {
      addError(FIELD_TIMESTAMP, ValidationError.VOTE_TIMESTAMP_REQUIRED.getMessage());
    }
    this.timestamp = timestamp;
    updateTimestamp();
  }

  @Override
  public String toString() {
    return String.format("Vote{voterId=%s, candidateId=%s, electionId=%s, timestamp=%s}",
        voterId, candidateId, electionId, timestamp);
  }
}