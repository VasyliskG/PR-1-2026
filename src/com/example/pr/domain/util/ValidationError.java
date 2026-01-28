package com.example.pr.domain.util;

public enum ValidationError {
  // Voter
  FIRST_NAME_REQUIRED("Ім'я не може бути порожнім."),
  FIRST_NAME_LENGTH("Ім'я повинно містити від 2 до 50 символів."),
  LAST_NAME_REQUIRED("Прізвище не може бути порожнім."),
  LAST_NAME_LENGTH("Прізвище повинно містити від 2 до 50 символів."),
  EMAIL_REQUIRED("Електронна пошта не може бути порожньою."),
  EMAIL_FORMAT("Невірний формат електронної пошти."),
  PASSWORD_REQUIRED("Пароль не може бути порожнім."),
  ROLE_REQUIRED("Роль є обов'язковою."),
  PASSPORT_NUMBER_REQUIRED("Номер паспорта є обов'язковим."),
  PASSPORT_NUMBER_FORMAT("Невірний формат номера паспорта."),
  BIRTH_DATE_REQUIRED("Дата народження є обов'язковою."),
  VOTER_TOO_YOUNG("Виборець повинен бути старше 18 років."),
  REGION_ID_REQUIRED("ID регіону є обов'язковим."),

  // Candidate
  CANDIDATE_FULL_NAME_REQUIRED("Повне ім'я кандидата є обов'язковим."),
  CANDIDATE_FULL_NAME_LENGTH("Повне ім'я кандидата повинно містити від 2 до 100 символів."),
  PARTY_ID_REQUIRED("ID партії є обов'язковим."),
  ELECTION_ID_REQUIRED("ID виборів є обов'язковим."),
  CANDIDATE_PROGRAM_LENGTH("Програма кандидата занадто довга (максимум 4096 символів)."),

  // Party
  PARTY_NAME_REQUIRED("Назва партії не може бути порожньою."),
  PARTY_NAME_LENGTH("Назва партії повинна містити від 2 до 100 символів."),
  PARTY_ABBREVIATION_LENGTH("Абревіатура партії повинна містити від 2 до 10 символів."),

  // Region
  REGION_NAME_REQUIRED("Назва регіону не може бути порожньою."),
  REGION_NAME_LENGTH("Назва регіону повинна містити від 2 до 100 символів."),
  REGION_CODE_REQUIRED("Код регіону є обов'язковим."),
  REGION_CODE_FORMAT("Невірний формат коду регіону."),

  // Election
  ELECTION_NAME_REQUIRED("Назва виборів не може бути порожньою."),
  ELECTION_NAME_LENGTH("Назва виборів повинна містити від 5 до 200 символів."),
  ELECTION_START_DATE_REQUIRED("Дата початку виборів є обов'язковою."),
  ELECTION_END_DATE_REQUIRED("Дата завершення виборів є обов'язковою."),
  ELECTION_DATE_INVALID("Дата завершення повинна бути пізніше дати початку."),
  ELECTION_STATUS_REQUIRED("Статус виборів є обов'язковим."),

  // Vote
  VOTE_VOTER_ID_REQUIRED("ID виборця є обов'язковим."),
  VOTE_CANDIDATE_ID_REQUIRED("ID кандидата є обов'язковим."),
  VOTE_ELECTION_ID_REQUIRED("ID виборів є обов'язковим."),
  VOTE_TIMESTAMP_REQUIRED("Час голосування є обов'язковим."),
  VOTE_ALREADY_EXISTS("Виборець вже проголосував на цих виборах.");

  private final String message;

  ValidationError(String message) {
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}