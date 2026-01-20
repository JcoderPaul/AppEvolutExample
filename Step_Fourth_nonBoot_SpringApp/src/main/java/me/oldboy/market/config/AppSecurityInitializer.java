package me.oldboy.market.config;

import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

/**
 * Регистрирует DelegatingFilterProxy для использования springSecurityFilterChain
 * перед любым другим зарегистрированным фильтром.
 */
public class AppSecurityInitializer extends AbstractSecurityWebApplicationInitializer {
}
