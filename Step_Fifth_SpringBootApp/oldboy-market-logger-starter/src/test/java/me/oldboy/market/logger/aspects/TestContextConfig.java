package me.oldboy.market.logger.aspects;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * Изолированный тестовый контекст.
 */
@Configuration
@EnableAspectJAutoProxy
@Import({MethodLogger.class, TestTargetService.class})
public class TestContextConfig {
}
