package me.oldboy.market.productmanager.config;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import me.oldboy.market.productmanager.core.repository.BrandRepository;
import me.oldboy.market.productmanager.core.repository.CategoryRepository;
import me.oldboy.market.productmanager.core.repository.ProductRepository;
import me.oldboy.market.productmanager.core.services.BrandServiceImpl;
import me.oldboy.market.productmanager.core.services.CategoryServiceImpl;
import me.oldboy.market.productmanager.core.services.ProductServiceImpl;
import me.oldboy.market.productmanager.core.services.interfaces.BrandService;
import me.oldboy.market.productmanager.core.services.interfaces.CategoryService;
import me.oldboy.market.productmanager.core.services.interfaces.ProductService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация автоматического подключения модуля управления продуктами (Product).
 * Создаёт и настраивает компоненты для взаимодействия с продуктами, категориями и
 * брэндами приложения, если в classpath присутствуют необходимые классы.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties(ProductManagerProperties.class)
@ConditionalOnClass(ProductManagerProperties.class)
public class ProductManagerAutoConfiguration {

    /**
     * Инициализационный метод для фиксации факта загрузки конфигурации.
     * Выводит информационное сообщение в лог при инициализации конфигурации.
     */
    @PostConstruct
    void init() {
        log.info("ProductManagerConfiguration init");
    }

    /**
     * Создаёт бин сервиса для управления продуктами.
     * Сервис отвечает за сохранение, изменение,
     * удаление, просмотр продуктов в БД.
     */
    @Bean
    @ConditionalOnMissingBean(ProductService.class)
    public ProductService productService(ProductRepository productRepository,
                                         CategoryRepository categoryRepository,
                                         BrandRepository brandRepository) {
        return new ProductServiceImpl(productRepository, categoryRepository, brandRepository);
    }

    /**
     * Создаёт бин сервиса для управления категориями продуктов.
     * Сервис отвечает за просмотр категорий продуктов в БД.
     */
    @Bean
    @ConditionalOnMissingBean(CategoryService.class)
    public CategoryService categoryService(CategoryRepository categoryRepository) {
        return new CategoryServiceImpl(categoryRepository);
    }

    /**
     * Создаёт бин сервиса для управления брэндами продуктов.
     * Сервис отвечает за просмотр брэндов продуктов в БД.
     */
    @Bean
    @ConditionalOnMissingBean(BrandService.class)
    public BrandService brandService(BrandRepository brandRepository) {
        return new BrandServiceImpl(brandRepository);
    }
}