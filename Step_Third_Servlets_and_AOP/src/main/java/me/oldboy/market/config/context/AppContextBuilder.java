package me.oldboy.market.config.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import me.oldboy.market.aop.aspects.AuditingAspect;
import me.oldboy.market.config.connection.DbConnectionPool;
import me.oldboy.market.config.liquibase.LiquibaseManager;
import me.oldboy.market.config.utils.ConfigProvider;
import me.oldboy.market.config.utils.PropertiesReader;
import me.oldboy.market.controllers.*;
import me.oldboy.market.repository.*;
import me.oldboy.market.repository.interfaces.*;
import me.oldboy.market.security.JwtTokenGenerator;
import me.oldboy.market.services.*;
import me.oldboy.market.services.interfaces.*;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

/**
 * Web-слушатель для инициализации и конфигурации контекста приложения.
 * Выполняет настройку всех компонентов приложения при запуске веб-приложения.
 *
 * @see ServletContextListener
 * @see DbConnectionPool
 * @see ConfigProvider
 */
@WebListener
public class AppContextBuilder implements ServletContextListener {
    /**
     * Пул соединений с базой данных
     */
    private DbConnectionPool connectionPool;
    /**
     * Поставщик конфигурационных параметров приложения
     */
    private ConfigProvider configProvider;

    /**
     * Вызывается при запуске веб-приложения.
     * Инициализирует все компоненты приложения и регистрирует их в контексте.
     *
     * @param sce событие инициализации сервлет-контекста
     * @throws RuntimeException если произошла ошибка при инициализации компонентов
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        final ServletContext servletContext = sce.getServletContext();

        this.configProvider = new PropertiesReader();
        this.connectionPool = DbConnectionPool.getINSTANCE();
        connectionPool.initPool(configProvider);

        startDatabaseMigration();
        serviceContextInit(servletContext);
        loadMappers(servletContext);
    }

    /**
     * Вызывается при завершении работы веб-приложения.
     * Выполняет очистку ресурсов и закрытие соединений.
     *
     * @param sce событие уничтожения сервлет-контекст
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
    }

    /**
     * Запускает миграции базы данных с использованием Liquibase.
     * Миграции выполняются только если свойство 'liquibase.enabled' установлено в true.
     *
     * @throws RuntimeException если произошла ошибка SQL при выполнении миграций
     */
    private void startDatabaseMigration() {
        if (Boolean.parseBoolean(configProvider.get("liquibase.enabled"))) {
            LiquibaseManager liquibaseManager = LiquibaseManager.getInstance(configProvider);
            try {
                liquibaseManager.migrationsStart(connectionPool.getConnection());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Инициализирует все сервисы, репозитории и контроллеры приложения.
     * Регистрирует созданные объекты в сервлет-контексте для дальнейшего использования.
     *
     * @param servletContext сервлет-контекст для регистрации атрибутов
     */
    private void serviceContextInit(ServletContext servletContext) {
        /* Инициализация "слоя репозиториев" */
        UserRepository userRepository = new UserRepositoryImpl(connectionPool);
        ProductRepository productRepository = new ProductRepositoryImpl(connectionPool);
        CategoryRepository categoryRepository = new CategoryRepositoryImpl(connectionPool);
        BrandRepository brandRepository = new BrandRepositoryImpl(connectionPool);
        AuditRepository auditRepository = new AuditRepositoryImpl(connectionPool);

        /* Инициализация "слоя сервисов" */
        UserService userService = new UserServiceImpl(userRepository);
        ProductService productService = new ProductServiceImpl(productRepository, categoryRepository, brandRepository);
        CategoryService categoryService = new CategoryServiceImpl(categoryRepository);
        BrandService brandService = new BrandServiceImpl(brandRepository);
        AuditService auditService = new AuditServiceImpl(auditRepository, userRepository);

        /* Инициализация части "раздела аспектов" */
        AuditingAspect.setAuditService(auditService);

        /* Инициализация "раздела безопасности" */
        JwtTokenGenerator jwtTokenGenerator = new JwtTokenGenerator(configProvider.get("jwt.secret"));
        SecurityService securityService = new SecurityService(userRepository, jwtTokenGenerator);

        /* Инициализация "слоя контроллеров" */
        UserController userController = new UserController(userService, securityService);
        ProductController productController = new ProductController(productService, categoryService, brandService);
        AuditController auditController = new AuditController(auditService);
        BrandController brandController = new BrandController(brandService);
        CategoryController categoryController = new CategoryController(categoryService);

        /* "Загружаем" атрибуты в контекст */
        servletContext.setAttribute("jwtTokenGenerator", jwtTokenGenerator);
        servletContext.setAttribute("securityService", securityService);

        servletContext.setAttribute("userService", userService);
        servletContext.setAttribute("productService", productService);
        servletContext.setAttribute("auditService", auditService);
        servletContext.setAttribute("brandService", brandService);
        servletContext.setAttribute("categoryService", categoryService);

        servletContext.setAttribute("userController", userController);
        servletContext.setAttribute("productController", productController);
        servletContext.setAttribute("auditController", auditController);
        servletContext.setAttribute("brandController", brandController);
        servletContext.setAttribute("categoryController", categoryController);
    }

    /**
     * Настраивает и регистрирует ObjectMapper для JSON сериализации/десериализации.
     *
     * @param servletContext сервлет-контекст для регистрации ObjectMapper
     */
    private void loadMappers(ServletContext servletContext) {
        ObjectMapper objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        objectMapper.registerModule(module);
        servletContext.setAttribute("objectMapper", objectMapper);
    }
}