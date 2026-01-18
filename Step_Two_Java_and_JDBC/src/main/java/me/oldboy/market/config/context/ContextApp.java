package me.oldboy.market.config.context;

import lombok.Getter;
import me.oldboy.market.controlers.LoginLogoutController;
import me.oldboy.market.controlers.ProductCrudController;
import me.oldboy.market.controlers.view.ViewAuditRecordController;
import me.oldboy.market.controlers.view.ViewBrandController;
import me.oldboy.market.controlers.view.ViewCategoryController;
import me.oldboy.market.controlers.view.ViewProductController;
import me.oldboy.market.repository.*;
import me.oldboy.market.services.*;
import me.oldboy.market.validator.InputExistChecker;

import java.sql.Connection;

/**
 * Класс формирует "контекст" приложения, инициализирует основные классы и передает зависимости.
 */
@Getter
public class ContextApp {
    /* Слой репозиториев (DAO) */
    private AuditRepository auditRepository;
    private BrandRepository brandRepository;
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;
    /* Слой сервисов */
    private AuditServiceImpl auditService;
    private ProductServiceImpl productService;
    private UserServiceImpl userService;
    private BrandServiceImpl brandService;
    private CategoryServiceImpl categoryService;
    /* Слой контроллеров */
    private LoginLogoutController loginLogoutController;
    private ProductCrudController productCrudController;
    private ViewProductController viewProductController;
    private ViewBrandController viewBrandController;
    private ViewCategoryController viewCategoryController;
    private ViewAuditRecordController viewAuditRecordController;
    /* Псевдо валидаторы */
    private InputExistChecker inputExistChecker;

    public ContextApp(Connection connection) {
        /* Инициализируем слой репозиториев и прокидываем зависимости */
        this.userRepository = new UserRepository(connection);
        this.productRepository = new ProductRepository(connection);
        this.categoryRepository = new CategoryRepository(connection);
        this.brandRepository = new BrandRepository(connection);
        this.auditRepository = new AuditRepository(connection);
        /* Инициализируем слой сервисов и прокидываем зависимости */
        this.userService = new UserServiceImpl(userRepository);
        this.productService = new ProductServiceImpl(productRepository, categoryRepository, brandRepository);
        this.auditService = new AuditServiceImpl(auditRepository, userRepository);
        this.brandService = new BrandServiceImpl(brandRepository);
        this.categoryService = new CategoryServiceImpl(categoryRepository);
        /* Инициализируем слой контроллеров и прокидываем зависимости */
        this.loginLogoutController = new LoginLogoutController(userService, auditService);
        this.productCrudController = new ProductCrudController(productService, auditService);
        this.viewProductController = new ViewProductController(productService);
        this.viewCategoryController = new ViewCategoryController(categoryRepository);
        this.viewBrandController = new ViewBrandController(brandRepository);
        this.viewAuditRecordController = new ViewAuditRecordController(auditRepository);
        /* Инициализируем псевдо-валидатор и прокидываем зависимости */
        this.inputExistChecker = new InputExistChecker(categoryRepository, brandRepository);
    }
}