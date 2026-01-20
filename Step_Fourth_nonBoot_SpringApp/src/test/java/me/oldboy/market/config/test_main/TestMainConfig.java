package me.oldboy.market.config.test_main;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
@EnableWebMvc
@ComponentScan({
        "me.oldboy.market.config.test_data_source"
        , "me.oldboy.market.config.security_config"
        , "me.oldboy.market.config.security_details"
        , "me.oldboy.market.config.jwt_config"
        , "me.oldboy.market.controllers"
        , "me.oldboy.market.services"
        , "me.oldboy.market.repository"
        , "me.oldboy.market.exceptions"
})
public class TestMainConfig {
}
