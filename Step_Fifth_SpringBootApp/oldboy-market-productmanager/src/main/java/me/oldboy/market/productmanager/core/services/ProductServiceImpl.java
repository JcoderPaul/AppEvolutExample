package me.oldboy.market.productmanager.core.services;

import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.logger.annotation.EnableLog;
import me.oldboy.market.productmanager.core.dto.product.ProductCreateDto;
import me.oldboy.market.productmanager.core.dto.product.ProductReadDto;
import me.oldboy.market.productmanager.core.dto.product.ProductUpdateDto;
import me.oldboy.market.productmanager.core.entity.Product;
import me.oldboy.market.productmanager.core.entity.prod_species.Brand;
import me.oldboy.market.productmanager.core.entity.prod_species.Category;
import me.oldboy.market.productmanager.core.exceptions.ProductManagerModuleException;
import me.oldboy.market.productmanager.core.mapper.ProductMapper;
import me.oldboy.market.productmanager.core.repository.BrandRepository;
import me.oldboy.market.productmanager.core.repository.CategoryRepository;
import me.oldboy.market.productmanager.core.repository.ProductRepository;
import me.oldboy.market.productmanager.core.services.interfaces.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для управления товарами.
 * Содержит бизнес-логику проверки уникальности названий, существования категорий и брендов.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {
    /**
     * Репозиторий для работы с товарами
     */
    private final ProductRepository productRepository;
    /**
     * Репозиторий для работы с категориями товаров (проверка их наличия)
     */
    private CategoryRepository categoryRepository;
    /**
     * Репозиторий для работы с брэндами товаров (проверка их наличия)
     */
    private BrandRepository brandRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
    }

    /**
     * Возвращает все товары из системы.
     *
     * @return список всех товаров
     */
    @Override
    @EnableLog
    public List<ProductReadDto> findAll() {
        return productRepository
                .findAll()
                .stream()
                .map(ProductMapper.INSTANCE::mapToReadDto)
                .toList();
    }

    /**
     * Находит товар по идентификатору.
     *
     * @param entityId идентификатор товара
     * @return найденный товар или null если не найден
     */
    @Override
    @EnableLog
    public Optional<ProductReadDto> findById(Long entityId) {
        return productRepository
                .findById(entityId)
                .map(ProductMapper.INSTANCE::mapToReadDto);
    }

    /**
     * Проверяет уникальность названия товара в системе.
     *
     * @param productName название товара для проверки
     * @return true - название уникально, false - товар с таким названием уже существует в БД
     */
    @Override
    @EnableLog
    public boolean isProductNameUnique(String productName) {
        return findAll()
                .stream()
                .noneMatch(product -> product.name().equals(productName));
    }

    /**
     * Создает новый товар в системе.
     *
     * @param createDto товар для создания без ID
     * @return созданный товар с присвоенным идентификатором ID
     * @throws ProductManagerModuleException если нарушены бизнес-правила валидации (не уникальное имя, не существующий брэнд или категория)
     */
    @Transactional
    @Override
    @EnableLog
    public ProductReadDto create(ProductCreateDto createDto) {
        if (!isProductNameUnique(createDto.name())) {
            throw new ProductManagerModuleException("Имя продукта" + createDto.name() + " не уникально, сохранение товара не возможно");
        }
        Optional<Category> mayBeFoundCategory = categoryRepository.findById(createDto.categoryId());
        if (mayBeFoundCategory.isEmpty()) {
            throw new ProductManagerModuleException("Категория с ID - " + createDto.categoryId() + " не найдена, сохранение товара не возможно");
        }
        Optional<Brand> mayBeFoundBrand = brandRepository.findById(createDto.brandId());
        if (mayBeFoundBrand.isEmpty()) {
            throw new ProductManagerModuleException("Брэнд с ID - " + createDto.brandId() + " не найден, сохранение товара не возможно");
        }

        Product toCreateProduct = Product.builder()
                .name(createDto.name())
                .price(createDto.price())
                .category(mayBeFoundCategory.get())
                .brand(mayBeFoundBrand.get())
                .description(createDto.description())
                .stockQuantity(createDto.stockQuantity())
                .creationAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now().plusSeconds(1))
                .build();

        Product productWithId = productRepository.save(toCreateProduct);

        return ProductMapper.INSTANCE.mapToReadDto(productWithId);
    }

    /**
     * Обновляет данные товара с проверкой бизнес-правил.
     *
     * @param updateEntity товар с обновленными данными
     * @return обновленную сущность (обновленные данные по продукту)
     * @throws ProductManagerModuleException если нарушены бизнес-правила валидации (ID товара не найден, имя товара не уникально)
     */
    @Transactional
    @Override
    @EnableLog
    public ProductReadDto update(ProductUpdateDto updateEntity) {
        if (findById(updateEntity.id()).isEmpty()) {
            throw new ProductManagerModuleException("Не найден ID - " + updateEntity.id() + " продукта, обновление невозможно");
        }
        if (!isProductNameUnique(updateEntity.name())) {
            throw new ProductManagerModuleException("Имя продукта " + updateEntity.name() + " не уникально, обновление невозможно");
        }
        Optional<Product> mayBeFoundProduct = productRepository.findById(updateEntity.id());
        if (mayBeFoundProduct.isPresent()) {
            Product forUpdatedProduct = mayBeFoundProduct.get();

            forUpdatedProduct.setName(updateEntity.name());
            forUpdatedProduct.setPrice(updateEntity.price());
            forUpdatedProduct.setDescription(updateEntity.description());
            forUpdatedProduct.setStockQuantity(updateEntity.stockQuantity());
            forUpdatedProduct.setModifiedAt(LocalDateTime.now());

            Product updatedDto = productRepository.save(forUpdatedProduct);

            return ProductMapper.INSTANCE.mapToReadDto(updatedDto);
        } else {
            return null;
        }
    }

    /**
     * Удаляет товар по идентификатору.
     *
     * @param entityId идентификатор товара для удаления
     * @return true - товар удален, false - в противном случае
     */
    @Transactional
    @Override
    @EnableLog
    public boolean delete(Long entityId) {
        if (findById(entityId).isPresent()) {
            productRepository.deleteById(entityId);
            return true;
        } else {
            throw new ProductManagerModuleException("Not found product with ID - " + entityId + " for delete!");
        }
    }

    /**
     * Находит товар по идентификатору категории и идентификатору товара.
     *
     * @param categoryId идентификатор категории
     * @param productId  идентификатор товара
     * @return найденный товар или null если не найден
     */
    @Override
    @EnableLog
    public Optional<ProductReadDto> findProductByCategoryAndId(Integer categoryId, Long productId) {
        Optional<List<Product>> mayBeFoundList = productRepository.findByCategory(categoryId);
        if (mayBeFoundList.get().size() != 0) {
            return mayBeFoundList
                    .get()
                    .stream()
                    .filter(product -> product.getId().equals(productId))
                    .findAny()
                    .map(ProductMapper.INSTANCE::mapToReadDto);
        }
        return Optional.empty();
    }

    /**
     * Находит товар по идентификатору бренда и идентификатору товара.
     *
     * @param brandId   идентификатор бренда
     * @param productId идентификатор товара
     * @return найденный товар или null если не найден
     */
    @Override
    @EnableLog
    public Optional<ProductReadDto> findProductByBrandAndId(Integer brandId, Long productId) {
        Optional<List<Product>> mayBeFoundList = productRepository.findByBrand(brandId);
        if (mayBeFoundList.get().size() != 0) {
            return mayBeFoundList
                    .get()
                    .stream()
                    .filter(product -> product.getId().equals(productId))
                    .findAny()
                    .map(ProductMapper.INSTANCE::mapToReadDto);
        }
        return Optional.empty();
    }

    /**
     * Находит товар по идентификатору бренда и названию товара.
     *
     * @param brandId идентификатор бренда
     * @param name    название товара
     * @return найденный товар или null если не найден
     */
    @Override
    @EnableLog
    public Optional<ProductReadDto> findProductByBrandAndName(Integer brandId, String name) {
        Optional<List<Product>> mayBeFoundList = productRepository.findByBrand(brandId);
        if (mayBeFoundList.get().size() != 0) {
            return mayBeFoundList
                    .get()
                    .stream()
                    .filter(product -> product.getName().equals(name))
                    .findAny()
                    .map(ProductMapper.INSTANCE::mapToReadDto);
        }
        return Optional.empty();
    }

    /**
     * Находит все товары указанной категории.
     *
     * @param categoryId идентификатор категории
     * @return список товаров категории
     */
    @Override
    @EnableLog
    public List<ProductReadDto> findProductByCategory(Integer categoryId) {
        List<Product> mayBeFoundList = productRepository.findByCategory(categoryId).get();
        if (mayBeFoundList.size() != 0) {
            return mayBeFoundList
                    .stream()
                    .map(ProductMapper.INSTANCE::mapToReadDto)
                    .toList();
        }
        return new ArrayList<>();
    }

    /**
     * Находит все товары указанного бренда.
     *
     * @param brandId идентификатор бренда
     * @return список товаров бренда
     */
    @Override
    @EnableLog
    public List<ProductReadDto> findProductByBrand(Integer brandId) {
        List<Product> mayBeFoundList = productRepository.findByBrand(brandId).get();
        if (mayBeFoundList.size() != 0) {
            return mayBeFoundList
                    .stream()
                    .map(ProductMapper.INSTANCE::mapToReadDto)
                    .toList();
        }
        return new ArrayList<>();
    }

    /**
     * Находит все товары указанного бренда и категории.
     *
     * @param brandId    идентификатор бренда
     * @param categoryId идентификатор категории
     * @return список товаров бренда
     */
    @Override
    @EnableLog
    public List<ProductReadDto> findProductByBrandAndCategory(Integer brandId, Integer categoryId) {
        List<Product> mayBeFoundList = productRepository.findByBrandAndCategory(brandId, categoryId).get();
        if (mayBeFoundList.size() != 0) {
            return mayBeFoundList
                    .stream()
                    .map(ProductMapper.INSTANCE::mapToReadDto)
                    .toList();
        }
        return new ArrayList<>();
    }
}