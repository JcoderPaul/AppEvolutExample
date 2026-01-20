package me.oldboy.market.services.interfaces;

import me.oldboy.market.dto.brand.BrandReadDto;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.services.interfaces.crud.ReadOnlyService;

import java.util.Optional;

/**
 * Специализированный сервис для работы с брэндами.
 * Предоставляет только операции чтения (поиска).
 *
 * @see ReadOnlyService
 * @see Brand
 */
public interface BrandService extends ReadOnlyService<Integer, BrandReadDto> {
    /**
     * Находит сущность по его уникальному имени (названию).
     *
     * @param brandName имя сущности (название брэнда)
     * @return найденная сущность
     */
    Optional<BrandReadDto> findByName(String brandName);
}
