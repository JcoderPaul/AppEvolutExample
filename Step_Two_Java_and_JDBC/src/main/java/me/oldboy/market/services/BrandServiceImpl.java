package me.oldboy.market.services;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.repository.BrandRepository;
import me.oldboy.market.services.interfaces.BrandService;

import java.util.List;

/**
 * Реализация сервиса для работы с брэндами товара (продукта).
 *
 * @see BrandService
 * @see BrandRepository
 * @see Brand
 */
@AllArgsConstructor
public class BrandServiceImpl implements BrandService {
    /**
     * Репозиторий для работы с записями о брэндах товара
     */
    private BrandRepository brandRepository;

    /**
     * Находит брэнд в БД по его идентификатору.
     *
     * @param entityId идентификатор брэнда в таблице БД
     * @return найденный брэнд или null если запись о нем не найдена
     */
    @Override
    public Brand findById(Integer entityId) {
        return brandRepository
                .findById(entityId)
                .orElse(null);
    }

    /**
     * Возвращает все брэнды из системы.
     *
     * @return список всех брэндов
     */
    @Override
    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    /**
     * Находит запись брэнд в БД по его названию (имени).
     *
     * @param brandName уникальное название брэнда (имя) в таблице БД
     * @return найденный брэнд или null если запись о нем не найдена
     */
    @Override
    public Brand findByName(String brandName) {
        return brandRepository
                .findByName(brandName)
                .orElse(null);
    }
}