package me.oldboy.market.controllers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.aop.annotations.Loggable;
import me.oldboy.market.dto.brand.BrandReadDto;
import me.oldboy.market.mapper.BrandMapper;
import me.oldboy.market.services.interfaces.BrandService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс - контроллер для управления брэндами товара (в текущей реализации только просмотр)
 */
@Slf4j
@AllArgsConstructor
public class BrandController {
    private BrandService brandService;

    /**
     * Ищет брэнд по уникальному идентификационному номеру ID в БД
     *
     * @param brandId идентификатор брэнда в БД
     * @return найденный брэнд в случае успеха и null - если не найден
     */
    @Loggable
    public BrandReadDto findBrandById(Integer brandId) {
        return BrandMapper.INSTANCE.mapToReadDto(brandService.findById(brandId));
    }

    /**
     * Возвращает список всех доступных брэндов.
     *
     * @return все доступные брэнды
     */
    @Loggable
    public List<BrandReadDto> findAllBrands() {
        return brandService
                .findAll()
                .stream()
                .map(brand -> BrandMapper.INSTANCE.mapToReadDto(brand))
                .collect(Collectors.toList());
    }
}
