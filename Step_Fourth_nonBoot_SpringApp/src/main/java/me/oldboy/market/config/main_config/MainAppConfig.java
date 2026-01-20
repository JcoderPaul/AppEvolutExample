package me.oldboy.market.config.main_config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Основной класс конфигурации, определяющий базовые возможности приложения
 */
@Configuration
@EnableWebMvc
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {
        "me.oldboy.market.config.data_source"
        , "me.oldboy.market.config.security_config"
        , "me.oldboy.market.config.security_details"
        , "me.oldboy.market.config.jwt_config"
        , "me.oldboy.market.config.swagger"
        , "me.oldboy.market.aop"
        , "me.oldboy.market.controllers"
        , "me.oldboy.market.services"
        , "me.oldboy.market.repository"
        , "me.oldboy.market.exceptions"
})
public class MainAppConfig implements WebMvcConfigurer {

}
