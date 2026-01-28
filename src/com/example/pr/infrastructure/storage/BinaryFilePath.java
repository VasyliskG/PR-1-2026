package com.example.pr.infrastructure.storage;

/**
 * Enum що містить шляхи до бінарних файлів для збереження даних.
 */
public enum BinaryFilePath {
  VOTERS("data/voters.dat"),
  CANDIDATES("data/candidates.dat"),
  ELECTIONS("data/elections.dat"),
  VOTES("data/votes.dat"),
  REGIONS("data/regions.dat"),
  PARTIES("data/parties.dat");

  private final String path;

  BinaryFilePath(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}