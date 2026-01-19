package me.oldboy.market.services;

import lombok.AllArgsConstructor;
import me.oldboy.market.entity.Product;
import me.oldboy.market.exceptions.ServiceLayerException;
import me.oldboy.market.repository.BrandRepositoryImpl;
import me.oldboy.market.repository.CategoryRepositoryImpl;
import me.oldboy.market.repository.ProductRepositoryImpl;
import me.oldboy.market.repository.interfaces.BrandRepository;
import me.oldboy.market.repository.interfaces.CategoryRepository;
import me.oldboy.market.repository.interfaces.ProductRepository;
import me.oldboy.market.services.interfaces.ProductService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для управления товарами.
 * Содержит бизнес-логику проверки уникальности названий, существования категорий и брендов.
 *
 * @see ProductService
 * @see ProductRepositoryImpl
 * @see CategoryRepositoryImpl
 * @see BrandRepositoryImpl
 * @see Product
 */
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {
    /**
     * Репозиторий для работы с товарами
     */
    private ProductRepository productRepository;
    /**
     * Репозиторий для работы с категориями товаров (проверка их наличия)
     */
    private CategoryRepository categoryRepository;
    /**
     * Репозиторий для работы с брэндами товаров (проверка их наличия)
     */
    private BrandRepository brandRepository;

    /**
     * Возвращает все товары из системы.
     *
     * @return список всех товаров
     */
    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    /**
     * Находит товар по идентификатору.
     *
     * @param entityId идентификатор товара
     * @return найденный товар или null если не найден
     */
    @Override
    public Product findById(Long entityId) {
        return productRepository
                .findById(entityId)
                .orElse(null);
    }

    /**
     * Проверяет уникальность названия товара в системе.
     *
     * @param productName название товара для проверки
     * @return true - название уникально, false - товар с таким названием уже существует в БД
     */
    @Override
    public boolean isProductNameUnique(String productName) {
        return findAll()
                .stream()
                .noneMatch(product -> product.getName().equals(productName));
    }

    /**
     * Создает новый товар в системе.
     *
     * @param entityWithoutId товар для создания без ID
     * @return созданный товар с присвоенным идентификатором ID
     * @throws ServiceLayerException если нарушены бизнес-правила валидации (не уникальное имя, не существующий брэнд или категория)
     */
    @Override
    public Product create(Product entityWithoutId) throws ServiceLayerException {
        if (!isProductNameUnique(entityWithoutId.getName())) {
            throw new ServiceLayerException("Имя продукта" + entityWithoutId.getName() + " не уникально, сохранение товара не возможно");
        }
        if (categoryRepository.findById(entityWithoutId.getCategoryId()).isEmpty()) {
            throw new ServiceLayerException("Категория с ID - " + entityWithoutId.getCategoryId() + " не найдена, сохранение товара не возможно");
        }
        if (brandRepository.findById(entityWithoutId.getBrandId()).isEmpty()) {
            throw new ServiceLayerException("Брэнд с ID - " + entityWithoutId.getBrandId() + " не найден, сохранение товара не возможно");
        }
        return productRepository
                .create(entityWithoutId)
                .orElseThrow(() -> new ServiceLayerException("Не удалось создать новый товар."));
    }

    /**
     * Обновляет данные товара с проверкой бизнес-правил.
     *
     * @param updateEntityWithIdAndData товар с обновленными данными
     * @return true - обновление успешно, false - в противном случае
     * @throws ServiceLayerException если нарушены бизнес-правила валидации (ID товара не найден, имя товара не уникально)
     */
    @Override
    public boolean update(Product updateEntityWithIdAndData) throws ServiceLayerException {
        boolean isUpdated = false;
        if (findById(updateEntityWithIdAndData.getId()) == null) {
            throw new ServiceLayerException("Не найден ID - " + updateEntityWithIdAndData.getId() + " продукта, обновление невозможно");
        }
        if (!isProductNameUnique(updateEntityWithIdAndData.getName())) {
            throw new ServiceLayerException("Имя продукта " + updateEntityWithIdAndData.getName() + " не уникально, обновление невозможно");
        }
        Optional<Product> mayBeFoundProduct = productRepository.findById(updateEntityWithIdAndData.getId());
        if (mayBeFoundProduct.isPresent()) {
            Product forUpdatedProduct = mayBeFoundProduct.get();

            forUpdatedProduct.setName(updateEntityWithIdAndData.getName());
            forUpdatedProduct.setPrice(updateEntityWithIdAndData.getPrice());
            forUpdatedProduct.setDescription(updateEntityWithIdAndData.getDescription());
            forUpdatedProduct.setStockQuantity(updateEntityWithIdAndData.getStockQuantity());
            forUpdatedProduct.setModifiedAt(LocalDateTime.now());

            isUpdated = productRepository.update(forUpdatedProduct);
        }
        return isUpdated;
    }

    /**
     * Удаляет товар по идентификатору.
     *
     * @param entityId идентификатор товара для удаления
     * @return true - товар удален, false - в противном случае
     */
    @Override
    public boolean delete(Long entityId) {
        boolean idDeleted = false;
        if (findById(entityId) != null) {
            idDeleted = productRepository.delete(entityId);
        }
        return idDeleted;
    }

    /**
     * Находит товар по идентификатору категории и идентификатору товара.
     *
     * @param categoryId идентификатор категории
     * @param productId  идентификатор товара
     * @return найденный товар или null если не найден
     */
    @Override
    public Product findProductByCategoryAndId(Integer categoryId, Long productId) {
        return productRepository
                .findByCategoryId(categoryId)
                .get()
                .stream()
                .filter(product -> product.getId().equals(productId))
                .findAny()
                .orElse(null);
    }

    /**
     * Находит товар по идентификатору бренда и идентификатору товара.
     *
     * @param brandId   идентификатор бренда
     * @param productId идентификатор товара
     * @return найденный товар или null если не найден
     */
    @Override
    public Product findProductByBrandAndId(Integer brandId, Long productId) {
        return productRepository
                .findByBrandId(brandId)
                .get()
                .stream()
                .filter(product -> product.getId().equals(productId))
                .findAny()
                .orElse(null);
    }

    /**
     * Находит товар по идентификатору бренда и названию товара.
     *
     * @param brandId идентификатор бренда
     * @param name    название товара
     * @return найденный товар или null если не найден
     */
    @Override
    public Product findProductByBrandAndName(Integer brandId, String name) {
        return productRepository
                .findByBrandId(brandId)
                .get()
                .stream()
                .filter(product -> product.getName().equals(name))
                .findAny()
                .orElse(null);
    }

    /**
     * Находит все товары указанной категории.
     *
     * @param categoryId идентификатор категории
     * @return список товаров категории
     */
    @Override
    public List<Product> findProductByCategory(Integer categoryId) {
        return productRepository.findByCategoryId(categoryId).get();
    }

    /**
     * Находит все товары указанного бренда.
     *
     * @param brandId идентификатор бренда
     * @return список товаров бренда
     */
    @Override
    public List<Product> findProductByBrand(Integer brandId) {
        return productRepository.findByBrandId(brandId).get();
    }
}