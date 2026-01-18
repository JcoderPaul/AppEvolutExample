package me.oldboy.market.controlers.view;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.exceptions.ServiceLayerException;
import me.oldboy.market.services.ProductServiceImpl;

import java.util.List;

/**
 * Класс для отображения товаров (продуктов, Product)
 */
@AllArgsConstructor
public class ViewProductController {
    private ProductServiceImpl productService;

    /**
     * Отображает список всех доступных продуктов.
     */
    public void viewAllProduct() {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("\nСписок товаров:\n");
        productService.findAll().forEach(System.out::println);
        System.out.println("-----------------------------------------------------------------------------");
    }

    /**
     * Отображает товар Product найденный по его ID уникальному идентификатору
     *
     * @param id уникальный идентификатор товара (продукта, Product)
     */
    public void viewProductById(Long id) {
        try {
            System.out.println("-----------------------------------------------------------------------------");
            System.out.println("\nВыбранный по " + id + "товар:\n");
            System.out.println(productService.findById(id));
            System.out.println("-----------------------------------------------------------------------------");
        } catch (ServiceLayerException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Отображает продукт найденный по категории Category и уникальному идентификатору товара ID
     *
     * @param category  категория искомого товара
     * @param productId искомый уникальный идентификатор товара
     */
    public void findProductByCategoryAndId(Category category, Long productId) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("\nВыбранной категории " + category.getName() + " и ID - " + productId + " соответствует:\n");
        System.out.println(productService.findProductByCategoryAndId(category.getId(), productId));
        System.out.println("-----------------------------------------------------------------------------");
    }

    /**
     * Отображает продукт Product найденный по его брэнду Brand и уникальному идентификатору товара ID
     *
     * @param brand     брэнд искомого товара
     * @param productId искомый уникальный идентификатор товара
     */
    public void findProductByBrandAndId(Brand brand, Long productId) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("\nВыбранному брэнду" + brand.getName() + " и ID - " + productId + " соответствует:\n");
        System.out.println(productService.findProductByBrandAndId(brand.getId(), productId));
        System.out.println("-----------------------------------------------------------------------------");
    }

    /**
     * Отображает продукт Product найденный по его брэнду Brand и уникальному названию
     *
     * @param brand брэнд искомого товара
     * @param name  название искомого товара
     */
    public void findProductByBrandAndName(Brand brand, String name) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("\nВыбранному брэнду" + brand.getName() + " и названию - " + name + " соответствует:\n");
        System.out.println(productService.findProductByBrandAndName(brand.getId(), name));
        System.out.println("-----------------------------------------------------------------------------");
    }

    /**
     * Отображает все продукты Product принадлежащих к категории Category
     *
     * @param category категория для поиска
     * @return список всех продуктов Product указанной категории Category
     */
    public List<Product> findProductByCategory(Category category) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("\nСписок товаров по категории" + category.getName() + " :\n");
        productService.findProductByCategory(category.getId()).forEach(System.out::println);
        System.out.println("-----------------------------------------------------------------------------");
        return productService.findProductByCategory(category.getId());
    }

    /**
     * Отображает все продукты Product принадлежащих брэнду Brand
     *
     * @param brand брэнд для поиска
     * @return список всех продуктов Product указанного брэнда Brand
     */
    public List<Product> findProductByBrand(Brand brand) {
        System.out.println("-----------------------------------------------------------------------------");
        System.out.println("\nСписок товаров по брэнду" + brand.getName() + " :\n");
        productService.findProductByBrand(brand.getId()).forEach(System.out::println);
        System.out.println("-----------------------------------------------------------------------------");
        return productService.findProductByBrand(brand.getId());
    }
}