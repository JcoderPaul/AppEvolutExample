package me.oldboy.market.cache_bd;

import lombok.Getter;
import me.oldboy.market.entity.Product;
import me.oldboy.market.entity.prod_species.Brand;
import me.oldboy.market.entity.prod_species.Category;
import me.oldboy.market.exceptions.ProductDBException;

import java.util.*;

/**
 * Класс имитирует "кэш" таблицы БД содержащей данные о доступных товарах
 */
public class ProductDB {
    @Getter
    private List<Product> productsList = new ArrayList<>();
    @Getter
    private Map<Long, Product> productByIdIndex = new HashMap<>();
    @Getter
    private Map<Category, List<Product>> categoryIndex = new HashMap<>();
    @Getter
    private Map<Brand, List<Product>> brandIndex = new HashMap<>();

    private static ProductDB INSTANCE;

    public static ProductDB getINSTANCE() {
        if (INSTANCE == null) {
            INSTANCE = new ProductDB();
        }
        return INSTANCE;
    }

    /**
     * Метод добавляет новый товар в "кэш" таблицы хранящей сведения о товарах
     *
     * @param product новый добавляемый товар
     * @return ID добавленного товара в "кэш" БД товаров
     */
    public Long add(Product product) {
        Long index = 1L;

        if (productsList.size() != 0) {
            index = index + productsList.stream()
                    .map(p -> p.getId())
                    .max((a, b) -> a > b ? 1 : -1)
                    .orElseThrow(() -> new ProductDBException("Element not found"));
        }

        product.setId(index);
        productsList.add(product);
        productByIdIndex.put(index, product);

        List<Product> catProdList = categoryIndex.get(product.getCategory());
        if (catProdList == null) {
            catProdList = new ArrayList<>();
        }
        catProdList.add(product);
        categoryIndex.put(product.getCategory(), catProdList);

        List<Product> catBrandList = brandIndex.get(product.getBrand());
        if (catBrandList == null) {
            catBrandList = new ArrayList<>();
        }
        catBrandList.add(product);
        brandIndex.put(product.getBrand(), catBrandList);

        return index;
    }

    /**
     * Метод удаляет товар из "кэша таблицы" товаров
     *
     * @param product товар удаляемый из "кэша"
     * @return true - если удаление прошло успешно, false - в случае неудачного удаления
     */
    public boolean delete(Product product) {
        boolean idDelete = false;
        if (productsList.remove(product)) {
            idDelete = true;
        }
        if (productByIdIndex.remove(product.getId()) != null) {
            idDelete = true;
        }
        {
            Category existCategory = product.getCategory();

            categoryIndex.computeIfPresent(existCategory, (category, productList) -> {
                productList.removeIf(p -> p.equals(product));
                return productList;
            });

            if (categoryIndex.get(existCategory).contains(product)) {
                idDelete = false;
            }
        }
        {
            Brand existBrand = product.getBrand();

            brandIndex.computeIfPresent(existBrand, (brand, productList) -> {
                productList.removeIf(p -> p.equals(product));
                return productList;
            });

            if (brandIndex.get(existBrand).contains(product)) {
                idDelete = false;
            }
        }
        return idDelete;
    }

    /**
     * Метод обновляет товар в "кэше таблицы" товаров
     *
     * @param updateProduct товар для обновления
     */
    public void update(Product updateProduct) {
        Product oldProduct = productByIdIndex.get(updateProduct.getId());

        if (!oldProduct.getBrand().equals(updateProduct.getBrand()) ||
                !oldProduct.getCategory().equals(updateProduct.getCategory())) {
            throw new ProductDBException("Unable to update category or brand, please create a new product.");
        }

        oldProduct.setName(updateProduct.getName());
        oldProduct.setDescription(updateProduct.getDescription());
        oldProduct.setPrice(updateProduct.getPrice());
        oldProduct.setStockQuantity(updateProduct.getStockQuantity());
        oldProduct.setLastModifiedTimestamp(updateProduct.getLastModifiedTimestamp());
    }

    /**
     * Метод ищет товар по уникальному идентификатору ID
     *
     * @param id значение идентификатора искомого товара
     * @return Optional, содержащий Product, если продукт найден, иначе пустой Optional.
     */
    public Optional<Product> findProductById(Long id) {
        return Optional.ofNullable(productByIdIndex.get(id));
    }

    /**
     * Метод ищет все товары по их категории
     *
     * @param category категория товаров которые нужно найти
     * @return возвращает список Product объектов искомой категории.
     */
    public List<Product> findProductByCategory(Category category) {
        if (categoryIndex.get(category) == null) {
            throw new ProductDBException(category.getName() + " category not found");
        }
        return categoryIndex.get(category);
    }

    /**
     * Метод ищет все товары по их брэнду
     *
     * @param brand брэнд товаров которые нужно найти
     * @return возвращает список Product объектов соответствующих искомому брэнду.
     */
    public List<Product> findProductByBrand(Brand brand) {
        if (brandIndex.get(brand) == null) {
            throw new ProductDBException(brand.getName() + " brand not found");
        }
        return brandIndex.get(brand);
    }
}