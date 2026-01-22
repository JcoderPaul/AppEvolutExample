package me.oldboy.market.integration.annotation;

import me.oldboy.market.integration.TestAppRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ActiveProfiles("integration-test")
@Transactional
@SpringBootTest(classes = TestAppRunner.class)
public @interface IntegrationTests {
}