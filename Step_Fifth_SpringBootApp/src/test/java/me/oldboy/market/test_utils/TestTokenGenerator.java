package me.oldboy.market.test_utils;

import me.oldboy.market.config.jwt_config.JwtTokenGenerator;
import me.oldboy.market.config.security_details.ClientDetailsService;
import me.oldboy.market.config.security_details.SecurityUserDetails;
import org.springframework.security.core.userdetails.UserDetails;

public class TestTokenGenerator {
    public static String generate(ClientDetailsService clientDetailsService,
                                  JwtTokenGenerator jwtTokenGenerator,
                                  String bearer_prefix){
        UserDetails userDetails = clientDetailsService.loadUserByUsername("admin@admin.ru");
        SecurityUserDetails securityUserDetails = (SecurityUserDetails) userDetails;
        String token = jwtTokenGenerator.getToken(securityUserDetails.getUser().getUserId(), securityUserDetails.getUser().getEmail());
        return bearer_prefix + token;
    }
}
