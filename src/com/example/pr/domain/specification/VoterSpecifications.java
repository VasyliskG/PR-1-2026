package com.example.pr.domain.specification;

import com.example.pr.domain.enums.VoterRole;
import com.example.pr.domain.impl.Voter;
import java.util.UUID;

public final class VoterSpecifications {

  private VoterSpecifications() {}

  public static Specification<Voter> byEmail(String email) {
    return voter -> voter.getEmail() != null
        && voter.getEmail().equalsIgnoreCase(email);
  }

  public static Specification<Voter> byPassportNumber(String passportNumber) {
    return voter -> voter.getPassportNumber() != null
        && voter.getPassportNumber().equals(passportNumber);
  }

  public static Specification<Voter> byRole(VoterRole role) {
    return voter -> voter.getRole() == role;
  }

  public static Specification<Voter> byRegionId(UUID regionId) {
    return voter -> voter.getRegionId() != null
        && voter.getRegionId().equals(regionId);
  }

  public static Specification<Voter> fullNameContains(String text) {
    return voter -> {
      String fullName = voter.getFirstName() + " " + voter.getLastName();
      return fullName.toLowerCase().contains(text.toLowerCase());
    };
  }

  public static Specification<Voter> all() {
    return voter -> true;
  }
}