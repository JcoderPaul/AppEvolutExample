package me.oldboy.market.controlers.view;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.repository.BrandRepository;

import java.util.List;

/**
 * Класс для отображения брэндов
 */
@AllArgsConstructor
public class ViewBrandController {
    private BrandRepository brandRepository;

    /**
     * Метод отображает все доступные брэнды товаров - Brand
     *
     * @return коллекцию брэндов - Brand объектов
     */
    public List<Brand> printAllBrands() {
        List<Brand> allBrands = brandRepository.findAll();
        System.out.println(allBrands);
        return allBrands;
    }
}