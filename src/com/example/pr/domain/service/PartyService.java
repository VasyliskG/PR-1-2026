package com.example.pr.domain.service;

import com.example.pr.domain.dto.party.PartyCreateDto;
import com.example.pr.domain.dto.party.PartyResponseDto;
import com.example.pr.domain.dto.party.PartyUpdateDto;
import com.example.pr.domain.impl.Party;
import com.example.pr.domain.service.exception.DuplicateEntityException;
import com.example.pr.domain.service.exception.EntityNotFoundException;
import com.example.pr.domain.service.exception.ServiceException;
import com.example.pr.domain.specification.PartySpecifications;
import com.example.pr.infrastructure.storage.contract.PartyRepository;
import com.example.pr.infrastructure.storage.impl.DataContext;

import java.util.List;
import java.util.UUID;

/**
 * Сервіс для роботи з партіями.
 */
public class PartyService {

  private final PartyRepository partyRepository;
  private final DataContext context;

  public PartyService() {
    this.context = DataContext.getInstance();
    this.partyRepository = context.parties();
  }

  /**
   * Створює нову партію.
   */
  public PartyResponseDto create(PartyCreateDto dto) {
    // Перевірка унікальності назви
    if (partyRepository.exists(PartySpecifications.byName(dto.name()))) {
      throw new DuplicateEntityException("Партія", "name", dto.name());
    }

    // Перевірка унікальності абревіатури
    if (dto.abbreviation() != null &&
        partyRepository.exists(PartySpecifications.byAbbreviation(dto.abbreviation()))) {
      throw new DuplicateEntityException("Партія", "abbreviation", dto.abbreviation());
    }

    Party party = new Party(dto.partyCode(), dto.name(), dto.abbreviation(), dto.logoPath(), dto.program());
    partyRepository.save(party);

    return PartyResponseDto.fromEntity(party);
  }

  /**
   * Знаходить партію за ID.
   */
  public PartyResponseDto findById(UUID id) {
    Party party = partyRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Партія", id));
    return PartyResponseDto.fromEntity(party);
  }

  /**
   * Знаходить партію за абревіатурою.
   */
  public PartyResponseDto findByAbbreviation(String abbreviation) {
    Party party = partyRepository.findOne(PartySpecifications.byAbbreviation(abbreviation))
        .orElseThrow(() -> new EntityNotFoundException("Партія", "abbreviation", abbreviation));
    return PartyResponseDto.fromEntity(party);
  }

  /**
   * Оновлює партію.
   */
  public PartyResponseDto update(UUID id, PartyUpdateDto dto) {
    Party party = partyRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Партія", id));

    dto.name().ifPresent(name -> {
      if (!name.equals(party.getName()) &&
          partyRepository.exists(PartySpecifications.byName(name))) {
        throw new DuplicateEntityException("Партія", "name", name);
      }
      party.setName(name);
    });

    dto.abbreviation().ifPresent(abbr -> {
      String normalized = abbr.toUpperCase();
      if (!normalized.equals(party.getAbbreviation()) &&
          partyRepository.exists(PartySpecifications.byAbbreviation(normalized))) {
        throw new DuplicateEntityException("Партія", "abbreviation", normalized);
      }
      party.setAbbreviation(normalized);
    });

    dto.logoPath().ifPresent(party::setLogoPath);
    dto.program().ifPresent(party::setProgram);

    partyRepository.save(party);
    return PartyResponseDto.fromEntity(party);
  }

  /**
   * Видаляє партію.
   */
  public void delete(String partyCode) {
    if (!partyRepository.existsByCode(partyCode)) {
      throw new EntityNotFoundException("Партія", partyCode);
    }

    // Перевірка наявності кандидатів від партії
    long candidateCount = context.candidates().countByPartyCode(partyCode);
    if (candidateCount > 0) {
      throw new ServiceException(
          String.format("Не можна видалити партію, від якої є %d кандидатів", candidateCount)
      );
    }

    partyRepository.deleteByCode(partyCode);
  }

  /**
   * Знаходить всі партії.
   */
  public List<PartyResponseDto> findAll() {
    return partyRepository.findAll(PartySpecifications.all()).stream()
        .map(PartyResponseDto::fromEntity)
        .toList();
  }

  /**
   * Пошук партій за назвою.
   */
  public List<PartyResponseDto> searchByName(String query) {
    return partyRepository.findAll(PartySpecifications.nameContains(query)).stream()
        .map(PartyResponseDto::fromEntity)
        .toList();
  }

  /**
   * Знаходить партії з програмою.
   */
  public List<PartyResponseDto> findWithProgram() {
    return partyRepository.findAll(PartySpecifications.hasProgram()).stream()
        .map(PartyResponseDto::fromEntity)
        .toList();
  }

  /**
   * Підраховує кількість партій.
   */
  public long count() {
    return partyRepository.count(PartySpecifications.all());
  }

  /**
   * Отримує кількість кандидатів від партії.
   */
  public long getCandidateCount(String partyCode) {
    if (!partyRepository.existsByCode(partyCode)) {
      throw new EntityNotFoundException("Партія", partyCode);
    }
    return context.candidates().countByPartyCode(partyCode);
  }

  /**
   * Знаходить партію за кодом.
   */
  public PartyResponseDto findByCode(String code) {
    Party party = partyRepository.findByCode(code)
        .orElseThrow(() -> new EntityNotFoundException("Партія", "code", code));
    return PartyResponseDto.fromEntity(party);
  }
}
