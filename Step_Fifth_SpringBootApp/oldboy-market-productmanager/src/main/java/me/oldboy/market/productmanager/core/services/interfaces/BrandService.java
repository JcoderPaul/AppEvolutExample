package me.oldboy.market.productmanager.core.services.interfaces;

import me.oldboy.market.productmanager.core.dto.brand.BrandReadDto;
import me.oldboy.market.productmanager.core.services.interfaces.crud.ReadOnlyService;

import java.util.Optional;

/**
 * Специализированный сервис для работы с брэндами.
 * Предоставляет только операции чтения (поиска).
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
