package me.oldboy.market.config.test_data_source;

import me.oldboy.market.config.annotation.IT;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@IT
public abstract class TestContainerInit {

    public static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:14");

    @BeforeAll
    static void runContainer() {
        container.start();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("datasource.url", () -> container.getJdbcUrl());
    }
}
