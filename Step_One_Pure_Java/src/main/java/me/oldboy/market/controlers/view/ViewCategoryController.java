package me.oldboy.market.controlers.view;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.CategoryRepository;

import java.util.List;

/**
 * Класс для отображения категорий товаров
 */
@AllArgsConstructor
public class ViewCategoryController {
    private CategoryRepository categoryRepository;

    /**
     * Метод отображает все доступные категории товаров - Category
     *
     * @return коллекцию доступных категорий товаров - Category объектов
     */
    public List<Category> printAllCategory() {
        List<Category> allCategories = categoryRepository.findAll();
        System.out.println(allCategories);
        return allCategories;
    }
}
