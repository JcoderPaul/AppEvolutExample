package me.oldboy.market.validator;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.BrandRepository;
import me.oldboy.market.repository.CategoryRepository;

import java.util.Scanner;

/**
 * Класс для интерактивной проверки существования сущностей при пользовательском вводе.
 * Обеспечивает повторный запрос ввода до получения валидного ID существующей сущности.
 *
 * @see CategoryRepository
 * @see BrandRepository
 * @see InputValidator
 */
@AllArgsConstructor
public class InputExistChecker {
    /**
     * Репозиторий для работы с категориями товаров
     */
    private CategoryRepository categoryRepository;
    /**
     * Репозиторий для работы с брэндами товаров
     */
    private BrandRepository brandRepository;

    /**
     * Интерактивно запрашивает выбор категории до получения валидного ID существующей категории.
     *
     * @param scanner сканер для чтения пользовательского ввода
     * @return существующая в системе категория
     */
    public Category repeatsCategoryChoiceToExistingOne(Scanner scanner) {
        while (true) {
            try {
                Integer inputCategoryId = InputValidator.intValidator(scanner);
                return categoryRepository
                        .findById(inputCategoryId)
                        .orElseThrow(IllegalArgumentException::new);
            } catch (IllegalArgumentException e) {
                System.out.println("\nОшибка ввода!\nВыберите ID существующей категории:");
            }
        }
    }

    /**
     * Интерактивно запрашивает выбор бренда до получения валидного ID существующего бренда.
     *
     * @param scanner сканер для чтения пользовательского ввода
     * @return существующий в системе бренд
     */
    public Brand repeatsBrandChoiceToExistingOne(Scanner scanner) {
        while (true) {
            try {
                Integer inputBrandId = InputValidator.intValidator(scanner);
                return brandRepository
                        .findById(inputBrandId)
                        .orElseThrow(IllegalArgumentException::new);
            } catch (IllegalArgumentException e) {
                System.out.println("\nОшибка ввода!\nВыберите ID существующего брэнда:");
            }
        }
    }
}