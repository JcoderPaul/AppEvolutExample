package me.oldboy.market.validator;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.repository.BrandRepository;
import me.oldboy.market.repository.CategoryRepository;

import java.util.Scanner;

@AllArgsConstructor
public class InputExistChecker {
    private CategoryRepository categoryRepository;
    private BrandRepository brandRepository;

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