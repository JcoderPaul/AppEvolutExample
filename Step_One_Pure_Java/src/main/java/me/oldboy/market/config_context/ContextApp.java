package me.oldboy.market.config_context;

import lombok.Getter;
import me.oldboy.market.cache_bd.*;
import me.oldboy.market.cache_bd.loaders.*;
import me.oldboy.market.controlers.LoginLogoutController;
import me.oldboy.market.controlers.ProductCrudController;
import me.oldboy.market.controlers.view.ViewAuditRecordController;
import me.oldboy.market.controlers.view.ViewBrandController;
import me.oldboy.market.controlers.view.ViewCategoryController;
import me.oldboy.market.controlers.view.ViewProductController;
import me.oldboy.market.repository.*;
import me.oldboy.market.services.AuditService;
import me.oldboy.market.services.ProductService;
import me.oldboy.market.services.UserService;
import me.oldboy.market.validator.InputExistChecker;

/**
 * Класс формирует "контекст" приложения, инициализирует основные классы и передает зависимости.
 */
@Getter
public class ContextApp {
    /* БД */
    private AuditDB auditDB;
    private UserDB userDB;
    private CategoryDB categoryDB;
    private ProductDB productDB;
    private BrandDB brandDB;

    /* Слой репозиториев (DAO) */
    private AuditRepository auditRepository;
    private BrandRepository brandRepository;
    private CategoryRepository categoryRepository;
    private ProductRepository productRepository;
    private UserRepository userRepository;
    /* Слой сервисов */
    private AuditService auditService;
    private ProductService productService;
    private UserService userService;
    /* Слой контроллеров */
    private LoginLogoutController loginLogoutController;
    private ProductCrudController productCrudController;
    private ViewProductController viewProductController;
    private ViewBrandController viewBrandController;
    private ViewCategoryController viewCategoryController;
    private ViewAuditRecordController viewAuditRecordController;
    /* Псевдо валидаторы */
    private InputExistChecker inputExistChecker;

    public ContextApp() {
        /* Инициализируем БД */
        this.userDB = UserDB.getINSTANCE();
        this.categoryDB = CategoryDB.getINSTANCE();
        this.brandDB = BrandDB.getINSTANCE();
        this.productDB = ProductDB.getINSTANCE();
        this.auditDB = AuditDB.getINSTANCE();
        /* Прогружаем их данными */
        UserDBLoader.initInMemoryBase(userDB);
        CategoryDBLoader.initInMemoryBase(categoryDB);
        BrandDBLoader.initInMemoryBase(brandDB);
        ProductBDLoader.initInMemoryBase(productDB);
        AuditDBLoader.initInMemoryBase(auditDB);
        /* Инициализируем слой репозиториев и прокидываем зависимости */
        this.userRepository = new UserRepository(userDB);
        this.productRepository = new ProductRepository(productDB);
        this.categoryRepository = new CategoryRepository(categoryDB);
        this.brandRepository = new BrandRepository(brandDB);
        this.auditRepository = new AuditRepository(auditDB);
        /* Инициализируем слой сервисов и прокидываем зависимости */
        this.userService = new UserService(userRepository);
        this.productService = new ProductService(productRepository);
        this.auditService = new AuditService(auditRepository);
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
