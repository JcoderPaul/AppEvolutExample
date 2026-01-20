package me.oldboy.market.services;

import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.aop.annotations.Loggable;
import me.oldboy.market.dto.brand.BrandReadDto;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.mapper.BrandMapper;
import me.oldboy.market.repository.BrandRepository;
import me.oldboy.market.services.interfaces.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для работы с брэндами товара (продукта).
 *
 * @see BrandService
 * @see BrandRepository
 * @see Brand
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {
    /**
     * Репозиторий для работы с записями о брэндах товара
     */
    private final BrandRepository brandRepository;

    @Autowired
    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    /**
     * Находит брэнд в БД по его идентификатору.
     *
     * @param entityId идентификатор брэнда в таблице БД
     * @return найденный брэнд или null если запись о нем не найдена
     */
    @Override
    @Loggable
    public Optional<BrandReadDto> findById(Integer entityId) {
        return brandRepository
                .findById(entityId)
                .map(BrandMapper.INSTANCE::mapToReadDto);
    }

    /**
     * Возвращает все брэнды из системы.
     *
     * @return список всех брэндов
     */
    @Override
    @Loggable
    public List<BrandReadDto> findAll() {
        return brandRepository
                .findAll()
                .stream()
                .map(BrandMapper.INSTANCE::mapToReadDto)
                .toList();
    }

    /**
     * Находит запись брэнд в БД по его названию (имени).
     *
     * @param brandName уникальное название брэнда (имя) в таблице БД
     * @return найденный брэнд или null если запись о нем не найдена
     */
    @Override
    @Loggable
    public Optional<BrandReadDto> findByName(String brandName) {
        return brandRepository
                .findByName(brandName)
                .map(brand -> BrandMapper.INSTANCE.mapToReadDto(brand));
    }
}