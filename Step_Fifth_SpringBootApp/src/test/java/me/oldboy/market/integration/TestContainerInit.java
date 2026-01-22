package me.oldboy.market.integration;

import me.oldboy.market.integration.annotation.IntegrationTests;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@IntegrationTests
public class TestContainerInit {
    public static final PostgreSQLContainer<?> container =
            new PostgreSQLContainer<>("postgres:14");

    @BeforeAll
    static void runContainer() {
        container.start();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
    }
}
