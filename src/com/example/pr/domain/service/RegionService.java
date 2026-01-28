package com.example.pr.domain.service;

import com.example.pr.domain.dto.region.RegionCreateDto;
import com.example.pr.domain.dto.region.RegionResponseDto;
import com.example.pr.domain.dto.region.RegionUpdateDto;
import com.example.pr.domain.impl.Region;
import com.example.pr.domain.service.exception.DuplicateEntityException;
import com.example.pr.domain.service.exception.EntityNotFoundException;
import com.example.pr.domain.service.exception.ServiceException;
import com.example.pr.domain.specification.RegionSpecifications;
import com.example.pr.infrastructure.storage.contract.RegionRepository;
import com.example.pr.infrastructure.storage.impl.DataContext;

import java.util.List;
import java.util.UUID;

/**
 * Сервіс для роботи з регіонами.
 */
public class RegionService {

  private final RegionRepository regionRepository;
  private final DataContext context;

  public RegionService() {
    this.context = DataContext.getInstance();
    this.regionRepository = context.regions();
  }

  /**
   * Створює новий регіон.
   */
  public RegionResponseDto create(RegionCreateDto dto) {
    // Перевірка унікальності коду
    if (regionRepository.exists(RegionSpecifications.byCode(dto.code()))) {
      throw new DuplicateEntityException("Регіон", "code", dto.code());
    }

    // Перевірка унікальності назви
    if (regionRepository.exists(RegionSpecifications.byName(dto.name()))) {
      throw new DuplicateEntityException("Регіон", "name", dto.name());
    }

    Region region = new Region(dto.name(), dto.code(), dto.description());
    regionRepository.save(region);

    return RegionResponseDto.fromEntity(region);
  }

  /**
   * Знаходить регіон за ID.
   */
  public RegionResponseDto findById(UUID id) {
    Region region = regionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Регіон", id));
    return RegionResponseDto.fromEntity(region);
  }

  /**
   * Знаходить регіон за кодом.
   */
  public RegionResponseDto findByCode(String code) {
    Region region = regionRepository.findOne(RegionSpecifications.byCode(code))
        .orElseThrow(() -> new EntityNotFoundException("Регіон", "code", code));
    return RegionResponseDto.fromEntity(region);
  }

  /**
   * Оновлює регіон.
   */
  public RegionResponseDto update(UUID id, RegionUpdateDto dto) {
    Region region = regionRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Регіон", id));

    dto.name().ifPresent(name -> {
      // Перевірка унікальності нової назви
      if (!name.equals(region.getName()) &&
          regionRepository.exists(RegionSpecifications.byName(name))) {
        throw new DuplicateEntityException("Регіон", "name", name);
      }
      region.setName(name);
    });

    dto.code().ifPresent(code -> {
      // Перевірка унікальності нового коду
      String normalizedCode = code.toUpperCase();
      if (!normalizedCode.equals(region.getCode()) &&
          regionRepository.exists(RegionSpecifications.byCode(normalizedCode))) {
        throw new DuplicateEntityException("Регіон", "code", normalizedCode);
      }
      region.setCode(normalizedCode);
    });

    dto.description().ifPresent(region::setDescription);

    regionRepository.save(region);
    return RegionResponseDto.fromEntity(region);
  }

  /**
   * Видаляє регіон.
   */
  public void delete(UUID id) {
    if (!regionRepository.existsById(id)) {
      throw new EntityNotFoundException("Регіон", id);
    }

    // Перевірка наявності виборців у регіоні
    long voterCount = context.voters().countByRegionId(id);
    if (voterCount > 0) {
      throw new ServiceException(
          String.format("Не можна видалити регіон, в якому є %d виборців", voterCount)
      );
    }

    regionRepository.deleteById(id);
  }

  /**
   * Знаходить всі регіони.
   */
  public List<RegionResponseDto> findAll() {
    return regionRepository.findAll(RegionSpecifications.all()).stream()
        .map(RegionResponseDto::fromEntity)
        .toList();
  }

  /**
   * Пошук регіонів за назвою.
   */
  public List<RegionResponseDto> searchByName(String query) {
    return regionRepository.findAll(RegionSpecifications.nameContains(query)).stream()
        .map(RegionResponseDto::fromEntity)
        .toList();
  }

  /**
   * Підраховує кількість регіонів.
   */
  public long count() {
    return regionRepository.count(RegionSpecifications.all());
  }

  /**
   * Отримує статистику виборців по регіону.
   */
  public long getVoterCount(UUID regionId) {
    if (!regionRepository.existsById(regionId)) {
      throw new EntityNotFoundException("Регіон", regionId);
    }
    return context.voters().countByRegionId(regionId);
  }
}
